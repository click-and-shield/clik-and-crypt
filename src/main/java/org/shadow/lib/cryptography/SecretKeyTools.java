package org.shadow.lib.cryptography;

import java.security.InvalidParameterException;

/**
 * A utility class for performing operations related to password strength and entropy calculations.
 */

public class SecretKeyTools {
    private static final int lowercaseCharsetSize = 26;
    private static final int uppercaseCharsetSize = 26;
    private static final int digitsCharsetSize = 10;
    private static final int specialCharsetSize = 32;
    public enum Strength { LOW, MEDIUM, HIGH, VERY_HIGH };

    /**
     * Calculates the entropy of a given password, which is a measure of its strength.
     *
     * @param password The password whose entropy is to be calculated.
     * @return The calculated entropy of the password. If the password does not contain any valid characters, returns 0.
     */

    public static double calculateEntropy(String password) {
        int passwordLength = password.length();
        int charsetSize = calculateCharsetSize(password);

        if (charsetSize == 0) {
            return 0;
        }

        // Maximum value for `charsetSize`: 26 + 26 + 10 + 32 = 94
        // Maximum value for `Math.log(charsetSize) / Math.log(2)`: log(94) / log(3) = 6.55

        return passwordLength * (Math.log(charsetSize) / Math.log(2));
    }

    /**
     * Calculates the strength of the provided password based on its entropy.
     *
     * @param password The password for which the strength is to be calculated.
     * @return The strength of the password.
     */

    public static Strength calculateStrength(String password) {
        double entropy = calculateEntropy(password);
        return calculateStrength(entropy);
    }

    /**
     * Evaluates the strength level of a password based on its entropy value.
     *
     * @param entropy The entropy value of the password, which is a measure of its unpredictability and complexity.
     * @return The strength of the password categorized as LOW, MEDIUM, HIGH, or VERY_HIGH.
     */

    public static Strength calculateStrength(double entropy) {
        if (entropy < 40) { return Strength.LOW; }
        if (entropy < 60) { return Strength.MEDIUM; }
        if (entropy < 80) { return Strength.HIGH; }
        return Strength.VERY_HIGH;
    }

    /**
     * Calculates the size of the character set used in the provided password.
     * The character set size is determined based on the presence of lowercase letters,
     * uppercase letters, digits, and special characters in the password.
     *
     * @param password The password whose character set size is to be calculated.
     * @return The size of the character set used in the password.
     * @throws InvalidParameterException If the password contains an invalid character.
     */

    private static int calculateCharsetSize(String password) throws InvalidParameterException {
        boolean hasLower = false;
        boolean hasUpper = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isLowerCase(c)) {
                hasLower = true;
            } else if (Character.isUpperCase(c)) {
                hasUpper = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (Character.isDefined(c) && ! Character.isISOControl(c)) {
                hasSpecial = true;
            } else {
                throw new InvalidParameterException(String.format("Invalid character (code %d) in password", (int)c));
            }
        }

        int charsetSize = 0; // maximum: 26+26+10+32
        if (hasLower) {
            charsetSize += lowercaseCharsetSize; // number of uppercase letters
        }
        if (hasUpper) {
            charsetSize += uppercaseCharsetSize; // number of lowercase letters
        }
        if (hasDigit) {
            charsetSize += digitsCharsetSize; // number of numbers
        }
        if (hasSpecial) {
            charsetSize += specialCharsetSize; // number of special characters
        }

        return charsetSize;
    }
}
