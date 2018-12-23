package app;


import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class MainServer {

    private static final int PORT = 1234;
    private static ServerSocket serverSocket;
    private static List<GameConfig> gameServerConfigs;

    enum Command {CREATE_SERVER, SAVE_SERVER_CONFIGURATION, DISCONNECT_MAIN, GET_SERVERS, CHECK_PASSWORD, END_MESSAGE}

    ;

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
                Socket client = serverSocket.accept();

                Thread thread = new Thread(() -> {

                    try {
                        BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
                        PrintWriter writer = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));
                        String hostMessage = br.readLine();
                        while (!hostMessage.equalsIgnoreCase(Command.DISCONNECT_MAIN.name())) {
                            handle(hostMessage, writer);
                            hostMessage = br.readLine();
                        }
                        //todo: close games/servers/idk what after Disconnecting


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

    private void handle(String clientMessage, PrintWriter writer) {
        String[] parts = clientMessage.split(" ");
        String commandFromHost = parts[0];
        String data;
        System.out.println(commandFromHost);
        Optional<Command> optionalCommand = Arrays.stream(Command.values()).filter(command -> command.name().equalsIgnoreCase(commandFromHost)).findAny();
        if (optionalCommand.isPresent()) {
            Command command = optionalCommand.get();
            switch (command) {
                case CREATE_SERVER:

                    Server server = createGameServer();
                    InetAddress serverAddress = server.getServerSocket().getInetAddress();
                    int port = server.getServerSocket().getLocalPort();
                    writer.println("ip:" + serverAddress.getHostAddress());
                    writer.println("port:" + port);
                    writer.println(Command.END_MESSAGE);
                    writer.flush();
                    System.out.println("sent to host: " + "ip:" + serverAddress.getHostAddress() + " port:" + port);
                    break;

                case SAVE_SERVER_CONFIGURATION:
                    data = parts[1];
                    GameConfig gameConfig = new GameConfig();

                    String[] lines = data.split(",");
                    for (String line : lines) {
                        String[] lineParts = line.split(":");
                        String attribute = lineParts[0];
                        String value = lineParts[1];
                        switch (attribute) {
                            case "ipAddress":
                                try {
                                    gameConfig.setAddress(InetAddress.getByName(value));
                                } catch (UnknownHostException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case "port":
                                gameConfig.setPort(Integer.parseInt(value));
                                break;
                            case "name":
                                gameConfig.setName(value);
                                break;
                            case "password":
                                gameConfig.setServerPassword(value);
                                break;
                            case "maxPlayers":
                                gameConfig.setMaxPlayers(Integer.parseInt(value));
                                break;
                            case "playersCount":
                                gameConfig.setPlayersCount(Integer.parseInt(value));
                                break;
                            case "mazeHeight":
                                gameConfig.setMazeHeight(Integer.parseInt(value));
                                break;
                            case "end":
                                continue;
                            default:
                                System.out.println("recieved from host server attribute is not defined!");
                        }
                    }
                    gameServerConfigs.add(gameConfig);
                    break;

                case GET_SERVERS:
                    gameServerConfigs.forEach(game -> {
                        writer.println(
                                "ip:" + game.getAddress().getHostAddress() + "," +
                                        "port:" + game.getPort() + "," +
                                        "name:" + game.getName() + "," +
                                        "maxPlayers:" + game.getMaxPlayers() + "," +
                                        "playersCount:" + game.getPlayersCount() + "," +
                                        "mazeHeight:" + game.getMazeHeight()
                        );
                        writer.println(Command.END_MESSAGE);
                        writer.flush();
                    });
                    break;

                case CHECK_PASSWORD:
                    data = parts[1];
                    String[] dataLines = data.split(",");
                    InetAddress serverIp = null;
                    int serverPort = -1;
                    String password = "";
                    boolean passwordIsCorrect = false;
                    for (String line : dataLines) {
                        String[] lineParts = line.split(":");
                        String attribute = lineParts[0];
                        String value = lineParts[1];
                        switch (attribute) {
                            case "serverIp":
                                try {
                                    serverIp = InetAddress.getByName(value);
                                } catch (UnknownHostException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case "serverPort":
                                serverPort = Integer.parseInt(value);
                                break;
                            case "password":
                                password = value;
                                break;
                        }
                    }

                    for(GameConfig gameConfig1: gameServerConfigs){
                        if(gameConfig1.getAddress().equals(serverIp) &&
                                gameConfig1.getPort() == serverPort &&
                                gameConfig1.getServerPassword().equals(password)){
                            passwordIsCorrect = true;
                            break;
                        }
                    }
                    if(passwordIsCorrect){
                        writer.println("OK");
                    }
                    else {
                        writer.println("INVALID PASSWORD");
                    }
                    writer.println(Command.END_MESSAGE);
                    writer.flush();

            }
        }
    }


    private Server createGameServer() {
        Server server = null;
        try {
            server = new Server(PORT + 1 + gameServerConfigs.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return server;
    }
}
