package uk.ac.aber.cs221.group15.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import uk.ac.aber.cs221.group15.TaskerCLI;

/**
 * This class allows navigation between different views
 * where the current view will be at index 0 of the StackPane
 *
 * @author Darren White
 * @version 0.0.3
 */
public class NavigationPane extends GridPane {

	/**
	 * The height for each navigation item
	 */
	private static final int NAV_ITEM_HEIGHT = 50;

	/**
	 * Creates a new navigation pane using the StackPane
	 * to display the current view at index 0
	 *
	 * @param stack The StackPane to display the current view
	 * @param token The token for the current user
	 */
	public NavigationPane(StackPane stack, String token) {
		init(stack, token);
	}

	/**
	 * Initialize this pane and its components
	 *
	 * @param stack The StackPane to display the current view
	 * @param token The token for the current user
	 */
	private void init(StackPane stack, String token) {
		// Initialize the different views
		DashboardView db = new DashboardView(token);
		TaskView tasks = new TaskView(token);

		// Set padding and gaps to 0px
		setPadding(new Insets(0, 0, 0, 0));
		setHgap(0);
		setVgap(0);

		// Set background - TODO use css later
		setStyle("-fx-background-color: rgb(230, 230, 230);");

		// The navigation link to the Dashboard view
		NavButton paneDb = new NavButton("Dashboard");
		// Mouse EventHandler for on click to change the view to the dashboard
		paneDb.setOnMouseClicked(e -> setCurrentView(paneDb, stack, db));
		add(paneDb, 0, 0);

		// The navigation link to the Tasks view
		NavButton paneTasks = new NavButton("Tasks");
		// Mouse EventHandle for the tasks view
		paneTasks.setOnMouseClicked(e -> setCurrentView(paneTasks, stack, tasks));
		add(paneTasks, 0, 1);

		// The logout button (label in this case)
		NavButton paneLogout = new NavButton("Logout");
		// Logout on click
		paneLogout.setOnMouseClicked(e -> logout());
		add(paneLogout, 0, 2);

		// Column 0 - Ensure everything fills the nav pane
		ColumnConstraints cc0 = new ColumnConstraints();
		cc0.setHgrow(Priority.ALWAYS);

		// Change the column size
		getColumnConstraints().add(cc0);

		// Row 0 - The four statistic panels (fixed width at 200px)
		// Each nav section has the same height
		RowConstraints rw0 = new RowConstraints();
		rw0.setMinHeight(NAV_ITEM_HEIGHT);
		rw0.setMaxHeight(NAV_ITEM_HEIGHT);

		// Row 1 - tasks overview table (fill remaining height)
		// Each nav section has the same height
		RowConstraints rw1 = new RowConstraints();
		rw1.setMinHeight(NAV_ITEM_HEIGHT);
		rw1.setMaxHeight(NAV_ITEM_HEIGHT);

		// Row 1 - tasks overview table (fill remaining height)
		// Each nav section has the same height
		RowConstraints rw2 = new RowConstraints();
		rw2.setMinHeight(NAV_ITEM_HEIGHT);
		rw2.setMaxHeight(NAV_ITEM_HEIGHT);

		// Change the row sizes
		getRowConstraints().addAll(rw0, rw1, rw2);

		// Set the current view as the dashboard
		setCurrentView(paneDb, stack, db);
	}

	/**
	 * Log the user out and redisplay the login window and
	 * start the main application again if the user logs back in
	 */
	private void logout() {
		// Hide the current window
		getScene().getWindow().hide();

		// Create new stage to restart
		Stage stage = new Stage();
		// Store the user unique token/key
		String token;
		// Show login again
		if ((token = TaskerCLI.startLogin(stage)) != null) {
			// If we are logged in
			// Display main window again
			TaskerCLI.startOverview(stage, token);
		}

		// Application will exit automatically
		// This is because we have hidden all windows
		// so the application will exit (assuming
		// implicit exit is set as true)
		// Platform.setImplicitExit(boolean) is default
		// as true
	}

	/**
	 * Sets the current view on the StackPane at index 0
	 *
	 * @param navBtn The NavButton which is to be clicked
	 * @param stack  The StackPane to display the view
	 * @param view   The view to display
	 */
	private void setCurrentView(NavButton navBtn, StackPane stack, Pane view) {
		// Set all NavButton styles as unselected
		getChildren().stream().filter(n -> n instanceof NavButton).forEach(n -> {
			NavButton pane = (NavButton) n;
			pane.unselect();
		});

		// Set the NavButton style as selected
		navBtn.select();

		// If there is no current view, add it
		if (stack.getChildren().isEmpty()) {
			stack.getChildren().add(view);
		} else {
			// Otherwise set the current view
			stack.getChildren().set(0, view);
		}
	}

	/**
	 * Used as a navigation button/pane
	 */
	private static class NavButton extends StackPane {

		/**
		 * The label to display a descriptive title
		 */
		private final Label lbl;

		/**
		 * Useful variables to test if the NavButton
		 * is the current view or is being hovered (by the mouse)
		 */
		private boolean selected, hover;

		/**
		 * Creates a new unselected NavButton with a label
		 *
		 * @param text The text to use for the label
		 */
		private NavButton(String text) {
			// Create a label with the font size
			lbl = new Label(text);
			lbl.setFont(new Font(16));

			// Add it and center it
			getChildren().add(lbl);
			StackPane.setAlignment(lbl, Pos.CENTER);

			// Default is to unselect it
			unselect();

			// Add mouse event handlers for enter/exit
			setOnMouseEntered(e -> enter());
			setOnMouseExited(e -> exit());
		}

		/**
		 * Used when the mouse enters this pane
		 * Sets the style as selected
		 */
		private void enter() {
			// Change the style if we are not selected
			if (!selected) {
				setStyle("-fx-background-color: rgba(200, 200, 200);");
			}

			// Mouse entered, we are hovering
			hover = true;
		}

		/**
		 * Used when the mouse exits this pane
		 * Sets the style as unselected
		 */
		private void exit() {
			// Only unselect if it isn't the current view
			if (!selected) {
				unselect();
			}

			// Mouse has exited, so we are not hovering
			hover = false;
		}

		/**
		 * Changes the style to make this panel the
		 * selected view (current display)
		 */
		private void select() {
			// Change the style to show that it is selected
			setStyle("-fx-background-color: rgb(40, 140, 255);");
			lbl.setStyle("-fx-text-fill: white;");
			selected = true;
		}

		/**
		 * Changes the style to make this panel not selected
		 */
		private void unselect() {
			// Change the style to default to show it is not selected
			setStyle("-fx-background-color: rgb(230, 230, 230);" +
					"-fx-text-fill: rgb(40, 140, 255);");
			lbl.setStyle("-fx-text-fill: rgb(40, 140, 255);");
			selected = false;
		}
	}
}