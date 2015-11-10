package uk.ac.aber.cs221.group15.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;

/**
 * @author Darren White
 * @version 0.0.1
 * @since 0.0.1
 */
public class LogoPane extends StackPane {

	public LogoPane() {
		init();
	}

	private void init() {
		setPadding(new Insets(10));

		// TODO Add logo imageview
		// Temporary layout and styling
		Label tmp = new Label("Banner image placeholder");
		tmp.setFont(new Font(50));
		tmp.setStyle("-fx-text-fill: white;");
		getChildren().add(tmp);

		setAlignment(tmp, Pos.TOP_LEFT);
		setStyle("-fx-background-color: black;");
	}
}