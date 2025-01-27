package org.shadow.skriva.form.encrypt_decrypt;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import org.shadow.skriva.Configuration;

/**
 * The Components class encapsulates the UI components and their configurations that are used in the form
 * implemented by the current package.
 *
 * Please note that this class contains "package-private" variables.
 */

public class Components {
    static final int passwordMaxLength = 20;
    static final String showLabel = "\uD83D\uDC41";
    static final String hideLabel = "\uD83D\uDD76";
    static final String encryptLabel = "\uD83D\uDD12"; // https://www.compart.com/en/unicode/U+1F512
    static final String decryptLabel = "\uD83D\uDD13"; // https://www.compart.com/en/unicode/U+1F512




//    static final String encryptLabel = "encrypt"; // https://www.compart.com/en/unicode/U+1F512
//    static final String decryptLabel = "decrypt"; // https://www.compart.com/en/unicode/U+1F512

    static Configuration configuration;
    static final BooleanProperty isVisible = new SimpleBooleanProperty(false);
    static final Button visibilityButton = new Button();
    static final Button actionButton = new Button();
    static final PasswordField password1NonVisible = new PasswordField();
    static final PasswordField password2NonVisible = new PasswordField();
    static final TextField password1Visible = new TextField();
    static final TextField password2Visible = new TextField();
    static final HBox p1box = new HBox(password1Visible, password1NonVisible);
    static final HBox p2box = new HBox(password2Visible, password2NonVisible);
    static final Label enterPassword1Label = new Label("Enter password:");
    static final Label enterPassword2Label = new Label("Confirm password:");
    static final Label passwordStrengthLabel = new Label("Password strength:");
    static final Label progressionLabel = new Label("Progression:");
    static final ProgressBar progressBar = new ProgressBar(0);
    static final ProgressBar passwordStrengthBar = new ProgressBar(0);
}
