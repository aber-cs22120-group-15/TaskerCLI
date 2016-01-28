package uk.ac.aber.cs221.group15.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.json.simple.parser.ParseException;
import uk.ac.aber.cs221.group15.TaskerCLI;
import uk.ac.aber.cs221.group15.service.LoginService;
import uk.ac.aber.cs221.group15.service.Service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

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
 * @version 0.0.8
 */
public class Login extends Stage {

	/**
	 * Temporary token used for offline mode
	 */
	public static final String TOKEN_OFFLINE = "offline-mode";

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
	 * The path to store user credentials
	 */
	private static final String PATH_LOGIN = TaskerCLI.getUserHomeDir() +
			File.separator + ".tasker_login";

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

		// Set label id for css
		lblTitle.setId("lbl-title");

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

		// Use this as the default button
		login.setDefaultButton(true);
		// If the login button is pressed, login
		login.setOnAction(event -> login(txtEmail.getText(), pwd.getText(),
				cbRemember.isSelected()));
		// Make the login button stretch
		login.setMaxWidth(WIDTH);

		// Set the label id for css
		lblStatus.setId("lbl-status");
		// Set the status text to the statusProp field
		// Whenever the statusProp value is changed this
		// label will also change what it displays to the
		// value of the statusProp
		lblStatus.textProperty().bind(statusProp);

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

		// The path to store the credentials
		Path p = Paths.get(PATH_LOGIN);

		// If the path exists then load then credentials
		if (Files.exists(p)) {
			// Create a reader to read the email
			try (BufferedReader br = Files.newBufferedReader(p)) {
				// Set the email text as the email in the file
				txtEmail.setText(br.readLine());
				// Remember user again
				cbRemember.setSelected(true);
				// Set the focus on the password text
				pwd.requestFocus();
			} catch (IOException e) {
				System.err.println("Unable to load previous email!");
				e.printStackTrace();
			}
		}

		// Set the width of all components to 55% of the window width
		ColumnConstraints cc0 = new ColumnConstraints();
		cc0.setPercentWidth(55);

		// Change the column size
		grid.getColumnConstraints().add(cc0);

		// Set the stylesheet for css styling
		scene.getStylesheets().add(TaskerCLI.getResource("resources/css/Login.css").toExternalForm());
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
		// The path to for the user credentials
		Path cache = Paths.get(PATH_LOGIN);

		// Check if we are offline
		if (!Service.checkConnection()) {
			// If a user has logged in before, offer offline mode
			// otherwise don't login
			if (Files.exists(cache)) {
				// Create and show the notification for offline mode
				Notifications.create()
						.message("Cannot reach server. Continue offline?")
						.title("Unable to connect")
						.yesNo(event -> {
							// If the yes button was pressed use a temporary token
							// and close this window
							Button src = (Button) event.getSource();
							if (src.getText().equals("Yes")) {
								token = TOKEN_OFFLINE;
								close();
							}

							// Also hide the notification
							src.getScene().getWindow().hide();
						}).show();
			}

			return;
		}

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
				// We logged in successfully

				// Save the credentials for next time if we need to
				if (remember) {
					// Create a writer for the path (overwrite existing)
					try (BufferedWriter bw = Files.newBufferedWriter(cache, StandardOpenOption.CREATE)) {
						// Write the email to the file
						bw.write(email);
						// Flush before close
						bw.flush();
						// Auto close
					} catch (IOException e) {
						System.err.println("Unable to save credentials!");
						e.printStackTrace();
					}
				} else {
					// We don't want to save the credentials so delete the
					// existing ones if there is any
					if (Files.exists(cache)) {
						Files.delete(cache);
					}
				}

				// Close this window
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