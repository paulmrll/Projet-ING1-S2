package org.example;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javafx.stage.Stage;


public class WaterTankView {
    private Ground ground;
    public WaterTankView(Ground g){
        ground = g;
    }
    public Scene getScene(Stage stage, WaterTank w) {

        HBox topBar = SmartFarmUI.getTopBar(stage, "WaterTank n°"+w.getId());
        BorderPane main = new BorderPane();
        main.setCenter(uploadInfo(w, stage));
        main.setTop(topBar);

        Scene scene = new Scene(main, 1200, 700);
        return scene;
    }


    private VBox uploadInfo (WaterTank w, Stage stage) {
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(20);

        Label idLabel = new Label("WaterTank ID : " + w.getId());
        idLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        vbox.getChildren().add(idLabel);

        HBox coordinatesBox = new HBox();
        coordinatesBox.setAlignment(Pos.CENTER);
        coordinatesBox.setSpacing(20);

        Label xLabel = new Label("X : " + w.getX());
        Label yLabel = new Label("Y : " + w.getY());

        coordinatesBox.getChildren().addAll(xLabel, yLabel);
        vbox.getChildren().add(coordinatesBox);

        HBox dataBox = new HBox();
        dataBox.setAlignment(Pos.CENTER);
        dataBox.setSpacing(20);

        Label flowLabel = new Label("Flow : " + w.getFlow());
        Label capacityLabel = new Label("Capacity : " + w.getCapacity());

        dataBox.getChildren().addAll(flowLabel, capacityLabel);
        vbox.getChildren().add(dataBox);

        Button btnReturn = new Button("Return to the ground");
        btnReturn.setOnAction(e->{
            MapView mapView = new MapView(ground);
            stage.setScene(mapView.getScene(stage));
        });
        vbox.getChildren().addAll(btnReturn);

        return vbox;
    }
}
