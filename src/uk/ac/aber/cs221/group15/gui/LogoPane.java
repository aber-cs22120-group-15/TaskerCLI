package uk.ac.aber.cs221.group15.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

/**
 * This class will display a banner/logo for the client
 *
 * @author Darren White
 * @version 0.0.2
 */
public class LogoPane extends StackPane {

	/**
	 * Creates a new logo pane
	 */
	public LogoPane() {
		init();
	}

	/**
	 * Initialize this pane and its components
	 */
	private void init() {
		// Set the id for css styles
		setId("logo-pane");
		// Set padding to 10px
		setPadding(new Insets(10));

		// TODO Add logo imageview
		// Use a temporary placeholder
		Label tmp = new Label("TaskerCLI");
		// Set the label id for css stlying
		tmp.setId("lbl-banner");
		// Add it to this pane
		getChildren().add(tmp);

		// Center the label
		setAlignment(tmp, Pos.CENTER);
	}
}