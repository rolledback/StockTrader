public class StockTrader {

    public static void main(String[] args) {
        Market market = new Market(5000, 500);

        String traderA = market.registerTrader();
        System.out.println("Trader A: " + traderA);
        String traderB = market.registerTrader();
        System.out.println("Trader B: " + traderB);

        String stockA = market.registerStock("ABC", 500);
        System.out.println("Stock A: " + stockA);
        String stockB = market.registerStock("XYZ", 500);
        System.out.println("Stock B: " + stockB);

        market.buyStock(stockA, traderA, 400);
        market.buyStock(stockA, traderA, 400);
        market.sellStock(stockA, traderA, 500);
        market.sellStock(stockA, traderA, 100);

        market.buyStock(stockB, traderB, 600);
        market.buyStock(stockA, traderB, 400);
        market.buyStock(stockB, traderB, 200);

        market.printLog();
    }
}
