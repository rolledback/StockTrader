public class ClientStock {

    public int numAvailable;
    public double currentPrice;
    public String id;

    public ClientStock(int numAvailable, double currentPrice, String id) {
        this.numAvailable = numAvailable;
        this.currentPrice = currentPrice;
        this.id = id;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Id: " + id);
        builder.append("\nNum Available: " + Integer.toString(numAvailable));
        builder.append("\nCurrent Price: " + Double.toString(currentPrice));
        return builder.toString();
    }
}