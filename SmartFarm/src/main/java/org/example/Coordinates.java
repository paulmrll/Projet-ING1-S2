package org.example;

import java.util.Objects;

/**
 * Represents a two-dimensional coordinate point with immutable X and Y coordinates.
 * <p>
 * This class is typically used to define a specific position in a 2D space.
 * </p>
 *
 * @author YourName
 * @version 1.0
 */
public class Coordinates {

    /** The horizontal X coordinate. */
    private final int x;

    /** The vertical Y coordinate. */
    private final int y;

    /**
     * Constructs a new {@code Coordinates} instance with the specified X and Y values.
     *
     * @param x the horizontal coordinate
     * @param y the vertical coordinate
     */
    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the horizontal X coordinate.
     *
     * @return the X coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the vertical Y coordinate.
     *
     * @return the Y coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * Compares this coordinate object to the specified object for equality.
     * <p>
     * The result is {@code true} if and only if the argument is not {@code null},
     * is an instance of {@code Coordinates}, and has the exact same X and Y values
     * as this object.
     * </p>
     *
     * @param o the object to compare with
     * @return {@code true} if the objects are equal; {@code false} otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof Coordinates c) {
            if (c.getX() == this.x && c.getY() == this.y) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a hash code value for this coordinate object.
     * <p>
     * This method is overridden to maintain the general contract of the
     * {@link Object#hashCode()} method, ensuring that equal coordinates
     * produce identical hash codes based on their X and Y values.
     * </p>
     *
     * @return a hash code value for this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    /**
     * Returns a string representation of these coordinates.
     * <p>
     * The format consists of the X coordinate, followed by a newline character,
     * and then the Y coordinate.
     * </p>
     *
     * @return a string representation of the coordinates
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(x);
        sb.append("\n");
        sb.append(y);
        return sb.toString();
    }
}