package com.mycompany.main;

import com.google.gson.JsonElement;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/** Part 2 operations and sent-message history for one authenticated session. */
public class Message {
    public static final String RECIPIENT_OK = "Cell phone number successfully captured.";
    public static final String RECIPIENT_ERROR = "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
    private final Login login;
    private final MessageStore store;
    private final SecureRandom random = new SecureRandom();
    private final Set<String> usedIds = new HashSet<>();
    private final List<MessageData> sentMessages = new ArrayList<>();

    public Message(Login login, MessageStore store) throws IOException {
        this.login = login;
        this.store = store;
        // Also avoid IDs already present in the stored-message file.
        for (JsonElement item : store.read()) {
            if (item.isJsonObject() && item.getAsJsonObject().has("messageId")) {
                usedIds.add(item.getAsJsonObject().get("messageId").getAsString());
            }
        }
    }

    public boolean checkMessageID(String id) {
        return id != null && id.matches("[0-9]{10}");
    }

    public String checkRecipientCell(String recipient) {
        return Login.checkCellPhoneNumber(recipient) ? RECIPIENT_OK : RECIPIENT_ERROR;
    }

    public String checkMessageLength(String text) {
        if (text == null || text.isBlank()) return "Please enter a non-empty message.";
        if (text.length() > 250) {
            return "Message exceeds 250 characters by " + (text.length() - 250)
                    + "; please reduce the size.";
        }
        return "Message ready to send.";
    }

    public String generateMessageID() {
        String id;
        do {
            StringBuilder digits = new StringBuilder();
            // A loop generates all ten random digits, preserving leading zeroes.
            for (int digit = 0; digit < 10; digit++) digits.append(random.nextInt(10));
            id = digits.toString().substring(0, 10);
        } while (!usedIds.add(id));
        return id;
    }

    public String createMessageHash(String id, int number, String text) {
        if (!checkMessageID(id) || number < 0 || text == null || text.isBlank()) {
            throw new IllegalArgumentException("A ten-digit ID, non-negative number and message are required.");
        }
        // Remove punctuation to reproduce HITONIGHT, not HITONIGHT?.
        String wordsOnly = text.replaceAll("[^\\p{L}\\p{N}\\s]", "").trim();
        String[] words = wordsOnly.isEmpty() ? new String[]{""} : wordsOnly.split("\\s+");
        return (id.substring(0, 2) + ":" + number + ":" + words[0]
                + words[words.length - 1]).toUpperCase(Locale.ROOT);
    }

    public MessageData createMessage(int number, String recipient, String text) {
        requireLogin();
        if (!"Message ready to send.".equals(checkMessageLength(text))) {
            throw new IllegalArgumentException(checkMessageLength(text));
        }
        String id = generateMessageID();
        return new MessageData(id, number, recipient, text, createMessageHash(id, number, text));
    }

    // Capitalisation and the spelling returnTotalMessagess follow the brief.
    public String SentMessage(MessageData message, int choice) throws IOException {
        requireLogin();
        if (!"Pending".equals(message.getStatus())) return "This message has already been processed.";
        if (choice == 2) return "Press 0 to delete the message.";
        if (choice != 1 && choice != 3) return "Invalid option. Please choose 1, 2 or 3.";
        if (!Login.checkCellPhoneNumber(message.getRecipient())) return RECIPIENT_ERROR;
        String validation = checkMessageLength(message.getMessage());
        if (!"Message ready to send.".equals(validation)) return validation;
        if (choice == 3) {
            storeMessage(message);
            return "Message successfully stored.";
        }
        message.setStatus("Sent");
        sentMessages.add(message);
        return "Message successfully sent.";
    }

    public String disregardMessage(MessageData message, int confirmation) {
        requireLogin();
        if (!"Pending".equals(message.getStatus())) return "This message has already been processed.";
        if (confirmation != 0) return "Deletion cancelled.";
        message.setStatus("Disregarded");
        return "Message disregarded.";
    }

    public void storeMessage(MessageData message) throws IOException {
        requireLogin();
        if (!"Pending".equals(message.getStatus())
                || !Login.checkCellPhoneNumber(message.getRecipient())
                || !"Message ready to send.".equals(checkMessageLength(message.getMessage()))) {
            throw new IllegalArgumentException("Only a valid pending message can be stored.");
        }
        store.append(message); // Change status only after the write succeeds.
        message.setStatus("Stored");
    }

    public String printMessages() {
        StringBuilder report = new StringBuilder();
        for (MessageData message : sentMessages) {
            if (report.length() > 0) report.append("\n\n");
            report.append(message.details());
        }
        return report.length() == 0 ? "No messages sent." : report.toString();
    }

    public int returnTotalMessagess() { return sentMessages.size(); }

    private void requireLogin() {
        if (!login.isLoggedIn()) throw new IllegalStateException("Please log in before sending messages.");
    }
}
