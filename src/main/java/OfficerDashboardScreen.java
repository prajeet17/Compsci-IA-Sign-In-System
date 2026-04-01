import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class OfficerDashboardScreen {
    private final Stage stage;
    private final SignInApp app;

    public OfficerDashboardScreen(Stage stage, SignInApp app) {
        this.stage = stage;
        this.app = app;
    }

    public void show() {
        Label title = new Label("Officer Dashboard");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");
        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-background-color: #29ABE2; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 8 20;");
        logoutButton.setOnAction(event -> new InitialLoginScreen(stage, app).show());
        BorderPane header = new BorderPane();
        header.setLeft(title);
        header.setRight(logoutButton);
        BorderPane.setAlignment(title, Pos.CENTER_LEFT);
        BorderPane.setAlignment(logoutButton, Pos.CENTER_RIGHT);
        HBox chartsIcon = makeIcon("📊");
        Label chartsTitle = new Label("Charts");
        chartsTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        Label chartsSubtitle = new Label("View charts and stats");
        chartsSubtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #666666;");
        VBox chartsText = new VBox(4, chartsTitle, chartsSubtitle);
        HBox chartsTile = new HBox(16, chartsIcon, chartsText);
        chartsTile.setAlignment(Pos.CENTER_LEFT);
        styleTile(chartsTile);
        chartsTile.setOnMouseClicked(event -> new ChartsScreen(stage, app).show());
        HBox dbIcon = makeIcon("📀");
        Label dbTitle = new Label("Database");
        dbTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        Label dbSubtitle = new Label("View and export stats");
        dbSubtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #666666;");
        VBox dbText = new VBox(4, dbTitle, dbSubtitle);
        HBox dbTile = new HBox(16, dbIcon, dbText);
        dbTile.setAlignment(Pos.CENTER_LEFT);
        styleTile(dbTile);
        dbTile.setOnMouseClicked(event -> new StudentRecordsScreen(stage, app, false).show());
        VBox root = new VBox(24, header, chartsTile, dbTile);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #f4f4f4;");
        stage.setScene(new Scene(root, 640, 420));
        stage.setTitle("Officer Dashboard");
        stage.setResizable(false);
        stage.show();
    }

    private HBox makeIcon(String symbol) {
        Label icon = new Label(symbol);
        icon.setStyle("-fx-font-size: 28px;");
        HBox box = new HBox(icon);
        box.setAlignment(Pos.CENTER);
        box.setMinSize(48, 48);
        return box;
    }

    private void styleTile(HBox tile) {
        tile.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-radius: 12; -fx-border-color: #dddddd; -fx-border-width: 1; -fx-padding: 20; -fx-cursor: hand;");
        tile.setOnMouseEntered(event -> tile.setStyle("-fx-background-color: #f0f9ff; -fx-background-radius: 12; -fx-border-radius: 12; -fx-border-color: #29ABE2; -fx-border-width: 1; -fx-padding: 20; -fx-cursor: hand;"));
        tile.setOnMouseExited(event -> tile.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-radius: 12; -fx-border-color: #dddddd; -fx-border-width: 1; -fx-padding: 20; -fx-cursor: hand;"));
    }
}