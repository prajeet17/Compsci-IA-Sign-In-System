import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class AdminDashboardScreen {
    private final Stage stage;
    private final SignInApp app;

    public AdminDashboardScreen(Stage stage, SignInApp app) {
        this.stage = stage;
        this.app = app;
    }

    public void show() {
        Label title = new Label("Admin Dashboard");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");
        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-background-color: #29ABE2; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 8 20;");
        logoutButton.setOnAction(event -> new InitialLoginScreen(stage, app).show());
        BorderPane header = new BorderPane();
        header.setLeft(title);
        header.setRight(logoutButton);
        BorderPane.setAlignment(title, Pos.CENTER_LEFT);
        BorderPane.setAlignment(logoutButton, Pos.CENTER_RIGHT);
        HBox row1 = new HBox(16, makeTile("📊", "Charts", "View charts and stats", event-> new ChartsScreen(stage, app).show()), makeTile("📀", "Database","View and edit records", event -> new StudentRecordsScreen(stage, app, true).show()));
        HBox row2 = new HBox(16, makeTile("+", "Add Person", "Add a new student", event -> new AddPersonScreen(stage, app).show()), makeTile("X", "Remove Person", "Remove a student", event -> new RemovePersonScreen(stage, app).show()));
        HBox row3 = new HBox(16, makeTile("🗝️", "Change Key", "Update officer password", event -> new ChangeKeyScreen(stage, app).show()));
        row1.setAlignment(Pos.CENTER_LEFT);
        row2.setAlignment(Pos.CENTER_LEFT);
        row3.setAlignment(Pos.CENTER_LEFT);
        VBox root = new VBox(20, header, row1, row2, row3);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #f4f4f4;");
        stage.setScene(new Scene(root, 640, 520));
        stage.setTitle("Admin Dashboard");
        stage.setResizable(false);
        stage.show();
    }

    private HBox makeTile(String symbol, String titleText, String subtitleText, javafx.event.EventHandler<javafx.scene.input.MouseEvent> onClick) {
        Label icon = new Label(symbol);
        icon.setStyle("-fx-font-size: 24px;");
        HBox iconBox = new HBox(icon);
        iconBox.setAlignment(Pos.CENTER);
        iconBox.setMinSize(44, 44);
        Label tileTitle = new Label(titleText);
        tileTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        Label tileSubtitle = new Label(subtitleText);
        tileSubtitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666;");
        VBox text = new VBox(3, tileTitle, tileSubtitle);
        HBox tile = new HBox(14, iconBox, text);
        tile.setAlignment(Pos.CENTER_LEFT);
        tile.setPrefWidth(260);
        String baseStyle = "-fx-background-color: white; -fx-background-radius: 12; -fx-border-radius: 12; -fx-border-color: #dddddd; -fx-border-width: 1; -fx-padding: 18; -fx-cursor: hand;";
        String hoverStyle = "-fx-background-color: #f0f9ff; -fx-background-radius: 12; -fx-border-radius: 12; -fx-border-color: #29ABE2; -fx-border-width: 1; -fx-padding: 18; -fx-cursor: hand;";
        tile.setStyle(baseStyle);
        tile.setOnMouseEntered(event -> tile.setStyle(hoverStyle));
        tile.setOnMouseExited(event -> tile.setStyle(baseStyle));
        tile.setOnMouseClicked(onClick);
        return tile;
    }
}