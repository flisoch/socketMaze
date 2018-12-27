package GameProcess;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;


public class Game {
    InetAddress gameServerIp;
    int gameServerPort;
    private boolean isHostRun;
    private int mazeHeight;
    Client client;

    public Game(InetAddress gameServerIp, int gameServerPort, boolean isHostRun, int mazeHeight, Client client) {
        this.gameServerIp = gameServerIp;
        this.gameServerPort = gameServerPort;
        this.isHostRun = isHostRun;
        this.mazeHeight = mazeHeight;
        this.client = client;
    }

    public void start() throws IOException {
        String osName = System.getProperty("os.name");
        String terminalName = "";
        String terminalCommandParameter = "";
        if(osName.contains("Linux")){
            terminalName = "lxterminal";
            terminalCommandParameter = "--command";
        }
        else if(osName.contains("Mac")){
            terminalName = "Termnal";
        }
        else {
            terminalName = "cmd";
            terminalCommandParameter = " -command ";
        }

        String hostString = isHostRun?"True":"False";
        String command = "python game.py" + " "
                + gameServerIp.getHostAddress() + " " + gameServerPort + " " + hostString + " " + mazeHeight + " " + client.getPlayerName();
        System.out.println(terminalName + terminalCommandParameter + command);
        ProcessBuilder procBuilder = new ProcessBuilder(terminalName, terminalCommandParameter, command);

        // перенаправляем стандартный поток ошибок на
        // стандартный вывод
        procBuilder.redirectErrorStream(true);

        // запуск программы
        Process process = procBuilder.start();

        // читаем стандартный поток вывода
        // и выводим на экран
        InputStream stdout = process.getInputStream();
        InputStreamReader isrStdout = new InputStreamReader(stdout);
        BufferedReader brStdout = new BufferedReader(isrStdout);


        PrintWriter writer = new PrintWriter(process.getOutputStream());
        String line = null;
        while((line = brStdout.readLine()) != null) {
            System.out.println(line);
        }

        // ждем пока завершится вызванная программа
        // и сохраняем код, с которым она завершилась в
        // в переменную exitVal
        try {
            int exitVal = process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
