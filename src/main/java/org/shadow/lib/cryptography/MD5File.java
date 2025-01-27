package org.shadow.lib.cryptography;

import org.shadow.lib.exception.CompileTimeError;

import java.io.FileInputStream;
import java.rmi.UnexpectedException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5File extends DigestFile {

    /**
     * Initializes a new instance of the MD5File class with the specified file path.
     * Sets up the input stream from the file and prepares the MessageDigest instance for MD5.
     *
     * @param path The path to the input file to be processed.
     * @throws NoSuchAlgorithmException If the MD5 algorithm is not available.
     * @throws RuntimeException If the specified file does not exist or cannot be opened.
     */

    public MD5File(String path) throws NoSuchAlgorithmException, RuntimeException {
        super(path, MessageDigest.getInstance("MD5"));
    }

    /**
     * Initializes a new instance of the MD5File class with the specified file path and chunk length.
     * Sets up the input stream from the file, prepares the MessageDigest instance for MD5, and configures the chunk size for processing.
     *
     * @param path The path to the input file to be processed.
     * @param chunkLength The length of the chunks that the file will be divided into for processing.
     * @throws NoSuchAlgorithmException If the MD5 algorithm is not available.
     * @throws RuntimeException If the specified file does not exist or cannot be opened.
     * @throws UnexpectedException If an unexpected error occurs during initialization.
     */

    public MD5File(String path, long chunkLength) throws NoSuchAlgorithmException, RuntimeException, UnexpectedException {
        super(path, MessageDigest.getInstance("MD5"), chunkLength);
    }

    /**
     * Initializes a new instance of the MD5File class with the specified input stream and message digest.
     *
     * @param inputStream The FileInputStream instance to be used for reading the file's content.
     * @param md The MessageDigest instance to be used for calculating the file's digest.
     */

    public MD5File(FileInputStream inputStream, MessageDigest md) {
        super(inputStream, md);
    }

    /**
     * Initializes a new instance of the MD5File class with the specified input stream, message digest, and chunk length.
     *
     * @param inputStream The FileInputStream instance to be used for reading the file's content.
     * @param md The MessageDigest instance to be used for calculating the file's digest.
     * @param chunkLength The length of the chunks that the file will be divided into for processing.
     */

    public MD5File(FileInputStream inputStream, MessageDigest md, long chunkLength) {
        super(inputStream, md, chunkLength);
    }

    /**
     * Generates and returns the current digest as a hexadecimal string.
     *
     * @return the MD5 digest of the input data as a 32-character hexadecimal string.
     */

    public String getDigestAsHex() {
        return String.format("%032x", new java.math.BigInteger(1, md.digest()));
    }

    public static int getDigestLength() throws RuntimeException {
        try {
            return MessageDigest.getInstance("MD5").getDigestLength();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm is not available");
        }

    }
}
