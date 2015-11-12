package uk.ac.aber.cs221.group15;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import uk.ac.aber.cs221.group15.gui.Login;
import uk.ac.aber.cs221.group15.gui.OverviewPane;

/**
 * @author Darren White
 * @version 0.0.1
 * @since 0.0.1
 */
public class TaskerCLI extends Application {

	public static final String APP_NAME = "TaskerCLI";
	public static final double APP_WIDTH = 800;
	public static final double APP_HEIGHT = 600;

	public static final String URL_PREFIX = "http://users.aber.ac.uk/dkm2/TaskerMAN/";
	public static final String URL_METHOD = URL_PREFIX + "api.php?method=%s";

	public static void main(String[] args) {
		launch(args);
	}

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

	public static void startOverview(Stage stage) {
		// Initialize and show main app
		OverviewPane ovp = new OverviewPane();
		Scene scene = new Scene(ovp, APP_WIDTH, APP_HEIGHT);

		stage.setScene(scene);
		stage.setTitle(APP_NAME);
		// Show main overview window
		stage.show();
	}
}