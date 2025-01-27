package org.shadow.lib.cryptography;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

/**
 * The AES256 class provides methods for generating a key from a password and salt,
 * and generating an initialization vector (IV) for AES 256 encryption.
 */

public class AES256 {

    /**
     * Generates a SecretKey from a given password and salt using PBKDF2 with HmacSHA256 algorithm.
     *
     * @param password the password to be used for key generation.
     * @param salt the salt to be used for key generation.
     * @return the generated SecretKey for AES encryption.
     * @throws RuntimeException if a NoSuchAlgorithmException or InvalidKeySpecException occurs.
     */

    protected static SecretKey generateKeyFromPassword(String password, byte[] salt) throws RuntimeException {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            return new SecretKeySpec(tmp.getEncoded(), "AES");
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(String.format("An unexpected exception occurred (%s: %s)", e.getClass().getName(), e.getMessage()));
        }
    }

    /**
     * Generates a secure random initialization vector (IV) for AES encryption.
     *
     * @return a byte array containing the generated IV.
     */

    protected static byte[] generateIV() {
        byte[] iv = new byte[16];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);
        return iv;
    }
}
