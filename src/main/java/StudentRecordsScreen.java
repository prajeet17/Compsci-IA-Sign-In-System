import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class StudentRecordsScreen {
    private final Stage stage;
    private final SignInApp app;
    private final boolean canEdit; // true for admin, false for officer
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a");

    public StudentRecordsScreen(Stage stage, SignInApp app, boolean canEdit) {
        this.stage = stage;
        this.app = app;
        this.canEdit = canEdit;
    }

    public void show() {
        Label back = new Label("back to dashboard");
        back.setStyle("-fx-font-size: 13px; -fx-text-fill: #29ABE2; -fx-cursor: hand;");
        back.setOnMouseClicked(event -> {
            if (canEdit) {
                new AdminDashboardScreen(stage, app).show();
            } else {
                new OfficerDashboardScreen(stage, app).show();
            }
        });
        String titleText;
        if (canEdit) {
            titleText = "Edit/View Database";
        } else {
            titleText = "Student Records";
        }
        Label title = new Label(titleText);
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");
        DatePicker startPicker = new DatePicker(LocalDate.now().minusDays(30));
        DatePicker endPicker = new DatePicker(LocalDate.now());
        startPicker.setStyle("-fx-font-size: 12px;");
        endPicker.setStyle("-fx-font-size: 12px;");
        Button filterButton = new Button("Filter Dates");
        filterButton.setStyle(outlineButtonStyle());
        TextField searchField = new TextField();
        searchField.setPromptText("Search by name...");
        searchField.setStyle("-fx-background-radius: 20; -fx-border-radius: 20; -fx-padding: 6 14; -fx-font-size: 13px;");
        searchField.setPrefWidth(220);
        TableView<AttendanceRecord> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<AttendanceRecord, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getStudentId())));
        TableColumn<AttendanceRecord, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(c -> {
            try {
                Student student = app.getStudentDatabase().getStudent(c.getValue().getStudentId());
                if (student != null) {
                    return new SimpleStringProperty(student.getName());
                }
                return new SimpleStringProperty("Unknown");
            } catch (SQLException exception) {
                return new SimpleStringProperty("Error");
            }
        });
        TableColumn<AttendanceRecord, String> contactColumn = new TableColumn<>("Contact");
        contactColumn.setCellValueFactory(c -> {
            try {
                Student student = app.getStudentDatabase().getStudent(c.getValue().getStudentId());
                if (student != null) {
                    return new SimpleStringProperty(student.getContact());
                }
                return new SimpleStringProperty("—");
            } catch (SQLException exception) {
                return new SimpleStringProperty("Error");
            }
        });
        TableColumn<AttendanceRecord, String> signInColumn = new TableColumn<>("Sign In");
        signInColumn.setCellValueFactory(c -> {
            LocalDateTime time = c.getValue().getSignInTime();
            if (time != null) {
                return new SimpleStringProperty(time.format(FMT));
            }
            return new SimpleStringProperty("—");
        });
        TableColumn<AttendanceRecord, String> signOutColumn = new TableColumn<>("Sign Out");
        signOutColumn.setCellValueFactory(c -> {
            LocalDateTime time = c.getValue().getSignOutTime();
            if (time != null) {
                return new SimpleStringProperty(time.format(FMT));
            }
            return new SimpleStringProperty("Still signed in");
        });
        table.getColumns().addAll(idColumn, nameColumn, contactColumn, signInColumn, signOutColumn);
        if (canEdit) {
            TableColumn<AttendanceRecord, Void> editColumn = new TableColumn<>("Edit");
            editColumn.setCellFactory(col -> new TableCell<>() {
                private final Button editButton = new Button("Edit sign-out");
                {
                    editButton.setStyle("-fx-background-color: #29ABE2; -fx-text-fill: white; -fx-font-size: 11px; -fx-background-radius: 8; -fx-padding: 4 10;");
                    editButton.setOnAction(e -> {
                        AttendanceRecord record = getTableView().getItems().get(getIndex());
                        showEditDialog(record, table, startPicker.getValue(), endPicker.getValue());
                    });
                }
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : editButton);
                }
            });
            table.getColumns().add(editColumn);
        }
        table.setPrefHeight(340);
        loadRecords(table, startPicker.getValue(), endPicker.getValue(), "");
        filterButton.setOnAction(e -> loadRecords(table, startPicker.getValue(), endPicker.getValue(), searchField.getText().trim()));
        searchField.textProperty().addListener((obs, o, n) -> loadRecords(table, startPicker.getValue(), endPicker.getValue(), n.trim()));
        HBox filterRow = new HBox(10, new Label("From:"), startPicker, new Label("To:"), endPicker, filterButton);
        filterRow.setAlignment(Pos.CENTER_LEFT);
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        topBar.getChildren().addAll(filterRow, spacer, searchField);
        BorderPane headerPane = new BorderPane();
        headerPane.setLeft(back);
        headerPane.setRight(title);
        VBox root = new VBox(16, headerPane, topBar, table);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #f4f4f4;");
        stage.setScene(new Scene(root, 860, 520));
        String windowTitle;
        if (canEdit) {
            windowTitle = "Edit/View Database";
        } else {
            windowTitle = "Student Records";
        }
        stage.setTitle(windowTitle);
        stage.show();
    }


    private void loadRecords(TableView<AttendanceRecord> table, LocalDate start, LocalDate end, String search) {
        table.getItems().clear();
        try {
            List<AttendanceRecord> records =
                    app.getAttendanceDatabase().getDateRangeRecords(start, end);
            for (AttendanceRecord r : records) {
                if (search.isEmpty()) {
                    table.getItems().add(r);
                } else {
                    Student s = app.getStudentDatabase().getStudent(r.getStudentId());
                    if (s != null && s.getName().toLowerCase().contains(search.toLowerCase())) {
                        table.getItems().add(r);
                    }
                }
            }
        } catch (SQLException exception) {
            showError("Failed to load records: " + exception.getMessage());
        }
    }

    private void showEditDialog(AttendanceRecord record, TableView<AttendanceRecord> table, LocalDate start, LocalDate end) {
        Dialog<LocalDateTime> dialog = new Dialog<>();
        dialog.setTitle("Edit Sign-out Time");
        dialog.setHeaderText("Update sign-out for record:\nSign-in: " + record.getSignInTime().format(FMT));
        LocalDate date;
        if (record.getSignOutTime() != null) {
            date = record.getSignOutTime().toLocalDate();
        } else {
            date = LocalDate.now();
        }
        DatePicker datePicker = new DatePicker(date);
        String timeText;
        if (record.getSignOutTime() != null) {
            timeText = record.getSignOutTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        } else {
            timeText = "17:00";
        }
        TextField timeField = new TextField(timeText);
        timeField.setPromptText("HH:mm");
        VBox content = new VBox(10, new Label("Date:"), datePicker, new Label("Time (24h):"), timeField);
        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(content);
        ButtonType saveType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);
        dialog.setResultConverter(button -> {
            if (button == saveType) {
                try {
                    String[] parts = timeField.getText().split(":");
                    return datePicker.getValue().atTime(
                            Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
                } catch (Exception exception) {
                    showError("Invalid time format. Use HH:mm");
                    return null;
                }
            }
            return null;
        });
        dialog.showAndWait().ifPresent(newTime -> {
            try {
                app.getAttendanceDatabase().editSignOutTime(
                        record.getStudentId(), newTime);
                loadRecords(table, start, end, "");
            } catch (SQLException exception) {
                showError("Failed to save: " + exception.getMessage());
            }
        });
    }

    private String outlineButtonStyle() {
        return "-fx-background-color: transparent; -fx-border-color: #cccccc; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-size: 12px; -fx-padding: 6 14;";
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }
}