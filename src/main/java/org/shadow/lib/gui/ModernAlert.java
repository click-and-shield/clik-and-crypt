package org.shadow.lib.gui;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.control.Label;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The ModerAlert class extends the Stage class to create a custom modal alert dialogue.
 * It displays a message and a close button.
 */

public class ModernAlert extends Stage {
    static public int width = 400;
    private final VBox container;
    private final Label messageLabel;
    @Nullable private final TextArea detailsTextArea;
    private final Button closeButton;
    private final String message;
    @Nullable private final String details;
    private double xOffset = 0;
    private double yOffset = 0;

    // TODO: make the CSS class names configurable.

    void initialize() {
        create();
        style();
        setEventHandlers();
        setResizable(false);
        setAlwaysOnTop(true);
        setOnCloseRequest(event -> this.close());
    }

    public ModernAlert(String message) {
        super();
        this.message = message;
        this.details = null;
        this.messageLabel = new Label();
        this.closeButton = new Button();
        this.container = new VBox();
        this.detailsTextArea = null;
        initialize();
    }

    public ModernAlert(String message, @NotNull String details) {
        super();
        this.message = message;
        this.details = details;
        this.messageLabel = new Label();
        this.closeButton = new Button();
        this.container = new VBox();
        this.detailsTextArea = new TextArea();
        initialize();
    }

    private void style() {
        initStyle(StageStyle.UNDECORATED);
        messageLabel.getStyleClass().add("modern-alert-label-message");
        detailsTextArea.getStyleClass().add("modern-alert-textarea-details");
        closeButton.getStyleClass().add("modern-alert-button-ok");
        container.getStyleClass().add("modern-alert-vbox-container");
    }

    private void create() {
        initModality(Modality.APPLICATION_MODAL);

        messageLabel.setText(message);
        messageLabel.setPrefWidth(width);
        messageLabel.setWrapText(true);

        closeButton.setText("OK");
        closeButton.setPrefWidth(width);

        if (null != detailsTextArea) {
            detailsTextArea.setEditable(false);
            detailsTextArea.setWrapText(true);
            detailsTextArea.setPrefWidth(width);
            detailsTextArea.setPrefHeight(100);
            detailsTextArea.setText(details);
            container.getChildren().addAll(messageLabel, detailsTextArea, closeButton);
        } else {
            container.getChildren().addAll(messageLabel, closeButton);
        }

        Scene scene = new Scene(container);
        setScene(scene);
        scene.setFill(null);
    }

    private void setEventHandlers() {
        closeButton.setOnAction(event -> this.close());

        container.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        container.setOnMouseDragged(event -> {
            setX(event.getScreenX() - xOffset);
            setY(event.getScreenY() - yOffset);
        });
    }
}
