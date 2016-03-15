import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**************************************\
* Simulates a stock market. Currently  *
* only supports a single trader.       *
\**************************************/
public class Market {
    
    private final String id = Util.MARKET_ID;
    private final int startingTraderMoney = 500;

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

    // this needs to run on its own thread
    public void run() {
        while(cycleNum < maxCycles) {
            // every cycleLength miliseconds, stock prices update
            // no buying/selling should be allowed at this time (lock needed?)
            cycleNum++;
        }
    }

    // this is called on main thread when buying stock from the market
    // if method returns false, the buying was not succesful
    // if method returns true, the buying was successful
    public boolean buyStock(String stockId, String traderId, int quantity) {
        Stock toBuy = stocks.get(stockId);
        Trader buyer = traders.get(traderId);

        if(toBuy.numAvailable < quantity || quantity == 0) {
            // not enough stocks left to buy or trying to buy 0
            return false;
        }
        else if(toBuy.getValue(cycleNum) * quantity > buyer.money) {
            // not enough money to buy
            return false;
        }
        else {
            // ok to buy, remove stock from the market, and give to trader
            toBuy.decrementAvailable(quantity);
            buyer.addStock(stockId, quantity, toBuy.getValue(cycleNum));
            tradingRecord.add(new ActionRecord(traderId, stockId, id, quantity));
            return true;
        }
    }

    // this is called on main thread when selling stock back to the market
    // if method returns true, the selling was successful
    public boolean sellStock(String stockId, String traderId, int quantity) {
        Stock toSell = stocks.get(stockId);
        Trader seller = traders.get(traderId);

        if(seller.ownedStocks.get(stockId) < quantity || quantity == 0) {
            // not enough stocks to sell or trying to sell 0
            return false;
        }
        else {
            // ok to sell, remove stock from the trader, and put in the market
            seller.removeStock(stockId, quantity, toSell.getValue(cycleNum));
            toSell.incrementAvailable(quantity);
            tradingRecord.add(new ActionRecord(id, stockId, traderId, quantity));
            return true;
        }
    }

    public void printLog() {
        for(ActionRecord record: tradingRecord) {
            System.out.println(record);
        }
    }

    // returns map of stock id -> current value
    public HashMap<String, Integer> getStockValues() {
        // TODO
        return null;
    }

}
