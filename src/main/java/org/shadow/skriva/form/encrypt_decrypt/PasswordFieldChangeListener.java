package org.shadow.skriva.form.encrypt_decrypt;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import org.shadow.lib.cryptography.SecretKeyTools;

/**
 * A ChangeListener implementation that monitors changes to the text of a PasswordField.
 * This listener alters the background color of the PasswordField based on the length of the input text.
 * It displays or hides warning icons based on whether the input meets the minimum password length requirement.
 */

public class PasswordFieldChangeListener implements ChangeListener<String> {
    private final TextField[] password1Fields;
    private final TextField[] password2Fields;
    private final TextField[] passwordFields;

    /**
     * Constructs an InputPasswordFieldChangeListener to monitor two sets of password TextField arrays.
     */
    public PasswordFieldChangeListener() {
        password1Fields = new TextField[]{Components.password1Visible, Components.password1NonVisible};
        password2Fields = new TextField[]{Components.password2Visible, Components.password2NonVisible};
        passwordFields = new TextField[password1Fields.length + password2Fields.length];
        System.arraycopy(password1Fields, 0, passwordFields, 0, password1Fields.length);
        System.arraycopy(password2Fields, 0, passwordFields, password1Fields.length, password2Fields.length);
    }

    /**
     * Changes the CSS style of all password input fields.
     *
     * @param css the CSS style to apply to the password input fields
     */
    private void changeInput(String css) {
        for (TextField currentPasswordField : passwordFields) {
            currentPasswordField.setStyle(css);
        }
    }

    /**
     * Updates the visual representation of password strength based on the given secret keys.
     *
     * @param secretKey1 The first secret key used to calculate strength.
     * @param secretKey2 The second secret key used to calculate strength.
     */
    private void updateStrength(String secretKey1, String secretKey2) {
        // Calculate the strength of the key. This is the strength of the strongest key.
        if (secretKey1.isEmpty() && secretKey2.isEmpty()) {
            Components.passwordStrengthBar.setProgress(0);
            return;
        }

        final double entropy1 = SecretKeyTools.calculateEntropy(secretKey1);
        final double entropy2 = SecretKeyTools.calculateEntropy(secretKey2);
        final double entropy = Math.max(entropy1, entropy2);
        final SecretKeyTools.Strength strength = SecretKeyTools.calculateStrength(entropy);

        Components.passwordStrengthBar.setProgress(entropy / 100);

        Components.passwordStrengthBar.getStyleClass().clear();
        if (strength == SecretKeyTools.Strength.LOW) {
            Components.passwordStrengthBar.getStyleClass().addAll("strength-low", "progress-bar");
        } else if (strength == SecretKeyTools.Strength.MEDIUM) {
            Components.passwordStrengthBar.getStyleClass().addAll("strength-medium", "progress-bar");
        } else if (strength == SecretKeyTools.Strength.HIGH) {
            Components.passwordStrengthBar.getStyleClass().addAll("strength-high", "progress-bar");
        } else {
            Components.passwordStrengthBar.getStyleClass().addAll("strength-very-high", "progress-bar");
        }
    }

    /**
     * Updates the background color of the password input fields based on whether the passwords match or meet
     * certain criteria.
     *
     * @param secretKey1 The first password input by the user.
     * @param secretKey2 The second password input for confirmation.
     */
    private void updateBackground(String secretKey1, String secretKey2) {
        if (! secretKey1.equals(secretKey2) && !secretKey1.isEmpty() && !secretKey2.isEmpty()) {
            changeInput("-fx-background-color: lightpink;"); // passwords mismatch
            Components.actionButton.setDisable(true);
        } else if (secretKey1.isEmpty() || secretKey2.isEmpty()) {
            changeInput("-fx-background-color: white;"); // reset inputs
            Components.actionButton.setDisable(true);
        } else {
            changeInput("-fx-background-color: lightgreen;"); // passwords match
            Components.actionButton.setDisable(false);
        }
    }

    /**
     * Called whenever the value of the observable changes.
     * Updates the background color of the password input fields based on whether the passwords match.
     *
     * @param observable the observable value being monitored
     * @param oldValue the old value before the change
     * @param newValue the new value after the change
     */
    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        // Please note that the contents of the "clear" and "masked" password input fields are synchronized.
        // That is: - if the content of `password1Visible` changes, then the content of `password1NonVisible` also changes.
        //          - if the content of `password2Visible` changes, then the content of `password2NonVisible` also changes.
        final String secretKey1 = password1Fields[0].getText();
        final String secretKey2 = password2Fields[0].getText();
        updateBackground(secretKey1, secretKey2);
        updateStrength(secretKey1, secretKey2);
    }
}
