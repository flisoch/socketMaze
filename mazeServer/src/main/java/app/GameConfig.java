package app;

import java.net.Socket;

public class GameConfig {
    public HostConnection getHostConnection() {
        return hostConnection;
    }

    private HostConnection hostConnection;
    private String serverPassword;

    public boolean isConfigured(){
        return serverPassword != null;

    }

    public GameConfig(HostConnection hostConnection) {
        this.hostConnection = hostConnection;
    }

    public String getServerPassword() {
        return serverPassword;
    }

    public void setServerPassword(String serverPassword) {
        this.serverPassword = serverPassword;
    }
}
