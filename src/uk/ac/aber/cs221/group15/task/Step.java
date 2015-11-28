package uk.ac.aber.cs221.group15.task;

import javafx.beans.property.*;

/**
 * Represents a single step for a task. A step can
 * be set as completed and contains a comment.
 * Each task can have many steps but a step can only
 * have one task
 *
 * @author Darren White
 * @version 0.1.0
 */
public class Step {

	/**
	 * The unique id for this task step
	 */
	private final ReadOnlyIntegerProperty id;

	/**
	 * The title of the task step
	 */
	private final ReadOnlyStringProperty title;

	/**
	 * The user comment for the task step
	 */
	private final StringProperty comment;

	/**
	 * Creates a new step with the id, title and
	 * the user comment if it exists
	 *
	 * @param title   The title of the task step
	 * @param comment The user comment of the task step
	 */
	public Step(int id, String title, String comment) {
		this.id = new ReadOnlyIntegerWrapper(id);
		this.title = new ReadOnlyStringWrapper(title);
		this.comment = new SimpleStringProperty(comment);
	}

	/**
	 * Gets the comment property
	 *
	 * @return The comment property
	 */
	public StringProperty commentProperty() {
		return comment;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object o) {
		return this == o ||
				!(o == null || getClass() != o.getClass()) &&
						getId() == ((Step) o).getId();
	}

	/**
	 * The user comment for this task step
	 *
	 * @return The comment for the step
	 */
	public String getComment() {
		return commentProperty().getValue();
	}

	/**
	 * Gets this step unique id
	 *
	 * @return The step id
	 */
	public int getId() {
		return idProperty().getValue();
	}

	/**
	 * Gets the title associated with this task step
	 *
	 * @return The desription of this task step
	 */
	public String getTitle() {
		return titleProperty().getValue();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return getId();
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
	 * Allows the user to change this task step
	 *
	 * @param comment The String to set the comment as
	 */
	public void setComment(String comment) {
		this.comment.setValue(comment);
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
		return "Step{id=" + getId() + ", title='" + getTitle() +
				"\', comment='" + getComment() + "\'}";
	}
}