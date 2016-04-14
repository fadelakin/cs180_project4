import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

import static org.junit.Assert.*;

public class Project4TestStudent {

    private static final String TEST_CSV = "test_student.csv";
	private static final String SUCCESS = "SUCCESS\r\n";


	private static final String TEST_EMAIL_USERNAME = "testuser33";
	private static final String TEST_EMAIL_SENDER   = "sender1";
	private static final int TEST_EMAIL_ID       = 2;
	private static final String TEST_EMAIL_MESSAGE  = "TEST MESSAGE 2";
	private static final Email TEST_EMAIL = new Email(TEST_EMAIL_USERNAME, TEST_EMAIL_SENDER, TEST_EMAIL_ID, TEST_EMAIL_MESSAGE);


	@Before
	public void setUp() throws Exception {
		// delete user file for each test
		try{
			new File(TEST_CSV).delete();
		}catch(Exception e){
			e.printStackTrace();
		}
	}


	// Checks if the given message is an error message.
	private static boolean isErrorMessage(String message) {
		return message.trim().startsWith("FAILURE");
	}

	// Checks that the given error message is formatted correctly
	private static boolean isFormattedCorrectly(String message) {
		if (!message.endsWith("\r\n"))
			return false;

		String[] st = message.split("\t");
		if (st.length != 3)
			return false;

		try {
			Integer.parseInt(st[1]);
		} catch(Exception e) {
			return false;
		}

		return true;
	}

	private static void matchesErrorCode(String messagePrefix, String response, int expectedCode) {
		assertTrue(messagePrefix + " returned an error", isErrorMessage(response));
		assertTrue(messagePrefix + " error response was formatted incorrectly", isFormattedCorrectly(response));

		// get response code
		int given = Integer.parseInt(response.split("\t")[1]);
		assertEquals(messagePrefix + " response code is incorrect", expectedCode, given);
	}


	/********************************************************************************************************
	 *
	 * Email
	 *
	 ********************************************************************************************************/
	@Test(timeout=1000)
	public void testEmailGetFunctions() {
        String prefix = "testEmailGetFunctions(): ";

		assertEquals(prefix + "Email.getID() returns incorrect ID.", TEST_EMAIL_ID, TEST_EMAIL.getID());
		assertEquals(prefix + "Email.getOwner() returns incorrect string.", TEST_EMAIL_USERNAME, TEST_EMAIL.getOwner());
		assertEquals(prefix + "Email.getSender() returns incorrect string.", TEST_EMAIL_SENDER, TEST_EMAIL.getSender());
		assertEquals(prefix + "Email.getMessage() returns incorrect string.", TEST_EMAIL_MESSAGE, TEST_EMAIL.getMessage());
	}

	@Test(timeout=1000)
	public void testEmailToString() {

		// ?1;Sun Oct 18 01:16:36 EDT 2015; From: sender1 ?TEST MESSAGE 1?
		String[] splitStr = TEST_EMAIL.toString().split(";");

		assertEquals("testEmailToString(): Email.toString() format error.", "" + TEST_EMAIL_ID, splitStr[0]);

		try {
			SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
			Date date = formatter.parse(splitStr[1]);
		} catch (ParseException e) {
			assertTrue("testEmailToString(): Email.toString() date format error", false);
		}

		assertEquals("testEmailToString(): Email.toString() format error.", " From: " + TEST_EMAIL_SENDER + " \"" +TEST_EMAIL_MESSAGE+ "\"", splitStr[2]);
	}



	/********************************************************************************************************
	 *
	 * Dynamic Buffer
	 *
	 ********************************************************************************************************/

	private static final int initSize = 4; // this needs to be a power of 2
	@Test(timeout=1000)
	public void testBufferSize() {

		DynamicBuffer buf = new DynamicBuffer(initSize);

		assertEquals("testBufferSize(): DynamicBuffer.getBufferSize() returns incorrect size.", initSize, buf.getBufferSize());
		assertEquals("testBufferSize(): DynamicBuffer.numElements() returns incorrect size.", 0, buf.numElements());
	}

	@Test(timeout=1000)
	public void testBufferAdd() {
		DynamicBuffer buf = new DynamicBuffer(initSize);

		assertEquals("testBufferAdd(): DynamicBuffer.getBufferSize() returns incorrect size.", initSize, buf.getBufferSize());
		assertEquals("testBufferAdd(): DynamicBuffer.numElements() returns incorrect size.", 0, buf.numElements());

		buf.add(TEST_EMAIL);

		assertEquals("testBufferAdd(): DynamicBuffer.getBufferSize() returns incorrect size.", initSize, buf.getBufferSize());
		assertEquals("testBufferAdd(): DynamicBuffer.numElements() returns incorrect size.", 1, buf.numElements());
	}

	@Test(timeout=1000)
	public void testBufferRetrieveOne() {
		final String ERROR_MESSAGE = "testBufferRetrieveOne(): DynamicBuffer.getNewest(n) returns incorrect elements.";

		DynamicBuffer buf = new DynamicBuffer(initSize);

		Email[] retEmails = buf.getNewest(1);
		assertTrue("testBufferRetrieveOne(): DynamicBuffer.getNewest(n) returns incorrect elements.", retEmails == null);

		Email email = new Email("user1", "sender1", 1L, "TEST MESSAGE 1");
		buf.add(email);

		retEmails = buf.getNewest(1);
		assertTrue(ERROR_MESSAGE, retEmails.length == 1);
		assertEquals(ERROR_MESSAGE, 1L, retEmails[0].getID());
		assertEquals(ERROR_MESSAGE, "user1", retEmails[0].getOwner());
		assertEquals(ERROR_MESSAGE, "sender1", retEmails[0].getSender());
		assertEquals(ERROR_MESSAGE, "TEST MESSAGE 1", retEmails[0].getMessage());
	}

