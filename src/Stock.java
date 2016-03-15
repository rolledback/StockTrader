public class Stock {

    public int numAvailable;
    public String id, name;

    public Stock(String name, String id, int numAvailable) {
        this.name = name;
        this.id = id;
        this.numAvailable = numAvailable;
    }

    public int getValue(int tick) {
        return 0;
    }

}

// this may not be needed, still thinking
enum STOCK_TYPE {
    STABLE,
    VOLITALE
};
