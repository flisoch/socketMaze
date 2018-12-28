package Menu;

import protocol.Protocol;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class ServerList {

//    private static List<Server> servers;

    public static List<Server> getServers(){

        return getServersFromServer();
    }

    private static List<Server> getServersFromServer() {
        sendRequestToGetServers();
        return recieveListFromServer();
    }

    private static List<Server> recieveListFromServer() {

        List<Server> servers = new ArrayList<>();
        String response = MainServerConnector.getResponse();
        if(response.equals("NULL")){
            return servers;
        }
        else {
            String[] lines = response.split("\n");
            for(String line: lines){

                Server server = Server.builder().build();
                String[] attributes = line.split(",");

                for (String attribute:attributes){
                    String[] parts = attribute.split(":");
                    String field = parts[0];
                    String data = parts[1];
                    switch (field) {
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
                        case "name":
                            server.setName(data);
                            break;
                        case "maxPlayers":
                            server.setMaxPlayers(Integer.parseInt(data));
                            break;
                        case "playersCount":
                            server.setPlayersCount(Integer.parseInt(data));
                            break;
                    }
                }
                servers.add(server);
            }
        }


        return servers;

    }


    private static void sendRequestToGetServers() {
        MainServerConnector.sendRequest(Protocol.Command.GET_SERVERS.name());
    }

}
