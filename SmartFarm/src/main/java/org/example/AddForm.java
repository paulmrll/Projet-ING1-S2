package org.example;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AddForm {

    public Scene getFieldScene(Stage stage, Ground ground) {
        // 1. On récupère le formulaire de base (les champs uniquement)
        VBox layout = getFieldForm();

        // 2. On crée le bouton à part, ici dans la Scene
        Button btnValider = buttonStyle("Enregistrer le champ");

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

        layout.setSpacing(20);
        layout.setAlignment(Pos.CENTER);

        return sceneStyle(layout);
    }

    public VBox getFieldForm() {
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

    public Scene getSprinklerScene(Stage stage, Ground ground) {
        VBox layout = getSprinklerForm();
        Button btnValider = buttonStyle("Enregistrer l'arroseur");

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
        layout.setSpacing(20);
        layout.setAlignment(Pos.CENTER);

        return sceneStyle(layout);
    }

    public VBox getSprinklerForm() {
        Label titleLabel = labelStyle("Ajouter un nouveau arroseur");
        TextField xInput = textFieldEnterPromptText("Coordonnée X");
        TextField yInput = textFieldEnterPromptText("Coordonnée Y");
        TextField flowInput = textFieldEnterPromptText("Flow");
        TextField radiusInput = textFieldEnterPromptText("Radius");

        return new VBox(15, titleLabel, xInput, yInput, flowInput, radiusInput);
    }

    public Scene getWaterTankScene(Stage stage, Ground ground) {
        VBox layout = getWaterTankForm();
        Button btnValider = buttonStyle("Enregistrer le watertank");

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
        layout.setSpacing(20);
        layout.setAlignment(Pos.CENTER);

        return sceneStyle(layout);
    }

    public VBox getWaterTankForm() {
        Label titleLabel = labelStyle("Ajouter un nouveau waterTank");
        TextField xInput = textFieldEnterPromptText("Coordonnée X");
        TextField yInput = textFieldEnterPromptText("Coordonnée Y");
        TextField flowInput = textFieldEnterPromptText("Flow");
        TextField capacityInput = textFieldEnterPromptText("Capacity");
        return new VBox(15, titleLabel, xInput, yInput, flowInput, capacityInput);
    }

    public static Scene modifyTanksSprinklers(Stage stage, Point p, Ground g) {
        VBox main = new VBox();

        if (p != null) {
            Label title = null;
            TextField xInput;
            TextField yInput;
            TextField flowInput;


            if (p instanceof Sprinkler s) {
                title = labelStyle("Modify Sprinkler n°" + s.getId());
                xInput = textFieldEnterText(String.valueOf(s.getX()));
                yInput = textFieldEnterText(String.valueOf(s.getY()));
                flowInput = textFieldEnterText(String.valueOf(s.getFlow()));
            } else if (p instanceof WaterTank w) {
                title = labelStyle("Modify WaterTanks n°" + w.getId());
                xInput = textFieldEnterText(String.valueOf(w.getX()));
                yInput = textFieldEnterText(String.valueOf(w.getY()));
                flowInput = textFieldEnterText(String.valueOf(w.getFlow()));
            } else {
                yInput = null;
                flowInput = null;
                xInput = null;
            }

            main.setSpacing(20);
            main.setAlignment(Pos.CENTER);
            Button modify = buttonStyle("MODIFY");
            main.getChildren().addAll(title, xInput, yInput, flowInput, modify);

            modify.setOnAction(e -> {
                try {
                    double x = Double.parseDouble(xInput.getText());
                    double y = Double.parseDouble(yInput.getText());
                    double flow = Double.parseDouble(flowInput.getText());
                    if (p instanceof Sprinkler s) {
                        s.setFlow(flow);
                        s.setX(x);
                        s.setY(y);
                    } else if (p instanceof WaterTank w) {
                        w.setFlow(flow);
                        w.setX(x);
                        w.setY(y);
                    }
                    MapView mapView = new MapView(g);
                    stage.setScene(mapView.getScene(stage));
                } catch (NumberFormatException ex) {
                    System.out.println("Erreur");
                }
            });

        }
        return sceneStyle(main);
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
    private static String textFieldStyle(){
        return "-fx-backgroun-color: #ffffff; " +
                "-fx-text-fill: black; " +
                "-fx-prompt-text-fill: #233722; " +
                "-fx-background-radius: 6; " +
                "-fx-padding: 8 12 8 12;";
    }

    private static Scene sceneStyle(Parent parent) {
        Scene scene = new Scene(parent, 1200, 700);
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
