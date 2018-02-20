/*package main.java.bgu.spl.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import main.java.bgu.spl.app.Store.BuyResult;

public class StoreTest {

	private static Store store;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		store = Store.getInstance();
	}
	
	@Before
	public void setUp() throws Exception {
		ShoeStorageInfo[] stock = {
				new ShoeStorageInfo("white-sandals", 3, 0),
				new ShoeStorageInfo("red-havanias", 6, 0),
				new ShoeStorageInfo("black-sneakers", 12, 1),
				new ShoeStorageInfo("pink-flip-flops", 2, 0),
				new ShoeStorageInfo("red-boots", 9, 0)};
		store.load(stock);
	}
	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testGetInstance() {
		assertTrue(store.receipts!=null);
	}
	@Test
//	public void teststock {
//		assertEquals(store.stock.size(), 5);
//	}
//	@Test
	public void testLoad() {
		assertTrue(store.stock!=null);
		assertEquals(store.stock.size(), 5);
		assertTrue(store.stock.get("white-sandals").getShoeType().equals("white-sandals"));
		assertTrue(store.stock.get("red-havanias").getShoeType().equals("red-havanias"));
		assertTrue(store.stock.get("black-sneakers").getShoeType().equals("black-sneakers"));
		assertTrue(store.stock.get("pink-flip-flops").getShoeType().equals("pink-flip-flops"));
		assertTrue(store.stock.get("red-boots").getShoeType().equals("red-boots"));
	}
	@Test
	public void testTake() {
		assertEquals(store.take("yellow-peanutbutter-sneakers", false), BuyResult.NOT_IN_STOCK);
		assertEquals(store.take("black-sneakers", true), BuyResult.DISCOUNTED_PRICE);
		assertEquals(store.take("red-boots", false), BuyResult.REGULAR_PRICE);
	}
	@Test
	public void testAdd() {
		assertEquals(store.stock.get("red-boots").getAmountOnStorage(), 9);
		store.add("red-boots", 3);
		assertEquals(store.stock.get("red-boots").getAmountOnStorage(), 12);
	}
	@Test
	public void testAddDiscount() {
		assertEquals(store.stock.get("red-boots").getDiscountedAmount(), 0);
		store.addDiscount("red-boots", 2);
		assertEquals(store.stock.get("red-boots").getDiscountedAmount(), 2);
	}
	@Test
	public void testFile() {
		assertEquals(store.receipts.size(), 0);
		Receipt rec1 = new Receipt("Alice", "Bob", "red-boots", false, 6, 6, 1);
		store.file(rec1);
		assertEquals(store.receipts.size(), 1);
		Receipt rec2 = new Receipt("Charlie", "David", "new-brand-havanias", true, 5, 4, 1);
		store.file(rec2);
		assertEquals(store.receipts.size(), 2);
	}
}
*/