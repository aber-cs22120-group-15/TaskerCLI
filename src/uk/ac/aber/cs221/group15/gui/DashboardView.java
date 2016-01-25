package uk.ac.aber.cs221.group15.gui;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import uk.ac.aber.cs221.group15.TaskerCLI;
import uk.ac.aber.cs221.group15.task.Task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * This class is used to represent the Dashboard in the Overview
 * of the client which will display statistics and an overview of
 * upcoming tasks and a few major details
 *
 * @author Darren White
 * @version 0.0.10
 */
public class DashboardView extends GridPane {

	/**
	 * Defines the height for the four statistics panels
	 */
	private static final int STATISTICS_HEIGHT = 115;

	/**
	 * The number of tasks to show for the dashboard overview
	 */
	private static final int MAX_TASKS = 10;

	/**
	 * Creates a new Dashboard
	 *
	 * @param token The token for the current user
	 */
	public DashboardView(String token) {
		init(token);
	}

	/**
	 * Creates the stat panes to be displayed on the dashboard
	 *
	 * @return The list of stat panes
	 */
	private List<StatPane> createStatPanes() {
		// Store panes in a list
		List<StatPane> panes = new ArrayList<>();

		// The total tasks statistic
		Callable<Integer> totalTasks = TaskerCLI.getTaskSync().getTasks()::size;
		// The color for the total tasks stat
		Callable<Paint> totalColor = () -> Color.rgb(40, 140, 255);

		// Add the total takes stat
		panes.add(new StatPane("Total Tasks", totalTasks, totalColor));

		// The outstanding tasks statistic
		Callable<Integer> outstandingTasks = () -> {
			// Filter tasks with status allocated
			return TaskerCLI.getTaskSync().getTasks().filtered(t -> t.getStatus() == Task.ALLOCATED).size();
		};
		// The color for the outstanding tasks stat
		Callable<Paint> outstandingColor = () -> {
			// Get the current stat number
			int stat = outstandingTasks.call();

			// Green if 0, Orange < 10 otherwise red
			if (stat == 0) {
				return Color.GREEN;
			} else if (stat < 10) {
				return Color.ORANGE;
			} else {
				return Color.RED;
			}
		};

		// Add the outstanding takes stat
		panes.add(new StatPane("Outstanding Tasks", outstandingTasks, outstandingColor));

		// The statistic value for overdue tasks
		Callable<Integer> overdueTasks = () -> {
			// The current date
			Calendar now = Calendar.getInstance();
			// Filter tasks to dates in past with status allocated
			return TaskerCLI.getTaskSync().getTasks().filtered(t -> t.getDateDue().compareTo(now) < 0 && t.getStatus() == Task.ALLOCATED).size();
		};
		// The color for the overdue tasks stat
		Callable<Paint> overdueColor = () -> {
			// Get the current stat number
			int stat = overdueTasks.call();

			// If 0 overdue tasks, set it as green otherwise red
			if (stat == 0) {
				return Color.GREEN;
			} else {
				return Color.RED;
			}
		};

		// The overdue tasks statistic
		panes.add(new StatPane("Overdue Tasks", overdueTasks, overdueColor));

		// Return the stat panes
		return panes;
	}

	/**
	 * Creates a table to display tasks
	 *
	 * @return A new table to display tasks
	 */
	private TableView<Task> createTaskTable() {
		// Create the task table for an overview of tasks
		TableView<Task> table = new TableView<>();

		table.itemsProperty().bind(Bindings.createObjectBinding(() -> {
			ObservableList<Task> tasks = TaskerCLI.getTaskSync().getTasks();
			return tasks.filtered(t -> tasks.indexOf(t) < MAX_TASKS);
		}, TaskerCLI.getTaskSync().getTasks()));

		// Create columns: task, due date, member, and status
		TableColumn<Task, String> titleCol = new TableColumn<>("Task");
		TableColumn<Task, Calendar> dueDateCol = new TableColumn<>("Due date");
		TableColumn<Task, String> creatorCol = new TableColumn<>("Assigned by");
		TableColumn<Task, String> statusCol = new TableColumn<>("Status");

		// Set each of the cell value factories (which fields they
		// correspond to in the Task class)
		titleCol.setCellValueFactory(t -> t.getValue().titleProperty());
		// Display the cell as a CalendarCell
		dueDateCol.setCellFactory(param -> new CalendarCell<>());
		dueDateCol.setCellValueFactory(t -> t.getValue().dateDueProperty());
		creatorCol.setCellValueFactory(t -> t.getValue().creatorProperty());
		// Convert the status integer to readable string
		statusCol.setCellValueFactory(t -> Bindings.createStringBinding(() ->
						t.getValue().getStatusString(),
				t.getValue().statusProperty()));

		// Set the column dyanmic sizes
		// Title col - 35% width (subtract 2 so that the horizontal scrollbar doesn't show)
		// Due date col - 25% width
		// Creator name col - 25% width
		// Status col - 15% width
		titleCol.prefWidthProperty().bind(table.widthProperty().multiply(0.35).subtract(2));
		dueDateCol.prefWidthProperty().bind(table.widthProperty().multiply(0.25));
		creatorCol.prefWidthProperty().bind(table.widthProperty().multiply(0.25));
		statusCol.prefWidthProperty().bind(table.widthProperty().multiply(0.15));

		// Add the columns to the table
		table.getColumns().add(titleCol);
		table.getColumns().add(dueDateCol);
		table.getColumns().add(creatorCol);
		table.getColumns().add(statusCol);

		// Return the new table created
		return table;
	}

