package uk.ac.aber.cs221.group15.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import uk.ac.aber.cs221.group15.task.Task;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * This class is used to represent the Dashboard in the Overview
 * of the client which will display statistics and an overview of
 * upcoming tasks and a few major details
 *
 * @author Darren White
 * @version 0.0.2
 * @since 0.0.1
 */
public class DashboardView extends GridPane {

	/**
	 * Defines the height for the four statistics panels
	 */
	public static final int STATISTICS_HEIGHT = 200;

	/**
	 * Creates a new Dashboard
	 */
	public DashboardView() {
		init();
	}

	/**
	 * Initializes this view and all of its components
	 */
	private void init() {
		// Set padding & gaps to 10px
		setPadding(new Insets(10));
		setHgap(10);
		setVgap(10);

		// TODO Make cleaner (use inner class) and add support
		// for a separate pane for statistics display
		// Labels for the statistics
		Label lblOutstanding = new Label("Outstanding Tasks");
		Label lblOverdue = new Label("Overdue Tasks");
		Label lblDist = new Label("Task Distribution");
		Label lblComp = new Label("Completed On Time");

		// Position the four labels at the bottom center
		setHalignment(lblOutstanding, HPos.CENTER);
		setValignment(lblOutstanding, VPos.BOTTOM);
		setHalignment(lblOverdue, HPos.CENTER);
		setValignment(lblOverdue, VPos.BOTTOM);
		setHalignment(lblDist, HPos.CENTER);
		setValignment(lblDist, VPos.BOTTOM);
		setHalignment(lblComp, HPos.CENTER);
		setValignment(lblComp, VPos.BOTTOM);

		// Add the four labels to the first row, one in each column
		add(lblOutstanding, 0, 0);
		add(lblOverdue, 1, 0);
		add(lblDist, 2, 0);
		add(lblComp, 3, 0);

		// Create the task table for an overview of tasks
		TableView<Task> table = new TableView<>();

		// Create four columns: id, task, due date, and member
		TableColumn<Task, Integer> idCol = new TableColumn<>("ID");
		TableColumn<Task, String> taskCol = new TableColumn<>("Task");
		TableColumn<Task, Date> dueDateCol = new TableColumn<>("Due date");
		TableColumn<Task, String> memberCol = new TableColumn<>("Member");

		// Set each of the cell value factories (which fields they
		// correspond to in the Task class)
		idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
		taskCol.setCellValueFactory(new PropertyValueFactory<>("title"));
		dueDateCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
		memberCol.setCellValueFactory(new PropertyValueFactory<>("member"));

		// Add the four columns to the table
		table.getColumns().add(idCol);
		table.getColumns().add(taskCol);
		table.getColumns().add(dueDateCol);
		table.getColumns().add(memberCol);

		// Remove the focus from the table - doesn't look nice
		table.setFocusTraversable(false);

		// The list of tasks in the overview - currently static
		ObservableList<Task> tasks = FXCollections.observableArrayList();

		// Test task - remove later and add functionality to get tasks
		// from the DB
		tasks.add(new Task(1, "Continue work on TaskerCLI", "Darren",
				new GregorianCalendar(2015, Calendar.NOVEMBER, 13).getTime(),
				new GregorianCalendar(2015, Calendar.DECEMBER, 10).getTime()));

		// Set the table items as the list we just created
		table.setItems(tasks);

		// Add the table to the second row (row 1), in the first column (col 0)
		// and allow it to span all four columns and 1 row
		add(table, 0, 1, 4, 1);

		// Column 0 - for outstanding tasks (25% width)
		ColumnConstraints cc0 = new ColumnConstraints();
		cc0.setPercentWidth(25);

		// Column 1 - for overdue tasks (25% width)
		ColumnConstraints cc1 = new ColumnConstraints();
		cc1.setPercentWidth(25);

		// Column 2 - for task distribution (25% width)
		ColumnConstraints cc2 = new ColumnConstraints();
		cc2.setPercentWidth(25);

		// Column 3 - for completed on time (25% width)
		ColumnConstraints cc3 = new ColumnConstraints();
		cc3.setPercentWidth(25);

		// Change the column sizes
		getColumnConstraints().addAll(cc0, cc1, cc2, cc3);

		// Row 0 - The four statistic panels (fixed width)
		RowConstraints rw0 = new RowConstraints();
		rw0.setMinHeight(STATISTICS_HEIGHT);
		rw0.setMaxHeight(STATISTICS_HEIGHT);

		// Row 1 - tasks overview table (fill remaining height)
		RowConstraints rw1 = new RowConstraints();
		rw1.setVgrow(Priority.ALWAYS);

		// Change the row sizes
		getRowConstraints().addAll(rw0, rw1);
	}
}