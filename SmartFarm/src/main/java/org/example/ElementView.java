package org.example;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * The {@code ElementView} class serves as an abstract base for specialized view components
 * that display technical data and properties of a specific element (like tools, sensors,
 * or tanks) on the map.
 * It establishes a mandatory connection with the underlying {@link Ground} terrain object.
 *
 * @author SmartFarm Team
 * @version 1.0
 */
public abstract class ElementView {

    /** The underlying ground terrain instance associated with this view component. */
    private final Ground ground;

    /**
     * Constructs a new {@code ElementView} bound to the specified {@link Ground} instance.
     *
     * @param g the {@link Ground} layout architecture representing the farm terrain.
     */
    public ElementView(Ground g) {
        ground = g;
    }

    /**
     * Abstract factory method intended to process a specific interactive point data
     * model and render its associated contextual controls and descriptive metrics
     * into a JavaFX graphical container.
     *
     * @param p     the specific target {@link Point} model to fetch and display.
     * @param stage the active primary {@link Stage} context used for navigation routines.
     * @return a JavaFX {@link Node} layout hierarchy populated with the target item's technical parameters.
     */
    protected abstract Node uploadInfo(Point p, Stage stage);

    /**
     * Retrieves the {@link Ground} layout object attached to this view container.
     *
     * @return the active {@link Ground} instance tracking data changes.
     */
    public Ground getGround() {
        return ground;
    }
}
