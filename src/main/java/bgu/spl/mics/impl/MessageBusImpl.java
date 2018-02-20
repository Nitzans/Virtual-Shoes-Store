package main.java.bgu.spl.mics.impl;

import main.java.bgu.spl.mics.Broadcast;
import main.java.bgu.spl.mics.Message;
import main.java.bgu.spl.mics.MessageBus;
import main.java.bgu.spl.mics.MicroService;
import main.java.bgu.spl.mics.Request;
import main.java.bgu.spl.mics.RequestCompleted;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageBusImpl implements MessageBus {

	private ConcurrentHashMap<Class<? extends Request>,LastIndexArray> fRequest;
	private ConcurrentHashMap<Class<? extends Broadcast>,CopyOnWriteArrayList<MicroService>> fBroadcast;
	private ConcurrentHashMap<MicroService,LinkedBlockingQueue<Message>> fMSQueue;
	private ConcurrentHashMap<Request,MicroService> fRequestedByService;

	private static class SingletonHolder {
		private static MessageBusImpl instance = new MessageBusImpl();
	}
	private  MessageBusImpl(){
		fMSQueue = new ConcurrentHashMap<MicroService,LinkedBlockingQueue<Message>>();
		fRequest = new ConcurrentHashMap<Class<? extends Request>,LastIndexArray>();
		fBroadcast = new ConcurrentHashMap<Class<? extends Broadcast>,CopyOnWriteArrayList<MicroService>>();
		fRequestedByService = new ConcurrentHashMap<Request,MicroService>();
	}
	public static MessageBusImpl getInstance() {
		return SingletonHolder.instance;
	}

	public synchronized void subscribeRequest(Class<? extends Request> type, MicroService m) {
		if (fRequest.containsKey(type)){
			fRequest.get(type).array.add(m);

		}else {
			fRequest.put(type, new LastIndexArray());
			fRequest.get(type).array.add(m);
		}
	}

	@Override
	public synchronized void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		if (fBroadcast.containsKey(type))
			fBroadcast.get(type).add(m);	
		else{
			fBroadcast.put(type,new CopyOnWriteArrayList<MicroService>());
			fBroadcast.get(type).add(m);
		}
	}

	@Override
	public <T> void complete(Request<T>	r, T result) {
		RequestCompleted<T> req = new RequestCompleted<T>(r, result);
		try {
			MicroService m = fRequestedByService.get(r);
			fMSQueue.get(m).put(req);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		CopyOnWriteArrayList<MicroService> brdList = fBroadcast.get(b.getClass());
		for (MicroService i : brdList){ //Iterator
			fMSQueue.get(i).add(b);
		}
	}

	@Override
	public boolean sendRequest(Request<?> r, MicroService requester) {
		//		synchronized (this){
		fRequestedByService.put(r, requester);
		if (fRequest.containsKey(r.getClass())){
			LastIndexArray reqList = fRequest.get(r.getClass());
			int i = reqList.getIndex();
			try {
				fMSQueue.get(reqList.array.get(i)).put(r);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			reqList.updateI();
			return true;
		}
		return false;
		//		}
	}

	public synchronized void register(MicroService m) {
		if (!fMSQueue.containsKey(m))
			fMSQueue.put(m, new LinkedBlockingQueue<Message>());
	}

	public synchronized void unregister(MicroService m) {
		while (!fMSQueue.get(m).isEmpty()) //delete queue
			fMSQueue.get(m).poll(); 
		fMSQueue.remove(m);		//delete MicroService
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		if (fMSQueue.containsKey(m))
			return fMSQueue.get(m).take();
		else
			throw new IllegalStateException();
	}

	public ConcurrentHashMap<Class<? extends Request>,LastIndexArray> getRequestMap(){
		return fRequest;
	}

	public ConcurrentHashMap<Class <? extends Broadcast>,CopyOnWriteArrayList<MicroService>> getBroadcastMap(){
		return fBroadcast;
	}

	public boolean hasMessages(MicroService m){
		return !fMSQueue.get(m).isEmpty();
	}

}
