package app;

import lombok.Data;

import java.net.InetAddress;
import java.net.Socket;

@Data
public class GameConfig {

    private InetAddress address;
    private int port;
    private String name;
    private String serverPassword;
    private int maxPlayers;
    private int playersCount = 0;
    private int mazeHeight;

}
