import java.net.*;
import java.io.*;

public class MarketServer implements Runnable {

    private Market owner;
    private ServerSocket serverSocket;
    private Socket server;
    private DataInputStream inStream;
    private DataOutputStream outStream;

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
                System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "...");

                server = serverSocket.accept();                
                System.out.println("Just connected to " + server.getRemoteSocketAddress());

                inStream = new DataInputStream(server.getInputStream());
                System.out.println(inStream.readUTF());

                outStream = new DataOutputStream(server.getOutputStream());
                outStream.writeUTF("Thank you for connecting to " + server.getLocalSocketAddress() + "\nGoodbye!");

                server.close();
            }
            catch(SocketTimeoutException s) {
                System.out.println("Socket timed out!");
                break;
            }
            catch(IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
