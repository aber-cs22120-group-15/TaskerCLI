package uk.ac.aber.cs221.group15.gui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;

/**
 * @author Darren White
 * @version 0.0.1
 * @since 0.0.1
 */
public class DashboardView extends GridPane {

	public DashboardView() {
		init();
	}

	private void init() {
		setPadding(new Insets(10));
		setHgap(10);
		setVgap(10);
		// Temporary styling
		setStyle("-fx-background-color: rgb(0, 0, 100);");

		// Temporary layout
		Label lblTitle = new Label("Dashboard");
		lblTitle.setFont(new Font(30));
		lblTitle.setStyle("-fx-text-fill: white;");

		add(lblTitle, 0, 0);
	}
}