package org.example;

import java.util.Arrays;
import java.util.Objects;

/**
 * Represents a geographical or agricultural field defined by coordinate boundaries,
 * an area, a name, and a collection of water plants.
 */
public class Field {
    private Point[] waterPlants;
    private final int xStart;
    private final int xStop;
    private final int yStart;
    private final int yStop;
    private String name;
    private float area;

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
    public Field(String name, int xStart, int xStop, int yStart, int yStop, float area){
        this.name = name;
        this.xStart = xStart;
        this.xStop = xStop;
        this.yStart = yStart;
        this.yStop = yStop;
        this.area = area;
    }

    /**
     * Returns the name of the field.
     *
     * @return the field name
     */
    public String getName(){
        return name;
    }

    /**
     * Returns the starting X-coordinate boundary of the field.
     *
     * @return the starting X-coordinate
     */
    public int getxStart(){
        return xStart;
    }

    /**
     * Returns the ending X-coordinate boundary of the field.
     *
     * @return the ending X-coordinate
     */
    public int getxStop(){
        return xStop;
    }

    /**
     * Returns the starting Y-coordinate boundary of the field.
     *
     * @return the starting Y-coordinate
     */
    public int getyStart(){
        return yStart;
    }

    /**
     * Returns the ending Y-coordinate boundary of the field.
     *
     * @return the ending Y-coordinate
     */
    public int getyStop(){
        return yStop;
    }

    /**
     * Returns the total area of the field.
     *
     * @return the field area
     */
    public float getArea(){
        return area;
    }

    /**
     * Returns the array of water plants located in this field.
     *
     * @return an array of {@link Point} objects representing the water plants
     */
    public Point[] getWaterPlants(){
        return this.waterPlants;
    }

    /**
     * Compares this field to the specified object for equality.
     * Two fields are considered equal if they have the same name, coordinates,
     * area, and identical water plant elements in the exact same order.
     * Note: This implementation may throw a {@code NullPointerException} if {@code waterPlants}
     * is null on either object.
     *
     * @param O the object to compare with this field
     * @return {@code true} if the specified object is equal to this field; {@code false} otherwise
     */
    public boolean equals(Object O){
        if (O instanceof Field f){
            if (this.name.equals(f.getName())){
                if (xStop == f.getxStop() && xStart == f.getxStart() && yStart == f.getyStart() && yStop == f.getyStop() && f.getArea() == area){
                    Point [] waterPlantsF = f.getWaterPlants();
                    for (int i = 0; i < this.waterPlants.length; i++){
                        if (!waterPlants[i].equals(waterPlantsF[i])){
                            return false;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Returns a hash code value for this field based on its water plants,
     * name, and coordinate boundaries.
     *
     * @return a hash code value for this field
     */
    @Override
    public int hashCode() {
        return Objects.hash(Arrays.deepHashCode(waterPlants), name, xStart, xStop, yStart, yStop);
    }

    /**
     * Returns a string representation of the field's water plants.
     * Each plant's string representation is followed by a new line character.
     *
     * @return a string describing the water plants in this field
     */
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < waterPlants.length; i++){
            sb.append(waterPlants[i].toString());
            sb.append("\n");
        }
        return sb.toString();
    }
}