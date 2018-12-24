package app;

import java.net.Socket;

public class Connection  {
    Socket socket;
    private String[][] mazeTable;

    public Connection(Socket socket) {
        this.socket = socket;
    }

    public String[][] getMazeTable() {
        return mazeTable;
    }

    public void setMazeTable(String[][] mazeTable) {
        this.mazeTable = mazeTable;
    }
}
