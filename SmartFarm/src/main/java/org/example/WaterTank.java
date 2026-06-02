package org.example;

import java.util.Objects;

/**
 * Represents a water tank positioned at specific coordinates.
 * A water tank has a maximum capacity, a current flow rate and an active status.
 * It extends the {@link Point} class to inherit spatial coordinates.
 * @author Paul MORILLE, Oscar LUIGGI
 * @version 1.0
 */
public class WaterTank extends Point {

    /** The maximum capacity of the water tank. */
    private double capacity;

    /** The current water flow rate of the tank. */
    private double flow;

    /** Specifies whether the water tank is currently active. */
    private boolean active;

    /**
     * Constructs a new WaterTank with specified coordinates, capacity, and flow.
     * The capacity must be strictly greater than 0 for the fields to be initialized.
     *
     * @param x        the X coordinate of the tank
     * @param y        the Y coordinate of the tank
     * @param capacity the maximum capacity of the tank (must be > 0)
     * @param flow     the initial flow rate of the tank
     */
    public WaterTank(double x, double y, double capacity, double flow) {
        super(x, y);
        if (capacity > 0) {
            this.capacity = capacity;
            this.flow = flow;
        }
    }

    /**
     * Constructs a new WaterTank with specified coordinates and capacity.
     * The initial flow rate is set to 0 by default.
     *
     * @param x        the X coordinate of the tank
     * @param y        the Y coordinate of the tank
     * @param capacity the maximum capacity of the tank
     */
    public WaterTank(double x, double y, double capacity) {
        this(x, y, capacity, 0);
    }

    /**
     * Returns the maximum capacity of this water tank.
     *
     * @return the capacity of the tank
     */
    public double getCapacity() {
        return capacity;
    }

    /**
     * Checks whether this water tank is active.
     *
     * @return true if the tank is active, false otherwise
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active status of this water tank.
     *
     * @param active the new active status to set
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Returns the current flow rate of this water tank.
     *
     * @return the flow rate of the tank
     */
    public double getFlow() {
        return flow;
    }

    /**
     * Sets the current flow rate of this water tank.
     * If the provided value is negative, the flow rate is set to 0.
     *
     * @param flow the new flow rate to assign to this water tank
     */
    public void setFlow(double flow) {
        if (flow >= 0.0) {
            this.flow = flow;
        }else{
            this.flow = 0.0;
        }
    }

    /**
     * Checks whether this water tank is empty.
     *
     * @return true if the flow rate is 0, false otherwise
     */
    public boolean isEmpty() {
        return flow == 0.0;
    }

    /**
     * Refills the tank by resetting the flow to its maximum capacity.
     */
    public void refill() {
        this.flow = this.capacity;
    }

    /**
     * Returns a string representation of the water tank.
     * The string contains the capacity, flow, active status,
     * and the superclass data, each separated by a newline character.
     *
     * @return a string describing this water tank
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("capacity = ").append(capacity);
        sb.append(" , flow = ").append(flow);
        sb.append(", active = ").append(active).append(" ");
        sb.append(super.toString());
        return sb.toString();
    }

    /**
     * Compares this water tank to the specified object for equality.
     * The result is true if and only if the argument is not null, is a WaterTank object,
     * and has the same active status, flow, capacity, and spatial coordinates
     * as this object.
     *
     * @param O the object to compare this WaterTank against
     * @return true if the given object is equivalent to this water tank, false otherwise
     */
    @Override
    public boolean equals(Object O) {
        if (O instanceof WaterTank w) {
            if (w.isActive() == active && w.getFlow() == flow && w.getCapacity() == capacity && super.equals(w)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a hash code value for this water tank based on its attributes
     * (active status, flow, capacity) and its superclass state.
     *
     * @return a hash code value for this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(active, flow, capacity, super.hashCode());
    }
}