package uk.ac.aber.cs221.group15.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;

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
		// Set padding to 10px
		setPadding(new Insets(10));

		// TODO Add logo imageview
		// Use a temporary placeholder and add it to this pane
		Label tmp = new Label("TaskerCLI");
		tmp.setFont(new Font(20));
		tmp.setStyle("-fx-text-fill: rgb(40, 140, 255);");
		getChildren().add(tmp);

		// Center the label and set its color
		setAlignment(tmp, Pos.CENTER);
		setStyle("-fx-background-color: rgb(40, 40, 40);");
	}
}