public class Stock {

    public int numAvailable;
    public String id;

    public Stock(String id, int numAvailable) {
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

    public String toString() {
        String ret = "";
        ret += "Id: "+ id + " | ";
        ret += "Num Available: " + numAvailable;
        return ret;
    }
}
