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
    int numUsers;
    int numEmails;

    public EmailServer() {
        String[] args = {"ADD-USER", "root", "cs180"};
        //this.addUser(args);
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

        if (request.startsWith("\t")) {
            return ErrorFactory.makeErrorMessage(ErrorFactory.FORMAT_COMMAND_ERROR);
        } else if (request.endsWith("\t")) {
            return ErrorFactory.makeErrorMessage(ErrorFactory.FORMAT_COMMAND_ERROR);
        } else if (!request.endsWith("\r\n")) {
            return ErrorFactory.makeErrorMessage(ErrorFactory.FORMAT_COMMAND_ERROR);
        }

        String sub = request.substring(0, request.length() - 2);
        String[] parts = sub.split("\t");

        if (parts.length == 1)
            return ErrorFactory.makeErrorMessage(ErrorFactory.FORMAT_COMMAND_ERROR);

        if (!parts[0].equals("ADD-USER") && !parts[0].equals("GET-ALL-USERS")
                && !parts[0].equals("DELETE_USER") && !parts[0].equals("SEND-EMAIL")
                && !parts[0].equals("GET-EMAILS") && !parts[0].equals("DELETE-EMAIL")) {
            return ErrorFactory.makeErrorMessage(ErrorFactory.UNKNOWN_COMMAND_ERROR);
        }

        if (parts[0].equals("ADD-USER")) {
            if (parts.length != 3) {
                return ErrorFactory.makeErrorMessage(ErrorFactory.FORMAT_COMMAND_ERROR);
            }
            if (parts[1].equals("root") && !parts[2].equals("cs180")) {
                return ErrorFactory.makeErrorMessage(-22);
            }

            if (parts[1].equals("root")) {
                return ErrorFactory.makeErrorMessage(-10);
            }

            if (User.checkUser(parts[1], parts[2])) {
                //return addUser(parts);
            } else {
                return ErrorFactory.makeErrorMessage(-23);
            }
        }

        if (parts[0].equals("GET-ALL-USERS")) {
            if (parts.length != 3) {
                return ErrorFactory.makeErrorMessage(ErrorFactory.FORMAT_COMMAND_ERROR);
            } else {
                // get all users
            }
        }

        if (parts[0].equals("DELETE-USER")) {
            if (parts.length != 3) {
                return ErrorFactory.makeErrorMessage(ErrorFactory.FORMAT_COMMAND_ERROR);
            } else {
                // delete user
            }
        }

        if (parts[0].equals("SEND-EMAIL")) {
            if (parts.length != 5) {
                return ErrorFactory.makeErrorMessage(ErrorFactory.FORMAT_COMMAND_ERROR);
            } else {
                // send email
            }
        }

        if (parts[0].equals("GET-EMAILS")) {
            if (parts.length != 4) {
                return ErrorFactory.makeErrorMessage(ErrorFactory.FORMAT_COMMAND_ERROR);
            } else {
                // get email
            }
        }

        if (parts[0].equals("DELETE-EMAIL")) {

            if (parts.length != 4) {
                return ErrorFactory.makeErrorMessage(ErrorFactory.FORMAT_COMMAND_ERROR);
            }

            if (parts[1].equals("root") && !parts[2].equals("cs180")) {
                return ErrorFactory.makeErrorMessage(-22);
            }

            if (User.checkUser(parts[1], parts[2])) {
                //return addUser(parts);
            } else {
                return ErrorFactory.makeErrorMessage(-23);
            }

            deleteEmails(parts);
        }

        return ErrorFactory.makeErrorMessage(ErrorFactory.UNKNOWN_COMMAND_ERROR);
    }

    public int numUsers() {
        int counter = 0;
        for (User u: this.users) {
            if (u != null)
                counter++;
        }
        return counter;
    }

    public boolean findUser(String username) {
        for (User u: this.users) {
            if (u.getName().equals(username))
                return true;
        }
        return false;
    }

    public String getAllUsers(String[] args) {
        return "";
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

