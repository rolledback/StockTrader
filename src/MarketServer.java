import java.net.*;
import java.io.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class MarketServer implements Runnable {

    private Market owner;
    private ServerSocket serverSocket;
    private Socket server;
    private DataInputStream inStream;
    private DataOutputStream outStream;

    private Pattern buyStockPattern = Pattern.compile("BUY ([\\w-]+) ([\\w-]+) (\\d+)");
    private Matcher match;

    public MarketServer(Market owner) throws IOException {
        this.owner = owner;

        try {
            serverSocket = new ServerSocket(5656);
        }
        catch (IOException e) {
           throw e;
        }
    }

    public void run() {
        while(true) {
            try {
                System.out.println("Waiting for client to connect.");
                server = serverSocket.accept();                
                System.out.println("Client connected from " + server.getRemoteSocketAddress());

                inStream = new DataInputStream(server.getInputStream());
                outStream = new DataOutputStream(server.getOutputStream());

                while(true) {
                    String message = inStream.readUTF();
                    String response = "";
                    RESULT result = RESULT.FAILURE;

                    // big switch to determine response, should probably be moved to a method
                    if(message.equals("REGISTER")) {
                        response = owner.registerTrader();
                    }
                    else if(message.equals("QUIT")) {
                        System.out.println("Client requesting to end session.\n");
                        break;
                    }
                    else if((match = buyStockPattern.matcher(message)).matches()) {
                        try {
                            result = owner.buyStock(match.group(1), match.group(2), Integer.parseInt(match.group(3)));
                            response = result.toString();
                        }
                        catch(Exception e) { /* don't worry about handling this at the moment */ }
                    }

                    // always write response, client will look for it if they need to
                    outStream.writeUTF(response);
                }

                server.close();
            }
            catch(SocketTimeoutException s) {
                System.out.println("Socket timed out.");
                break;
            }
            catch(IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
