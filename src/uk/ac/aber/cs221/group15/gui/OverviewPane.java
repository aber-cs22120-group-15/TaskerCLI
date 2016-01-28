package uk.ac.aber.cs221.group15.gui;

import javafx.geometry.Insets;
import javafx.scene.layout.*;

/**
 * This class places each of the following panes in their
 * correct position: LogoPane, StackPane (for current view),
 * and the NavigationPane
 *
 * @author Darren White
 * @version 0.0.3
 */
public class OverviewPane extends GridPane {

	/**
	 * Represents the height of the logo/banner
	 */
	private static final int BANNER_HEIGHT = 160;

	/**
	 * Represents the width of the navigation pane
	 */
	private static final int NAVIGATION_WIDTH = 150;

	/**
	 * Creates a new OverviewPane
	 *
	 * @param token The token for the current user
	 */
	public OverviewPane(String token) {
		init(token);
	}

	/**
	 * Initializes this pane and its components
	 *
	 * @param token The token for the current user
	 */
	private void init(String token) {
		// Set padding & gaps to 0px
		setPadding(new Insets(0));
		setHgap(0);
		setVgap(0);

		// Used to display the logo/banner
		LogoPane logo = new LogoPane();
		add(logo, 0, 0, 2, 1);

		// Used to display the current view (dashboard, task list, etc.)
		StackPane stack = new StackPane();
		add(stack, 1, 1);

		// Used to navigate each view
		NavigationPane nav = new NavigationPane(stack, token);
		add(nav, 0, 1);

		// Column 0 - for navigation (fixed at 150px width)
		ColumnConstraints cc0 = new ColumnConstraints();
		cc0.setMinWidth(NAVIGATION_WIDTH);
		cc0.setMaxWidth(NAVIGATION_WIDTH);

		// Column 1 - for overview (fill remaining width)
		ColumnConstraints cc1 = new ColumnConstraints();
		cc1.setHgrow(Priority.ALWAYS);

		// Change the column sizes
		getColumnConstraints().addAll(cc0, cc1);

		// Row 0 - for logo/banner (fixed width)
		RowConstraints rw0 = new RowConstraints();
		rw0.setMinHeight(BANNER_HEIGHT);
		rw0.setMaxHeight(BANNER_HEIGHT);

		// Row 1 - for current view/stack (fill remaining height)
		RowConstraints rw1 = new RowConstraints();
		rw1.setVgrow(Priority.ALWAYS);

		// Change the row sizes
		getRowConstraints().addAll(rw0, rw1);
	}
}