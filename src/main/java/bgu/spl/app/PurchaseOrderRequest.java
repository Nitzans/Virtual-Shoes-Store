package main.java.bgu.spl.app;

import main.java.bgu.spl.mics.Request;

public class PurchaseOrderRequest implements Request<Receipt> {

	private String shoeType;
	private String customer;
	private int amountSold;
	private int purchaseTick;
	private boolean discounted;
	
	public PurchaseOrderRequest(String shoeType, String customer, int amountSold, int purchaseTick, boolean discounted){
	this.shoeType = shoeType;
	this.customer = customer;
	this.amountSold = amountSold;
	this.purchaseTick = purchaseTick;
	this.discounted = discounted;
	}

	public String getShoeType() {
		return shoeType;
	}

	public void setShoeType(String shoeType) {
		this.shoeType = shoeType;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}
	
	public int getAmountSold() {
		return amountSold;
	}

	public void setAmountSold(int amountSold) {
		this.amountSold = amountSold;
	}

	public int getPurchaseTick() {
		return purchaseTick;
	}

	public void setPurchaseTick(int purchaseTick) {
		this.purchaseTick = purchaseTick;
	}

	public boolean isDiscounted() {
		return discounted;
	}

	public void setDiscounted(boolean discounted) {
		this.discounted = discounted;
	}

}
