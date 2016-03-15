import java.util.HashMap;
import java.util.Set;

public class Trader {

    public String id;
    public int money;

    // stock id -> num owned
    public HashMap<String, Integer> ownedStocks;

    public Trader(String id, int startingMoney) {
        this.id = id;
        this.money = startingMoney;
        this.ownedStocks = new HashMap<String, Integer>();
    }

    // adds the given quantity of the stock to the trader's owned stock
    // uses withdraw money to make sure trader can afford the stock
    public void addStock(String stockId, int quantity, int value) {
        withdrawMoney(quantity * value);
        if(!ownedStocks.containsKey(stockId)) {
            ownedStocks.put(stockId, 0);
        }
        ownedStocks.put(stockId, ownedStocks.get(stockId) + quantity);
    }

    // withdraws money from the trader's funds
    // helper method for add stock
    private void withdrawMoney(int amount) {
        if(amount > money) {
            throw new IllegalArgumentException("Cannot withdraw more money than a trader has.");
        }
        else {
            money -= amount;
        }
    }

    // removes the given quantitiy of the stock from the trader's owned stock
    // makes sure that the trader actually has that much stock to sell
    public void removeStock(String stockId, int quantity, int value) {
        if(!ownedStocks.containsKey(stockId) || ownedStocks.get(stockId) < quantity) {
            throw new IllegalArgumentException("Cannot withdraw more of a stock than a trader has.");
        }
        addMoney(quantity * value);
        ownedStocks.put(stockId, ownedStocks.get(stockId) - quantity);
    }

    // adds money to the trader's funds
    // helper method for remove stock
    public void addMoney(int amount) {
        money += amount;
    }
}
