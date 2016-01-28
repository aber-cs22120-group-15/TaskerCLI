package uk.ac.aber.cs221.group15;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.json.simple.parser.ParseException;
import uk.ac.aber.cs221.group15.gui.Login;
import uk.ac.aber.cs221.group15.service.Service;
import uk.ac.aber.cs221.group15.service.TaskService;
import uk.ac.aber.cs221.group15.task.Task;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TaskSync is used to sync tasks with the server as well as submit local updates
 * when connected. All syncing is done in here.
 *
 * @author Darren White
 * @version 0.0.4
 */
public class TaskSync extends TimerTask implements Callable<ObservableList<Task>> {

	/**
	 * The path to store local sync updates
	 */
	private static final String PATH_SYNC = TaskerCLI.getUserHomeDir() +
			File.separator + ".tasker_sync";

	/**
	 * The path to store tasks locally
	 */
	private static final String PATH_TASKS = TaskerCLI.getUserHomeDir() +
			File.separator + ".tasker_tasks";

	/**
	 * The service used to submit requests to get task steps
	 */
	private final TaskService service = new TaskService();

	/**
	 * The set of tasks for the user
	 */
	private final ObservableList<Task> tasks = FXCollections.observableList(new LinkedList<>());

	/**
	 * The executor service used for scheduling sync updates for tasks
	 */
	private final ScheduledExecutorService executor;

	/**
	 * The current users token
	 */
	private final String token;

	/**
	 * Creates a new TaskSync
	 *
	 * @param token The token for the current user
	 */
	public TaskSync(String token) {
		this.token = token;

		// Create the executor scheduling service
		executor = Executors.newSingleThreadScheduledExecutor(r -> {
			Thread t = new Thread(r);

			// Make the thread daemon
			if (!t.isDaemon()) {
				t.setDaemon(true);
			}

			// Set to normal priority
			t.setPriority(Thread.NORM_PRIORITY);

			return t;
		});
		// Used for scheduling sync updates to the server every 5 minutes
		executor.scheduleAtFixedRate(this, 0, 5, TimeUnit.MINUTES);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ObservableList<Task> call() throws Exception {
		// Used to store the synced tasks
		ObservableList<Task> newTasks = FXCollections.observableList(new LinkedList<>());
		// The path to store the tasks locally
		Path p = Paths.get(PATH_TASKS);

		// Check for local updates to submit
		checkSyncUpdates();

		// If we don't have a connection to the server then load the locally
		// stored tasks
		if (!Service.checkConnection()) {
			newTasks.addAll(readFromFile());

			Platform.runLater(() -> {
				tasks.clear();
				tasks.addAll(newTasks);
			});

			return tasks;
		}

		try {
			// Try and load the tasks from the database
			service.getTasks(newTasks, token);
		} catch (IOException | ParseException e) {
			System.err.println("Unable to load tasks from database");
			e.printStackTrace();
		}

		Platform.runLater(() -> {
			// Use the new synced tasks
			tasks.clear();
			tasks.addAll(newTasks);
		});

		// Store the tasks locally
		writeToFile(newTasks);

		return tasks;
	}

	/**
	 * Check if there are any local updates to be submitted and synced with
	 * the server
	 *
	 * @throws IOException If an I/O exception occurs
	 */
	private void checkSyncUpdates() throws IOException {
		// The path for the local updates
		Path sync = Paths.get(PATH_SYNC);
		// Contents of the file
		String content = "";
		// The current line being read
		String line;
		// Updates that failed and should be saved again
		String failedContent = "";

		// The file doesn't exist so no updates to sync
		if (!Files.exists(sync)) {
			return;
		}

		// Read the file contents
		try (BufferedReader in = new BufferedReader(new InputStreamReader(Files.newInputStream(sync)))) {
			while ((line = in.readLine()) != null) {
				content += line + '\n';
			}
		}

		// Regex match for the file
		// First line is the url
		// Second line is post argument (might be blank)
		// Third line is blank
		Matcher m = Pattern.compile("(http.*)\\n(.*)\\n").matcher(content);

		// Iterate all matches
		while (m.find()) {
			// Get the matches
			String url = m.group(1), post = m.group(2);
			int status = -1;

			// When offline a temporary token is used so replace it
			url = url.replace(Login.TOKEN_OFFLINE, token);

			try {
				// Submit the request (if there is a post, send it too)
				if (!post.isEmpty()) {
					status = service.submit(url, post);
				} else {
					status = service.submit(url);
				}
			} catch (ParseException e) {
				System.err.println("Unable to sync local updates");
				e.printStackTrace();
			}

			// If we couldn't submit the update then add it back to the file
			// in the same format
			if (status != Service.STATUS_SUCCESS) {
				failedContent += url + '\n' + post + '\n' + '\n';
			}
		}

		// Delete the old updates
		Files.delete(sync);

		// If there are any failed updates, save them
		if (!failedContent.isEmpty()) {
			try (PrintWriter pw = new PrintWriter(Files.newOutputStream(sync, StandardOpenOption.CREATE_NEW))) {
				pw.println(failedContent);
			}
		}
	}

	/**
	 * Forces an update to the server
	 */
	public void forceSync() {
		// Just submit this as a callable (not runnable) as the runnable is the
		// timer task although it wouldn't make a big difference
		executor.submit((Callable) this);
	}

	/**
	 * Gets the current list of tasks that have been loaded
	 *
	 * @return The task list
	 */
	public ObservableList<Task> getTasks() {
		return tasks;
	}

	/**
	 * Read the tasks locally from file if the file exists
	 *
	 * @return The local task list
	 * @throws IOException If an I/O exception occurs
	 * @throws ClassNotFoundException If a ClassNotFoundExceptionOccurs
	 */
	public ObservableList<Task> readFromFile() throws IOException, ClassNotFoundException {
		// Store the local tasks in a list
		ObservableList<Task> newTasks = FXCollections.observableList(new LinkedList<>());
		// The path to the file
		Path p = Paths.get(PATH_TASKS);

		// If it doesn't exist return the already loaded tasks
		if (!Files.exists(p)) {
			return tasks;
		}

		// Open a new input stream
		try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(p))) {
			// Read the number of tasks that are in the file
			int len = in.readInt();
			// Iterate all of the tasks and read them
			while (len-- > 0) {
				newTasks.add(Task.readTask(in));
			}
		}

		return newTasks;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		// This is for the timer task
		// so just execute the callable
		try {
			call();
		} catch (Exception e) {
			System.err.println("Unable to sync with the server");
			e.printStackTrace();
		}
	}

	/**
	 * Write the currently loaded tasks to file
	 *
	 * @throws IOException If an I/O exception occurs
	 */
	public void writeToFile() throws IOException {
		// Just write the loaded task list to file
		writeToFile(tasks);
	}

	/**
	 * Write the tasks locally to a file
	 *
	 * @param tasks The tasks to write to the file
	 * @throws IOException If an I/O exception occurs
	 */
	private void writeToFile(ObservableList<Task> tasks) throws IOException {
		// Open a new output stream
		try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(Paths.get(PATH_TASKS)))) {
			// Write the number of tasks (so we know how many to read)
			out.writeInt(tasks.size());
			// Iterate the tasks and write them to file
			tasks.forEach(t -> {
				try {
					t.writeTask(out);
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		}
	}
}