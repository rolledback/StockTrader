import java.net.*;
import java.io.*;

public class HumanClient {

    public static void main(String[] args) {
        HumanClient one = new HumanClient();
    }

    private Socket client;
    private DataOutputStream outStream;
    private DataInputStream inStream;

    private final String address = "127.0.0.1";
    private final int port = 5656;

    public HumanClient() {
        try {
            System.out.println("Connecting to " + address + " on port " + port);

            client = new Socket(address, port);
            System.out.println("Just connected to " + client.getRemoteSocketAddress());

            outStream = new DataOutputStream(client.getOutputStream());
            outStream.writeUTF("Hello from " + client.getLocalSocketAddress());

            DataInputStream in = new DataInputStream(client.getInputStream());
            System.out.println("Server says " + in.readUTF());
            client.close();
        }
        catch (IOException e) {
            System.out.println(e);
        }
    }
}
