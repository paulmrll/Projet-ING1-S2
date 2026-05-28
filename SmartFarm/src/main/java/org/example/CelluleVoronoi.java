package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a Voronoi cell.
 * 
 * @author SmartFarm Project
 * @version 1.0
 */
public class CelluleVoronoi {

    private WaterTank reservoir;
    private List<Point> vertices;
    private double surface;
    private String state;

    /**
     * Constructs a new CelluleVoronoi associated with a water tank reservoir.
     * 
     * @param reservoir the water tank reservoir for this cell
     */
    public CelluleVoronoi(WaterTank reservoir) {
        this.reservoir = reservoir;
        this.vertices = new ArrayList<>();
        this.surface = 0.0;
        this.state = "active";
    }

    /**
     * Gets the water tank reservoir.
     * 
     * @return the reservoir
     */
    public WaterTank getReservoir() {
        return reservoir;
    }

    /**
     * Gets the list of vertices.
     * 
     * @return the vertices list
     */
    public List<Point> getVertices() {
        return vertices;
    }

    /**
     * Gets the surface area.
     * 
     * @return the surface area
     */
    public double getSurface() {
        return surface;
    }

    /**
     * Sets the surface area.
     * 
     * @param surface the surface area value
     */
    public void setSurface(double surface) {
        this.surface = surface;
    }

    /**
     * Gets the state.
     * 
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the state.
     * 
     * @param state the state value
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Compares two CelluleVoronoi objects based on their associated reservoir.
     * 
     * @param o the object to compare
     * @return true if both cells are associated with the same reservoir
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CelluleVoronoi that = (CelluleVoronoi) o;
        return Objects.equals(reservoir, that.reservoir);
    }

    /**
     * Returns the hash code based on the associated reservoir.
     * 
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(reservoir);
    }

}
