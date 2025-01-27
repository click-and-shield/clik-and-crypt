package org.shadow.lib.cryptography;

import org.jetbrains.annotations.Nullable;

public interface SkrivaCypher {
    void EncryptFile(String inputFile, String outputFile, String secretKey, @Nullable TaskUpdater taskUpdater) throws RuntimeException;
    void DecryptFile(String inputFile, String outputFile, String secretKey, @Nullable TaskUpdater taskUpdater) throws RuntimeException;
}
