package org.shadow.lib.gui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ModernSuccess extends Stage {
    private final GridPane container;
    private final Label messageLabel;
    private final Button buttonOk;
    private final String message;
    private double xOffset = 0;
    private double yOffset = 0;

    // TODO: make the CSS class names configurable.

    public ModernSuccess(String message) {
        super();
        this.message = message;
        initModality(Modality.APPLICATION_MODAL);
        container = new GridPane();
        messageLabel = new Label();
        buttonOk = new Button();

        create();
        style();
        setEventHandlers();
        setResizable(false);
        setAlwaysOnTop(true);
        setOnCloseRequest(event -> this.close());
    }

    private void style() {
        initStyle(StageStyle.UNDECORATED);
        buttonOk.getStyleClass().add("modern-success-button-ok");
        messageLabel.getStyleClass().add("modern-success-label-message");
        container.getStyleClass().add("modern-success-gridpan-container");
    }

    private void create() {
        messageLabel.setText(message);
        buttonOk.setText("OK");

        // The bloc below ensures that the label occupies all the available space.
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(Double.MAX_VALUE);
        messageLabel.setMaxHeight(Double.MAX_VALUE);

        // This code ensures that the button occupies all the available space.
        buttonOk.setMaxWidth(Double.MAX_VALUE);
        buttonOk.setMaxHeight(Double.MAX_VALUE);

        container.setAlignment(Pos.CENTER);
        container.add(messageLabel, 0, 0); // (column, row)
        container.add(buttonOk, 0, 1);
//
        Scene scene = new Scene(container);
        setScene(scene);
        scene.setFill(null);
    }

    private void setEventHandlers() {
        buttonOk.setOnAction(event -> this.close());

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