package org.example;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

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
        HBox topBar = SmartFarmUI.getTopBar(stage, "Save at the path "+ currentFolder);
        BorderPane mainLayout = new BorderPane();
        mainLayout.setTop(topBar);
        mainLayout.setCenter(chargerFichiers(currentFolder, stage));

        Scene scene = new Scene(mainLayout, 1200, 700);
        return scene;
    }

    
    private VBox chargerFichiers(Path dossier, Stage stage) {
        VBox files = new VBox();

        File currentFolder = new File(dossier.toUri());

        File[] filesList = currentFolder.listFiles();

        if (filesList != null) {
            for (File f : filesList) {
                if (f.isFile()) {
                    Button button = new Button(f.getName());
                    button.setStyle(
                            "-fx-background-color : #3a5a30;" + "-fx-text-fill : white;" + "-fx-background-radius : 6;"
                    );
                    button.setPrefSize(200, 50);
                    button.setOnAction(e->{
                        Save save = new Save(f.getName());
                        try {
                            MapView mapView = new MapView(save.readSave());
                            Scene mapScene = mapView.getScene(stage);
                            stage.setScene(mapScene);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    });
                    files.getChildren().add(button);
                }
            }
        }
        files.setAlignment(Pos.CENTER);
        files.setSpacing(30);
        return files;
    }
}
