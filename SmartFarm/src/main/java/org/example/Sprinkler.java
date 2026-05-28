package org.example;

import java.util.Objects;

/**
 * Represents an automatic sprinkler positioned at specific coordinates.
 * A sprinkler is characterized by its water flow rate, its final coverage radius,
 * and its operational status (active or inactive).
 * It extends the {@link Point} class to inherit spatial coordinates.
 *
 * @author Oscar LUIGGI
 * @version 1.0
 */
public class Sprinkler extends Point {

    /** The water flow rate of the sprinkler. */
    private double flow;

    /** The final coverage radius of the sprinkler. */
    private final double radius;

    /** Specifies whether the sprinkler is currently active. */
    private boolean active;

    /**
     * Constructs a new {@code Sprinkler} with the specified coordinates, flow rate,
     * coverage radius, and active status.
     * <p>
     * If the provided flow or radius is negative, they are automatically initialized to 0.
     * </p>
     *
     * @param x      the X-coordinate of the sprinkler
     * @param y      the Y-coordinate of the sprinkler
     * @param flow   the water flow rate of the sprinkler
     * @param radius the final coverage radius of the sprinkler
     * @param active the initial operational status of the sprinkler
     */
    public Sprinkler(double x, double y, double flow, double radius, boolean active) {
        super((int) x, (int) y); // Cast to int if your Point class requires integers
        if (flow >= 0 && radius >= 0) {
            this.flow = flow;
            this.radius = radius;
        } else {
            this.flow = 0;
            this.radius = 0;
        }
        this.active = active;
    }

    /**
     * Constructs a new {@code Sprinkler} with the specified coordinates, flow rate,
     * and coverage radius. The sprinkler is set to inactive ({@code false}) by default.
     *
     * @param x      the X-coordinate of the sprinkler
     * @param y      the Y-coordinate of the sprinkler
     * @param flow   the water flow rate of the sprinkler
     * @param radius the final coverage radius of the sprinkler
     */
    public Sprinkler(double x, double y, double flow, double radius) {
        this(x, y, flow, radius, false);
    }

    /**
     * Returns the current water flow rate of this sprinkler.
     *
     * @return the flow rate
     */
    public double getFlow() {
        return flow;
    }

    /**
     * Returns the final coverage radius of this sprinkler.
     *
     * @return the radius
     */
    public double getRadius() {
        return radius;
    }

    /**
     * Checks whether this sprinkler is currently active.
     *
     * @return {@code true} if the sprinkler is active; {@code false} otherwise
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the operational status of this sprinkler.
     *
     * @param active the new active status to set
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Updates the water flow rate of this sprinkler.
     * If the provided flow rate is negative, it is set to 0.
     *
     * @param flow the new flow rate to set
     */
    public void setFlow(double flow) {
        if (flow >= 0) {
            this.flow = flow;
        } else {
            this.flow = 0;
        }
    }

    /**
     * Returns a string representation of this sprinkler.
     * The string includes the superclass representation, followed by the flow rate
     * and the active status.
     *
     * @return a string describing this sprinkler
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append("flow = ").append(flow);
        sb.append(", active = ").append(active);
        return sb.toString();
    }

    /**
     * Compares this sprinkler to the specified object for equality.
     * Two sprinklers are considered equal if they share the same spatial coordinates (superclass equality)
     * as well as identical flow rate, radius, and active status.
     *
     * @param o the object to compare with this sprinkler
     * @return {@code true} if the objects are equal; {@code false} otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof Sprinkler) {
            Sprinkler s = (Sprinkler) o;
            if (!super.equals(o)) {
                return false;
            }
            if (flow == s.flow && radius == s.radius && active == s.active) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a hash code value for this sprinkler.
     * The hash code is generated based on its active status, flow rate, radius,
     * and the superclass state.
     *
     * @return a hash code value for this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(active, flow, radius, super.hashCode());
    }
}