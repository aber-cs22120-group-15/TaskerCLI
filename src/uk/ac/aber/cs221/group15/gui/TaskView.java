package uk.ac.aber.cs221.group15.gui;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.TextAlignment;
import uk.ac.aber.cs221.group15.service.TaskService;
import uk.ac.aber.cs221.group15.task.Task;

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
	 * The list to store all the user tasks
	 */
	private final ObservableList<Task> tasks;

	/**
	 * Creates a new task view
	 *
	 * @param token The token for the current user
	 * @param tasks The tasks for the user
	 */
	public TaskView(String token, ObservableList<Task> tasks) {
		this.tasks = tasks;
		init(token);
	}

	/**
	 * Creates a table to display tasks
	 *
	 * @param token The token for the current user
	 * @return A new table to display tasks
	 */
	private TableView<Task> createTaskTable(String token) {
		// Create the task table for the tasks
		TableView<Task> table = new TableView<>(tasks);

		// Use this for listening to mouse events on each row
		table.setRowFactory(tv -> {
			// Create the row
			TableRow<Task> tr = new TableRow<>();
			// Add mouse event
			tr.setOnMouseClicked(event -> {
				// On double click on a row with a task
				if (event.getClickCount() == 2 && !tr.isEmpty()) {
					// Show task details window
					TaskDetail taskDetail = new TaskDetail(getScene().getWindow(), token, tr.getItem());
					taskDetail.sizeToScene();
					taskDetail.showAndWait();
				}
			});
			// Return the row
			return tr;
		});

		// Create six columns: task, created date, due date,
		// completed date, member and status
		TableColumn<Task, String> titleCol = new TableColumn<>("Task");
		TableColumn<Task, Date> createdDateCol = new TableColumn<>("Date created");
		TableColumn<Task, Date> dueDateCol = new TableColumn<>("Due date");
		TableColumn<Task, Date> completedDateCol = new TableColumn<>("Date completed");
		TableColumn<Task, String> creatorCol = new TableColumn<>("Assigned by");
		TableColumn<Task, String> statusCol = new TableColumn<>("Status");

		// Set each of the cell value factories (which fields they
		// correspond to in the Task class)
		titleCol.setCellValueFactory(t -> t.getValue().titleProperty());
		createdDateCol.setCellValueFactory(t -> t.getValue().dateCreatedProperty());
		dueDateCol.setCellValueFactory(t -> t.getValue().dateDueProperty());
		completedDateCol.setCellValueFactory(t -> t.getValue().dateCompletedProperty());
		creatorCol.setCellValueFactory(t -> t.getValue().creatorProperty());
		// Convert the status integer to readable string
		statusCol.setCellValueFactory(t -> Bindings.createStringBinding(() -> {
			// Get the correct status string
			switch (t.getValue().getStatus()) {
				case Task.ABANDONED:
					return "Abandoned";
				case Task.ALLOCATED:
					return "Allocated";
				case Task.COMPLETED:
					return "Completed";
				default:
					return null;
			}
		}, t.getValue().statusProperty()));

		// Set the column dyanmic sizes
		// Title col - 25% width (subtract 2 so that the horizontal scrollbar doesn't show)
		// Created date col - 15% width
		// Due date col - 15% width
		// Completed date col - 15% width
		// Creator name col - 15% width
		// Status col - 15% width
		titleCol.prefWidthProperty().bind(table.widthProperty().multiply(0.25));
		createdDateCol.prefWidthProperty().bind(table.widthProperty().multiply(0.15));
		dueDateCol.prefWidthProperty().bind(table.widthProperty().multiply(0.15));
		completedDateCol.prefWidthProperty().bind(table.widthProperty().multiply(0.15));
		creatorCol.prefWidthProperty().bind(table.widthProperty().multiply(0.15));
		statusCol.prefWidthProperty().bind(table.widthProperty().multiply(0.15));

		// Add the six columns to the table
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
	 *
	 * @param token The token for the current user
	 */
	private void init(String token) {
		// Set padding & gaps to 10px
		setPadding(new Insets(10));
		setHgap(10);
		setVgap(10);

		// The caption for the table
		Label lblTableCaption = new Label("All Tasks\nDouble-click a task for more detail");
		// Set the id for css
		lblTableCaption.setId("lbl-all-tasks");
		// Center the text
		lblTableCaption.setTextAlignment(TextAlignment.CENTER);
		// Center it
		GridPane.setHalignment(lblTableCaption, HPos.CENTER);
		// Add it above the table
		add(lblTableCaption, 0, 0);

		// Create the task table and add it
		add(createTaskTable(token), 0, 1);

		// Column 0 - fill everything
		ColumnConstraints cc0 = new ColumnConstraints();
		cc0.setHgrow(Priority.ALWAYS);

		// Change the column sizes
		getColumnConstraints().add(cc0);

		// Row 0 - table caption (fit to label size)
		RowConstraints rw0 = new RowConstraints();
		rw0.setVgrow(Priority.NEVER);

		// Row 0 - tasks table (fill remaining height)
		RowConstraints rw1 = new RowConstraints();
		rw1.setVgrow(Priority.ALWAYS);

		// Change the row sizes
		getRowConstraints().addAll(rw0, rw1);
	}
}