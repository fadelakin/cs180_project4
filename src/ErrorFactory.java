/**
 * <b> Project 4 - Error Factory </b>
 * <p>
 * 
 * This class is an error message factory. It will generate the error
 * messages that the email server will send when an error occurs
 * during the processing of a client request.
 * 
 * @author ghousto
 * @version 8/10/15
 *
 */
public class ErrorFactory {
	
	public static final int UNKNOWN_ERROR = -1;
	
	public static final int FORMAT_COMMAND_ERROR = -10;
	public static final int UNKNOWN_COMMAND_ERROR = -11;
	
	public static final int USERNAME_LOOKUP_ERROR = -20;
	public static final int AUTHENTICATION_ERROR = -21;
	public static final int USER_EXIST_ERROR = -22;
	public static final int INVALID_VALUE_ERROR = -23;
	
	/**
	 * Creates a "FAILURE" server response based on the error code
	 * and appends the user message to the end of the response.
	 * 
	 * @param errorCode - the generic error that occurred
	 * @param customMessage - the message describing the error
	 * @return a fully formatted server failure response or null if 
	 */
	public static String makeErrorMessage(int errorCode, String customMessage) {
		StringBuilder ret = new StringBuilder("FAILURE\t");
		ret.append(errorCode);
		ret.append("\t");
		
		switch(errorCode) {
			case UNKNOWN_ERROR:
				ret.append("Unknown Error: ");
				break;
				
			case FORMAT_COMMAND_ERROR:
				ret.append("Format Command Error: ");
				break;
				
			case UNKNOWN_COMMAND_ERROR:
				ret.append("Unknown Command Error: ");
				break;
				
			case USERNAME_LOOKUP_ERROR:
				ret.append("Username Lookup Error: ");
				break;
				
			case AUTHENTICATION_ERROR:
				ret.append("Authentication Error: ");
				break;
				
			case USER_EXIST_ERROR:
				ret.append("User Exist Error: ");
				break;
				
			case INVALID_VALUE_ERROR:
				ret.append("Invalid Value Error: ");
				break;
				
			default:
				return makeErrorMessage(0, String.format(
				"The error code \"%02d\" is not recognized.", errorCode));
		}
		
		ret.append(customMessage);
		ret.append("\r\n");
		
		return ret.toString();
	}
	
	public static String makeErrorMessage(int errorCode) {
		StringBuilder message = new StringBuilder();
		
		switch(errorCode) {
			case UNKNOWN_ERROR:
				message.append("An unknown error occurred. This was likely caused by an uncaught exception.");
				break;
			
			case FORMAT_COMMAND_ERROR:
				message.append("The specified client command isn't formatted properly.");
				break;
			
			case UNKNOWN_COMMAND_ERROR:
				message.append("The specified client command doesn't exist.");
				break;

			case USERNAME_LOOKUP_ERROR:
				message.append("The specified user does not exist.");
				break;
				
			case AUTHENTICATION_ERROR:
				message.append("The given password is not correct for the specified user.");
				break;
				
			case USER_EXIST_ERROR:
				message.append("The user cannot be created because the username has already been taken.");
				break;
				
			case INVALID_VALUE_ERROR:
				message.append("One of the specified values is logically invalid.");
				break;
		}
		
		return makeErrorMessage(errorCode, message.toString());
	}

}
