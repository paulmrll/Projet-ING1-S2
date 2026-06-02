package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class representing an agricultural ground.
 * A ground has an area and contains fields, water tanks and sprinkler systems
 * for intelligent irrigation management.
 * 
 * @author Tom LEMENAND, Oscar LUIGGI
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
     * Removes a field from the ground.
     *
     * @param field the field to remove
     * @return true if the field was removed, false if it wasn't found
     */
    public boolean removeField(Field field) {
        return fields.remove(field);
    }

    /**
     * Removes a water tank from the ground.
     *
     * @param tank the tank to remove
     * @return true if the tank was removed, false if it wasn't found
     */
    public boolean removeTank(WaterTank tank) {
        return tanks.remove(tank);
    }

    /**
     * Removes a sprinkler from the ground.
     *
     * @param sprinkler the sprinkler to remove
     * @return true if the sprinkler was removed, false if it wasn't found
     */
    public boolean removeSprinkler(Sprinkler sprinkler) {
        return sprinklers.remove(sprinkler);
    }

    /**
     * Replaces an existing field with a new one.
     *
     * @param oldField the field to replace
     * @param newField the new field
     * @return true if the field was updated, false if oldField wasn't found
     */
    public boolean updateField(Field oldField, Field newField) {
        int index = fields.indexOf(oldField);
        if (index == -1) return false;
        fields.set(index, newField);
        return true;
    }

    /**
     * Replaces an existing water tank with a new one.
     *
     * @param oldTank the tank to replace
     * @param newTank the new tank
     * @return true if the tank was updated, false if oldTank wasn't found
     */
    public boolean updateTank(WaterTank oldTank, WaterTank newTank) {
        int index = tanks.indexOf(oldTank);
        if (index == -1) return false;
        tanks.set(index, newTank);
        return true;
    }

    /**
     * Replaces an existing sprinkler with a new one.
     *
     * @param oldSprinkler the sprinkler to replace
     * @param newSprinkler the new sprinkler
     * @return true if the sprinkler was updated, false if oldSprinkler wasn't found
     */
    public boolean updateSprinkler(Sprinkler oldSprinkler, Sprinkler newSprinkler) {
        int index = sprinklers.indexOf(oldSprinkler);
        if (index == -1) return false;
        sprinklers.set(index, newSprinkler);
        return true;
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
     * Compares two Ground objects based on their area, fields, tanks and sprinklers.
     * Two grounds are considered equal if they have the same area and identical
     * lists of fields, tanks and sprinklers.
     *
     * @param o the object to compare
     * @return true if both objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ground ground = (Ground) o;
        return Double.compare(ground.area, area) == 0
                && Objects.equals(fields, ground.fields)
                && Objects.equals(tanks, ground.tanks)
                && Objects.equals(sprinklers, ground.sprinklers);
    }

    /**
     * Returns a hash code value for this ground based on its area,
     * fields, tanks and sprinklers.
     *
     * @return a hash code value for this ground
     */
    @Override
    public int hashCode() {
        return Objects.hash(area, fields, tanks, sprinklers);
    }
}