	@Test(timeout=1000)
	public void testBufferRetrieveMultiple() {
		final String INCORRECT_PREFIX = "testBufferRetrieveMultiple(): DynamicBuffer.getNewest(n) returns incorrect ";

		DynamicBuffer buf = new DynamicBuffer(initSize);

		// most recent first
		Email[] ourBuffer = new Email[5];

		Email email;
		for (int i = 0 ; i < 5 ; i++) {
			email = new Email("user"+String.format("%d", i), "sender"+String.format("%d", i), i, "TEST MESSAGE "+String.format("%d", i));
			buf.add(email);
			ourBuffer[4 - i] = email;
		}

		Email[] retEmails = buf.getNewest(1);
		assertEquals(INCORRECT_PREFIX + "number of elements", 1, retEmails.length);
		assertEquals(INCORRECT_PREFIX + "first email", ourBuffer[0].toString(), retEmails[0].toString());

		retEmails = buf.getNewest(5);
		assertEquals(INCORRECT_PREFIX + "number of elements", 5, retEmails.length);
		for (int i = 0; i < 5; i++) {
			assertEquals(INCORRECT_PREFIX + "email " + i, ourBuffer[i].toString(), retEmails[i].toString());
		}
	}

	@Test(timeout=1000)
	public void testBufferGrowth() {
		DynamicBuffer buf = new DynamicBuffer(initSize);

		// Growth
		Email email;
		int i;
		for (i = 0 ; i < initSize-1 ; i++) {
			email = new Email("user"+String.format("%d", i), "sender"+String.format("%d", i), (long)i, "TEST MESSAGE "+String.format("%d", i));
			buf.add(email);
			assertEquals("testBufferGrowth(): DynamicBuffer.numElements() returns incorrect size.", i + 1, buf.numElements());
		}

		assertEquals("testBufferGrowth(): DynamicBuffer.getBufferSize() returns incorrect size.", initSize, buf.getBufferSize());

		email = new Email("user7", "sender" + (initSize - 1), (initSize - 1), "TEST MESSAGE " + (initSize - 1));
		buf.add(email);

		assertEquals("testBufferGrowth(): DynamicBuffer.numElements() returns incorrect size.", initSize, buf.numElements());
		assertEquals("testBufferGrowth(): DynamicBuffer.getBufferSize() returns incorrect size.", initSize * 2, buf.getBufferSize());

		// grow to 128
		for (i = initSize ; i < 64 ; i++) {
			email = new Email("user"+String.format("%d", i), "sender"+String.format("%d", i), (long)i, "TEST MESSAGE "+String.format("%d", i));
			buf.add(email);
		}

		assertEquals("testBufferGrowth(): DynamicBuffer.numElements() returns incorrect size.", 64, buf.numElements());
		assertEquals("testBufferGrowth(): DynamicBuffer.getBufferSize() returns incorrect size.", 64 * 2, buf.getBufferSize());

		// retrieve
		Email[] retEmails = buf.getNewest(64);
		assertTrue("testBufferGrowth(): DynamicBuffer.getNewest(n) returns null, which is not expected.", retEmails != null);

		System.out.println(Arrays.deepToString(retEmails));
		for (i = 0 ; i < 64 ; i++) {
			assertEquals("testBufferGrowth(): Wrong id in email " + i, 63 - i, retEmails[i].getID());
		}
	}

	@Test(timeout=1000)
	public void testBufferRemoveByIndex() {
		DynamicBuffer buf = new DynamicBuffer(initSize);

		Email email;
		int i;
		for (i = 0 ; i < 64 ; i++) {
			email = new Email("user"+String.format("%d", i), "sender"+String.format("%d", i), i, "TEST MESSAGE "+String.format("%d", i));
			buf.add(email);

		}
		assertEquals("testBufferRemoveByIndex(): DynamicBuffer.numElements() returns incorrect size.", 64, buf.numElements());
		assertEquals("testBufferRemoveByIndex(): DynamicBuffer.getBufferSize() returns incorrect size.", 128, buf.getBufferSize());

		// remove non-existed index
		boolean ret = buf.remove(64);
		assertFalse("testBufferRemoveByIndex(): DynamicBuffer.remove(index) fails to remove non-existed index.", ret);

		// remove one
		ret = buf.remove(62);
		assertTrue("testBufferRemoveByIndex(): DynamicBuffer.remove(index) returns false, but the index should exist.", ret);

		Email[] retEmails = buf.getNewest(63);
		int j = 0;
		for (i = 0 ; i < 63 ; i++, j++) {
			if (i==1)
				j++;
			assertEquals("testBufferRemoveByIndex(): Email.getID() returns incorrect ID.", 63 - j, retEmails[i].getID());
		}
	}

