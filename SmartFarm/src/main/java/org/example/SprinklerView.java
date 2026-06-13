package org.example;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * The {@code SprinklerView} class manages the graphical user interface display
 * for a specific sprinkler element within the application.
 * It extends {@link ElementView} to present technical details such as
 * the sprinkler's unique ID, coordinates, and current flow rate.
 *
 * @author SmartFarm Team
 * @version 1.0
 */
public class SprinklerView extends ElementView {

    /**
     * Constructs a new {@code SprinklerView} associated with the given terrain.
     *
     * @param g the {@link Ground} terrain object linked to this view.
     */
    public SprinklerView(Ground g) {
        super(g);
    }

    /**
     * Generates a vertical layout container (VBox) populated with detailed information
     * about a specific {@link Sprinkler}. It displays the equipment's location data,
     * operational flow, and provides a navigation button to return to the main map layout.
     *
     * @param p     the {@link Point} object to display, which is safely cast to a {@link Sprinkler}.
     * @param stage the primary {@link Stage} of the application, used to handle scene switching.
     * @return a {@link VBox} containing labels, metrics, and interaction controls for the sprinkler.
     */
    protected VBox uploadInfo(Point p, Stage stage) {
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
        btnReturn.setOnAction(e -> {
            MapView mapView = new MapView(super.getGround());
            stage.setScene(mapView.getScene(stage));
        });
        vbox.getChildren().addAll(btnReturn);

        return vbox;
    }
}