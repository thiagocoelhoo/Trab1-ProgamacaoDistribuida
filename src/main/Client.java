package main;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.Scanner;

public class Client {
    private int multicastPort;
    private String multicastGroup;

    Client(String multicastGroup, int multicastPort) {
        this.multicastGroup = multicastGroup;
        this.multicastPort = multicastPort;
    }

    public void run() throws Exception {
        MulticastSocket multicastSocket = new MulticastSocket(multicastPort);
        InetAddress multicastIP = InetAddress.getByName(multicastGroup);
        InetSocketAddress group = new InetSocketAddress(multicastIP, multicastPort);
        NetworkInterface netInerface = NetworkInterface.getByName("wlp0s20f3"); //wlo1
        multicastSocket.joinGroup(group, netInerface);
        
        byte buffer[] = new byte[1024];
        DatagramPacket pct;
        
        while (true) {
            pct = new DatagramPacket(buffer, buffer.length);
            multicastSocket.receive(pct);
            System.out.write(buffer, 0, pct.getLength());
            System.out.println();
        }    

        // multicastSocket.leaveGroup(group, netInerface);
        // multicastSocket.close();
    }

    public static void main(String args[]) {
        try {
            Client client = new Client("224.0.0.1", 2000);
            client.run();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
