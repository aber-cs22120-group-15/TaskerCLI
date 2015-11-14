package uk.ac.aber.cs221.group15.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.json.simple.parser.ParseException;
import uk.ac.aber.cs221.group15.service.LoginService;

import java.io.IOException;

/**
 * This class displays a Login window where
 * the user can login to the main application
 * with their email and password. An option to
 * remember the user can also be selected so
 * when the next time the application starts
 * the previous email used is already entered
 * to enable a faster login process
 *
 * @author Darren White
 * @version 0.0.4
 */
public class Login extends Stage {

	/**
	 * The title of the window
	 */
	private static final String APP_NAME = "Login";

	/**
	 * The main window width
	 */
	private static final double WIDTH = 300;

	/**
	 * The main window height
	 */
	private static final double HEIGHT = 200;

	/**
	 * The service used to submit login requests
	 */
	private static final LoginService service = new LoginService();

	/**
	 * Used to store the status
	 */
	private final SimpleStringProperty statusProp = new SimpleStringProperty();

	/**
	 * Store the user token/key for further database requests
	 */
	private String token;

	/**
	 * Creates a new login window
	 *
	 * @param owner The main application
	 */
	public Login(Window owner) {
		// Set the owner as the main window
		initOwner(owner);
		// No need for the login to be resizable
		setResizable(false);
		// Set the title of the window
		setTitle(APP_NAME);

		// Initialize the components
		init();
	}

	/**
	 * Gets the user login token/key to be used
	 * to get information from the database
	 *
	 * @return The user token/key
	 */
	public String getToken() {
		return token;
	}

	/**
	 * Initializes this windows components
	 */
	private void init() {
		// Create the grid that we are going to
		// put all the component onto
		GridPane grid = new GridPane();
		// Create the scene
		Scene scene = new Scene(grid, WIDTH, HEIGHT);

		// Center all components
		grid.setAlignment(Pos.CENTER);
		// Set the padding and gaps to 5px
		grid.setHgap(5);
		grid.setVgap(5);
		grid.setPadding(new Insets(5));

		// Create all the components: title label, email textfield,
		// password field, checkbox to remember user, login button
		// and the status label
		Label lblTitle = new Label("Please sign in");
		TextField txtEmail = new TextField();
		PasswordField pwd = new PasswordField();
		CheckBox cbRemember = new CheckBox("Remember me");
		Button login = new Button("Sign in");
		Label lblStatus = new Label();

		// The title needs to stand out
		lblTitle.setFont(new Font(20));

		// Set the hint text (displayed when no text is input and no focus)
		txtEmail.setPromptText("Email address");
		// If the enter key is pressed proceed to input password
		txtEmail.setOnKeyPressed(e -> {
			// If the key pressed is enter
			if (e.getCode() == KeyCode.ENTER) {
				// Set the focus to the password field
				pwd.requestFocus();
			}
		});

		// Set the hint text
		pwd.setPromptText("Password");
		// If the enter key is pressed; login
		pwd.setOnKeyPressed(e -> {
			// If the enter key was pressed
			if (e.getCode() == KeyCode.ENTER) {
				// Request login with the user details
				login(txtEmail.getText(), pwd.getText(), cbRemember.isSelected());
			}
		});

		// If the login button is pressed, login
		login.setOnAction(event -> login(txtEmail.getText(), pwd.getText(),
				cbRemember.isSelected()));
		// Make the login button stretch
		login.setMaxWidth(WIDTH);

		// Set the status text to the statusProp field
		// Whenever the statusProp value is changed this
		// label will also change what it displays to the
		// value of the statusProp
		lblStatus.textProperty().bind(statusProp);
		// The status displays any errors, so make it red
		lblStatus.setStyle("-fx-text-fill: red;");

		// Add each component to a new row (all in column 0)
		grid.add(lblTitle, 0, 0);
		grid.add(txtEmail, 0, 1);
		grid.add(pwd, 0, 2);
		grid.add(cbRemember, 0, 3);
		grid.add(login, 0, 4);
		grid.add(lblStatus, 0, 5);

		// Set focus on the title label to begin with
		// This ensures that the prompt text for
		// Email and password is showing
		lblTitle.requestFocus();

		// Set the width of all components to 55% of the window width
		ColumnConstraints cc0 = new ColumnConstraints();
		cc0.setPercentWidth(55);

		// Change the column size
		grid.getColumnConstraints().add(cc0);

		// Sets the scene
		setScene(scene);
	}

	/**
	 * Tries to login with the user details
	 *
	 * @param email    The email specified in the ui
	 * @param pwd      The password specified in the ui
	 * @param remember If the user is to be remembered for next time
	 */
	private void login(String email, String pwd, boolean remember) {
		// TODO Implement the remember me feature - store the email for next time

		try {
			// Submit the email and password
			// and store the token is successful (null if error)
			token = service.login(email, pwd);

			// We encountered an error
			if (token == null) {
				// We didn't login as there was an error
				// Display the message
				statusProp.set(service.getErrorMessage());
			} else {
				// We logged in, so close this window
				close();
			}
		} catch (IOException e) {
			// Manage the exceptions
			e.printStackTrace();
			statusProp.set("Cannot connect to server!");
			token = null;
		} catch (ParseException e) {
			e.printStackTrace();
			statusProp.set("Cannot parse server data!");
			token = null;
		} catch (Exception e) {
			e.printStackTrace();
			statusProp.set("An internal error occured!");
			token = null;
		}
	}
}