	@Test(timeout=1000)
	public void testBufferShrink() {
		DynamicBuffer buf = new DynamicBuffer(initSize);

		Email email;
		int i;
		for (i = 0 ; i < 64 ; i++) {
			email = new Email("user"+String.format("%d", i), "sender"+String.format("%d", i), i, "TEST MESSAGE "+String.format("%d", i));
			buf.add(email);
		}

		assertEquals("testBufferShrink(): DynamicBuffer.numElements() returns incorrect size.", 64, buf.numElements());
		assertEquals("testBufferShrink(): DynamicBuffer.getBufferSize() returns incorrect size.", 128, buf.getBufferSize());

		// Remove 32 elements (64->32), buffer size should shrink to 64
		boolean ret;
		for (i = 0 ; i < 32 ; i++) {
			ret = buf.remove(i);
		}
		assertEquals("testBufferShrink(): DynamicBuffer.numElements() returns incorrect size.", 32, buf.numElements());
		assertEquals("testBufferShrink(): DynamicBuffer.getBufferSize() returns incorrect size.", 64, buf.getBufferSize());

		// remove 16 elements (32->16), buffer size should shrink to 32
		for (i = 0 ; i < 16 ; i++) {
			ret = buf.remove(i);
		}
		assertEquals("testBufferShrink(): DynamicBuffer.numElements() returns incorrect size.", 16, buf.numElements());
		assertEquals("testBufferShrink(): DynamicBuffer.getBufferSize() returns incorrect size.", 32, buf.getBufferSize());

		// new array with 8 elements
		buf = new DynamicBuffer(initSize);
		for (i = 0 ; i < 8 ; i++) {
			email = new Email("user"+String.format("%d", i), "sender"+String.format("%d", i), i, "TEST MESSAGE "+String.format("%d", i));
			buf.add(email);
		}
		assertEquals("testBufferShrink(): DynamicBuffer.numElements() returns incorrect size.", 8, buf.numElements());
		assertEquals("testBufferShrink(): DynamicBuffer.getBufferSize() returns incorrect size.", 16, buf.getBufferSize());

		// remove 4 elements (8->4), buffer size should keep 16, since it should not be lower than the initial size
		for (i = 0 ; i < 4 ; i++) {
			ret = buf.remove(i);
		}
		assertEquals("testBufferShrink(): DynamicBuffer.numElements() returns incorrect size.", 4, buf.numElements());
		assertEquals("testBufferShrink(): DynamicBuffer.getBufferSize() returns incorrect size.", 8, buf.getBufferSize());
	}



	/********************************************************************************************************
	 *
	 * User
	 *
	 ********************************************************************************************************/
	@Test(timeout=1000)
	public void testUserInit() {
		String name = "testuser2";
		String password = "testpassword2";
		User user = new User(name, password);

		assertEquals("testUserInit(): User.getName() returns incorrect name.", name, user.getName());
		assertTrue("testUserInit(): User.checkPassword() fails.", user.checkPassword(password));
		assertEquals("testUserInit(): User.numEmail() returns incorrect value.", user.numEmail(), 0);
	}

	@Test(timeout=1000)
	public void testUserReceiveEmails() {
		User user = new User("user", "password");

		final int nEmails = 10;
		for (int i = 0 ; i < nEmails ; i++) {
			user.receiveEmail("sender" + String.format("%d", i), "TEST MESSAGE " + String.format("%d", i));
		}

		assertEquals("testUserInit(): User.numEmail() returns incorrect value.", user.numEmail(), nEmails);
	}

	@Test(timeout=1000)
	public void testUserRetrieveEmails() {
        User user = new User("user", "password");

		int nEmails = 10;
		int i;
		for (i = 0 ; i < nEmails ; i++) {
			user.receiveEmail("sender"+String.format("%d", i), "MESSAGE "+String.format("%d", i));
		}

		Email[] emails = user.retrieveEmail(nEmails);

		// should not remove emails
		assertEquals("testUserInit(): User.numEmail() returns incorrect value.", user.numEmail(), nEmails);

        final String incorrectElementMessage = "testUserRetrieveEmails(): User.retrieveEmail() returns incorrect elements.";
		// check elements
		for (i = 0 ; i < nEmails ; i++) {
            String senderId = String.format("%d", nEmails - i - 1);

			assertEquals(incorrectElementMessage, "user", emails[i].getOwner());
			assertEquals(incorrectElementMessage, "sender" + senderId, emails[i].getSender());
			assertEquals(incorrectElementMessage, "MESSAGE " + senderId, emails[i].getMessage());
		}
	}

	@Test(timeout=1000)
	public void testUserRemoveEmails() {
		User user = new User("testuser1", "testpassword1");

		int nEmails = 10;
		for (int i = 0 ; i < nEmails ; i++) {
			user.receiveEmail("sender"+String.format("%d", i), "TEST MESSAGE "+String.format("%d", i));
		}

		Email[] emails = user.retrieveEmail(nEmails);

		// check elements
		long[] id = new long [nEmails];
		for (int i = 0 ; i < nEmails ; i++) {
			id[i] = emails[i].getID();
		}

		// get unduplicated test id
		long testId;
		boolean dup;
		do {
			dup = false;
			testId = (new Random()).nextLong();
			for (int i = 0 ; i < nEmails ; i++) {
				if (testId == id[i]) {
					dup = true;
				}
			}
		} while(dup);

		// test remove fail
		boolean ret = user.removeEmail(testId);
		assertFalse("testUserRemoveEmails(): User.removeEmail(id) returns incorrect value.", ret);

		// remove all
		for (int i = 0 ; i < nEmails; i++) {
			ret = user.removeEmail(id[i]);
			assertTrue("testUserRemoveEmails(): User.removeEmail(id) returns incorrect value.", ret);
			assertEquals("testUserRemoveEmails(): User.removeEmail(id) fails..", user.numEmail(), nEmails-1-i);
		}
	}


