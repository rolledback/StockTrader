import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Map;

/**************************************\
* Simulates a stock market. Currently  *
* only supports a single trader.       *
\**************************************/
public class Market implements Runnable {
    
    private final String id = Util.MARKET_ID;
    private final int startingTraderMoney = 500;

    private int cycleNum, maxCycles, cycleLength;
    private ArrayList<ActionRecord> tradingRecord;

    // stock id -> stock
    private HashMap<String, Stock> stocks;

    // trader id -> trader
    private HashMap<String, Trader> traders;

    private MarketServer server;

    public Market(int maxCycles, int cycleLength) {
        this.cycleNum = 0;
        this.maxCycles = maxCycles;
        this.cycleLength = cycleLength;
        this.tradingRecord = new ArrayList<ActionRecord>();
        this.stocks = new HashMap<String, Stock>();
        this.traders = new HashMap<String, Trader>();

        try {
            System.out.println("STARTING MARKET SERVER");
            server = new MarketServer(this);
        }
        catch(Exception e) {
            System.out.println("UNABLE TO START/RUN MARKET SERVER\n");
            System.exit(0);
        }
        finally {
            System.out.println("MARKET SERVER STARTED\n");
            Thread marketServerThread = new Thread(server, "Market Server");
            marketServerThread.start();
        }
    }

    // creates a new trader on the market
    // returns the id of the trader
    public String registerTrader() {
        String newId = Util.genRandomId();
        Trader newTrader = new Trader(newId, startingTraderMoney);
        traders.put(newId, newTrader);
        return newId;
    }

    // creates a new stock with the given name
    // returns the id of the stock
    public String registerStock(String name, int numAvailable) {
        String newId = Util.genRandomId();
        Stock newStock = new Stock(name, newId, numAvailable);
        stocks.put(newId, newStock);
        return newId;
    }

    public void run() {
        while(cycleNum < maxCycles) {
            try {
                System.out.println("Updating in " + cycleLength + " miliseconds.");
                Thread.sleep(cycleLength);
            }
            catch(InterruptedException e) {}
            incrementCycle();
        }
    }

    public synchronized void incrementCycle() {
        System.out.println("Updating.\n");
        cycleNum++;
    }

    // this is called on main thread when buying stock from the market
    // if method returns false, the buying was not succesful
    // if method returns true, the buying was successful
    public synchronized RESULT buyStock(String stockId, String traderId, int quantity) {
        if(!stocks.containsKey(stockId)) {
            return RESULT.INVALID_STOCK;
        }
        if(!traders.containsKey(traderId)) {
            return RESULT.INVALID_TRADER;
        }

        Stock toBuy = stocks.get(stockId);
        Trader buyer = traders.get(traderId);

        if(toBuy.numAvailable < quantity || quantity == 0) {
            return RESULT.INSUFFICIENT_SUPPLY;
        }
        else if(toBuy.getValue(cycleNum) * quantity > buyer.money) {
            return RESULT.INSUFFICIENT_FUNDS;
        }
        else {
            // ok to buy, remove stock from the market, and give to trader
            toBuy.decrementAvailable(quantity);
            buyer.addStock(stockId, quantity, toBuy.getValue(cycleNum));
            tradingRecord.add(new ActionRecord(traderId, stockId, id, quantity));
            return RESULT.SUCCESS;
        }
    }

    // this is called on main thread when selling stock back to the market
    // if method returns true, the selling was successful
    public synchronized RESULT sellStock(String stockId, String traderId, int quantity) {
        if(!stocks.containsKey(stockId)) {
            return RESULT.INVALID_STOCK;
        }
        if(!traders.containsKey(traderId)) {
            return RESULT.INVALID_TRADER;
        }

        Stock toSell = stocks.get(stockId);
        Trader seller = traders.get(traderId);

        if(seller.ownedStocks.get(stockId) < quantity || quantity == 0) {
            return RESULT.INSUFFICIENT_SUPPLY;
        }
        else {
            // ok to sell, remove stock from the trader, and put in the market
            seller.removeStock(stockId, quantity, toSell.getValue(cycleNum));
            toSell.incrementAvailable(quantity);
            tradingRecord.add(new ActionRecord(id, stockId, traderId, quantity));
            return RESULT.SUCCESS;
        }
    }

    public void debugDump() {
        System.out.println("Registered Traders:");
        for(String id: traders.keySet()) {
            System.out.println(id);
        }
        System.out.println();

        printLog();
    }

    public void printLog() {
        for(ActionRecord record : tradingRecord) {
            System.out.println(record);
        }
    }

    public void printLog(String traderId, String stockId) {
        for(ActionRecord record : tradingRecord) {
            if(record.involvesTrader(traderId) || record.involvesStock(stockId)) {
                System.out.println(record);
            }
        }
    }

    // returns map of stock id -> current value, number available
    public synchronized HashMap<String, Integer[]> getStocks() {
        HashMap<String, Integer[]> stockToValue = new HashMap<String, Integer[]>();
        for(Map.Entry<String, Stock> entry : stocks.entrySet()) {
            stockToValue.put(entry.getKey(), new Integer[] {entry.getValue().getValue(cycleNum), entry.getValue().numAvailable});
        }
        return stockToValue;
    }
}
