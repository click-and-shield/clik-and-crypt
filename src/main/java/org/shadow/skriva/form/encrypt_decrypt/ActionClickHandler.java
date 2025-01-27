package org.shadow.skriva.form.encrypt_decrypt;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 * This event handler is responsible for starting the task that processes the input file.
 * Please keep in mind that the processing involves either encrypting or decrypting the input file.
 */

public class ActionClickHandler implements EventHandler<ActionEvent> {

    /**
     * Process the input file when the user clicks on the "Encrypt / Decrypt" button.
     * Please note that this task is executed as a thread.
     *
     * @param event The `ActionEvent` that triggered this handler.
     */

    @Override
    public void handle(ActionEvent event) {
        final String secretKey = Components.password1Visible.getText();
        EncryptDecryptTask task = new EncryptDecryptTask(secretKey);
        // The following line attaches an exception handler to the task responsible for processing the input file.
        task.setOnFailed(evt -> handleTaskException(task.getException()));
        Components.progressBar.progressProperty().bind(task.progressProperty());
        Components.actionButton.setDisable(true);
        Thread thread = new Thread(task);
        thread.setDaemon(true); // this line is not essential, but we keep it as a reminder
        thread.start();
    }

    /**
     * Handles exceptions that occur during the execution of the task responsible for processing the input file.
     * This method re-throws the exception as a RuntimeException. The re-thrown exception will be handled by the
     * application's level exception handling mechanism. Please, look for the call to
     * `Thread.setDefaultUncaughtExceptionHandler()`.
     *
     * @param exception The Throwable exception that occurred during task execution.
     * @throws RuntimeException The exception describing the error that is reported to the user.
     */

    private void handleTaskException(Throwable exception) throws RuntimeException {
        Components.progressBar.progressProperty().unbind();
        Components.progressBar.setDisable(true);
        Components.progressBar.setProgress(0); // this prevents the "wave" from continuously bouncing inside the progress bar
        System.out.println("re-throw the exception: " + exception.getMessage());
        throw new RuntimeException(exception.getMessage()); // will be handled by the application's level exception handling mechanism
    }
}
