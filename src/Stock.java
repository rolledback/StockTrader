import java.io.*;
import java.util.Random;

public class Stock {

    public static void main(String[] args) {
        int count = 10;
        int maxCycles = 500;

        double[][] prices = new double[maxCycles][count];

        for(int i = 0; i < count; i++) {
            Stock.Builder builder = new Stock.Builder(maxCycles);
            builder.atPrice(50)
                   .hasAvailable(100)
                   .volatilityRangeOf(-1, 1)
                   .driftRangeOf(.01, .5);
            Stock test = builder.build();

            System.out.println(test.id);
            addArrayAsColumn(test.getPriceHistory(), prices, i);
        }
        writeMatrixToFile(prices, "stocks");
    }

    public static void addArrayAsColumn(double[] array, double[][] matrix, int col) {
        for(int i = 0; i < array.length; i++) {
            matrix[i][col] = array[i];
        }
    }

    public static void writeMatrixToFile(double[][] matrix, String file) {
        try {
            FileWriter fstream = new FileWriter(file + ".csv");
            BufferedWriter out = new BufferedWriter(fstream);

            for(int row = 0; row < matrix.length; row++) {
                for(int col = 0; col < matrix[row].length; col++) {
                    out.write(Double.toString(matrix[row][col]));
                    if(col != matrix[row].length -1) {
                        out.write(",");
                    }
                }
                out.write("\n");
            }

            out.close();
        }
        catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public int numAvailable;
    public String id;
    public int maxCycles;
    private double startingPrice;

    private double volatility;
    private double drift;
    private double[] priceHistory;

    private static double minVol = -1;
    private static double maxVol = 1;

    private static double minDrift = .01;
    private static double maxDrift = .5;

    private static final double timeStep = 0.01;

    public static final Random rand = new Random();

    // adding while working on builder
    private Stock() {}

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

    public double[] getPriceHistory() {
        return priceHistory;
    }

    public void dumpToPriceHistoryToFile(String file) {
        try {
            FileWriter fstream = new FileWriter(file + id + ".stock");
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
        volatility = minVol + (maxVol - minVol) * rand.nextDouble();
        drift = minDrift + (maxDrift - minDrift) * rand.nextDouble();
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

            if(priceHistory[i] == 0) {
                break;
            }
        }
    }

    public double getValue(int tick) {
        return (double) Math.round(priceHistory[tick] * 100) / 100;
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

    public static class Builder {
        private final Stock stock;

        public Builder(int maxCycles) {
            stock = new Stock();
            stock.id = Util.genRandomId();
            stock.maxCycles = maxCycles;
            stock.priceHistory = new double[maxCycles];
        }

        public Builder atPrice(double price) {
            stock.startingPrice = price;
            return this;
        }

        public Builder hasAvailable(int numAvailable) {
            stock.numAvailable = numAvailable;
            return this;
        }

        public Builder volatilityRangeOf(double minVol, double maxVol) {
            stock.minVol = minVol;
            stock.maxVol = maxVol;
            return this;
        }

        public Builder driftRangeOf(double minDrift, double maxDrift) {
            stock.minDrift = minDrift;
            stock.maxDrift = maxDrift;
            return this;
        }

        public Stock build() {
            if(!validate()) {
                return null;
            }
            else {
                stock.genRandomParams();
                stock.fillPriceHistory();
                return stock;
            }
        }

        private boolean validate() {
            if(stock.maxCycles < 0) {
                return false;
            }
            if(stock.minDrift > stock.maxDrift) {
                return false;
            }
            else if(stock.minVol > stock.maxVol) {
                return false;
            }
            else if(stock.numAvailable < 0) {
                return false;
            }
            else if(stock.startingPrice < 0) {
                return false;
            }
            return true;
        }
    }
}
