import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/********************
* Simulates a stock market. Currently
* only supports a single trader.
********************/
public class Market {
    
    private final String id = "0000-0000";
    private int cycleNum, maxCycles, cycleLength;
    private ArrayList<ActionRecord> tradingRecord;

    // stock id -> stock
    private HashMap<String, Stock> stocks;

    // trader id -> trader
    private HashMap<String, Trader> traders;

    public Market(int maxCycles, int cycleLength) {
        this.cycleNum = 0;
        this.maxCycles = maxCycles;
        this.cycleLength = cycleLength;
        this.tradingRecord = new ArrayList<ActionRecord>();
        this.stocks = new HashMap<String, Stock>();
        this.traders = new HashMap<String, Trader>();
    }

    // creates a new trader on the market
    // returns the id of the trader
    public String registerTrader() {
        String newId = Util.genRandomId();
        Trader newTrader = new Trader(newId, stocks.keySet(), 10000);
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

    // this needs to run on its own thread
    public void run() {
        while(true) {
            // every cycleLength miliseconds, stock prices update
            // no buying/selling should be allowed at this time (lock needed?)
            cycleNum++;
        }
    }

    // this is called on main thread when buying stock from the market
    // if method returns false, the buying was not succesful
    // if method returns true, the buying was successful
    public boolean buyStock(String stockId, String traderId) {
        Stock toBuy = stocks.get(id);
        if(toBuy.numAvailable < 1) {
            return false;
        }
        else {
            // increment the number of this stock that the trader owns
            // TODO
            toBuy.numAvailable--;
            tradingRecord.add(new ActionRecord(traderId, stockId, id));
            return true;
        }
    }

    // this is called on main thread when selling stock to the market
    // method is void as selling to the market is always allowed
    public void sellStock(String stockId) {
        Stock toSell = stocks.get(id);
        // decrement the number of this stock that the trader owns
        // TODO
        toSell.numAvailable++;
    }

    // returns map of stock id -> current value
    public HashMap<String, Integer> getStockValues() {
        // TODO
        return null;
    }

}
