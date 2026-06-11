package org.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AddForm {

    public Scene getFieldScene(Stage stage, Ground ground) {
        // 1. On récupère le formulaire de base (les champs uniquement)
        VBox layout = getFieldForm();

        // 2. On crée le bouton à part, ici dans la Scene
        Button btnValider = new Button("Enregistrer le champ");

        // 3. On gère le comportement du bouton à part
        btnValider.setOnAction(e -> {
            try {
                // On va chercher les valeurs dans le VBox au MOMENT du clic
                double xStart = Double.parseDouble(((TextField) layout.getChildren().get(1)).getText());
                double xStop = Double.parseDouble(((TextField) layout.getChildren().get(2)).getText());
                double yStart = Double.parseDouble(((TextField) layout.getChildren().get(3)).getText());
                double yStop = Double.parseDouble(((TextField) layout.getChildren().get(4)).getText());

                // Index 5 correspond à la ComboBox
                String culture = ((ComboBox<String>) layout.getChildren().get(5)).getValue();

                // Métier et changement de scène
                ground.addField(new Field(culture, xStart, xStop, yStart, yStop));
                MapView mapView = new MapView(ground);
                stage.setScene(mapView.getScene(stage));

            } catch (NumberFormatException ex) {
                System.out.println("Erreur : Veuillez saisir des nombres valides pour les coordonnées.");
            }
        });

        layout.getChildren().add(btnValider);

        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        return new Scene(layout, 1200, 700);
    }

    public VBox getFieldForm() {
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

        return new VBox(15, titleLabel, xStartInput, xStopInput, yStartInput, yStopInput, typeCulture);
    }

    public Scene getSprinklerScene(Stage stage, Ground ground) {
        VBox layout = getSprinklerForm();
        Button btnValider = new Button("Enregistrer l'arroseur");

        btnValider.setOnAction(e -> {
            try {
                double x = Double.parseDouble(((TextField) layout.getChildren().get(1)).getText());
                double y = Double.parseDouble(((TextField) layout.getChildren().get(2)).getText());
                double flow = Double.parseDouble(((TextField) layout.getChildren().get(3)).getText());
                double radius = Double.parseDouble(((TextField) layout.getChildren().get(4)).getText());

                ground.addSprinkler(new Sprinkler(x, y, flow, radius));
                MapView mapView = new MapView(ground);
                stage.setScene(mapView.getScene(stage));
            } catch (NumberFormatException ex) {
                System.out.println("Erreur : Veuillez saisir des nombres valides pour les coordonnées.");
            }
        });

        layout.getChildren().add(btnValider);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        return new Scene(layout, 1200, 700);
    }

    public VBox getSprinklerForm() {
        Label titleLabel = new Label("Ajouter un nouveau arroseur");
        TextField xInput = new TextField();
        xInput.setPromptText("Coordonnée X");
        TextField yInput = new TextField();
        yInput.setPromptText("Coordonnée Y");
        TextField flowInput = new TextField();
        flowInput.setPromptText("Flow");
        TextField radiusInput = new TextField();
        radiusInput.setPromptText("Radius");

        return new VBox(15, titleLabel, xInput, yInput, flowInput, radiusInput);
    }

    public Scene getWaterTankScene(Stage stage, Ground ground) {
        VBox layout = getWaterTankForm();
        Button btnValider = new Button("Enregistrer le watertank");

        btnValider.setOnAction(e -> {
            try {
                double x = Double.parseDouble(((TextField) layout.getChildren().get(1)).getText());
                double y = Double.parseDouble(((TextField) layout.getChildren().get(2)).getText());
                double flow = Double.parseDouble(((TextField) layout.getChildren().get(3)).getText());
                double capacity = Double.parseDouble(((TextField) layout.getChildren().get(4)).getText());

                ground.addTank(new WaterTank(x, y, capacity, flow));
                MapView mapView = new MapView(ground);
                stage.setScene(mapView.getScene(stage));
            } catch (NumberFormatException ex) {
                System.out.println("Erreur : Veuillez saisir des nombres valides pour les coordonnées.");
            }
        });

        layout.getChildren().add(btnValider);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        return new Scene(layout, 1200, 700);
    }

    public VBox getWaterTankForm() {
        Label titleLabel = new Label("Ajouter un nouveau waterTank");
        TextField xInput = new TextField();
        xInput.setPromptText("Coordonnée X");
        TextField yInput = new TextField();
        yInput.setPromptText("Coordonnée Y");
        TextField flowInput = new TextField();
        flowInput.setPromptText("Flow");
        TextField capacityInput = new TextField();
        capacityInput.setPromptText("Capacity");
        return new VBox(15, titleLabel, xInput, yInput, flowInput, capacityInput);
    }
    public static Scene modify(Stage stage, Point p, Ground g){
        VBox main = new VBox();

        if (p != null){
            Label title = new Label();
            TextField xInput = new TextField();
            TextField yInput = new TextField();
            TextField flowInput = new TextField();


            if (p instanceof Sprinkler s){
                title.setText("Modify Sprinkler n°"+s.getId());
                xInput.setText(String.valueOf(s.getX()));
                yInput.setText(String.valueOf(s.getY()));
                flowInput.setText(String.valueOf(s.getFlow()));
            } else if (p instanceof WaterTank w){
                title.setText("Modify WaterTanks n°"+w.getId());
                xInput.setText(String.valueOf(w.getX()));
                yInput.setText(String.valueOf(w.getY()));
                flowInput.setText(String.valueOf(w.getFlow()));
            }

            main.setSpacing(20);
            Button modify = new Button("MODIFY");
            main.getChildren().addAll(title, xInput, yInput, flowInput, modify);

            modify.setOnAction(e->{
                try{
                    double x = Double.parseDouble(xInput.getText());
                    double y = Double.parseDouble(yInput.getText());
                    double flow = Double.parseDouble(flowInput.getText());
                    if (p instanceof Sprinkler s){
                        s.setFlow(flow);
                        s.setX(x);
                        s.setY(y);
                    } else if (p instanceof WaterTank w){
                        w.setFlow(flow);
                        w.setX(x);
                        w.setY(y);
                    }
                    MapView mapView = new MapView(g);
                    stage.setScene(mapView.getScene(stage));
                }catch (NumberFormatException ex) {
                    System.out.println("Erreur");
                }
            });

        }
        return new Scene(main, 1200, 700);
    }
}
