package uk.ac.aber.cs221.group15.gui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import uk.ac.aber.cs221.group15.TaskerCLI;

/**
 * @author Darren White
 * @version 0.0.1
 * @since 0.0.1
 */
public class NavigationPane extends GridPane {

	private DashboardView db;
	private TasksView tasks;

	public NavigationPane(StackPane stack) {
		init(stack);
	}

	private void init(StackPane stack) {
		db = new DashboardView();
		tasks = new TasksView();

		setPadding(new Insets(10));
		setHgap(10);
		setVgap(10);
		// Temporary styling
		setStyle("-fx-background-color: rgb(0, 100, 0)");

		// The navigation link to the Dashboard view
		Label lblDb = new Label("Dashboard");
		lblDb.setOnMouseClicked(event -> stack.getChildren().set(0, db));
		lblDb.setFont(new Font(20));
		lblDb.setStyle("-fx-text-fill: white;");
		add(lblDb, 0, 0);

		// The navigation link to the Tasks view
		Label lblTasks = new Label("Tasks");
		lblTasks.setOnMouseClicked(event -> stack.getChildren().set(0, tasks));
		lblTasks.setFont(new Font(20));
		lblTasks.setStyle("-fx-text-fill: white;");
		add(lblTasks, 0, 1);

		// The logout button (label in this case)
		Label lblLogout = new Label("Logout");
		lblLogout.setOnMouseClicked(event -> logout());
		lblLogout.setFont(new Font(20));
		lblLogout.setStyle("-fx-text-fill: white;");
		add(lblLogout, 0, 2);

		// Set default view as Dashboard
		stack.getChildren().add(db);
	}

	private void logout() {
		// Hide the current window
		getScene().getWindow().hide();

		// Create new stage to restart
		Stage stage = new Stage();
		// Show login again
		if (TaskerCLI.startLogin(stage)) {
			// If we are logged in
			// Display main window again
			TaskerCLI.startOverview(stage);
		}

		// Application will exit automatically
		// This is because we have hidden all windows
		// so the application will exit (assuming
		// implicit exit is set as true)
		// Platform.setImplicitExit(boolean) is default
		// as true
	}
}