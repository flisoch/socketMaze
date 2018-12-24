package app;

import designParts.Observer;
import protocol.Command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Server implements Runnable {

    private ServerSocket serverSocket;
    private List<Connection> connections;
    private Thread gameServerThread;

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        connections = new ArrayList<>();
        gameServerThread = new Thread(this::run);
        gameServerThread.start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();

                Connection connection = new Connection(socket);
                System.out.println("ACCEPTED IN GAMESOCKET");
                connections.add(connection);
                Thread thread = new Thread(() -> {
                    try {
                        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter writer = new PrintWriter(socket.getOutputStream());
                        String s = br.readLine();
                        while (s != null) {
                            onNext(s, writer);
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

    private void onNext(String message, PrintWriter writer) {

        String[] parts = message.split(" ", 2);
        String commandFromClient = parts[0];
        String data;

        System.out.println(commandFromClient);
        Optional<Command> optionalCommand = Arrays.stream(Command.values()).filter(command -> command.name().equalsIgnoreCase(commandFromClient)).findAny();
        if (optionalCommand.isPresent()) {
            Command command = optionalCommand.get();
            switch (command) {
                case SAVE_TABLE_MAZE:
                    data = parts[1];
                    System.out.println(data);
                    String[] lineParts = data.split(";");
                    String port = lineParts[0];
                    String table = lineParts[1];
                    int host_port = Integer.parseInt(port.split(":")[1]);
                    String[] lines = table.split(":")[1].split("//");
                    int n = lines.length - 1;
                    String[][] mazeTable = new String[n][n];
                    Connection connection = null;
                    for (Connection connection1 : connections) {
                        if (connection1.socket.getLocalPort() == host_port) {
                            connection = connection1;
                        }
                    }

                    for (int i = 0; i < n; i++) {
                        String[] items = lines[i].split(",");
                        for (int j = 0; j < items.length; j++) {
                            mazeTable[i][j] = items[j];
                        }
                    }
                    if (connection != null) {
                        connection.setMazeTable(mazeTable);
                    }
                    break;
                case GET_TABLE_MAZE:
                    data = parts[1];
                    String[] port1 = data.split(":");
                    int host_port1 = Integer.parseInt(port1[1]);
                    Connection connection1 = connections.stream()
                            .filter(connection2 -> connection2.socket.getLocalPort() == host_port1)
                            .findAny()
                            .orElse(null);
                    String[][] tableMaze1 = connection1.getMazeTable();

                    StringBuilder builder = new StringBuilder();
                    for (String[] row : tableMaze1) {
                        for (String item : row) {
                            builder.append(item).append(",");
                        }
                        builder.setLength(builder.length() - 1);
                        builder.append("//");
                    }
                    builder.setLength(builder.length() - 3);
                    builder.append('\n');
                    System.out.println(builder);
                    writer.println(builder.toString());
                    writer.flush();
                    break;
            }
        }
        //send message to all except source

        /*for (Socket s : sockets) {
            try {
                PrintWriter pw = new PrintWriter(s.getOutputStream());
                pw.println(message);
                pw.flush();
//                pw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
    }
}
