package org.example;

import javafx.application.Application;
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
import java.io.IOException;

import java.awt.Taskbar;
import java.awt.Toolkit;

public class SmartFarmUI extends Application {

    @Override
    public void start(Stage stage) {

        Label title = new Label("SmartFarm");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        title.setTextFill(Color.WHITE);


        Button btnAbout   = menuButton("À propos");
        Button btnCredits = menuButton("Crédits");
        Button btnQuit    = menuButton("Quitter");
        Button btnSave    = menuButton("Saves");
        btnSave.setOnAction(e ->{
                SaveView register = new SaveView();
                stage.setScene(register.getScene(stage));
        });

        btnQuit.setOnAction(e -> stage.close());

        VBox content = new VBox(20, title, btnSave, btnCredits, btnAbout, btnQuit);
        content.setAlignment(Pos.CENTER);

        VBox overlay = new VBox();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");

        StackPane root = new StackPane();
        root.setStyle("-fx-background-image: url('/bg.png'); -fx-background-size: cover;");
        root.getChildren().addAll(overlay, content);

        Scene scene = new Scene(root, 1200, 700);
        stage.setTitle("SmartFarm");
        stage.setScene(scene);
        stage.getIcons().add(new javafx.scene.image.Image("/appImage.png"));

        if (Taskbar.isTaskbarSupported()) {
            var taskbar = Taskbar.getTaskbar();
            if (taskbar.isSupported(Taskbar.Feature.ICON_IMAGE)) {
                taskbar.setIconImage(Toolkit.getDefaultToolkit().getImage(
                        getClass().getResource("/appImage.png")
                ));
            }
        }
        stage.show();
    }

    private Button menuButton(String text) {
        Button btn = new Button(text);
        btn.setPrefWidth(260);
        btn.setFont(Font.font("Arial", 14));
        btn.setStyle(
                "-fx-background-color: #3a5a30;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 6;" +
                        "-fx-padding: 10 20 10 20;" +
                        "-fx-cursor: hand;"
        );
        return btn;
    }

    public static void main(String[] args) {
        launch(args);
    }
    public static HBox getTopBar(Stage stage, String name){
        Label title = new Label(name);
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        title.setTextFill(Color.WHITE);

        Button btnBack = new Button("<- Retour");
        btnBack.setStyle(
                "-fx-background-color : #3a5a30;" + "-fx-text-fill : white;" + "-fx-background-radius : 6;"
        );

        HBox topBar = new HBox(20, btnBack, title);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color : #1e3e1a; -fx-padding : 10 20 10 20");
        btnBack.setOnAction(e -> {
            SmartFarmUI menu = new SmartFarmUI();
            try {
                menu.start(stage);
            } catch (Exception ex) {
                System.out.println("Erreur retour menu : " + ex.getMessage());
            }
        });
        return topBar;
    }
}