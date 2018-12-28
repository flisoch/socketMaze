package Menu;

import GameProcess.Client;
import GameProcess.Game;
import GameProcess.HostClient;
import protocol.Protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class App {

    public static void main(String[] args) {
        App app = new App();
        app.run();

    }
    private BufferedReader reader;
    private Client client;

    public App() {
        this.reader = new BufferedReader(new InputStreamReader(System.in));
    }

    private void run() {

        MainServerConnector.configure();
        System.out.println("enter your username: ");
        String name = "unnamed";
        try {
            name = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        client = new Client(name);

        try {
            String choice = "";
            while (!choice.equals("4")) {

                clearDisplay();
                showMainMenu();

                choice = reader.readLine();
                switch (choice) {
                    case "1":
                        startNewGame();
                        break;
                    case "2":
                        showServers();
                        handleServerChooseInput();
                        break;
                    case "3":
                        showSettings();
                        handleSettingsChooseInput();
                        break;
                    case "4":
                        disconnectFromMainServer(Protocol.Command.DISCONNECT_MAIN.name());
                        break;
                    default:
                        clearDisplay();
                        System.out.println("incorrect input");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void disconnectFromMainServer(String message) {
        MainServerConnector.sendRequest(message);
    }

    private void handleSettingsChooseInput() {
        String choice;
        try {
            choice = reader.readLine();
            switch (choice) {
                case "1":
                    System.out.println(client.getPlayerName());
                    changeName();
                    break;
                case "2":
                    break;
                default:
                    System.out.println("incorrect input");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void changeName() {

        System.out.println("type new name: ");
        try {
            String name = reader.readLine();
            client.setPlayerName(name);
        } catch (IOException e) {
            System.out.println("name hasn't been read succesfully while changing the name");
            e.printStackTrace();
        }

    }

    private void handleServerChooseInput() {
        List<Server> servers = ServerList.getServers();
        if(servers.size() == 0){
            return;
        }
        try {
            System.out.println("choose a server: ");
            String choice = reader.readLine();
            Server server = servers.get(Integer.parseInt(choice) - 1);
            String clientPassword = askPassword();
            if(passwordIsCorrect(server, clientPassword)){
                connectToServer(server, false);
            }
        } catch (IOException e) {
            System.out.println("incorrect input while choosing server");
            e.printStackTrace();
        }

    }

    private boolean passwordIsCorrect(Server server, String clientPassword) {
        boolean isCorrect = false;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Protocol.Command.CHECK_PASSWORD.name());
        stringBuilder.append(" ").append("serverIp:").append(server.getAddress().getHostAddress());
        stringBuilder.append(",").append("serverPort:").append(server.getPort());
        stringBuilder.append(",").append("password:").append(clientPassword);

        MainServerConnector.sendRequest(stringBuilder.toString());
        String response = MainServerConnector.getResponse();
        System.out.println(response);
        if(response.equals("OK\n")){
            isCorrect = true;
        }
        return isCorrect;
    }

    private void connectToServer(Server server, boolean isHostRun) {
        Game game = new Game(server.getAddress(),server.getPort(), isHostRun, server.getMazeHeight(), client);
        try {
            game.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String askPassword() {
        System.out.println("enter a password: ");
        try {
            return reader.readLine();
        } catch (IOException e) {
            System.out.println("server password hasn't been acquired from client successfully");
            e.printStackTrace();
        }
        return null;
    }

    private void clearDisplay() {
        System.out.println("\033[H\033[2J");
        System.out.flush();
    }

    private void showSettings() {
        System.out.println("1. change name");
        System.out.println("2. exit");
    }

    private void showServers() {
        List<Server> servers = ServerList.getServers();
        if(servers.size() == 0){
            System.out.println("no servers created yet");
        }
        servers.forEach(System.out::println);
    }

    private void startNewGame() {
        HostClient hostClient = new HostClient(client);
        Server server = hostClient.connectToMainServer();
        connectToServer(server, true);
    }

    private void showMainMenu() {
        System.out.println("1.new game");
        System.out.println("2.servers");
        System.out.println("3.settings");
        System.out.println("4.exit");
    }
}
