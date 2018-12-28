package app;


import lombok.Data;
import protocol.Command;

import java.io.*;
import java.net.*;
import java.util.*;

public class MainServer {

    private static final int PORT = 1234;
    public static String serverGlobalIp = configureIp();
    private static int counter = 1;
    private static ServerSocket serverSocket;
    private static List<GameConfig> gameServerConfigs;

    public static void main(String[] args) {
        MainServer server = new MainServer();
        server.run();

    }

    private static String configureIp(){
        String ip;
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                // filters out 127.0.0.1 and inactive interfaces
                if (iface.isLoopback() || !iface.isUp())
                    continue;

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while(addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    ip = addr.getHostAddress();
                    if(ip.split("\\.").length == 4){
                        System.out.println(ip);
                        return ip;
                    }
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        return "0.0.0.0";
    }

    public static int getPORT() {
        return PORT;
    }

    public static ServerSocket getServerSocket() {
        return serverSocket;
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
                    writer.println("ip:" + serverGlobalIp);
                    writer.println("port:" + port);
                    writer.println(Command.END_MESSAGE);
                    writer.flush();
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
                    System.out.println(gameServerConfigs.add(gameConfig));
                    break;

                case GET_SERVERS:
                    System.out.println("servers count: " + gameServerConfigs.size());
                    if(gameServerConfigs.size() == 0){
                        writer.write("NULL");
                    }
                    else {
                        gameServerConfigs.forEach(game -> writer.println(
                                "ip:" + game.getAddress().getHostAddress() + "," +
                                        "port:" + game.getPort() + "," +
                                        "name:" + game.getName() + "," +
                                        "maxPlayers:" + game.getMaxPlayers() + "," +
                                        "playersCount:" + game.getPlayersCount() + "," +
                                        "mazeHeight:" + game.getMazeHeight()
                        ));
                    }
                    writer.println(Command.END_MESSAGE);
                    writer.flush();
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
                    String status;
                    if(passwordIsCorrect){
                        status = "OK";
                    }
                    else {
                        status = "INVALID PASSWORD";
                    }
                    System.out.println(status);
                    writer.println(status);
                    writer.println(Command.END_MESSAGE);
                    writer.flush();
                    break;
                case KILL_SERVER:
                    data = parts[1];
                    String finalData = data;
                    boolean killed = gameServerConfigs.remove(gameServerConfigs.stream().
                            filter(server1 -> server1.getPort() == Integer.parseInt(finalData))
                            .findAny()
                            .get());
                    System.out.println("KILLED :" + killed);
                    break;
                case GET_SERVER_CONFIG:
                    data = parts[1];
                    String port1 = data;

                    GameConfig gameConfig2 = gameServerConfigs.stream()
                            .filter(gameConfig1 -> gameConfig1.getPort() == Integer.parseInt(port1))
                            .findAny()
                            .get();
                    System.out.println(gameServerConfigs.size());
                    writer.println("maxPlayers:" + gameConfig2.getMaxPlayers());
                    writer.flush();
                    break;
            }
        }
    }


    private Server createGameServer() {
        Server server = null;
        try {
            server = new Server(PORT + counter++);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return server;
    }
}
