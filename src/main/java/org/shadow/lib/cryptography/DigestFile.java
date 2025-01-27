package org.shadow.lib.cryptography;

import org.shadow.lib.exception.CompileTimeError;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public abstract class DigestFile implements AutoCloseable {
    private final long bufferLength;
    private final FileInputStream inputStream;
    protected final MessageDigest md;
    private static final long defaultChunkLength = 4096L;

    /**
     * Initializes a new instance of the DigestFile class with the specified file path and message digest.
     * Sets up the input stream from the file and prepares the message digest.
     *
     * @param path The path to the input file to be processed.
     * @param md The MessageDigest instance to be used for calculating the file's digest.
     * @throws RuntimeException If the specified file does not exist or cannot be opened.
     */

    protected DigestFile(String path, MessageDigest md) throws RuntimeException {
        this.bufferLength = defaultChunkLength;

        try {
            this.inputStream = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(String.format("The input file \"%s\" does not exist", path));
        }
        this.md = md;
    }

    /**
     * Initializes a new instance of the DigestFile class with the specified file path, message digest, and chunk length.
     * Sets up the input stream from the file and prepares the message digest.
     *
     * @param path The path to the input file to be processed.
     * @param md The MessageDigest instance to be used for calculating the file's digest.
     * @param chunkLength The length of the chunks that the file will be divided into for processing.
     * @throws RuntimeException If the specified file does not exist or cannot be opened.
     */

    protected DigestFile(String path, MessageDigest md, long chunkLength) throws RuntimeException {
        this.bufferLength = chunkLength;

        try {
            this.inputStream = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(String.format("The input file \"%s\" does not exist", path));
        }
        this.md = md;
    }

    /**
     * Initializes a new instance of the DigestFile class with the specified input stream and message digest.
     *
     * @param inputStream The FileInputStream instance to be used for reading the file's content.
     * @param md The MessageDigest instance to be used for calculating the file's digest.
     */

    protected DigestFile(FileInputStream inputStream, MessageDigest md) {
        this.bufferLength = defaultChunkLength;
        this.inputStream = inputStream;
        this.md = md;
    }

    /**
     * Initializes a new instance of the DigestFile class with the specified message digest,
     * chunk length, and input stream.
     *
     * @param inputStream The FileInputStream instance to be used for reading the file's content.
     * @param md The MessageDigest instance to be used for calculating the file's digest.
     * @param chunkLength The length of the chunks that the file will be divided into for processing.
     */

    protected DigestFile(FileInputStream inputStream, MessageDigest md, long chunkLength) {
        this.bufferLength = chunkLength;
        this.inputStream = inputStream;
        this.md = md;
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
     * Reads a chunk of data from the input stream and updates the message digest.
     * This method processes a chunk of the file input based on the predefined buffer length,
     * updates the given message digest with the read data, and indicates whether more data can be read.
     *
     * @return true if more data can be read from the input stream, false if the end of the stream is reached.
     * @throws RuntimeException if an I/O error occurs during reading or if updating the message digest fails.
     */

    public boolean digestChunk() throws RuntimeException {
        byte[] buffer = new byte[(int) bufferLength];
        int bytesRead;
        try {
            bytesRead = inputStream.read(buffer);
        } catch (IOException e) {
            throw new RuntimeException(String.format("An error occurred while calculating the MD5: %s", e.getMessage()));
        }
        if (bytesRead == -1) {
            return false;
        }
        md.update(buffer, 0, bytesRead);
        return true;
    }

    /**
     * Continuously processes the entire input file by reading and digesting chunks of data until completion.
     * This method repeatedly calls {@link #digestChunk()} until it returns false, indicating that the end of the input stream has been reached.
     *
     * @throws RuntimeException if an error occurs during the reading or digesting process.
     */

    public void digestAll() throws RuntimeException {
        boolean progress = true;
        while (progress) { progress = digestChunk(); }
    }

    /**
     * Generates and returns the current digest as a hexadecimal string.
     *
     * @return the message digest of the input data as a hexadecimal string.
     */

    public abstract String getDigestAsHex();

    /**
     * Generates and returns the current message digest as a byte array.
     *
     * @return the message digest of the input data as a byte array.
     */

    public byte[] getDigestAsBytes() { return md.digest(); }

    /**
     * Generates the current message digest and writes it into the provided buffer as a byte array.
     * This method populates the specified portion of the buffer with the computed message digest.
     *
     * @param buffer The byte array where the digest will be stored.
     * @return The number of bytes written to the buffer.
     * @throws DigestException If an error occurs during the digest operation.
     */

    public int getDigestAsBytes(byte[] buffer) throws DigestException {
            return md.digest(buffer, 0, md.getDigestLength());
    }

    public static int getDigestLength() throws CompileTimeError {
        throw new CompileTimeError("the static method getDigestLength must be implemented!");
    }

    @Override
    public void close() throws IOException {
        this.inputStream.close();
    }

}
