package app;

import designParts.Observer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MainServer {

    private static ServerSocket serverSocket;
    private static List<Socket> hosts;

    public static void main(String[] args) {
        MainServer server = new MainServer();

    }

    public MainServer() {
        try {
            serverSocket = new ServerSocket(1234);
            hosts = new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void run() {

        while (true) {
            try {
                Socket host = serverSocket.accept();
                hosts.add(host);
                Thread thread = new Thread(() -> {
                    boolean configured = false;
                    try {
                        BufferedReader br = new BufferedReader(new InputStreamReader(host.getInputStream()));
                        String s = br.readLine();
                        String hostMessage = br.readLine();
                        while (!configured){
                            configured = handleConfigMessage(hostMessage);
                        }
                        createGameServer();
                        PrintWriter writer = new PrintWriter(new OutputStreamWriter(host.getOutputStream()));
                        sendAllInfoToHost(writer);

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

    private void sendAllInfoToHost(PrintWriter writer) {
        //send ip, port, serverName, password
    }

    private void createGameServer() {
        try {
            Server server = new Server(1235 + hosts.size());
            server.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private boolean handleConfigMessage(String hostMessage) {
        //get some settings from hostClient, configure some settings
        return false;
    }

}
