import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.List;

/**
 * this is the screen that allows the admin to change the key for officers and admins
 */
public class ChangeKeyScreen {
    private final Stage stage;
    private final SignInApp app;

    /**
     * this is the constructor that creates a new change key screen
     * @param stage the stage where the screen is displayed
     * @param app the main app
     */
    public ChangeKeyScreen(Stage stage, SignInApp app) {
        this.stage = stage;
        this.app = app;
    }

    /**
     * this displays the screen and handles events
     */
    public void show() {
        Label back = new Label("back to dashboard");
        back.setStyle("-fx-font-size: 13px; -fx-text-fill: #29ABE2; -fx-cursor: hand;");
        back.setOnMouseClicked(event -> new AdminDashboardScreen(stage, app).show());
        Label changeOfficerKey = new Label("Change Officer Key");
        changeOfficerKey.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");
        Label selectOfficer = new Label("Select officer:");
        selectOfficer.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        ComboBox<String> officerPicker = new ComboBox<>();
        fillOfficerPicker(officerPicker);
        officerPicker.setPromptText("Choose an officer...");
        officerPicker.setStyle("-fx-font-size: 13px;");
        officerPicker.setMaxWidth(Double.MAX_VALUE);
        PasswordField currentPassword = styledPasswordField("Current password");
        PasswordField newPassword = styledPasswordField("New password");
        PasswordField confirmPassword = styledPasswordField("Confirm new password");
        Label feedback = new Label();
        feedback.setStyle("-fx-font-size: 12px; -fx-text-fill: red;");
        feedback.setVisible(false);
        feedback.setWrapText(true);
        Button continueBtn = new Button("Continue");
        continueBtn.setStyle("-fx-background-color: #29ABE2; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 12 20;");
        continueBtn.setMaxWidth(Double.MAX_VALUE);
        continueBtn.setOnAction(event -> {
            feedback.setVisible(false);
            if (officerPicker.getValue() == null) {
                feedback.setText("Please select an officer.");
                feedback.setVisible(true);
                return;
            }
            String current = currentPassword.getText();
            String newPass = newPassword.getText();
            String confirm = confirmPassword.getText();
            if (current.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
                feedback.setText("All fields are required.");
                feedback.setVisible(true);
                return;
            }
            if (!newPass.equals(confirm)) {
                feedback.setText("New password and confirmation do not match.");
                feedback.setVisible(true);
                return;
            }
            int officerId = Integer.parseInt(
                    officerPicker.getValue().replaceAll(".*\\((\\d+)\\).*", "$1"));
            try {
                Authentication.Result authResult = app.resolve(current);
                if (authResult.action == Authentication.Result.Action.WRONG || authResult.student == null || authResult.student.getId() != officerId) {
                    feedback.setText("Current password is incorrect.");
                    feedback.setVisible(true);
                    return;
                }
                Student officer = app.getStudentDatabase().getStudent(officerId);
                officer.setPassword(newPass);
                app.getStudentDatabase().setPassword(officerId, officer.getPassword());
                showSuccess();
            } catch (SQLException exception) {
                feedback.setText("Failed to update key: " + exception.getMessage());
                feedback.setVisible(true);
            }
        });

        VBox card = new VBox(12, selectOfficer, officerPicker, new Label("Current"), currentPassword, new Label("New"), newPassword, new Label("Confirm New"), confirmPassword, feedback, continueBtn);
        card.setStyle(cardStyle());
        card.setMaxWidth(420);
        VBox root = new VBox(16, back, changeOfficerKey, card);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #f4f4f4;");
        stage.setScene(new Scene(root, 560, 520));
        stage.setTitle("Change Officer Key");
        stage.show();
    }

    /**
     * this fills a box with anyone who is an officer or admin
     * @param picker
     */
    private void fillOfficerPicker(ComboBox<String> picker) {
        try {
            List<Student> students = app.getStudentDatabase().getAllStudents();
            for (Student s : students) {
                if (s.getStatus() == Status.OFFICER || s.getStatus() == Status.ADMIN) {
                    picker.getItems().add(s.getName() + " (" + s.getId() + ")");
                }
            }
        } catch (SQLException e) {
            picker.getItems().add("Error loading officers");
        }
    }

    /**
     * this shows the success message after updating the key
     */
    private void showSuccess() {
        Circle circle = new Circle(50, Color.web("#4CAF50"));
        Label check = new Label("✅");
        check.setStyle("-fx-font-size: 48px; -fx-text-fill: white; -fx-font-weight: bold;");
        StackPane icon = new StackPane(circle, check);
        Label msg = new Label("Key Successfully Updated");
        msg.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        Label ret = new Label("returning to home...");
        ret.setStyle("-fx-font-size: 12px; -fx-text-fill: #999999;");
        VBox card = new VBox(14, icon, msg, ret);
        card.setAlignment(Pos.CENTER);
        card.setStyle(cardStyle());
        card.setMaxWidth(380);
        VBox root = new VBox(card);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #f4f4f4;");
        stage.setScene(new Scene(root, 560, 360));
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(event -> new AdminDashboardScreen(stage, app).show());
        pause.play();
    }

    /**
     * returns the string used for text input fields
     * @return css string for field appearance
     */
    private PasswordField styledPasswordField(String prompt) {
        PasswordField field = new PasswordField();
        field.setPromptText(prompt);
        field.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc; -fx-border-width: 1; -fx-padding: 8 12; -fx-font-size: 13px;");
        return field;
    }

    /**
     * returns string used for the ui containers
     * @return css string used for ui containers
     */
    private String cardStyle() {
        return "-fx-background-color: white; -fx-background-radius: 12; -fx-border-radius: 12; -fx-border-color: #dddddd; -fx-border-width: 1; -fx-padding: 30;";
    }
}