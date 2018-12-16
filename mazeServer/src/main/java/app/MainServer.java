package app;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MainServer {

    private static ServerSocket serverSocket;
    private static List<GameConfig> gameServerConfigs;

    public static void main(String[] args) {
        MainServer server = new MainServer();
        server.run();

    }

    public MainServer() {
        try {
            serverSocket = new ServerSocket(1234);
            gameServerConfigs = new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void run() {

        while (true) {
            try {
                Socket host = serverSocket.accept();
                HostConnection hostConnection = new HostConnection(host);
                GameConfig gameConfig = new GameConfig(hostConnection);
                gameServerConfigs.add(gameConfig);
                Thread thread = new Thread(() -> {
                    boolean configured = false;
                    try {
                        BufferedReader br = new BufferedReader(new InputStreamReader(host.getInputStream()));
                        String hostMessage = br.readLine();

                        while (!configured) {
                            configured = handleConfigMessage(gameConfig, hostMessage);
                        }
                        createGameServer(hostConnection);

                        PrintWriter writer = new PrintWriter(new OutputStreamWriter(host.getOutputStream()));
                        sendAllInfoToHost(gameConfig, writer);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                thread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendAllInfoToHost(GameConfig gameConfig, PrintWriter writer) {
        //send ip, port, serverName, password
        System.out.println("sending ip, port to Host client!!");
        writer.println("ip:"+gameConfig.getHostConnection().getServer().getServerSocket().getInetAddress().getHostAddress());
        writer.println("port:"+gameConfig.getHostConnection().getServer().getServerSocket().getLocalPort());
        writer.println("password:"+gameConfig.getServerPassword());
        writer.println("end");
        writer.flush();

    }

    private void createGameServer(HostConnection hostConnection) {
        try {
            Server server = new Server(1235 + gameServerConfigs.size());
            hostConnection.setServer(server);
//            System.out.println(hostConnection.getServer().getServerSocket().getInetAddress().getHostAddress());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private boolean handleConfigMessage(GameConfig gameConfig, String hostMessage) {
        //get some settings from hostClient, configure some settings
        String[] parts = hostMessage.split(":");
        String command = parts[0];
        String data = parts[1];
        switch (command){
            /*case "ip":
                gameConfig.setServerIp(Integer.parseInt(data));
                break;
            case "port":
                gameConfig.setServerPort(Integer.parseInt(data));
                break;*/
            case"password":
                gameConfig.setServerPassword(data);
                break;
        }
        return gameConfig.isConfigured();
    }

}
