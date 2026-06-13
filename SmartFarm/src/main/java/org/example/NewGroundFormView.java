package org.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Map;

/**
 * The {@code NewGroundFormView} class handles the generation of JavaFX scenes
 * dedicated to the multi-step creation of a new farm infrastructure.
 * It gathers initial owner profile information, initializes a {@link Ground} instance,
 * and provides embedded structural forms to add fields, water tanks, and sprinklers.
 *
 * @author SmartFarm Team
 * @version 1.0
 */
public class NewGroundFormView {

    /**
     * Builds and returns the second-step layout scene where users configure components
     * for their newly created terrain. It nests sub-forms built via {@link AddForm}
     * and manages specific action buttons to explicitly parse inputs and insert instances into
     * the active {@link Ground} dataset.
     *
     * @param stage  the primary window ({@link Stage}) used to adjust scenes upon navigation.
     * @param ground the active {@link Ground} data instance currently being populated.
     * @return a stylized {@link Scene} dedicated to component registration (fields, tanks, sprinklers).
     */
    private Scene groundFormView(Stage stage, Ground ground) {

        AddForm addForm = new AddForm();
        VBox vBoxField = addForm.getFieldForm();
        VBox vBoxTank = addForm.getWaterTankForm();
        VBox vBoxSprinkler = addForm.getSprinklerForm();

        Button btnSaveField = buttonStyle("Enregistrer");
        Button btnSaveTank = buttonStyle("Enregistrer");
        Button btnSaveSprinkler = buttonStyle("Enregistrer");
        Button btnMap = buttonStyle("Access to the mapView");

        btnMap.setOnAction(e -> {
            if (!ground.getFields().isEmpty()){
                MapView mapView = new MapView(ground);
                stage.setScene(mapView.getScene(stage));
            }
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
                ((TextField) vBoxField.getChildren().get(1)).clear();
                ((TextField) vBoxField.getChildren().get(2)).clear();
                ((TextField) vBoxField.getChildren().get(3)).clear();
                ((TextField) vBoxField.getChildren().get(4)).clear();
                ((ComboBox<String>) vBoxField.getChildren().get(5)).setValue("Blé");
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
                ((TextField) vBoxTank.getChildren().get(1)).clear();
                ((TextField) vBoxTank.getChildren().get(2)).clear();
                ((TextField) vBoxTank.getChildren().get(3)).clear();
                ((TextField) vBoxTank.getChildren().get(4)).clear();
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
                ((TextField) vBoxSprinkler.getChildren().get(1)).clear();
                ((TextField) vBoxSprinkler.getChildren().get(2)).clear();
                ((TextField) vBoxSprinkler.getChildren().get(3)).clear();
                ((TextField) vBoxSprinkler.getChildren().get(4)).clear();
                ground.addSprinkler(new Sprinkler(x, y, flow, radius));
            } catch (Exception ex) {
                System.out.println("Erreur dans le formulaire de l'Arroseur.");
            }
        });


        HBox hBoxTankSprinkler = new HBox();
        hBoxTankSprinkler.setSpacing(20);
        hBoxTankSprinkler.setAlignment(Pos.CENTER);
        hBoxTankSprinkler.getChildren().addAll(vBoxTank, vBoxSprinkler);
        VBox root = new VBox();
        root.setSpacing(20);
        vBoxField.setAlignment(Pos.CENTER);
        root.getChildren().addAll(vBoxField, hBoxTankSprinkler);
        root.getChildren().add(btnMap);
        root.setAlignment(Pos.CENTER);

        return sceneStyle(root);
    }

    /**
     * Instantiates the primary setup form scene to initialize a new farm configuration.
     * Collects farmer demographics (Name, Firstname, Email, Age) alongside the structural
     * surface area. Once inputs are validated, it builds a {@link Ground} entity and routes
     * the workflow towards the secondary configuration interface.
     *
     * @param stage the primary window ({@link Stage}) of the application required for view routing.
     * @return a stylized JavaFX {@link Scene} holding the initial registration inputs.
     */
    public Scene newGround(Stage stage) {
        BorderPane root = new BorderPane();
        VBox personForm = new VBox();
        personForm.setSpacing(20);
        personForm.setAlignment(Pos.CENTER);
        HBox topBar = SmartFarmUI.getTopBar(stage, "New Farm");
        root.setTop(topBar);
        Label titleLabel = new Label("Your Information");

        titleLabel.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 50px;" +
                        "-fx-font-weight: bold;"
        );
        TextField nameInput = textFieldEnter("Name");


        TextField firstnameInput = textFieldEnter("Firstname");

        TextField emailInput = textFieldEnter("Email");

        TextField ageInput = textFieldEnter("Age");

        TextField areaInput = textFieldEnter("Area");
        personForm.getChildren().addAll(nameInput, firstnameInput, emailInput, ageInput);

        Button btnSave = buttonStyle("Create Farm");
        btnSave.getStyleClass().add("btn-primary");

        btnSave.setOnAction(e -> {
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
        card.setStyle(
                "-fx-background-color: rgba(0,0,0,0.40); " +
                        "-fx-background-radius: 12; " +
                        "-fx-padding: 28 40 28 40;"
        );
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(520);

        VBox center = new VBox(30, titleLabel, card, btnSave);
        center.setAlignment(Pos.TOP_CENTER);
        center.setPadding(new Insets(60, 40, 40, 40));
        root.setCenter(center);


        return sceneStyle(root);
    }

    /**
     * Creates an empty user text field populated with a specific helper prompt text
     * and structured CSS attributes.
     *
     * @param s the prompt text string displayed within the field boundaries.
     * @return an customized instance of a JavaFX {@link TextField}.
     */
    private TextField textFieldEnter(String s) {
        TextField textField = new TextField();
        textField.setStyle(
                "-fx-backgroun-color: #ffffff; " +
                        "-fx-text-fill: black; " +
                        "-fx-prompt-text-fill: #233722; " +
                        "-fx-background-radius: 6; " +
                        "-fx-padding: 8 12 8 12;"
        );
        textField.setPromptText(s);
        textField.setMaxWidth(400);
        return textField;
    }

    /**
     * Formats a fixed dimension JavaFX Scene layout by embedding an external
     * night theme graphic asset onto its root layout nodes.
     *
     * @param parent the target parent node architecture acting as the scene's core layout.
     * @return a customized {@link Scene} container with a scale responsive background image.
     */
    private static Scene sceneStyle(Parent parent) {
        Scene scene = new Scene(parent, 1300, 800);
        scene.getRoot().setStyle(
                "-fx-background-image: url('/bg_nuit.png'); " +
                        "-fx-background-size: cover;"
        );
        return scene;
    }

    /**
     * Generates a stylized ready-to-use action push button tailored to match the application theme rules.
     * Sets dimensions, background hue, text alignment, paddings, and cursor mouse hand states.
     *
     * @param text the alphanumeric characters printed on the interactive button surface.
     * @return a styled JavaFX {@link Button} reference.
     */
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