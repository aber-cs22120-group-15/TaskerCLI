package uk.ac.aber.cs221.group15.task;

import javafx.beans.property.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Represents a single step for a task. A step can
 * be set as completed and contains a comment.
 * Each task can have many steps but a step can only
 * have one task
 *
 * @author Darren White
 * @version 0.1.4
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
	 * Used to determine if this step has been edited
	 */
	private final BooleanProperty edited;

	/**
	 * Creates a new step with the id, title and
	 * the user comment if it exists
	 *
	 * @param id      The step id
	 * @param title   The title of the task step
	 * @param comment The user comment of the task step
	 */
	public Step(int id, String title, String comment) {
		this.id = new ReadOnlyIntegerWrapper(id);
		this.title = new ReadOnlyStringWrapper(title);
		this.comment = new SimpleStringProperty(comment);
		edited = new SimpleBooleanProperty(false);
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
	 * Gets the edited property
	 *
	 * @return The edited property
	 */
	public BooleanProperty editedProperty() {
		return edited;
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
		return commentProperty().get();
	}

	/**
	 * Gets this step unique id
	 *
	 * @return The step id
	 */
	public int getId() {
		return idProperty().get();
	}

	/**
	 * Gets the title associated with this task step
	 *
	 * @return The desription of this task step
	 */
	public String getTitle() {
		return titleProperty().get();
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
	 * Checks if this step has been edited or not
	 *
	 * @return If the Step has been edited
	 */
	public boolean isEdited() {
		return editedProperty().get();
	}

	/**
	 * Reads a step from the input stream
	 *
	 * @param in The input stream to read from
	 * @return The new step created from the input stream
	 * @throws IOException            If an I/O Exception occurs
	 * @throws ClassNotFoundException If a ClassNotFoundException occurs
	 */
	public static Step readStep(ObjectInputStream in) throws IOException, ClassNotFoundException {
		// Simply read the id, title and comment
		return new Step(in.readInt(), in.readUTF(), in.readUTF());
	}

	/**
	 * Allows the user to change this task step
	 *
	 * @param comment The String to set the comment as
	 */
	public void setComment(String comment) {
		commentProperty().set(comment);
	}

	/**
	 * Sets the edited property (used to indicate if this step has been edited)
	 *
	 * @param edited If this step has been edited or not
	 */
	public void setEdited(boolean edited) {
		editedProperty().set(edited);
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
		return "Step{" +
				"id=" + id +
				", title=" + title +
				", comment=" + comment + '}';
	}

	/**
	 * Writes this step to the output stream
	 *
	 * @param out The output stream to write to
	 * @throws IOException If an I/O Exception occurs
	 */
	public void writeStep(ObjectOutputStream out) throws IOException {
		writeStep(this, out);
	}

	/**
	 * Writes a step to the output stream
	 *
	 * @param out The output stream to write to
	 * @throws IOException If an I/O Exception occurs
	 */
	public static void writeStep(Step step, ObjectOutputStream out) throws IOException {
		out.writeInt(step.getId());
		out.writeUTF(step.getTitle());
		out.writeUTF(step.getComment() != null ? step.getComment() : "");
	}
}