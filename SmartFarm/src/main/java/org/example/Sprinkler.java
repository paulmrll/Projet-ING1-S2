package org.example;

import java.util.Objects;

/**
 * Represents an automatic sprinkler positioned at specific coordinates.
 * A sprinkler is characterized by its water flow rate, its final coverage radius,
 * and its operational status (active or inactive).
 * It extends the {@link Point} class to inherit spatial coordinates.
 *
 * @author Oscar LUIGGI
 * @version 1.1
 */
public class Sprinkler extends Point {

    /**
     * The unique identifier of this specific Sprinkler.
     */
    private final int id;

    /**
     * Counter used to generate unique identifiers for new Sprinklers.
     */
    private static int nbId = 0;

    /** The water flow rate of the sprinkler. */
    private double flow;

    /** The final coverage radius of the sprinkler. */
    private final double radius;

    /** Specifies whether the sprinkler is currently active. */
    private boolean active;

    /** The water tank that supplies this sprinkler. */
    private WaterTank source;

    /**
     * Constructs a new {@code Sprinkler} with the specified coordinates, flow rate,
     * coverage radius, and active status.
     *
     * @param x      the X-coordinate of the sprinkler
     * @param y      the Y-coordinate of the sprinkler
     * @param flow   the water flow rate of the sprinkler (must be >= 0)
     * @param radius the final coverage radius of the sprinkler (must be >= 0)
     * @param active the initial operational status of the sprinkler
     * @throws IllegalArgumentException if flow or radius are negative
     */
    public Sprinkler(double x, double y, double flow, double radius, boolean active) {
        super(x, y);

        // Validation stricte comme dans WaterTank
        if (flow < 0) {
            throw new IllegalArgumentException("Le flux ne peut pas être négatif.");
        }
        if (radius < 0) {
            throw new IllegalArgumentException("Le rayon de couverture ne peut pas être négatif.");
        }

        this.flow = flow;
        this.radius = radius;
        this.active = active;

        // Attribution sécurisée de l'ID
        this.id = nbId;
        nbId++;
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
     * Gets the unique identifier of this Sprinkler.
     *
     * @return the Sprinkler's unique ID
     */
    public int getId() {
        return this.id;
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
        if (flow >= 0.0) {
            this.flow = flow;
        } else {
            this.flow = 0.0;
        }
    }

    /**
     * Returns the water tank supplying this sprinkler.
     *
     * @return the source WaterTank, or null if not assigned
     */
    public WaterTank getSource() {
        return source;
    }

    /**
     * Sets the water tank that supplies this sprinkler.
     *
     * @param source the WaterTank to assign as source
     */
    public void setSource(WaterTank source) {
        this.source = source;
    }

    /**
     * Activates the sprinkler if its source tank has enough water.
     * Consumes water from the source tank equal to the sprinkler's flow rate.
     *
     * @return true if the sprinkler was activated, false if no source or tank is empty
     */
    public boolean activate() {
        if (source == null || source.isEmpty() || source.getFlow() < flow){
            return false;
        }

        source.setFlow(source.getFlow() - flow);
        this.active = true;
        return true;
    }

    /**
     * Deactivates the sprinkler.
     */
    public void deactivate() {
        this.active = false;
    }

    /**
     * Returns a string representation of this sprinkler.
     *
     * @return a string describing this sprinkler
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Sprinkler{");
        sb.append("id=").append(getId());
        sb.append(", pos=(").append(String.format("%.1f", getX()));
        sb.append(", ").append(String.format("%.1f", getY())).append(")");
        sb.append(", flow=").append(String.format("%.1f", flow));
        sb.append(", radius=").append(String.format("%.1f", radius));
        sb.append(", active=").append(active);
        sb.append("}");
        return sb.toString();
    }

    /**
     * Compares this sprinkler to the specified object for equality.
     * Two sprinklers are considered equal if they have the same unique identifier,
     * flow, radius, active status, and spatial coordinates.
     *
     * @param o the object to compare with this sprinkler
     * @return {@code true} if the objects are equal; {@code false} otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof Sprinkler s) {
            if (s.getId() == getId() &&
                    Double.compare(s.getFlow(), getFlow()) == 0 &&
                    Double.compare(s.getRadius(), getRadius()) == 0 &&
                    s.isActive() == isActive() &&
                    super.equals(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a hash code value for this sprinkler.
     *
     * @return a hash code value for this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, flow, radius, active);
    }
}