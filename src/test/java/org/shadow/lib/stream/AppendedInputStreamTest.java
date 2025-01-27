package org.shadow.lib.stream;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class AppendedInputStreamTest {

    @Test
    public void testReadFirstFormAllBytes() throws IOException {
        final byte[] originalBytes = "Hello".getBytes();
        final byte[] appendBytes = "World".getBytes();

        ByteArrayInputStream originalStream = new ByteArrayInputStream(originalBytes);
        AppendedInputStream appendedInputStream = new AppendedInputStream(originalStream, appendBytes);

        for (byte originalByte : originalBytes) {
            assertEquals(originalByte, appendedInputStream.read());
        }
        for (byte appendByte : appendBytes) {
            assertEquals(appendByte, appendedInputStream.read());
        }
        assertEquals(-1, appendedInputStream.read());
        originalStream.close();
    }

    private static String createInput(int n) {
        StringBuilder result = new StringBuilder();
        Character c = 'A';
        for (int i = 0; i < n; i++) {
            result.append(c);
            c = (char) (c + 1);
            if (c > 'Z') {
                c = 'A';
            }
        }
        return result.toString();
    }

    @Test
    public void testReadSecondFormAllBytes() throws IOException {
        final int CHUNK_SIZE = 2;
        final int ORIGINAL_CHUNK_COUNT = 3;
        final int APPEND_CHUNK_COUNT = 2;
        final byte[] originalBytes =  createInput(CHUNK_SIZE * ORIGINAL_CHUNK_COUNT + 1).getBytes();
        final byte[] appendBytes = createInput(CHUNK_SIZE * APPEND_CHUNK_COUNT + 1).getBytes();
        byte[] buffer = new byte[2];

        ByteArrayInputStream originalStream = new ByteArrayInputStream(originalBytes);
        AppendedInputStream appendedInputStream = new AppendedInputStream(originalStream, appendBytes);

        for (int i = 0; i < ORIGINAL_CHUNK_COUNT; i++) {
            assertEquals(CHUNK_SIZE, appendedInputStream.read(buffer));
            for (int j = 0; j < CHUNK_SIZE; j++) {
                assertEquals(originalBytes[i * CHUNK_SIZE + j], buffer[j]);
            }
        }
        assertEquals(1, appendedInputStream.read(buffer));
        assertEquals(originalBytes[ORIGINAL_CHUNK_COUNT * CHUNK_SIZE], buffer[0]);

        for (int i = 0; i < APPEND_CHUNK_COUNT; i++) {
            assertEquals(CHUNK_SIZE, appendedInputStream.read(buffer));
            for (int j = 0; j < CHUNK_SIZE; j++) {
                assertEquals(appendBytes[i * CHUNK_SIZE + j], buffer[j]);
            }
        }
        assertEquals(1, appendedInputStream.read(buffer));
        assertEquals(appendBytes[APPEND_CHUNK_COUNT * CHUNK_SIZE], buffer[0]);

        assertEquals(-1, appendedInputStream.read());
        originalStream.close();
    }

    @Test
    public void testReadThirdFormAllBytes() throws IOException {
        final int CHUNK_SIZE = 2;
        final int ORIGINAL_CHUNK_COUNT = 3;
        final int APPEND_CHUNK_COUNT = 2;
        final byte[] originalBytes =  createInput(CHUNK_SIZE * ORIGINAL_CHUNK_COUNT + 1).getBytes();
        final byte[] appendBytes = createInput(CHUNK_SIZE * APPEND_CHUNK_COUNT + 1).getBytes();
        byte[] buffer_original = new byte[CHUNK_SIZE*ORIGINAL_CHUNK_COUNT+1];
        byte[] buffer_append = new byte[CHUNK_SIZE*APPEND_CHUNK_COUNT+1];
        int position;

        ByteArrayInputStream originalStream = new ByteArrayInputStream(originalBytes);
        AppendedInputStream appendedInputStream = new AppendedInputStream(originalStream, appendBytes);

        // Original bytes
        position = 0;
        for (int i = 0; i < ORIGINAL_CHUNK_COUNT; i++) {
            assertEquals(CHUNK_SIZE, appendedInputStream.read(buffer_original, position, CHUNK_SIZE));
            for (int j = 0; j < CHUNK_SIZE; j++) {
                assertEquals(originalBytes[i * CHUNK_SIZE + j], buffer_original[position + j]);
            }
            position += CHUNK_SIZE;
        }
        appendedInputStream.read(buffer_original, position, 1);
        assertArrayEquals(originalBytes, buffer_original);

        // Appended bytes
        position = 0;
        for (int i = 0; i < APPEND_CHUNK_COUNT; i++) {
            assertEquals(CHUNK_SIZE, appendedInputStream.read(buffer_append, position, CHUNK_SIZE));
            for (int j = 0; j < CHUNK_SIZE; j++) {
                assertEquals(appendBytes[i * CHUNK_SIZE + j], buffer_append[position + j]);
            }
            position += CHUNK_SIZE;
        }
        appendedInputStream.read(buffer_append, position, 1);
        assertArrayEquals(appendBytes, buffer_append);

        assertEquals(-1, appendedInputStream.read());
        originalStream.close();
    }
}