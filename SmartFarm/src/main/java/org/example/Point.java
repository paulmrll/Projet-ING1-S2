package org.example;

import java.util.Objects;

/**
 * Represents a specific point in a coordinate system, characterized by its
 * coordinates, a unique identifier, an internal type, and a state.
 * * @author Paul MORILLE
 *
 * @version 1.0
 */
public class Point {


    /**
     * The geographic or grid coordinates of this point.
     */
    private final Coordinates coordinates;

    /**
     * The unique identifier of this specific point instance.
     */
    private final int id;

    /**
     * Counter used to generate unique identifiers for new points.
     */
    private static int nbId;

    /**
     * Constructs a new {@code Point} with the specified coordinates.
     * Automatically assigns a unique ID to the point.
     *
     * @param x the X-coordinate of the point
     * @param y the Y-coordinate of the point
     */
    public Point(int x, int y) {
        coordinates = new Coordinates(x, y);
        id = nbId;
        nbId++;
    }

    /**
     * Gets the unique identifier of this point.
     *
     * @return the point's unique ID
     */
    public int getId() {
        return this.id;
    }

    /**
     * Compares this point to the specified object for equality.
     * Two points are considered equal if they share both the same ID
     * and the same coordinates.
     *
     * @param O the object to compare with this point
     * @return {@code true} if the objects are equal; {@code false} otherwise
     */
    @Override
    public boolean equals(Object O) {
        if (O instanceof Point p) {
            if (p.getId() == this.id && p.coordinates.equals(this.coordinates)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a string representation of this point.
     * The string includes the class name, its coordinates, and its unique ID.
     *
     * @return a string representation of this point
     */
    @Override
    public String toString() {
        return "Point{" + "coordinates=" + coordinates + ", id=" + id + '}';
    }

    /**
     * Returns a hash code value for this point.
     * The hash code is generated based on the point's ID and coordinates.
     *
     * @return a hash code value for this point
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, coordinates);
    }
}