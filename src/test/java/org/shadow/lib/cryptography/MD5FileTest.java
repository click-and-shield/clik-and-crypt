package org.shadow.lib.cryptography;

import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import static org.junit.jupiter.api.Assertions.*;

class MD5FileTest {

    @Test
    void testDigestAllWithExistingFile() {
        // Powershell:
        // ([System.BitConverter]::ToString((New-Object -TypeName System.Security.Cryptography.MD5CryptoServiceProvider).ComputeHash((New-Object -TypeName System.Text.UTF8Encoding).GetBytes("Testing digest all")))).Replace("-","")
        // => 18DE5DB0D3A2DAB3FDAC3934EC50F49F
        try {
            File newFile = File.createTempFile("test", "txt");
            BufferedWriter writer = new BufferedWriter(new FileWriter(newFile));
            writer.write("Testing digest all");
            writer.close();

            MD5File md5File = new MD5File(newFile.getAbsolutePath(), 3);
            md5File.digestAll();
            String result = md5File.getDigestAsHex().toUpperCase();
            assertNotNull(result);
            assert(result.equals("18DE5DB0D3A2DAB3FDAC3934EC50F49F"));
            md5File.close();

            if (!newFile.delete()) {
                fail("Failed to delete temporary file");
            }
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    void testDigestAllWithEmptyFile() {
        // Powershell:
        // ([System.BitConverter]::ToString((New-Object -TypeName System.Security.Cryptography.MD5CryptoServiceProvider).ComputeHash((New-Object -TypeName System.Text.UTF8Encoding).GetBytes("")))).Replace("-","")
        // => D41D8CD98F00B204E9800998ECF8427E
        try {
            File newFile = File.createTempFile("test", "txt");
            MD5File md5File = new MD5File(newFile.getAbsolutePath(), 3);
            md5File.digestAll();
            md5File.close();

            String result = md5File.getDigestAsHex().toUpperCase();
            assertNotNull(md5File.getDigestAsHex());
            assert(result.equals("D41D8CD98F00B204E9800998ECF8427E"));

            if (!newFile.delete()) {
                fail("Failed to delete temporary file");
            }
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    void testDigestAllWithNonexistingFile() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            MD5File md5File = new MD5File("nonExistentFile.txt");
            md5File.digestAll();
        });

        String expectedMessage = "The input file \"nonExistentFile.txt\" does not exist";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    private static class TestCase {
        public final String text;
        public final long expected;

        public TestCase(String text, long expected) {
            this.text = text;
            this.expected = expected;
        }
    }

    @Test
    void testCalculateIterationNumber() {
        final long chuckSize = 3L;
        TestCase[] test = new TestCase[]{
                new TestCase("", 0L),
                new TestCase("A".repeat((int) chuckSize), 1),
                new TestCase("A".repeat((int) chuckSize + 1), 2)
        };

        try {
            for (TestCase testCase : test) {
                File newFile = File.createTempFile("test", "txt");
                BufferedWriter writer = new BufferedWriter(new FileWriter(newFile));
                writer.write(testCase.text);
                writer.close();

                MD5File md5File = new MD5File(newFile.getAbsolutePath(), chuckSize);
                assertEquals(md5File.calculateIterationNumber(), testCase.expected);
                md5File.close();

                if (!newFile.delete()) {
                    fail("Failed to delete temporary file");
                }
            }
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    void testDigestChunk() {
        // Powershell:
        // ([System.BitConverter]::ToString((New-Object -TypeName System.Security.Cryptography.MD5CryptoServiceProvider).ComputeHash((New-Object -TypeName System.Text.UTF8Encoding).GetBytes("AAAAAAAAAA")))).Replace("-","")
        // => 16C52C6E8326C071DA771E66DC6E9E57

        final long chuckSize = 3L;
        final int chunkCount = 3;
        final String text = "A".repeat((int) chuckSize * chunkCount + 1);
        final int expectedCount = chunkCount + 1;
        final String expectedDigest = "16C52C6E8326C071DA771E66DC6E9E57";

        try {
                File newFile = File.createTempFile("test", "txt");
                BufferedWriter writer = new BufferedWriter(new FileWriter(newFile));
                writer.write(text);
                writer.close();

                MD5File md5File = new MD5File(newFile.getAbsolutePath(), chuckSize);
                int count = 0;
                while (md5File.digestChunk()) { count++; }
                assertEquals(count, expectedCount);
                assertEquals(md5File.getDigestAsHex().toUpperCase(), expectedDigest);
                md5File.close();

                if (!newFile.delete()) {
                    fail("Failed to delete temporary file");
                }
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }
}