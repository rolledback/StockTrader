import java.net.*;
import java.io.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.ArrayList;
import java.util.Map;

public class MarketSocket implements Runnable {

    private Market owner;
    private MarketServer parent;

    private Socket server;
    private ServerSocket serverSocket;
    private DataInputStream inStream;
    private DataOutputStream outStream;

    private Pattern buyStockPattern = Pattern.compile("BUY ([\\w-]+) ([\\w-]+) (\\d+)");
    private Pattern sellStockPattern = Pattern.compile("SELL ([\\w-]+) ([\\w-]+) (\\d+)");
    private Matcher match;

    private int port;
    private String traderId;
    private String traderIp;
    private String tag;
    private boolean busy;

    public MarketSocket(Market owner, MarketServer parent, int port, int index) throws IOException {
        this.owner = owner;
        this.parent = parent;
        this.traderIp = traderIp;
        this.port = port;
        this.tag = "CON-" + index + "-" + port;        
        this.busy = false;

        try {
            serverSocket = new ServerSocket(port);
        }
        catch (IOException e) {
           throw e;
        }
    }

    // return the port of this connection if it isn't busy, otherwise return -1
    public int isBusy() {
        if(busy) {
            return -1;
        }
        else {
            return port;
        }
    }

    public void run() {
        try {
            busy = true;
            Util.print(tag, "Waiting for client to connect.");
            server = serverSocket.accept();
            traderIp = server.getRemoteSocketAddress().toString();

            inStream = new DataInputStream(server.getInputStream());
            outStream = new DataOutputStream(server.getOutputStream());

            Util.print(tag, "Client connected from " + server.getRemoteSocketAddress());

            while(true) {
                StringBuilder responseBuilder = new StringBuilder();

                try {
                    while(inStream.available() == 0) {
                        Thread.sleep(10);
                    }
                }
                catch(InterruptedException e) {}

                String message = inStream.readUTF();
                String response = "";
                RESULT result = RESULT.FAILURE;

                // big switch to determine response, should probably be moved to a method
                if(message.equals("REGISTER")) {
                    traderId = owner.registerTrader();
                    response = traderId;
                }
                else if(message.equals("QUIT")) {
                    Util.print(tag, "Client requesting to end session.");
                    break;
                }
                else if(message.equals("STOCKS")) {
                    Map<String, Object[]> stockValues = owner.getStocks();

                    // this will be used in the future
                    ArrayList<String> stocks = owner.getStocksInJson();
                    response = stocks.toString();
                }
                else {
                    try {
                        if((match = buyStockPattern.matcher(message)).matches()) {
                            result = owner.buyStock(match.group(1), match.group(2), Integer.parseInt(match.group(3)));
                        }
                        else if((match = sellStockPattern.matcher(message)).matches()) {
                            result = owner.sellStock(match.group(1), match.group(2), Integer.parseInt(match.group(3)));
                        }
                    }
                    catch(Exception e) { System.out.println(e.toString()); }
                    response = result.toString();
                }

                // always write response, client will look for it if they need to
                outStream.writeUTF(response);
            }

            server.close();
        }
        catch(SocketTimeoutException s) {
            Util.print(tag, "Socket timed out.");
        }
        catch(IOException e) {
            Util.print(tag, e.toString());
            e.printStackTrace();
        }
        Util.print(tag, "Finishing.");
        busy = false;
    }

    public String toString() {
        return tag;
    }
}
