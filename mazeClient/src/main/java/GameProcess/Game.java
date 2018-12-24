package GameProcess;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;


public class Game {
    InetAddress gameServerIp;
    int gameServerPort;

    public Game(InetAddress gameServerIp, int gameServerPort, boolean isHostRun, int mazeHeight) {
        this.gameServerIp = gameServerIp;
        this.gameServerPort = gameServerPort;
        this.isHostRun = isHostRun;
        this.mazeHeight = mazeHeight;
    }
    private boolean isHostRun;
    private int mazeHeight;

    public void start() throws IOException {

        String hostString = isHostRun?"True":"False";
        String command = "--command=python mazeClient/src/main/python/game.py" + " "
                + gameServerIp.getHostAddress() + " " + gameServerPort + " " + hostString + " " + mazeHeight;
        ProcessBuilder procBuilder = new ProcessBuilder("lxterminal", command);

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
