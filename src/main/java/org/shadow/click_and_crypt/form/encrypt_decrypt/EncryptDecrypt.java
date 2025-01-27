package org.shadow.click_and_crypt.form.encrypt_decrypt;

import javafx.geometry.HPos;
import javafx.scene.layout.GridPane;
import org.shadow.click_and_crypt.Action;
import org.shadow.click_and_crypt.Configuration;

public class EncryptDecrypt extends GridPane {

    // public EncryptDecrypt(String[] args, String alertErrorCssPath) {
    public EncryptDecrypt() {
        super();
        create();
        style();
        setEventHandlers();
    }

    public void setConfiguration(Configuration configuration) {
        Components.configuration = configuration;

        if (Components.configuration.getAction() == Action.Encrypt) {
            Components.actionButton.setText(Components.encryptLabel);
        } else {
            Components.actionButton.setText(Components.decryptLabel);
        }
    }

    private void style() {
        getStyleClass().add("grid");
        Components.enterPassword1Label.getStyleClass().add("label");
        Components.enterPassword2Label.getStyleClass().add("label");
        Components.passwordStrengthLabel.getStyleClass().add("label");
        Components.progressionLabel.getStyleClass().add("label");
        Components.actionButton.getStyleClass().add("button-action");
        Components.visibilityButton.getStyleClass().add("button-action");
        Components.password1Visible.getStyleClass().add("input-text");
        Components.password1NonVisible.getStyleClass().add("input-text");
        Components.password2Visible.getStyleClass().add("input-text");
        Components.password2NonVisible.getStyleClass().add("input-text");
    }

    private void create() {
        // Configure the button used to trigger the action (encryption or decryption).
        Components.actionButton.setText("    ");
        Components.actionButton.setDisable(true);
        Components.actionButton.setMaxWidth(Double.MAX_VALUE);
        Components.actionButton.setMaxHeight(Double.MAX_VALUE);
        GridPane.setHalignment(Components.actionButton, HPos.RIGHT);

        // Configure the button used to modify the visibility of the passwords.
        Components.visibilityButton.setMaxWidth(Double.MAX_VALUE);
        Components.visibilityButton.setMaxHeight(Double.MAX_VALUE);
        Components.visibilityButton.setText(Components.showLabel);

        // Configure the progress bar.
        Components.progressBar.setMaxWidth(Double.MAX_VALUE);
        Components.progressBar.setMaxHeight(Double.MAX_VALUE);
        Components.progressBar.setVisible(true);
        GridPane.setHgrow(Components.progressBar, javafx.scene.layout.Priority.ALWAYS);
        GridPane.setVgrow(Components.progressBar, javafx.scene.layout.Priority.ALWAYS);

        // Configure the password strength bae.
        Components.passwordStrengthBar.setMaxWidth(Double.MAX_VALUE);
        Components.passwordStrengthBar.setMaxHeight(Double.MAX_VALUE);
        Components.passwordStrengthBar.setVisible(true);
        GridPane.setHgrow(Components.passwordStrengthBar, javafx.scene.layout.Priority.ALWAYS);
        GridPane.setVgrow(Components.passwordStrengthBar, javafx.scene.layout.Priority.ALWAYS);

        Components.password1NonVisible.setPrefColumnCount(Components.passwordMaxLength);
        Components.password2NonVisible.setPrefColumnCount(Components.passwordMaxLength);
        Components.password1Visible.setPrefColumnCount(Components.passwordMaxLength);
        Components.password2Visible.setPrefColumnCount(Components.passwordMaxLength);

        Components.password1NonVisible.setMaxHeight(Double.MAX_VALUE);
        Components.password2NonVisible.setMaxHeight(Double.MAX_VALUE);
        Components.password1Visible.setMaxHeight(Double.MAX_VALUE);
        Components.password2Visible.setMaxHeight(Double.MAX_VALUE);

        // First line of this grid.
        add(Components.enterPassword1Label, 0, 0);
        add(Components.p1box, 1, 0);
        add(Components.visibilityButton, 2, 0);

        // Second line of this grid.
        add(Components.enterPassword2Label, 0, 1);
        add(Components.p2box, 1, 1);
        add(Components.actionButton, 2, 1);

        // Third line of this grid.
        add(Components.passwordStrengthLabel, 0, 2);
        add(Components.passwordStrengthBar, 1, 2);

        // Fourth line of this grid.
        add(Components.progressionLabel, 0, 3);
        add(Components.progressBar, 1, 3);
    }

    private void setEventHandlers() {

        Components.password1Visible.managedProperty().bind(Components.password1Visible.visibleProperty());
        Components.password1NonVisible.managedProperty().bind(Components.password1NonVisible.visibleProperty());
        Components.password1Visible.visibleProperty().bind(Components.isVisible.asObject().isEqualTo(true));
        Components.password1NonVisible.visibleProperty().bind(Components.isVisible.asObject().isEqualTo(false));

        Components.password2Visible.managedProperty().bind(Components.password2Visible.visibleProperty());
        Components.password2NonVisible.managedProperty().bind(Components.password2NonVisible.visibleProperty());
        Components.password2Visible.visibleProperty().bind(Components.isVisible.asObject().isEqualTo(true));
        Components.password2NonVisible.visibleProperty().bind(Components.isVisible.asObject().isEqualTo(false));

        Components.password1Visible.textProperty().bindBidirectional(Components.password1NonVisible.textProperty());
        Components.password2Visible.textProperty().bindBidirectional(Components.password2NonVisible.textProperty());

        // Please note that the contents of the "clear" and "masked" password input fields are synchronized.
        // That is: - if the content of `password1Visible` changes, then the content of `password1NonVisible` also changes.
        //          - if the content of `password2Visible` changes, then the content of `password2NonVisible` also changes.

        // Below, we set the listener that compares the contents of the (two) password fields whenever the content
        // of a password input field changes. Based on the result of the comparison, the listener changes the
        // appearance of the password input fields.
        PasswordFieldChangeListener passwordFieldChangeListener = new PasswordFieldChangeListener();
        Components.password1NonVisible.textProperty().addListener(passwordFieldChangeListener);
        Components.password2NonVisible.textProperty().addListener(passwordFieldChangeListener);

        // Create the button that toggles the password visibility.
        Components.visibilityButton.setOnAction(new ButtonVisibilityClickHandler());
        Components.actionButton.setOnAction(new ActionClickHandler());
    }
}
