package uk.ac.aber.cs221.group15.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import org.json.simple.parser.ParseException;
import uk.ac.aber.cs221.group15.service.TaskService;
import uk.ac.aber.cs221.group15.task.Task;

import java.io.IOException;
import java.util.Date;

/**
 * Used to display all tasks for the user in a table
 * and each task can be viewed in detail and can be
 * set as completed (or complete each step)
 *
 * @author Darren White
 * @version 0.0.1
 */
public class TaskView extends GridPane {

	/**
	 * The service used to submit requests to list all tasks
	 */
	private static final TaskService service = new TaskService();

	/**
	 * Creates a new task view
	 *
	 * @param token The token for the current user
	 */
	public TaskView(String token) {
		init(token);
	}

	/**
	 * Creates a table to display tasks
	 *
	 * @return A new table to display tasks
	 */
	private TableView<Task> createTaskTable(ObservableList<Task> tasks) {
		// Create the task table for the tasks
		TableView<Task> table = new TableView<>(tasks);

		// Create seven columns: id, task, created date, due date,
		// completed date, member and status
		TableColumn<Task, Integer> idCol = new TableColumn<>("ID");
		TableColumn<Task, String> titleCol = new TableColumn<>("Task");
		TableColumn<Task, Date> createdDateCol = new TableColumn<>("Date created");
		TableColumn<Task, Date> dueDateCol = new TableColumn<>("Due date");
		TableColumn<Task, Date> completedDateCol = new TableColumn<>("Date completed");
		TableColumn<Task, String> creatorCol = new TableColumn<>("Member");
		TableColumn<Task, String> statusCol = new TableColumn<>("Status");

		// Set each of the cell value factories (which fields they
		// correspond to in the Task class)
		idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
		titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
		createdDateCol.setCellValueFactory(new PropertyValueFactory<>("dateCreated"));
		dueDateCol.setCellValueFactory(new PropertyValueFactory<>("dateDue"));
		completedDateCol.setCellValueFactory(new PropertyValueFactory<>("dateCompleted"));
		creatorCol.setCellValueFactory(new PropertyValueFactory<>("creator"));
		statusCol.setCellValueFactory(t -> {
			int status = t.getValue().getStatus();
			SimpleStringProperty val = new SimpleStringProperty();

			switch (status) {
				case Task.ABANDONED:
					val.set("Abandoned");
					break;
				case Task.ALLOCATED:
					val.set("Allocated");
					break;
				case Task.COMPLETED:
					val.set("Completed");
					break;
				default:
					throw new IllegalStateException("Unknown status: " + status);
			}

			return val;
		});

		// Set the column dyanmic sizes
		// ID col - 5% width (- 2 so that the horizontal scrollbar doesn't show)
		// Title col - 25% width
		// Created date col - 15% width
		// Due date col - 15% width
		// Completed date col - 15% width
		// Creator name col - 15% width
		// Status col - 10% width
		idCol.prefWidthProperty().bind(table.widthProperty().multiply(0.05).subtract(2));
		titleCol.prefWidthProperty().bind(table.widthProperty().multiply(0.25));
		createdDateCol.prefWidthProperty().bind(table.widthProperty().multiply(0.15));
		dueDateCol.prefWidthProperty().bind(table.widthProperty().multiply(0.15));
		completedDateCol.prefWidthProperty().bind(table.widthProperty().multiply(0.15));
		creatorCol.prefWidthProperty().bind(table.widthProperty().multiply(0.15));
		statusCol.prefWidthProperty().bind(table.widthProperty().multiply(0.1));

		// Add the seven columns to the table
		table.getColumns().add(idCol);
		table.getColumns().add(titleCol);
		table.getColumns().add(createdDateCol);
		table.getColumns().add(dueDateCol);
		table.getColumns().add(completedDateCol);
		table.getColumns().add(creatorCol);
		table.getColumns().add(statusCol);

		// Return the new table created
		return table;
	}

	/**
	 * Initializes this view and its components
	 */
	private void init(String token) {
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

		// Create the task table
		TableView<Task> taskTable = createTaskTable(tasks);

		add(taskTable, 0, 0);

		// Column 0 - fill everything
		ColumnConstraints cc0 = new ColumnConstraints();
		cc0.setHgrow(Priority.ALWAYS);

		// Change the column size
		getColumnConstraints().add(cc0);

		// Row 0 - fill everything
		RowConstraints rw0 = new RowConstraints();
		rw0.setVgrow(Priority.ALWAYS);

		// Change the row size
		getRowConstraints().add(rw0);
	}
}