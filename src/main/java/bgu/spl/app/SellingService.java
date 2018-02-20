package main.java.bgu.spl.app;

import main.java.bgu.spl.mics.MicroService;
import main.java.bgu.spl.app.Store;
import main.java.bgu.spl.app.Store.BuyResult;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import main.java.bgu.spl.app.PurchaseOrderRequest;

public class SellingService extends MicroService {
	private final static Logger LOGGER = Logger.getGlobal();
	Store store = Store.getInstance();	
	int currentTick;
	CountDownLatch cdl;
	Object sync;
	
	public SellingService(String name, CountDownLatch cdl) {
		super(name);
		this.cdl = cdl;
	}

	protected void initialize() {

		subscribeBroadcast(TerminateBroadcast.class, end ->{
			LOGGER.info("\t"+getName()+" is terminating");
			terminate();
		});
		
		subscribeBroadcast(TickBroadcast.class, brod-> 
		{currentTick = brod.getBrodTick(); });

		subscribeRequest(PurchaseOrderRequest.class, req->{
			BuyResult result = Store.getInstance().take(req.getShoeType(), req.isDiscounted());
			switch(result){

			case NOT_IN_STOCK: {
				RestockRequest restock = new RestockRequest(req.getShoeType(), req.getPurchaseTick()%5+1, currentTick);
				Receipt receipt = new Receipt(getName(), req.getCustomer(), req.getShoeType(), false, currentTick, req.getPurchaseTick(), 1);
				sendRequest(restock, order->{
					if (!order)
						complete(req, null);
					else{
						complete(req, receipt);
						store.file(receipt);
					}
				});
			}
			break;

			case NOT_ON_DISCOUNT:
				complete(req, null); //we don't have the shoe on sale	
				break;

			case REGULAR_PRICE: {
				Receipt receipt = new Receipt(getName(), req.getCustomer(), req.getShoeType(), false, currentTick, req.getPurchaseTick(), 1);
				complete(req, receipt);
				store.file(receipt);
			}
			break;

			case DISCOUNTED_PRICE: {
				Receipt receipt = new Receipt(getName(), req.getCustomer(), req.getShoeType(), true, currentTick, req.getPurchaseTick(), 1);
				complete(req, receipt);
				store.file(receipt);
			}
			break;

			} // end of switch
		});//end of req lambda
		cdl.countDown();
	}

}
