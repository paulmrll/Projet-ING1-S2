package org.example;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class MapView {
    
    private Ground ground;

    public MapView(Ground ground){
        this.ground = ground;
    }

    public Scene getScene(Stage stage){

        //Haut de la page la barre de titre
        Label title = new Label("SmartFarm - " + ground.getOwner().getFirstname() + " " + ground.getOwner().getName());
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        title.setTextFill(Color.WHITE);

        Button btnBack = new Button("<- Retour");
        btnBack.setStyle(
                "-fx-background-color : #3a5a30;" + "-fx-text-fill : white;" + "-fx-background-radius : 6;"
        );

        HBox topBar = new HBox(20, btnBack, title);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color : #1e3e1a; -fx-padding : 10 20 10 20");

        //Centre de la page donc la zone de la carte
        Label placeholder = new Label("On met la carte Voronoï ici");
        placeholder.setFont(Font.font("Arial", 22));
        placeholder.setTextFill(Color.LIGHTGRAY);

        javafx.scene.layout.StackPane center = new javafx.scene.layout.StackPane(placeholder);
        center.setStyle("-fx-background-color : #2a2a2a");

        // le bas de la page on affiche les infos de la ferme

        Label info = new Label(
                "Surface : " + ground.getArea() + " m²  |  " +
                     "Champs : " + ground.getFields().size() + "  |  " +
                     "Citernes : " + ground.getTanks().size() + "  |  " +
                     "Arroseurs : " + ground.getSprinklers().size()
        );
        info.setTextFill(Color.WHITE);
        info.setFont(Font.font("Arial", 13));

        HBox bottomBar = new HBox(info);
        bottomBar.setAlignment(Pos.CENTER);
        bottomBar.setStyle("-fx-background-color : 1e3a1a; -fx-padding : 8 20 8 20;");

        //on assemble tout ca !!
        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(center);
        root.setBottom(bottomBar);

        // retour au menu
        btnBack.setOnAction(e -> {
            SmartFarmUI menu = new SmartFarmUI();
            try {
                menu.start(stage);
            } catch (Exception ex) {
                System.out.println("Erreur retour menu : " + ex.getMessage());
            }
        });

        return new Scene(root, 1200, 700);
    }
}
