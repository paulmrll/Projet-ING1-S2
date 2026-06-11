package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a geographical or agricultural field defined by coordinate boundaries,
 * an area, a name, and a collection of water plants.
 * @author Paul MORILLE
 * @version 1.0
 */
public class Field {
    private List<Point> waterPlants;
    private final double xStart;
    private final double xStop;
    private final double yStart;
    private final double yStop;
    private String name;
    private double area;

    /**
     * Constructs a new Field with the specified name, coordinate boundaries, and area.
     *
     * @param name   the name of the field
     * @param xStart the starting X-coordinate boundary
     * @param xStop  the ending X-coordinate boundary
     * @param yStart the starting Y-coordinate boundary
     * @param yStop  the ending Y-coordinate boundary
     * @param area   the total area of the field
     */
    public Field(String name, double xStart, double xStop, double yStart, double yStop, double area) {
        this.name = name;
        this.xStart = xStart;
        this.xStop = xStop;
        this.yStart = yStart;
        this.yStop = yStop;
        this.area = area;
        this.waterPlants = new ArrayList<>();
    }
    public Field(String name, double xStart, double xStop, double yStart, double yStop) {
        this.name = name;
        this.xStart = xStart;
        this.xStop = xStop;
        this.yStart = yStart;
        this.yStop = yStop;
        this.area = (xStop-xStart)*(yStop-yStart);
        this.waterPlants = new ArrayList<>();
    }

    /**
     * Returns the name of the field.
     *
     * @return the field name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the field.
     *
     * @param name the new name to assign to this field
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the starting X-coordinate boundary of the field.
     *
     * @return the starting X-coordinate
     */
    public double getxStart() {
        return xStart;
    }

    /**
     * Returns the ending X-coordinate boundary of the field.
     *
     * @return the ending X-coordinate
     */
    public double getxStop() {
        return xStop;
    }

    /**
     * Returns the starting Y-coordinate boundary of the field.
     *
     * @return the starting Y-coordinate
     */
    public double getyStart() {
        return yStart;
    }

    /**
     * Returns the ending Y-coordinate boundary of the field.
     *
     * @return the ending Y-coordinate
     */
    public double getyStop() {
        return yStop;
    }

    /**
     * Returns the total area of the field.
     *
     * @return the field area
     */
    public double getArea() {
        return area;
    }

    /**
     * Returns the list of water plants located in this field.
     *
     * @return the list of water plants
     */
    public List<Point> getWaterPlants() {
        return waterPlants;
    }

    /**
     * Adds a water plant to the field.
     *
     * @param plant the water plant to add
     */
    public void addWaterPlant(Point plant) {
        waterPlants.add(plant);
    }

    /**
     * Removes a water plant from the field.
     *
     * @param plant the water plant to remove
     * @return true if the plant was removed, false if it wasn't found
     */
    public boolean removeWaterPlant(Point plant) {
        return waterPlants.remove(plant);
    }

    /**
     * Replaces an existing water plant with a new one.
     *
     * @param oldPlant the plant to replace
     * @param newPlant the new plant
     * @return true if the plant was updated, false if oldPlant wasn't found
     */
    public boolean updateWaterPlant(Point oldPlant, Point newPlant) {
        int index = waterPlants.indexOf(oldPlant);
        if (index == -1) return false;
        waterPlants.set(index, newPlant);
        return true;
    }

    /**
     * Compares this field to the specified object for equality.
     * Two fields are considered equal if they have the same name, coordinates,
     * area, and identical water plants.
     *
     * @param O the object to compare with this field
     * @return true if the specified object is equal to this field, false otherwise
     */
    @Override
    public boolean equals(Object O) {
        if (this == O) return true;
        if (!(O instanceof Field f)) return false;
        return Double.compare(xStart, f.xStart) == 0
                && Double.compare(xStop, f.xStop) == 0
                && Double.compare(yStart, f.yStart) == 0
                && Double.compare(yStop, f.yStop) == 0
                && Double.compare(area, f.area) == 0
                && Objects.equals(name, f.name)
                && Objects.equals(waterPlants, f.waterPlants);
    }

    /**
     * Returns a hash code value for this field based on its water plants,
     * name, coordinate boundaries and area.
     *
     * @return a hash code value for this field
     */
    @Override
    public int hashCode() {
        return Objects.hash(waterPlants, name, xStart, xStop, yStart, yStop, area);
    }

    /**
     * Returns a string representation of the field.
     *
     * @return a string describing the field and its water plants
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Field{name=").append(name)
          .append(", area=").append(area)
          .append(", plants=").append(waterPlants.size()).append("}\n");
        for (Point waterPlant : waterPlants) {
            sb.append("  ").append(waterPlant.toString()).append("\n");
        }
        return sb.toString();
    }

    /**
     * Checks whether a given point is within the boundaries of this field.
     *
     * @param p the point to check
     * @return true if the point is inside the field, false otherwise
     */
    public boolean contains(Point p) {
        return p.getX() >= this.xStart && p.getX() <= this.xStop
                && p.getY() >= this.yStart && p.getY() <= this.yStop;
    }
}
