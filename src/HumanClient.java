import java.net.*;
import java.io.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class HumanClient {

    public static void main(String[] args) {
        try {
            HumanClient one = new HumanClient();
            String myId = one.registerWithMarket();
            if(myId != null && myId != "") {
                System.out.println("Registration succesful. My trader ID is " + myId + ".");
                one.updateStockRecords(one.getStocks());
                one.printRecords();
                for(int i = 0; i < 50; i++) {
                    one.buyRandomStock();
                }
                one.closeConnection();
            }
        }
        catch(Exception e) { System.out.println(e.toString()); }
    }

    private Socket market;
    private DataOutputStream outStream;
    private DataInputStream inStream;
    private String id;

    private Map<String, Integer[]> marketState;

    private final String marketAddress = "127.0.0.1";
    private final int marketPort = 5656;

    private Pattern stocksSummaryPattern = Pattern.compile("\\[([\\w-]+)\\,\\[(\\d+)\\,(\\d+)\\]\\]");
    private Matcher match;

    public HumanClient() throws IOException {
        marketState = new HashMap<String, Integer[]>();

        System.out.println("Connecting to market at " + marketAddress + " on port " + marketPort);

        connectTo(marketAddress, marketPort);

        int portToConnectTo = Integer.parseInt(inStream.readUTF());
        System.out.println("Market says to connect to market connection on port " + portToConnectTo);

        connectTo(marketAddress, portToConnectTo);
    }

    private void connectTo(String address, int port) throws IOException {
        if(market != null) {
            market.close();
        }

        market = new Socket(address, port);
        System.out.println("Succesfully connected to market at " + market.getRemoteSocketAddress());

        outStream = new DataOutputStream(market.getOutputStream());
        inStream = new DataInputStream(market.getInputStream());
    }

    // object should not send messages directly
    // this should only help methods with specific actions send a message
    private String sendMessage(String message, boolean expectReply) throws IOException {
        outStream.writeUTF(message);
        if(expectReply) {
            return inStream.readUTF();
        }
        else {
            return null;
        }
    }

    public void updateStockRecords(String getStocksResponse) {
        match = stocksSummaryPattern.matcher(getStocksResponse);
        while(match.find()) {
            String stockSummary = getStocksResponse.substring(match.start(), match.end());
            String stockId = match.group(1);
            int currentValue = Integer.parseInt(match.group(2));
            int numAvailable = Integer.parseInt(match.group(3));

            marketState.put(stockId, new Integer[] {currentValue, numAvailable});
        }
    }

    public void printRecords() {
        System.out.println();
        for(Map.Entry<String, Integer[]> entry : marketState.entrySet()) {
            System.out.println("Stock ID: " + entry.getKey());
            System.out.println("Current price: " + entry.getValue()[0]);
            System.out.println("Number available: " + entry.getValue()[1]);
            System.out.println();
        }
    }

    public String getStocks() throws IOException {
        return sendMessage("STOCKS", true);
    }

    public String buyStock(String stockId, int amount) throws IOException {
        return sendMessage("BUY " + stockId + " " + id + " " + amount, true);
    }

    public String registerWithMarket() throws IOException {
        id = sendMessage("REGISTER", true);
        return id;
    }

    public void closeConnection() throws IOException {
        sendMessage("QUIT", false);
        market.close();
    }

    public void buyRandomStock() throws IOException {
        List<String> keysAsArray = new ArrayList<String>(marketState.keySet());
        Random r = new Random();

        String randId = keysAsArray.get(r.nextInt(keysAsArray.size()));
        int amount = r.nextInt(marketState.get(randId)[1]);

        System.out.print("Buying " + amount + " of " + randId + " (");

        System.out.println(buyStock(randId, amount) + ")");
    }
}