	/********************************************************************************************************
	 *
	 * EmailServer.parseRequest
	 *
	 ********************************************************************************************************/
	@Test(timeout=1000)
	public void testServerParseAddUserWrongFormat() {
		EmailServer server = new EmailServer();


		String retStr = server.parseRequest("ADD-USER\t42\tcs240\thereicome\tmoreparam\r\n");
		matchesErrorCode("testServerParseAddUserWrongFormat(): 'parseRequest'",
				retStr, ErrorFactory.FORMAT_COMMAND_ERROR);

		retStr = server.parseRequest("ADD-USER\r\n");
		matchesErrorCode("testServerParseAddUserWrongFormat(): 'parseRequest'",
				retStr, ErrorFactory.FORMAT_COMMAND_ERROR);

		retStr = server.parseRequest("ADD-USER\t42\r\n");
		matchesErrorCode("testServerParseAddUserWrongFormat(): 'parseRequest' incorrect error code.",
				retStr, ErrorFactory.FORMAT_COMMAND_ERROR);

		retStr = server.parseRequest("ADD-USER\troot\tcs180");

		matchesErrorCode("testServerParseAddUserWrongFormat() 'parseRequest'", retStr, ErrorFactory.FORMAT_COMMAND_ERROR);
	}


	@Test(timeout=1000)
	public void testServerParseGetAllUsersWrongFormat() {
		EmailServer server = new EmailServer();


		String retStr = server.parseRequest("GET-ALL-USERS\troot\tcs180\tmoreparam\r\n");
		matchesErrorCode("testServerParseGetAllUsersWrongFormat(): 'parseRequest'",
				retStr, ErrorFactory.FORMAT_COMMAND_ERROR);

		retStr = server.parseRequest("GET-ALL-USERS\r\n");
		matchesErrorCode("testServerParseGetAllUsersWrongFormat(): 'parseRequest'",
				retStr, ErrorFactory.FORMAT_COMMAND_ERROR);

		retStr = server.parseRequest("GET-ALL-USERS\troot\tcs190\tmoreparam\r\n");
		matchesErrorCode("testServerParseGetAllUsersWrongFormat(): 'parseRequest'",
				retStr, ErrorFactory.FORMAT_COMMAND_ERROR);

		retStr = server.parseRequest("GET-ALL-USERS\trootu\tcs180\tmoreparam\r\n");
		matchesErrorCode("testServerParseGetAllUsersWrongFormat(): 'parseRequest'",
				retStr, ErrorFactory.FORMAT_COMMAND_ERROR);

		retStr = server.parseRequest("GET-ALL-USERS\trootu\r\n");
		matchesErrorCode("testServerParseGetAllUsersWrongFormat(): 'parseRequest'",
				retStr, ErrorFactory.FORMAT_COMMAND_ERROR);

		retStr = server.parseRequest("GET-ALL-USERS\troot\tcs180");
		matchesErrorCode("testServerParseGetAllUsersWrongFormat(): 'parseRequest'",
				retStr, ErrorFactory.FORMAT_COMMAND_ERROR);
	}

	@Test(timeout=1000)
	public void testServerParseSendEmailWrongFormat() {
		EmailServer server = new EmailServer();

		String retStr = server.parseRequest("SEND-EMAIL\r\n");
		matchesErrorCode("testServerParseSendEmailWrongFormat(): 'parseRequest' incorrect error code.",
			retStr, ErrorFactory.FORMAT_COMMAND_ERROR);

		retStr = server.parseRequest("SEND-EMAIL\t42\r\n");
		matchesErrorCode("testServerParseSendEmailWrongFormat(): 'parseRequest' incorrect error code.",
				retStr, ErrorFactory.FORMAT_COMMAND_ERROR);

		retStr = server.parseRequest("SEND-EMAIL\t42\t4\tmoreparam\r\n");
		matchesErrorCode("testServerParseSendEmailWrongFormat(): 'parseRequest' incorrect error code.",
				retStr, ErrorFactory.FORMAT_COMMAND_ERROR);

		retStr = server.parseRequest("SEND-EMAIL\tuser1\tpassword1\tmoreparam\r\n");
		matchesErrorCode("testServerParseSendEmailWrongFormat(): 'parseRequest' incorrect error code.",
				retStr, ErrorFactory.FORMAT_COMMAND_ERROR);

		retStr = server.parseRequest("SEND-EMAIL\tuser1\tpassword1\tuser2\tmessage1");
		matchesErrorCode("testServerParseSendEmailWrongFormat(): 'parseRequest' incorrect error code.",
				retStr, ErrorFactory.FORMAT_COMMAND_ERROR);
	}

	@Test(timeout=1000)
	public void testServerParseGetEmailsWrongFormat() {
		EmailServer server = new EmailServer();

		String retStr = server.parseRequest("GET-EMAILS\t42\r\n");
		matchesErrorCode("testServerParseGetEmailsWrongFormat(): 'parseRequest'",
				retStr, ErrorFactory.FORMAT_COMMAND_ERROR);

		retStr = server.parseRequest("GET-EMAILS\r\n");
		matchesErrorCode("testServerParseGetEmailsWrongFormat(): 'parseRequest' ",
				retStr, ErrorFactory.FORMAT_COMMAND_ERROR);

		retStr = server.parseRequest("GET-EMAILS\troot\tcs180\tmoreparam");
		matchesErrorCode("testServerParseGetEmailsWrongFormat(): 'parseRequest' ",
				retStr, ErrorFactory.FORMAT_COMMAND_ERROR);
	}

