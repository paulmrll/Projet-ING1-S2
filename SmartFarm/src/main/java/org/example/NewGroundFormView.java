package org.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
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
        BorderPane root = new BorderPane();
        VBox personForm = new VBox();
        personForm.setSpacing(20);
        personForm.setAlignment(Pos.CENTER);
        HBox topBar = SmartFarmUI.getTopBar(stage, "New Farm");
        root.setTop(topBar);
        Label titleLabel = new Label("Your Information");

        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 50px; -fx-font-weight: bold;");
        TextField nameInput = new TextField();nameInput.setStyle("-fx-background-color: #ffffff; -fx-text-fill: black; -fx-prompt-text-fill: #233722; -fx-background-radius: 6; -fx-padding: 8 12 8 12;");
        nameInput.setPromptText("Name");
        nameInput.setMaxWidth(400);

        TextField firstnameInput = new TextField();
        firstnameInput.setStyle("-fx-background-color: #ffffff; -fx-text-fill: black; -fx-prompt-text-fill: #233722; -fx-background-radius: 6; -fx-padding: 8 12 8 12;");
        firstnameInput.setPromptText("Firstname");
        firstnameInput.setMaxWidth(400);

        TextField emailInput = new TextField();
        emailInput.setStyle("-fx-background-color: #ffffff; -fx-text-fill: black; -fx-prompt-text-fill: #233722; -fx-background-radius: 6; -fx-padding: 8 12 8 12;");
        emailInput.setPromptText("Email");
        emailInput.setMaxWidth(400);

        TextField ageInput = new TextField();
        ageInput.setStyle("-fx-background-color: #ffffff; -fx-text-fill: black; -fx-prompt-text-fill: #233722; -fx-background-radius: 6; -fx-padding: 8 12 8 12;");
        ageInput.setPromptText("Age");
        ageInput.setMaxWidth(400);

        TextField areaInput = new TextField();
        areaInput.setStyle("-fx-background-color: #ffffff; -fx-text-fill: black; -fx-prompt-text-fill: #233722; -fx-background-radius: 6; -fx-padding: 8 12 8 12;");
        personForm.getChildren().addAll(nameInput, firstnameInput, emailInput, ageInput);
        areaInput.setPromptText("Area");
        areaInput.setMaxWidth(400);

        Button btnSave = new Button("Create Farm");
        btnSave.getStyleClass().add("btn-primary");

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
        VBox card = new VBox(15, personForm, areaInput);
        card.setStyle("-fx-background-color: rgba(0,0,0,0.40); -fx-background-radius: 12; -fx-padding: 28 40 28 40;");
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(520);

        VBox center = new VBox(30, titleLabel, card, btnSave);
        center.setAlignment(Pos.TOP_CENTER);
        center.setPadding(new Insets(60, 40, 40, 40));
        root.setCenter(center);

        root.setStyle("-fx-background-image: url('/bg_nuit.png'); -fx-background-size: cover;");
        Scene scene = new Scene(root, 1200, 700);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        return scene;
    }
}
