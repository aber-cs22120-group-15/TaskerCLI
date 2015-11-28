package uk.ac.aber.cs221.group15.service;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import uk.ac.aber.cs221.group15.task.Step;
import uk.ac.aber.cs221.group15.task.Task;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This service provides functionality to submit a request
 * to get all tasks for a user using the unique token
 *
 * @author Darren White
 * @version 0.0.4
 */
public class TaskService extends Service {

	/**
	 * The method to submit in the final url to list
	 * all the users tasks
	 */
	private static final String METHOD_LIST_TASKS = "list_tasks";

	/**
	 * The extra arguments needed for the method to submit.
	 * The user token/key is needed to get the list of tasks.
	 */
	private static final String METHOD_LIST_TASKS_ARGUMENTS = "&token=%s";

	/**
	 * The method to submit in the final url to
	 * list a tasks' steps
	 */
	private static final String METHOD_GET_STEPS = "get_steps";

	/**
	 * The extra arguments needed for the method to submit.
	 * The user token/key is needed and a task id to get the task steps.
	 */
	private static final String METHOD_GET_STEPS_ARGUMENTS = "&token=%s&id=%d";

	/**
	 * The method to submit in the final url to list
	 * all the users tasks
	 */
	private static final String METHOD_UPDATE_STATUS = "change_status";

	/**
	 * The extra arguments needed for the method to submit.
	 * The user token/key is needed and a task id to get the task steps.
	 */
	private static final String METHOD_UPDATE_STATUS_ARGUMENTS = "&token=%s&id=%d&status=%d";

	/**
	 * The method to submit in the final url to
	 * set a task step comment
	 */
	private static final String METHOD_SET_COMMENT = "set_task_step_comment";

	/**
	 * The key attribute to get the task list value
	 */
	private static final String KEY_TASKS = "tasks";

	/**
	 * The key attribute to get the task list value
	 */
	private static final String KEY_STEPS = "steps";

	/**
	 * The key attribute to get the task/step id
	 */
	private static final String KEY_ID = "id";

	/**
	 * The key attribute to get the task title
	 */
	private static final String KEY_TITLE = "title";

	/**
	 * The key attribute to get the name of the
	 * member who created the task
	 */
	private static final String KEY_CREATOR = "created_name";

	/**
	 * The key attribute to get the date the task
	 * was created
	 */
	private static final String KEY_DATE_CREATED = "created_time";

	/**
	 * The key attribute to get the date the task
	 * is due by
	 */
	private static final String KEY_DATE_DUE = "due_by";

	/**
	 * The key attribute to get the date the task
	 * was completed
	 */
	private static final String KEY_DATE_COMPLETED = "completed_time";

	/**
	 * The key attribute to get the task status
	 */
	private static final String KEY_STATUS = "status";

	/**
	 * The key attribute to get the step title
	 */
	private static final String KEY_STEP_TITLE = "title";

	/**
	 * The key attribute to get the step comment
	 */
	private static final String KEY_COMMENT = "comment";

	/**
	 * The date format used to parse date/time values
	 */
	private static final SimpleDateFormat format =
			new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	/**
	 * Gets the steps for a specific task using the
	 * user token and the task id
	 *
	 * @param token The token for the current user
	 * @param id    The unique id for the task
	 * @return The steps associated with the task with the id
	 */
	public Set<Step> getTaskSteps(String token, int id) throws IOException, ParseException {
		// Use a LinkedHashSet so no duplicates are added
		// and ordering is preserved
		Set<Step> steps = new LinkedHashSet<>();
		// Create the url to submit with the method, and token
		String url = String.format(URL_METHOD_TEMPLATE, METHOD_GET_STEPS) +
				String.format(METHOD_GET_STEPS_ARGUMENTS, token, id);
		// Submit the request along with the token
		int status = submit(url);

		// An error occurred, handle it
		if (status == STATUS_ERROR) {
			// This should never happen as the token
			// is retrieved at login and task ids are loaded
			// from list_tasks method
			throw new IllegalStateException(getErrorMessage());
		} else if (status == STATUS_SUCCESS) {
			// Get the response object
			JSONObject response = (JSONObject) getResponse();
			// Get the list of tasks as an array
			JSONArray stepList = (JSONArray) response.get(KEY_STEPS);

			// Iterate over the tasks
			for (Object o : stepList) {
				// Each object is a JSON object
				JSONObject obj = (JSONObject) o;

				// Parse the step and add it to the set
				steps.add(parseStep(obj));
			}
		}

		return steps;
	}

