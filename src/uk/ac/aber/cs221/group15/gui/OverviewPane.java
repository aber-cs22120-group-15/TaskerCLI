package uk.ac.aber.cs221.group15.gui;

import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;

/**
 * @author Darren White
 * @version 0.0.1
 * @since 0.0.1
 */
public class OverviewPane extends GridPane {

	public OverviewPane() {
		init();
	}

	private void init() {
		// Used to display the logo/banner
		LogoPane logo = new LogoPane();
		add(logo, 0, 0, 2, 1);

		// Used to display the current view (dashboard, task list, etc.)
		StackPane stack = new StackPane();
		add(stack, 1, 1);

		// Used to navigate each view
		NavigationPane nav = new NavigationPane(stack);
		add(nav, 0, 1);

		// Column 0 - for navigation (fixed at 150px width)
		ColumnConstraints cc0 = new ColumnConstraints();
		cc0.setMinWidth(150);
		cc0.setMaxWidth(150);

		// Column 1 - for overview (fill remaining width)
		ColumnConstraints cc1 = new ColumnConstraints();
		cc1.setPercentWidth(100);

		// Change the column sizes
		getColumnConstraints().addAll(cc0, cc1);

		// Row 0 - for logo/banner (fixed at 100px)
		RowConstraints rw0 = new RowConstraints();
		rw0.setMinHeight(100);
		rw0.setMaxHeight(100);

		// Row 1 - for current view/stack (fill remaining height)
		RowConstraints rw1 = new RowConstraints();
		rw1.setPercentHeight(100);

		getRowConstraints().addAll(rw0, rw1);
	}
}