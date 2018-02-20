package main.java.bgu.spl.app;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CountDownLatch;

import com.google.gson.*;

public class ShoeStoreRunner {

	public static void main(String[] args) throws JsonIOException, JsonSyntaxException, FileNotFoundException {
		Store store = Store.getInstance();
		ArrayList<Thread> threads = new ArrayList<Thread>();		
		CountDownLatch cdl;

		System.out.println("Enter the JSON file name (without the '.json' extension):");
		Scanner json = new Scanner (System.in);
		String file = json.nextLine(); // other option: String file = args[0] and then run configure->arguments->${string_prompt}
		Gson gson = new Gson();

		JsonParser parser = new JsonParser();
		JsonElement source = parser.parse(new FileReader("src/"+file+".json")); //the file has to be in the src folder
		JsonObject tree = source.getAsJsonObject();

		JsonElement services = (JsonElement) tree.get("services");
		JsonElement time = ((JsonElement) services).getAsJsonObject().get("time").getAsJsonObject();
		int speed = time.getAsJsonObject().get("speed").getAsInt();
		int duration = time.getAsJsonObject().get("duration").getAsInt();
		TimeService timer = new TimeService(speed, duration);
		Thread timeT = new Thread(new TimeService(speed, duration));
		threads.add(timeT);
				
		int NumOfFactories = services.getAsJsonObject().get("factories").getAsInt();
		int numOfSellers = services.getAsJsonObject().get("sellers").getAsInt();
		
		JsonArray customers = ((JsonElement) services).getAsJsonObject().get("customers").getAsJsonArray();
		int numOfCustomers = 0;
		for(JsonElement cust : customers)
			numOfCustomers++;
		
		int numOfServices = NumOfFactories + numOfSellers + numOfCustomers + 1;//the 1 is the manager		
		 cdl = new CountDownLatch(numOfServices);
		 
		JsonArray storage = tree.get("initialStorage").getAsJsonArray();
		ShoeStorageInfo[] initShoes = new ShoeStorageInfo[storage.size()];
		int i=0;
		for(JsonElement j : storage){
			String type = ((JsonObject) j).get("shoeType").getAsString();
			int amount = ((JsonObject) j).get("amount").getAsInt();
			initShoes[i] = new ShoeStorageInfo(type,amount, 0);
			i++;
		}
		
		JsonElement manager = ((JsonElement) services).getAsJsonObject().get("manager").getAsJsonObject();
		JsonArray discount = manager.getAsJsonObject().get("discountSchedule").getAsJsonArray();
		List<DiscountSchedule> allDiscount = new LinkedList<DiscountSchedule>();
		for(JsonElement j : discount){
			String type = ((JsonObject) j).get("shoeType").getAsString();
			int tick = ((JsonObject) j).get("tick").getAsInt();
			int amount = ((JsonObject) j).get("amount").getAsInt();
			DiscountSchedule disc = new DiscountSchedule(type, amount, tick);
			allDiscount.add(disc);
		}
		Thread managerT = new Thread(new ManagementService(allDiscount, cdl));
		threads.add(managerT);

		for (JsonElement j : customers){
			String name = ((JsonObject) j).get("name").getAsString();
			JsonArray wishListArr = ((JsonObject) j).get("wishList").getAsJsonArray();
			JsonArray purchaseArr = ((JsonObject) j).get("purchaseSchedule").getAsJsonArray();
			Set<String> wishList = new CopyOnWriteArraySet<String>();
			for (JsonElement k : wishListArr){
				String wishShoe = k.getAsString();
				wishList.add(wishShoe);
			}
			List<PurchaseSchedule> purch = new CopyOnWriteArrayList<PurchaseSchedule>();
			for (JsonElement k : purchaseArr){
				String shoeType = ((JsonObject) k).get("shoeType").getAsString();
				int tick = ((JsonObject) k).get("tick").getAsInt();
				purch.add(new PurchaseSchedule(shoeType,tick));
			}
			Thread client = new Thread (new WebsiteClientService(name, purch, wishList, cdl));
			threads.add(client);
			numOfCustomers++;
		}
		
		for(int j=1; j<=NumOfFactories; j++)
			threads.add(new Thread(new ShoeFactoryService("factory "+j, cdl)));
		for(int j=1; j<=numOfSellers; j++)
			threads.add(new Thread(new SellingService("seller "+j, cdl)));
		
		store.load(initShoes);
		 
		for(Thread t : threads)
			t.start();

		for(Thread t : threads)
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		store.print(); //print only after all microservices terminate
	}////end main
}
