package com.mycompany.main;

import static org.junit.jupiter.api.Assertions.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Scanner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Drives the real console flow through scripted input without a GUI. */
class MainTest {
    @TempDir Path folder;
    private static final String REGISTER = "Kyle\nSmith\nkyl_1\nCh&&sec@ke99!\n+27838968976\n";
    private static final String LOGIN = "kyl_1\nCh&&sec@ke99!\n";

    private String run(String input) {
        ByteArrayOutputStream capture = new ByteArrayOutputStream();
        new Main(new Scanner(input), new PrintStream(capture, true, StandardCharsets.UTF_8),
                folder.resolve("stored_messages.json")).run();
        return capture.toString(StandardCharsets.UTF_8);
    }
    @Test void welcomeMenuComingSoonAndQuit() {
        String output = run(REGISTER + LOGIN + "0\n2\n3\n");
        assertTrue(output.contains("Welcome to QuickChat."));
        assertTrue(output.contains("1) Send Messages\n2) Show recently sent messages\n3) Quit"));
        assertTrue(output.contains("Coming Soon."));
        assertTrue(output.contains("Total messages sent: 0"));
        assertTrue(output.contains("Goodbye."));
    }
    @Test void failedLoginCannotReachMessaging() {
        String output = run(REGISTER + "kyl_1\nwrong\n/quit\n");
        assertTrue(output.contains("Username or password incorrect, please try again."));
        assertFalse(output.contains("Welcome to QuickChat."));
        assertFalse(output.contains("Send Messages"));
    }
    @Test void failedLoginCanRetrySuccessfully() {
        String output = run(REGISTER + "kyl_1\nwrong\n" + LOGIN + "0\n3\n");
        assertTrue(output.contains("Welcome to QuickChat."));
    }
    @Test void invalidNumbersAreReprompted() {
        String output = run(REGISTER + LOGIN + "abc\n-1\n9999999999999\n0\nx\n4\n3\n");
        assertTrue(output.contains("Please enter a whole number from 0 to 2147483647."));
        assertTrue(output.contains("Please enter a whole number from 1 to 3."));
        assertTrue(output.contains("Goodbye."));
    }
    @Test void requiredTwoMessagesAndNoExtraMessages() {
        String output = run(REGISTER + LOGIN + "2\n1\n+27718693002\nHi Mike, can you join us for dinner tonight?\n1\n08575975889\nHi Keegan, did you receive the payment?\n2\n0\n1\n2\n3\n");
        assertTrue(output.contains("Message number: 0"));
        assertTrue(output.contains("Message number: 1"));
        assertFalse(output.contains("Message number: 2"));
        assertTrue(output.contains("Message ID generated: "));
        assertTrue(output.contains("Message successfully sent."));
        assertTrue(output.contains("Press 0 to delete the message."));
        assertTrue(output.contains("Total messages sent: 1"));
        assertTrue(output.contains("You have reached your message limit."));
        assertTrue(output.contains("Coming Soon."));
    }
    @Test void zeroMessageLimitAndEarlyQuitWork() {
        String output = run(REGISTER + LOGIN + "0\n1\n3\n");
        assertFalse(output.contains("Message number: 0"));
        assertTrue(output.contains("You have reached your message limit."));
        assertFalse(run(REGISTER + LOGIN + "2\n3\n").contains("Message number: 0"));
    }
    @Test void invalidTextAndRecipientCanBeCorrected() {
        String output = run(REGISTER + LOGIN + "1\n1\n08575975889\n" + "x".repeat(251)
                + "\nHi Mike\n1\n+27718693002\n3\n");
        assertTrue(output.contains("Message exceeds 250 characters by 1; please reduce the size."));
        assertTrue(output.contains(Message.RECIPIENT_ERROR));
        assertTrue(output.contains("Message successfully sent."));
        assertTrue(output.contains("Total messages sent: 1"));
    }
    @Test void storeChoiceWritesFile() throws Exception {
        String output = run(REGISTER + LOGIN + "1\n1\n+27718693002\nStore this please\n3\n3\n");
        assertTrue(output.contains("Message successfully stored."));
        assertTrue(output.contains("Total messages sent: 0"));
        assertEquals(1, new MessageStore(folder.resolve("stored_messages.json")).read().size());
    }
    @Test void closedInputExitsCleanly() {
        assertTrue(run(REGISTER + LOGIN + "1\n").contains("Input closed. Goodbye."));
    }
}
