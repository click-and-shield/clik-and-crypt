package org.shadow.click_and_crypt.form.encrypt_decrypt;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;

public class ButtonVisibilityClickHandler implements EventHandler<ActionEvent> {
//    private final BooleanProperty isVisible;
//    private final String showLabel;
//    private final String hideLabel;
//
//    public ButtonVisibilityClickHandler(BooleanProperty isVisible, String showLabel, String hideLabel) {
//        this.isVisible = isVisible;
//        this.showLabel = showLabel;
//        this.hideLabel = hideLabel;
//    }

    @Override
    public void handle(ActionEvent event) {
        Button source = (Button) event.getSource();
        Components.isVisible.set(! Components.isVisible.get());

        // If the password is visible, then the button that changes its visibility must be labeled "hide".
        // If the password is not visible, then the button that changes its visibility must be labeled "show".
        if (Components.isVisible.get()) {
            source.setText(Components.hideLabel);
        } else {
            source.setText(Components.showLabel);
        }
    }
}