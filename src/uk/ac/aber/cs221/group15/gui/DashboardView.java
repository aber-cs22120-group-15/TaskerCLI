package uk.ac.aber.cs221.group15.gui;

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
import javafx.scene.text.Font;
import org.json.simple.parser.ParseException;
import uk.ac.aber.cs221.group15.service.TaskService;
import uk.ac.aber.cs221.group15.task.Task;

import java.io.IOException;
import java.util.Date;

/**
 * This class is used to represent the Dashboard in the Overview
 * of the client which will display statistics and an overview of
 * upcoming tasks and a few major details
 *
 * @author Darren White
 * @version 0.0.3
 */
public class DashboardView extends GridPane {

	/**
	 * Defines the height for the four statistics panels
	 */
	private static final int STATISTICS_HEIGHT = 200;

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

	/**
	 * Creates a table to display tasks
	 *
	 * @return A new table to display tasks
	 */
	private TableView<Task> createTaskTable() {
		// Create the task table for an overview of tasks
		TableView<Task> table = new TableView<>();

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
		// ID col - 10% width (- 2 so that the horizontal scrollbar doesn't show)
		// Title col - 35% width
		// Due date col - 30% width
		// Creator name col - 25% width
		idCol.prefWidthProperty().bind(table.widthProperty().multiply(0.1).subtract(2));
		titleCol.prefWidthProperty().bind(table.widthProperty().multiply(0.35));
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
		// Set padding & gaps to 10px
		setPadding(new Insets(10));
		setHgap(10);
		setVgap(10);

		// Panes for the statistics
		StatPane paneOutstanding = new StatPane("Outstanding Tasks");
		StatPane paneOverdue = new StatPane("Overdue Tasks");
		StatPane paneDist = new StatPane("Task Distribution");
		StatPane paneComp = new StatPane("Completed On Time");

		// Add the four panes to the first row, one in each column
		add(paneOutstanding, 0, 0);
		add(paneOverdue, 1, 0);
		add(paneDist, 2, 0);
		add(paneComp, 3, 0);

		// Create the task table
		TableView<Task> taskTable = createTaskTable();

		// The list of tasks in the overview
		ObservableList<Task> tasks = null;

		try {
			// Try and load the tasks from the database
			tasks = FXCollections.observableArrayList(service.getTasks(token));
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}

		// Set the table items as the list we just created
		taskTable.setItems(tasks);

		// Add the table to the second row (row 1), in the first column (col 0)
		// and allow it to span all four columns and 1 row
		add(taskTable, 0, 1, 4, 1);

		// Column 0 - for outstanding tasks (25% width)
		ColumnConstraints cc0 = new ColumnConstraints();
		cc0.setPercentWidth(25);

		// Column 1 - for overdue tasks (25% width)
		ColumnConstraints cc1 = new ColumnConstraints();
		cc1.setPercentWidth(25);

		// Column 2 - for task distribution (25% width)
		ColumnConstraints cc2 = new ColumnConstraints();
		cc2.setPercentWidth(25);

		// Column 3 - for completed on time (25% width)
		ColumnConstraints cc3 = new ColumnConstraints();
		cc3.setPercentWidth(25);

		// Change the column sizes
		getColumnConstraints().addAll(cc0, cc1, cc2, cc3);

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
		 * The label for the statistic
		 */
		private final Label lbl;

		/**
		 * Creates a new StatPane with the text label
		 *
		 * @param text The string for the label
		 */
		private StatPane(String text) {
			// Create a label with the text
			lbl = new Label(text);
			lbl.setFont(new Font(16));

			// Position the label at the bottom center
			setHalignment(lbl, HPos.CENTER);
			setValignment(lbl, VPos.BOTTOM);

			// Add the label in the second row
			add(lbl, 0, 1);

			// TODO Replace pane with statistics information
			// Temporary placeholder pane
			StackPane pane = new StackPane();
			setHgrow(pane, Priority.ALWAYS);
			setVgrow(pane, Priority.ALWAYS);
			add(pane, 0, 0);
		}
	}
}