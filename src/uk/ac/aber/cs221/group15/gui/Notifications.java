package uk.ac.aber.cs221.group15.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

/**
 * @author Darren White
 * @version 0.0.1
 */
public class Notifications {

	private String title;
	private String message;
	private boolean yesNoBtns;
	private EventHandler<ActionEvent> yesNoEvent;

	private Notifications() {
		// Builder class - nothing to do here
		// Keep constructor private
	}

	public static Notifications create() {
		return new Notifications();
	}

	public Notifications message(String message) {
		this.message = message;
		return this;
	}

	public void show() {
		GridPane pane = new GridPane();
		Scene scene = new Scene(pane);

		pane.setPadding(new Insets(5));
		pane.setHgap(5);
		pane.setVgap(5);

		if (message != null) {
			Label lblMsg = new Label(message);

			lblMsg.setPadding(new Insets(20));
			pane.add(lblMsg, 0, 0, 3, 1);
		}

		if (yesNoBtns) {
			Button btnYes = new Button("Yes");
			Button btnNo = new Button("No");

			btnYes.setOnAction(yesNoEvent);
			btnNo.setOnAction(yesNoEvent);

			GridPane.setHalignment(btnNo, HPos.RIGHT);
			GridPane.setHalignment(btnYes, HPos.RIGHT);

			Region spacer = new Region();
			GridPane.setHgrow(spacer, Priority.ALWAYS);

			pane.add(spacer, 0, 1);
			pane.add(btnNo, 1, 1);
			pane.add(btnYes, 2, 1);
		}

		Stage stage = new Stage();

		stage.setScene(scene);
		stage.setResizable(false);
		if (title != null) {
			stage.setTitle(title);
		}
		stage.sizeToScene();
		stage.showAndWait();
	}

	public Notifications title(String title) {
		this.title = title;
		return this;
	}

	public Notifications yesNo(EventHandler<ActionEvent> event) {
		yesNoBtns = true;
		yesNoEvent = event;
		return this;
	}
}