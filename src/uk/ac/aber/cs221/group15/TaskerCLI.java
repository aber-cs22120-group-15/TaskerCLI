package uk.ac.aber.cs221.group15;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import uk.ac.aber.cs221.group15.gui.Login;
import uk.ac.aber.cs221.group15.gui.OverviewPane;

/**
 * @author Darren White
 * @version 1.0
 * @since 1.0
 */
public class TaskerCLI extends Application {

	public static final String APP_NAME = "TaskerCLI";
	public static final double APP_WIDTH = 800;
	public static final double APP_HEIGHT = 600;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) {
		// Begin login protocol
		Login login = new Login(stage);
		login.sizeToScene();
		login.showAndWait();

		if (!login.isLoggedIn()) {
			// return;
		}

		// Initialize and show main app
		OverviewPane ovp = new OverviewPane();
		Scene scene = new Scene(ovp, APP_WIDTH, APP_HEIGHT, Color.WHITE);

		stage.setScene(scene);
		stage.setTitle(APP_NAME);
		stage.show();
	}
}