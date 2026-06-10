package org.example;

import java.util.Objects;

/**
 * Represents a specific point in a coordinate system, characterized by its
 * x and y coordinates.
 *
 * @author Paul MORILLE, Oscar LUIGGI
 * @version 1.0
 */
public class Point {

    /**
     * The horizontal X coordinate.
     */
    private double x;

    /**
     * The vertical Y coordinate.
     */
    private double y;

    /**
     * Constructs a new {@code Point} with the specified x and y coordinates.
     *
     * @param x the X-coordinate of the point
     * @param y the Y-coordinate of the point
     */
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
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
     * Sets the horizontal X coordinate of this point.
     *
     * @param x the new X coordinate to set
     */
    public void setX(double x) {
        if (x >= 0){
            this.x = x;
        }
    }

    /**
     * Computes the Euclidean distance between this point and another point.
     *
     * @param other the other point
     * @return the distance between the two points
     */
    public double distanceTo(Point other) {
        double dx = this.x - other.getX();
        double dy = this.y - other.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Sets the vertical Y coordinate of this point.
     *
     * @param y the new Y coordinate to set
     */
    public void setY(double y) {
        if (y >= 0) {
            this.y = y;
        }
    }

    /**
     * Compares this point to the specified object for equality.
     * Two points are considered equal if they share the same x and y coordinates.
     *
     * @param O the object to compare with this point
     * @return {@code true} if the objects are equal; {@code false} otherwise
     */
    @Override
    public boolean equals(Object O) {
        if (O instanceof Point p) {
            if (p.getX() == this.x && p.getY() == this.y) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a string representation of this point.
     * The string includes the class name and its x and y coordinates.
     *
     * @return a string representation of this point
     */
    @Override
    public String toString() {
        return "Point{pos=(" + x + ", " + y + ")}";
    }

    /**
     * Returns a hash code value for this point.
     * The hash code is generated based on the point's x and y coordinates.
     *
     * @return a hash code value for this point
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}