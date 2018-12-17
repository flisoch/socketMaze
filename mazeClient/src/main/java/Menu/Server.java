package Menu;

import lombok.Data;

import java.net.InetAddress;

@Data
public class Server {
    private InetAddress address;
    private int port;

    private String name;
    private String password;

    private int maxPlayers;
    private int playersCount;

}
