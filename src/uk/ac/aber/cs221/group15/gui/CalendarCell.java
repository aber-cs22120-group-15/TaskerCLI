package uk.ac.aber.cs221.group15.gui;

import javafx.scene.control.TableCell;
import uk.ac.aber.cs221.group15.task.Task;

import java.util.Calendar;

/**
 * This class is used to represent Calendar objects in a TableCell
 * using the date format used to parse & format tasks
 *
 * @author Darren White
 * @version 0.0.1
 */
public class CalendarCell<S> extends TableCell<S, Calendar> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void updateItem(Calendar item, boolean empty) {
		// Update the item
		super.updateItem(item, empty);

		// If the cell is empty, set text as empty string
		if (empty || item == null) {
			setText("");
		} else {
			// Format the date and set it as the text
			setText(Task.DATE_FORMAT.format(item.getTime()));
		}
	}
}