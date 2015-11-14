package uk.ac.aber.cs221.group15.task;

import java.util.Collections;
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
 * @version 0.0.1
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
	 * This tasks steps which the user can edit
	 * We use a LinkedHashSet as it preserves the order
	 * of Steps in the set.
	 * <p>
	 * TODO allow step comments to be added/edited/removed
	 */
	private final Set<Step> steps = new LinkedHashSet<>();

	/**
	 * The unique task id
	 */
	private final int id;

	/**
	 * The title of the task
	 */
	private final String title;

	/**
	 * The member who created this task
	 */
	private final String creator;

	/**
	 * Date task was created, expected completion date and
	 * completition date
	 */
	private final Date dateCreated, dateDue, dateCompleted;

	/**
	 * The current status of the task
	 */
	private int status;

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
		this.id = id;
		this.title = title;
		this.creator = creator;
		this.dateCreated = dateCreated;
		this.dateDue = dateDue;
		this.dateCompleted = dateCompleted;
		this.status = status;
	}

	/**
	 * Add a new step for this task - cannot have duplicates (same description)
	 *
	 * @param step The step to add
	 * @return If the step was added
	 */
	public boolean addStep(Step step) {
		return steps.add(step);
	}

	/**
	 * Creates a new Step with the description and comment and adds it
	 *
	 * @param description The description of the step
	 * @param comment     The step comment (or null if none)
	 * @return If the step was added
	 */
	public boolean addStep(String description, String comment) {
		// Create the step and add it
		return steps.add(new Step(description, comment));
	}

	/**
	 * Gets the member who created this task
	 *
	 * @return The member name
	 */
	public String getCreator() {
		return creator;
	}

	/**
	 * The date this task was completed
	 *
	 * @return The date this task was completed
	 */
	public Date getDateCompleted() {
		return dateCompleted;
	}

	/**
	 * The date this task was created
	 *
	 * @return The date this task was created
	 */
	public Date getDateCreated() {
		return dateCreated;
	}

	/**
	 * The expected completion date
	 *
	 * @return The expected completion date
	 */
	public Date getDateDue() {
		return dateDue;
	}

	/**
	 * Gets this tasks unique id
	 *
	 * @return This tasks id
	 */
	public int getId() {
		return id;
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
		return status;
	}

	/**
	 * Gets all the steps for this task. Note: this
	 * Set of steps return is unmodifiable.
	 *
	 * @return A set of steps (both completed and not)
	 */
	public Set<Step> getSteps() {
		return Collections.unmodifiableSet(steps);
	}

	/**
	 * Gets the task title
	 *
	 * @return The title of the task
	 */
	public String getTitle() {
		return title;
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

		this.status = status;
	}
}