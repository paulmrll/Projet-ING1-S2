package org.example;

import java.util.Objects;

/**
 * Represents a specific point in a coordinate system, characterized by its
 * x and y coordinates, a unique identifier, an internal type, and a state.
 *
 * @author Paul MORILLE, Oscar LUIGGI
 * @version 1.0
 */
public class Point {

    /**
     * The unique identifier of this specific point instance.
     */
    private final int id;

    /**
     * Counter used to generate unique identifiers for new points.
     */
    private static int nbId;

    /**
     * The horizontal X coordinate.
     */
    private final double x;

    /**
     * The vertical Y coordinate.
     */
    private final double y;

    /**
     * Constructs a new {@code Point} with the specified x and y coordinates.
     * Automatically assigns a unique ID to the point.
     *
     * @param x the X-coordinate of the point
     * @param y the Y-coordinate of the point
     */
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
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
     * Returns the horizontal X coordinate.
     *
     * @return the X coordinate
     */
    public double getX() {
        return x;
    }

    /**
     * Returns the vertical Y coordinate.
     *
     * @return the Y coordinate
     */
    public double getY() {
        return y;
    }

    /**
     * Compares this point to the specified object for equality.
     * Two points are considered equal if they share both the same ID
     * and the same x and y coordinates.
     *
     * @param O the object to compare with this point
     * @return {@code true} if the objects are equal; {@code false} otherwise
     */
    @Override
    public boolean equals(Object O) {
        if (O instanceof Point p) {
            if (p.getId() == this.id && p.getX() == this.x && p.getY() == this.y) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a string representation of this point.
     * The string includes the class name, its unique ID, and its x and y coordinates.
     *
     * @return a string representation of this point
     */
    @Override
    public String toString() {
        return "Point{" + "id=" + id + ", x=" + x + ", y=" + y + '}';
    }

    /**
     * Returns a hash code value for this point.
     * The hash code is generated based on the point's ID, x, and y coordinates.
     *
     * @return a hash code value for this point
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, x, y);
    }
}