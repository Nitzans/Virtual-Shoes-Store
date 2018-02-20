package main.java.bgu.spl.app;
import java.util.logging.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

import main.java.bgu.spl.app.Store.BuyResult;
import main.java.bgu.spl.mics.MicroService;

public class ManagementService extends MicroService{

	private final static Logger LOGGER = Logger.getGlobal();
	private Store store = Store.getInstance();
	private List <DiscountSchedule> discList;
	int currentTick;
	private Map<String,Integer>  awaitingOrders; //requests hasn't handled yet
	private Map<String,LinkedBlockingQueue<RestockRequest>> requestedShoes ; //all the requests the factory handle
	private CountDownLatch cdl;

	public ManagementService( List <DiscountSchedule> discountSchedule,CountDownLatch cdl) {
		super("manager");
		this.discList=discountSchedule;
		requestedShoes= new ConcurrentHashMap<String,LinkedBlockingQueue<RestockRequest>>();
		awaitingOrders =new HashMap <String,Integer>();
		this.cdl = cdl;
	}

	protected void initialize() {
		cdl.countDown();
		
		subscribeBroadcast(TerminateBroadcast.class, terminateBroadcast -> {
			LOGGER.info("\t"+getName()+" is terminating");
			terminate();
		});
		
		subscribeBroadcast(TickBroadcast.class, brd->{
            currentTick = brd.getBrodTick();
            for (DiscountSchedule i : discList){
                if (currentTick == i.getTick()){ //it's time for sale!
                    if (store.take(i.getShoeType(),true)!= BuyResult.NOT_IN_STOCK){
                        NewDiscountBroadcast discbrod = new NewDiscountBroadcast(i.getShoeType(), i.getAmount());
                        sendBroadcast(discbrod);
                    }
                }
            }
        });

		subscribeRequest(RestockRequest.class , restock-> {
			String shoe = restock.getShoeType();
			awaitingOrders.putIfAbsent(shoe, 0);
			if(!awaitingOrders.containsKey(shoe) || awaitingOrders.get(shoe)==0){
				ManufacturingOrderRequest manufacuringOrderRequest = new ManufacturingOrderRequest(shoe, (currentTick%5)+1, currentTick);
				boolean succeded = sendRequest(manufacuringOrderRequest, receipt->{
					if(receipt!=null)
					{
						int i=0;
						while(i<receipt.getAmountSold() && (!requestedShoes.get(manufacuringOrderRequest.getShoeType()).isEmpty()))
						{
							complete(requestedShoes.get(manufacuringOrderRequest.getShoeType()).poll(), true);						
							i++;
						}
						if(manufacuringOrderRequest.getAmount()-i!=0)
							store.add(manufacuringOrderRequest.getShoeType(), manufacuringOrderRequest.getAmount()-i);
						store.file(receipt);
						if(requestedShoes.get(manufacuringOrderRequest.getShoeType()).isEmpty())	
							awaitingOrders.put(manufacuringOrderRequest.getShoeType(), 0);
					}
				});

				if (succeded){
					if(!requestedShoes.containsKey(shoe)){
						LinkedBlockingQueue<RestockRequest> newShoe	=new LinkedBlockingQueue<RestockRequest>();
						requestedShoes.put(shoe,newShoe);
					}
					requestedShoes.get(shoe).add(restock);
					awaitingOrders.put(shoe, (currentTick%5)); //no +1 because first index is 0
				}
				else{
					LOGGER.info("\tSorry, manufacture can't supply "+shoe);
					complete(restock,false);
				}
			}
			
			else {
				int amount= awaitingOrders.get(shoe).intValue();
				awaitingOrders.put(shoe, amount-1);
				requestedShoes.get(shoe).add(restock);
			}
		});
	}
}
