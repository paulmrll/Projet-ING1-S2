package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class representing an agricultural ground.
 * A ground has an area and contains fields, water tanks and sprinkler systems
 * for intelligent irrigation management.
 * 
 * @author Tom LEMENAND
 * @version 1.0
 */
public class Ground {
    /**
     * The area of the ground in square units (typically in m²).
     */
    private double area;
    
    /**
     * List of fields present on the ground.
     */
    private List<Field> fields;
    
    /**
     * List of water tanks available on the ground.
     */
    private List<WaterTank> tanks;
    
    /**
     * List of sprinkler systems installed on the ground.
     */
    private List<Sprinkler> sprinklers;

    /**
     * Constructor for the Ground class.
     * Initializes a new ground with a given area and creates empty lists
     * for fields, tanks and sprinklers.
     * 
     * @param area the area of the ground in square units
     */
    public Ground(double area) {
        this.area = area;
        this.fields = new ArrayList<>();
        this.tanks = new ArrayList<>();
        this.sprinklers = new ArrayList<>();
    }

    /**
     * Gets the area of the ground.
     * 
     * @return the area of the ground
     */
    public double getArea() {
        return area;
    }

    /**
     * Gets the list of all fields on the ground.
     * 
     * @return the list of fields
     */
    public List<Field> getFields() {
        return fields;
    }

    /**
     * Gets the list of all water tanks on the ground.
     * 
     * @return the list of tanks
     */
    public List<WaterTank> getTanks() {
        return tanks;
    }

    /**
     * Gets the list of all sprinkler systems on the ground.
     * 
     * @return the list of sprinklers
     */
    public List<Sprinkler> getSprinklers() {
        return sprinklers;
    }

    /**
     * Sets the area of the ground.
     * 
     * @param area the new area of the ground
     */
    public void setArea(double area) {
        this.area = area;
    }

    /**
     * Adds a new field to the ground.
     * 
     * @param field the field to add
     */
    public void addField(Field field) {
        fields.add(field);
    }

    /**
     * Adds a new water tank to the ground.
     * 
     * @param tank the tank to add
     */
    public void addTank(WaterTank tank) {
        tanks.add(tank);
    }

    /**
     * Adds a new sprinkler system to the ground.
     * 
     * @param sprinkler the sprinkler to add
     */
    public void addSprinkler(Sprinkler sprinkler) {
        sprinklers.add(sprinkler);
    }

    /**
     * Returns a string representation of the ground.
     * 
     * @return a string containing the main information of the ground
     */
    @Override
    public String toString() {
        return "Ground{" +
                "area=" + area +
                ", fields=" + fields.size() +
                ", tanks=" + tanks.size() +
                ", sprinklers=" + sprinklers.size() +
                '}';
    }

    /**
     * Compares two Ground objects based on their area.
     * 
     * @param o the object to compare
     * @return true if both objects have the same area
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ground ground = (Ground) o;
        return ground.area == area;
    }

    /**
     * Returns the hash code based on the area.
     * 
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(area);
    }
}
