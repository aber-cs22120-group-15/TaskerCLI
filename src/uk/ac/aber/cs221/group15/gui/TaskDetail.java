package uk.ac.aber.cs221.group15.gui;

import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import uk.ac.aber.cs221.group15.task.Task;

/**
 * This class will display a new window which
 * will show a specific tasks details and allow
 * the ability to edit task step comments and
 * set tasks as completed (or uncompleted)
 *
 * @author Darren White
 * @version 0.0.1
 */
public class TaskDetail extends Stage {

	/**
	 * The title of the window
	 */
	private static final String APP_NAME = "Task Details";

	/**
	 * The main window width
	 */
	private static final double WIDTH = 400;

	/**
	 * The main window height
	 */
	private static final double HEIGHT = 600;

	/**
	 * Creates a new task detail window
	 *
	 * @param owner The main application
	 * @param task  The task to display details for
	 */
	public TaskDetail(Window owner, Task task) {
		// Set the owner as the main window
		initOwner(owner);
		// No need for the task details to be resizable
		setResizable(false);
		// Set the title of the window
		setTitle(APP_NAME);

		// Initialize the components
		init(task);
	}

	/**
	 * Initializes this windows components
	 *
	 * @param task The task to display details for
	 */
	private void init(Task task) {
		// Create the grid that we are going to
		// put all the component onto
		GridPane grid = new GridPane();
		// Create the scene
		Scene scene = new Scene(grid, WIDTH, HEIGHT);

		// TODO Add main components wuth functionality

		// Sets the scene
		setScene(scene);
	}
}