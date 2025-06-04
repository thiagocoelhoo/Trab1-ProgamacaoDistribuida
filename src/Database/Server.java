package Database;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    private ServerSocket serverSocket;
    private int port;
    private Logger logger;
    
    Server(int port) {
        this.port = port;
        this.logger = new Logger();
    }

    private void handleClient(Socket clientSocket) {
        try {
            // Setup client
            Scanner in_s = new Scanner(clientSocket.getInputStream());
            OutputStream out_s = clientSocket.getOutputStream();
            String message;

            // Read the message
            do {
                message = in_s.nextLine();
                System.out.println(message);
            } while (!message.isEmpty());
    
            // Send response
            out_s.write("Hello".getBytes());

            // Close connection
            out_s.close();
            in_s.close();
            clientSocket.close();
        } catch (IOException e) {
            logger.log("Exception: " + e.getMessage());
        }
    }

    public void run() throws IOException {
        logger.log(String.format("Running server on port %d", port));

        serverSocket = new ServerSocket(port);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            Thread clientThread = new Thread(() -> { handleClient(clientSocket); });
            clientThread.start();
        }
    }

    public static void main(String args[]) {
        Server server = new Server(8000);
        
        try {
            server.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
}
