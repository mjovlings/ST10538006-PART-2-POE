package com.mycompany.main;

import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Required assertion data from PROG.pdf pages 14-16 plus boundary cases. */
class MessageTest {
    @TempDir Path folder;
    private Login login;
    private Message messages;
    private MessageStore store;
    private static final String DINNER = "Hi Mike, can you join us for dinner tonight?";
    private static final String PAYMENT = "Hi Keegan, did you receive the payment?";

    @BeforeEach void setUp() throws IOException {
        login = new Login();
        login.registerUser("Kyle", "Smith", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        login.loginUser("kyl_1", "Ch&&sec@ke99!");
        store = new MessageStore(folder.resolve("stored_messages.json"));
        messages = new Message(login, store);
    }
    @Test void acceptsMessageAt250CharacterBoundary() {
        assertEquals("Message ready to send.", messages.checkMessageLength(DINNER));
        assertEquals("Message ready to send.", messages.checkMessageLength("x".repeat(250)));
    }
    @Test void reportsExactExcessLength() {
        assertEquals("Message exceeds 250 characters by 1; please reduce the size.", messages.checkMessageLength("x".repeat(251)));
        assertEquals("Message exceeds 250 characters by 20; please reduce the size.", messages.checkMessageLength("x".repeat(270)));
    }
    @Test void rejectsBlankAndNullMessages() {
        assertEquals("Please enter a non-empty message.", messages.checkMessageLength("  "));
        assertEquals("Please enter a non-empty message.", messages.checkMessageLength(null));
        assertThrows(IllegalArgumentException.class, () -> messages.createMessage(0, "+27718693002", ""));
    }
    @Test void exactRecipientResponses() {
        assertEquals("Cell phone number successfully captured.", messages.checkRecipientCell("+27718693002"));
        assertEquals("Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.", messages.checkRecipientCell("08575975889"));
    }
    @Test void exactRequiredHashWithDeterministicId() {
        assertEquals("00:0:HITONIGHT", messages.createMessageHash("0012345678", 0, DINNER));
    }
    @Test void remainingHashesTestedInLoop() {
        String[] texts = {DINNER, PAYMENT, "Hello", "  Hello    world!  "};
        String[] expected = {"12:0:HITONIGHT", "12:1:HIPAYMENT", "12:2:HELLOHELLO", "12:3:HELLOWORLD"};
        for (int i = 0; i < texts.length; i++) {
            assertEquals(expected[i], messages.createMessageHash("1234567890", i, texts[i]));
        }
    }
    @Test void idValidation() {
        assertTrue(messages.checkMessageID("0012345678"));
        assertFalse(messages.checkMessageID("12345678901"));
        assertFalse(messages.checkMessageID("123"));
        assertFalse(messages.checkMessageID("abcdefghij"));
        assertFalse(messages.checkMessageID(null));
    }
    @Test void generatedIdsAreUniqueTenDigitStrings() {
        Set<String> ids = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            MessageData message = messages.createMessage(i, "+27718693002", DINNER);
            assertTrue(messages.checkMessageID(message.getMessageId()));
            assertTrue(ids.add(message.getMessageId()));
            assertEquals(i, message.getMessageNumber());
            assertEquals(message.getMessageId().substring(0, 2) + ":" + i + ":HITONIGHT", message.getMessageHash());
        }
    }
    @Test void suppliedTwoMessageScenarioCountsOnlySentMessage() throws IOException {
        MessageData first = messages.createMessage(0, "+27718693002", DINNER);
        MessageData second = messages.createMessage(1, "08575975889", PAYMENT);
        assertEquals("Message successfully sent.", messages.SentMessage(first, 1));
        assertEquals("Press 0 to delete the message.", messages.SentMessage(second, 2));
        assertEquals("Message disregarded.", messages.disregardMessage(second, 0));
        assertEquals(1, messages.returnTotalMessagess());
        assertEquals(first.details(), messages.printMessages());
        assertFalse(messages.printMessages().contains(PAYMENT));
        assertEquals(0, store.read().size());
    }
    @Test void sentDetailsHaveRequiredOrder() throws IOException {
        MessageData message = messages.createMessage(0, "+27718693002", DINNER);
        messages.SentMessage(message, 1);
        assertEquals("Message ID: " + message.getMessageId() + "\nMessage Hash: "
                + message.getMessageHash() + "\nRecipient: +27718693002\nMessage: " + DINNER, messages.printMessages());
    }
    @Test void printMessagesIncludesEverySentMessage() throws IOException {
        MessageData first = messages.createMessage(0, "+27718693002", DINNER);
        MessageData second = messages.createMessage(1, "+27838968976", PAYMENT);
        messages.SentMessage(first, 1);
        messages.SentMessage(second, 1);
        assertEquals(first.details() + "\n\n" + second.details(), messages.printMessages());
        assertEquals(2, messages.returnTotalMessagess());
    }
    @Test void emptyHistory() {
        assertEquals(0, messages.returnTotalMessagess());
        assertEquals("No messages sent.", messages.printMessages());
    }
    @Test void storeReturnsExactResponseAndDoesNotIncreaseSentCount() throws IOException {
        MessageData message = messages.createMessage(0, "+27718693002", DINNER);
        assertEquals("Message successfully stored.", messages.SentMessage(message, 3));
        assertEquals(0, messages.returnTotalMessagess());
        assertEquals("Stored", message.getStatus());
        assertEquals(1, store.read().size());
        assertEquals(DINNER, store.read().get(0).getAsJsonObject().get("message").getAsString());
    }
    @Test void jsonPreservesEarlierMessagesAcrossSessionsAndEscapesText() throws IOException {
        String special = "Hi \"Mike\", path C:\\notes\nThanks café";
        MessageData first = messages.createMessage(0, "+27718693002", special);
        messages.storeMessage(first);
        Message nextSession = new Message(login, store);
        nextSession.storeMessage(nextSession.createMessage(0, "+27838968976", PAYMENT));
        assertEquals(2, store.read().size());
        var saved = store.read().get(0).getAsJsonObject();
        assertEquals(special, saved.get("message").getAsString());
        assertEquals(first.getMessageId(), saved.get("messageId").getAsString());
        assertEquals(first.getMessageHash(), saved.get("messageHash").getAsString());
        assertEquals(0, saved.get("messageNumber").getAsInt());
        assertEquals("+27718693002", saved.get("recipient").getAsString());
        assertEquals("Stored", saved.get("status").getAsString());
    }
    @Test void cannotSendOrStoreInvalidRecipient() throws IOException {
        MessageData message = messages.createMessage(0, "08575975889", PAYMENT);
        assertEquals(Message.RECIPIENT_ERROR, messages.SentMessage(message, 1));
        assertEquals(Message.RECIPIENT_ERROR, messages.SentMessage(message, 3));
        assertEquals("Pending", message.getStatus());
        assertEquals(0, messages.returnTotalMessagess());
        assertEquals(0, store.read().size());
    }
    @Test void duplicateSendDoesNotDoubleCount() throws IOException {
        MessageData message = messages.createMessage(0, "+27718693002", DINNER);
        messages.SentMessage(message, 1);
        assertEquals("This message has already been processed.", messages.SentMessage(message, 1));
        assertEquals(1, messages.returnTotalMessagess());
    }
    @Test void invalidActionAndCancelledDeletionKeepPending() throws IOException {
        MessageData message = messages.createMessage(0, "+27718693002", DINNER);
        assertEquals("Invalid option. Please choose 1, 2 or 3.", messages.SentMessage(message, 9));
        assertEquals("Deletion cancelled.", messages.disregardMessage(message, 1));
        assertEquals("Pending", message.getStatus());
        assertEquals(0, messages.returnTotalMessagess());
    }
    @Test void unauthenticatedOperationsAreBlocked() {
        login.loginUser("kyl_1", "wrong");
        assertThrows(IllegalStateException.class, () -> messages.createMessage(0, "+27718693002", DINNER));
    }
    @Test void authenticationIsRecheckedBeforeSendingOrStoring() {
        MessageData message = messages.createMessage(0, "+27718693002", DINNER);
        login.loginUser("kyl_1", "wrong");
        assertThrows(IllegalStateException.class, () -> messages.SentMessage(message, 1));
        assertThrows(IllegalStateException.class, () -> messages.storeMessage(message));
    }
    @Test void storageFailureLeavesPendingAndPreservesBadFile() throws IOException {
        MessageData message = messages.createMessage(0, "+27718693002", DINNER);
        Path file = folder.resolve("stored_messages.json");
        Files.writeString(file, "{broken");
        assertThrows(IOException.class, () -> messages.SentMessage(message, 3));
        assertEquals("Pending", message.getStatus());
        assertEquals("{broken", Files.readString(file));
        assertEquals(0, messages.returnTotalMessagess());
    }
    @Test void jsonObjectRootIsRejected() throws IOException {
        Files.writeString(folder.resolve("stored_messages.json"), "{}");
        assertThrows(IOException.class, store::read);
    }
}
