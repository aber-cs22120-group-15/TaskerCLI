package uk.ac.aber.cs221.group15.gui;

import javafx.beans.binding.Bindings;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.json.simple.parser.ParseException;
import uk.ac.aber.cs221.group15.TaskerCLI;
import uk.ac.aber.cs221.group15.service.TaskService;
import uk.ac.aber.cs221.group15.task.Step;
import uk.ac.aber.cs221.group15.task.Task;

import java.io.IOException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;

/**
 * This class will display a new window which
 * will show a specific tasks details and allow
 * the ability to edit task step comments and
 * set tasks as completed (or uncompleted)
 *
 * @author Darren White
 * @version 0.1.0
 */
public class TaskDetail extends Stage {

	/**
	 * The title of the window
	 */
	private static final String APP_NAME = "Task Details";

	/**
	 * The service used to submit requests to get task steps
	 */
	private static final TaskService service = new TaskService();

	/**
	 * The initial status of the task
	 */
	private final int initialStatus;

	/**
	 * The initial date completed of the task
	 */
	private final Date initialCompletedDate;

	/**
	 * Used to check if any comments have been edited
	 */
	private boolean edited = false;

	/**
	 * The task being viewed & edited
	 */
	private Task task;

	/**
	 * Creates a new task detail window
	 *
	 * @param owner The main application
	 * @param task  The task to display details for
	 */
	public TaskDetail(Window owner, String token, Task task) {
		this.task = task;
		initialStatus = task.getStatus();
		initialCompletedDate = task.getDateCompleted();

		// Use window modality so the main window cannot be used while
		// this window is open
		initModality(Modality.WINDOW_MODAL);
		// Set the owner as the main window
		initOwner(owner);
		// No need for the task details to be resizable
		setResizable(false);
		// Set the title of the window
		setTitle(APP_NAME);

		setOnCloseRequest(e -> {
			if (edited) {
				task.setStatus(initialStatus);
				task.setDateCompleted(initialCompletedDate);
			}
		});

		// Initialize the components
		init(token);
	}

	public Task getTask() {
		return task;
	}

