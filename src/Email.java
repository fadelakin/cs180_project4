import java.util.Date;

/**
 * Created by temidayo on 4/11/16.
 */
public class Email {

    private String recipient;
    private String sender;
    private long id;
    private String message;
    private Date messageDate;

    public Email(String recipient, String sender, long id, String message) {
        this.recipient = recipient;
        this.sender = sender;
        this.id = id;
        this.message = message;
        messageDate = new Date();
    }

    public String getOwner() {
        return recipient;
    }

    public String getSender() {
        return sender;
    }

    public long getID() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public String toString() {
        return id + ";" + messageDate.toString() + "; From: " + sender + " \"" + message + "\"";
    }
}
