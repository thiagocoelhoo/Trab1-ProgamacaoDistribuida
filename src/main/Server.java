package main;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    String databaseIp;
    int databasePort;
    int port;
    int multicastPort = 2000;
    ExecutorService executor;
    Logger logger;
    
    Server(String databaseIp, int databasePort, int port) {
        this.databaseIp = databaseIp;
        this.databasePort = databasePort;
        this.port = port;
        this.logger = new Logger(String.format("%s_%d.log", Server.class.getName(), port));
    }

    private String sendToDatabase(String message) throws Exception {
        Socket databaseSocket = new Socket(databaseIp, databasePort);
        DataOutputStream outStream = new DataOutputStream(databaseSocket.getOutputStream());
        DataInputStream inStream = new DataInputStream(databaseSocket.getInputStream());

        String response = "";

        try {
            outStream.writeUTF(message);

            while (!(response.endsWith("OK") || response.endsWith("END"))) {    
                response += inStream.readUTF();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        databaseSocket.close();

        return response;
    }

    // private String recvFromDatabase() throws Exception {
    //     Socket databaseSocket = new Socket(databaseIp, databasePort);
    //     DataInputStream inStream = new DataInputStream(databaseSocket.getInputStream());
    //     String message = inStream.readUTF();
    //     databaseSocket.close();

    //     return message;
    // }

    private void updateUsers() throws Exception {
        String response = sendToDatabase("read");
        logger.log("RESPONSE: " + response + ".");

        String group = "224.0.0.1";

        DatagramSocket ds = new DatagramSocket();
        byte bufferEnvio[] = response.getBytes();
        
        DatagramPacket pct = new DatagramPacket(
            bufferEnvio,
            bufferEnvio.length,
            InetAddress.getByName(group),
            multicastPort
        );

        ds.send(pct);
        ds.close();
    }

    private void handleDrone(Socket drone) throws Exception {
        DataInputStream inStream = new DataInputStream(drone.getInputStream());
        DataOutputStream outStream = new DataOutputStream(drone.getOutputStream());
        String message;

        message = inStream.readUTF();
        logger.log(message);

        DroneData data = DroneData.parse(message);
        sendToDatabase(data.toString());

        outStream.writeUTF("Bye!"); // TODO: REMOVER ISSO
        drone.close();
    }

    public void run() throws Exception {
        ServerSocket serverSocket = new ServerSocket(port);
        executor = Executors.newFixedThreadPool(10);
        // ExecutorService exe2 = Executors.newScheduledThreadPool(1);

        System.out.printf("Servidor escutando na porta %d\n", port);
        
        executor.submit(() -> {
            try {
                while (true) {
                    updateUsers();
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        while (true) {
            Socket clientSocket = serverSocket.accept();
            executor.submit(() -> {
                try {
                    handleDrone(clientSocket);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public static void main(String args[]) {
        Scanner s = new Scanner(System.in);
        String databaseIp;
        int databasePort;
        int port;

        System.out.print("Database IP: ");
        databaseIp = s.nextLine();

        System.out.print("Database port: ");
        databasePort = s.nextInt();

        System.out.print("Port: ");
        port = s.nextInt();

        try {
            Server server = new Server(databaseIp, databasePort, port);
            server.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
