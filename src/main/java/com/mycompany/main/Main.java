package com.mycompany.main;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.Scanner;

/** Console entry point extending the user's Part 1 registration/login flow. */
public class Main {
    private final Scanner input;
    private final PrintStream output;
    private final Login login = new Login();
    private final Path storagePath;

    public Main(Scanner input, PrintStream output, Path storagePath) {
        this.input = input;
        this.output = output;
        this.storagePath = storagePath;
    }

    public static void main(String[] args) {
        new Main(new Scanner(System.in), System.out, Path.of("stored_messages.json")).run();
    }

    public void run() {
        try {
            registerUser();
            while (!login.isLoggedIn()) {
                String username = read("Enter your username to login (or /quit): ");
                if (username.equals("/quit")) return;
                String password = read("Enter your password to login: ");
                output.println(login.returnLoginStatus(login.loginUser(username, password)));
            }
            runChat();
        } catch (NoSuchElementException ex) {
            output.println("Input closed. Goodbye.");
        } catch (IOException ex) {
            output.println("Unable to open message storage: " + ex.getMessage());
        }
    }

    private void registerUser() {
        output.println("QuickChat registration");
        String first = readNonBlank("Enter your first name: ");
        String last = readNonBlank("Enter your last name: ");
        String username = read("Enter your username: ");
        while (!Login.checkUserName(username)) {
            output.println(Login.USER_ERROR);
            username = read("Enter your username: ");
        }
        output.println("Username successfully captured.");
        String password = read("Enter your password: ");
        while (!Login.checkPasswordComplexity(password)) {
            output.println(Login.PASSWORD_ERROR);
            password = read("Enter your password: ");
        }
        output.println("Password successfully captured.");
        String phone = read("Enter your cellphone number (+27): ");
        while (!Login.checkCellPhoneNumber(phone)) {
            output.println(Message.RECIPIENT_ERROR);
            phone = read("Enter your cellphone number (+27): ");
        }
        output.println("Cell phone number successfully added.");
        output.println(login.registerUser(first, last, username, password, phone));
    }

    private void runChat() throws IOException {
        Message messages = new Message(login, new MessageStore(storagePath));
        output.println("Welcome to QuickChat.");
        int messageLimit = readNumber("How many messages would you like to enter? ", 0, Integer.MAX_VALUE);
        int entered = 0;
        boolean running = true;
        // This while loop keeps the menu running until option 3 is selected.
        while (running) {
            output.println("\n1) Send Messages\n2) Show recently sent messages\n3) Quit");
            int choice = readNumber("Choose an option: ", 1, 3);
            switch (choice) {
                case 1:
                    if (entered == messageLimit) {
                        output.println("You have reached your message limit.");
                    } else {
                        // The counter supplies message numbers 0, 1, 2 ... exactly once.
                        for (int messageNumber = entered; messageNumber < messageLimit; messageNumber++) {
                            enterMessage(messages, messageNumber);
                            entered++;
                        }
                    }
                    output.println("Total messages sent: " + messages.returnTotalMessagess());
                    break;
                case 2:
                    output.println("Coming Soon.");
                    break;
                case 3:
                    output.println("Total messages sent: " + messages.returnTotalMessagess());
                    output.println("Goodbye.");
                    running = false;
                    break;
                default:
                    break;
            }
        }
    }

    private void enterMessage(Message messages, int number) {
        output.println("\nMessage number: " + number);
        String recipient = read("Enter recipient number (+27): ");
        output.println(messages.checkRecipientCell(recipient));
        String text = read("Enter message (maximum 250 characters): ");
        while (!"Message ready to send.".equals(messages.checkMessageLength(text))) {
            output.println(messages.checkMessageLength(text));
            text = read("Enter message (maximum 250 characters): ");
        }
        output.println(messages.checkMessageLength(text));
        MessageData message = messages.createMessage(number, recipient, text);
        output.println("Message ID generated: " + message.getMessageId());
        while (message.getStatus().equals("Pending")) {
            output.println("1) Send Message\n2) Disregard Message\n3) Store Message to send later");
            int action = readNumber("Choose an action: ", 1, 3);
            // An invalid recipient may be discarded but cannot be sent/stored.
            if (action != 2 && !Login.checkCellPhoneNumber(message.getRecipient())) {
                output.println(Message.RECIPIENT_ERROR);
                message.setRecipient(read("Enter a valid recipient (+27), or choose disregard next: "));
                if (!Login.checkCellPhoneNumber(message.getRecipient())) continue;
                output.println(Message.RECIPIENT_OK);
            }
            try {
                output.println(messages.SentMessage(message, action));
                if (action == 1) output.println(message.details());
                if (action == 2) {
                    int confirm = readNumber("Enter 0 to delete, or 1 to keep editing: ", 0, 1);
                    output.println(messages.disregardMessage(message, confirm));
                }
            } catch (IOException ex) {
                output.println("Message was not stored: " + ex.getMessage());
                output.println("Please retry, send or disregard this message.");
            }
        }
    }

    private String read(String prompt) {
        output.print(prompt);
        return input.nextLine();
    }

    private String readNonBlank(String prompt) {
        String value = read(prompt).trim();
        while (value.isEmpty()) value = read(prompt).trim();
        return value;
    }

    private int readNumber(String prompt, int minimum, int maximum) {
        while (true) {
            try {
                int number = Integer.parseInt(read(prompt).trim());
                if (number >= minimum && number <= maximum) return number;
            } catch (NumberFormatException ex) {
                // Read a whole line again; invalid input never remains in Scanner.
            }
            output.println("Please enter a whole number from " + minimum + " to " + maximum + ".");
        }
    }
}
