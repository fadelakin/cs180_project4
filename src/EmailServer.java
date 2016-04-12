import java.util.*;

/**
 * <b> CS 180 - Project 4 - Email Server Skeleton </b>
 * <p>
 * 
 * This is the skeleton code for the EmailServer Class. This is a private email
 * server for you and your friends to communicate.
 * 
 * @author (Your Name) <(YourEmail@purdue.edu)>
 * 
 * @lab (Your Lab Section)
 * 
 * @version (Today's Date)
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
        // TODO: implement this method
        return null;
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

