/**
 * CS 180 - Project 4 - Dynamic Buffer
 *
 * @author Temidayo Adelakin, tadelaki@purdue.edu
 *
 * @lab L11
 *
 * @version April 11, 2016
 */
public class DynamicBuffer {

    private int maxEmails;
    private int totalEmails;

    private Email[] emails;
    private final int initSize;

    public DynamicBuffer(int initSize) {
        this.initSize = initSize;
        maxEmails = initSize;
        emails = new Email[maxEmails];
        totalEmails = 0;
    }

    // return the number of emails stored in the array
    public int numElements() {
        return totalEmails;
    }

    // return the length of the array
    public int getBufferSize() {
        checkBufferSize();
        return emails.length;
    }

    // add an email to the emails array
    // if the array becomes full, double its size
    public void add(Email email) {

        if (totalEmails == maxEmails) {
            maxEmails *=2;

            Email[] emails = new Email[maxEmails];
            System.arraycopy(this.emails, 0, emails, 0, this.emails.length);
            this.emails = emails;
        }

        emails[totalEmails] = email;
        totalEmails++;
        checkBufferSize();
    }

    // removes an email at the specified index from the buffer
    // return true if the index is valid and an email is removed; else return false
    // If the number of emails in the buffer becomes less than or equal to one fourth of the buffer size after the removal,
    // > shrink the buffer size to half of the current buffer size.
    // Note: the buffer size should never be lower than the initial size.
    public boolean remove(int index) {

        for(int i = 0; i < totalEmails; i++) {
            if (emails[index] != null && emails[index] == emails[i]) {
                for (int j = i; j < totalEmails - 1; j++) {
                    emails[j] = emails[j + 1];
                }

                emails[totalEmails - 1] = null;
                totalEmails--;
                checkBufferSize(); // check the buffer size after removing the email, then return true
                return true;
            }
        }

        return false;
    }

    // checks the buffer size and acts accordingly
    private void checkBufferSize() {
        if (totalEmails <= (emails.length / 2) / 2) {
            maxEmails /= 2;

            if (maxEmails < initSize) {
                maxEmails *= 2;
            }

            Email[] emails = new Email[maxEmails];
            for (int i = 0; i < totalEmails; i++) {
                emails[i] = this.emails[i];
            }

            this.emails = emails;
        }

        if (totalEmails == emails.length) {
            maxEmails *=2;

            Email[] emails = new Email[maxEmails];
            for (int i = 0; i < totalEmails; i++) {
                emails[i] = this.emails[i];
            }

            this.emails = emails;
        }
    }

    public int deleteEmailbyIndexandID(long emailID) {
        for (int i = 0; i < totalEmails; i++) {
            if (emails[i].getID() == emailID)
                return i;
        }

        return 0;
    }

    public boolean doesIDExist(long emailID) {
        for (int i = 0; i < totalEmails; i++) {
            if (emails[i].getID() == emailID)
                return true;
        }

        return false;
    }

    // gets the n most recently added emails to the buffer (the last n)
    // returned emails must be sorted from most recently to least recently added to the buffer.
    // return all emails if n is greater than the number of emails in the buffer
    // return null if the buffer is empty or an invalid number of emails is requested (e.g. -1)
    public Email[] getNewest(int n) {

        if (n > emails.length) {
            return this.emails;
        }

        if (n > totalEmails && totalEmails == 0) {
            return null;
        }

        if (emails.length == 0 || n < 0) {
            // Either n is less than 0 or emails length is 0
            return null;
        }

        Email[] newestEmails = new Email[n];

        int counter;
        int back = 1;

        for (counter = 0; counter < n; counter++) {
            newestEmails[counter] = emails[totalEmails - back];
            back++;
        }

        return newestEmails;
    }
}
