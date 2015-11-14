package uk.ac.aber.cs221.group15;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import uk.ac.aber.cs221.group15.gui.Login;
import uk.ac.aber.cs221.group15.gui.OverviewPane;

/**
 * The main entry point to the application which
 * displays the login window and if successful
 * will then show the main window
 *
 * @author Darren White
 * @version 0.0.1
 * @since 0.0.1
 */
public class TaskerCLI extends Application {

	/**
	 * The name of the application
	 */
	public static final String APP_NAME = "TaskerCLI";

	/**
	 * The main window width
	 */
	public static final double APP_WIDTH = 800;

	/**
	 * The main window height
	 */
	public static final double APP_HEIGHT = 600;

	/**
	 * The base url of the database
	 */
	public static final String URL_PREFIX = "http://users.aber.ac.uk/dkm2/TaskerMAN/";

	/**
	 * The base api url with the method suffix (and %s for the method)
	 */
	public static final String URL_METHOD = URL_PREFIX + "api.php?method=%s";

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
		// Begin login protocol
		if (startLogin(stage)) {
			// Show the main overview window
			startOverview(stage);
		}

		// User did not login
		// So we exit
	}

	/**
	 * Displays the login window
	 *
	 * @param stage The primary stage to be used
	 * @return If the user successfully logged in
	 */
	public static boolean startLogin(Stage stage) {
		// Create login window
		// Use primary stage as owner
		Login login = new Login(stage);
		// Resize the login window
		login.sizeToScene();
		// Show and wait until it closes
		login.showAndWait();

		// Return if we are logged in or not
		return login.isLoggedIn();
	}

	/**
	 * Displays the main window
	 *
	 * @param stage The primary stage
	 */
	public static void startOverview(Stage stage) {
		// Initialize and show main app
		OverviewPane ovp = new OverviewPane();
		// Create a new scene with the default width & height
		Scene scene = new Scene(ovp, APP_WIDTH, APP_HEIGHT);

		// Set the primary stage scene and the default title
		stage.setScene(scene);
		stage.setTitle(APP_NAME);
		// Show main overview window
		stage.show();
	}
}