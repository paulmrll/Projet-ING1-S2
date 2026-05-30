package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a Voronoi cell.
 * 
 * @author Tom LEMENAND
 * @version 1.0
 */
public class CelluleVoronoi {
    /** The waterTank of the voronoi cell */
    private WaterTank reservoir;
    /** List of the vertices of the cell */
    private List<Point> vertices;
    private double area;

    /**
     * Constructs a new CelluleVoronoi associated with a water tank reservoir.
     * 
     * @param reservoir the water tank reservoir for this cell
     */
    public CelluleVoronoi(WaterTank reservoir) {
        this.reservoir = reservoir;
        this.vertices = new ArrayList<>();
        this.area = 0.0;
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
     * Gets the area.
     *
     * @return the area
     */
    public double getArea() {
        return area;
    }

    /**
     * Sets the area.
     *
     * @param area the area value
     */
    public void setArea(double area) {
        this.area = area;
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
