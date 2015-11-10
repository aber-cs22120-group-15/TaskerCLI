package uk.ac.aber.cs221.group15.gui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;

/**
 * @author Darren White
 * @version 1.0
 * @since 1.0
 */
public class NavigationPane extends GridPane {

	private final DashboardView db = new DashboardView();
	private final TasksView tasks = new TasksView();

	public NavigationPane(StackPane stack) {
		init(stack);
	}

	private void init(StackPane stack) {
		setPadding(new Insets(10));
		setHgap(10);
		setVgap(10);
		setStyle("-fx-background-color: rgb(0, 100, 0)");

		Label lblDb = new Label("Dashboard");
		lblDb.setOnMouseClicked(event -> stack.getChildren().set(0, db));
		lblDb.setFont(new Font(20));
		lblDb.setStyle("-fx-text-fill: white;");
		add(lblDb, 0, 0);

		Label lblTasks = new Label("Tasks");
		lblTasks.setOnMouseClicked(event -> stack.getChildren().set(0, tasks));
		lblTasks.setFont(new Font(20));
		lblTasks.setStyle("-fx-text-fill: white;");
		add(lblTasks, 0, 1);

		Label lblLogout = new Label("Logout");
		lblLogout.setOnMouseClicked(event -> logout());
		lblLogout.setFont(new Font(20));
		lblLogout.setStyle("-fx-text-fill: white;");
		add(lblLogout, 0, 2);

		// Set default view as Dashboard
		stack.getChildren().add(db);
	}

	private void logout() {
		getScene().getWindow().hide();
	}
}