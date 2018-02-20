package main.java.bgu.spl.app;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import main.java.bgu.spl.mics.MicroService;

public class WebsiteClientService extends MicroService {
	private final static Logger LOGGER = Logger.getGlobal();

	private List<PurchaseSchedule> purch;
	private Set<String> wishList;
	private int tick;
	private CountDownLatch cdl;

	public WebsiteClientService(String name, List<PurchaseSchedule> lstPurchase, Set<String> wish, CountDownLatch cdl) {
		super(name);
		purch = lstPurchase;
		wishList = wish;
		this.cdl = cdl;
	}

	protected void initialize() {
		
		LOGGER.info("\tClient " + getName() + " started");
		
		subscribeBroadcast(TerminateBroadcast.class, end ->{
			LOGGER.info("\t"+getName()+" is terminating");
			terminate();
		});

		subscribeBroadcast(TickBroadcast.class, brd->{
			tick = brd.getBrodTick();
			for (PurchaseSchedule i : purch){
				String shoeT = i.getShoeType();
				if (tick == i.getTick()){
					LOGGER.info("\t"+getName()+" is purchasing "+shoeT);
					sendRequest(new PurchaseOrderRequest(i.getShoeType(), getName(), 1, i.getTick(), false), receipt ->{
						if(receipt!=null)
							LOGGER.info("\t"+this.getName()+" got a recipt ");
						else 
							LOGGER.info("\t"+this.getName()+" failed buying "+shoeT);
						purch.remove(i);
						if (wishList.contains(shoeT)){
							wishList.remove(shoeT); // if we bought this item in regular purchase it remove from the wish list (according to the forum)
						}
					});
				}
			}
		});
		
		subscribeBroadcast(NewDiscountBroadcast.class, dsc->{
			String shoeT = dsc.getShoeType();
			int amount = dsc.getAmount();
			if  (wishList.contains(shoeT)){
				if (sendRequest(new PurchaseOrderRequest(shoeT, getName(), amount, tick, true), receipt ->{
					LOGGER.info("\t"+getName()+" requested " +shoeT);
					if(receipt!=null)
						LOGGER.info("\t"+this.getName()+" got a recipt ");
					else 
						LOGGER.info("\t"+this.getName()+" failed buying "+shoeT);
				})) wishList.remove(shoeT); //your wish came true
				
			}
		}); //end of purchase from wish list

		cdl.countDown();
	}
}
