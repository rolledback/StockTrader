import java.net.*;
import java.io.*;
import java.util.Scanner;

public class ConsoleClient {

    public static void main(String[] args) {
        ConsoleClient console = new ConsoleClient();
        try {
            console.connectToMarket();
            console.start();
        }
        catch(Exception e) {}
    }

    private final String consoleKey = "SECRET_KEY";
    private Scanner console;
    private String prompt = "> ";
    private Socket consoleSocket;
    private DataOutputStream outStream;
    private DataInputStream inStream;

    private final String marketAddress = "127.0.0.1";
    private int consolePort = 5655;

    public ConsoleClient() {
        console = new Scanner(System.in);
    }

    private void connectToMarket() throws IOException {
        System.out.println("Connecting to console socket...");
        if(consoleSocket != null) {
            consoleSocket.close();
        }

        consoleSocket = new Socket(marketAddress, consolePort);
        System.out.println("Succesfully connected to console socket at " + consoleSocket.getRemoteSocketAddress());

        outStream = new DataOutputStream(consoleSocket.getOutputStream());
        inStream = new DataInputStream(consoleSocket.getInputStream());
        outStream.writeUTF(consoleKey);

        System.out.println(inStream.readUTF());
    }

    private void start() throws IOException {
        System.out.print("> ");
        while(console.hasNextLine()) {
            String cmd = console.nextLine();

            outStream.writeUTF(cmd);
            System.out.println(inStream.readUTF());

            System.out.print("> ");
        }
    }

}
