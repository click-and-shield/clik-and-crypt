package org.shadow.lib.cryptography;

import org.jetbrains.annotations.Nullable;
import org.shadow.lib.exception.FatalRuntimeException;
import org.shadow.lib.file.FileManip;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * SkrivaCypherV1 is a utility class for encrypting and decrypting files using AES encryption and validating
 * file integrity through MD5 checksums. It supports updating progress through a TaskUpdater interface.
 */

public class SkrivaCypherV1 implements SkrivaCypher {

    /**
     * Creates a temporary file with the prefix "skriva-tmp" and suffix ".tmp".
     *
     * @return the path of the created temporary file as a String.
     * @throws RuntimeException if an I/O error occurs while creating the temporary file.
     */

    private String createTemporaryPath() throws RuntimeException {
        try {
            Path tempFilePath = Files.createTempFile("skriva-tmp", ".tmp");
            return String.valueOf(tempFilePath);
        } catch (IOException e) {
            throw new RuntimeException(String.format("cannot create temporary file: \"%s\n", e.getMessage()));
        }
    }

    /**
     * Calculates the MD5 checksum of a file specified by the given path and writes the result to the provided destination array.
     * Optionally updates progress information through the provided TaskUpdater.
     *
     * @param path The path to the file for which the MD5 checksum is to be calculated.
     * @param destination A byte array where the calculated MD5 checksum will be stored.
     * @param taskUpdater An optional TaskUpdater instance for reporting progress. Can be null.
     * @throws FatalRuntimeException If an error occurs during the MD5 calculation, or if the MD5 algorithm is not available.
     */

    private void calculateMd5Checksum(String path, byte[] destination, @Nullable TaskUpdater taskUpdater) throws FatalRuntimeException {
        try (MD5File md5 = new MD5File(String.valueOf(path))) {
            final long total = md5.calculateIterationNumber();
            boolean progress = true;
            long current = 0;

            while (progress) {
                progress = md5.digestChunk();
                current++;
                if (null != taskUpdater) taskUpdater.update(current, total);
            }
            if (null != taskUpdater) taskUpdater.update(0, total);
            md5.getDigestAsBytes(destination);
        } catch (NoSuchAlgorithmException e) {
            throw new FatalRuntimeException("the MD5 algorithm is not available!");
        } catch (DigestException | IOException e) {
            throw new FatalRuntimeException("an unexpected error occurred while calculating the MD5 digest of the decrypted file");
        }
    }

    /**
     * Appends the MD5 checksum of the specified input file to the end of a temporary file
     * and returns the path to this temporary file. Optionally updates progress using the provided TaskUpdater.
     *
     * This method performs the following operations:
     * (1) It calculates the MD5 checksum of the input file (intended for encryption).
     * (2) It creates a temporary file that is a copy of the input file (intended for encryption).
     * (3) It appends the previously calculated MD5 checksum to the end of the temporary file.
     *
     * @param inputFile The path to the file for which the MD5 checksum is to be calculated and appended.
     * @param taskUpdater An optional TaskUpdater instance for reporting progress, can be null.
     * @return The path to the temporary file with the appended MD5 checksum.
     * @throws FatalRuntimeException If an error occurs during file copying, checksum calculation, or file writing operations.
     */

