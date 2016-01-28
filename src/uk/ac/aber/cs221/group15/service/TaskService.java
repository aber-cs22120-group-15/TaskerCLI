package uk.ac.aber.cs221.group15.service;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import uk.ac.aber.cs221.group15.TaskerCLI;
import uk.ac.aber.cs221.group15.task.Step;
import uk.ac.aber.cs221.group15.task.Task;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Calendar;

/**
 * This service provides functionality to submit a request
 * to get all tasks for a user using the unique token
 *
 * @author Darren White
 * @version 0.1.5
 */
public class TaskService extends Service {

	/**
	 * The url to list all tasks using the user token
	 */
	private static final String URL_LIST_TASKS = URL_API +
			"?method=list_tasks&token=%s";

	/**
	 * The url to list all steps using the user token and
	 * step ids (separated using commas)
	 */
	private static final String URL_LIST_STEPS = URL_API +
			"?method=get_steps&token=%s&id=%s";

	/**
	 * The url to update a task status using the user token,
	 * the task id and the new status
	 */
	private static final String URL_SET_STATUS = URL_API +
			"?method=change_status&token=%s&id=%d&status=%d&completed_time=%d";

	/**
	 * The url to set a task step comment using the user token,
	 * step id and the new comment
	 */
	private static final String URL_SET_COMMENT = URL_API +
			"?method=set_task_step_comment&token=%s&id=%d";

	/**
	 * The arguments to post to the url
	 */
	private static final String URL_SET_COMMENT_POST = "comment=%s";

	/**
	 * The key attribute to get the task list value
	 */
	private static final String KEY_TASK_ARRAY = "tasks";

	/**
	 * The key attribute to get the task list value
	 */
	private static final String KEY_STEP_ARRAY = "steps";

	/**
	 * The key attribute to get the task/step id
	 */
	private static final String KEY_TASK_ID = "id";

	/**
	 * The key attribute to get the task title
	 */
	private static final String KEY_TASK_TITLE = "title";

	/**
	 * The key attribute to get the name of the
	 * member who created the task
	 */
	private static final String KEY_TASK_CREATOR = "created_name";

	/**
	 * The key attribute to get the date the task
	 * was created
	 */
	private static final String KEY_TASK_DATE_CREATED = "created_time";

	/**
	 * The key attribute to get the date the task
	 * is due by
	 */
	private static final String KEY_TASK_DATE_DUE = "due_by";

	/**
	 * The key attribute to get the date the task
	 * was completed
	 */
	private static final String KEY_TASK_DATE_COMPLETED = "completed_time";

	/**
	 * The key attribute to get the task status
	 */
	private static final String KEY_TASK_STATUS = "status";

	/**
	 * The key attribute to get the step id
	 */
	private static final String KEY_STEP_ID = "id";

	/**
	 * The key attribute to get the step title
	 */
	private static final String KEY_STEP_TITLE = "title";

	/**
	 * The key attribute to get the step comment
	 */
	private static final String KEY_STEP_COMMENT = "comment";

	/**
	 * The path to store local sync updates
	 */
	private static final String PATH_SYNC = TaskerCLI.getUserHomeDir() +
			File.separator + ".tasker_sync";

