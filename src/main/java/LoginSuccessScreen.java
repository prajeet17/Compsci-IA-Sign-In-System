import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * success screen to show user logged in and out successfully
 */
public class LoginSuccessScreen {
    private final Stage stage;
    private final SignInApp app;
    private final SignInStatus result;

    /**
     * the constructor to create a new login success screen
     * @param stage the stage where the screen is displayed
     * @param app the main application
     * @param result time and status
     */
    public LoginSuccessScreen(Stage stage, SignInApp app, SignInStatus result) {
        this.stage = stage;
        this.app = app;
        this.result = result;
    }

    /**
     * displays the screen
     */
    public void show() {
        Circle circle = new Circle(50, Color.web("#4CAF50"));
        Label check = new Label("✅");
        check.setStyle("-fx-font-size: 48px; -fx-text-fill: white; -fx-font-weight: bold;");
        javafx.scene.layout.StackPane icon = new javafx.scene.layout.StackPane(circle, check);
        String studentName = getStudentName();
        String actionText = buildActionText(studentName);
        Label mainLabel = new Label(actionText);
        mainLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        mainLabel.setTextAlignment(TextAlignment.CENTER);
        mainLabel.setWrapText(true);
        Label timeLabel = new Label(buildTimeText());
        timeLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #555555;");
        Label returningLabel = new Label("returning to home...");
        returningLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #999999;");
        Label autoLogoutLabel = new Label();
        if (result.type == Type.AUTO) {
            autoLogoutLabel.setText("Note: you were automatically signed out from your last session.");
            autoLogoutLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #E67E22;");
            autoLogoutLabel.setWrapText(true);
            autoLogoutLabel.setTextAlignment(TextAlignment.CENTER);
        }
        VBox card = new VBox(14, icon, mainLabel, timeLabel, autoLogoutLabel, returningLabel);
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-radius: 12; -fx-border-color: #dddddd; -fx-border-width: 1; -fx-padding: 40;");
        card.setMaxWidth(380);
        VBox root = new VBox(card);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #f4f4f4;");
        stage.setScene(new Scene(root, 500, 420));
        stage.setTitle("Club Sign-In");
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(event -> new InitialLoginScreen(stage, app).show());
        pause.play();
    }

    /**
     * returns the student name
     * @return student name
     */
    private String getStudentName() {
        try {
            Student student = app.getStudentDatabase().getStudent(result.activeRecord.getStudentId());
            if (student != null) {
                return student.getName();
            } else {
                return "Student";
            }
        } catch (Exception e) {
            return "Student";
        }
    }

    /**
     * creates sign in or sign out message for success screen
     * @param name name of student
     * @return the success message
     */
    private String buildActionText(String name) {
        if (result.type == Type.IN || result.type == Type.AUTO) {
            return "Welcome " + name;
        }
        else {
            return "Goodbye " + name;
        }
    }

    /**
     * creates sign in or sign out message with time for success screen
     * @return the string of sign in or sign out and the time
     */
    private String buildTimeText() {
        java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("h:mm a");
        if (result.type == Type.IN || result.type == Type.AUTO)
            return "Sign in time - " + result.activeRecord.getSignInTime().format(fmt);
        else
            return "Sign out time - " + result.activeRecord.getSignOutTime().format(fmt);
    }
}
