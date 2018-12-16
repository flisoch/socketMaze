package app;

import java.net.Socket;

public class GameConfig {
    public HostConnection getHostConnection() {
        return hostConnection;
    }

    private HostConnection hostConnection;
//    private int serverIp;
//    private int serverPort;
    private String serverPassword;

    public boolean isConfigured(){
        return serverPassword != null;

//        return (serverIp != 0) && (serverPort != 0) && (serverPassword != null);
    }

    public GameConfig(HostConnection hostConnection) {
        this.hostConnection = hostConnection;
    }


   /* public int getServerIp() {
        return serverIp;
    }

    public int getServerPort() {
        return serverPort;
    }*/

    public String getServerPassword() {
        return serverPassword;
    }

    /*public void setServerIp(int serverIp) {
        this.serverIp = serverIp;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }*/

    public void setServerPassword(String serverPassword) {
        this.serverPassword = serverPassword;
    }
}