	/**
	 * Adds the steps to each task in the specified list using the
	 * user login token
	 *
	 * @param tasks The list of tasks to add steps to
	 * @param token The token for the current user
	 */
	private void addTaskSteps(ObservableList<Task> tasks, String token) throws IOException, ParseException {
		// All the task ids concataned and separated with commas
		StringBuilder ids = new StringBuilder();

		// Append the task ids to the string
		tasks.forEach(t -> ids.append(t.getId()).append(','));

		// Create the url to submit with the method, token and task ids
		String url = String.format(URL_LIST_STEPS, token, ids.toString());
		// Submit the request along with the token
		int status = submit(url);

		// If success then add the steps otherwise there are no steps
		if (status == STATUS_SUCCESS) {
			// Get the response object
			JSONObject response = (JSONObject) getResponse();
			// Get all the steps from the response object
			JSONObject stepList = (JSONObject) response.get(KEY_STEP_ARRAY);

			// Iterate all tasks to add its steps
			for (Task task : tasks) {
				// Get the array of steps with the task id
				// JSON uses string for keys so wrap the id in a string
				JSONArray stepsArray = (JSONArray) stepList.get(String.valueOf(task.getId()));

				// Iterate all the steps
				for (Object step : stepsArray) {
					// Each step is a JSONObject
					// Parse the step and add it to the task
					task.addStep(parseStep((JSONObject) step));
				}
			}
		}
	}

	/**
	 * Gets the tasks from the database for the user
	 * using the token
	 *
	 * @param token The token for the current user
	 * @throws IOException    If an I/O exception occurs
	 * @throws ParseException If a Parse exception occurs
	 */
	public void getTasks(ObservableList<Task> tasks, String token) throws IOException, ParseException {
		// Create the url to submit with the method, and token
		String url = String.format(URL_LIST_TASKS, token);
		// Submit the request along with the token
		int status = submit(url);

		// An error occurred, handle it
		if (status == STATUS_ERROR) {
			// This should never happen as the token
			// is retrieved at login
			throw new IllegalStateException(getErrorMessage());
		} else if (status == STATUS_SUCCESS) {
			// Get the response object
			JSONObject response = (JSONObject) getResponse();
			// Get the list of tasks as an array
			JSONArray taskList = (JSONArray) response.get(KEY_TASK_ARRAY);

			// Iterate over the tasks
			for (Object o : taskList) {
				// Each object is a JSON object
				JSONObject obj = (JSONObject) o;

				// Parse the task
				Task task = parseTask(obj);

				// Add it to the list
				// Run on the JavaFX thread to update the ui
				Platform.runLater(() -> tasks.add(task));
			}

			// Ensure all tasks have been added before getting
			// the steps - this is due to adding the tasks to the
			// list while on the JavaFX thread
			while (tasks.size() != taskList.size()) {
				try {
					Thread.sleep(50);
				} catch (InterruptedException ignored) {
				}
			}

			// Add the task steps for all tasks
			addTaskSteps(tasks, token);
		}
	}

	/**
	 * Try and parse the dates from the String
	 *
	 * @param s The date in string format
	 * @return The date parsed from the string
	 * @link format
	 */
	private Calendar parseDate(String s) {
		// Create a new Calendar to store the date in
		Calendar c = Calendar.getInstance();

		try {
			// Try and parse the date from the string
			c.setTime(Task.DATE_FORMAT.parse(s));
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}

		return c;
	}

	/**
	 * Parse the step from the JSONObject
	 *
	 * @param obj The object to parse the step from
	 * @return The new step from the object
	 */
	private Step parseStep(JSONObject obj) {
		// Get step id (cannot cast to int as json works with strings)
		int id = Integer.parseInt((String) obj.get(KEY_STEP_ID));
		// Get the title of the step
		String title = (String) obj.get(KEY_STEP_TITLE);
		// Get the comment of the step
		String comment = (String) obj.get(KEY_STEP_COMMENT);

		// Create the step and return it
		return new Step(id, title, comment);
	}

