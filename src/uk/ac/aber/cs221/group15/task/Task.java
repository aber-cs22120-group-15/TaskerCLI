package uk.ac.aber.cs221.group15.task;

import javafx.beans.property.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Represents a single task for a user. Each task has an id, title,
 * creator (assigned by), start and end dates, status and a list of steps.
 * Both the status and steps can be set as completed. A Task can have many
 * steps.
 *
 * @author Darren White
 * @version 0.2.2
 */
public class Task {

	/**
	 * Status for a task that has been abandoned.
	 */
	public static final int ABANDONED = 0;

	/**
	 * Status for a task that has been allocated. A user can set a Task as
	 * being allocated if the Task status is currently COMPLETED and they
	 * haven't actually completed the Task.
	 */
	public static final int ALLOCATED = 1;

	/**
	 * Status for a task that has been completed. A user can set a Task as
	 * completed if and only if the current status of that Task is ALLOCATED.
	 */
	public static final int COMPLETED = 2;

	/**
	 * The date format used for date/time values
	 */
	public static final SimpleDateFormat DATE_FORMAT =
			new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	/**
	 * The unique task id
	 */
	private final ReadOnlyIntegerProperty id;

	/**
	 * The title of the task
	 */
	private final ReadOnlyStringProperty title;

	/**
	 * The member who created this task
	 */
	private final ReadOnlyStringProperty creator;

	/**
	 * Date task was created
	 */
	private final ReadOnlyObjectProperty<Calendar> dateCreated;

	/**
	 * Expected completed date for the task
	 */
	private final ReadOnlyObjectProperty<Calendar> dateDue;

	/**
	 * Completion date for the task
	 */
	private final ObjectProperty<Calendar> dateCompleted;

	/**
	 * The current status of the task
	 */
	private final IntegerProperty status;

	/**
	 * The steps for this task - using a set doesn't allow duplicates
	 */
	private final ReadOnlyObjectProperty<Set<Step>> steps;

	/**
	 * Creates a new task with the given information
	 *
	 * @param id            The task id
	 * @param title         The title of the task
	 * @param creator       The member who created the task
	 * @param dateCreated   The date the task was created
	 * @param dateDue       The expected completion date
	 * @param dateCompleted The date the task was completed
	 * @param status        The current status of the task
	 */
	public Task(int id, String title, String creator, Calendar dateCreated,
	            Calendar dateDue, Calendar dateCompleted, int status) {
		this.id = new ReadOnlyIntegerWrapper(id);
		this.title = new ReadOnlyStringWrapper(title);
		this.creator = new ReadOnlyStringWrapper(creator);
		this.dateCreated = new ReadOnlyObjectWrapper<>(dateCreated);
		this.dateDue = new ReadOnlyObjectWrapper<>(dateDue);
		this.dateCompleted = new SimpleObjectProperty<>(dateCompleted);
		this.status = new SimpleIntegerProperty(status);
		// LinkedHashSet preserves order
		this.steps = new ReadOnlyObjectWrapper<>(new LinkedHashSet<>());
	}

	/**
	 * Add a step to this task
	 *
	 * @param step The step to add
	 */
	public void addStep(Step step) {
		stepsProperty().get().add(step);
	}

	/**
	 * Gets the creator property
	 *
	 * @return The member name property
	 */
	public ReadOnlyStringProperty creatorProperty() {
		return creator;
	}

	/**
	 * Gets the date completed property
	 *
	 * @return The date completed property
	 */
	public ObjectProperty<Calendar> dateCompletedProperty() {
		return dateCompleted;
	}

	/**
	 * Gets the date created property
	 *
	 * @return The date created property
	 */
	public ReadOnlyObjectProperty<Calendar> dateCreatedProperty() {
		return dateCreated;
	}

	/**
	 * Gets the date due property
	 *
	 * @return The date due property
	 */
	public ReadOnlyObjectProperty<Calendar> dateDueProperty() {
		return dateDue;
	}

	/**
	 * Gets the member who created this task
	 *
	 * @return The member name
	 */
	public String getCreator() {
		return creatorProperty().get();
	}

