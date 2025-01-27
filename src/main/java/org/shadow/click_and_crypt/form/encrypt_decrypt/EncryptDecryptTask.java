package org.shadow.click_and_crypt.form.encrypt_decrypt;

import javafx.application.Platform;
import javafx.concurrent.Task;
import org.shadow.lib.cryptography.TaskUpdater;
import org.shadow.lib.exception.FatalRuntimeException;
import org.shadow.lib.gui.ModernSuccess;
import org.shadow.click_and_crypt.Action;
import org.shadow.lib.cryptography.SkrivaCypherV1;

/**
 * This task is responsible for processing (encrypt or decrypt) the input file.
 * It is executed when the user clicks on the "Encrypt / Decrypt" button.
 */

public class EncryptDecryptTask extends Task<Void> implements TaskUpdater {
    private final String secretKey;

    public EncryptDecryptTask(String secretKey) {
        this.secretKey = secretKey;
    }

    public void update(long current, long total) {
        this.updateProgress(current, total);
    }

    /**
     * Performs the file encryption or decryption operation based on the configuration settings.
     * The method uses the SkrivaCypherV1 class to either encrypt or decrypt the specified input file and writes the result to the output file.
     * The operation is determined by the action specified in the configuration (Encrypt or Decrypt).
     *
     * @return null as the method completes the task without returning any significant value.
     * @throws FatalRuntimeException if a runtime error occurs during the encryption or decryption process.
     */

    @Override
    protected Void call() throws FatalRuntimeException {
        SkrivaCypherV1 cypher = new SkrivaCypherV1();

        // Proceed to the encryption or decryption.
        if (Components.configuration.getAction() == Action.Encrypt) {
            cypher.EncryptFile(Components.configuration.getInput(), Components.configuration.getOutput(), secretKey, this);
        } else {
            cypher.DecryptFile(Components.configuration.getInput(), Components.configuration.getOutput(), secretKey, this);
        }

        // If the execution pointer reaches this point, it means that the operation was successful.
        // Let's show the box that signal the user that the operation is successful.
        Platform.runLater(() -> {
            ModernSuccess success = new ModernSuccess("Operation completed successfully");
            success.getScene().getStylesheets().add(Components.configuration.getModernSuccessCssPath());
            success.showAndWait();
            System.exit(0);
        });

        return null;
    }
}