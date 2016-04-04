import java.net.*;
import java.io.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.HashMap;
import java.util.Map;

public class MarketServer implements Runnable {

    private Market owner;
    private ServerSocket serverSocket;
    private Socket server;
    private DataInputStream inStream;
    private DataOutputStream outStream;

    private Pattern buyStockPattern = Pattern.compile("BUY ([\\w-]+) ([\\w-]+) (\\d+)");
    private Pattern sellStockPattern = Pattern.compile("SELL ([\\w-]+) ([\\w-]+) (\\d+)");
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
                    StringBuilder responseBuilder = new StringBuilder();
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
                    else if(message.equals("STOCKS")) {
                        HashMap<String, Integer[]> stockValues = owner.getStocks();
                        responseBuilder.append("{");
                        for(Map.Entry<String, Integer[]> entry : stockValues.entrySet()) {
                            responseBuilder.append("[");
                            responseBuilder.append(entry.getKey());
                            responseBuilder.append(",[");
                            responseBuilder.append(Integer.toString(entry.getValue()[0]));
                            responseBuilder.append(",");
                            responseBuilder.append(Integer.toString(entry.getValue()[1]));
                            responseBuilder.append("]],");
                        }
                        responseBuilder.deleteCharAt(responseBuilder.length() - 1);
                        responseBuilder.append("}");
                        response = responseBuilder.toString();
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
