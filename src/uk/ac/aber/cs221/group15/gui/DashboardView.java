package uk.ac.aber.cs221.group15.gui;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.json.simple.parser.ParseException;
import uk.ac.aber.cs221.group15.service.TaskService;
import uk.ac.aber.cs221.group15.task.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * This class is used to represent the Dashboard in the Overview
 * of the client which will display statistics and an overview of
 * upcoming tasks and a few major details
 *
 * @author Darren White
 * @version 0.0.5
 */
public class DashboardView extends GridPane {

	/**
	 * Defines the height for the four statistics panels
	 */
	private static final int STATISTICS_HEIGHT = 115;

	/**
	 * The service used to submit requests to list all tasks
	 */
	private static final TaskService service = new TaskService();

	/**
	 * Creates a new Dashboard
	 *
	 * @param token The token for the current user
	 */
	public DashboardView(String token) {
		init(token);
	}

	private List<StatPane> createStatPanes(ObservableList<Task> tasks) {
		List<StatPane> panes = new ArrayList<>();

		// The outstanding tasks statistic
		Callable<Integer> outstandingTasks = () -> {
			// The current date
			Date now = new GregorianCalendar().getTime();
			// Filter tasks to dates in future and the task status is allocated
			return tasks.filtered(t -> t.getDateDue().compareTo(now) > 0 &&
					t.getStatus() == Task.ALLOCATED).size();
		};
		Callable<Paint> outstandingColor = () -> {
			int stat = outstandingTasks.call();

			if (stat == 0) {
				return Color.GREEN;
			} else if (stat < 10) {
				return Color.ORANGE;
			} else {
				return Color.RED;
			}
		};

		panes.add(new StatPane("Outstanding Tasks", outstandingTasks,
				outstandingColor, tasks));

		Callable<Integer> overdueTasks = () -> {
			// The current date
			Date now = new GregorianCalendar().getTime();
			// Filter tasks to dates in past
			return tasks.filtered(t -> t.getDateDue().compareTo(now) < 0).size();
		};
		Callable<Paint> overdueColor = () -> {
			int stat = overdueTasks.call();

			if (stat == 0) {
				return Color.GREEN;
			} else {
				return Color.RED;
			}
		};

		// The overdue tasks statistic
		panes.add(new StatPane("Overdue Tasks", overdueTasks, overdueColor, tasks));

		return panes;
	}

	/**
	 * Creates a table to display tasks
	 *
	 * @return A new table to display tasks
	 */
	private TableView<Task> createTaskTable(ObservableList<Task> tasks) {
		// Create the task table for an overview of tasks
		TableView<Task> table = new TableView<>(tasks);

		// Create four columns: id, task, due date, and member
		TableColumn<Task, Integer> idCol = new TableColumn<>("ID");
		TableColumn<Task, String> titleCol = new TableColumn<>("Task");
		TableColumn<Task, Date> dueDateCol = new TableColumn<>("Due date");
		TableColumn<Task, String> creatorCol = new TableColumn<>("Member");

		// Set each of the cell value factories (which fields they
		// correspond to in the Task class)
		idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
		titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
		dueDateCol.setCellValueFactory(new PropertyValueFactory<>("dateDue"));
		creatorCol.setCellValueFactory(new PropertyValueFactory<>("creator"));

		// Set the column dyanmic sizes
		// ID col - 5% width (- 2 so that the horizontal scrollbar doesn't show)
		// Title col - 40% width
		// Due date col - 30% width
		// Creator name col - 25% width
		idCol.prefWidthProperty().bind(table.widthProperty().multiply(0.05).subtract(2));
		titleCol.prefWidthProperty().bind(table.widthProperty().multiply(0.4));
		dueDateCol.prefWidthProperty().bind(table.widthProperty().multiply(0.3));
		creatorCol.prefWidthProperty().bind(table.widthProperty().multiply(0.25));

		// Add the four columns to the table
		table.getColumns().add(idCol);
		table.getColumns().add(titleCol);
		table.getColumns().add(dueDateCol);
		table.getColumns().add(creatorCol);

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
		// Set padding & gaps to 10px
		setPadding(new Insets(10));
		setHgap(10);
		setVgap(10);

		// The list of tasks in the overview
		ObservableList<Task> tasks = FXCollections.observableArrayList();

		try {
			// Try and load the tasks from the database
			tasks.addAll(service.getTasks(token));
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}

		// Creates the panes for the statistics
		List<StatPane> panes = createStatPanes(tasks);

		// Add the panes to the first row, one in each column
		for (int i = 0; i < panes.size(); i++) {
			add(panes.get(i), i, 0);
		}

		// Create the task table
		TableView<Task> taskTable = createTaskTable(tasks);

		// Add the table to the second row (row 1), in the first column (col 0)
		// and allow it to span all four columns and 1 row
		add(taskTable, 0, 1, 4, 1);

		// Column 0 - for outstanding tasks (50% width)
		ColumnConstraints cc0 = new ColumnConstraints();
		cc0.setPercentWidth(50);

		// Column 1 - for overdue tasks (50% width)
		ColumnConstraints cc1 = new ColumnConstraints();
		cc1.setPercentWidth(50);

		// Change the column sizes
		getColumnConstraints().addAll(cc0, cc1);

		// Row 0 - The four statistic panels (fixed width)
		RowConstraints rw0 = new RowConstraints();
		rw0.setMinHeight(STATISTICS_HEIGHT);
		rw0.setMaxHeight(STATISTICS_HEIGHT);

		// Row 1 - tasks overview table (fill remaining height)
		RowConstraints rw1 = new RowConstraints();
		rw1.setVgrow(Priority.ALWAYS);

		// Change the row sizes
		getRowConstraints().addAll(rw0, rw1);
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
		 * @param tasks     The tasks list to bind to
		 */
		private StatPane(String caption, Callable<Integer> statFunc,
		                 Callable<Paint> colorFunc, ObservableList<Task> tasks) {
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
			lblStat.textProperty().bind(Bindings.format("%d", Bindings.createIntegerBinding(statFunc, tasks)));
			// Bind the stat color callable function with tasks as dependency
			lblStat.textFillProperty().bind(Bindings.createObjectBinding(colorFunc, tasks));

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