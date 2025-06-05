package main;
/*
Considere um sistema onde quatro drones sobrevoam determinados ambientes (divididos em leste,
oeste, norte e sul) e por meio de sensores, coletam dados de importantes elementos climáticos como
pressão atmosférica, radiação solar, temperatura e umidade.
    - Drone norte coleta os dados no formato: pressao-radiacao-temperatura-umidade.
    - Drone sul coleta os dados no formato: (pressao;radiacao;temperatura;umidade).
    - Drone leste coleta os dados no formato: {pressao,radiacao,temperatura,umidade}.
    - Drone oeste coleta os dados no formato: pressao#radiacao#temperatura#umidade.
*/

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.lang.Math;
import java.net.Socket;
import java.util.Scanner;

enum DroneType {
    NORTE,
    SUL,
    LESTE,
    OESTE
}

public class Drone {
    private DroneType tipo;
    private Logger logger;

    public Drone(DroneType tipo) {
        this.tipo = tipo;
        logger = new Logger(String.format("Drone_%s.log", tipo.toString()));
    }

    private DroneData collectData() {
        double pressao = Math.random() * 1000 + 950; // hPa
        double radiacao = Math.random() * 1000; // W/m^2
        double temperatura = Math.random() * 30 + 15; // °C
        double umidade = Math.random() * 100; // %

        return new DroneData(pressao, radiacao, temperatura, umidade);
    }

    public String getDroneData() {
        DroneData data = collectData();
        switch (tipo) {
            case NORTE:
                return String.format("%f-%f-%f-%f", data.pressao, data.radiacao, data.temperatura, data.umidade);
            case SUL:
                return String.format("(%f;%f;%f;%f)", data.pressao, data.radiacao, data.temperatura, data.umidade);
            case LESTE:
                return String.format("{%f,%f,%f,%f}", data.pressao, data.radiacao, data.temperatura, data.umidade);
            case OESTE:
                return String.format("%f#%f#%f#%f", data.pressao, data.radiacao, data.temperatura, data.umidade);
            default:
                return "";
        }
    }

    public void sendData(String serverIp, int serverPort) throws Exception {
        Socket socket = new Socket(serverIp, serverPort);
        DataOutputStream outStream = new DataOutputStream(socket.getOutputStream());
        DataInputStream inStream = new DataInputStream(socket.getInputStream());
        
        String message = getDroneData();
        logger.log(message);

        outStream.writeUTF(message);
        String response = inStream.readUTF();
        
        logger.log("Response: " + response);

        socket.close();
    }

    public void run(String serverIp, int serverPort) throws Exception {
        logger.log("Inicializando drone...");
        
        while (true) {
            sendData(serverIp, serverPort);
            // Sleep aleatório
            long interval = (long) Math.ceil((Math.random() * 3 + 2) * 1000);
            Thread.sleep(interval);
        }
    }

    public static void main(String args[]) {
        Scanner s = new Scanner(System.in);
        String serverIp;
        int serverPort;
        
        System.out.print("Server IP: ");
        serverIp = s.nextLine();
        
        System.out.print("Server port: ");
        serverPort = s.nextInt();
        
        System.out.println("Escolha a região do drone");
        System.out.println("1 - NORTE");
        System.out.println("2 - SUL");
        System.out.println("3 - LESTE");
        System.out.println("4 - OESTE");

        System.out.print("Região:");
        int regiao = s.nextInt();
        Drone drone;

        switch (regiao) {
            case 1:
                drone = new Drone(DroneType.NORTE);
                break;
            case 2:
                drone = new Drone(DroneType.SUL);
                break;
            case 3:
                drone = new Drone(DroneType.LESTE);
                break;
            case 4:
                drone = new Drone(DroneType.OESTE);
                break;
            default: {
                System.out.println("Região inválida.");
                return;
            }
        }

        try {
            drone.run(serverIp, serverPort);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}