	@Test(timeout=1000)
	public void testServerParseDeleteEmailsWrongFormat() {
		EmailServer server = new EmailServer();

		String retStr = server.parseRequest("DELETE-EMAIL\t42\r\n");
		matchesErrorCode("testServerParseDeleteEmailsWrongFormat(): 'parseRequest' incorrect error code.",
				retStr, ErrorFactory.FORMAT_COMMAND_ERROR);

		retStr = server.parseRequest("DELETE-EMAIL\r\n");
		matchesErrorCode("testServerParseDeleteEmailsWrongFormat(): 'parseRequest' incorrect error code.)",
				retStr, ErrorFactory.FORMAT_COMMAND_ERROR);

		retStr = server.parseRequest("DELETE-EMAIL\troot\tcs180\tmoreparam");
		matchesErrorCode("testServerParseDeleteEmailsWrongFormat(): 'parseRequest' incorrect error code.",
				retStr, ErrorFactory.FORMAT_COMMAND_ERROR);
	}

	@Test(timeout=1000)
	public void testInvalidCommand() {
		EmailServer server = new EmailServer();

		String retStr = server.parseRequest("GET-EMAIL\tparam1\tparam2\r\n");
		matchesErrorCode("testInvalidCommand(): 'parseRequest' ",
				retStr, ErrorFactory.UNKNOWN_COMMAND_ERROR);

		retStr = server.parseRequest("get-all-users\tparam1\tparam2\r\n");
		matchesErrorCode("testInvalidCommand(): 'parseRequest' ",
				retStr, ErrorFactory.UNKNOWN_COMMAND_ERROR);

		retStr = server.parseRequest("get-emails\tparam1\tparam2\r\n");
		matchesErrorCode("testInvalidCommand(): 'parseRequest' ",
				retStr, ErrorFactory.UNKNOWN_COMMAND_ERROR);

		retStr = server.parseRequest("hhheeeh\tparam1\tparam2\r\n");
		matchesErrorCode("testInvalidCommand(): 'parseRequest' ",
				retStr, ErrorFactory.UNKNOWN_COMMAND_ERROR);

		retStr = server.parseRequest("GETMESSAGES\tparam1\tparam2\r\n");
		matchesErrorCode("testInvalidCommand(): 'parseRequest' ",
				retStr, ErrorFactory.UNKNOWN_COMMAND_ERROR);

		retStr = server.parseRequest("123456\tparam1\tparam2\r\n");
		matchesErrorCode("testInvalidCommand(): 'parseRequest' ",
				retStr, ErrorFactory.UNKNOWN_COMMAND_ERROR);
	}

	@Test(timeout=1000)
	public void testInvalidValue() {
		EmailServer server = new EmailServer();

		//  name length = [1, 20]
		String retStr = server.parseRequest("ADD-USER\ta12345678901234567890\tabcf\r\n");
		matchesErrorCode("testInvalidValue(): 'parseRequest' ",
				retStr, ErrorFactory.INVALID_VALUE_ERROR);

		//  password length = [4, 40]
		retStr = server.parseRequest("ADD-USER\tuser1\tabc\r\n");
		matchesErrorCode("testInvalidValue(): 'parseRequest' ",
				retStr, ErrorFactory.INVALID_VALUE_ERROR);

		retStr = server.parseRequest("ADD-USER\tuser1\ta1234567890123456789012345678901234567890\r\n");
		matchesErrorCode("testInvalidValue(): 'parseRequest' ",
				retStr, ErrorFactory.INVALID_VALUE_ERROR);

		retStr = server.parseRequest("DELETE-EMAIL\troot\tcs180\t1234455\r\n");
		matchesErrorCode("testInvalidValue(): 'parseRequest' ",
				retStr, ErrorFactory.INVALID_VALUE_ERROR);
	}

	@Test(timeout=1000)
	public void testAuthError() {
		EmailServer server = new EmailServer();

		String[] authErrorStrings = new String[]{
				"GET-ALL-USERS\troot\tHello\r\n",
				"GET-EMAILS\troot\tHello\t10\r\n",
				"DELETE-EMAIL\troot\tHell\t12345o\r\n",
				"SEND-EMAIL\troot\tHello\tuser1\tabcsssss\r\n"
		};

		for(String request : authErrorStrings) {
			String response = server.parseRequest(request);
			matchesErrorCode("testAuthError() 'parseRequest'", response, ErrorFactory.AUTHENTICATION_ERROR);
		}
	}

	@Test(timeout=1000)
	public void testUserLookupError() {
		EmailServer server = new EmailServer();

		String[] authErrorStrings = new String[]{
				"SEND-EMAIL\troot\tcs180\tuser1\tabcsssss\r\n",
				"GET-ALL-USERS\tnouser\tHello\r\n",
				"DELETE-EMAIL\tnouser\tHello\t10000\r\n",
				"GET-EMAILS\tnouser\tHello\t10\r\n"
		};

		for(String request : authErrorStrings) {
			String response = server.parseRequest(request);
			matchesErrorCode("testUserLookupError(): 'parseRequest'",
					response,
					ErrorFactory.USERNAME_LOOKUP_ERROR);
		}
	}

