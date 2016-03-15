public class Stock {

    public int numAvailable;
    public String id, name;

    public Stock(String name, String id, int numAvailable) {
        this.name = name;
        this.id = id;
        this.numAvailable = numAvailable;
    }

    public int getValue(int tick) {
        return 1;
    }

    public void decrementAvailable(int quantity) {
        if(quantity > numAvailable) {
            throw new IllegalArgumentException("Market should not be asking to decrement more stock than is availble.");
        }
        else {
            numAvailable -= quantity;
        }
    }

    public void incrementAvailable(int quantity) {
        numAvailable += quantity;
    }
}
