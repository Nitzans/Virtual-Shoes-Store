package main.java.bgu.spl.app;

import main.java.bgu.spl.mics.Request;

public class ManufacturingOrderRequest implements Request<Receipt>{
	private String shoeType;
	private int amount;
	private int orderTick;
	
	public ManufacturingOrderRequest (String shoeType, int amount, int orderTick){
		this.shoeType = shoeType;
		this.amount = amount;
		this.orderTick = orderTick;
	}
	
	public String getShoeType() {
		return shoeType;
	}
	public void setShoeType(String shoeType) {
		this.shoeType = shoeType;
	}
	
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	
	public int getOrderTick() {
		return orderTick;
	}
	public void setOrderTick(int orderTick) {
		this.orderTick = orderTick;
	}
	
}