	/********************************************************************************************************
	 *
	 * EmailServer.addUser
	 *
	 ********************************************************************************************************/
	@Test(timeout=10000)
	public void testAddUserNormal() {
		EmailServer server = new EmailServer();

		String retStr = server.addUser(new String[]{"ADD-USER", "user1", "cs240"});
		assertEquals(
				"testAddUserNormal(): 'addUser' doesn't return correct success message or didn't succeed when it should have.",
				SUCCESS, retStr);
	}

	@Test(timeout=1000)
	public void testAddUserExistError() {
		EmailServer server = new EmailServer();

		String retStr = server.parseRequest("ADD-USER\troot\tabcde\r\n");
		matchesErrorCode("testAddUserExistError(): 'parseRequest' trying to add 'root' ",
				retStr,
				ErrorFactory.USER_EXIST_ERROR);

		server.parseRequest("ADD-USER\tuser1\tabcde\r\n");
		retStr = server.parseRequest("ADD-USER\tuser1\taaaa\r\n");
		System.out.println(retStr);

		matchesErrorCode("testAddUserExistError(): 'parseRequest'",
                retStr,
                ErrorFactory.USER_EXIST_ERROR);
	}

	@Test(timeout=1000)
	public void testAddUserInvalidUsername() {
		EmailServer server = new EmailServer();
		String[] errorStrings = new String[]{
				"ADD-USER\ta12345678901234567890\tabcf\r\n",  // name too long
				"ADD-USER\taaa-bbb-cccc\tabcd\r\n",	// invalid chars
				"ADD-USER\taaa+bbb+ee\tabcd\r\n",
				"ADD-USER\t(aaabbb)\tabcd\r\n",
				"ADD-USER\t/aaabbb\tabcd\r\n"
		};

		for(String request : errorStrings) {
			String response = server.parseRequest(request);
			matchesErrorCode("testAddUserInvalidUsername(): 'parseRequest'",
					response,
					ErrorFactory.INVALID_VALUE_ERROR);
		}
	}

	@Test(timeout=1000)
	public void testAddUserInvalidPassword() {
		EmailServer server = new EmailServer();
		String[] errorStrings = new String[]{
				"ADD-USER\tuser1\tabc\r\n",  // too short
				"ADD-USER\tuser2\ta1234567890123456789012345678901234567890\r\n", // too long
				"ADD-USER\tuser4\taaa-bbb\r\n",	// invalid chars
				"ADD-USER\tuser5\taaa=bbb\r\n",
				"ADD-USER\tuser6\t(aabb)\r\n",
				"ADD-USER\tuser7\taaa/bbb\r\n"
		};

		for(String request : errorStrings) {
			String response = server.parseRequest(request);
			matchesErrorCode("testAddUserInvalidPassword(): in 'parseRequest' check for passwords that are too short/long/invalid",
					response,
					ErrorFactory.INVALID_VALUE_ERROR);
		}
	}

	/********************************************************************************************************
	 *
	 * EmailServer.deleteUser
	 *
	 ********************************************************************************************************/
	@Test(timeout=1000)
	public void testDeleteUserNormal() {
		EmailServer server = new EmailServer();

		server.addUser(new String[]{"ADD-USER", "usertest", "cs240"});
		String retStr = server.deleteUser(new String[]{"DELETE-USER", "usertest", "cs240"});
		assertEquals(
				"testDeleteUserNormal(): 'deleteUser' doesn't return correct success message or didn't succeed when it should have.",
				SUCCESS, retStr);
	}


	@Test(timeout=1000)
	public void testDeleteUserRoot() {
		EmailServer server = new EmailServer();

		String retStr = server.deleteUser(new String[]{"DELETE-USER", "root", "cs180"});
		matchesErrorCode("testDeleteUserRoot(): 'parseRequest' ",
                retStr, ErrorFactory.INVALID_VALUE_ERROR);
	}

	@Test(timeout=1000)
	public void testDeleteUserNotExist() {
		EmailServer server = new EmailServer();

		String retStr = server.parseRequest("DELETE-USER\tuser1\tabcde\r\n");
		matchesErrorCode("testDeleteUserNotExist(): 'parseRequest' ",
				retStr, ErrorFactory.USERNAME_LOOKUP_ERROR);
	}



	/********************************************************************************************************
	 *
	 * EmailServer.getAllUsers
	 *
	 ********************************************************************************************************/
	@Test(timeout=1000)
	public void testGetAllUsers() {
		EmailServer server = new EmailServer();

		String retStr = server.getAllUsers(new String[] { "GET-ALL-USERS", "root", "cs180" });
		String ta = "SUCCESS\troot\r\n";
		assertEquals("testGetAllUsers(): 'GET-ALL-USERS' doesn't return correct success message or didn't succeed when it should have.",
				     ta, retStr);

		server.addUser(new String[] { "ADD-USER", "33", "cs240" });
		retStr = server.getAllUsers(new String[] { "GET-ALL-USERS", "root", "cs180" });

		String ans1 = "SUCCESS\troot\t33\r\n";
		String ans2 = "SUCCESS\t33\troot\r\n";
		boolean test = retStr.equals(ans1) || retStr.equals(ans2);
		assertTrue("testGetAllUsers(): 'GET-ALL-USERS' doesn't return correct success message or didn't succeed when it should have.",
				test);
	}


