import java.util.regex.Pattern;

/**
 * CS 180 - Project 4 - User class
 *
 * @author Temidayo Adelakin, tadelaki@purdue.edu
 *
 * @lab L11
 *
 * @version April 11, 2016
 */
public class User {

    private String username;
    private String password;
    private DynamicBuffer inbox;
    private static int emailID = 0;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        inbox = new DynamicBuffer(8);
    }

    public String getName() {
        return username;
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    // number of emails in the user's inbox
    public int numEmail() {
        return inbox.numElements();
    }

    // add message to the user's inbox
    public void receiveEmail(String sender, String message) {
        Email email =  new Email(username, sender, emailID, message);
        emailID++;
        inbox.add(email);
    }

    // retrieve the n most recent emails in the user's inbox
    public Email[] retrieveEmail(int n) {
        return inbox.getNewest(n);
    }

    // remove an email with the specified emailID
    // TODO: Gonna come back to this later
    public boolean removeEmail(long emailID) {

        //System.out.println("Email id i'm looking for is: " + emailID);
        int numElements = inbox.numElements();
        Email[] emails = inbox.getNewest(numElements);
        for (int i = 0; i < numElements; i++) {
            //System.out.println("Email ID I have for is: " + emails[i].getID());
            if (emails[i] != null) {
                if (emails[i].getID() == emailID) {
                    inbox.remove(i);
                    //System.out.println("Removed email");
                    //System.out.println("Returning true");
                    return true;
                }
            }
        }

        //System.out.println("Returning false");
        return false;
    }

    public static boolean checkUser(String username, String password) {

        boolean usernameLength = false, passwordLength = false;
        if (username.length() >= 1 && username.length() <= 20) {
            usernameLength = true;
        }

        if (password.length() >= 4 && password.length() <= 20) {
            passwordLength = true;
        }

        if (!passwordLength || !usernameLength)
            return false;

        Pattern pattern = Pattern.compile("[^a-zA-Z0-9]");
        boolean userNameHasSpecialChar = pattern.matcher(username).find();
        boolean passwordHasSpecialChar = pattern.matcher(password).find();

        return !(userNameHasSpecialChar && passwordHasSpecialChar) && !(userNameHasSpecialChar || passwordHasSpecialChar);

    }
}
