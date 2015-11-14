package uk.ac.aber.cs221.group15.gui;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;

/**
 * Used to display all tasks for the user in a table
 * and each task can be viewed in detail and can be
 * set as completed (or complete each step)
 *
 * @author Darren White
 * @version 0.0.1
 * @since 0.0.1
 */
public class TasksView extends GridPane {

	/**
	 * Creates a new task view
	 */
	public TasksView() {
		init();
	}

	/**
	 * Initializes this view and its components
	 */
	private void init() {
		// Set padding & gaps to 10px
		setPadding(new Insets(10));
		setHgap(10);
		setVgap(10);

		// Create a temporary placeholder in the center
		Label lblTitle = new Label("Tasks");
		lblTitle.setFont(new Font(20));
		lblTitle.setStyle("-fx-text-fill: black;");

		setHalignment(lblTitle, HPos.CENTER);
		setValignment(lblTitle, VPos.CENTER);

		add(lblTitle, 0, 0);

		// Column 0 - fill everything
		ColumnConstraints cc0 = new ColumnConstraints();
		cc0.setHgrow(Priority.ALWAYS);

		// Change the column size
		getColumnConstraints().add(cc0);

		// Row 0 - fill everything
		RowConstraints rw0 = new RowConstraints();
		rw0.setVgrow(Priority.ALWAYS);

		// Change the row size
		getRowConstraints().add(rw0);
	}
}