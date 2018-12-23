package app;


import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class MainServer {

    private static final int PORT = 1234;
    private static ServerSocket serverSocket;
    private static List<GameConfig> gameServerConfigs;
    enum Command {CREATE_SERVER, SAVE_SERVER_CONFIGURATION, DISCONNECT_MAIN};

    public static void main(String[] args) {
        MainServer server = new MainServer();
        server.run();

    }

    public MainServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            gameServerConfigs = new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void run() {

        while (true) {
            try {
                Socket host = serverSocket.accept();

                Thread thread = new Thread(() -> {
                    boolean configured = false;
                    try {
                        BufferedReader br = new BufferedReader(new InputStreamReader(host.getInputStream()));
                        PrintWriter writer = new PrintWriter(new OutputStreamWriter(host.getOutputStream()));


                        String hostMessage = br.readLine();
                        while (!hostMessage.equalsIgnoreCase(Command.DISCONNECT_MAIN.name())){
                            handle(hostMessage, writer);
                            hostMessage = br.readLine();
                        }


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

    private void handle(String hostMessage, PrintWriter writer) {
        String[] parts = hostMessage.split(" ");
        String commandFromHost = parts[0];
        System.out.println(commandFromHost);
        Optional<Command> optionalCommand = Arrays.stream(Command.values()).filter(command -> command.name().equalsIgnoreCase(commandFromHost)).findAny();
        if(optionalCommand.isPresent()){
            Command command = optionalCommand.get();
            switch (command){
                case CREATE_SERVER:

                    Server server = createGameServer();
                    InetAddress serverAddress = server.getServerSocket().getInetAddress();
                    int port = server.getServerSocket().getLocalPort();
                    writer.println("ip:" + serverAddress.getHostAddress());
                    writer.println("port:" + port);
                    writer.println("end");
                    writer.flush();
                    System.out.println("sent to host: " + "ip:" + serverAddress.getHostAddress() + "port:" + port);
                    break;

                case SAVE_SERVER_CONFIGURATION:

                    String data = parts[1];
                    GameConfig gameConfig = new GameConfig();
                    gameServerConfigs.add(gameConfig);

                    String[] lines = data.split(",");
                    for(String line: lines){
                        String[] lineParts = line.split(":");
                        String attribute = lineParts[0];
                        String value = lineParts[1];
                    }

            }
        }
    }

    private void sendBaseServerInfo(GameConfig gameConfig, PrintWriter writer) {
        //send ip, port
        System.out.println("sending ip, port to Host client!!");
        writer.println("ip:"+gameConfig.getAddress().getHostAddress());
        writer.println("port:"+gameConfig.getPort());
        writer.println("end");
        writer.flush();

    }

    private Server createGameServer() {
        Server server = null;
        try {
            System.out.println(gameServerConfigs.size());
            server = new Server(PORT + 1 + gameServerConfigs.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return server;
    }
}
