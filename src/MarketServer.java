import java.net.*;
import java.io.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.HashMap;
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
    private ArrayList<MarketConnection> connections;

    private String tag = "SERVER";

    public MarketServer(Market owner) throws IOException {
        this.owner = owner;
        connections = new ArrayList<MarketConnection>();

        try {
            serverSocket = new ServerSocket(port);
        }
        catch (IOException e) {
           throw e;
        }
    }

    private void createNextMarketConnection() throws IOException {
        nextPort++;
        int index = connections.size();
        connections.add(new MarketConnection(owner, this, nextPort, index));

        String threadTitle = "MarketConnection-" + index + "-" + nextPort;
        Util.print(tag, "Starting: " + threadTitle);
        new Thread(connections.get(index), threadTitle).start();
    }

    public void run() {
        while(true) {
            try {
                createNextMarketConnection();

                Util.print(tag, "Waiting for client to connect.");
                server = serverSocket.accept();                
                Util.print(tag, "Client connected from " + server.getRemoteSocketAddress());

                outStream = new DataOutputStream(server.getOutputStream());
                outStream.writeUTF(Integer.toString(nextPort));
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
