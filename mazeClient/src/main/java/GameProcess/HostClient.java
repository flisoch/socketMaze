package GameProcess;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Properties;

public class HostClient {
    public static void main(String[] args) {
        HostClient hostClient = new HostClient();
        try {
            hostClient.connectToMainServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connectToMainServer() throws IOException {
        Properties properties = new Properties();
        int port;
        String ip;

        try {
            properties.load(new FileInputStream("mazeClient/src/main/resources/application.properties"));
            port = Integer.parseInt(properties.getProperty("mainServer.port"));
            ip = properties.getProperty("mainServer.ip");
            InetAddress inetAddress = InetAddress.getByName(ip);

            Socket s = new Socket(inetAddress, port);

            PrintWriter writer = new PrintWriter(s.getOutputStream());
            //send config data, such as serverName, maxPlayrs, password and etc
            writer.println("password:123456");
            writer.flush();

            String gameServerIp = "IP TUT DOLJEN IT";
            int gameServerPort = 0;
            String status = "ERROR";

            BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
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
                    case "status":
                        status = data;
                        break;
                    case "end":
                        continue;
                    default:
                        System.out.println("command is not defined!");
                }
                line = reader.readLine();
            }
            InetAddress gameInetAddress = InetAddress.getByName(gameServerIp);
            System.out.println(gameInetAddress);
            System.out.println("status: " + status + " ServerIp: " + gameServerIp + " port: " + gameServerPort);

            Socket gameSocket = new Socket(gameServerIp, gameServerPort);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
