import java.net.*;
import java.io.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;
import java.lang.StringBuilder;

public class ConsoleSocket implements Runnable {

    private Market owner;
    private MarketServer parent;

    private Socket server;
    private ServerSocket serverSocket;
    private DataInputStream inStream;
    private DataOutputStream outStream;

    private int consolePort = 5655;
    private final String tag = "CONSOLE";
    private final String consoleKey = "SECRET_KEY";
    private boolean authenticated;

    private Pattern filterLogPattern = Pattern.compile("log( -t [\\w-]+)*( -s [\\w-]+)*");
    private Matcher match;

    private static Map<String, String> validCommands;
    static {
        validCommands = new LinkedHashMap<String, String>();
        validCommands.put("quit", "Disconnect from the console socket.");
        validCommands.put("help", "List all available commands.");
        validCommands.put("debug", "Produces a full debug dump of the market.");
        validCommands.put("log", "Prints out the full market's trading log.\n\tOptinal Arguments:\n\t-t <trader id to match>\n\t-s <stock id to match>");
    }

    public ConsoleSocket(Market owner) throws IOException {
        this.owner = owner;
        this.authenticated = false;

        try {
            serverSocket = new ServerSocket(consolePort);
        }
        catch (IOException e) {
           throw e;
        }
    }

    public void run() {
        while(true) {
            try {
                authenticated = false;
                Util.print(tag, "Waiting for console client to connect.");
                server = serverSocket.accept();

                inStream = new DataInputStream(server.getInputStream());
                outStream = new DataOutputStream(server.getOutputStream());

                Util.print(tag, "Console client connected from " + server.getRemoteSocketAddress());

                while(true) {
                    StringBuilder responseBuilder = new StringBuilder();

                    try {
                        while(inStream.available() == 0) {
                            Thread.sleep(10);
                        }
                    }
                    catch(InterruptedException e) {}

                    String cmd = inStream.readUTF();
                    String response = handleCommand(cmd);

                    if(!authenticated) {
                        if(cmd.equals(consoleKey)) {
                            authenticated = true;
                            Util.print(tag, "Concsole client has been authenticated.");
                            response = "Authentication success.";
                        }
                        else {
                            Util.print(tag, "Concsole client unable to authentice.");
                            response = "Authentication failure.";
                            break;
                        }
                    }
                    else {
                        Util.print(tag, "Command received: " + cmd);

                        if(response.equals("")) {
                            break;
                        }
                    }

                    outStream.writeUTF(response);
                }

                Util.print(tag, "Closing connection.");
                server.close();
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
        Util.print(tag, "Finishing.");
    }

    public String toString() {
        return tag;
    }

    private String handleCommand(String command) {
        StringBuilder responseBuilder = new StringBuilder();

        if(command.equals("quit")) {
            return "";
        }
        else if(command.equals("help")) {
            for (Map.Entry<String,String> entry : validCommands.entrySet()) {
                responseBuilder.append(entry.getKey() + ": " + entry.getValue());
                responseBuilder.append("\n");
            }
            return responseBuilder.toString();
        }
        else if(command.equals("debug")) {
            return owner.debugDump();
        }
        else if(command.equals("log")) {
            return owner.tradingLog();
        }
        else if((match = filterLogPattern.matcher(command)).matches()) {
            String traderId = ""; 
            String stockId = "";

            if(match.group(1) != null) {
                traderId = match.group(1).substring(match.group(1).lastIndexOf(' ') + 1);
            }
            if(match.group(2) != null) {
                stockId = match.group(2).substring(match.group(2).lastIndexOf(' ') + 1);
            }

            return owner.tradingLog(traderId, stockId);
        }
        return "Invalid command. Run \"help\" for a list of valid commands.";
    }
}
