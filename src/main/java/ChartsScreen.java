import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * this is the screen that allows for the chart ui to be displayed
 */
public class ChartsScreen {
    private final Stage stage;
    private final SignInApp app;

    /**
     * constructor to create a new charts screen
     * @param stage the stage where the chart screen is displayed
     * @param app the main app
     */
    public ChartsScreen(Stage stage, SignInApp app) {
        this.stage = stage;
        this.app = app;
    }

    /**
     * displays the charts screen and tiles to choose between charts for one student or all students
     */
    public void show() {
        Label back = new Label("back to dashboard");
        back.setStyle("-fx-font-size: 13px; -fx-text-fill: #29ABE2; -fx-cursor: hand;");
        back.setOnMouseClicked(event -> {
            if (app.getCurrentStudent().getStatus() == Status.ADMIN) {
                new AdminDashboardScreen(stage, app).show();
            } else {
                new OfficerDashboardScreen(stage, app).show();
            }
        });
        Label title = new Label("Attendance Charts");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");
        Label individualTitle = new Label("individual Analysis");
        individualTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        Label individualSubtitle = new Label("View charts and stats for a specific person");
        individualSubtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #666666;");
        Label individualIcon = new Label("📊");
        individualIcon.setStyle("-fx-font-size: 28px;");
        VBox individualText = new VBox(4, individualTitle, individualSubtitle);
        HBox individualTile = new HBox(16, individualIcon, individualText);
        individualTile.setAlignment(Pos.CENTER_LEFT);
        styleTile(individualTile);
        individualTile.setOnMouseClicked(event -> showindividualChart());
        Label allTitle = new Label("All Members");
        allTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        Label allSubtitle = new Label("View charts and stats for all Team Members");
        allSubtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #666666;");
        Label allIcon = new Label("◑");
        allIcon.setStyle("-fx-font-size: 28px;");
        VBox allText = new VBox(4, allTitle, allSubtitle);
        HBox allTile = new HBox(16, allIcon, allText);
        allTile.setAlignment(Pos.CENTER_LEFT);
        styleTile(allTile);
        allTile.setOnMouseClicked(event -> showAllMembersChart());
        VBox root = new VBox(20, back, title, individualTile, allTile);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #f4f4f4;");
        stage.setScene(new Scene(root, 640, 420));
        stage.setTitle("Attendance Charts");
        stage.show();
    }

    /**
     * displays screen to make chart for one student
     */
    private void showindividualChart() {
        Label pickLabel = new Label("Select student:");
        pickLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        ComboBox<String> studentPicker = new ComboBox<>();
        try {
            List<Student> students = app.getStudentDatabase().getAllStudents();
            for (Student s : students) {
                studentPicker.getItems().add(s.getName() + " (" + s.getId() + ")");
            }
        } catch (SQLException exception) {
            studentPicker.getItems().add("Error loading students");
        }
        studentPicker.setPromptText("Choose a student");
        studentPicker.setStyle("-fx-font-size: 13px;");
        ToggleGroup rangeGroup = new ToggleGroup();
        ToggleButton btn7 = rangeToggleButton("Past 7 days", rangeGroup);
        ToggleButton btn30 = rangeToggleButton("Past 30 days", rangeGroup);
        ToggleButton btnAll = rangeToggleButton("All time", rangeGroup);
        btn7.setSelected(true);
        HBox rangeBar = new HBox(0, btn7, btn30, btnAll);
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Hours");
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setLegendVisible(false);
        barChart.setPrefHeight(260);
        PieChart pieChart = new PieChart();
        pieChart.setPrefHeight(260);
        pieChart.setLegendVisible(true);
        Button loadBtn = new Button("Load");
        loadBtn.setStyle(buttonStyle());
        loadBtn.setOnAction(event -> {
            String selected = studentPicker.getValue();
            if (selected == null) return;
            int id = Integer.parseInt(selected.replaceAll(".*\\((\\d+)\\).*", "$1"));
            LocalDate[] range = selectedRange(rangeGroup);
            try {
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setData(app.getCharts().getStudentDataBarChart(id, range[0], range[1]));
                barChart.getData().clear();
                barChart.getData().add(series);
                pieChart.setData(app.getCharts().getAllStudentDataPieChart(range[0], range[1]));
            } catch (SQLException exception) {
                showError("Failed to load chart: " + exception.getMessage());
            }
        });
        Label back = new Label("← back");
        back.setStyle("-fx-font-size: 13px; -fx-text-fill: #29ABE2; -fx-cursor: hand;");
        back.setOnMouseClicked(event -> show());
        HBox controls = new HBox(12, pickLabel, studentPicker, rangeBar, loadBtn);
        controls.setAlignment(Pos.CENTER_LEFT);
        HBox charts = new HBox(20, barChart, pieChart);
        HBox.setHgrow(barChart, Priority.ALWAYS);
        HBox.setHgrow(pieChart, Priority.ALWAYS);
        VBox root = new VBox(16, back, controls, charts);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #f4f4f4;");
        stage.setScene(new Scene(root, 860, 440));
        stage.setTitle("individual Analysis");
        stage.show();
    }

