public class StockTrader {

	public static void main(String[] args) {
		Market market = new Market(5000, 500);

		String traderA = market.registerTrader();
		System.out.println(traderA);
		String traderB = market.registerTrader();
		System.out.println(traderB);

		String stockA = market.registerStock("ABC", 500);
		System.out.println(stockA);
		String stockB = market.registerStock("XYZ", 500);
		System.out.println(stockB);
	}
}
