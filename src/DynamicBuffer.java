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
        System.out.println("length of emails at end: " + emails.length);
        checkBufferSize();
        System.out.println("length of emails at end: " + emails.length);
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

        /* Don't think this method would work so I commented it out for now.
        if (emails[index] != null) {
            for (int i = 0; i < totalEmails; i++) {
                for (int j = i; j < totalEmails - 1; j++) {
                    emails[j] = emails[j + 1];
                }

                emails[totalEmails - 1] = null;
                totalEmails--;
            }
        }
        */

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

    private void checkBufferSize() {
        if (totalEmails <= (emails.length / 2) / 2) {
            maxEmails /= 2;

            if (maxEmails < initSize) {
                maxEmails *= 2;
            }

            Email[] emails = new Email[maxEmails];
            //System.arraycopy(this.emails, 0, emails, 0, this.emails.length);
            for (int i = 0; i < totalEmails; i++) {
                emails[i] = this.emails[i];
            }
            System.out.println("length of emails at buffer 1: " + emails.length);
            this.emails = emails;
        }

        /*if ((totalEmails <= (emails.length / 2) / 2) && totalEmails != 0) {
            System.out.println("DING DING DING");
        }*/

        if (totalEmails == emails.length) {
            maxEmails *=2;

            Email[] emails = new Email[maxEmails];
            for (int i = 0; i < totalEmails; i++) {
                emails[i] = this.emails[i];
            }
            System.out.println("length of emails at buffer 2: " + emails.length);
            this.emails = emails;
        }
    }

    public Email[] getNewest(int n) {
        return new Email[maxEmails];
    }
}
