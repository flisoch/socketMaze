package app;

import java.net.ServerSocket;
import java.net.Socket;

public class HostConnection {
    private Socket hostClient;
    private Server server;

    public void setHostClient(Socket hostClient) {
        this.hostClient = hostClient;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public HostConnection(Socket hostClient) {
        this.hostClient = hostClient;
    }

    public Socket getHostClient() {
        return hostClient;
    }

    public Server getServer() {
        return server;
    }
}
