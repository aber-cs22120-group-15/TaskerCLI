package uk.ac.aber.cs221.group15;

import javafx.application.Application;
import javafx.scene.Scene;
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
 * @version 0.0.2
 */
public class TaskerCLI extends Application {

	/**
	 * The title of the application window
	 */
	public static final String APP_NAME = "TaskerCLI";

	/**
	 *
	 */
	public static final String APP_VERSION = "0.0.4";

	/**
	 * The main window width
	 */
	private static final double APP_WIDTH = 800;

	/**
	 * The main window height
	 */
	private static final double APP_HEIGHT = 600;

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
			} catch (MalformedURLException e) {
				return null;
			}
		}

		return in;
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
	 */
	public static void startOverview(Stage stage, String token) {
		// Initialize and show main app
		OverviewPane ovp = new OverviewPane(token);
		// Create a new scene with the default width & height
		Scene scene = new Scene(ovp, APP_WIDTH, APP_HEIGHT);

		// Set the stylesheet for css styling
		scene.getStylesheets().add(TaskerCLI.getResource("resources/css/TaskerCLI.css").toExternalForm());
		// Set the primary stage scene and the default title
		stage.setScene(scene);
		stage.setTitle(APP_NAME);
		// Show main overview window
		stage.show();
	}
}