	/**
	 * Parse the task values from the JSONObject
	 *
	 * @param obj The object to parse values from
	 * @return The new task with values from the object
	 */
	private Task parseTask(JSONObject obj) {
		// Get task id (cannot cast to int as json works with strings)
		int id = Integer.parseInt((String) obj.get(KEY_TASK_ID));
		// Get the title of the task
		String title = (String) obj.get(KEY_TASK_TITLE);
		// Get the member name who created the task
		String creator = (String) obj.get(KEY_TASK_CREATOR);
		// Parse dates
		Calendar dateCreated = parseDate((String) obj.get(KEY_TASK_DATE_CREATED));
		Calendar dateDue = parseDate((String) obj.get(KEY_TASK_DATE_DUE));
		Calendar dateCompleted = null;
		// Get the task status
		int status = Integer.parseInt((String) obj.get(KEY_TASK_STATUS));

		// Task date can only be set if the task has been completed
		if (status == Task.COMPLETED) {
			dateCompleted = parseDate((String) obj.get(KEY_TASK_DATE_COMPLETED));
		}

		// Create the task and return it
		return new Task(id, title, creator, dateCreated, dateDue,
				dateCompleted, status);
	}

	/**
	 * Updates a task status using the user token and the task
	 *
	 * @param token The token for the current user
	 * @param task  The task to update
	 */
	public void updateTaskStatus(String token, Task task) throws IOException, ParseException {
		long seconds = 0;

		if (task.getDateCompleted() != null) {
			seconds = task.getDateCompleted().getTimeInMillis() / 1000;
		}

		updateTaskStatus(token, task.getId(), task.getStatus(), seconds);
	}

	/**
	 * Updates a task status using the user token and the task id
	 *
	 * @param token   The token for the current user
	 * @param id      The unique id for the task
	 * @param status  The status to set for the task
	 * @param seconds The timestamp in seconds when the task was completed
	 */
	public void updateTaskStatus(String token, int id, int status, long seconds) throws IOException, ParseException {
		// Create the url to submit with the method, token, id, status and seconds
		String url = String.format(URL_SET_STATUS, token, id, status, seconds);

		// If we're offline then save updates to file
		if (!Service.checkConnection()) {
			// Create new file if it doens't exit
			// Append changes to the file
			try (PrintWriter pw = new PrintWriter(Files.newOutputStream(Paths.get(PATH_SYNC), StandardOpenOption.APPEND, StandardOpenOption.CREATE), true)) {
				// Print the url to submit
				pw.println(url);
				// Print new line to separate
				pw.println();
			}

			return;
		}

		// Submit the request along with the token
		// and check if an error was returned
		if (submit(url) == STATUS_ERROR) {
			// This should never happen as the token and tasks
			// are retrieved from the database
			throw new IllegalStateException(getErrorMessage());
		}
	}

	/**
	 * Updates a task step comment using the user token and the
	 * task step
	 *
	 * @param token The token for the current user
	 * @param step  The task step to update
	 */
	public void updateTaskStepComment(String token, Step step) throws IOException, ParseException {
		updateTaskStepComment(token, step.getId(), step.getComment());
	}

	/**
	 * Updates a task step comment using the user token and the
	 * task step id
	 *
	 * @param token   The token for the current user
	 * @param id      The unique id for the task step
	 * @param comment The comment to set for the task step
	 */
	public void updateTaskStepComment(String token, int id, String comment) throws IOException, ParseException {
		// Create the url to submit with the token
		String url = String.format(URL_SET_COMMENT, token, id);

		// If we're offline then save updates to file
		if (!Service.checkConnection()) {
			// Create new file if it doens't exit
			// Append changes to the file
			try (PrintWriter pw = new PrintWriter(Files.newOutputStream(Paths.get(PATH_SYNC), StandardOpenOption.APPEND, StandardOpenOption.CREATE), true)) {
				// Print the url to submit
				pw.println(url);
				// Print the comment for POST
				pw.println(String.format(URL_SET_COMMENT_POST, encode(comment)));
				// Print new line to separate
				pw.println();
			}

			return;
		}

		// Submit the request along with the id and comment (for POST data)
		// Encode the comment for the url
		if (submit(url, String.format(URL_SET_COMMENT_POST, encode(comment))) == STATUS_ERROR) {
			// This should never happen as the token and tasks
			// are retrieved from the database
			throw new IllegalStateException(getErrorMessage());
		}
	}
}
