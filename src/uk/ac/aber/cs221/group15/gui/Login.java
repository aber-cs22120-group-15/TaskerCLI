package uk.ac.aber.cs221.group15.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * @author Darren White
 * @version 1.0
 * @since 1.0
 */
public class Login extends Stage {

	public static final String APP_NAME = "Login";
	public static final double WIDTH = 300;
	public static final double HEIGHT = 200;

	private boolean loggedIn;

	public Login(Window owner) {
		super();

		initOwner(owner);
		setResizable(false);
		setTitle(APP_NAME);

		init();
	}

	private void init() {
		GridPane grid = new GridPane();
		Scene scene = new Scene(grid, WIDTH, HEIGHT, Color.WHITE);

		grid.setAlignment(Pos.CENTER);
		grid.setHgap(5);
		grid.setVgap(5);
		grid.setPadding(new Insets(5));

		Label lblTitle = new Label("Please sign in");
		grid.add(lblTitle, 0, 0);

		TextField txtEmail = new TextField();
		txtEmail.setPromptText("Email address");
		grid.add(txtEmail, 0, 1);

		PasswordField pwd = new PasswordField();
		pwd.setPromptText("Password");
		grid.add(pwd, 0, 2);

		CheckBox cbRemember = new CheckBox("Remember me");
		grid.add(cbRemember, 0, 3);

		Button login = new Button("Sign in");
		login.setOnAction(event -> login(cbRemember.isSelected()));
		login.setMaxWidth(WIDTH);
		grid.add(login, 0, 4);

		lblTitle.requestFocus();
		setScene(scene);
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}

	private void login(boolean remember) {
		loggedIn = false;

		close();
	}
}