import cs180.net.ServerSocket;
import cs180.net.Socket;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Scanner;

/**
 * CS 180 - Project 4 - Email
 *
 * @author Temidayo Adelakin, tadelaki@purdue.edu
 * @version April 14, 2016
 * @lab L11
 */
public class OnlineEmailServer extends EmailServer {

    private int port;
    private ServerSocket serverSocket;

    public OnlineEmailServer(String filename, int port) throws IOException {
        this.port = port;
        serverSocket = new ServerSocket(port);
        serverSocket.setReuseAddress(true);

    }

    @Override
    public void run() {

        Socket socket;
        Scanner in = null;
        ObjectOutputStream out = null;
        String line;

        try {
            while (true) {
                socket = serverSocket.accept();
                try {
                    in = new Scanner(socket.getInputStream());
                    out = new ObjectOutputStream(socket.getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (in != null) in.close();
                    if (out != null) out.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void processClient(Socket client) throws IOException {
        // Handle processing a client's request (input and output)
    }

    public void stop() {
        // TODO implement
    }
}
