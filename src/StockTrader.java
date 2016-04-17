import java.util.Scanner;

public class StockTrader {

    public static void main(String[] args) {
        Market market = new Market(5000, 1000, 20, 250, 500);
        // addMultiStocks(market, 25);
        market.startServer();
        market.startConsoleConnection();
        Thread marketRunThread = new Thread(market, "Market Runner");
        marketRunThread.start();
    }

    public static void implementationTest(Market market) {
        String traderA = market.registerTrader();
        System.out.println("Trader A: " + traderA);
        String traderB = market.registerTrader();
        System.out.println("Trader B: " + traderB);

        String stockA = market.registerStock(500);
        System.out.println("Stock A: " + stockA);
        String stockB = market.registerStock(500);
        System.out.println("Stock B: " + stockB);

        market.buyStock(stockA, traderA, 400);
        market.buyStock(stockA, traderA, 400);
        market.sellStock(stockA, traderA, 500);
        market.sellStock(stockA, traderA, 100);

        market.buyStock(stockB, traderB, 600);
        market.buyStock(stockA, traderB, 400);
        market.buyStock(stockB, traderB, 200);
    }

    public static void addStocks(Market market) {
        System.out.println("Adding stocks to the market:");
        String stockA = market.registerStock(500);
        System.out.println("Stock A: " + stockA);
        String stockB = market.registerStock(500);
        System.out.println("Stock B: " + stockB);
    }

    public static void addMultiStocks(Market market, int n) {
        System.out.println("Adding stocks to the market:");
        for(int i = 0; i < n; i++) {
            String temp = market.registerStock(500);
            System.out.println("Temp stock: " + temp);
        }
    }
}
