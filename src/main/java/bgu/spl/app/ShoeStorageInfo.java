package main.java.bgu.spl.app;

public class ShoeStorageInfo {
	private String ShoeType;
	private int AmountOnStorage;
	private int DiscountedAmount;
	
	public ShoeStorageInfo(String ShoeType, int AmountOnStorage, int DiscountedAmount){
		this.ShoeType = ShoeType;
		this.AmountOnStorage = AmountOnStorage;
		this.DiscountedAmount = DiscountedAmount;
	}

	public String getShoeType() {
		return ShoeType;
	}
	public void setShoeType(String ShoeType) {
		this.ShoeType = ShoeType;
	}
	
	public int getAmountOnStorage() {
		return AmountOnStorage;
	}
	public void setAmountOnStorage(int AmountOnStorage) {
		this.AmountOnStorage = AmountOnStorage;
	}
	
	public int getDiscountedAmount() {
		return DiscountedAmount;
	}
	public void setDiscountedAmount(int DiscountedAmount) {
		this.DiscountedAmount = DiscountedAmount;
	}
	
}
