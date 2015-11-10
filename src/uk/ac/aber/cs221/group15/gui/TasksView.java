package uk.ac.aber.cs221.group15.gui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;

/**
 * @author Darren White
 * @version 1.0
 * @since 1.0
 */
public class TasksView extends GridPane {

	public TasksView() {
		init();
	}

	private void init() {
		setPadding(new Insets(10));
		setHgap(10);
		setVgap(10);
		setStyle("-fx-background-color: rgb(100, 0, 100);");

		Label lblTitle = new Label("Tasks");
		lblTitle.setFont(new Font(30));
		lblTitle.setStyle("-fx-text-fill: white;");

		add(lblTitle, 0, 0);
	}
}