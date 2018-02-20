package main.java.bgu.spl.app;

public class DiscountSchedule {

	private String shoeType;
	private int amount;
	private int tick;

	public DiscountSchedule(String shoeType, int amount, int tick){
		this.shoeType = shoeType;
		this.amount = amount;
		this.tick = tick;
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
	public int getTick() {
		return tick;
	}
	public void setTick(int tick) {
		this.tick = tick;
	}
}
