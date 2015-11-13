package uk.ac.aber.cs221.group15.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import uk.ac.aber.cs221.group15.TaskerCLI;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * @author Darren White
 * @version 0.0.3
 * @since 0.0.1
 */
public class Login extends Stage {

	public static final String APP_NAME = "Login";
	public static final double WIDTH = 300;
	public static final double HEIGHT = 200;

	private final SimpleStringProperty statusProp = new SimpleStringProperty();
	private boolean loggedIn;
	private String token;

	public Login(Window owner) {
		super();

		initOwner(owner);
		setResizable(false);
		setTitle(APP_NAME);

		init();
	}

	public String getToken() {
		return token;
	}

	private void init() {
		GridPane grid = new GridPane();
		Scene scene = new Scene(grid, WIDTH, HEIGHT, Color.WHITE);

		grid.setAlignment(Pos.CENTER);
		grid.setHgap(5);
		grid.setVgap(5);
		grid.setPadding(new Insets(5));

		Label lblTitle = new Label("Please sign in");
		CheckBox cbRemember = new CheckBox("Remember me");
		TextField txtEmail = new TextField();
		PasswordField pwd = new PasswordField();
		Button login = new Button("Sign in");

		grid.add(lblTitle, 0, 0);

		txtEmail.setPromptText("Email address");
		txtEmail.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.ENTER) {
				pwd.requestFocus();
			}
		});
		grid.add(txtEmail, 0, 1);

		// Use password field for password
		pwd.setPromptText("Password");
		pwd.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.ENTER) {
				login(txtEmail.getText(), pwd.getText(), cbRemember.isSelected());
			}
		});
		grid.add(pwd, 0, 2);

		grid.add(cbRemember, 0, 3);

		login.setOnAction(event -> login(txtEmail.getText(), pwd.getText(),
				cbRemember.isSelected()));
		login.setMaxWidth(WIDTH);
		grid.add(login, 0, 4);

		Label lblStatus = new Label();
		lblStatus.textProperty().bind(statusProp);
		lblStatus.setStyle("-fx-text-fill: red;");
		grid.add(lblStatus, 0, 5);

		// Set focus on the title label to begin with
		// This ensures that the prompt text for
		// Email and password is showing
		lblTitle.requestFocus();

		setScene(scene);
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}

	private void login(String email, String pwd, boolean remember) {
		try {
			// Connect to the url request a login
			URL url = new URL(String.format(TaskerCLI.URL_METHOD +
					"&email=%s&password=%s", "login", email, pwd));

			// Prepare to read the JSON output
			JSONParser parser = new JSONParser();
			// Parse it from the input stream of the url
			InputStreamReader in = new InputStreamReader(url.openStream());
			JSONObject obj = (JSONObject) parser.parse(in);

			// The status object will inform us of the login process
			String statusMsg = (String) obj.get("status");

			// If the status is error, then display it
			if (statusMsg.equals("error")) {
				JSONObject errorObj = (JSONObject) obj.get("error");
				String errMsg = (String) errorObj.get("message");
				statusProp.set(errMsg);
				// Encountered an error, don't login
				loggedIn = false;
			} else if (statusMsg.equals("success")) {
				// Login was successful
				// Get the response object
				JSONObject response = (JSONObject) obj.get("response");
				// Store the token which is used to retrieve
				// user specific data
				token = (String) response.get("key");
				// We logged in successfully
				loggedIn = true;
				// Close the window as we are done
				close();
			}
		} catch (IOException e) {
			// Manage the exceptions
			e.printStackTrace();
			statusProp.set("Cannot connect to server!");
			loggedIn = false;
		} catch (ParseException e) {
			statusProp.set("Cannot parse server data!");
			loggedIn = false;
			e.printStackTrace();
		}
	}
}