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
    public boolean removeEmail(long emailID) {
        Email[] emails = inbox.getEmails();
        for (int i = 0; i < emails.length; i++) {
            if (emails[i].getID() == emailID) {
                inbox.remove(i);
                return true;
            }
        }

        return false;
    }
}
