package Database;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import SensorData;

public class Database {
    static void handleClient(Socket client) {
        String message;
        Scanner s = new Scanner(client.getInputStream());

        while (true) {    
            message = s.nextLine();
        }
    }


}
