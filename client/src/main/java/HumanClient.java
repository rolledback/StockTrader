import java.net.*;
import java.io.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Type;
import java.lang.StringBuilder;

import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;

public class HumanClient {

    public static void main(String[] args) {
        try {
            HumanClient one = new HumanClient();
            String myId = one.registerWithMarket();
            if(myId != null && myId != "") {
                System.out.println("Registration succesful. My trader ID is " + myId + ".");
                one.updateStockRecords(one.getStocks());
                one.printStocks();
                for(int i = 0; i < 50; i++) {
                    one.buyRandomStock();
                }
                one.closeConnection();
            }
        }
        catch(Exception e) { System.out.println(e.toString()); }
    }

    private Gson gson = new Gson();

    private Socket market;
    private DataOutputStream outStream;
    private DataInputStream inStream;
    private String id;

    private List<ClientStock> stocks;

    private final String marketAddress = "127.0.0.1";
    private final int marketPort = 5656;

    public HumanClient() throws IOException {
        stocks = new ArrayList<ClientStock>();

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
        Type listType = new TypeToken<List<ClientStock>>() {}.getType();
        stocks = gson.fromJson(getStocksResponse, listType);
    }

    public void printStocks() {
        System.out.println("Available Stocks");
        for(ClientStock stock : stocks) {
            System.out.println(stock.toString() + "\n");
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
        Random r = new Random();

        ClientStock randStock = stocks.get(r.nextInt(stocks.size()));
        String randId = randStock.id;
        int amount = r.nextInt(randStock.numAvailable);

        System.out.print("Buying " + amount + " of " + randId + " (");

        System.out.println(buyStock(randId, amount) + ")");
    }
}
