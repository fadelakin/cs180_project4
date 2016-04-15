import cs180.net.ServerSocket;
import cs180.net.Socket;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * CS 180 - Project 4 - Email
 *
 * @author Temidayo Adelakin, tadelaki@purdue.edu
 * @version April 14, 2016
 * @lab L11
 */
public class OnlineEmailServer extends EmailServer implements Runnable {

    private ServerSocket serverSocket;
    private Socket client;

    public OnlineEmailServer(String filename, int port) throws IOException {
        serverSocket = new ServerSocket(port);
        serverSocket.setReuseAddress(true);

        client = serverSocket.accept();
        run();
    }

    @Override
    public void run() {
        try {
            while (!client.isClosed()) {
                processClient(client);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void processClient(Socket client) throws IOException {
        new Thread().start();
        Scanner in = new Scanner(client.getInputStream());
        Pattern pattern = Pattern.compile("(\r\n){2,}");
        in.useDelimiter(pattern);

        PrintWriter out = new PrintWriter(client.getOutputStream());

        while (in.hasNextLine()) {
            String line = in.nextLine();
            //line = line.concat("\r\n");
            System.out.println(line);
            String response = parseRequest(line);
            System.out.printf(response);
            out.printf(response);
            out.flush();

        }

        // input done, close connections...
        out.close();
        in.close();

    }

    public void stop() {
        try {
            serverSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        try {
            new OnlineEmailServer("test_student.csv", 22334).run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
