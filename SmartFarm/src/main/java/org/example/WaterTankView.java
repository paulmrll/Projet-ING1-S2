package org.example;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javafx.stage.Stage;


public class WaterTankView extends ElementView {
    public WaterTankView(Ground g) {
        super(g);
    }


    protected VBox uploadInfo(Point p, Stage stage) {
        WaterTank w = (WaterTank) p;
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

        double div = w.getFlow() / w.getCapacity();
        String result = String.format("%.2f", div);
        Label rapport = new Label("Rapport : " + w.getFlow() + "/" + w.getCapacity() + "=" + result + "%");
        vbox.getChildren().add(rapport);

        Button btnReturn = new Button("Return to the ground");
        btnReturn.setOnAction(e -> {
            MapView mapView = new MapView(super.getGround());
            stage.setScene(mapView.getScene(stage));
        });
        vbox.getChildren().addAll(btnReturn);

        return vbox;
    }
}
