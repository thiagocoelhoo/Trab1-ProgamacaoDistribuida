package main;

public class IPAddress {
    public String ip;
    public int port;

    IPAddress(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String toString() {
        return String.format("IPAddress(%s, %d)", ip, port);
    }
}
