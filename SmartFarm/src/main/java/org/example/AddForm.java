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

/**
 * The {@code AddForm} class provides static and instance utility methods
 * to generate JavaFX user interface forms. These forms allow users to add
 * or modify smart farm components (fields, sprinklers, water tanks) as well
 * as user profile information.
 * * @author SmartFarmTeam
 * @version 1.0
 */
public class AddForm {

    /**
     * Generates a vertical layout form (VBox) to input information
     * for creating a new agricultural field.
     * The form includes inputs for start/end coordinates (X, Y) and a culture type selector.
     *
     * @return a {@link VBox} containing the layout, text fields, and combo box for the new field.
     */
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

    /**
     * Generates a vertical layout form (VBox) to input information
     * for creating a new sprinkler.
     * The form includes inputs for coordinates (X, Y), flow rate, and coverage radius.
     *
     * @return a {@link VBox} configured for sprinkler property input.
     */
    public VBox getSprinklerForm() {
        Label titleLabel = labelStyle("Ajouter un nouveau arroseur");
        TextField xInput = textFieldEnterPromptText("Coordonnée X");
        TextField yInput = textFieldEnterPromptText("Coordonnée Y");
        TextField flowInput = textFieldEnterPromptText("Flow");
        TextField radiusInput = textFieldEnterPromptText("Radius");

        return new VBox(15, titleLabel, xInput, yInput, flowInput, radiusInput);
    }

    /**
     * Generates a vertical layout form (VBox) to input information
     * for creating a new water tank.
     * The form includes inputs for coordinates (X, Y), flow rate, and maximum capacity.
     *
     * @return a {@link VBox} configured for water tank property input.
     */
    public VBox getWaterTankForm() {
        Label titleLabel = labelStyle("Ajouter un nouveau waterTank");
        TextField xInput = textFieldEnterPromptText("Coordonnée X");
        TextField yInput = textFieldEnterPromptText("Coordonnée Y");
        TextField flowInput = textFieldEnterPromptText("Flow");
        TextField capacityInput = textFieldEnterPromptText("Capacity");
        return new VBox(15, titleLabel, xInput, yInput, flowInput, capacityInput);
    }

    /**
     * Generates a dynamic editing interface to modify an existing equipment (Sprinkler or Water Tank).
     * It dynamically checks the type of the {@link Point} object and adapts the input fields accordingly.
     * On submission, it updates the target object properties and refreshes the map view scene.
     *
     * @param stage the primary {@link Stage} of the application used to refresh the active scene.
     * @param p     the item to modify; must be an instance of {@link Sprinkler} or {@link WaterTank}.
     * @param g     the current {@link Ground} terrain, used to re-render the map view post-update.
     * @return a {@link VBox} containing the pre-filled editing form, or an empty layout if {@code p} is null.
     */
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
            if (hBoxCapacity != null) {
                main.getChildren().addAll(title, hBoxFlow, hBoxCapacity, modify);
            } else {
                main.getChildren().addAll(title, hBoxFlow, modify);
            }


            modify.setOnAction(e -> {
                try {
                    double flow = Double.parseDouble(flowInput.getText());
                    double capacity;
                    if (capacityInput != null) {
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

    /**
     * Generates a complete JavaFX Scene to edit the personal profile information
     * of the farm's owner (Lastname, Firstname, Email, Age).
     * Upon saving, updates the owner's details and redirects back to the primary map view.
     *
     * @param stage the primary {@link Stage} of the application.
     * @param g     the {@link Ground} terrain containing the owner profile data to be modified.
     * @return a stylized {@link Scene} containing the full user profile edit form.
     */
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

    /**
     * Creates an empty input text field initialized with placeholder helper text
     * and custom CSS styling applied.
     *
     * @param s the placeholder text string to display when the field is empty.
     * @return a stylized {@link TextField} with the specified prompt text.
     */
    private static TextField textFieldEnterPromptText(String s) {
        TextField textField = new TextField();
        textField.setStyle(textFieldStyle());
        textField.setPromptText(s);
        textField.setMaxWidth(400);
        return textField;
    }

    /**
     * Creates an input text field pre-filled with an initial value
     * and custom CSS styling applied.
     *
     * @param s the initial text value to populate within the text field.
     * @return a stylized pre-filled {@link TextField}.
     */
    private static TextField textFieldEnterText(String s) {
        TextField textField = new TextField();
        textField.setStyle(textFieldStyle());
        textField.setText(s);
        textField.setMaxWidth(400);
        return textField;
    }

    /**
     * Defines the inline JavaFX CSS style string used across all input text fields.
     * Configures background color, text colors, boundary rounding, and internal padding.
     *
     * @return a {@link String} containing JavaFX CSS properties.
     */
    private static String textFieldStyle() {
        return "-fx-backgroun-color: #ffffff; " + "-fx-text-fill: black; " + "-fx-prompt-text-fill: #233722; " + "-fx-background-radius: 6; " + "-fx-padding: 8 12 8 12;";
    }

    /**
     * Builds and configures a fixed dimensions JavaFX Scene, applying
     * a night themed background image across its root layout component.
     *
     * @param parent the root container layout to wrap within the scene.
     * @return a customized {@link Scene} sized to 1300x800 pixels with its background graphic applied.
     */
    private static Scene sceneStyle(Parent parent) {
        Scene scene = new Scene(parent, 1300, 800);
        scene.getRoot().setStyle("-fx-background-image: url('/bg_nuit.png'); " + "-fx-background-size: cover;");
        return scene;
    }

    /**
     * Instantiates a standardized GUI title component as a JavaFX Label.
     * Configures an Arial bold typeface font, 24px character sizing, and a white font color.
     *
     * @param string the raw text string to render inside the label.
     * @return a styled {@link Label} matching the UI guidelines.
     */
    private static Label labelStyle(String string) {
        Label label = new Label(string);
        label.setStyle("-fx-font-family: 'Arial';" + "-fx-font-weight: bold;" + "-fx-font-size: 24px;" + "-fx-text-fill: white;");
        return label;
    }

    /**
     * Instantiates a standardized JavaFX Button component designed for user action confirmation.
     * Sets a uniform width, forest green background, white lettering, and switches the cursor behavior to interactive hand on hover.
     *
     * @param text the label string displayed on the face of the button.
     * @return a stylized ready-to-use {@link Button}.
     */
    private static Button buttonStyle(String text) {
        Button btn = new Button(text);
        btn.setPrefWidth(192);
        btn.setStyle("-fx-background-color: #3a5a30;" + "-fx-text-fill: white;" + "-fx-background-radius: 5;" + "-fx-padding: 8 12 8 12;" + "-fx-font-size: 13px;" + "-fx-cursor: hand;");
        return btn;
    }
}