import java.net.*;
import java.io.*;

public class HumanClient {

    public static void main(String[] args) {
        try {
            HumanClient one = new HumanClient();
            String myId = one.registerWithMarket();
            if(myId != null && myId != "") {
                System.out.println("Registration succesful. My trader ID is " + myId + ".");
                System.out.println("Result of attempting to buy stock: " + one.buyStock("0IPC-HINL", 250));
                System.out.println("Result of attempting to buy stock: " + one.buyStock("0IPC-HINL", 500));
                System.out.println("Result of attempting to buy stock: " + one.buyStock("0IPC-HEL2", 250));
                one.closeConnection();
            }
        }
        catch(Exception e) {}
    }

    private Socket client;
    private DataOutputStream outStream;
    private DataInputStream inStream;
    private String id;

    private final String address = "127.0.0.1";
    private final int port = 5656;

    public HumanClient() throws IOException {
        System.out.println("Connecting to market at " + address + " on port " + port);

        client = new Socket(address, port);
        System.out.println("Succesfully connected to market at " + client.getRemoteSocketAddress());

        outStream = new DataOutputStream(client.getOutputStream());
        inStream = new DataInputStream(client.getInputStream());
    }

    // object should not send messages directly
    // this should only help methods with specific actions send a message
    private String sendMessage(String message, boolean expectReply) throws IOException {
        outStream.writeUTF(message);
        if(expectReply) {
            return inStream.readUTF();
        }
        else {
            return null;
        }
    }

    public String buyStock(String stockId, int amount) throws IOException {
        return sendMessage("BUY " + stockId + " " + id + " " + amount, true);
    }

    public String registerWithMarket() throws IOException {
        id = sendMessage("REGISTER", true);
        return id;
    }

    public void closeConnection() throws IOException {
        sendMessage("QUIT", false);
        client.close();
    }
}
