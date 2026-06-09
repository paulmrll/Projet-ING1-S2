package org.example;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javafx.stage.Stage;


public class SprinklerView extends ElementView{
    public SprinklerView(Ground g){
        super(g);
    }



    protected VBox uploadInfo (Point p, Stage stage) {
        Sprinkler s = (Sprinkler) p;
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(20);

        Label idLabel = new Label(" ID : " + s.getId());
        idLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        vbox.getChildren().add(idLabel);

        HBox coordinatesBox = new HBox();
        coordinatesBox.setAlignment(Pos.CENTER);
        coordinatesBox.setSpacing(20);

        Label xLabel = new Label("X : " + s.getX());
        Label yLabel = new Label("Y : " + s.getY());

        coordinatesBox.getChildren().addAll(xLabel, yLabel);
        vbox.getChildren().add(coordinatesBox);

        HBox dataBox = new HBox();
        dataBox.setAlignment(Pos.CENTER);
        dataBox.setSpacing(20);

        Label flowLabel = new Label("Flow : " + s.getFlow());
        vbox.getChildren().add(flowLabel);

        Button btnReturn = new Button("Return to the ground");
        btnReturn.setOnAction(e->{
            MapView mapView = new MapView(super.getGround());
            stage.setScene(mapView.getScene(stage));
        });
        vbox.getChildren().addAll(btnReturn);

        return vbox;
    }
}
