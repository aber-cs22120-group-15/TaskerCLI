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
 * @author Darren White
 * @version 0.0.3
 */
public class TaskSync extends TimerTask implements Callable<ObservableList<Task>> {

	/**
	 * The path to store local sync updates
	 */
	private static final String PATH_SYNC = System.getProperty("user.home") +
			File.separator + ".tasker_sync";

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
		// Used to store the synced tasks
		ObservableList<Task> newTasks = FXCollections.observableList(new LinkedList<>());
		// The path to store the tasks locally
		Path p = Paths.get(PATH_TASKS);

		checkSyncUpdates();

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

		writeToFile(newTasks);

		Platform.runLater(() -> {
			// Use the new synced tasks
			tasks.clear();
			tasks.addAll(newTasks);
		});

		return tasks;
	}

	private void checkSyncUpdates() throws IOException {
		Path sync = Paths.get(PATH_SYNC);
		String content = "";
		String line;
		String failedContent = "";

		if (!Files.exists(sync)) {
			return;
		}

		try (BufferedReader in = new BufferedReader(new InputStreamReader(Files.newInputStream(sync)))) {
			while ((line = in.readLine()) != null) {
				content += line + '\n';
			}
		}

		Matcher m = Pattern.compile("(http.*)\\n(.*)\\n").matcher(content);

		while (m.find()) {
			String url = m.group(1), post = m.group(2);
			int status = -1;

			url = url.replace(Login.TOKEN_OFFLINE, token);

			try {
				if (!post.isEmpty()) {
					status = service.submit(url);
				} else {
					status = service.submit(url, post);
				}
			} catch (ParseException e) {
				System.err.println("Unable to sync local updates");
				e.printStackTrace();
			}

			if (status != Service.STATUS_SUCCESS) {
				failedContent += url + '\n' + post + '\n' + '\n';
			}
		}

		Files.delete(sync);

		try (PrintWriter pw = new PrintWriter(Files.newOutputStream(sync, StandardOpenOption.CREATE_NEW))) {
			pw.println(failedContent);
		}
	}

	public void forceSync() {
		executor.submit((Callable) this);
	}

	public ObservableList<Task> getTasks() {
		return tasks;
	}

	public ObservableList<Task> readFromFile() throws IOException, ClassNotFoundException {
		ObservableList<Task> newTasks = FXCollections.observableList(new LinkedList<>());
		Path p = Paths.get(PATH_TASKS);

		if (!Files.exists(p)) {
			return tasks;
		}

		try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(p))) {
			int len = in.readInt();
			while (len-- > 0) {
				newTasks.add(Task.readTask(in));
			}
		}

		return newTasks;
	}

	@Override
	public void run() {
		try {
			call();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void writeToFile(ObservableList<Task> tasks) throws IOException {
		try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(Paths.get(PATH_TASKS)))) {
			out.writeInt(tasks.size());
			tasks.forEach(t -> {
				try {
					t.writeTask(out);
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		}
	}

	public void writeToFile() throws IOException {
		writeToFile(tasks);
	}
}