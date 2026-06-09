package org.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AddFieldForm {
    public Scene getForm(Stage stage){
        Label titleLabel = new Label("Ajouter un nouveau champ");

        TextField xStartInput = new TextField();
        xStartInput.setPromptText("Coordonnée X de début"); // Texte d'aide en arrière-plan

        TextField yStartInput = new TextField();
        yStartInput.setPromptText("Coordonnée Y de début");

        ComboBox<String> typeCulture = new ComboBox<>();
        typeCulture.getItems().addAll("Blé", "Maïs", "Orge", "Tournesol");
        typeCulture.setValue("Blé"); // Valeur par défaut

        Button btnValider = new Button("Enregistrer le champ");

        // 2. Écouteur sur le bouton pour récupérer les données lors du clic
        btnValider.setOnAction(e -> {
            try {
                // On récupère et on convertit les données du TextField
                double xStart = Double.parseDouble(xStartInput.getText());
                double yStart = Double.parseDouble(yStartInput.getText());

                // On récupère la valeur de la ComboBox
                String culture = typeCulture.getValue();

                // Exemple d'utilisation : Affichage dans la console
                System.out.println("Données récupérées !");
                System.out.println("Position : (" + xStart + ", " + yStart + ") | Culture : " + culture);

                // Ici, vous ferez probablement :
                // Field newField = new Field(xStart, ..., culture);
                // ground.addField(newField);

            } catch (NumberFormatException ex) {
                // Gestion d'erreur si l'utilisateur a tapé des lettres à la place d'un nombre
                System.out.println("Erreur : Veuillez saisir des nombres valides pour les coordonnées.");
            }
        });

        // 3. Mise en page
        VBox layout = new VBox(15, titleLabel, xStartInput, yStartInput, typeCulture, btnValider);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        return new Scene(layout, 400, 400);

    }
}
