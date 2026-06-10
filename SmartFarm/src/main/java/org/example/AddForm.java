package org.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AddForm {
    public Scene getFieldForm(Stage stage, Ground ground){
        Label titleLabel = new Label("Ajouter un nouveau champ");

        TextField xStartInput = new TextField();
        xStartInput.setPromptText("Coordonnée X de début");
        TextField xStopInput = new TextField();
        xStopInput.setPromptText("Coordonnée X de fin");

        TextField yStartInput = new TextField();
        yStartInput.setPromptText("Coordonnée Y de début");
        TextField yStopInput = new TextField();
        yStopInput.setPromptText("Coordonnée Y de fin");

        ComboBox<String> typeCulture = new ComboBox<>();
        typeCulture.getItems().addAll("Blé", "Maïs", "Orge", "Tournesol");
        typeCulture.setValue("Blé");

        Button btnValider = new Button("Enregistrer le champ");


        btnValider.setOnAction(e -> {
            try {
                double xStart = Double.parseDouble(xStartInput.getText());
                double yStart = Double.parseDouble(yStartInput.getText());
                double xStop = Double.parseDouble(xStopInput.getText());
                double yStop = Double.parseDouble(yStopInput.getText());

                String culture = typeCulture.getValue();
                ground.addField(new Field(culture, xStart, xStop, yStart, yStop));
                MapView mapView = new MapView(ground);
                stage.setScene(mapView.getScene(stage));
            } catch (NumberFormatException ex) {
                System.out.println("Erreur : Veuillez saisir des nombres valides pour les coordonnées.");
            }
        });

        VBox layout = new VBox(15, titleLabel, xStartInput, xStopInput, yStartInput, yStopInput, typeCulture, btnValider);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        return new Scene(layout, 1200, 700);
    }
    public Scene getSprinklerForm(Stage stage, Ground ground){
        Label titleLabel = new Label("Ajouter un nouveau arroseur");

        TextField xInput = new TextField();
        xInput.setPromptText("Coordonnée X");

        TextField yInput = new TextField();
        yInput.setPromptText("Coordonnée Y");

        TextField flowInput = new TextField();
        flowInput.setPromptText("Flow");

        TextField radiusInput = new TextField();
        radiusInput.setPromptText("Radius");

        Button btnValider = new Button("Enregistrer l'arroseur");


        btnValider.setOnAction(e -> {
            try {
                double x = Double.parseDouble(xInput.getText());
                double y = Double.parseDouble(yInput.getText());
                double flow = Double.parseDouble(flowInput.getText());
                double radius = Double.parseDouble(radiusInput.getText());

                ground.addSprinkler(new Sprinkler(x, y, flow, radius));
                MapView mapView = new MapView(ground);
                stage.setScene(mapView.getScene(stage));
            } catch (NumberFormatException ex) {
                System.out.println("Erreur : Veuillez saisir des nombres valides pour les coordonnées.");
            }
        });

        VBox layout = new VBox(15, titleLabel, xInput, yInput, flowInput, radiusInput, btnValider);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        return new Scene(layout, 1200, 700);
    }
    public Scene getWaterTankForm(Stage stage, Ground ground){
        Label titleLabel = new Label("Ajouter un nouveau waterTank");

        TextField xInput = new TextField();
        xInput.setPromptText("Coordonnée X");

        TextField yInput = new TextField();
        yInput.setPromptText("Coordonnée Y");

        TextField flowInput = new TextField();
        flowInput.setPromptText("Flow");

        TextField capacityInput = new TextField();
        capacityInput.setPromptText("Capacity");

        Button btnValider = new Button("Enregistrer le watertank");


        btnValider.setOnAction(e -> {
            try {
                double x = Double.parseDouble(xInput.getText());
                double y = Double.parseDouble(yInput.getText());
                double flow = Double.parseDouble(flowInput.getText());
                double capacity = Double.parseDouble(capacityInput.getText());

                ground.addTank(new WaterTank(x, y, capacity, flow));
                MapView mapView = new MapView(ground);
                stage.setScene(mapView.getScene(stage));
            } catch (NumberFormatException ex) {
                System.out.println("Erreur : Veuillez saisir des nombres valides pour les coordonnées.");
            }
        });

        VBox layout = new VBox(15, titleLabel, xInput, yInput, flowInput, capacityInput, btnValider);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        return new Scene(layout, 1200, 700);
    }
}
