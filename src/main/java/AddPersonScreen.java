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

public class AddPersonScreen {
    private final Stage stage;
    private final SignInApp app;

    public AddPersonScreen(Stage stage, SignInApp app) {
        this.stage = stage;
        this.app   = app;
    }

    public void show() {
        Label backLink = new Label("← back to dashboard");
        backLink.setStyle("-fx-font-size: 13px; -fx-text-fill: #29ABE2; -fx-cursor: hand;");
        backLink.setOnMouseClicked(e -> new AdminDashboardScreen(stage, app).show());
        Label title = new Label("Add Person");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");
        TextField idField = styledField("Student ID (numbers only)");
        TextField nameField = styledField("Full name");
        TextField contactField = styledField("Email or phone number");
        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");
        passField.setStyle(fieldStyle());
        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.getItems().addAll("STUDENT", "OFFICER", "ADMIN");
        statusBox.setValue("STUDENT");
        statusBox.setStyle("-fx-font-size: 13px;");
        statusBox.setMaxWidth(Double.MAX_VALUE);
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        errorLabel.setVisible(false);
        errorLabel.setWrapText(true);
        Button confirmBtn = new Button("Confirm And Add Student");
        confirmBtn.setStyle("-fx-background-color: #29ABE2; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 12 20;");
        confirmBtn.setMaxWidth(Double.MAX_VALUE);
        confirmBtn.setOnAction(event -> {
            errorLabel.setVisible(false);
            String idText = idField.getText().trim();
            String name = nameField.getText().trim();
            String contact = contactField.getText().trim();
            String password = passField.getText();
            String status = statusBox.getValue();
            if (idText.isEmpty() || name.isEmpty() || contact.isEmpty() || password.isEmpty()) {
                errorLabel.setText("All fields are required.");
                errorLabel.setVisible(true);
                return;
            }
            int studentId;
            try {
                studentId = Integer.parseInt(idText);
            } catch (NumberFormatException exception) {
                errorLabel.setText("Student ID must be a number.");
                errorLabel.setVisible(true);
                return;
            }
            try {
                if (app.getStudentDatabase().checkStudentExists(studentId)) {
                    errorLabel.setText("A student with that ID already exceptionists.");
                    errorLabel.setVisible(true);
                    return;
                }
                app.getStudentDatabase().addStudent(
                        new Student(name, contact, studentId, password, Status.valueOf(status), false));
                showSuccess(name, studentId);
            } catch (Exception exception) {
                errorLabel.setText("Failed to add student: " + exception.getMessage());
                errorLabel.setVisible(true);
            }
        });
        VBox card = new VBox(12, new Label("ID"),       idField, new Label("Full Name"), nameField, new Label("Contact"),  contactField, new Label("Password"), passField, new Label("Role"), statusBox, errorLabel, confirmBtn);
        card.setStyle(cardStyle());
        card.setMaxWidth(420);
        VBox root = new VBox(16, backLink, title, card);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #f4f4f4;");
        root.setAlignment(Pos.TOP_LEFT);
        stage.setScene(new Scene(root, 560, 560));
        stage.setTitle("Add Person");
        stage.show();
    }

    private void showSuccess(String name, int id) {
        Circle circle = new Circle(50, Color.web("#4CAF50"));
        Label check = new Label("✅");
        check.setStyle("-fx-font-size: 48px; -fx-text-fill: white; -fx-font-weight: bold;");
        StackPane icon = new StackPane(circle, check);
        Label message = new Label(name + " Successfully Added");
        message.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        Label idLabel = new Label("Id: " + id);
        idLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #555555;");
        Label ret  = new Label("returning to home...");
        ret.setStyle("-fx-font-size: 12px; -fx-text-fill: #999999;");
        VBox card = new VBox(14, icon, message, idLabel, ret);
        card.setAlignment(Pos.CENTER);
        card.setStyle(cardStyle());
        card.setMaxWidth(380);
        VBox root = new VBox(card);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #f4f4f4;");
        stage.setScene(new Scene(root, 560, 400));
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(e -> new AdminDashboardScreen(stage, app).show());
        pause.play();
    }

    private TextField styledField(String prompt) {
        TextField f = new TextField();
        f.setPromptText(prompt);
        f.setStyle(fieldStyle());
        return f;
    }

    private String fieldStyle() {
        return "-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc; -fx-border-width: 1; -fx-padding: 8 12; -fx-font-size: 13px;";
    }

    private String cardStyle() {
        return "-fx-background-color: white; -fx-background-radius: 12; -fx-border-radius: 12; -fx-border-color: #dddddd; -fx-border-width: 1; -fx-padding: 30;";
    }
}