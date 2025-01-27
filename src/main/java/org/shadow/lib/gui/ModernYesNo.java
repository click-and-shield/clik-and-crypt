package org.shadow.lib.gui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;




public class ModernYesNo extends Stage {
    private final GridPane container;
    private final Label messageLabel;
    private final Button buttonYes;
    private final Button buttonNo;
    private final String message;
    private double xOffset = 0;
    private double yOffset = 0;
    private boolean isYes = false;

    // TODO: make the CSS class names configurable.

    public ModernYesNo(String message) {
        super();
        this.message = message;
        initModality(Modality.APPLICATION_MODAL);
        container = new GridPane();
        messageLabel = new Label();
        buttonNo = new Button();
        buttonYes = new Button();

        create();
        style();
        setEventHandlers();
        setResizable(false);
        setAlwaysOnTop(true);
        setOnCloseRequest(event -> this.close());
    }

    private void style() {
        initStyle(StageStyle.UNDECORATED);
        messageLabel.getStyleClass().add("modern-yes-no-label-message");
        buttonNo.getStyleClass().add("modern-yes-no-button-no");
        buttonYes.getStyleClass().add("modern-yes-no-button-yes");
        container.getStyleClass().add("modern-yes-no-gridpan-container");
    }

    private void create() {
        messageLabel.setText(message);
        buttonNo.setText("No");
        buttonYes.setText("Yes");

        // The bloc below ensures that:
        // - the label occupies all the available space.
        // - the column that contains the label spans over 2 columns.
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(Double.MAX_VALUE);
        messageLabel.setMaxHeight(Double.MAX_VALUE);
        GridPane.setColumnSpan(messageLabel, 2);

        // The bloc below ensures that the buttons "Yes" and "No" occupy all the available space.
        buttonYes.setMaxWidth(Double.MAX_VALUE);
        buttonNo.setMaxWidth(Double.MAX_VALUE);
        buttonYes.setMaxHeight(Double.MAX_VALUE);
        buttonNo.setMaxHeight(Double.MAX_VALUE);

        // The bloc below ensures that the buttons "Yes" and "No" always have the same width.
        ColumnConstraints colYes = new ColumnConstraints();
        ColumnConstraints colNo = new ColumnConstraints();
        colYes.setHgrow(Priority.ALWAYS);
        colNo.setHgrow(Priority.ALWAYS);
        colYes.setPercentWidth(50);
        colNo.setPercentWidth(50);
        container.getColumnConstraints().addAll(colYes, colNo);

        container.setAlignment(Pos.CENTER);
        container.add(messageLabel, 0, 0);
        container.add(buttonYes, 0, 1);
        container.add(buttonNo, 1, 1);

        Scene scene = new Scene(container);
        setScene(scene);
        scene.setFill(null);
    }

    private void setEventHandlers() {
        buttonNo.setOnAction(event -> this.close());
        buttonYes.setOnAction(event -> { this.close(); isYes = true; });

        container.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        container.setOnMouseDragged(event -> {
            setX(event.getScreenX() - xOffset);
            setY(event.getScreenY() - yOffset);
        });
    }

    public boolean isYes() {
        return isYes;
    }
}