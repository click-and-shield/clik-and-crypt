package org.shadow.click_and_crypt;

import org.shadow.lib.exception.FatalRuntimeException;
import org.shadow.lib.exception.RecoverableRuntimeException;
import org.shadow.click_and_crypt.form.encrypt_decrypt.EncryptDecrypt;

import java.util.List;

/**
 * The RunSetConfiguration class is responsible for running and setting up
 * the configuration for the EncryptDecrypt operations.
 * It implements the Runnable interface to allow the configuration setup
 * in a separate thread of execution.
 *
 * Please, search for the code marker "ID:001".
 */

public class  RunSetConfiguration implements Runnable {

    private final Configuration configuration;
    private final EncryptDecrypt encryptDecryptForm;
    private final List<String> args;

    public RunSetConfiguration(Configuration configuration, List<String> args, EncryptDecrypt encryptDecryptForm) {
        this.configuration = configuration;
        this.encryptDecryptForm = encryptDecryptForm;
        this.args = args;
    }

    @Override
    public void run() throws FatalRuntimeException, RecoverableRuntimeException {
        if (configuration.isVerbose()) {
            System.out.println("Set the configuration for the interface");
        }
        org.shadow.click_and_crypt.Parameters.ParseCommandLine(configuration, args);
        encryptDecryptForm.setConfiguration(configuration);
    }
}