	/********************************************************************************************************
	 *
	 * EmailServer.sendEmail
	 *
	 ********************************************************************************************************/
	@Test(timeout=1000)
	public void testSendEmail() {
		EmailServer server = new EmailServer();

		String retStr = server.sendEmail(new String[]{"SEND-EMAIL", "root", "cs180", "root", "message1"});
		assertEquals("testSendEmail(): 'SEND-EMAIL' doesn't return correct success message or didn't succeed when it should have.",
				SUCCESS, retStr);
	}

	@Test(timeout=1000)
	public void testSendEmailUserLookupError() {
		EmailServer server = new EmailServer();

		String retStr = server.sendEmail(new String[]{"SEND-EMAIL", "root", "cs180", "user1", "message1"});
		matchesErrorCode("testSendEmailUserLookupError(): in your 'sendEmail()' ",
				retStr, ErrorFactory.USERNAME_LOOKUP_ERROR);
	}


	/********************************************************************************************************
	 *
	 * EmailServer.getEmails
	 *
	 ********************************************************************************************************/
	@Test(timeout=1000)
	public void testGetEmails() {
		EmailServer server = new EmailServer();

		server.addUser(new String[] { "ADD-USER", "user1", "cs240" });
		String retStr = server.getEmails(new String[]{"GET-EMAILS", "user1", "cs240", "1"});
		assertEquals("testGetEmails(): 'GET-EMAILS' doesn't return correct success message or didn't succeed when it should have.",
				SUCCESS, retStr);

		// exist 1 get 1
		server.sendEmail(new String[] { "GET-EMAILS", "root", "cs180", "user1", "message1" });
		retStr = server.getEmails(new String[] { "GET-EMAILS", "user1", "cs240", "1" });
		retStr = retStr.replaceAll("\r", "");
		retStr = retStr.replaceAll("\n", "");

		int i, j;
		String[] splittedStr;
		String[] splittedEmailStr;

		splittedStr = retStr.split("\t");
		assertEquals("testGetEmails(): 'GET-EMAILS' doesn't return correct success message or didn't succeed when it should have.",
			     "SUCCESS", splittedStr[0]);

		for (i = 1, j = splittedStr.length-1 ; i < splittedStr.length ; i++, j--) {
			splittedEmailStr = splittedStr[i].split(";");
			assertEquals("tesGetEmails(): 'GET-EMAILS' doesn't return correct success message or didn't succeed when it should have.",
 				     String.format(" From: root \"message%d\"", j), splittedEmailStr[2]);
		}

		// exist 1 get 10
		retStr = server.getEmails(new String[]{"GET-EMAILS", "user1", "cs240", "10" });
		splittedStr = retStr.trim().split("\t");
		assertEquals("tesGetEmails(): 'GET-EMAILS' doesn't return correct success message or didn't succeed when it should have.",
			     "SUCCESS", splittedStr[0]);

		for (i = 1, j = splittedStr.length-1 ; i < splittedStr.length ; i++, j--) {
			splittedEmailStr = splittedStr[i].split(";");
			assertEquals("tesGetEmails(): 'GET-EMAILS' doesn't return correct success message or didn't succeed when it should have.",
				     String.format(" From: root \"message%d\"", j), splittedEmailStr[2]);
		}

		// exist 2 get 1
		server.sendEmail(new String[] { "GET-EMAILS", "root", "cs180", "user1", "message2" });
		retStr = server.getEmails(new String[] { "GET-EMAILS", "user1", "cs240", "1" });
        splittedStr = retStr.trim().split("\t");
		assertEquals("tesGetEmails(): 'GET-EMAILS' doesn't return correct success message or didn't succeed when it should have.",
			     "SUCCESS", splittedStr[0]);

		for (i = 1, j = splittedStr.length-1 ; i < splittedStr.length ; i++, j--) {
			splittedEmailStr = splittedStr[i].split(";");
			assertEquals("tesGetEmails(): 'GET-EMAILS' doesn't return correct success message or didn't succeed when it should have.",
				     	" From: root \"message2\"", splittedEmailStr[2]);
		}

		// exist 2 get 10
		retStr = server.getEmails(new String[] { "GET-EMAILS", "user1", "cs240", "10" });
		splittedStr = retStr.trim().split("\t");
		assertEquals("tesGetEmails(): 'GET-EMAILS' doesn't return correct success message or didn't succeed when it should have.",
			     "SUCCESS", splittedStr[0]);

		for (i = 1, j = splittedStr.length-1 ; i < splittedStr.length ; i++, j--) {
			splittedEmailStr = splittedStr[i].split(";");
			assertEquals("tesGetEmails(): 'GET-EMAILS' doesn't return correct success message or didn't succeed when it should have.",
				     String.format(" From: root \"message%d\"", j), splittedEmailStr[2]);
		}
	}

	/********************************************************************************************************
	 *
	 * EmailServer.deleteEmail
	 *
	 ********************************************************************************************************/
	@Test(timeout=1000)
	public void testDeleteEmails() {
		EmailServer server = new EmailServer();

		server.addUser(new String[] { "ADD-USER", "user1", "cs240" });
		server.sendEmail(new String[] { "SEND-EMAIL", "root", "cs180", "user1", "message1" });
		String retStr = server.getEmails(new String[]{"GET-EMAILS", "user1", "cs240", "1"});
		retStr = retStr.trim();

		String[] response = retStr.trim().split("\t");
		assertEquals("The message box should have one email: ", 1, response.length - 1);
		String[] email = response[1].split(";");

		int i, j;
		String[] splittedStr;
		String[] splittedEmailStr;
		long id;

		splittedStr = retStr.split("\t");
		for (i = 1, j = splittedStr.length-1 ; i < splittedStr.length ; i++, j--) {

			splittedEmailStr = splittedStr[i].split(";");
			id = Long.parseLong(splittedEmailStr[0]);

			retStr = server.deleteEmail(new String[] { "DELETE-EMAIL", "user1", "cs240", String.format("%d", id) });
			assertEquals("testDeleteEmails(): 'DELETE-EMAIL' doesn't return correct success message or didn't succeed when it should have.",
					SUCCESS, retStr);
		}
	}

