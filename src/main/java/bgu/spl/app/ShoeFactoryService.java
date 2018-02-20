package main.java.bgu.spl.app;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;
import main.java.bgu.spl.mics.MicroService;
import java.util.LinkedList;
import java.util.Queue;


public class ShoeFactoryService extends MicroService {

	static Logger LOGGER = Logger.getGlobal();
	private int currentTick;
	private Queue<ManufacturingOrderRequest> ordersQueue;
	private ManufacturingOrderRequest order;
	private int remainingShoes;	
	private CountDownLatch cdl;

	public ShoeFactoryService(String name,CountDownLatch cdl) {
		super(name);
		ordersQueue = new LinkedList<ManufacturingOrderRequest>();
		this.cdl=cdl;
	}

	public void initialize() {
		remainingShoes = -1;
		subscribeBroadcast(TerminateBroadcast.class, end -> 
		{
			LOGGER.info("\t"+getName() + " is terminating");
			terminate();
		});

		subscribeBroadcast(TickBroadcast.class, tickBroadcast -> 
		{ currentTick = tickBroadcast.getBrodTick();

		if (order == null){ //if hasn't order to handle, fetch one from the queue
			if (!ordersQueue.isEmpty()){
				order = ordersQueue.remove();
				remainingShoes = order.getAmount();
			}
		}

		// if we still have shoes we need to create
		if (remainingShoes > 0){
			remainingShoes--;
			LOGGER.info("\t"+getName() + " created " + order.getShoeType());
		}
		// no more shoes need to be manufactured
		else if (remainingShoes == 0){
			LOGGER.info("\t"+getName() + ": We have completed the request for making " + order.getAmount() + " " + order.getShoeType() + ". Here's your receipt");
			Receipt receipt = new Receipt(getName(), "store", order.getShoeType(), false, currentTick, order.getOrderTick(), order.getAmount());
			complete(order, receipt);

			//if there are more order Request
			if (!ordersQueue.isEmpty()){
				order = ordersQueue.remove();
				remainingShoes = order.getAmount();
				LOGGER.info("\t"+getName() + " order request for " + remainingShoes + " " + order.getShoeType() + " started");

				if (remainingShoes > 0){
					remainingShoes--;
					LOGGER.info("\t"+getName() + " created " + order.getShoeType());
				}
			}
			else{ //finished working
				order = null;
				remainingShoes = -1;
			}
		}
		});

		subscribeRequest(ManufacturingOrderRequest.class, manufacturingOrderRequest -> {
			ordersQueue.add(manufacturingOrderRequest);
			LOGGER.info("\t"+getName() + " received order of " + manufacturingOrderRequest.getAmount() + " " + manufacturingOrderRequest.getShoeType());
		});
		cdl.countDown();
	}
}
