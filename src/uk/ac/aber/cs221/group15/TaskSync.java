package uk.ac.aber.cs221.group15;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.json.simple.parser.ParseException;
import uk.ac.aber.cs221.group15.service.Service;
import uk.ac.aber.cs221.group15.service.TaskService;
import uk.ac.aber.cs221.group15.task.Task;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Darren White
 * @version 0.0.3
 */
public class TaskSync extends TimerTask implements Callable<ObservableList<Task>> {

	private static final String PATH_TASKS = System.getProperty("user.home") +
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
	 * @param token The token for the current user
	 */
	public TaskSync(String token) {
		this.token = token;

		// Create the executor scheduling service
		executor = Executors.newSingleThreadScheduledExecutor(r -> {
			Thread t = new Thread(r);
			if (!t.isDaemon()) {
				t.setDaemon(true);
			}
			t.setPriority(Thread.NORM_PRIORITY);
			return t;
		});
		// Used for scheduling sync updates to the server every 5 minutes
		executor.scheduleAtFixedRate(this, 0, 5, TimeUnit.MINUTES);
	}

	@Override
	public ObservableList<Task> call() throws Exception {
		Path p = Paths.get(PATH_TASKS);

		if (!Service.checkConnection()) {
			if (!Files.exists(p)) {
				return tasks;
			}

			try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(p))) {
				int len = in.readInt();
				while (len-- > 0) {
					tasks.add(Task.readTask(in));
				}
			}

			return tasks;
		}


		// TODO Check for edited tasks and submit them

		// Used to store the synced tasks
		ObservableList<Task> newTasks = FXCollections.observableList(new LinkedList<>());

		try {
			// Try and load the tasks from the database
			service.getTasks(newTasks, token);
		} catch (IOException | ParseException e) {
			System.err.println("Unable to load tasks from database");
			e.printStackTrace();
		}

		try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(p))) {
			out.writeInt(newTasks.size());
			newTasks.forEach(t -> {
				try {
					t.writeTask(out);
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		}

		Platform.runLater(() -> {
			// Use the new synced tasks
			tasks.clear();
			tasks.addAll(newTasks);
		});

		return tasks;
	}

	public void forceSync() {
		executor.submit((Callable) this);
	}

	public ObservableList<Task> getTasks() {
		return tasks;
	}

	@Override
	public void run() {
		try {
			call();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}