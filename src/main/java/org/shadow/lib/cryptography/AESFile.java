package org.shadow.lib.cryptography;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.UnexpectedException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * The AESFile class provides functionality for encrypting and decrypting files
 * using the AES 256 algorithm in CBC mode.
 */

public class AESFile extends AES256 implements AutoCloseable {
    private static final long bufferLength = 4096L;
    private final Cipher cipher;
    private final String secretKey;
    private final FileInputStream inputStream;
    private final FileOutputStream outputStream;
    private CipherOutputStream cipherOutputStream;
    private boolean started = false;

    /**
     * Constructor for creating an instance of AESFile which handles encryption and decryption operations
     * on a specified input file and writes to a specified output file.
     *
     * @param secretKey the secret key used for AES encryption.
     * @param inputPath the file path of the input file to be encrypted or decrypted.
     * @param outputPath the file path of the output file where the encrypted or decrypted data will be stored.
     * @throws RuntimeException if the input or output file specified does not exist.
     */

    public AESFile(String secretKey, String inputPath, String outputPath) throws RuntimeException {
        this.secretKey = secretKey;

        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("AES algorithm is not available");
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException("PKCS5Padding algorithm is not available");
        }

        try {
            this.inputStream = new FileInputStream(inputPath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(String.format("The input file \"%s\" does not exist", inputPath));
        }

        try {
            this.outputStream = new FileOutputStream(outputPath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(String.format("The output file \"%s\" does not exist", outputPath));
        }
    }

    /**
     * Closes the AESFile instance by terminating the processing and releasing all
     * associated resources such as input and output streams.
     *
     * @throws IOException if an I/O error occurs while closing the streams.
     */

    @Override
    public void close() throws IOException {
        this.cipherOutputStream.close();
        this.inputStream.close();
        this.outputStream.close();
    }

    /**
     * Calculates the number of iterations needed based on the file size and buffer length.
     *
     * @return the number of iterations required to process the entire input file.
     * @throws IOException if an I/O error occurs, including if the file size cannot be determined.
     */

    public long calculateIterationNumber() throws IOException {
        final long fileSize = inputStream.getChannel().size();
        return fileSize / bufferLength + (fileSize % bufferLength == 0 ? 0 : 1);
    }

    /**
     * Encrypts a chunk of data from the input stream using AES encryption and writes the encrypted output.
     *
     * This method processes the input file in chunks. When a chunk is processed, it returns an indication
     * whether there are more chunks to be processed. This allows the method to be used in an iterative context
     * to monitor the encryption progress.
     *
     * @return {@code true} if a chunk was successfully encrypted and there are more chunks to process;
     *         {@code false} if the end of the input stream is reached or an error occurs in the encryption process.
     * @throws RuntimeException if an unexpected exception occurs during encryption.
     */

    public boolean encryptChunk() throws RuntimeException {
        try {
            if (! started) {
                final byte[] ivBytes = generateIV();
                final SecretKey key = generateKeyFromPassword(secretKey, ivBytes);
                final IvParameterSpec ivParam = new IvParameterSpec(ivBytes);
                cipher.init(Cipher.ENCRYPT_MODE, key, ivParam);
                this.cipherOutputStream = new CipherOutputStream(outputStream, cipher);
                this.outputStream.write(ivParam.getIV());
                started = true;
            }
            boolean continueProcess = process();
            if (! continueProcess) close();
            return continueProcess;
        } catch(IOException e) {
            throw new RuntimeException(String.format("An error occurred while encrypting the file: %s", e.getMessage()));
        }catch (InvalidAlgorithmParameterException | InvalidKeyException e) {
            throw new RuntimeException(String.format("An unexpected exception occurred while encrypting the file (%s: %s)", e.getClass().getName(), e.getMessage()));
        }
    }

    /**
     * Decrypts a chunk of data from the input stream using AES algorithm and writes the decrypted output.
     *
     * This method processes the input file in chunks. Each time it processes a chunk, it returns.
     * The returned value indicates whether there are more chunks to process. This allows the function to be
     * used in the context of an application that must inform the caller about the task's progress.
     *
     * @return {@code true} if a chunk was successfully decrypted and there are more chunks to process;
     *         {@code false} if the end of the input stream is reached or there was an error in the decryption process.
     * @throws RuntimeException if an unexpected exception occurs during decryption.
     */

    public boolean decryptChunk() throws RuntimeException {
        try {
            if (! started) {
                byte[] ivBytes = inputStream.readNBytes(16);
                IvParameterSpec ivParam = new IvParameterSpec(ivBytes);
                // Generate the secret key.
                SecretKey secretKey = generateKeyFromPassword(this.secretKey, ivBytes);
                // Initialise the cypher.
                cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParam);
                this.cipherOutputStream = new CipherOutputStream(outputStream, cipher);
                started = true;
            }
            boolean continueProcess = process();
            if (! continueProcess) close();
            return continueProcess;
        } catch (IOException a) {
            throw new RuntimeException("An error occurred while decrypting the file");
        }catch (InvalidAlgorithmParameterException | InvalidKeyException e) {
            throw new RuntimeException(String.format("An unexpected exception occurred while decrypting the file (%s: %s)", e.getClass().getName(), e.getMessage()));
        }
    }

    /**
     * Encrypts the entire input file and writes the decrypted data to the output file.
     *
     * @throws Exception if an unexpected error occurs during encryption or while closing the streams.
     */

    public void encrypt() throws Exception {
        boolean progress = true;
        while (progress) { progress = encryptChunk(); }
        close();
    }

    /**
     * Decrypts the entire input file and writes the decrypted data to the output file.
     *
     * @throws Exception if an unexpected error occurs during decryption or while closing the streams.
     */

    public void decrypt() throws Exception {
        boolean progress = true;
        while (progress) { progress = decryptChunk(); }
        close();
    }

    /**
     * Processes a chunk of data from the input stream and writes it to the cipher output stream.
     *
     * This method reads data from the input stream into a buffer and writes the buffer contents
     * to the cipher output stream. It returns a boolean indicating whether more data remains
     * to be processed.
     *
     * @return {@code true} if a chunk was successfully read and written, and there are more chunks
     *         to process; {@code false} if the end of the input stream is reached or an error occurs
     *         during reading or writing.
     * @throws IOException if an I/O error occurs during reading from the input stream or writing
     *         to the cipher output stream.
     */

    private boolean process() throws IOException {
        byte[] buffer = new byte[(int) bufferLength];
        int bytesRead = inputStream.read(buffer);
        if (bytesRead == -1) {
            return false; // we've reached the end of the input file
        }
        cipherOutputStream.write(buffer, 0, bytesRead);
        return true;
    }
}