    private String appendChecksum(String inputFile, @Nullable TaskUpdater taskUpdater) throws FatalRuntimeException {
        final String tempFilePath = createTemporaryPath();

        // Calculate the MD5 checksum of the input file (intended for encryption).
        final long digestLength = MD5File.getDigestLength();
        final byte[] actualDigest = new byte[(int) digestLength];
        calculateMd5Checksum(inputFile, actualDigest, taskUpdater);

        // Create the temporary file that is a copy of the input file (to be encrypted).
        try {
            Files.copy(Paths.get(inputFile), Paths.get(tempFilePath), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new FatalRuntimeException("an error occurred while encrypting the file. The input file has not been modified",
                    String.format("cannot copy the input file \"%s\" to \"%s\": %s", inputFile, tempFilePath, e.getMessage()));
        }

        // Append the previously calculated MD5 checksum to the end of the temporary file.
        try (FileOutputStream fos = new FileOutputStream(tempFilePath, true)) {
            fos.write(actualDigest);
            fos.flush();
        } catch (IOException e) {
            FileManip.DeleteIfExists(tempFilePath);
            throw new FatalRuntimeException("an error occurred while encrypting the file. The input file has not been modified",
                    String.format("cannot append MD5 digest to the temporary file \"%s\"", tempFilePath));
        }

        return tempFilePath;
    }

    /**
     * Encrypts a given file using AES encryption and writes the encrypted content to an output file.
     * The encryption process can optionally update progress information through the provided TaskUpdater.
     *
     * @param path The path to the input file that needs to be encrypted.
     * @param secretKey The secret key to be used for AES encryption.
     * @param outputFile The path to the output file where the encrypted content will be written.
     * @param taskUpdater An optional TaskUpdater instance for reporting progress. Can be null.
     * @throws FatalRuntimeException If an error occurs during the encryption process, such as issues with reading the
     *         file or writing the encrypted content.
     */

    private void Encrypt(String path, String secretKey, String outputFile, @Nullable TaskUpdater taskUpdater) throws FatalRuntimeException {
        try (AESFile aes = new AESFile(secretKey, path, outputFile)) {
            final long total = aes.calculateIterationNumber();
            boolean progress = true;
            long current = 0;

            while (progress) {
                progress = aes.encryptChunk();
                current++;
                if (null != taskUpdater) taskUpdater.update(current, total);
            }
        } catch (IOException e) {
            throw new FatalRuntimeException(String.format("an error occurred while encrypting file \"%s\" to \"%s\n", path, outputFile),
                    Arrays.toString(e.getStackTrace()));
        }
    }

    /**
     * Extracts the expected MD5 checksum from a given file and stores it in the destination byte array.
     * This method also adjusts the length of the file, effectively removing the checksum portion.
     *
     * @param path The path to the file from which the MD5 checksum needs to be extracted.
     * @param destination A byte array where the extracted MD5 checksum will be stored.
     * @throws FatalRuntimeException If the input file is not encrypted, is corrupted, or any other unexpected runtime error occurs.
     */

    private void ExtractExpectedChecksum(String path, byte[] destination) throws FatalRuntimeException {
        // Extract the expected MD5 digest from the temporary file (that contains the decrypted file).
        try (RandomAccessFile raf = new RandomAccessFile(String.valueOf(path), "rw")) {
            final long fileLength = raf.length();
            final long originalFileLength = fileLength - destination.length; // the length of the original file

            if (originalFileLength < 0) {
                throw new FatalRuntimeException(String.format("the input file \"%s\" is not encrypted or is corrupted - its length of too short", path),
                        String.format("length: %d vs %d", fileLength, originalFileLength));
            }

            // Extract the expected MD5 digest from the temporary file.
            raf.seek(originalFileLength);
            raf.read(destination);

            // Remove the bytes that represents the MD5 digest from the end of the temporary file.
            raf.seek(0);
            raf.setLength(originalFileLength);
        } catch (IOException e) {
            throw new FatalRuntimeException(String.format("an error occurred while reading from or writing to file \"%s\"", path),
                    Arrays.toString(e.getStackTrace()));
        }
    }

    /**
     * Decrypts an encrypted input file using AES decryption, with the decrypted content written to a temporary file.
     * This method can optionally update a progress indicator through the provided TaskUpdater.
     *
     * @param inputFile The path to the encrypted input file.
     * @param secretKey The secret key used for AES decryption.
     * @param taskUpdater An optional TaskUpdater instance for reporting progress, can be null.
     * @return The path of the temporary file containing the decrypted content.
     * @throws FatalRuntimeException If an error occurs during the decryption process, such as issues with reading
     *         the file, or writing the decrypted data.
     */

    private String Decrypt(String inputFile, String secretKey, @Nullable TaskUpdater taskUpdater) throws FatalRuntimeException {
        final String tempFilePath = createTemporaryPath();

        // Decrypt the input file. The result is written into the previously created temporary file.
        try (AESFile aes = new AESFile(secretKey, inputFile, tempFilePath)) {
            final long total = aes.calculateIterationNumber();
            boolean progress = true;
            long current = 0;

            while (progress) {
                progress = aes.decryptChunk();
                current++;
                if (null != taskUpdater) taskUpdater.update(current, total);
            }

            // Reset the progress bar.
            if (null != taskUpdater) taskUpdater.update(0, total);
        } catch (IOException e) {
            FileManip.DeleteIfExists(tempFilePath);
            throw new FatalRuntimeException(String.format("an error occurred while decrypting file \"%s\" to \"%s\n", inputFile, tempFilePath),
                    Arrays.toString(e.getStackTrace()));
        }
        return tempFilePath;
    }

    /**
     * Encrypts a file by first appending its MD5 checksum and then using AES encryption, saving the result to the specified output file.
     *
     * This method performs the following steps:
     * 1. Appends the MD5 checksum of the input file to the end of the file.
     * 2. Encrypts the resulting file with the appended checksum.
     *
     * @param inputFile The path to the input file to be encrypted.
     * @param outputFile The path to the output file where the encrypted result will be saved.
     * @param secretKey The secret key used for AES encryption.
     * @param taskUpdater An optional TaskUpdater instance for reporting progress, can be null.
     * @throws FatalRuntimeException If an error occurs during the encryption process or while appending the checksum to the input file.
     */

    public void EncryptFile(String inputFile, String outputFile, String secretKey, @Nullable TaskUpdater taskUpdater) throws FatalRuntimeException {
        Encrypt(appendChecksum(inputFile, taskUpdater), secretKey, outputFile, taskUpdater);
    }

    /**
     * Decrypts an encrypted file, verifies its integrity using an MD5 checksum,
     * and moves the decrypted content to the specified output file.
     *
     * This method performs the following steps:
     * 1. Decrypts the input file, resulting in a temporary decrypted file.
     * 2. Extracts the expected MD5 checksum from the end of the decrypted file. Please note that this action
     *    reduces the length of the decrypted file.
     * 3. Calculates the actual MD5 checksum of the decrypted file.
     * 4. Compares the extracted and calculated MD5 checksums to verify the file's integrity.
     * 5. If the checksums do not match, a RuntimeException is thrown indicating possible corruption or invalid secret key.
     * 6. If the checksums match, moves the decrypted file to the configured output path.
     *
     * @param inputFile The path to the encrypted input file.
     * @param outputFile The path where the decrypted output file will be saved.
     * @param secretKey The secret key used for decryption.
     * @param taskUpdater An optional TaskUpdater instance for reporting progress, can be null.
     * @throws FatalRuntimeException If the decryption fails, the file is corrupted, or if the key is invalid.
     */

    public void DecryptFile(String inputFile, String outputFile, String secretKey, @Nullable TaskUpdater taskUpdater) throws FatalRuntimeException {
        final long digestLength = MD5File.getDigestLength();
        final byte[] expectedDigest = new byte[(int) digestLength];
        final byte[] actualDigest = new byte[(int) digestLength];

        // Decrypt the input file. The result is a temporary file.
        String tempFilePath = Decrypt(inputFile, secretKey, taskUpdater);
        // Extract the expected MD5 checksum from the end of the temporary file, which will reduce the file's length.
        ExtractExpectedChecksum(tempFilePath, expectedDigest);
        // Calculate the actual MD5 checksum of the decrypted file.
        calculateMd5Checksum(tempFilePath, actualDigest, taskUpdater);
        // Compare the MD5 checksums.
        if (!MessageDigest.isEqual(expectedDigest, actualDigest)) {
            throw new FatalRuntimeException("the encrypted file is corrupted or the given secret key is not valid",
                    String.format("input file: \"%s\"", inputFile));
        }
        // Move the temporary file to the expected output path.
        try {
            Files.move(Paths.get(tempFilePath), Paths.get(outputFile), StandardCopyOption.REPLACE_EXISTING);
            if (null != taskUpdater) taskUpdater.update(1, 1);
        } catch (Exception e) {
            FileManip.DeleteIfExists(tempFilePath);
            throw new FatalRuntimeException("an error occurred while decrypting the file. The input file has not been modified",
                    String.format("cannot move file \"%s\" to \"%s\"", tempFilePath, outputFile));
        }
    }
}
