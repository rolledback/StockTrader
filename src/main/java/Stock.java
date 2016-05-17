import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.lang.StringBuilder;
import java.io.*;

import com.google.gson.Gson;

public class Stock {

    public static void main(String[] args) {
        int count = 10;
        int maxCycles = 500;

        double[][] prices = new double[maxCycles][count];
        List<Stock> stocks;

        Stock.Builder builder = new Stock.Builder(maxCycles);
        builder.priceRangeOf(25, 100)
               .hasAvailable(100)
               .volatilityRangeOf(-1, 1)
               .driftRangeOf(.01, .5);

        stocks = builder.buildMulti(count);

        for(int i = 0; i < stocks.size(); i++) {
            Stock curr = stocks.get(i);
            System.out.println(curr.id);
            Util.addArrayAsColumn(curr.getPriceHistory(), prices, i);
        }
        Util.writeMatrixToFile(prices, "stocks");
    }

    private static final double timeStep = 0.01;
    private static final Random rand = new Random();

    public int numAvailable;
    public String id;
    public int maxCycles;
    private double[] priceHistory;
    private double startingPrice;
    private double volatility;
    private double drift;

    private Stock() {}

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
            throw new IllegalArgumentException("Cannot decrement more stock than is availble.");
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

        private double minDrift = .01, maxDrift = .5;
        private double minVol = -1, maxVol = 1;
        private double minPrice, maxPrice;

        public Builder(int maxCycles) {
            stock = new Stock();
            stock.id = Util.genRandomId();
            stock.maxCycles = maxCycles;
            stock.priceHistory = new double[maxCycles];
        }

        public Builder hasAvailable(int numAvailable) {
            stock.numAvailable = numAvailable;
            return this;
        }

        public Builder atPrice(double price) {
            this.minPrice = price;
            this.maxPrice = price;
            return this;
        }

        public Builder priceRangeOf(double minPrice, double maxPrice) {
            this.minPrice = minPrice;
            this.maxPrice = maxPrice;
            return this;
        }

        public Builder withVolatility(double volatility) {
            this.minVol = volatility;
            this.maxVol = volatility;
            return this;
        }

        public Builder volatilityRangeOf(double minVol, double maxVol) {
            this.minVol = minVol;
            this.maxVol = maxVol;
            return this;
        }

        public Builder withDrift(double drift) {
            this.minDrift = drift;
            this.maxDrift = drift;
            return this;
        }

        public Builder driftRangeOf(double minDrift, double maxDrift) {
            this.minDrift = minDrift;
            this.maxDrift = maxDrift;
            return this;
        }
        
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append("ID: " + stock.id);
            stringBuilder.append("\nMax Cycles: " + stock.maxCycles);
            stringBuilder.append("\nPrice Range: " + this.minPrice + " " + this.maxPrice);
            stringBuilder.append("\nVolatility Range: " + this.minVol + " " + this.maxVol);
            stringBuilder.append("\nDrift Range: " + this.minDrift + " " + this.maxDrift);

            return stringBuilder.toString();
        }

        public Stock build() {
            if(!validate()) {
                return null;
            }
            else {
                genRandomParams();
                stock.fillPriceHistory();
                return stock;
            }
        }

        public List<Stock> buildMulti(int num) {
            List<Stock> stocks = new ArrayList<Stock>();

            if(!validate()) {
                return null;
            }
            else {
                for(int i = 0; i < num; i++) {
                    // copy params of the current builder over to a new one
                    Builder temp = new Builder(stock.maxCycles);
                    temp.hasAvailable(stock.numAvailable);
                    temp.priceRangeOf(this.minPrice, this.maxPrice);
                    temp.volatilityRangeOf(this.minVol, this.maxVol);
                    temp.driftRangeOf(this.minDrift, this.maxDrift);

                    temp.genRandomParams();
                    temp.stock.fillPriceHistory();

                    stocks.add(temp.stock);

                    Gson gson = new Gson();
                    gson.toJson(temp.stock, System.out);
                }
                return stocks;
            }

        }

        private void genRandomParams() {
            // drift
            if(this.minDrift == this.maxDrift) {
                stock.drift = maxDrift;
            }
            else {
                stock.drift = Util.randomDouble(minDrift, maxDrift);
            }

            // volatility
            if(this.minVol == this.maxVol) {
                stock.volatility = maxVol;
            }
            else {
                stock.volatility = Util.randomDouble(minVol, maxVol);
            }

            // starting price
            if(this.minPrice == this.maxPrice) {
                stock.startingPrice = maxPrice;
            }
            else {
                stock.startingPrice = Util.randomDouble(minPrice, maxPrice);
            }
        }

        private boolean validate() {
            if(stock.maxCycles < 0) {
                return false;
            }
            if(minDrift > maxDrift) {
                return false;
            }            
            if(minVol > maxVol) {
                return false;
            }
            if(minPrice > maxPrice) {
                return false;
            }
            if(stock.numAvailable < 0) {
                return false;
            }
            return true;
        }
    }
}
