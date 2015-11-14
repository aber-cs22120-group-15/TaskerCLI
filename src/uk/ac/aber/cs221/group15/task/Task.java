package uk.ac.aber.cs221.group15.task;

import java.util.Date;
import java.util.LinkedList;

/**
 * Represents a single task for a user. Each task has an id, title,
 * member (assigned by), start and end dates, status and a list of steps.
 * Both the status and steps can be set as completed. A Task can have many
 * steps.
 *
 * @author Darren White
 * @version 0.0.1
 * @since 0.0.1
 */
public class Task {

	/**
	 * This tasks steps
	 */
	private final LinkedList<Step> steps = new LinkedList<>();

	/**
	 * The unique task id
	 */
	private final int id;

	/**
	 * The title of the task
	 */
	private final String title;

	/**
	 * The member who assigned this task
	 */
	private final String member;

	/**
	 * Date task was started and expected completion date
	 */
	private final Date startDate, endDate;

	/**
	 * The current status of the task
	 */
	private int status;
	// TODO add status constants

	/**
	 * Creates a new task with the given information
	 *
	 * @param id The task id
	 * @param title The title of the task
	 * @param member The member who assigned the task
	 * @param startDate The date the task was assigned
	 * @param endDate The expected completion date
	 */
	public Task(int id, String title, String member, Date startDate, Date endDate) {
		this.id = id;
		this.title = title;
		this.member = member;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	/**
	 * The expected completion date
	 *
	 * @return The expected completion date
	 */
	public Date getEndDate() {
		return endDate;
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
	 * Gets the member who assigned this task
	 *
	 * @return The asignee
	 */
	public String getMember() {
		return member;
	}

	/**
	 * The date this task was assigned
	 *
	 * @return The date this task was started
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * The current status of this task
	 *
	 * @return The current status
	 * @see // TODO
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * Gets all the steps for this task
	 *
	 * @return A list of steps (both completed and not)
	 */
	public LinkedList<Step> getSteps() {
		return steps;
	}

	/**
	 * Gets the task title
	 *
	 * @return The title of the task
	 */
	public String getTitle() {
		return title;
	}
}