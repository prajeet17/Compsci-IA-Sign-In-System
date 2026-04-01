import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.SQLException;
import java.util.List;

public class RemovePersonScreen {
    private final Stage stage;
    private final SignInApp app;

    public RemovePersonScreen(Stage stage, SignInApp app) {
        this.stage = stage;
        this.app   = app;
    }

    public void show() {
        Label back = new Label("back to dashboard");
        back.setStyle("-fx-font-size: 13px; -fx-text-fill: #29ABE2; -fx-cursor: hand;");
        back.setOnMouseClicked(event -> new AdminDashboardScreen(stage, app).show());
        Label title = new Label("Remove Person");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");
        TextField searchField = new TextField();
        searchField.setPromptText("Search by name...");
        searchField.setStyle("-fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #333333; -fx-border-width: 1.5; -fx-padding: 8 14; -fx-font-size: 13px;");
        searchField.setMaxWidth(340);
        VBox listBox = new VBox(0);
        listBox.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-radius: 12; -fx-border-color: #dddddd; -fx-border-width: 1; -fx-padding: 10;");
        listBox.setMaxWidth(560);
        loadStudentList(listBox, "");
        searchField.textProperty().addListener((obs, o, n) -> loadStudentList(listBox, n.trim()));
        VBox card = new VBox(14, searchField, listBox);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-radius: 12; -fx-border-color: #dddddd; -fx-border-width: 1; -fx-padding: 24;");
        card.setMaxWidth(560);
        VBox root = new VBox(16, back, title, card);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #f4f4f4;");
        stage.setScene(new Scene(root, 660, 500));
        stage.setTitle("Remove Person");
        stage.show();
    }

    private void loadStudentList(VBox listBox, String search) {
        listBox.getChildren().clear();
        try {
            List<Student> students = app.getStudentDatabase().getAllStudents();
            for (Student s : students) {
                if (!search.isEmpty() && !s.getName().toLowerCase().contains(search.toLowerCase())) {
                    continue;
                }

                Label nameLbl = new Label(s.getName());
                nameLbl.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
                Label idLbl = new Label(String.valueOf(s.getId()));
                idLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666;");
                Label contactLbl = new Label(s.getContact());
                contactLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666;");
                VBox info = new VBox(2, nameLbl, idLbl, contactLbl);
                HBox.setHgrow(info, Priority.ALWAYS);
                Button deleteBtn = new Button("🗑️");
                deleteBtn.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-background-radius: 50; -fx-font-size: 14px; -fx-min-width: 36px; -fx-min-height: 36px; -fx-cursor: hand;");
                deleteBtn.setOnAction(event -> showConfirmation(s));
                HBox row = new HBox(12, info, deleteBtn);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setPadding(new Insets(10, 6, 10, 6));
                listBox.getChildren().addAll(row, new Separator());
            }
            if (listBox.getChildren().isEmpty()) {
                listBox.getChildren().add(new Label("No students found."));
            }
        } catch (SQLException exception) {
            listBox.getChildren().add(new Label("Error loading students."));
        }
    }

    private void showConfirmation(Student student) {
        Polygon triangle = new Polygon(40.0, 0.0, 80.0, 70.0, 0.0, 70.0);
        triangle.setFill(Color.TRANSPARENT);
        triangle.setStroke(Color.web("#E74C3C"));
        triangle.setStrokeWidth(4);
        Label exclamation = new Label("!");
        exclamation.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #E74C3C;");
        StackPane warningIcon = new StackPane(triangle, exclamation);
        StackPane.setAlignment(exclamation, Pos.BOTTOM_CENTER);
        Label nameLabel = new Label(student.getName());
        nameLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #555555;");
        Label idLabel = new Label(String.valueOf(student.getId()));
        idLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #555555;");
        Label contactLabel = new Label(student.getContact());
        contactLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #555555;");
        Button confirmButton = new Button("Confirm And Remove");
        confirmButton.setStyle("-fx-background-color: #29ABE2; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 12 20;");
        confirmButton.setMaxWidth(Double.MAX_VALUE);
        Label permanentLabel = new Label("This action is permanent");
        permanentLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #999999;");
        confirmButton.setOnAction(event -> {
            try {
                app.getStudentDatabase().removeStudent(student.getId());
                showSuccess(student.getName());
            } catch (SQLException ex) {
                showError("Failed to remove: " + ex.getMessage());
            }
        });
        VBox card = new VBox(12, warningIcon, nameLabel, idLabel, contactLabel, confirmButton, permanentLabel);
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-radius: 12; -fx-border-color: #dddddd; -fx-border-width: 1; -fx-padding: 30;");
        card.setMaxWidth(360);
        VBox root = new VBox(card);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #f4f4f4;");
        stage.setScene(new Scene(root, 560, 460));
        stage.setTitle("Confirm Removal");
        stage.show();
    }


    private void showSuccess(String name) {
        Circle circle = new Circle(50, Color.web("#4CAF50"));
        Label check = new Label("✅");
        check.setStyle("-fx-font-size: 48px; -fx-text-fill: white; -fx-font-weight: bold;");
        StackPane icon = new StackPane(circle, check);
        Label messageLabel = new Label(name + " Successfully Removed");
        messageLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        Label returnLabel = new Label("returning to home...");
        returnLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #999999;");
        VBox card = new VBox(14, icon, messageLabel, returnLabel);
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-radius: 12; -fx-border-color: #dddddd; -fx-border-width: 1; -fx-padding: 40;");
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


    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }
}