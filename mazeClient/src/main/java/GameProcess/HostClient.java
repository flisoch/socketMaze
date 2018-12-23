package GameProcess;

import Menu.MainServerConnector;
import Menu.Server;
import protocol.Protocol;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;

public class HostClient {

    public Server connectToMainServer()  {

        Server server;
        //todo:  prompt to configure automatically from default properties or by host

        sendRequestToCreateBaseServer();
        server = getBaseServerFromMain();
        configureServer(server);
        sendConfiguredServerInfo(server);

        return server;
    }

    private void sendRequestToCreateBaseServer() {
        MainServerConnector.sendRequest(Protocol.Command.CREATE_SERVER.name());
    }

    private Server getBaseServerFromMain() {

        Server server = Server.builder().build();

        String response = MainServerConnector.getResponse();
        String[] lines = response.split("\n");
        for(String line: lines){

            String[] parts = line.split(":");
            String command = parts[0];
            String data = parts[1];
            switch (command) {
                case "ip":
                    try {
                        server.setAddress(InetAddress.getByName(data));
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    break;
                case "port":
                    server.setPort(Integer.parseInt(data));
                    break;
                case "end":
                    continue;
                default:
                    System.out.println("recieved server attribute is not defined!");
            }
        }
        return server;
    }

    private void configureServer(Server server) {

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("enter server name: ");
            String answer = reader.readLine();
            String gameServerName = answer;

            System.out.println("enter password: ");
            answer = reader.readLine();
            String password = answer;

            System.out.println("enter maze height(width equals height): ");
            answer = reader.readLine();
            int height = Integer.parseInt(answer);

            System.out.println("enter maz players count: ");
            answer = reader.readLine();
            int maxPlayers = Integer.parseInt(answer);

            server.setMazeHeight(height);
            server.setMaxPlayers(maxPlayers);
            server.setName(gameServerName);
            server.setPassword(password);
            server.setPlayersCount(1);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendConfiguredServerInfo(Server server) {
        Protocol.Command command = Protocol.Command.SAVE_SERVER_CONFIGURATION;
        StringBuilder builder = new StringBuilder();
        builder.append(command);
        builder.append(" ipAddress:").append(server.getAddress().getHostAddress());
        builder.append(",port:").append(server.getPort());
        builder.append(",name:").append(server.getName());
        builder.append(",password:").append(server.getPassword());
        builder.append(",maxPlayers:").append(server.getMaxPlayers());
        builder.append(",mazeHeight:").append(server.getMazeHeight());
        builder.append(",playersCount:").append(server.getPlayersCount());

        MainServerConnector.sendRequest(builder.toString());
    }
}
