package uk.ac.aber.cs221.group15.service;

import javafx.embed.swing.JFXPanel;
import org.junit.Before;
import org.junit.Test;
import uk.ac.aber.cs221.group15.TaskSync;
import uk.ac.aber.cs221.group15.task.Step;
import uk.ac.aber.cs221.group15.task.Task;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Simon Scott
 * @author Darren White
 * @version 0.0.2
 */
public class TaskServiceTest {

	/**
	 * Used for login protocols
	 */
	private LoginService loginService;

	/**
	 * Used for updating tasks protocols
	 */
	private TaskService taskService;

	/**
	 * Used for selecting a task to update
	 */
	private int taskIndex = 1;

	/**
	 * Used for selecting a step to update
	 */
	private int stepIndex = 0;

	/**
	 * The task step comment to set
	 */
	private String updateComment = "JUnit test comment...";

	/**
	 * The task status to set
	 */
	private int updateStatus = Task.ALLOCATED;

	@Before
	public void setUp() throws Exception {
		// Create a new LoginService for login
		loginService = new LoginService();
		// Create a new TaskService for updating tasks
		taskService = new TaskService();
	}

	@Test
	public void updateTaskStatusTest() throws Exception {
		// Login with correct details to get token
		String token = loginService.login("sis22@aber.ac.uk", "scott");

		// Create task sync to load tasks
		TaskSync sync = new TaskSync(token);

		// Initialize JavaFX
		new JFXPanel();

		// Update tasks
		sync.forceSync();

		// Wait for tasks to load
		while (sync.getTasks().size() == 0) {
			Thread.sleep(50);
		}

		// Get all tasks
		List<Task> tasks = sync.getTasks();
		// Get the task to update
		Task t = tasks.get(taskIndex);

		System.out.println("Changing task status...");
		System.out.println("Task id: " + t.getId());
		System.out.println("Task status: " + t.getStatus());
		System.out.println();

		// Set the new status
		t.setStatus(updateStatus);
		// Set the date completed to now
		t.setDateCompleted(Calendar.getInstance());

		// Submit update for task status
		taskService.updateTaskStatus(token, t);

		// Wait for request to send
		Thread.sleep(500);

		// Clear the tasks
		sync.getTasks().clear();
		// Update the tasks
		sync.forceSync();

		// Wait for tasks to update
		while (sync.getTasks().size() == 0) {
			Thread.sleep(50);
		}

		// Get the tasks
		tasks = sync.getTasks();
		// Get the task that was updated
		t = tasks.get(taskIndex);

		System.out.println("Task status update check...");
		System.out.println("Task status: " + t.getStatus());
		System.out.println();

		// Ensure status has been updated
		assertEquals(updateStatus, t.getStatus());
	}

	@Test
	public void updateTaskStepCommentTest() throws Exception {
		// Login with correct details to get token
		String token = loginService.login("sis22@aber.ac.uk", "scott");
		// Create task sync to load tasks
		TaskSync sync = new TaskSync(token);

		// Initialize JavaFX
		new JFXPanel();

		// Update tasks
		sync.forceSync();

		// Wait for tasks to update
		while (sync.getTasks().size() == 0) {
			Thread.sleep(50);
		}

		// Get all the tasks
		List<Task> tasks = sync.getTasks();
		// Get the task to update
		Task t = tasks.get(taskIndex);
		// Iterator for the steps
		Iterator<Step> it = t.getSteps().iterator();

		// Iterate to the step index
		for (int i = 0; i < stepIndex && it.hasNext(); i++) {
			it.next();
		}

		// If there is not a next step then index is too large
		if (!it.hasNext()) {
			System.err.println("Step index out of range: " + stepIndex);
			return;
		}

		// Get the step to update
		Step s = it.next();

		System.out.println("Updating step comment...");
		System.out.println("Task id: " + t.getId());
		System.out.println("Step id: " + s.getId());
		System.out.println("Step comment: " + s.getComment());
		System.out.println();

		// Update the step comment
		taskService.updateTaskStepComment(token, s.getId(), updateComment);

		// Wait for request to send
		Thread.sleep(500);

		// Clear the tasks
		sync.getTasks().clear();
		// Update the tasks
		sync.forceSync();

		// Wait for tasks to update
		while (sync.getTasks().size() == 0) {
			Thread.sleep(50);
		}

		// Get the tasks
		tasks = sync.getTasks();
		// Get the task that was updated
		t = tasks.get(taskIndex);
		// Iterator for the steps
		it = t.getSteps().iterator();

		// Iterate to the step index
		for (int i = 0; i < stepIndex && it.hasNext(); i++) {
			it.next();
		}

		// Get the step that was updated
		s = it.next();

		System.out.println("Task step comment update check...");
		System.out.println("Step comment: " + s.getComment());
		System.out.println();

		// Ensure comment has been updated
		assertEquals(updateComment, s.getComment());
	}
}