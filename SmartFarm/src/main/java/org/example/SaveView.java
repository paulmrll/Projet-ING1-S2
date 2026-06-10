package org.example;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.geometry.Insets;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SaveView {
    private Label statusLabel;

    public Scene getScene(Stage stage) {



        statusLabel = new Label("Initialisation...");
        statusLabel.setFont(Font.font("Arial", 12));

        Path currentFolder = Paths.get("./SmartFarm/src/main/resources/Saves");
        HBox topBar = SmartFarmUI.getTopBar(stage, "Load a Farm ");
        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color : #1c1c1e;");
        mainLayout.setTop(topBar);
        mainLayout.setCenter(chargerFichiers(currentFolder, stage));

        Scene scene = new Scene(mainLayout, 1200, 700);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        return scene;
    }

    
    private VBox chargerFichiers(Path dossier, Stage stage) {
        Label soustitre = new Label("Your saved farms");
        soustitre.setStyle(
                "-fx-text-fill : #666; " +
                "-fx-font-size : 18px;"
        );

        Label titre = new Label("Which farm to load?");
        titre.setStyle(
                "-fx-text-fill : white; " +
                "-fx-font-size : 50px; " +
                "-fx-font-weight : bold;"
        );

        VBox titreSection = new VBox(6);
        titreSection.setAlignment(Pos.CENTER);
        titreSection.getChildren().addAll(soustitre, titre);

        File currentFolder = new File(dossier.toUri());
        File[] filesList = currentFolder.listFiles();

        VBox listeCartes = new VBox(10);
        listeCartes.setAlignment(Pos.CENTER);

        int nbFichiers = 0;

        if (filesList != null) {
            for (File f : filesList) {
                if (f.isFile()) {
                    nbFichiers ++;

                    Label icone = new Label("\uf06c");
                    icone.setFont(Font.font("FontAwesome", 16));
                    icone.setStyle(
                            "-fx-text-fill: #7aaa74;" +
                            "-fx-padding : 8; -fx-background-color : rgba(120, 200, 100, 0.10); " +
                            "-fx-background-radius : 8; " +
                            "-fx-min-width : 36; " +
                            "-fx-min-height : 36; " +
                            "-fx-alignment : center;"
                    );

                    Label nomFichier = new Label(f.getName());
                    nomFichier.setStyle(
                            "-fx-text-fill : white; " +
                            "-fx-font-size : 14px; " +
                            "-fx-font-weight : bold;"
                    );
                    nomFichier.setPrefWidth(500);

                    Label fleche = new Label("->");
                    fleche.setStyle(
                            "-fx-text-fill: #555; " +
                            "-fx-font-size: 16px;"
                    );

                    HBox carte = new HBox(14);
                    carte.setAlignment(Pos.CENTER_LEFT);
                    carte.setMaxWidth(630);
                    carte.setPrefWidth(630);
                    carte.getChildren().addAll(icone, nomFichier, fleche);
                    carte.setStyle(
                            "-fx-background-color: rgba(255,255,255,0.06); " +
                            "-fx-border-color: rgba(255,255,255,0.10); " +
                            "-fx-border-radius: 10; -fx-background-radius: 10; " +
                            "-fx-padding: 14 18 14 18; " +
                            "-fx-cursor: hand;"
                    );

                    carte.setOnMouseEntered(e -> carte.setStyle(
                            "-fx-background-color: rgba(255,255,255,0.11); " +
                            "-fx-border-color: rgba(255,255,255,0.22); " +
                            "-fx-border-radius: 10; -fx-background-radius: 10; " +
                            "-fx-padding: 14 18 14 18; " +
                            "-fx-cursor: hand;"
                    ));
                    carte.setOnMouseExited(e -> carte.setStyle(
                            "-fx-background-color: rgba(255,255,255,0.06); " +
                            "-fx-border-color: rgba(255,255,255,0.10); " +
                            "-fx-border-radius: 10; -fx-background-radius: 10; " +
                            "-fx-padding: 14 18 14 18; " +
                            "-fx-cursor: hand;"
                    ));

                    carte.setOnMouseClicked(e -> {
                        Save save = new Save(f.getAbsolutePath());
                        try {
                            MapView mapView = new MapView(save.readSave());
                            stage.setScene(mapView.getScene(stage));
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    });

                    listeCartes.getChildren().add(carte);
                }
            }
        }

        Label compteur = new Label(nbFichiers + " farms available");
        compteur.setStyle("-fx-text-fill: #555; -fx-font-size: 12px;");

        HBox separateur = new HBox();
        separateur.setMaxWidth(600);
        separateur.setPrefWidth(600);
        separateur.setPrefHeight(1);
        separateur.setStyle("-fx-background-color: #333;");

        Button btnNouvelleFerme = new Button("+ Add a new farm");
        btnNouvelleFerme.setStyle(
                "-fx-background-color: transparent; " +
                "-fx-border-color: rgba(255,255,255,0.18); " +
                "-fx-text-fill: #888; -fx-border-radius: 8; " +
                "-fx-background-radius: 8; " +
                "-fx-padding: 10 24 10 24; " +
                "-fx-font-size: 13px;"
        );
        btnNouvelleFerme.setDisable(true);

        VBox contenu = new VBox(24);
        contenu.setAlignment(Pos.CENTER);
        contenu.setPadding(new Insets(40));
        contenu.getChildren().addAll(titreSection, compteur, listeCartes, separateur, btnNouvelleFerme);

        return contenu;
    }
}
