package uk.ac.aber.cs221.group15.gui;

import javafx.beans.binding.Bindings;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
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
import java.util.Calendar;
import java.util.Set;

/**
 * This class will display a new window which
 * will show a specific tasks details and allow
 * the ability to edit task step comments and
 * set tasks as completed (or uncompleted)
 *
 * @author Darren White
 * @version 0.1.2
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
	private final Calendar initialCompletedDate;

	/**
	 * Used to check if any comments have been edited
	 * or the status has changed
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
		Label lblDateDue = new Label("Due by " + Task.DATE_FORMAT.format(task.getDateDue().getTime()));
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

		Label lblSteps = new Label("Steps");
		lblSteps.setId("lbl-task-steps");
		grid.add(lblSteps, 0, currentRow++);

		// Store the steps in this set
		Set<Step> steps = task.getSteps();

		// Add all the steps to the grid
		for (Step s : steps) {
			// Add the description label
			Label lblStepDesc = new Label(s.getTitle());
			lblStepDesc.setId("lbl-step-desc");
			lblStepDesc.setMaxWidth(300);
			lblStepDesc.setWrapText(true);
			grid.add(lblStepDesc, 0, currentRow);

			// Add the text field comment so users
			// can edit comments
			TextArea txtStepComment = new TextArea(s.getComment());
			txtStepComment.setId("txt-step-comment");
			txtStepComment.setPrefColumnCount(20);
			txtStepComment.setPrefRowCount(2);
			txtStepComment.setWrapText(true);
			txtStepComment.setOnKeyReleased(e -> {
				// Task step comment changed
				edited = true;
				s.setComment(txtStepComment.getText());
			});
			// Increment the row at the end
			grid.add(txtStepComment, 1, currentRow++);
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
			} else if (task.getStatus() == Task.ALLOCATED) {
				// Set the status as completed as all steps have comments
				task.setStatus(Task.COMPLETED);
				// Set the date completed as now
				task.setDateCompleted(Calendar.getInstance());
			}

			// Task status changed
			edited = true;
		});
		grid.add(btnComplete, 0, currentRow);

		// Add the button to save & close the details (if comments have been edited)
		Button btnSave = new Button("Save");
		btnSave.setId("btn-save");
		// Save the task on press and close the window
		btnSave.setOnAction(e -> {
			try {
				// Save the task & steps
				save(token, steps);
				// Close this window
				close();
			} catch (IOException | ParseException ex) {
				lblErr.setText("Unable to save changes: " + ex.getLocalizedMessage());
				ex.printStackTrace();
			}
		});
		grid.add(btnSave, 1, currentRow++);

		// Col 0 - Labels and Completed button
		ColumnConstraints cc0 = new ColumnConstraints();
		cc0.setHalignment(HPos.LEFT);
		cc0.setHgrow(Priority.NEVER);

		// Col 1 - TextFields and Save button
		ColumnConstraints cc1 = new ColumnConstraints();
		cc1.setHalignment(HPos.RIGHT);
		cc1.setHgrow(Priority.NEVER);

		// Change the row sizes
		grid.getColumnConstraints().addAll(cc0, cc1);

		// Make each row equidistant
		for (int i = 0; i < currentRow; i++) {
			RowConstraints rc = new RowConstraints();
			rc.setValignment(VPos.TOP);
			rc.setVgrow(Priority.NEVER);
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

		// TODO If we are offline save changes locally

		// Update the task status if it has changed
		if (task.getStatus() != initialStatus) {
			service.updateTaskStatus(token, task);
		}

		// Update all the task steps
		for (Step s : steps) {
			service.updateTaskStepComment(token, s);
		}
	}
}