package org.shadow.lib.cryptography;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.InvalidParameterException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test class for the SecretKeyTools utility class.
 *
 * This class contains unit tests for the methods in the SecretKeyTools class
 * that handles the calculation of charset sizes and password entropy. The tests
 * include various scenarios to validate the correctness of these methods.
 */

public class SecretKeyToolsTest {

    private static final SecretKeyTools Tools = new SecretKeyTools();
    private static Method calculateCharsetSize;
    private static int lowercaseCharsetSize;
    private static int uppercaseCharsetSize;
    private static int digitsCharsetSize;
    private static int specialCharsetSize;

    /**
     * Initializes the static fields and methods for use in unit tests.
     *
     * This method gains access to private static final fields in the Tools class and retrieves their values.
     * It also retrieves a reference to the private calculateCharsetSize method for reflective invocation in tests.
     *
     * @throws NoSuchMethodException if the calculateCharsetSize method is not found.
     * @throws NoSuchFieldException if any of the charset size fields are not found.
     * @throws IllegalAccessException if access to the fields or methods is denied.
     */

    @BeforeAll
    public static void init() throws NoSuchMethodException, NoSuchFieldException, IllegalAccessException {
        // Get the values of the static private final properties.
        Field lowercaseCharsetSizeField =  Tools.getClass().getDeclaredField("lowercaseCharsetSize");
        lowercaseCharsetSizeField.setAccessible(true);
        Field uppercaseCharsetSizeField = Tools.getClass().getDeclaredField("uppercaseCharsetSize");
        uppercaseCharsetSizeField.setAccessible(true);
        Field digitsCharsetSizeField = Tools.getClass().getDeclaredField("digitsCharsetSize");
        digitsCharsetSizeField.setAccessible(true);
        Field specialCharsetSizeField = Tools.getClass().getDeclaredField("specialCharsetSize");
        specialCharsetSizeField.setAccessible(true);

        lowercaseCharsetSize = (int) lowercaseCharsetSizeField.get(null);
        uppercaseCharsetSize = (int) uppercaseCharsetSizeField.get(null);
        digitsCharsetSize = (int) digitsCharsetSizeField.get(null);
        specialCharsetSize = (int) specialCharsetSizeField.get(null);

        // Get handlers on private methods.
        calculateCharsetSize = Tools.getClass().getDeclaredMethod("calculateCharsetSize", String.class);
        calculateCharsetSize.setAccessible(true);
    }

    /**
     * Unit test for the calculateCharsetSize method in the SecretKeyTools class.
     *
     * This test checks the calculateCharsetSize method by verifying its output
     * for various input strings. The method is invoked reflectively to handle
     * its private access modifier. The tests cover different types of character
     * sets: lowercase letters, uppercase letters, digits, and special characters.
     */

    @Test
    public void testCalculateCharsetSize() {
        try {
            assertEquals(lowercaseCharsetSize, (int) calculateCharsetSize.invoke(Tools, "abc"));
            assertEquals(uppercaseCharsetSize, (int) calculateCharsetSize.invoke(Tools, "ABC"));
            assertEquals(digitsCharsetSize, (int) calculateCharsetSize.invoke(Tools, "123"));
            assertEquals(specialCharsetSize, (int) calculateCharsetSize.invoke(Tools, "@!<"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Unit tests for the calculateEntropy method in the SecretKeyTools class.
     * This method calculates the entropy of a given password which is a measure of its strength.
     */

    @Test
    public void testCalculateEntropy_emptyPassword() {
        String password = "";
        double expectedEntropy = 0.0;
        double actualEntropy = SecretKeyTools.calculateEntropy(password);
        assertEquals(expectedEntropy, actualEntropy);
    }

    @Test
    public void testCalculateEntropy_onlyLowerCase() {
        String password = "lowercase";
        double expectedEntropy = password.length() * Math.log(26) / Math.log(2);
        double actualEntropy = SecretKeyTools.calculateEntropy(password);
        assertEquals(expectedEntropy, actualEntropy);
    }

    @Test
    public void testCalculateEntropy_onlyUpperCase() {
        String password = "UPPERCASE";
        double expectedEntropy = password.length() * (Math.log(26) / Math.log(2));
        double actualEntropy = SecretKeyTools.calculateEntropy(password);
        assertEquals(expectedEntropy, actualEntropy);
    }

    @Test
    public void testCalculateEntropy_onlyDigits() {
        String password = "123456";
        double expectedEntropy = password.length() * (Math.log(10) / Math.log(2));
        double actualEntropy = SecretKeyTools.calculateEntropy(password);
        assertEquals(expectedEntropy, actualEntropy);
    }

    @Test
    public void testCalculateEntropy_onlySpecialCharacters() {
        String password = "!@#$%^";
        double expectedEntropy = password.length() * (Math.log(32) / Math.log(2));
        double actualEntropy = SecretKeyTools.calculateEntropy(password);
        assertEquals(expectedEntropy, actualEntropy);
    }

    @Test
    public void testCalculateEntropy_mixedCharacters() {
        String password = "Mix3d@";
        double expectedEntropy = password.length() * Math.log(lowercaseCharsetSize + uppercaseCharsetSize + digitsCharsetSize + specialCharsetSize) / Math.log(2);
        double actualEntropy = SecretKeyTools.calculateEntropy(password);
        assertEquals(expectedEntropy, actualEntropy);
    }

    @Test
    public void testCalculateEntropy_invalidCharacters() {
        String password = "Invalid\u0019";
        assertThrows(InvalidParameterException.class, () -> SecretKeyTools.calculateEntropy(password));
    }
}