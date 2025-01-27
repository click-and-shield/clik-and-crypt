package org.shadow.lib.stream;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class AppendedInputStream extends InputStream {

    private final InputStream originalStream;
    private final byte[] additionalBytes;
    private int additionalIndex = 0;
    private boolean readingOriginal = true;

    public AppendedInputStream(InputStream originalStream, byte[] additionalBytes) {
        this.originalStream = originalStream;
        this.additionalBytes = additionalBytes;
    }

    /**
     * Reads the next byte of data from the input stream. This method first attempts
     * to read from the original input stream. If the end of the original stream
     * is reached, it then reads from the additional byte array until all additional
     * bytes have been read. If there are no more byte to be read, it returns -1.
     *
     * @return the next byte of data, or -1 if the end of the original stream and
     *         the additional byte array have been reached.
     * @throws IOException if an I/O error occurs.
     */

    @Override
    public int read() throws IOException {
        if (readingOriginal) {
            int originalData = originalStream.read();
            if (originalData == -1) {
                readingOriginal = false; // End of original stream, switch to additional bytes
            } else {
                return originalData;
            }
        }

        if (additionalIndex < additionalBytes.length) {
            return (int) additionalBytes[additionalIndex++];
        }
        return -1; // End of the additional data
    }

    /**
     * Reads the next sequence of bytes from the input stream. This method first attempts
     * to read from the original input stream. If the original stream is exhausted, it then
     * reads from the additional byte array provided during instantiation. If there are
     * no more bytes to be read, it returns -1.
     *
     * @param b the buffer into which the data is read.
     * @return the number of bytes read, or -1 if the end of the original stream and
     *         the additional byte array has been reached.
     * @throws IOException if an I/O error occurs.
     */

    @Override
    public int read(byte[] b) throws IOException {
        if (originalStream.available() > 0) {
            return originalStream.read(b);
        }

        if (additionalIndex < additionalBytes.length) {
            int bytesToRead = Math.min(b.length, additionalBytes.length - additionalIndex);
            System.arraycopy(additionalBytes, additionalIndex, b, 0, bytesToRead);
            additionalIndex += bytesToRead;
            return bytesToRead;
        }

        return -1; // Should not reach here
    }

    /**
     * Reads up to len bytes of data from the input stream into an array of bytes starting at the specified offset.
     * This method first attempts to read from the original input stream. If the end of the original stream
     * is reached, it then reads from the additional byte array until all additional bytes have been read.
     * If there are no more bytes to be read, it returns -1.
     *
     * @param b the buffer into which the data is read
     * @param off the start offset in the destination array b
     * @param len the maximum number of bytes read
     * @return the total number of bytes read into the buffer, or -1 if there is no more data because
     *         the end of the stream has been reached
     * @throws IOException if an I/O error occurs
     */

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (originalStream.available() > 0) {
            return originalStream.read(b, off, len);
        }

        if (additionalIndex < additionalBytes.length) {
            int bytesToRead = Math.min(len, additionalBytes.length - additionalIndex);
            System.arraycopy(additionalBytes, additionalIndex, b, off, bytesToRead);
            additionalIndex += bytesToRead;
            return bytesToRead;
        }

        return -1;
    }

    /**
     * Closes the input stream and releases any system resources associated with it.
     * This method closes the original stream that was provided during instantiation.
     *
     * @throws IOException if an I/O error occurs while closing the stream.
     */

    @Override
    public void close() throws IOException {
        originalStream.close();
    }
}
