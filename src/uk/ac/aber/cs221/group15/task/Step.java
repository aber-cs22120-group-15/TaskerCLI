package uk.ac.aber.cs221.group15.task;

/**
 * Represents a single step for a task. A step can
 * be set as completed and contains a description.
 * Each task can have many steps but a step can only
 * have one task
 *
 * @author Darren White
 * @version 0.0.1
 * @since 0.0.1
 */
public class Step {

	// The description for the step
	private final String description;
	// If the step has been completed or not
	private boolean completed;

	/**
	 * Creates a new step with the description and
	 * if it has been completed or not
	 *
	 * @param description The description of the step
	 * @param completed If the step is completed or not
	 */
	public Step(String description, boolean completed) {
		this.description = description;
		this.completed = completed;
	}

	/**
	 * The description of the step
	 *
	 * @return The description of the step
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Has the step been completed
	 *
	 * @return If the step has been completed or not
	 */
	public boolean isCompleted() {
		return completed;
	}

	/**
	 * Sets the step as completed or not
	 *
	 * @param completed Is the step completed
	 */
	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
}