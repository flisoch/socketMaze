package GameProcess;

import Menu.Server;
import protocol.Protocol;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Properties;

public class HostClient {

    Socket socket;

    public Server connectToMainServer()  {
        Properties properties = new Properties();
        int port;
        String ip;
        Server server = null;
        try {
            properties.load(new FileInputStream("mazeClient/src/main/resources/application.properties"));
            port = Integer.parseInt(properties.getProperty("mainServer.port"));
            ip = properties.getProperty("mainServer.ip");
            InetAddress inetAddress = InetAddress.getByName(ip);

            socket = new Socket(inetAddress, port);

            //todo:  prompt to configure automatically from default properties or by host

            sendRequestToCreateBaseServer(socket);
            server = getBaseServerFromMain();
            configureServer(server);
            sendConfiguredServerInfo(server);

        } catch (IOException e) {
            System.out.println("couldn't get Main server connection info from client files");
            e.printStackTrace();
        }
        return server;
    }

    private void sendRequestToCreateBaseServer(Socket socket) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(socket.getOutputStream());
            writer.println(Protocol.Command.CREATE_SERVER + " data");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Server getBaseServerFromMain() {
        Server server = null;
        String gameServerIp = "";
        int gameServerPort = 0;

        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line = reader.readLine();
            while (!line.equals("end")) {
                String[] parts = line.split(":");
                String command = parts[0];
                String data = parts[1];
                switch (command) {
                    case "ip":
                        gameServerIp = data;
                        break;
                    case "port":
                        gameServerPort = Integer.parseInt(data);
                        break;
                    case "end":
                        continue;
                    default:
                        System.out.println("recieved server attribute is not defined!");
                }
                line = reader.readLine();
            }

            System.out.println("recieved from main server: your ServerIp: " + gameServerIp + ", port: " + gameServerPort);

            server = Server.builder()
                    .address(InetAddress.getByName(gameServerIp))
                    .port(gameServerPort)
                    .build();

        } catch (IOException e) {
            e.printStackTrace();
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
            password = "123456";

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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendConfiguredServerInfo(Server server) {
        Protocol.Command command = Protocol.Command.SAVE_SERVER_CONFIGURATION;
        try {
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            writer.println(
                    command
                            + " address:" + server.getAddress()
                            + ",port:" + server.getPort()
                            + ",name:" + server.getName()
                            + ",password:" + server.getPassword()
                            + ",maxPlayers:" + server.getMaxPlayers()
                            + ",mazeHeight:" + server.getMazeHeight()
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
