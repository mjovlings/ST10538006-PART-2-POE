package com.mycompany.main;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/** Part 1 regression tests protecting the login gate required by Part 2. */
class LoginTest {
    @Test void usernameRules() {
        assertTrue(Login.checkUserName("kyl_1"));
        assertTrue(Login.checkUserName("a_"));
        assertFalse(Login.checkUserName("kyle!!!!!!!"));
        assertFalse(Login.checkUserName("abcdef_"));
        assertFalse(Login.checkUserName(null));
    }
    @Test void passwordRules() {
        assertTrue(Login.checkPasswordComplexity("Ch&&sec@ke99!"));
        assertTrue(Login.checkPasswordComplexity("ABCDEF1!"));
        for (String password : new String[]{"password", "Ab1!", "abcdef1!", "ABCDEFG!", "Abcdefg1", "Abcdef1 "}) {
            assertFalse(Login.checkPasswordComplexity(password), password);
        }
        assertFalse(Login.checkPasswordComplexity(null));
    }
    @Test void cellphoneRules() {
        assertTrue(Login.checkCellPhoneNumber("+27838968976"));
        for (String phone : new String[]{"08966553", "08575975889", "+2783896897", "+278389689760", "+2783896897A"}) {
            assertFalse(Login.checkCellPhoneNumber(phone), phone);
        }
        assertFalse(Login.checkCellPhoneNumber(null));
    }
    @Test void successfulAndFailedLogin() {
        Login login = new Login();
        assertFalse(login.loginUser("kyl_1", "Ch&&sec@ke99!"));
        assertEquals("User registered successfully.", login.registerUser("Kyle", "Smith", "kyl_1", "Ch&&sec@ke99!", "+27838968976"));
        assertTrue(login.loginUser("kyl_1", "Ch&&sec@ke99!"));
        assertEquals("Welcome Kyle, Smith it is great to see you again.", login.returnLoginStatus(true));
        assertFalse(login.loginUser("kyl_1", "wrong"));
        assertFalse(login.isLoggedIn());
        assertEquals("Username or password incorrect, please try again.", login.returnLoginStatus(false));
    }
    @Test void invalidRegistrationDoesNotCreateAccount() {
        Login login = new Login();
        assertEquals(Login.USER_ERROR, login.registerUser("A", "B", "toolong_", "ABCDEF1!", "+27718693002"));
        assertEquals(Login.PASSWORD_ERROR, login.registerUser("A", "B", "a_", "weak", "+27718693002"));
        assertEquals(Message.RECIPIENT_ERROR, login.registerUser("A", "B", "a_", "ABCDEF1!", "08575975889"));
        assertFalse(login.loginUser("a_", "ABCDEF1!"));
    }
}