	/**
	 * Gets the tasks from the database for the user
	 * using the token
	 *
	 * @param token The token for the current user
	 * @throws IOException    If an I/O exception occurs
	 * @throws ParseException If a Parse exception occurs
	 */
	public void getTasks(String token, ObservableList<Task> tasks) throws IOException, ParseException {
		// Create the url to submit with the method, and token
		String url = String.format(URL_METHOD_TEMPLATE, METHOD_LIST_TASKS) +
				String.format(METHOD_LIST_TASKS_ARGUMENTS, token);
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
			JSONArray taskList = (JSONArray) response.get(KEY_TASKS);

			// Iterate over the tasks
			for (Object o : taskList) {
				// Each object is a JSON object
				JSONObject obj = (JSONObject) o;

				// Parse the task
				Task task = parseTask(obj);

				// Get the task steps
				task.addSteps(getTaskSteps(token, task.getId()));

				// Add it to the list
				// Run on the JavaFX thread so it can update the table
				Platform.runLater(() -> tasks.add(task));
			}
		}
	}

	/**
	 * Try and parse the dates from the String
	 *
	 * @param s The date in string format
	 * @return The Date parsed from the string
	 * @link format
	 */
	private Date parseDate(String s) {
		try {
			// Try and parse the date from the string
			return format.parse(s);
		} catch (java.text.ParseException e) {
			e.printStackTrace();
			// Otherwise return a default date
			return new Date(0);
		}
	}

	/**
	 * Parse the step from the JSONObject
	 *
	 * @param obj The object to parse the step from
	 * @return The new step from the object
	 */
	private Step parseStep(JSONObject obj) {
		// Get step id (cannot cast to int as json works with strings)
		int id = Integer.parseInt((String) obj.get(KEY_ID));
		// Get the title of the step
		String title = (String) obj.get(KEY_STEP_TITLE);
		// Get the comment of the step
		String comment = (String) obj.get(KEY_COMMENT);

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
		int id = Integer.parseInt((String) obj.get(KEY_ID));
		// Get the title of the task
		String title = (String) obj.get(KEY_TITLE);
		// Get the member name who created the task
		String creator = (String) obj.get(KEY_CREATOR);
		// Parse dates
		Date dateCreated = parseDate((String) obj.get(KEY_DATE_CREATED));
		Date dateDue = parseDate((String) obj.get(KEY_DATE_DUE));
		Date dateCompleted = null;
		// Get the task status
		int status = Integer.parseInt((String) obj.get(KEY_STATUS));

		// Task date can only be set if the task has been completed
		if (status == Task.COMPLETED) {
			dateCompleted = parseDate((String) obj.get(KEY_DATE_COMPLETED));
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
		updateTaskStatus(token, task.getId(), task.getStatus());
	}

	/**
	 * Updates a task status using the user token and the task id
	 *
	 * @param token  The token for the current user
	 * @param id     The unique id for the task
	 * @param status The status to set for the task
	 */
	public void updateTaskStatus(String token, int id, int status) throws IOException, ParseException {
		// Create the url to submit with the method, token, id and status
		String url = String.format(URL_METHOD_TEMPLATE, METHOD_UPDATE_STATUS) +
				String.format(METHOD_UPDATE_STATUS_ARGUMENTS, token, id, status);
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
		// TODO
	}
}