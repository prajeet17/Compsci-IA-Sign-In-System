import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * this is the initial login screen that people see when they first enter the product
 */
public class InitialLoginScreen {
    private final Stage stage;
    private final SignInApp app;

    /**
     * constructor to create new initial login screen
     * @param stage stage where it will be displayed
     * @param app main application
     */
    public InitialLoginScreen(Stage stage, SignInApp app) {
        this.stage = stage;
        this.app = app;
    }

    /**
     * displays the initial login screen
     */
    public void show() {
        Label title = new Label("Login");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");
        Label codeLabel = new Label("Access Code:");
        codeLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        PasswordField codeField = new PasswordField();
        codeField.setPromptText("Enter your ID or password");
        codeField.setStyle("-fx-background-radius: 20; -fx-border-radius: 20; -fx-padding: 8 14; -fx-font-size: 14px;");
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        errorLabel.setVisible(false);
        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #29ABE2; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 10 40;");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setOnAction(event -> handle(codeField, errorLabel));
        codeField.setOnAction(event -> handle(codeField, errorLabel));
        VBox card = new VBox(12, codeLabel, codeField, errorLabel, loginButton);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-radius: 12; -fx-border-color: #dddddd; -fx-border-width: 1; -fx-padding: 30;");
        card.setMaxWidth(360);
        VBox root = new VBox(20, title, card);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #f4f4f4;");
        stage.setScene(new Scene(root, 500, 340));
        stage.setTitle("Club Sign-In");
        stage.setResizable(false);
        stage.show();
    }

    /**
     * handles login and logout attempts
     * @param codeField the user's password
     * @param errorLabel to display any errors
     */
    private void handle(PasswordField codeField, Label errorLabel) {
        errorLabel.setVisible(false);
        String input = codeField.getText().trim();
        if (input.isEmpty()) {
            errorLabel.setText("Please enter your ID or password.");
            errorLabel.setVisible(true);
            return;
        }
        try {
            Authentication.Result result = app.resolve(input);
            if (result.action == Authentication.Result.Action.SIGN_IN_OUT) {
                app.setCurrentStudent(result.student);
                SignInStatus signInResult = app.signIn(result.student.getId());
                app.showLoginSuccessScreen(signInResult);
            }
            else if (result.action == Authentication.Result.Action.OFFICER_DASHBOARD) {
                app.setCurrentStudent(result.student);
                app.showOfficerDashboardScreen();
            }
            else if (result.action == Authentication.Result.Action.ADMIN_DASHBOARD) {
                app.setCurrentStudent(result.student);
                app.showAdminDashboardScreen();
            }
            else if (result.action == Authentication.Result.Action.WRONG) {
                errorLabel.setText("Invalid ID or password.");
                errorLabel.setVisible(true);
                codeField.clear();
            }
        } catch (Exception ex) {
            errorLabel.setText("Something went wrong: " + ex.getMessage());
            errorLabel.setVisible(true);
        }
    }
}