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
public class OnlineEmailServer extends EmailServer {

    private ServerSocket serverSocket;
    private Socket client;

    public OnlineEmailServer(String filename, int port) throws IOException {
        serverSocket = new ServerSocket(port);
        serverSocket.setReuseAddress(true);
    }

    @Override
    public void run() {
        try {
            while (!serverSocket.isClosed()) {
                client = serverSocket.accept();
                //client.setSoTimeout(1000);
                processClient(client);
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public void processClient(Socket client) throws IOException {
        Scanner in = new Scanner(client.getInputStream());
        Pattern pattern = Pattern.compile("(\r\n){2,}");
        in.useDelimiter(pattern);

        PrintWriter out = new PrintWriter(client.getOutputStream(), true);

        while (in.hasNextLine()) {
            String line = in.nextLine();
            line = line.concat("\r\n");
            String response = parseRequest(line);
            out.print(response);

        }

        // input done, close connections...
        out.close();
        in.close();

    }

    public void stop() {
        try {
            client.close();
            serverSocket.close();
            System.exit(1);
        } catch (IOException e) {
            e.getMessage();
        }
    }

    public static void main(String args[]) {
        try {
            new OnlineEmailServer("test_student.csv", 22334).run();
        } catch (IOException e) {
            e.getMessage();
        }
    }
}
