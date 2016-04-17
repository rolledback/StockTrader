import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Random;
import java.util.Map;
import java.util.Scanner;
import java.lang.StringBuilder;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Market implements Runnable {
    
    private final String id = Util.MARKET_ID;
    private String tag = "MARKET";
    private final int startingTraderMoney = 10000;

    private static final Random rand = new Random();

    private int cycleNum, maxCycles, cycleLength;
    private List<ActionRecord> tradingRecord;

    // stock id -> stock
    private Map<String, Stock> stocks;

    // trader id -> trader
    private Map<String, Trader> traders;

    private MarketServer server;
    private ConsoleSocket console;

    public Market(int maxCycles, int cycleLength) {
        this.cycleNum = 0;
        this.maxCycles = maxCycles;
        this.cycleLength = cycleLength;
        this.tradingRecord = new ArrayList<ActionRecord>();
        this.stocks = new HashMap<String, Stock>();
        this.traders = new HashMap<String, Trader>();
    }

    public Market(int maxCycles, int cycleLength, int numStocks, int minNumAvail, int maxNumAvail) {
        this.cycleNum = 0;
        this.maxCycles = maxCycles;
        this.cycleLength = cycleLength;
        this.tradingRecord = new ArrayList<ActionRecord>();
        this.stocks = new HashMap<String, Stock>();
        this.traders = new HashMap<String, Trader>();

        for(int i = 0; i < numStocks; i++) {
            int numAvail = rand.nextInt((maxNumAvail - minNumAvail) + 1) + minNumAvail;
            registerStock(numAvail);
        }
    }

    // starts the Market's server
    public void startServer() {
        try {
            Util.print(tag, "Starting market server.");
            server = new MarketServer(this);
        }
        catch(Exception e) {
            Util.print(tag, "Unable to start/run server.");
            System.exit(0);
        }
        finally {
            Thread marketServerThread = new Thread(server, "Market Server");
            marketServerThread.start();
            Util.print(tag, "Server started.");
        }
    }

    // starts the socket that a console client can connect to
    public void startConsoleConnection() {
        try {
            Util.print(tag, "Starting console socket.");
            console = new ConsoleSocket(this);
        }
        catch(Exception e) {
            Util.print(tag, "Unable to start/run console socket.");
            System.exit(0);
        }
        finally {
            Thread consoleSocketThread = new Thread(console, "Console Socket");
            consoleSocketThread.start();
            Util.print(tag, "Console socket started.");
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
    public String registerStock(int numAvailable) {
        String newId = Util.genRandomId();
        Stock newStock = new Stock(newId, numAvailable, 50, maxCycles);
        stocks.put(newId, newStock);
        return newId;
    }

    public void run() {
        while(cycleNum < maxCycles) {
            try {
                Util.print(tag, "Updating in " + cycleLength + " miliseconds.");
                Thread.sleep(cycleLength);
            }
            catch(InterruptedException e) {}
            incrementCycle();
        }
    }

    public synchronized void incrementCycle() {
        Util.print(tag, "Updating.");
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

    public String debugDump() {
        StringBuilder tmp = new StringBuilder();
        tmp.append("Current cycle: " + cycleNum + "\n");

        tmp.append("\nRegistered Traders:" + "\n");
        for(String id : traders.keySet()) {
            tmp.append(id + "\n");
        }

        tmp.append("\nCurrent Stocks:" + "\n");
        for(String id : stocks.keySet()) {
            tmp.append(stocks.get(id).toString() + "@" + stocks.get(id).getValue(cycleNum) + "\n");
        }
        return tmp.toString();
    }

    public String tradingLog() {
        StringBuilder tmp = new StringBuilder();
        for(ActionRecord record : tradingRecord) {
            tmp.append(record.toString() + "\n");
        }
        return tmp.toString();
    }

    public String tradingLog(String traderId, String stockId) {
        StringBuilder tmp = new StringBuilder();
        for(ActionRecord record : tradingRecord) {
            if((traderId == "" || record.involvesTrader(traderId)) && (stockId == "" || record.involvesStock(stockId))) {
                tmp.append(record.toString() + "\n");
            }
        }
        return tmp.toString();
    }

    // returns map of stock id -> current value, number available
    public synchronized Map<String, Object[]> getStocks() {
        Map<String, Object[]> stockToValue = new HashMap<String, Object[]>();
        for(Map.Entry<String, Stock> entry : stocks.entrySet()) {
            stockToValue.put(entry.getKey(), new Object[] {entry.getValue().getValue(cycleNum), entry.getValue().numAvailable});
        }
        return stockToValue;
    }
}
