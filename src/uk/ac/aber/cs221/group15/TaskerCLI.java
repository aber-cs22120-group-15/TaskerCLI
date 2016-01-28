package uk.ac.aber.cs221.group15;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import uk.ac.aber.cs221.group15.gui.Login;
import uk.ac.aber.cs221.group15.gui.OverviewPane;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;

/**
 * The main entry point to the application which
 * displays the login window and if successful
 * will then show the main window
 *
 * @author Darren White
 * @version 0.0.6
 */
public class TaskerCLI extends Application {

	/**
	 * The title of the application window
	 */
	public static final String APP_NAME = "TaskerCLI";

	/**
	 * The current version for the application
	 */
	public static final String APP_VERSION = "0.1.0";

	/**
	 * The main window width
	 */
	private static final double APP_WIDTH = 800;

	/**
	 * The main window height
	 */
	private static final double APP_HEIGHT = 600;

	/**
	 * The instance of the SyncTask used for syncing
	 */
	private static TaskSync taskSync;

	/**
	 * Get the resource at the path
	 *
	 * @param path The relative path for the resource
	 * @return The absolute URL for the resource
	 */
	public static URL getResource(String path) {
		// Try and find the resource locally (relative in the JAR file)
		URL in = TaskerCLI.class.getResource('/' + path);

		// Not found so we get the resource relative to the working directory
		if (in == null) {
			try {
				in = Paths.get(path).toUri().toURL();
			} catch (MalformedURLException ignored) {
			}
		}

		return in;
	}

	/**
	 * Gets the instance of the task sync
	 *
	 * @return The instance of the TaskSync class
	 */
	public static TaskSync getTaskSync() {
		return taskSync;
	}

	/**
	 * Gets the user directory to store files in
	 *
	 * @return The user directory path
	 */
	public static String getUserHomeDir() {
		// Use system properties to get the user home directory
		return System.getProperty("user.home");
	}

	/**
	 * The main entry point
	 *
	 * @param args The program arguments - none should be used
	 */
	public static void main(String[] args) {
		// Launch the javafx application
		launch(args);
	}

	/**
	 * Starts the application with a stage
	 *
	 * @param stage The primary stage to be used
	 */
	@Override
	public void start(Stage stage) {
		// Store the user unique token/key
		String token;

		// Begin login protocol
		if ((token = startLogin(stage)) != null) {
			// Show the main overview window
			startOverview(stage, token);
		}

		// User did not login
		// So we exit
	}

	/**
	 * Displays the login window
	 *
	 * @param stage The primary stage to be used
	 * @return The user token
	 */
	public static String startLogin(Stage stage) {
		// Create login window
		// Use primary stage as owner
		Login login = new Login(stage);

		// Add the taskbar icon
		login.getIcons().add(new Image(getResource("resources/images/icon.png").toExternalForm()));

		// Resize the login window
		login.sizeToScene();
		// Show and wait until it closes
		login.showAndWait();

		// Return if we are logged in or not
		return login.getToken();
	}

	/**
	 * Displays the main window
	 *
	 * @param stage The primary stage
	 * @param token The user token to use
	 */
	public static void startOverview(Stage stage, String token) {
		// Start the task sync schedule
		taskSync = new TaskSync(token);

		// Initialize and show main app
		OverviewPane ovp = new OverviewPane(token);
		// Create a new scene with the default width & height
		Scene scene = new Scene(ovp, APP_WIDTH, APP_HEIGHT);

		// Add the taskbar icon
		stage.getIcons().add(new Image(getResource("resources/images/icon.png").toExternalForm()));

		// Set the stylesheet for css styling
		scene.getStylesheets().add(TaskerCLI.getResource("resources/css/TaskerCLI.css").toExternalForm());
		// Set the primary stage scene and the default title
		stage.setScene(scene);
		stage.setTitle(APP_NAME);
		// Maximize the window
		stage.setMaximized(true);
		// Show main overview window
		stage.show();
	}
}