    /**
     * displays screen to make chart for all members
     */
    private void showAllMembersChart() {
        ToggleGroup rangeGroup = new ToggleGroup();
        ToggleButton btn7 = rangeToggleButton("Past 7 days", rangeGroup);
        ToggleButton btn30 = rangeToggleButton("Past 30 days", rangeGroup);
        ToggleButton btnAll = rangeToggleButton("All time", rangeGroup);
        btn7.setSelected(true);
        HBox rangeBar = new HBox(0, btn7, btn30, btnAll);
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Hours");
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setLegendVisible(false);
        barChart.setPrefHeight(280);
        PieChart pieChart = new PieChart();
        pieChart.setPrefHeight(280);
        rangeGroup.selectedToggleProperty().addListener((obs, o, n) -> loadAllCharts(barChart, pieChart, rangeGroup));
        loadAllCharts(barChart, pieChart, rangeGroup);
        Label back = new Label("← back");
        back.setStyle("-fx-font-size: 13px; -fx-text-fill: #29ABE2; -fx-cursor: hand;");
        back.setOnMouseClicked(event -> show());
        HBox charts = new HBox(20, barChart, pieChart);
        HBox.setHgrow(barChart, Priority.ALWAYS);
        HBox.setHgrow(pieChart, Priority.ALWAYS);
        VBox root = new VBox(16, back, rangeBar, charts);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #f4f4f4;");
        stage.setScene(new Scene(root, 860, 440));
        stage.setTitle("All Members");
        stage.show();
    }

    /**
     * loads charts for students based on date range
     * @param barChart the bar chart of that student in that date range
     * @param pieChart the pie chart of that student in that date range
     * @param rangeGroup the preset range of days
     */
    private void loadAllCharts(BarChart<String, Number> barChart, PieChart pieChart, ToggleGroup rangeGroup) {
        LocalDate[] range = selectedRange(rangeGroup);
        try {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setData(app.getCharts().getAllStudentDataBarChart(range[0], range[1]));
            barChart.getData().clear();
            barChart.getData().add(series);
            pieChart.setData(app.getCharts().getAllStudentDataPieChart(range[0], range[1]));
        } catch (SQLException exception) {
            showError("Failed to load charts: " + exception.getMessage());
        }
    }

    /**
     * figures out the date range
     * @param group the preset range of days
     * @return the start and end date
     */
    private LocalDate[] selectedRange(ToggleGroup group) {
        ToggleButton selected = (ToggleButton) group.getSelectedToggle();
        if (selected == null) return Charts.past7Days();
        if (selected.getText().equals("Past 30 days")) {
            return Charts.past30Days();
        } else if (selected.getText().equals("All time")) {
            return Charts.allTime();
        } else {
            return Charts.past7Days();
        }
    }

    /**
     * creates button for selecting a date range
     * @param text the label of the button
     * @param group the group where the button belongs
     * @return a select date range button
     */
    private ToggleButton rangeToggleButton(String text, ToggleGroup group) {
        ToggleButton btn = new ToggleButton(text);
        btn.setToggleGroup(group);
        btn.setStyle("-fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #cccccc; -fx-border-width: 1; -fx-font-size: 12px; -fx-padding: 6 14;");
        btn.selectedProperty().addListener((obs, wasSelected, isSelected) -> btn.setStyle(isSelected ? "-fx-background-color: #29ABE2; -fx-text-fill: white; -fx-background-radius: 20; -fx-border-radius: 20; -fx-font-size: 12px; -fx-padding: 6 14;" : "-fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #cccccc; -fx-border-width: 1; -fx-font-size: 12px; -fx-padding: 6 14;"));
        return btn;
    }

    /**
     * allows us to standardize style across tiles
     * @param tile the tile you want to set the style of
     */
    private void styleTile(HBox tile) {
        String base = "-fx-background-color: white; -fx-background-radius: 12; -fx-border-radius: 12; -fx-border-color: #dddddd; -fx-border-width: 1; -fx-padding: 20; -fx-cursor: hand;";
        String hover = "-fx-background-color: #f0f9ff; -fx-background-radius: 12; -fx-border-radius: 12; -fx-border-color: #29ABE2; -fx-border-width: 1; -fx-padding: 20; -fx-cursor: hand;";
        tile.setStyle(base);
        tile.setOnMouseEntered(event -> tile.setStyle(hover));
        tile.setOnMouseExited(event -> tile.setStyle(base));
    }

    /**
     * the style for buttons
     * @return css string for button style
     */
    private String buttonStyle() {
        return "-fx-background-color: #29ABE2; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 8 20;";
    }

    /**
     * error message
     * @param msg the message to display
     */
    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }
}