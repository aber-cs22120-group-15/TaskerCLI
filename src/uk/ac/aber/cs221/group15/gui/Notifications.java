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
 * Provides functionality for popup dialogs in JavaFX
 *
 * @author Darren White
 * @version 0.0.2
 */
public class Notifications {

	/**
	 * The title of the window
	 */
	private String title;

	/**
	 * The message to display
	 */
	private String message;

	/**
	 * Whether or not to display yes/no buttons
	 */
	private boolean yesNoBtns;

	/**
	 * The event added to both yes/no buttons
	 */
	private EventHandler<ActionEvent> yesNoEvent;

	/**
	 * Creates a new Notifications object
	 */
	private Notifications() {
		// Builder class - nothing to do here
		// Keep constructor private
	}

	/**
	 * Creates a new Notifications instance
	 *
	 * @return A new Notifications instance
	 */
	public static Notifications create() {
		return new Notifications();
	}

	/**
	 * Set the message to be displayed
	 *
	 * @param message The message to show
	 * @return The Notifications instance
	 */
	public Notifications message(String message) {
		this.message = message;
		return this;
	}

	/**
	 * Shows this Notifications popup
	 */
	public void show() {
		// Use a gridpane to display the component
		GridPane pane = new GridPane();
		// Need a new scene to display the popup
		Scene scene = new Scene(pane);

		// Set the padding and gaps to 5px
		pane.setPadding(new Insets(5));
		pane.setHgap(5);
		pane.setVgap(5);

		// Add the message as a label if there is one
		if (message != null) {
			Label lblMsg = new Label(message);

			lblMsg.setPadding(new Insets(20));
			pane.add(lblMsg, 0, 0, 3, 1);
		}

		// Add the yes/no buttons if there are any
		if (yesNoBtns) {
			Button btnYes = new Button("Yes");
			Button btnNo = new Button("No");

			// Add the events and set as default/cancel buttons
			btnYes.setDefaultButton(true);
			btnYes.setOnAction(yesNoEvent);
			btnNo.setCancelButton(true);
			btnNo.setOnAction(yesNoEvent);

			// Align them to the right
			GridPane.setHalignment(btnNo, HPos.RIGHT);
			GridPane.setHalignment(btnYes, HPos.RIGHT);

			// Push the buttons to the right
			Region spacer = new Region();
			GridPane.setHgrow(spacer, Priority.ALWAYS);

			pane.add(spacer, 0, 1);
			pane.add(btnNo, 1, 1);
			pane.add(btnYes, 2, 1);
		}

		// Create a new stage to show the scene
		Stage stage = new Stage();

		stage.setScene(scene);
		// Don't want the popup to be resizable
		stage.setResizable(false);
		// Set the title if there is one
		if (title != null) {
			stage.setTitle(title);
		}
		// Resize it and show it
		stage.sizeToScene();
		stage.showAndWait();
	}

	/**
	 * Set the title for this notification
	 * @param title The title to use
	 * @return The Notifications instance
	 */
	public Notifications title(String title) {
		this.title = title;
		return this;
	}

	/**
	 * Set whether to use yes/no buttons
	 *
	 * @return The Notifications instance
	 */
	public Notifications yesNo() {
		yesNoBtns = true;
		yesNoEvent = null;
		return this;
	}

	/**
	 * Set whether to use yes/no buttons and add an event to both buttons
	 *
	 * @param event The event to use for the buttons
	 * @return The Notifications instance
	 */
	public Notifications yesNo(EventHandler<ActionEvent> event) {
		yesNoBtns = true;
		yesNoEvent = event;
		return this;
	}
}