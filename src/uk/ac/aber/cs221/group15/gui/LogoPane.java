package uk.ac.aber.cs221.group15.gui;

import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import uk.ac.aber.cs221.group15.TaskerCLI;

/**
 * This class will display a banner/logo for the client
 *
 * @author Darren White
 * @version 0.0.3
 */
public class LogoPane extends StackPane {

	/**
	 * Creates a new logo pane
	 */
	public LogoPane() {
		init();
	}

	/**
	 * Initialize this pane and its components
	 */
	private void init() {
		// Set the id for css styles
		setId("logo-pane");

		// Create an image view to hold the banner logo image
		ImageView img = new ImageView(TaskerCLI.getResource("resources/images/banner.png").toExternalForm());
		// Add it to this pane
		getChildren().add(img);
		// Center the banner
		setAlignment(img, Pos.CENTER_LEFT);
	}
}