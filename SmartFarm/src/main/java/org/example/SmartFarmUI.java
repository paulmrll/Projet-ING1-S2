package org.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.awt.Taskbar;
import java.awt.Toolkit;

public class SmartFarmUI extends Application {

    private static Font faFont;

    // FontAwesome 4.7
    private static final String FA_FOLDER = "\uf07c"; // fa-folder-open
    private static final String FA_LEAF   = "\uf06c"; // fa-leaf
    private static final String FA_INFO   = "\uf05a"; // fa-info-circle
    private static final String FA_USERS  = "\uf0c0"; // fa-users
    private static final String FA_POWER  = "\uf011"; // fa-power-off

    @Override
    public void start(Stage stage) {
        if (faFont == null) {
            faFont = Font.loadFont(
                getClass().getResourceAsStream("/fontawesome-webfont.ttf"), 10
            );
        }

        // title
        Label title = new Label("SmartFarm");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 52));
        title.setTextFill(Color.WHITE);

        // main buttons
        Button btnLoad = bigButton(FA_FOLDER,"Charger une\nsauvegarde");
        Button btnCreate = bigButton(FA_LEAF,"Créer une\nnouvelle ferme");

        btnLoad.setOnAction(e -> {
            SaveView saveView = new SaveView();
            stage.setScene(saveView.getScene(stage));
        });
        btnCreate.setOnAction(e -> {
            NewGroundFormView newGroundFormView = new NewGroundFormView();
            stage.setScene(newGroundFormView.newGround(stage));
        });

        HBox mainActions = new HBox(20, btnLoad, btnCreate);
        mainActions.setAlignment(Pos.CENTER);

        // buttons
        Button btnCredits = smallButton(FA_USERS, "Crédits");
        Button btnAbout = smallButton(FA_INFO,  "À propos");
        Button btnQuit = smallButton(FA_POWER, "Quitter");
        btnQuit.setOnAction(e -> stage.close());

        HBox secondaryActions = new HBox(10, btnCredits, btnAbout, btnQuit);
        secondaryActions.setAlignment(Pos.CENTER);

        // assembly
        VBox content = new VBox(30, title, mainActions, secondaryActions);
        content.setAlignment(Pos.CENTER);

        VBox overlay = new VBox();
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.62);");

        StackPane root = new StackPane();
        root.setStyle("-fx-background-image: url('/bg.png'); -fx-background-size: cover;");
        root.getChildren().addAll(overlay, content);

        Scene scene = new Scene(root, 1200, 700);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        stage.setTitle("SmartFarm");
        stage.setScene(scene);
        stage.getIcons().add(new javafx.scene.image.Image("/appImage.png"));

        if (Taskbar.isTaskbarSupported()) {
            var taskbar = Taskbar.getTaskbar();
            if (taskbar.isSupported(Taskbar.Feature.ICON_IMAGE)) {
                taskbar.setIconImage(Toolkit.getDefaultToolkit().getImage(
                        getClass().getResource("/appImage.png")));
            }
        }
        stage.show();
    }

    private Button bigButton(String icon, String text) {
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font("FontAwesome", 30));
        iconLabel.setTextFill(Color.color(1, 1, 1, 0.9));

        Label textLabel = new Label(text);
        textLabel.setStyle(
            "-fx-text-fill: white;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-alignment: center;"
        );
        textLabel.setWrapText(true);

        VBox box = new VBox(10, iconLabel, textLabel);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(20));

        Button btn = new Button();
        btn.setGraphic(box);
        btn.setPrefSize(170, 140);
        applyBigStyle(btn, false);
        btn.setOnMouseEntered(e -> applyBigStyle(btn, true));
        btn.setOnMouseExited(e -> applyBigStyle(btn, false));
        return btn;
    }

    private void applyBigStyle(Button btn, boolean hover) {
        btn.setStyle(
            "-fx-background-color: " + (hover ? "rgba(72,110,58,0.92)" : "rgba(50,80,40,0.82)") + ";" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: " + (hover ? "rgba(255,255,255,0.35)" : "rgba(255,255,255,0.18)") + ";" +
            "-fx-border-radius: 10;" +
            "-fx-border-width: 1;" +
            "-fx-cursor: hand;"
        );
    }

    private Button smallButton(String icon, String text) {
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font("FontAwesome", 14));
        iconLabel.setTextFill(Color.color(1, 1, 1, 0.7));

        Label textLabel = new Label(text);
        textLabel.setStyle("-fx-text-fill: rgba(255,255,255,0.7); -fx-font-size: 13px;");

        HBox box = new HBox(7, iconLabel, textLabel);
        box.setAlignment(Pos.CENTER);

        Button btn = new Button();
        btn.setGraphic(box);
        btn.setPrefSize(120, 38);
        applySmallStyle(btn, false);
        btn.setOnMouseEntered(e -> applySmallStyle(btn, true));
        btn.setOnMouseExited(e -> applySmallStyle(btn, false));
        return btn;
    }

    private void applySmallStyle(Button btn, boolean hover) {
        btn.setStyle(
            "-fx-background-color: " + (hover ? "rgba(255,255,255,0.08)" : "transparent") + ";" +
            "-fx-border-color: " + (hover ? "rgba(255,255,255,0.40)" : "rgba(255,255,255,0.22)") + ";" +
            "-fx-border-radius: 6;" +
            "-fx-background-radius: 6;" +
            "-fx-cursor: hand;"
        );
    }

    public static void main(String[] args) {
        launch(args);
    }

    /** Shared top bar used by all views. */
    public static HBox getTopBar(Stage stage, String name) {
        Label title = new Label(name);
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        title.setTextFill(Color.WHITE);

        Button btnBack = new Button("<- Go to the MENU");
        btnBack.setStyle(
                "-fx-background-color: #3a5a30;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 6;" +
                "-fx-cursor: hand;"
        );
        btnBack.setOnAction(e -> {
            SmartFarmUI menu = new SmartFarmUI();
            try {
                menu.start(stage);
            } catch (Exception ex) {
                System.err.println("Erreur retour menu : " + ex.getMessage());
            }
        });

        HBox topBar = new HBox(20, btnBack, title);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: #1e3e1a; -fx-padding: 10 20 10 20;");
        return topBar;
    }
}