	/**
	 * Initializes this windows components
	 */
	private void init(String token) {
		// Create the grid that we are going to
		// put all the component onto
		GridPane grid = new GridPane();
		// Create the scene
		Scene scene = new Scene(grid);
		// Use this so we know which row we are currently adding to
		int currentRow = 0;
		// A label to display any errors
		Label lblErr = new Label();

		// Set padding & gaps to 10px
		grid.setPadding(new Insets(10));
		grid.setHgap(10);
		grid.setVgap(10);

		// Create, center it, and add the title label
		Label lblTitle = new Label(task.getTitle());
		lblTitle.setId("lbl-task-title");
		GridPane.setHalignment(lblTitle, HPos.CENTER);
		grid.add(lblTitle, 0, currentRow++, 3, 1);

		// Create and add the due date label
		Label lblDateDue = new Label("Due by " + task.getDateDue().toString());
		lblDateDue.setId("lbl-task-due");
		grid.add(lblDateDue, 0, currentRow++);

		// Create and add the created by label
		Label lblCreator = new Label("Created by " + task.getCreator());
		lblCreator.setId("lbl-task-creator");
		grid.add(lblCreator, 0, currentRow++);

		// Create and add the status label
		Label lblStatus = new Label();
		lblStatus.textProperty().bind(Bindings.createStringBinding(() ->
						"Current status: " + task.getStatusString(),
				task.statusProperty()));
		lblStatus.setId("lbl-task-status");
		grid.add(lblStatus, 0, currentRow++);

		// Store the steps in this set
		Set<Step> steps = task.getSteps();

		// Add all the steps to the grid
		for (Step s : steps) {
			// Add the description label
			Label lblStepDesc = new Label(s.getTitle());
			lblStepDesc.setId("lbl-step-desc");
			grid.add(lblStepDesc, 0, currentRow);

			// Add the text field comment so users
			// can edit comments
			TextField txtStepComment = new TextField(s.getComment());
			txtStepComment.setId("txt-step-comment");
			txtStepComment.setOnKeyReleased(e -> {
				// Task step comment changed
				edited = true;
				s.setComment(txtStepComment.getText());
			});
			// Increment the row at the end
			grid.add(txtStepComment, 1, currentRow++, 2, 1);
		}

		// Add the error label here
		lblErr.setId("lbl-err");
		grid.add(lblErr, 0, currentRow++);

		// Add the button to complete the task
		Button btnComplete = new Button();
		btnComplete.textProperty().bind(Bindings.createStringBinding(() ->
						task.getStatus() == Task.COMPLETED ? "Not Completed" : "Completed",
				task.statusProperty()));
		btnComplete.setId("btn-completed");
		btnComplete.setMaxWidth(Double.MAX_VALUE);
		btnComplete.setAlignment(Pos.CENTER_RIGHT);
		// Set the task as completed on press
		btnComplete.setOnAction(e -> {
			// If the task is completed set it as allocated
			// Check the task isn't already completed otherwise
			// try and set the task as completed
			if (task.getStatus() == Task.COMPLETED) {
				// Change status to allocated
				task.setStatus(Task.ALLOCATED);
				// Change the date completed as nothing
				task.setDateCompleted(null);
			} else if (!setTaskCompleted(steps)) {
				// Show error text
				lblErr.setText("Must provide descriptions for each step!");
			}

			// Task status changed
			edited = true;
		});
		grid.add(btnComplete, 1, currentRow);

		// Add the button to save & close the details (if comments have been edited)
		Button btnSave = new Button("Save");
		btnSave.setId("btn-save");
		btnSave.setAlignment(Pos.CENTER_RIGHT);
		// Save the task on press and close the window
		btnSave.setOnAction(e -> {
			try {
				// Save the task & steps
				save(token, steps);
				// Close this window
				close();
			} catch (IOException | ParseException ex) {
				lblErr.setText("Unable to save changes!");
				ex.printStackTrace();
			}
		});
		grid.add(btnSave, 2, currentRow++);

		// Col 0 - Labels
		ColumnConstraints cc0 = new ColumnConstraints();
		cc0.setHgrow(Priority.NEVER);

		// Col 1 - TextFields and Completed button
		ColumnConstraints cc1 = new ColumnConstraints();
		cc1.setHgrow(Priority.ALWAYS);

		// Col 2 - TextFields and Save button
		ColumnConstraints cc2 = new ColumnConstraints();
		cc2.setHgrow(Priority.NEVER);

		// Change the row sizes
		grid.getColumnConstraints().addAll(cc0, cc1, cc2);

		// Make each row equidistant
		for (int i = 0; i < currentRow; i++) {
			RowConstraints rc = new RowConstraints();
			rc.setPercentHeight(100 / currentRow);
			grid.getRowConstraints().add(rc);
		}

		// Set the stylesheet for css styling
		scene.getStylesheets().add(TaskerCLI.getResource("resources/css/TaskDetail.css").toExternalForm());
		// Sets the scene
		setScene(scene);
	}

	private void save(String token, Set<Step> steps) throws IOException, ParseException {
		// Check if changes were made
		if (!edited) {
			return;
		}

		// Update the task status
		service.updateTaskStatus(token, task);

		// Update all the task steps
		for (Step s : steps) {
			service.updateTaskStepComment(token, s);
		}
	}

	/**
	 * Sets the task as completed using setStatus(int)
	 *
	 * @return false if one or more steps do not have comments
	 * otherwise true
	 */
	private boolean setTaskCompleted(Set<Step> steps) {
		// Can only complete a task if the status is allocated
		if (task.getStatus() != Task.ALLOCATED) {
			return false;
		}

		// Check all steps have comments, if not
		// return false
		for (Step s : steps) {
			String comment = s.getComment();

			if (comment == null || comment.trim().isEmpty()) {
				// We found a step without a comment
				return false;
			}
		}

		// Set the status as completed as all steps have comments
		task.setStatus(Task.COMPLETED);
		// Set the date completed as now
		task.setDateCompleted(new GregorianCalendar().getTime());

		// Return true as we successfully checked the steps and set the status
		return true;
	}
}