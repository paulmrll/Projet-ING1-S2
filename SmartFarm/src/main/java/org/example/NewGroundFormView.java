package org.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Map;

public class NewGroundFormView {

    private Scene groundFormView(Stage stage, Ground ground) {

        AddForm addForm = new AddForm();
        VBox vBoxField = addForm.getFieldForm();
        VBox vBoxTank = addForm.getWaterTankForm();
        VBox vBoxSprinkler = addForm.getSprinklerForm();

        Button btnSaveField = new Button("Enregistrer");
        Button btnSaveTank = new Button("Enregistrer");
        Button btnSaveSprinkler = new Button("Enregistrer");
        Button btnMap = new Button("Access to the mapView");

        btnMap.setOnAction(e->{
            MapView mapView = new MapView(ground);
            stage.setScene(mapView.getScene(stage));
        });

        vBoxField.getChildren().add(btnSaveField);
        vBoxTank.getChildren().add(btnSaveTank);
        vBoxSprinkler.getChildren().add(btnSaveSprinkler);
        btnSaveField.setOnAction(e -> {
            try {
                double xStart = Double.parseDouble(((TextField) vBoxField.getChildren().get(1)).getText());
                double xStop = Double.parseDouble(((TextField) vBoxField.getChildren().get(2)).getText());
                double yStart = Double.parseDouble(((TextField) vBoxField.getChildren().get(3)).getText());
                double yStop = Double.parseDouble(((TextField) vBoxField.getChildren().get(4)).getText());
                String culture = ((ComboBox<String>) vBoxField.getChildren().get(5)).getValue();
                ground.addField(new Field(culture, xStart, xStop, yStart, yStop));
            } catch (Exception ex) {
                System.out.println("Erreur dans le formulaire de Champ.");
            }
        });

        btnSaveTank.setOnAction(e -> {
            try {
                double x = Double.parseDouble(((TextField) vBoxTank.getChildren().get(1)).getText());
                double y = Double.parseDouble(((TextField) vBoxTank.getChildren().get(2)).getText());
                double flow = Double.parseDouble(((TextField) vBoxTank.getChildren().get(3)).getText());
                double capacity = Double.parseDouble(((TextField) vBoxTank.getChildren().get(4)).getText());

                ground.addTank(new WaterTank(x, y, capacity, flow));
            } catch (Exception ex) {
                System.out.println("Erreur dans le formulaire du Réservoir.");
            }
        });

        btnSaveSprinkler.setOnAction(e -> {
            try {
                double x = Double.parseDouble(((TextField) vBoxSprinkler.getChildren().get(1)).getText());
                double y = Double.parseDouble(((TextField) vBoxSprinkler.getChildren().get(2)).getText());
                double flow = Double.parseDouble(((TextField) vBoxSprinkler.getChildren().get(3)).getText());
                double radius = Double.parseDouble(((TextField) vBoxSprinkler.getChildren().get(4)).getText());

                ground.addSprinkler(new Sprinkler(x, y, flow, radius));
            } catch (Exception ex) {
                System.out.println("Erreur dans le formulaire de l'Arroseur.");
            }
        });



        HBox hBoxTankSprinkler = new HBox();
        hBoxTankSprinkler.setSpacing(20);
        hBoxTankSprinkler.getChildren().addAll(vBoxTank, vBoxSprinkler);
        VBox root = new VBox();
        root.setSpacing(20);
        root.getChildren().addAll(vBoxField, hBoxTankSprinkler);
        root.getChildren().add(btnMap);

        return new Scene(root, 1200, 700);
    }

    public Scene newGround(Stage stage){
        VBox root = new VBox();
        root.setSpacing(20);
        VBox personForm = new VBox();
        personForm.setSpacing(20);
        personForm.setAlignment(Pos.CENTER);
        root.setAlignment(Pos.CENTER);
        Label titleLabel = new Label("Nouvel Utilisateur");
        TextField nameInput = new TextField();
        nameInput.setPromptText("Name");
        TextField firstnameInput = new TextField();
        firstnameInput.setPromptText("Firstname");
        TextField emailInput = new TextField();
        emailInput.setPromptText("Email");
        TextField ageInput = new TextField();
        ageInput.setPromptText("Age");
        TextField areaInput = new TextField();
        personForm.getChildren().addAll(nameInput, firstnameInput, emailInput, ageInput);
        areaInput.setPromptText("Area");
        Button btnSave = new Button("Enregrister");

        btnSave.setOnAction(e->{
            try {
                String name = nameInput.getText();
                String firstname = firstnameInput.getText();
                String email = emailInput.getText();
                int age = Integer.parseInt(ageInput.getText());
                double area = Double.parseDouble(areaInput.getText());

                Ground ground = new Ground(area, new Person(age, name, firstname, email));
                stage.setScene(groundFormView(stage, ground));
            } catch (Exception ex) {
                System.out.println("Erreur dans le formulaire");
            }
        });
        root.getChildren().addAll(personForm, areaInput, btnSave);
        return new Scene(root, 1200, 700);
    }
}
