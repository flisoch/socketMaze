package Menu;

import lombok.*;

import java.net.InetAddress;

@Data
@Builder
@AllArgsConstructor
public class Server {
    private InetAddress address;
    private int port;
    private String name;
    private String password;
    private int maxPlayers;
    private int playersCount;
    private int mazeHeight;

    @Override
    public String toString() {
        return "Server(address=" + address + " port=" + port + " name=" + name + " maxPlayers=" + maxPlayers + ")";
    }
}
