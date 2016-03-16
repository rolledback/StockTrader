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
                System.out.println("Waiting for client to connect.");
                server = serverSocket.accept();                
                System.out.println("Client connected from " + server.getRemoteSocketAddress());

                inStream = new DataInputStream(server.getInputStream());
                outStream = new DataOutputStream(server.getOutputStream());

                while(true) {
                    String message = inStream.readUTF();
                    String response = "";

                    // big switch to determine response, should probably be moved to a method
                    if(message.equals("REGISTER")) {
                        response = owner.registerTrader();
                    }
                    else if(message.equals("QUIT")) {
                        System.out.println("Client requesting to end session.\n");
                        break;
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
