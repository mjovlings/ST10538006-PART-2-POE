package com.mycompany.main;

/** Registration and authentication extracted from the supplied Main.java. */
public class Login {
    public static final String USER_ERROR = "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.";
    public static final String PASSWORD_ERROR = "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
    private String registeredUsername;
    private String registeredPassword;
    private String firstName;
    private String lastName;
    private boolean loggedIn;

    public static boolean checkUserName(String username) {
        return username != null && username.length() <= 5 && username.contains("_");
    }

    public static boolean checkPasswordComplexity(String password) {
        return password != null && password.length() >= 8
                && password.matches(".*[A-Z].*") && password.matches(".*[0-9].*")
                && password.matches(".*[^a-zA-Z0-9\\s].*");
    }

    public static boolean checkCellPhoneNumber(String number) {
        // Retains the supplied South African rule. +27 replaces the local leading 0.
                return number != null && number.matches("^\\+27[0-9]{9}$");
    }

    public String registerUser(String first, String last, String username,
                               String password, String phone) {
        if (!checkUserName(username)) return USER_ERROR;
        if (!checkPasswordComplexity(password)) return PASSWORD_ERROR;
        if (!checkCellPhoneNumber(phone)) return Message.RECIPIENT_ERROR;
        firstName = first;
        lastName = last;
        registeredUsername = username;
        registeredPassword = password;
        loggedIn = false;
        return "User registered successfully.";
    }

    public boolean loginUser(String username, String password) {
        loggedIn = registeredUsername != null && registeredUsername.equals(username)
                && registeredPassword.equals(password);
        return loggedIn;
    }

    public String returnLoginStatus(boolean successful) {
        return successful && loggedIn
                ? "Welcome " + firstName + ", " + lastName + " it is great to see you again."
                : "Username or password incorrect, please try again.";
    }

    public boolean isLoggedIn() { return loggedIn; }
}
