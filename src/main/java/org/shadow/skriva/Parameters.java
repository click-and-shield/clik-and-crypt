package org.shadow.skriva;

import javafx.application.Platform;
import org.shadow.lib.exception.FatalRuntimeException;
import org.shadow.skriva.exception.RecoverableRuntimeException;

import java.util.List;

public class Parameters {

    private static RunVerifyConfiguration runVerifyConfiguration;

    /**
     * Parses the command-line arguments and updates the provided configuration object.
     *
     * Command-line arguments are:
     * - the action to perform (encrypt or decrypt).
     * - the input file.
     *
     * @param configuration the configuration instance that will be populated based on the parsed command-line arguments
     * @param args the list of command-line arguments to be parsed
     * @throws FatalRuntimeException if any of the provided arguments are invalid and this is not recoverable.
     * @throws RecoverableRuntimeException if any of the provided arguments are invalid and this is recoverable.
     * @apiNote Please note that this method can only be called once the application has been launched and
     *          the event loop has been started. Indeed it may raise exceptions.
     * @apiNote Please search for the code marker "M:001".
     */

    public static void ParseCommandLine(Configuration configuration, List<String> args) throws FatalRuntimeException, RecoverableRuntimeException {
        // Extract the parameters.
        if (args.size() < 2) {
            throw new FatalRuntimeException("Invalid number of parameters");
        }
        final String action = args.get(0);
        final String inputPath = args.get(1);

        // Verify the parameters.
        if (!Action.isValidName(action)) {
            throw new FatalRuntimeException("the given action \"" + action + "\" is not valid");
        }

        // Create the configuration.
        configuration.setAction(Action.nameToEnum(action));
        configuration.setInput(inputPath);

        // Please note that we need the configuration to be set for the main form to be initialized.
        runVerifyConfiguration = new RunVerifyConfiguration(configuration);
        Platform.runLater(runVerifyConfiguration); // [M:002] check the input and output files
    }

}