	@Test(timeout=1000)
	public void testDeleteEmailsInvalidID() {
		EmailServer server = new EmailServer();

        String result = server.deleteEmail(new String[]{"DELETE-EMAIL", "root", "cs180", "1"});

		matchesErrorCode("testDeleteEmailsInvalidID(): in your 'deleteEmail()' ",
				result, ErrorFactory.INVALID_VALUE_ERROR);
	}


/*
	*//********************************************************************************************************
	 *
	 * File I/O
	 *
	 ********************************************************************************************************//*
	@Test(timeout = 5000)
	public void testFileIOConstructorNoFile() throws InterruptedException {

		EmailServer server = null;
		try {
			server = new EmailServer(TEST_CSV);
		} catch (IOException e) {
			assertFalse("IOException when constructing EmailServer(): "+e.getMessage(), true);
		}

		String response = server.getAllUsers(new String[]{"GET-ALL-USERS", "root", "cs180"});
		String expected = "SUCCESS\troot\r\n";

		assertEquals("The server didn't read users from file. (Error may due to 'getAllUser')", expected, response);
		assertTrue("Server didn't create new file if none given.", new File(TEST_CSV).exists());
	}

    private void createTestUserFile(String contents) {
        File file = new File(TEST_CSV);

        try{
            file.createNewFile();
            PrintWriter out = new PrintWriter(file);
            out.println(contents);
            out.close();
        } catch (IOException e) {
            // Shouldn't happen
        }
    }

    private String readTestUserFile() {
        try {
            Scanner scan = new Scanner(new File(TEST_CSV));
            String response = scan.useDelimiter("\\Z").next();
            return response;
        } catch (FileNotFoundException e) {
            // Shouldn't happen
            assertNull(e);
        }

        return null;
    }

	@Test(timeout = 5000)
	public void testFileIOConstructorNominal() throws InterruptedException {

        createTestUserFile("joseph,joseph\nvarun,varun\ngray,gray\n");

		EmailServer serv = null;
		try {
			serv = new EmailServer(TEST_CSV);
		} catch (IOException e) {
			assertFalse("EmailServer can not be instantiated. EmailServer(String), file exists.", true);
		}

		String response = serv.getAllUsers(new String[]{"GET-ALL-USERS", "root", "cs180"});
		String expected = "SUCCESS\troot\tjoseph\tvarun\tgray\r\n";

		assertEquals("The server didn't read users from file correctly. (Error may due to 'getAllUser')", expected, response);
	}


	@Test(timeout = 5000)
	public void testFileIOConstructorInvalidLines() throws InterruptedException {

        createTestUserFile("joseph,joseph\ngreg,greg\nvarun,varun\nhaaaa\ngray,gray\n");

		EmailServer serv = null;
		try {
			serv = new EmailServer(TEST_CSV);
		} catch (IOException e) {
			assertFalse("EmailServer can not be instantiated. EmailServer(String), file exists.", true);
		}

		String response = serv.getAllUsers(new String[]{"GET-ALL-USERS", "root", "cs180"});
		String expected = "SUCCESS\troot\tjoseph\tgreg\tvarun\tgray\r\n";

		assertEquals("The server didn't read users from file correctly, the file contains invalid lines. (Error may due to 'getAllUser')", expected, response);
	}

	@Test(timeout = 5000)
	public void testFileIOAddUser() throws InterruptedException {
        createTestUserFile("joseph,joseph\nvarun,varun\n");


        EmailServer serv = null;
		try {
			serv = new EmailServer(TEST_CSV);
		} catch (IOException e) {
			assertFalse("EmailServer can not be instantiated. EmailServer(String), file exists.", true);
		}

		String response = serv.addUser(new String[]{"ADD-USER", "gray", "hello"});

		assertEquals("EmailServer: 'addUser' failed when it shouldn't have.", SUCCESS, response);


        String contents = readTestUserFile().trim();
        String expected = "joseph,joseph\nvarun,varun\ngray,hello";
        assertEquals("EmailServer: 'addUser' didn't add the user to the file correctly.", expected, contents);
	}

	@Test(timeout = 5000)
	public void testFileIODeleteUser() throws InterruptedException {
        createTestUserFile("joseph,joseph\nvarun,varun\n");

		EmailServer serv = null;
		try {
			serv = new EmailServer(TEST_CSV);
		} catch (IOException e) {
			assertFalse("EmailServer can not be instantiated. EmailServer(String), file exists.", true);
		}

		String response = serv.deleteUser(new String[]{"DELETE-USER", "varun", "varun"});

		assertEquals("EmailServer: 'deleteUser' failed when it shouldn't have.", SUCCESS, response);

        String contents = readTestUserFile().trim();
        String expected = "joseph,joseph";
        assertEquals("EmailServer: 'deleteUser' didn't add the user to the file correctly.", expected, contents);
	}*/
}

