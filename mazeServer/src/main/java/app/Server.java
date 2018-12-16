package app;

import designParts.Observer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server implements Observer {

    private ServerSocket serverSocket;
    private List<Socket> sockets;


    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        sockets = new ArrayList<>();
    }

    void run() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                sockets.add(socket);
                Thread thread = new Thread(() -> {
                    try {
                        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        String s = br.readLine();
                        while (s != null) {
                            onNext(s);
                            s = br.readLine();
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

    public void onNext(String message) {
        //send message to all except source

        for (Socket s : sockets) {
            try {
                PrintWriter pw = new PrintWriter(s.getOutputStream());
                pw.println(message);
                pw.flush();
//                pw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
