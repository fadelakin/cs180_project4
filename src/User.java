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
    private Email[] inbox;
    private DynamicBuffer buffer;
    private int uEmailID = 0;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        //inbox = new DynamicBuffer(8);
        inbox = new Email[10];
        buffer = new DynamicBuffer(inbox.length);
    }

    public String getName() {
        return username;
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    // number of emails in the user's inbox
    public int numEmail() {
        return buffer.numElements();
    }

    // add message to the user's inbox
    public void receiveEmail(String sender, String message) {
        Email email =  new Email(username, sender, uEmailID, message);
        uEmailID++;
        buffer.add(email);
    }

    // retrieve the n most recent emails in the user's inbox
    public Email[] retrieveEmail(int n) {
        return buffer.getNewest(n);
    }

    // remove an email with the specified emailID
    public boolean removeEmail(long emailID) {

        for (int i = 0; i < buffer.numElements(); i++) {
            if (buffer.doesIDExist(emailID)) {
                int index = buffer.deleteEmailbyIndexandID(emailID);
                buffer.remove(index);
                return true;
            }
        }

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
