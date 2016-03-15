import java.util.HashMap;
import java.util.Set;

public class Trader {

    public String id;
    public int money;

    // stock id -> num owned
    public HashMap<String, Integer> ownedStocks;

    public Trader(String id, Set<String> intialStocks, int startingMoney) {
        this.id = id;
        this.money = startingMoney;
        this.ownedStocks = new HashMap<String, Integer>();

        for(String stockId: intialStocks) {
        	ownedStocks.put(stockId, 0);
        }
    }
}