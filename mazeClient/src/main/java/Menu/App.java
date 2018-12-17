package Menu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class App {

    public static void main(String[] args) {
        App app = new App();
        app.run();

    }

    BufferedReader reader;

    public App() {
        this.reader = new BufferedReader(new InputStreamReader(System.in));
    }

    private void run() {

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
                        break;
                    case "4":
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

    private void handleServerChooseInput() {
        List<Server> servers = ServerList.getServers();
        try {
            String choice = reader.readLine();
            Server server = servers.get(Integer.parseInt(choice));
            String clientPassword = askPassword();
            if(checkPassword(server.getPassword(), clientPassword)){
                connectToServer(server);
            }
        } catch (IOException e) {
            System.out.println("incorrect input while choosing server");
            e.printStackTrace();
        }

    }

    private void connectToServer(Server server) {
        //todo: connect and start the game
    }

    private boolean checkPassword(String password, String clientPassword) {
        return password.equals(clientPassword);
    }

    private String askPassword() {
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
    }

    private void showServers() {
        List<Server> servers = ServerList.getServers();
        servers.forEach(System.out::println);
    }

    private void startNewGame() {

    }

    private void showMainMenu() {
        System.out.println("1.new game");
        System.out.println("2.servers");
        System.out.println("3.settings");
        System.out.println("4.exit");
    }
}
