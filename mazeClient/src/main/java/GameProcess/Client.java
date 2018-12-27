package GameProcess;

import lombok.Data;

@Data
public class Client {
    private String playerName;
    private long playerId;

    public Client(String playerName) {
        this.playerName = playerName;
    }
}
