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
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;

import java.awt.Taskbar;
import java.awt.Toolkit;

public class SmartFarmUI extends Application {

    private static Font faFont;

    // FontAwesome 4.7
    private static final String FA_FOLDER = "\uf07c"; // fa-folder-open
    private static final String FA_LEAF = "\uf06c"; // fa-leaf
    private static final String FA_INFO = "\uf05a"; // fa-info-circle
    private static final String FA_USERS = "\uf0c0"; // fa-users
    private static final String FA_POWER = "\uf011"; // fa-power-off

    @Override
    public void start(Stage stage) {
        if (faFont == null) {
            faFont = Font.loadFont(
                    getClass().getResourceAsStream("/fontawesome-webfont.ttf"), 10
            );
        }

        Font.loadFont(getClass().getResourceAsStream("/fonts/Poppins-Regular.ttf"), 13);
        Font.loadFont(getClass().getResourceAsStream("/fonts/Poppins-Medium.ttf"), 13);
        Font.loadFont(getClass().getResourceAsStream("/fonts/Poppins-SemiBold.ttf"), 13);
        Font.loadFont(getClass().getResourceAsStream("/fonts/Poppins-Bold.ttf"), 13);

        // title
        Label title = new Label("SmartFarm");
        title.setFont(Font.font("Poppins", FontWeight.BOLD, 52));
        title.setTextFill(Color.WHITE);

        // main buttons
        Button btnLoad = bigButton(FA_FOLDER, "Charger une\nsauvegarde");
        Button btnCreate = bigButton(FA_LEAF, "Créer une\nnouvelle ferme");

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
        Button btnAbout = smallButton(FA_INFO, "À propos");
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

        Scene scene = new Scene(root, 1300, 800);
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
        textLabel.setStyle(
                "-fx-text-fill: rgba(255,255,255,0.7); " +
                        "-fx-font-size: 13px;"
        );

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

    /**
     * Shared top bar used by all views.
     */
    public static HBox getTopBar(Stage stage, String title) {
        HBox topBar = new HBox();
        topBar.setStyle("-fx-background-color: linear-gradient(to right, #1a2e18, #0f1f0f); " +
                "-fx-padding: 14 24 14 24; " +
                "-fx-border-width: 0 0 2 0; " +
                "-fx-border-color: #3a7a30;");
        topBar.setAlignment(Pos.CENTER_LEFT);

        Button btnBack = new Button("Menu");
        btnBack.setStyle(
                "-fx-background-color: rgba(255,255,255,0.08); " +
                        "-fx-border-color: rgba(255,255,255,0.15); " +
                        "-fx-text-fill: #aaaaaa; -fx-border-radius: 20; " +
                        "-fx-background-radius: 20; -fx-padding: 6 16 6 16; " +
                        "-fx-font-size: 12px;"
        );
        btnBack.setOnAction(e -> {
            SmartFarmUI menu = new SmartFarmUI();
            menu.start(stage);
        });

        Region sepVertical = new Region();
        sepVertical.setPrefWidth(1);
        sepVertical.setPrefHeight(20);
        sepVertical.setMaxHeight(20);
        sepVertical.setStyle("-fx-background-color: #2e5228;");

        HBox gauche = new HBox(16);
        gauche.setAlignment(Pos.CENTER_LEFT);
        gauche.getChildren().addAll(btnBack, sepVertical);

        Label titreBar = new Label(title);
        titreBar.setStyle(
                "-fx-text-fill: white; " +
                        "-fx-font-size: 22px; " +
                        "-fx-font-weight: bold;"
        );

        Label badge = new Label("SmartFarm");
        badge.setStyle(
                "-fx-text-fill: #7aaa74; " +
                        "-fx-font-size: 11px; " +
                        "-fx-background-color: rgba(74,154,58,0.12); " +
                        "-fx-border-color: rgba(74,154,58,0.25); " +
                        "-fx-border-radius: 20; " +
                        "-fx-background-radius: 20; " +
                        "-fx-padding: 4 14 4 14;"
        );

        HBox droite = new HBox();
        droite.setAlignment(Pos.CENTER_RIGHT);
        droite.getChildren().add(badge);

        Region spacer1 = new Region();
        Region spacer2 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        topBar.getChildren().addAll(gauche, spacer1, titreBar, spacer2, droite);
        return topBar;
    }
}
