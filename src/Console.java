import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Console {

    public static void main(String[] args) {

    }

    private final String consoleKey = "SECRET_KEY";
    private Scanner console;
    private String prompt = "> ";
    private Socket market;
    private DataOutputStream outStream;
    private DataInputStream inStream;

    private final String marketAddress = "127.0.0.1";
    private int consolePort = 5655;

    public Console() {
        console = new Scanner(System.in);
    }

    private void connectToMarket() throws IOException {
        System.out.println("Connecting to market...");
        if(market != null) {
            market.close();
        }

        market = new Socket(marketAddress, consolePort);
        System.out.println("Succesfully connected to market at " + market.getRemoteSocketAddress());

        outStream = new DataOutputStream(market.getOutputStream());
        inStream = new DataInputStream(market.getInputStream());
    }

    private void start() {
        System.out.print("> ");
        while(console.hasNextLine()) {
            String cmd = console.nextLine();

            System.out.print("> ");
        }
    }

}