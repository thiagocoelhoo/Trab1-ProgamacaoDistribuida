package main;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


enum Algorithm {
    ROUND_ROBIN(1),
    RANDOM(2);

    private final int code;

    Algorithm(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static Algorithm fromCode(int code) {
        for (Algorithm algorithm: Algorithm.values()) {
            if (algorithm.getCode() == code) {
                return algorithm;
            }
        }

        throw new IllegalArgumentException("Código inválido: " + code);
    }
}

public class LoadBalancer {
    int port;
    Algorithm algorithm;
    
    int serverIndex = 0;
    List<IPAddress> serverAddresses;
    Logger logger;

    LoadBalancer(int port, int algorithm) {
        this.port = port;
        this.algorithm = Algorithm.fromCode(algorithm);
        this.serverAddresses = new LinkedList<>();
        this.logger = new Logger(String.format("%s.log", LoadBalancer.class.getName()));
    }

    public void registerServer(String serverIP, int serverPort) {
        serverAddresses.add(new IPAddress(serverIP, serverPort));
    }

    private synchronized IPAddress select() {
        switch (algorithm) {
            case ROUND_ROBIN: {
                IPAddress addr = serverAddresses.get(serverIndex);
                serverIndex += 1;
                serverIndex %= serverAddresses.size();
                return addr;
            }
            
            case RANDOM: {
                int index = (int) Math.floor(Math.random() * serverAddresses.size());
                IPAddress addr = serverAddresses.get(index);
                return addr;
            }

            default:
                return serverAddresses.get(0);
        }
    }

    private void handleClient(Socket clientSocket) throws Exception {
        // Create client streams
        DataInputStream inStream = new DataInputStream(clientSocket.getInputStream());
        DataOutputStream outStream = new DataOutputStream(clientSocket.getOutputStream());
        
        // Connect to server
        IPAddress addr = select();
        Socket server = new Socket(addr.ip, addr.port);
        DataOutputStream serverOutStream = new DataOutputStream(server.getOutputStream());
        DataInputStream serverInStream = new DataInputStream(server.getInputStream());
        
        // Get request
        String request = inStream.readUTF();
        serverOutStream.writeUTF(request);
        logger.log(request);

        // Send response
        String response = serverInStream.readUTF();
        outStream.writeUTF(response);
        logger.log(response);

        // Close connections
        server.close();
        clientSocket.close();
    }

    public void run() throws Exception {
        ServerSocket serverSocket = new ServerSocket(port);
        ExecutorService executor = Executors.newFixedThreadPool(10);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            executor.submit(() -> {
                try {
                    handleClient(clientSocket);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        }

    }

    public static void main(String args[]) {
        Scanner s = new Scanner(System.in);

        int algorithm;
        
        String serverHost1;
        String serverHost2;
        
        int serverPort1;
        int serverPort2;
        int port;

        System.out.print("Load balancer port: ");
        port = s.nextInt();

        System.out.println("\nServer 1");
        System.out.print("IP server 1: ");
        serverHost1 = s.next();
        System.out.print("Port server 1: ");
        serverPort1 = s.nextInt();

        System.out.println("\nServer 2");
        System.out.print("IP server 2: ");
        serverHost2 = s.next();
        System.out.print("Port server 2: ");
        serverPort2 = s.nextInt();

        System.out.println("Algorithms: ");
        System.out.println("1 - RoundRobin");
        System.out.println("2 - Random");
        algorithm = s.nextInt();


        try {
            LoadBalancer lb = new LoadBalancer(port, algorithm);
            lb.registerServer(serverHost1, serverPort1);
            lb.registerServer(serverHost2, serverPort2);
            lb.run();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
