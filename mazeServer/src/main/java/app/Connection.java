package app;

import lombok.Data;

import java.net.Socket;

@Data
public class Connection  {
    Socket socket;
    private double finishTime;
    private long playerId;
    private String playerName;
    boolean isReady = false;

    public Connection(Socket socket) {
        this.socket = socket;
    }
}
