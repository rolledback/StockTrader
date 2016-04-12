import java.net.*;
import java.io.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class MarketServer implements Runnable {

    private Market owner;

    private Socket server;
    private ServerSocket serverSocket;
    private DataOutputStream outStream;

    private Pattern buyStockPattern = Pattern.compile("BUY ([\\w-]+) ([\\w-]+) (\\d+)");
    private Pattern sellStockPattern = Pattern.compile("SELL ([\\w-]+) ([\\w-]+) (\\d+)");
    private Matcher match;

    private int port = 5656;
    private int nextPort = port;
    private List<MarketSocket> connections;

    private String tag = "SERVER";

    private final String consoleKey = "SECRET_KEY";

    public MarketServer(Market owner) throws IOException {
        this.owner = owner;
        connections = new ArrayList<MarketSocket>();

        try {
            serverSocket = new ServerSocket(port);
        }
        catch (IOException e) {
           throw e;
        }
    }

    private int getFirstAvailConnection() throws IOException {
        MarketSocket connection;
        for(int index = 0; index < connections.size(); index++) {
            connection = connections.get(index);
            int port = connection.isBusy();
            if(port != -1) {
                String threadTitle = "MarketSocket-" + index + "-" + port;
                Util.print(tag, "Reusing: " + threadTitle);
                new Thread(connections.get(index), threadTitle).start();
                return port;
            }
        }
        return -1;
    }

    private int createNextMarketSocket() throws IOException {
        nextPort++;
        int index = connections.size();
        connections.add(new MarketSocket(owner, this, nextPort, index));

        String threadTitle = "MarketSocket-" + index + "-" + nextPort;
        Util.print(tag, "Starting: " + threadTitle);
        new Thread(connections.get(index), threadTitle).start();
        return nextPort;
    }

    public void run() {
        while(true) {
            try {
                Util.print(tag, "Finding next available connection.");
                int portToCommunicate;
                if((portToCommunicate = getFirstAvailConnection()) == -1) {
                    portToCommunicate = createNextMarketSocket();
                }
                
                Util.print(tag, "Waiting for client to connect.");
                server = serverSocket.accept();                
                Util.print(tag, "Client connected from " + server.getRemoteSocketAddress());

                outStream = new DataOutputStream(server.getOutputStream());
                outStream.writeUTF(Integer.toString(portToCommunicate));
                Util.print(tag, "Client instructed to reconnect to last found connnection on port " + portToCommunicate + ".");
            }
            catch(SocketTimeoutException s) {
                Util.print(tag, "Socket timed out.");
                break;
            }
            catch(IOException e) {
                Util.print(tag, e.toString());
                e.printStackTrace();
                break;
            }
        }
    }
}
