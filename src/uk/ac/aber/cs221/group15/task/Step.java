package uk.ac.aber.cs221.group15.task;

/**
 * Represents a single step for a task. A step can
 * be set as completed and contains a comment.
 * Each task can have many steps but a step can only
 * have one task
 *
 * @author Darren White
 * @version 0.0.1
 */
public class Step {

	/**
	 * The description of the task step
	 */
	private final String description;

	/**
	 * The user comment for the task step
	 */
	private String comment;

	/**
	 * Creates a new step with the description and
	 * the user comment if it exists
	 *
	 * @param description The description of the task step
	 * @param comment     The user comment of the task step
	 */
	public Step(String description, String comment) {
		this.description = description;
		this.comment = comment;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object o) {
		return this == o ||
				!(o == null || getClass() != o.getClass()) &&
						getDescription().equals(((Step) o).getDescription());
	}

	/**
	 * The user comment for this task step
	 *
	 * @return The comment for the step
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * Gets the description associated with this task step
	 *
	 * @return The desription of this task step
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return getDescription().hashCode();
	}

	/**
	 * Allows the user to change this task step
	 *
	 * @param comment The String to set the comment as
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}
}