import java.net.*;
import java.io.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.ArrayList;
import java.util.List;
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

    private List<ConsoleCommand> commands;

    public ConsoleSocket(Market owner) throws IOException {
        this.owner = owner;
        this.authenticated = false;

        setupCommandsList();

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

    public String handleCommand(String cmd) {
        for(ConsoleCommand command : commands) {
            if(command.callback.matchesCommand(cmd)) {
                return command.callback.onInvoke();
            }
        }
        return "Invalid command.";
    }

    public void setupCommandsList() {
        commands = new ArrayList<ConsoleCommand>();
        commands.add(new ConsoleCommand("quit", "Disconnect from the console socket.", new ConsoleCommand.CommandCallback() {
            public String onInvoke() {
                return "";
            }

            public boolean matchesCommand(String candidate) {
                return candidate.equals("quit");
            }
        }));
        commands.add(new ConsoleCommand("help", "List all available commands.", new ConsoleCommand.CommandCallback() {
            public String onInvoke() {
                StringBuilder responseBuilder = new StringBuilder();
                for (ConsoleCommand command : commands) {
                    responseBuilder.append(command.command + ": " + command.help);
                    responseBuilder.append("\n");
                }
                return responseBuilder.toString();
            }

            public boolean matchesCommand(String candidate) {
                return candidate.equals("help");
            }
        }));
        commands.add(new ConsoleCommand("debug", "Produces a full debug dump of the market.", new ConsoleCommand.CommandCallback() {
            public String onInvoke() {
                return owner.debugDump();
            }

            public boolean matchesCommand(String candidate) {
                return candidate.equals("debug");
            }
        }));
        commands.add(new ConsoleCommand("start", "Start the market simulation.", new ConsoleCommand.CommandCallback() {
            public String onInvoke() {
                boolean effect = owner.changeRunning(true);
                if(effect) {
                    return "Simulation started.";
                }
                return "Simluation already running.";
            }

            public boolean matchesCommand(String candidate) {
                return candidate.equals("start");
            }
        }));
        commands.add(new ConsoleCommand("stop", "Stop the market simulation.", new ConsoleCommand.CommandCallback() {
            public String onInvoke() {
                boolean effect = owner.changeRunning(false);
                if(effect) {
                    return "Simulation stopped.";
                }
                return "Simluation already stopped.";
            }

            public boolean matchesCommand(String candidate) {
                return candidate.equals("stop");
            }

        }));
        commands.add(new ConsoleCommand("log", "Prints out the full market's trading log.\n\tOptinal Arguments:\n\t-t <trader id to match>\n\t-s <stock id to match>", new ConsoleCommand.CommandCallback() {
            public String onInvoke() {
                return owner.tradingLog(args.get("-t"), args.get("-s"));
            }

            public boolean matchesCommand(String candidate) {
                Pattern filterLogPattern = Pattern.compile("log( -t [\\w-]+)*( -s [\\w-]+)*");
                Matcher match;

                if((match = filterLogPattern.matcher(candidate)).matches()) {
                    args.put("-t", "");
                    args.put("-s", "");

                    if(match.group(1) != null) {
                        args.put("-t", match.group(1).substring(match.group(1).lastIndexOf(' ') + 1));
                    }
                    if(match.group(2) != null) {
                        args.put("-s", match.group(2).substring(match.group(2).lastIndexOf(' ') + 1));
                    }
                    return true;
                }
                else {
                    return candidate.equals("log");
                }
            }
        }));
    }
}
