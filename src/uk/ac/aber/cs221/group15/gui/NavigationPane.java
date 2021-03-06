package uk.ac.aber.cs221.group15.gui;

import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import uk.ac.aber.cs221.group15.TaskerCLI;

/**
 * This class allows navigation between different views
 * where the current view will be at index 0 of the StackPane
 *
 * @author Darren White
 * @version 0.0.9
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
		DashboardView dv = new DashboardView(token);
		TaskView tv = new TaskView(token);

		// Set id for css
		setId("nav-pane");
		// Set padding and gaps to 0px
		setPadding(new Insets(0, 0, 0, 0));
		setHgap(0);
		setVgap(0);

		// The navigation link to the Dashboard view
		NavButton paneDb = new NavButton("Dashboard");
		// Mouse EventHandler for on click to change the view to the dashboard
		paneDb.setOnMouseClicked(e -> setCurrentView(paneDb, stack, dv));
		add(paneDb, 0, 0);

		// The navigation link to the Tasks view
		NavButton paneTasks = new NavButton("Tasks");
		// Mouse EventHandler for the tasks view
		paneTasks.setOnMouseClicked(e -> setCurrentView(paneTasks, stack, tv));
		add(paneTasks, 0, 1);

		// A button used to force sync the tasks
		NavButton syncTasks = new NavButton("Refresh");
		// Prevent the selection of the button as its not a view pane
		syncTasks.addEventFilter(MouseEvent.MOUSE_RELEASED, Event::consume);
		// On click event handler
		syncTasks.setOnMouseClicked(e -> {
			// Force a sync update
			TaskerCLI.getTaskSync().forceSync();
			// Unselect the button
			syncTasks.setSelected(false);
		});
		add(syncTasks, 0, 2);

		// The logout button (label in this case)
		NavButton paneLogout = new NavButton("Logout");
		// Logout on click
		paneLogout.setOnMouseClicked(e -> logout());
		add(paneLogout, 0, 3);

		// Column 0 - Ensure everything fills the nav pane
		ColumnConstraints cc0 = new ColumnConstraints();
		cc0.setHgrow(Priority.ALWAYS);

		// Change the column size
		getColumnConstraints().add(cc0);

		// Each nav section has the same height
		RowConstraints rw0 = new RowConstraints();
		rw0.setMinHeight(NAV_ITEM_HEIGHT);
		rw0.setMaxHeight(NAV_ITEM_HEIGHT);

		// Each nav section has the same height
		RowConstraints rw1 = new RowConstraints();
		rw1.setMinHeight(NAV_ITEM_HEIGHT);
		rw1.setMaxHeight(NAV_ITEM_HEIGHT);

		// Each nav section has the same height
		RowConstraints rw2 = new RowConstraints();
		rw2.setMinHeight(NAV_ITEM_HEIGHT);
		rw2.setMaxHeight(NAV_ITEM_HEIGHT);

		// Change the row sizes
		getRowConstraints().addAll(rw0, rw1, rw2);

		// Set the current view as the dashboard
		setCurrentView(paneDb, stack, dv);
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
		// Select the button
		navBtn.setSelected(true);

		// If there is no current view, add it
		if (stack.getChildren().isEmpty()) {
			stack.getChildren().add(view);
		} else {
			// Otherwise set the current view
			stack.getChildren().set(0, view);
		}
	}

	/**
	 * Used as a navigation button
	 */
	private class NavButton extends ToggleButton {

		/**
		 * Creates a new unselected NavButton with a caption
		 *
		 * @param text The text to use for the button
		 */
		private NavButton(String text) {
			super(text);
			// Add the css class for styling
			getStyleClass().add("nav-btn");
			// Make the button fill its cell
			setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			// On mouse click unselect all other NavButtons
				setOnMouseReleased(e -> NavigationPane.this.getChildren().stream().filter(n ->
						n instanceof NavButton).forEach(n -> ((NavButton) n).setSelected(false)));
		}
	}
}