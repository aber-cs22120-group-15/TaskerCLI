package uk.ac.aber.cs221.group15.service;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
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
 * @version 0.0.1
 */
public class TaskService extends Service {

	/**
	 * The method to submit in the final url
	 */
	private static final String METHOD_NAME = "list_tasks";

	/**
	 * The extra arguments needed for the method to submit.
	 * The user token/key is needed to get the list of tasks.
	 */
	private static final String METHOD_ARGUMENTS = "&token=%s";

	/**
	 * The key attribute to get the task list value
	 */
	private static final String KEY_TASKS = "tasks";

	/**
	 * The key attribute to get the task id
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
	 * The date format used to parse date/time values
	 */
	private static final SimpleDateFormat format =
			new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getMethodName() {
		return METHOD_NAME;
	}

	/**
	 * Gets the tasks from the database for the user
	 * using the token
	 *
	 * @param token The token for the current user
	 * @return The set of tasks retrieved from the database
	 * @throws IOException    If an I/O exception occurs
	 * @throws ParseException If a Parse exception occurs
	 */
	public Set<Task> getTasks(String token) throws IOException, ParseException {
		// Use a LinkedHashSet so no duplicates are added
		// and ordering is preserved
		Set<Task> tasks = new LinkedHashSet<>();
		// Create the final url with the token
		String url = getBaseURL() + String.format(METHOD_ARGUMENTS, token);
		// Submit the request along with the token
		int status = submit(url);

		// An error occurred, handle it
		if (status == STATUS_ERROR) {
			// This should never happen as the token
			// is retrieved at login
			throw new IllegalStateException(getErrorMessage());
		} else if (status == STATUS_SUCCESS) {
			// Get the response object
			JSONObject response = getResponse();
			// Get the list of tasks as an array
			JSONArray taskList = (JSONArray) response.get(KEY_TASKS);

			// Iterate over the tasks
			for (Object o : taskList) {
				// Each object is a JSON object
				JSONObject obj = (JSONObject) o;

				// Parse the task and add it to the set
				tasks.add(parseTask(obj));
			}
		}

		// Return all tasks
		return tasks;
	}

	/**
	 * Try and parse the dates from the String
	 *
	 * @param s The date in string format
	 * @return The Date parsed from the string
	 * @link format
	 */
	private static Date parseDate(String s) {
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
	 * Parse the task values from the JSONObject
	 *
	 * @param obj The object to parse values from
	 * @return The new task with values from the object
	 */
	private static Task parseTask(JSONObject obj) {
		// Get task id (cannot cast to int as json works with strings)
		int id = Integer.parseInt((String) obj.get(KEY_ID));
		// Get the title of the task
		String title = (String) obj.get(KEY_TITLE);
		// Get the member name who created the task
		String creator = (String) obj.get(KEY_CREATOR);
		// Parse dates
		Date dateCreated = parseDate((String) obj.get(KEY_DATE_CREATED));
		Date dateDue = parseDate((String) obj.get(KEY_DATE_DUE));
		Date dateCompleted = parseDate((String) obj.get(KEY_DATE_COMPLETED));
		// Get the task status
		int status = Integer.parseInt((String) obj.get(KEY_STATUS));

		// Create the task and return it
		return new Task(id, title, creator, dateCreated, dateDue,
				dateCompleted, status);
	}
}