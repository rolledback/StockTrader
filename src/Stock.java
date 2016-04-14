import java.io.*;
import java.util.Random;

public class Stock {

    public static void main(String[] args) {
        Stock test = new Stock(Util.genRandomId(), 100, 50, 500);
        test.dumpToPriceHistoryToFile("price");
    }

    public int numAvailable;
    public String id;
    public int maxCycles;

    private double startingPrice;
    private double volatility;
    private double drift;
    private double[] priceHistory;

    private static final double timeStep = 0.01;
    public static final Random rand = new Random();

    public Stock(String id, int numAvailable) {
        this.id = id;
        this.numAvailable = numAvailable;
    }

    public Stock(String id, int numAvailable, int startingPrice, int maxCycles) {
        this.id = id;
        this.numAvailable = numAvailable;
        this.maxCycles = maxCycles;
        this.startingPrice = startingPrice;

        this.priceHistory = new double[maxCycles];

        genRandomParams();
        fillPriceHistory();
    }

    public void dumpToPriceHistoryToFile(String file) {
        try {
            FileWriter fstream = new FileWriter(file + ".stock");
            BufferedWriter out = new BufferedWriter(fstream);

            for(int i = 0; i < maxCycles; i++) {
                out.write(Double.toString(priceHistory[i]) + "\n");
            }

            out.close();
        }
        catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void genRandomParams() {
        // TODO: these need to be randomly generated
        volatility = 1;
        drift = 0.1;
    }

    private void fillPriceHistory() {
        // see here for math explaination: http://investexcel.net/geometric-brownian-motion-excel/
        double delta, totalDrift, uncertainty;
        priceHistory[0] = startingPrice;
        for(int i = 1; i < maxCycles; i++) {
            totalDrift = (priceHistory[i - 1] * drift * timeStep);
            uncertainty=  (priceHistory[i - 1] * volatility * rand.nextGaussian() * Math.sqrt(timeStep));
            delta = totalDrift + uncertainty;

            priceHistory[i] = priceHistory[i - 1] + delta;
        }
    }

    public int getValue(int tick) {
        // TODO: this needs to return double, lots of refactoring tbd to make that work
        return (int)priceHistory[tick];
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