	/**
	 * The date this task was completed
	 *
	 * @return The date this task was completed
	 */
	public Calendar getDateCompleted() {
		return dateCompletedProperty().get();
	}

	/**
	 * The date this task was created
	 *
	 * @return The date this task was created
	 */
	public Calendar getDateCreated() {
		return dateCreatedProperty().get();
	}

	/**
	 * The expected completion date
	 *
	 * @return The expected completion date
	 */
	public Calendar getDateDue() {
		return dateDueProperty().get();
	}

	/**
	 * Gets this tasks unique id
	 *
	 * @return This tasks id
	 */
	public int getId() {
		return idProperty().get();
	}

	/**
	 * The current status of this task
	 *
	 * @return The current status
	 * @link Task.ABANDONED
	 * @link Task.ALLOCATED
	 * @link Task.COMPLETED
	 */
	public int getStatus() {
		return statusProperty().get();
	}

	/**
	 * Gets the status as a readable string
	 *
	 * @return The status as a string
	 */
	public String getStatusString() {
		// Convert the status integer to readable string
		switch (getStatus()) {
			case ABANDONED:
				return "Abandoned";
			case ALLOCATED:
				return "Allocated";
			case COMPLETED:
				return "Completed";
			default:
				throw new IllegalArgumentException("Invalid status property: " + getStatus());
		}
	}

	/**
	 * Gets the task steps
	 *
	 * @return The steps for this task
	 */
	public Set<Step> getSteps() {
		return stepsProperty().get();
	}

	/**
	 * Gets the task title
	 *
	 * @return The title of the task
	 */
	public String getTitle() {
		return titleProperty().get();
	}

	/**
	 * Gets the id property
	 *
	 * @return The id property
	 */
	public ReadOnlyIntegerProperty idProperty() {
		return id;
	}

	public static Task readTask(ObjectInputStream in) throws IOException, ClassNotFoundException {

		int id = in.readInt();
		String title = in.readUTF();
		String creator = in.readUTF();
		Calendar dateCreated = (Calendar) in.readObject();
		Calendar dateDue = (Calendar) in.readObject();
		Calendar dateCompleted = (Calendar) in.readObject();
		int status = in.readInt();

		Task t = new Task(id, title, creator, dateCreated, dateDue, dateCompleted, status);

		int len = in.readInt();
		while (len-- > 0) {
			t.addStep(Step.readStep(in));
		}

		return t;
	}

	/**
	 * Set the date completed for this task
	 *
	 * @param dateCompleted The date this task was completed
	 */
	public void setDateCompleted(Calendar dateCompleted) {
		this.dateCompleted.set(dateCompleted);
	}

	/**
	 * Set the status for this task
	 *
	 * @param status The status to change this task to
	 * @link Task.ALLOCATED
	 * @link Task.COMPLETED
	 */
	public void setStatus(int status) {
		this.status.set(status);
	}

	/**
	 * The current status property of this task
	 *
	 * @return The current status property
	 */
	public IntegerProperty statusProperty() {
		return status;
	}

	/**
	 * Gets the steps property
	 *
	 * @return The steps property
	 */
	public ReadOnlyObjectProperty<Set<Step>> stepsProperty() {
		return steps;
	}

	/**
	 * Gets the title property
	 *
	 * @return The title property
	 */
	public ReadOnlyStringProperty titleProperty() {
		return title;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "Task{" +
				"id=" + id +
				", title=" + title +
				", creator=" + creator +
				", dateCreated=" + dateCreated +
				", dateDue=" + dateDue +
				", dateCompleted=" + dateCompleted +
				", status=" + status +
				", steps=" + steps +
				'}';
	}

	public void writeTask(ObjectOutputStream out) throws IOException {
		writeTask(this, out);
	}

	public static void writeTask(Task t, ObjectOutputStream out) throws IOException {
		out.writeInt(t.getId());
		out.writeUTF(t.getTitle());
		out.writeUTF(t.getCreator());
		out.writeObject(t.getDateCreated());
		out.writeObject(t.getDateDue());
		out.writeObject(t.getDateCompleted());
		out.writeInt(t.getStatus());
		out.writeInt(t.getSteps().size());
		t.getSteps().forEach(step -> {
			try {
				step.writeStep(out);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
}