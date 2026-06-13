package org.example;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * The {@code WaterTankView} class handles the graphical user interface display
 * for a specific water tank element within the application.
 * It extends {@link ElementView} to display technical details such as
 * coordinates, flow rate, capacity, and the flow-to-capacity ratio.
 * * @author SmartFarm Team
 * @version 1.0
 */
public class WaterTankView extends ElementView {

    /**
     * Constructs a new {@code WaterTankView} associated with the given terrain.
     *
     * @param g the {@link Ground} terrain object linked to this view.
     */
    public WaterTankView(Ground g) {
        super(g);
    }

    /**
     * Generates a vertical layout container (VBox) populated with detailed information
     * about a specific {@link WaterTank}. It calculates and displays the flow/capacity
     * ratio and provides a navigation button to return to the main map layout.
     *
     * @param p     the {@link Point} object to display, which is safely cast to a {@link WaterTank}.
     * @param stage the primary {@link Stage} of the application, used to handle scene switching.
     * @return a {@link VBox} containing labels, metrics, and interaction controls for the water tank.
     */
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