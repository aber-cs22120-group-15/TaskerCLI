package uk.ac.aber.cs221.group15.task;

import javafx.beans.property.*;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Represents a single task for a user. Each task has an id, title,
 * creator (assigned by), start and end dates, status and a list of steps.
 * Both the status and steps can be set as completed. A Task can have many
 * steps.
 *
 * @author Darren White
 * @version 0.1.0
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
	private final ReadOnlyObjectProperty<Date> dateCreated;

	/**
	 * Expected completed date for the task
	 */
	private final ReadOnlyObjectProperty<Date> dateDue;

	/**
	 * Completion date for the task
	 */
	private final ObjectProperty<Date> dateCompleted;

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
	public Task(int id, String title, String creator, Date dateCreated,
	            Date dateDue, Date dateCompleted, int status) {
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
	 * Add steps for this task
	 *
	 * @param steps The steps to add
	 */
	public void addSteps(Collection<? extends Step> steps) {
		stepsProperty().getValue().addAll(steps);
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
	public ObjectProperty<Date> dateCompletedProperty() {
		return dateCompleted;
	}

	/**
	 * Gets the date created property
	 *
	 * @return The date created property
	 */
	public ReadOnlyObjectProperty<Date> dateCreatedProperty() {
		return dateCreated;
	}

	/**
	 * Gets the date due property
	 *
	 * @return The date due property
	 */
	public ReadOnlyObjectProperty<Date> dateDueProperty() {
		return dateDue;
	}

	/**
	 * Gets the member who created this task
	 *
	 * @return The member name
	 */
	public String getCreator() {
		return creatorProperty().getValue();
	}

	/**
	 * The date this task was completed
	 *
	 * @return The date this task was completed
	 */
	public Date getDateCompleted() {
		return dateCompletedProperty().getValue();
	}

	/**
	 * The date this task was created
	 *
	 * @return The date this task was created
	 */
	public Date getDateCreated() {
		return dateCreatedProperty().getValue();
	}

	/**
	 * The expected completion date
	 *
	 * @return The expected completion date
	 */
	public Date getDateDue() {
		return dateDueProperty().getValue();
	}

	/**
	 * Gets this tasks unique id
	 *
	 * @return This tasks id
	 */
	public int getId() {
		return idProperty().getValue();
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
		return statusProperty().getValue();
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
		return stepsProperty().getValue();
	}

	/**
	 * Gets the task title
	 *
	 * @return The title of the task
	 */
	public String getTitle() {
		return titleProperty().getValue();
	}

	/**
	 * Gets the id property
	 *
	 * @return The id property
	 */
	public ReadOnlyIntegerProperty idProperty() {
		return id;
	}

	/**
	 * Set the date completed for this task
	 *
	 * @param dateCompleted The date this task was completed
	 */
	public void setDateCompleted(Date dateCompleted) {
		this.dateCompleted.setValue(dateCompleted);
	}

	/**
	 * Set the status for this task
	 *
	 * @param status The status to change this task to
	 * @link Task.ALLOCATED
	 * @link Task.COMPLETED
	 */
	public void setStatus(int status) {
		if (status == ABANDONED) {
			throw new IllegalArgumentException("Not allowed to set status as ABANDONED!");
		}

		this.status.setValue(status);
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
}