	/**
	 * Initializes this view and all of its components
	 *
	 * @param token The token for the current user
	 */
	private void init(String token) {
		// Set the id for css
		setId("db-view");
		// Set padding to 10 & hgap to 0px & vgap to 10px
		setPadding(new Insets(10));
		setHgap(0);
		setVgap(10);

		// Creates the panes for the statistics
		List<StatPane> panes = createStatPanes();

		// Add the panes to the first row, one in each column
		for (int i = 0; i < panes.size(); i++) {
			add(panes.get(i), i, 0);
		}

		// The caption for the table
		Label lblTableCaption = new Label("Upcoming " + MAX_TASKS + " Tasks");
		// Set the id for css
		lblTableCaption.setId("lbl-task-overview");
		// Center it
		GridPane.setHalignment(lblTableCaption, HPos.CENTER);
		// Add it below the stat panes and above the table
		// allowing it to span all columns and one row
		add(lblTableCaption, 0, 1, panes.size(), 1);

		// Create and add the table to the second row (row 1),
		// in the first column (col 0)
		// and allow it to span all columns and one row
		add(createTaskTable(), 0, 2, panes.size(), 1);

		// Make each column equidistant for each panel
		for (int i = 0; i < panes.size(); i++) {
			ColumnConstraints cc = new ColumnConstraints();
			cc.setPercentWidth(100 / panes.size());
			getColumnConstraints().add(cc);
		}

		// Row 0 - The four statistic panels (fixed width)
		RowConstraints rw0 = new RowConstraints();
		rw0.setMinHeight(STATISTICS_HEIGHT);
		rw0.setMaxHeight(STATISTICS_HEIGHT);

		// Row 1 - Table caption (fit to the label size)
		RowConstraints rw1 = new RowConstraints();
		rw1.setVgrow(Priority.NEVER);

		// Row 2 - tasks overview table (fill remaining height)
		RowConstraints rw2 = new RowConstraints();
		rw2.setVgrow(Priority.ALWAYS);

		// Change the row sizes
		getRowConstraints().addAll(rw0, rw1, rw2);
	}

	/**
	 * A pane used to display statistical information for tasks
	 */
	private class StatPane extends GridPane {

		/**
		 * The label for the caption
		 */
		private final Label lblCap;

		/**
		 * The label for the statistic
		 */
		private final Label lblStat;

		/**
		 * Creates a new StatPane with a caption and a statistic
		 *
		 * @param caption   The caption for the stat
		 * @param statFunc  A function used to get the statistic value
		 * @param colorFunc A function used to get the statistic color
		 */
		private StatPane(String caption, Callable<Integer> statFunc, Callable<Paint> colorFunc) {
			// Add the styleclass for css
			getStyleClass().add("stat-pane");
			// Create a label for the caption
			lblCap = new Label(caption);
			// Add the styleclass for css
			lblCap.getStyleClass().add("caption");

			// Create the stat label and bind the stat value to it
			lblStat = new Label();
			// Add the styleclass for css
			lblStat.getStyleClass().add("statistic");
			// Bind the stat value as the callable value with the tasks
			// list as a dependency
			lblStat.textProperty().bind(Bindings.format("%d", Bindings.createIntegerBinding(statFunc, TaskerCLI.getTaskSync().getTasks())));
			// Bind the stat color callable function with tasks as dependency
			lblStat.textFillProperty().bind(Bindings.createObjectBinding(colorFunc, TaskerCLI.getTaskSync().getTasks()));

			// Make a placeholder for the stat
			StackPane pane = new StackPane();

			// Make it fill the pane
			setHgrow(pane, Priority.ALWAYS);
			setVgrow(pane, Priority.NEVER);

			// Add the stat to the placeholder
			pane.getChildren().add(lblStat);

			// Add the statistic placeholder
			add(pane, 0, 0);

			// Position the caption at the bottom center
			setHalignment(lblCap, HPos.CENTER);
			setValignment(lblCap, VPos.BOTTOM);

			// Add the caption in the second row
			add(lblCap, 0, 1);
		}
	}
}