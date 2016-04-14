import java.io.*;
import java.util.*;

/**
 * <b> CS 180 - Project 4 - Email Server Skeleton </b>
 * <p>
 * <p>
 * This is the skeleton code for the EmailServer Class. This is a private email
 * server for you and your friends to communicate.
 *
 * @author Temidayo Adelakin tadelaki@purdue.edu
 * @version April 11, 2016
 * @lab L11
 */

public class EmailServer {
    // Useful constants
    public static final String FAILURE = "FAILURE";
    public static final String DELIMITER = "\t";
    public static final String SUCCESS = "SUCCESS";
    public static final String CRLF = "\r\n";

    // Used to print out extra information
    private boolean verbose = false;

    private User[] users = new User[100];
    int totalUsers;

    LinkedHashMap<String, String> maps = new LinkedHashMap<>();
    private File file;

    public EmailServer() {
        users[0] = new User("root", "cs180");
        totalUsers++;
    }

    public EmailServer(String fileName) throws IOException {

        maps.put("root", "cs180");

        file = new File(fileName);

        if (!file.exists()) {
            file.createNewFile();

            BufferedReader reader;
            String line;
            String cvsSplitBy = ",";

            try {
                reader = new BufferedReader(new FileReader(file));
                while ((line = reader.readLine()) != null) {

                    // use comma as separator
                    String[] users = line.split(cvsSplitBy);

                    // using a hashmap allows us to forgo checking for duplicate users
                    // because the key would be the same but the value would change per hashmap rules
                    // > The prior value for the key is dropped and replaced with the new one.
                    if (users.length == 2) {
                        maps.put(users[0], users[1]);
                        addUser(users);
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            BufferedReader reader;
            String line;
            String cvsSplitBy = ",";

            try {
                reader = new BufferedReader(new FileReader(file));
                while ((line = reader.readLine()) != null) {

                    line = line.trim();

                    // use comma as separator
                    if (line.split(cvsSplitBy).length != 0 && !line.equals("") && !line.isEmpty()) {
                        String[] users = line.split(cvsSplitBy);

                        // using a hashmap allows us to forgo checking for duplicate users
                        // because the key would be the same but the value would change per hashmap rules
                        // > The prior value for the key is dropped and replaced with the new one.
                        if (users.length == 2) {
                            maps.put(users[0], users[1]);
                            addUser(users);
                        }
                    }

                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
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
                && !parts[0].equals("DELETE-USER") && !parts[0].equals("SEND-EMAIL")
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
                return addUser(parts);
            } else {
                return ErrorFactory.makeErrorMessage(-23);
            }

        }

        if (parts[0].equals("GET-ALL-USERS")) {

            boolean checkName = false;
            boolean checkPass = false;

            if (parts.length != 3) {
                return ErrorFactory.makeErrorMessage(ErrorFactory.FORMAT_COMMAND_ERROR);
            }

            if (User.checkUser(parts[1], parts[2])) {
                for (int i = 0; i < totalUsers; i++) {
                    if (users[i].getName().equals(parts[1])) {
                        checkName = true;
                        if (users[i].checkPassword(parts[2])) {
                            checkPass = true;
                        }
                    }
                }
            }

            if (!checkName) {
                return ErrorFactory.makeErrorMessage(ErrorFactory.USERNAME_LOOKUP_ERROR);
            }

            if (!checkPass) {
                return ErrorFactory.makeErrorMessage(ErrorFactory.AUTHENTICATION_ERROR);
            }

            return getAllUsers(parts);
        }

        if (parts[0].equals("DELETE-USER")) {

            boolean checkName = false;
            boolean checkPass = false;

            if (parts.length != 3) {
                return ErrorFactory.makeErrorMessage(ErrorFactory.FORMAT_COMMAND_ERROR);
            }

            if (User.checkUser(parts[1], parts[2])) {
                for (int i = 0; i < totalUsers; i++) {
                    if (users[i].getName().equals(parts[1])) {
                        checkName = true;

                        if (users[i].getName().equals("root")) {
                            return ErrorFactory.makeErrorMessage(ErrorFactory.INVALID_VALUE_ERROR);
                        }

                        if (users[i].checkPassword(parts[2])) {
                            checkPass = true;
                        }
                    }
                }
            }

            if (!checkName) {
                return ErrorFactory.makeErrorMessage(ErrorFactory.USERNAME_LOOKUP_ERROR);
            }

            if (!checkPass) {
                return ErrorFactory.makeErrorMessage(ErrorFactory.AUTHENTICATION_ERROR);
            }

            return deleteUser(parts);
        }

        if (parts[0].equals("SEND-EMAIL")) {

            boolean checkName = false;
            boolean checkPass = false;
            boolean checkRecip = false;

            if (parts.length != 5) {
                return ErrorFactory.makeErrorMessage(ErrorFactory.FORMAT_COMMAND_ERROR);
            }

            if (User.checkUser(parts[1], parts[2])) {
                for (int i = 0; i < totalUsers; i++) {
                    if (users[i].getName().equals(parts[1])) {
                        checkName = true;
                        if (users[i].checkPassword(parts[2])) {
                            checkPass = true;
                        }
                    }
                }
            }

            for (int i = 0; i < totalUsers; i++) {
                if (users[i].getName().equals(parts[3])) {
                    checkRecip = true;
                }
            }

            if (!checkName) {
                return ErrorFactory.makeErrorMessage(ErrorFactory.USERNAME_LOOKUP_ERROR);
            }

            if (!checkPass) {
                return ErrorFactory.makeErrorMessage(ErrorFactory.AUTHENTICATION_ERROR);
            }

            if (!checkRecip)
                return ErrorFactory.makeErrorMessage(ErrorFactory.USERNAME_LOOKUP_ERROR);

            // send email
            return sendEmail(parts);
        }

        if (parts[0].equals("GET-EMAILS")) {

            boolean checkName = false;
            boolean checkPass = false;

            if (parts.length != 4) {
                return ErrorFactory.makeErrorMessage(ErrorFactory.FORMAT_COMMAND_ERROR);
            }

            if (User.checkUser(parts[1], parts[2])) {
                for (int i = 0; i < totalUsers; i++) {
                    if (users[i].getName().equals(parts[1])) {
                        checkName = true;
                        if (users[i].checkPassword(parts[2])) {
                            checkPass = true;
                        }
                    }
                }
            }

            if (!checkName) {
                return ErrorFactory.makeErrorMessage(ErrorFactory.USERNAME_LOOKUP_ERROR);
            }

            if (!checkPass) {
                return ErrorFactory.makeErrorMessage(ErrorFactory.AUTHENTICATION_ERROR);
            }

            // get emails
            return getEmails(parts);
        }

        if (parts[0].equals("DELETE-EMAIL")) {

            boolean checkName = false;
            boolean checkPass = false;

            if (parts.length != 4) {
                return ErrorFactory.makeErrorMessage(ErrorFactory.FORMAT_COMMAND_ERROR);
            }

            if (User.checkUser(parts[1], parts[2])) {
                for (int i = 0; i < totalUsers; i++) {
                    if (users[i].getName().equals(parts[1])) {
                        checkName = true;
                        if (users[i].checkPassword(parts[2])) {
                            checkPass = true;
                        }
                    }
                }
            }

            if (!checkName) {
                return ErrorFactory.makeErrorMessage(ErrorFactory.USERNAME_LOOKUP_ERROR);
            }

            if (!checkPass) {
                return ErrorFactory.makeErrorMessage(ErrorFactory.AUTHENTICATION_ERROR);
            }

            if (parts[1].equals("root") && !parts[2].equals("cs180")) {
                return ErrorFactory.makeErrorMessage(-22);
            }

            if (User.checkUser(parts[1], parts[2])) {
                return deleteEmail(parts);
            } else {
                return ErrorFactory.makeErrorMessage(-23);
            }
        }

        return ErrorFactory.makeErrorMessage(ErrorFactory.UNKNOWN_COMMAND_ERROR);
    }

    // method to add a user
    public String addUser(String[] args) {

        if (!maps.isEmpty() && args.length == 2) {
            // email server was constructed with the file
            try {
                PrintWriter writer = new PrintWriter(file);
                StringBuilder sb = new StringBuilder();
                for (Object o : maps.entrySet()) {
                    Map.Entry pair = (Map.Entry) o;
                    sb.append(pair.getKey());
                    sb.append(",");
                    sb.append(pair.getValue());
                    sb.append("\n");
                }
                writer.write(sb.toString());
                writer.close();
                return SUCCESS.concat(CRLF);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!maps.isEmpty()) {
            // email server was constructed with the file
            try {
                PrintWriter writer = new PrintWriter(file);
                StringBuilder sb = new StringBuilder();
                for (Object o : maps.entrySet()) {
                    Map.Entry pair = (Map.Entry) o;
                    if (pair.getKey().equals("root")) {
                    } else if (pair.getKey().equals("run")) {
                    } else {
                        sb.append(pair.getKey());
                        sb.append(",");
                        sb.append(pair.getValue());
                        sb.append("\n");
                    }
                }
                sb.append(args[1]);
                sb.append(",");
                sb.append(args[2]);
                sb.append("\n");
                writer.write(sb.toString());
                writer.close();
                return SUCCESS.concat(CRLF);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {

            if (args[2] == null || args[2].length() == 0) {
                return ErrorFactory.makeErrorMessage(ErrorFactory.INVALID_VALUE_ERROR);
            }

            for (int i = 1; i < args.length; i++) {
                if (!isValid(args[i])) {
                    return ErrorFactory.makeErrorMessage(ErrorFactory.INVALID_VALUE_ERROR);
                }
                if (args[i].length() > 20) {
                    return ErrorFactory.makeErrorMessage(ErrorFactory.INVALID_VALUE_ERROR);
                }
                if (i == 3) {
                    if (args[i].length() < 4) {
                        return ErrorFactory.makeErrorMessage(ErrorFactory.INVALID_VALUE_ERROR);
                    }
                } else if (i == 2) {
                    if (args[i].length() < 1) {
                        return ErrorFactory.makeErrorMessage(ErrorFactory.INVALID_VALUE_ERROR);
                    }
                }
            }


            for (int i = 0; i < totalUsers; i++) {
                if (users[i].getName().equals(args[1])) {
                    return ErrorFactory.makeErrorMessage(ErrorFactory.USER_EXIST_ERROR);
                }
            }

            User newUser = new User(args[1], args[2]);
            User[] temp = new User[users.length + 1];
            System.arraycopy(users, 0, temp, 0, users.length);
            for (int i = 0; i < temp.length; i++) {
                if (temp[i] == null) {
                    temp[i] = newUser;
                }
            }
            users = temp;
            totalUsers++;
            return SUCCESS.concat(CRLF);


        } catch (NumberFormatException e) {
            return ErrorFactory.makeErrorMessage(ErrorFactory.INVALID_VALUE_ERROR);
        }
    }

    public static boolean isValid(String input) {
        return input.matches("^[a-zA-Z0-9]*$");
    }

    // method to get all users
    public String getAllUsers(String[] args) {

        String ret = "";
        ret = ret.concat(SUCCESS);

        if (!maps.isEmpty()) {

            Set<String> userNames = maps.keySet();

            for (String userName : userNames) {
                ret = ret.concat(DELIMITER).concat(userName);
            }

            return ret.concat(CRLF);
        }

        for (int i = 0; i < totalUsers; i++) {
            ret = ret.concat(DELIMITER).concat(users[i].getName());
        }

        return ret.concat(CRLF);
    }

    // method to delete user
    public String deleteUser(String[] args) {

        if (!maps.isEmpty() && args.length == 2) {
            // email server was constructed with the file
            try {
                PrintWriter writer = new PrintWriter(file);
                StringBuilder sb = new StringBuilder();
                for (Object o : maps.entrySet()) {
                    Map.Entry pair = (Map.Entry) o;
                    sb.append(pair.getKey());
                    sb.append(",");
                    sb.append(pair.getValue());
                    sb.append("\n");
                }
                writer.write(sb.toString());
                writer.close();
                return SUCCESS.concat(CRLF);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!maps.isEmpty()) {
            // email server was constructed with the file
            try {

                for (Object o : maps.entrySet()) {
                    Map.Entry pair = (Map.Entry) o;
                    if (pair.getKey().equals(args[1])) {
                        if (pair.getValue().equals(args[2])) {
                            maps.remove(pair.getKey());
                            break;
                        }
                    }
                }

                PrintWriter writer = new PrintWriter(file);
                StringBuilder sb = new StringBuilder();
                for (Object o : maps.entrySet()) {
                    Map.Entry pair = (Map.Entry) o;
                    if (pair.getKey().equals("root")) {
                    } else if (pair.getKey().equals("run")) {
                    } else {
                        sb.append(pair.getKey());
                        sb.append(",");
                        sb.append(pair.getValue());
                        sb.append("\n");
                    }
                }
                writer.write(sb.toString());
                writer.close();
                return SUCCESS.concat(CRLF);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {

            if (args[1].equals("root")) {
                return ErrorFactory.makeErrorMessage(ErrorFactory.INVALID_VALUE_ERROR);
            }

            for (int i = 0; i < totalUsers; i++) {

                if (users[i].getName().equals(args[1])) {
                    for (int j = i; j < totalUsers - 1; j++) {
                        users[j] = users[j + 1];
                    }
                    users[totalUsers - 1] = null;
                    totalUsers--;
                }
            }

            return SUCCESS + CRLF;
        } catch (NumberFormatException e) {
            return ErrorFactory.makeErrorMessage(ErrorFactory.INVALID_VALUE_ERROR);
        }
    }

    // method to send email
    public String sendEmail(String[] args) {

        boolean checkRecipName = false;

        for (int i = 0; i < totalUsers; i++) {
            if (users[i].getName().equals(args[3])) {
                checkRecipName = true;
            }
        }

        if (!checkRecipName)
            return ErrorFactory.makeErrorMessage(ErrorFactory.USERNAME_LOOKUP_ERROR);

        Email email = new Email(args[3], args[1], new Random().nextLong(), args[4]);
        for (int i = 0; i < totalUsers; i++) {
            if (users[i].getName().equals(args[3])) {
                users[i].receiveEmail(email.getSender(), email.getMessage());
                return SUCCESS + CRLF;
            }
        }

        return FAILURE + CRLF;
    }

    // method to get emails
    public String getEmails(String[] args) {

        String ret = "";
        int numMessages = Integer.parseInt(args[3]);

        ret = ret.concat(SUCCESS);
        User user = new User("root", "cs180");
        Email[] emails;

        for (int i = 0; i < totalUsers; i++) {
            if (users[i].getName().equals(args[1])) {
                user = users[i];
            }
        }

        if (user != null) {
            emails = user.retrieveEmail(numMessages);

            if (emails == null || emails.length <= 0) {
                return ret.concat(CRLF);
            }

            for (int i = 0; i < numMessages; i++) {
                if (emails[i] != null) {
                    ret = ret.concat(DELIMITER).concat(emails[i].toString());
                }
            }
        }

        return ret.concat(CRLF);

    }

    // method to delete emails
    public String deleteEmail(String[] args) {

        try {

            if (args[2] == null || args[2].length() == 0) {
                return ErrorFactory.makeErrorMessage(ErrorFactory.INVALID_VALUE_ERROR);
            }

            for (int i = 1; i < args.length; i++) {
                if (!isValid(args[i])) {
                    return ErrorFactory.makeErrorMessage(ErrorFactory.INVALID_VALUE_ERROR);
                }
                if (args[i].length() > 20) {
                    return ErrorFactory.makeErrorMessage(ErrorFactory.INVALID_VALUE_ERROR);
                } else if (i == 2) {
                    if (args[i].length() < 1) {
                        return ErrorFactory.makeErrorMessage(ErrorFactory.INVALID_VALUE_ERROR);
                    }
                }
            }

            String ret = "";

            long emailID = Long.parseLong(args[3]);

            ret = ret.concat(SUCCESS);
            User user = new User("root", "cs180");
            Email[] emails;

            for (int i = 0; i < totalUsers; i++) {
                if (users[i].getName().equals(args[1])) {
                    user = users[i];
                }
            }

            if (user != null) {

                int numEmails = user.numEmail();
                emails = user.retrieveEmail(numEmails + 1);

                boolean isEmailID = false;
                for (int i = 0; i < numEmails; i++) {
                    if (emails[i].getID() == Long.parseLong(args[3])) {
                        isEmailID = true;
                        break;
                    }
                }

                if (!isEmailID) {
                    return ErrorFactory.makeErrorMessage(ErrorFactory.INVALID_VALUE_ERROR);
                }

                for (int i = 0; i < numEmails; i++) {
                    if (emails[i].getID() == emailID) {
                        user.removeEmail(emailID);
                        return ret.concat(CRLF);
                    }
                }
            }

            return ret;

        } catch (NumberFormatException e) {
            return ErrorFactory.makeErrorMessage(ErrorFactory.INVALID_VALUE_ERROR);
        }
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
     *
     * @param args - the command line arguments
     */
    public static void main(String[] args) {
        (new EmailServer()).run();
    }
}

