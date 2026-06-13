package org.example;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.*;

public class AddForm {

    public Scene getFieldScene(Stage stage, Ground ground) {
        VBox layout = getFieldForm();

        Button btnValider = buttonStyle("Enregistrer le champ");

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

        layout.setSpacing(20);
        layout.setAlignment(Pos.CENTER);

        return sceneStyle(layout);
    }

    public static VBox getFieldForm() {
        Label titleLabel = labelStyle("Ajouter un nouveau champ");

        TextField xStartInput = textFieldEnterPromptText("Coordonnée X de début");
        TextField xStopInput = textFieldEnterPromptText("Coordonnée X de fin");

        TextField yStartInput = textFieldEnterPromptText("Coordonnée Y de début");
        TextField yStopInput = textFieldEnterPromptText("Coordonnée Y de fin");

        ComboBox<String> typeCulture = new ComboBox<>();
        typeCulture.getItems().addAll("Blé", "Maïs", "Orge", "Tournesol");
        typeCulture.setValue("Blé");

        return new VBox(15, titleLabel, xStartInput, xStopInput, yStartInput, yStopInput, typeCulture);
    }

    public VBox getSprinklerForm() {
        Label titleLabel = labelStyle("Ajouter un nouveau arroseur");
        TextField xInput = textFieldEnterPromptText("Coordonnée X");
        TextField yInput = textFieldEnterPromptText("Coordonnée Y");
        TextField flowInput = textFieldEnterPromptText("Flow");
        TextField radiusInput = textFieldEnterPromptText("Radius");

        return new VBox(15, titleLabel, xInput, yInput, flowInput, radiusInput);
    }

    public VBox getWaterTankForm() {
        Label titleLabel = labelStyle("Ajouter un nouveau waterTank");
        TextField xInput = textFieldEnterPromptText("Coordonnée X");
        TextField yInput = textFieldEnterPromptText("Coordonnée Y");
        TextField flowInput = textFieldEnterPromptText("Flow");
        TextField capacityInput = textFieldEnterPromptText("Capacity");
        return new VBox(15, titleLabel, xInput, yInput, flowInput, capacityInput);
    }

    public static VBox modifyTanksSprinklers(Stage stage, Point p, Ground g) {
        VBox main = new VBox();

        if (p != null) {
            Label title = null;
            TextField flowInput;
            TextField capacityInput;
            HBox hBoxFlow = new HBox();
            HBox hBoxCapacity = new HBox();
            Label flowLabel = new Label("Flow : ");
            flowLabel.setStyle("-fx-text-fill: white;");
            Label capacityLabel = new Label("Capacity : ");
            capacityLabel.setStyle("-fx-text-fill: white;");
            Button modify = buttonStyle("MODIFY");
            if (p instanceof Sprinkler s) {
                title = labelStyle("Modify Sprinkler n°" + s.getId());
                flowInput = textFieldEnterText(String.valueOf(s.getFlow()));
                hBoxFlow.getChildren().addAll(flowLabel, flowInput);
                hBoxCapacity = null;
                capacityInput = null;
            } else if (p instanceof WaterTank w) {
                title = labelStyle("Modify WaterTanks n°" + w.getId());
                flowInput = textFieldEnterText(String.valueOf(w.getFlow()));
                capacityInput = textFieldEnterText(String.valueOf(w.getCapacity()));
                hBoxFlow.getChildren().addAll(flowLabel, flowInput);
                hBoxCapacity.getChildren().addAll(capacityLabel, capacityInput);
            } else {
                flowInput = null;
                capacityInput = null;
            }

            main.setSpacing(10);
            main.setAlignment(Pos.CENTER);
            if (hBoxCapacity != null){
                main.getChildren().addAll(title, hBoxFlow, hBoxCapacity, modify);
            } else {
                main.getChildren().addAll(title, hBoxFlow, modify);
            }


            modify.setOnAction(e -> {
                try {
                    double flow = Double.parseDouble(flowInput.getText());
                    double capacity;
                    if (capacityInput != null){
                        capacity = Double.parseDouble(capacityInput.getText());
                    }
                    if (p instanceof Sprinkler s) {
                        s.setFlow(flow);
                    } else if (p instanceof WaterTank w) {
                        w.setFlow(flow);
                    }
                    MapView mapView = new MapView(g);
                    stage.setScene(mapView.getScene(stage));
                } catch (NumberFormatException ex) {
                    System.out.println("Erreur");
                }
            });
        }
        return main;
    }

    public static Scene modifyUser(Stage stage, Ground g) {
        VBox main = new VBox();
        main.setSpacing(20);
        main.setAlignment(Pos.CENTER);
        if (g != null) {
            if (g.getOwner() != null) {
                Label title = labelStyle("Modify User " + g.getOwner().getName() + " informations");
                TextField nameInput = textFieldEnterText(g.getOwner().getName());
                TextField firstnameInput = textFieldEnterText(g.getOwner().getFirstname());
                TextField mailInput = textFieldEnterText(g.getOwner().getEmail());
                TextField ageInput = textFieldEnterText(String.valueOf(g.getOwner().getAge()));
                Button modify = buttonStyle("MODIFY");
                modify.setOnAction(e -> {
                    try {
                        String name = nameInput.getText();
                        String firstname = firstnameInput.getText();
                        String mail = mailInput.getText();
                        int age = Integer.parseInt(ageInput.getText());

                        g.getOwner().setName(name);
                        g.getOwner().setFirstname(firstname);
                        g.getOwner().setAge(age);
                        g.getOwner().setEmail(mail);

                        MapView mapView = new MapView(g);
                        stage.setScene(mapView.getScene(stage));
                    } catch (NumberFormatException ex) {
                        System.out.println("Erreur");
                    }
                });
                main.getChildren().addAll(title, nameInput, firstnameInput, ageInput, mailInput, modify);
            }
        }


        return sceneStyle(main);
    }

    private static TextField textFieldEnterPromptText(String s) {
        TextField textField = new TextField();
        textField.setStyle(
                textFieldStyle()
        );
        textField.setPromptText(s);
        textField.setMaxWidth(400);
        return textField;
    }

    private static TextField textFieldEnterText(String s) {
        TextField textField = new TextField();
        textField.setStyle(
                textFieldStyle()
        );
        textField.setText(s);
        textField.setMaxWidth(400);
        return textField;
    }

    private static String textFieldStyle() {
        return "-fx-backgroun-color: #ffffff; " +
                "-fx-text-fill: black; " +
                "-fx-prompt-text-fill: #233722; " +
                "-fx-background-radius: 6; " +
                "-fx-padding: 8 12 8 12;";
    }

    private static Scene sceneStyle(Parent parent) {
        Scene scene = new Scene(parent, 1300, 800);
        scene.getRoot().setStyle(
                "-fx-background-image: url('/bg_nuit.png'); " +
                        "-fx-background-size: cover;"
        );
        return scene;
    }

    private static Label labelStyle(String string) {
        Label label = new Label(string);
        label.setStyle("-fx-font-family: 'Arial';" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 24px;" +
                "-fx-text-fill: white;"
        );
        return label;
    }

    private static Button buttonStyle(String text) {
        Button btn = new Button(text);
        btn.setPrefWidth(192);
        btn.setStyle(
                "-fx-background-color: #3a5a30;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 5;" +
                        "-fx-padding: 8 12 8 12;" +
                        "-fx-font-size: 13px;" +
                        "-fx-cursor: hand;"
        );
        return btn;
    }
}
