package GameProcess;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;


public class Game {
    InetAddress gameServerIp;
    int gameServerPort;

    public Game(InetAddress gameServerIp, int gameServerPort) {
        this.gameServerIp = gameServerIp;
        this.gameServerPort = gameServerPort;
    }

    public void start() throws IOException {
        ProcessBuilder procBuilder = new ProcessBuilder("lxterminal", "--command=python mazeClient/src/main/python/game.py"
                + " " + gameServerIp.getHostAddress() + " " + gameServerPort);

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
