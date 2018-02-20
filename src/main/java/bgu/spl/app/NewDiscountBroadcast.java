package main.java.bgu.spl.app;

import main.java.bgu.spl.mics.Broadcast;

public class NewDiscountBroadcast implements Broadcast {

	private String shoeType;
	private int amount;
	
	public NewDiscountBroadcast(String shoeType, int amount){
		this.shoeType = shoeType;
		this. amount = amount;
	}

	public String getShoeType() {
		return shoeType;
	}

	public int getAmount() {
		return amount;
	}

}
