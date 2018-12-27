package app;

import protocol.Command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server implements Runnable {

    private ServerSocket serverSocket;
    private List<Connection> connections;
    private Thread gameServerThread;
    private String[][] table;

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public  Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        connections = new ArrayList<>();
        gameServerThread = new Thread(this::run);
        gameServerThread.start();
    }

    @Override
    public void run() {
        //todo:kill server somehow
        AtomicBoolean gameFinished = new AtomicBoolean(false);
        while (!gameFinished.get()) {
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
                            if (s.equals(Command.KILL_SERVER.name())) {
                                gameFinished.set(true);
                                break;
                            }
                            onNext(s, writer, connection);
                            s = br.readLine();
                        }
                        System.out.println("SERVER KILLED " + serverSocket.getInetAddress().getHostAddress() + " "
                                + serverSocket.getLocalPort());
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

    private void onNext(String message, PrintWriter writer, Connection connection) {

        String[] parts = message.split(" ", 2);
        String commandFromClient = parts[0];
        String data;
        long playerId;

        System.out.println(commandFromClient);
        Optional<Command> optionalCommand = Arrays.stream(Command.values()).filter(command -> command.name().equalsIgnoreCase(commandFromClient)).findAny();
        if (optionalCommand.isPresent()) {
            Command command = optionalCommand.get();
            switch (command) {
                case SAVE_TABLE_MAZE:
                    data = parts[1];
                    System.out.println(data);
                    String table = data;
                    String[] lines = table.split(":")[1].split("//");
                    int n = lines.length - 1;
                    this.table = new String[n][n];


                    for (int i = 0; i < n; i++) {
                        String[] items = lines[i].split(",");
                        for (int j = 0; j < items.length; j++) {
                            this.table[i][j] = items[j];
                        }
                    }
                    break;
                case GET_TABLE_MAZE:
                    StringBuilder builder = new StringBuilder();
                    for (String[] row : this.table) {
                        for (String item : row) {
                            builder.append(item).append(",");
                        }
                        builder.setLength(builder.length() - 1);
                        builder.append("//");
                    }
                    builder.setLength(builder.length() - 3);
                    //todo:check if works without \n
                    builder.append('\n');
                    System.out.println(builder);
                    writer.println(builder.toString());
                    writer.flush();
                    break;
                case SEND_TIME:
                    data = parts[1];
                    String[] timeAndPlayerId = data.split(",");
                    double finishTime = Double.parseDouble(timeAndPlayerId[0].split(":")[1]);
                    playerId = Long.parseLong(timeAndPlayerId[1].split(":")[1]);
                    connection = connections.stream()
                            .filter(connection3 -> connection3.getPlayerId() == playerId)
                            .findAny()
                            .orElse(null);
                    connection.setFinishTime(finishTime);
                    break;
                case READY:
                    data = parts[1];
                    playerId = Long.parseLong(data.split(":")[1]);
                    connection = connections.stream()
                            .filter(connection4 -> connection4.getPlayerId() == playerId)
                            .findAny()
                            .orElse(null);
                    connection.isReady = true;


                    boolean ready = false;
                    while (!ready) {
                        ready = connections.stream()
                                .filter(Connection::isReady)
                                .count() == connections.size();
                        if(!ready){
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    writer.println("READY");
                    writer.flush();
                    break;

                case GET_RESULTS:
                    connections.sort(Comparator.comparingDouble(Connection::getFinishTime));
                    connections.forEach(connection1 -> writer.println(
                            "player:" + connection1.getPlayerName() + "," +
                                    "time:" + connection1.getFinishTime()
                    ));
                    writer.flush();
                    break;
                case CREATE_PLAYER_ID:
                    data = parts[1];
                    String playerName = data.split(":")[1];
                    System.out.println(playerName);
                    connection.setPlayerName(playerName);
                    connection.setPlayerId(System.currentTimeMillis());
                    System.out.println(connection.getPlayerId());
                    writer.println(connection.getPlayerId());
                    writer.flush();
            }
        }
    }
}
