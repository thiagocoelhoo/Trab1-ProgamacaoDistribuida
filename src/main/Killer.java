package main;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.concurrent.ExecutorService;

public class Killer {
    public void killAll(int port, String groupIP, String interfaceName, ExecutorService executor) {
        try {
            MulticastSocket multicastSocket = new MulticastSocket(port);
            InetAddress multicastIP = InetAddress.getByName(groupIP);
            InetSocketAddress group = new InetSocketAddress(multicastIP, port);
            NetworkInterface netInerface = NetworkInterface.getByName(interfaceName); //wlo1
            multicastSocket.joinGroup(group, netInerface);
            
            byte buffer[] = new byte[1024];
            DatagramPacket pct;
            
            pct = new DatagramPacket(buffer, buffer.length);
            multicastSocket.receive(pct);

            executor.shutdownNow();

            System.out.write(buffer, 0, pct.getLength());
            System.out.println();
        } catch (Exception e) {
        }
    }
}