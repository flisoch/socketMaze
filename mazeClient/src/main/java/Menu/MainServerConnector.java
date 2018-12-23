package Menu;

import protocol.Protocol;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Properties;

public class MainServerConnector {
    private static Socket socket;

    public static Socket getSocket(){
        if(socket == null){
            Properties properties = new Properties();
            try {
                properties.load(new FileInputStream("mazeClient/src/main/resources/application.properties"));
                int port = Integer.parseInt(properties.getProperty("mainServer.port"));
                String ip = properties.getProperty("mainServer.ip");
                InetAddress inetAddress = InetAddress.getByName(ip);
                socket = new Socket(inetAddress, port);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return socket;
    }


    public static void sendRequest(String message) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(getSocket().getOutputStream());
            writer.println(message);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getResponse() {
        StringBuilder builder = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(getSocket().getInputStream()));
            String line = reader.readLine();
            while (!line.equals(Protocol.Command.END_MESSAGE.name())){
                builder.append(line).append("\n");
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }
}
