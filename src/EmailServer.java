import java.util.*;

/**
 * <b> CS 180 - Project 4 - Email Server Skeleton </b>
 * <p>
 *
 * This is the skeleton code for the EmailServer Class. This is a private email
 * server for you and your friends to communicate.
 *
 * @author Temidayo Adelakin tadelaki@purdue.edu
 *
 * @lab L11
 *
 * @version April 11, 2016
 *
 */

public class EmailServer {
    // Useful constants
    public static final String FAILURE = "FAILURE";
    public static final String DELIMITER = "\t";
    public static final String SUCCESS = "SUCCESS";
    public static final String CRLF = "\r\n";

    // Used to print out extra information
    private boolean verbose = false;

    User[] users = new User[100];

    public EmailServer() {
        String[] args = {"ADD-USER", "root", "cs180"};
        this.addUser(args);
    }


    public void run() {
        Scanner in = new Scanner(System.in);

        while (true) {
            System.out.printf("Input Server Request: ");
            String command = in.nextLine();

            command = replaceEscapeChars(command);

            if (command.equalsIgnoreCase("kill") || command.equalsIgnoreCase("kill\r\n"))
                break;

            if (command.equalsIgnoreCase("verbose") || command.equalsIgnoreCase("verbose\r\n")) {
                verbose = !verbose;
                System.out.printf("VERBOSE has been turned %s.\n\n", verbose ? "on" : "off");
                continue;
            }

            String response = null;
            try {
                response = parseRequest(command);
            } catch (Exception ex) {
                response = ErrorFactory.makeErrorMessage(ErrorFactory.UNKNOWN_ERROR,
                        String.format("An exception of %s occurred.", ex.getClass().toString()));
            }

            // change the formatting of the server response so it prints well on the terminal (for testing purposes only)
            //if (response.startsWith("SUCCESS" + DELIMITER))
            //	response = response.replace(DELIMITER, NEWLINE);
            if (response.startsWith(FAILURE) && !DELIMITER.equals("\t"))
                response = response.replace(DELIMITER, "\t");

            if (verbose)
                System.out.print("response: ");
            System.out.printf("\"%s\"\n\n", response);
        }

        in.close();
    }

    /**
     * Determines which client command the request is using and calls
     * the function associated with that command.
     *
     * @param request - the full line of the client request (CRLF included)
     * @return the server response
     */
    public String parseRequest(String request) {

        String[] arr = request.split("\t");
        String[] commands = {"ADD-USER", "GET-ALL-USERS", "DELETE-USER", "SEND-EMAIL", "GET-EMAILS", "DELETE-EMAIL"};
        int index = -1;
        String temp = "";

        if (arr[arr.length - 1].contains("\r\n")) {
            int seq = arr[arr.length - 1].indexOf("\r\n");
            temp += arr[arr.length - 1].substring(0, seq);
            arr[arr.length - 1] = temp;
        }

        for (int i = 0; i < commands.length; i++) {
            if (arr[0].equals(commands[i])) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            return ErrorFactory.makeErrorMessage(-11);
        }

        if (index  == 5) {
            if (arr.length != 4) {
                return ErrorFactory.makeErrorMessage(-10);
            }


        }

        if (index == 0 || index == 1 || index == 2 || index == 3 || index == 4) {
            if (arr.length != 3) {
                return ErrorFactory.makeErrorMessage(-10);
            }

            if (arr[1].equals("root")) {
                return ErrorFactory.makeErrorMessage(-10);
            }
        }

        switch (index) {
            case 0:
                if (User.checkUser(arr[1], arr[2])) {
                    return addUser(arr);
                } else {
                    return ErrorFactory.makeErrorMessage(-23);
                }
            case 1:
                return getAllUsers(arr);
            case 2:
                return deleteUser(arr);
            case 3:
                return sendEmail(arr);
            case 4:
                return getEmails(arr);
            case 5:
                if (User.checkUser(arr[1], arr[2])) {
                    return deleteEmails(arr);
                } else {
                    return ErrorFactory.makeErrorMessage(-23);
                }
            default:
                return "Invalid";
        }
    }

    public int numUsers() {
        int counter = 0;
        for (User u: this.users) {
            if (u != null)
                counter++;
        }
        return counter;
    }

    public User findUser(String username) {
        for (User u: this.users) {
            if (u.getName().equals(username))
                return u;
        }
        return null;
    }

    public String addUser(String[] args) {
        users[numUsers()] = (new User(args[1], args[2]));
        return SUCCESS+CRLF;
    }

    public String getAllUsers(String[] args) {
        String username = args[1];
        String password = args[2];
        if (this.findUser(username) == null)
            return ErrorFactory.makeErrorMessage(-20);
        else if (this.findUser(username).checkPassword(password)) {
            String success = "SUCCESS";
            for (User u: users) {
                success = success + "\t" + u.getName();
            }
            return success + "\r\n";
        }
        else
            return "FAILURE\t-20\t" + ErrorFactory.makeErrorMessage(-20) + "\r\n";
    }

    public String deleteUser(String[] args) {
        return "";
    }

    public String sendEmail(String[] args) {
        return "";
    }

    public String getEmails(String[] args) {
        return "";
    }

    public String deleteEmails(String[] args) {
        return SUCCESS+CRLF;
    }

    /**
     * Replaces "poorly formatted" escape characters with their proper
     * values. For some terminals, when escaped characters are
     * entered, the terminal includes the "\" as a character instead
     * of entering the escape character. This function replaces the
     * incorrectly inputed characters with their proper escaped
     * characters.
     *
     * @param str - the string to be edited
     * @return the properly escaped string
     */
    private static String replaceEscapeChars(String str) {
        str = str.replace("\\r\\n", "\r\n"); // may not be necessary, but just in case
        str = str.replace("\\r", "\r");
        str = str.replace("\\n", "\n");
        str = str.replace("\\t", "\t");
        str = str.replace("\\f", "\f");

        return str;
    }

    /**
     * This main method is for testing purposes only.
     * @param args - the command line arguments
     */
    public static void main(String[] args) {
        (new EmailServer()).run();
    }
}

