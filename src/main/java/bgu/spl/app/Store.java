
package main.java.bgu.spl.app;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

import main.java.bgu.spl.app.Store.BuyResult;
import main.java.bgu.spl.app.Receipt;
import main.java.bgu.spl.app.ShoeStorageInfo;
import main.java.bgu.spl.app.Store.BuyResult;


public class Store{

	private ConcurrentHashMap<String,ShoeStorageInfo> stock;
	private LinkedList<Receipt> receipts;

	private static class SingletonHolder {
		private static Store instance = new Store();
	}
	private Store(){
		stock = new ConcurrentHashMap<String,ShoeStorageInfo>();
		receipts = new LinkedList<Receipt>();
	}
	public static Store getInstance() {
		return SingletonHolder.instance;
	}

	public enum BuyResult{
		NOT_IN_STOCK,
		NOT_ON_DISCOUNT,
		REGULAR_PRICE,
		DISCOUNTED_PRICE;
	}

	public void load(ShoeStorageInfo[] storage){
		for (ShoeStorageInfo s : storage)
			stock.put(s.getShoeType(), s);
	}

	public BuyResult take(String shoeType , boolean onlyDiscount){
		ShoeStorageInfo shoe = stock.get(shoeType);
		
		if (!stock.containsKey(shoeType))
			return BuyResult.NOT_IN_STOCK;
		
		int amount = shoe.getAmountOnStorage();
		int discountedAmount = shoe.getDiscountedAmount();

		if (onlyDiscount && discountedAmount<=0)
			return BuyResult.NOT_ON_DISCOUNT;

		else if (amount>0 && discountedAmount>0){
			shoe.setAmountOnStorage(amount-1);
			shoe.setDiscountedAmount(discountedAmount-1);
			return BuyResult.DISCOUNTED_PRICE;
		}

		else if (amount>0 && discountedAmount<=0){
			shoe.setAmountOnStorage(amount-1);
			return BuyResult.REGULAR_PRICE;
		}
		else
			return BuyResult.NOT_IN_STOCK;
	}

	public void add (String shoeType, int amount){
		if (!stock.containsKey(shoeType))
			stock.put(shoeType, new ShoeStorageInfo(shoeType,amount,0)); //if the shoe doesn't exist create it
		ShoeStorageInfo shoe = stock.get(shoeType);
		shoe.setAmountOnStorage(shoe.getAmountOnStorage()+amount);
	}

	public void addDiscount (String shoeType, int amount ){
		if (!stock.containsKey(shoeType))
			stock.put(shoeType, new ShoeStorageInfo(shoeType,amount,amount)); //if you create new shoe they all would have discount
		ShoeStorageInfo shoe = stock.get(shoeType);
		shoe.setAmountOnStorage(shoe.getAmountOnStorage()+amount);
		shoe.setDiscountedAmount(shoe.getDiscountedAmount()+amount);
	}

	public void file (Receipt receipt){
		receipts.add(receipt);
	}

	public void print (){
		int stockNum=1;
		for (ShoeStorageInfo item : stock.values()) { //iterate on shoes
			System.out.println("Item No."+stockNum+" info:");
			System.out.println("\tShoe Name: "+item.getShoeType());
			System.out.println("\tAmount: "+item.getAmountOnStorage());
			System.out.println("\tDiscounted Amount: "+item.getDiscountedAmount());
			System.out.println("");
			stockNum++;
		}

		int receiptNum=1;
		for (Receipt rec : receipts) { //iterate on receipts
			System.out.printf("receipt No.%d details:\n",receiptNum);
			System.out.println("\tSeller : "+rec.getSeller());
			System.out.println("\tCustomer : "+rec.getCustomer());
			System.out.println("\tShoeType : "+rec.getShoeType());
			System.out.println("\tDiscount : "+rec.getDiscount());
			System.out.println("\tIssuedTick : "+rec.getIssuedTick());
			System.out.println("\tRequestTick : "+rec.getRequestTick());
			System.out.println("\tAmountSold : "+rec.getAmountSold());
			System.out.println("");
			receiptNum++;
		}
	}
	
}
