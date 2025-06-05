package main;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class Datacenter {
    public static void main(String args[]) throws Exception {
        Scanner s = new Scanner(System.in);
        System.out.print("Timer(seconds): ");
        double timer = s.nextDouble();

        Thread.sleep((int)(timer * 1000));

        String message = "KILL";

        String group = "224.0.0.2";
        int multicastPort = 5000;
        
        DatagramSocket ds;
        
        ds = new DatagramSocket();
        byte bufferEnvio[] = message.getBytes();
        
        DatagramPacket pct = new DatagramPacket(
            bufferEnvio,
            bufferEnvio.length,
            InetAddress.getByName(group),
            multicastPort
        );

        ds.send(pct);

        ds.close();        
    }
    
}
