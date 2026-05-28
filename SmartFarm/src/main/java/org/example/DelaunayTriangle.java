package org.example;

import java.util.Arrays;
import java.util.Objects;

/**
 * Represents a Delaunay triangle.
 * 
 * @author Tom LEMENAND
 * @version 1.0
 */
public class DelaunayTriangle {

    private WaterTank[] vertices;
    private Point circumcenter;
    private double circumRadius;

    /**
     * Constructs a new DelaunayTriangle with three water tank vertices.
     * 
     * @param vertex1 the first water tank vertex
     * @param vertex2 the second water tank vertex
     * @param vertex3 the third water tank vertex
     */
    public DelaunayTriangle(WaterTank vertex1, WaterTank vertex2, WaterTank vertex3) {
        this.vertices = new WaterTank[]{vertex1, vertex2, vertex3};
        calculateCircumcircle();
    }

    /**
     * Gets the array of vertices.
     * 
     * @return the vertices array
     */
    public WaterTank[] getVertices() {
        return vertices;
    }

    /**
     * Gets the circumcenter.
     * 
     * @return the circumcenter point
     */
    public Point getCircumcenter() {
        return circumcenter;
    }

    /**
     * Sets the circumcenter.
     * 
     * @param circumcenter the circumcenter point
     */
    public void setCircumcenter(Point circumcenter) {
        this.circumcenter = circumcenter;
    }

    /**
     * Gets the circumradius.
     * 
     * @return the circumradius
     */
    public double getCircumRadius() {
        return circumRadius;
    }

    /**
     * Sets the circumradius.
     * 
     * @param circumRadius the circumradius value
     */
    public void setCircumRadius(double circumRadius) {
        this.circumRadius = circumRadius;
    }

    /**
     * Compares two DelaunayTriangle objects based on their vertices.
     * 
     * @param o the object to compare
     * @return true if both triangles have the same vertices
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DelaunayTriangle that = (DelaunayTriangle) o;
        return Arrays.equals(vertices, that.vertices);
    }

    /**
     * Returns the hash code based on the vertices array.
     * 
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(vertices);
    }

    /**
     * Calculates the circumcircle (circumcenter and circumradius) of the triangle.
     * The circumcenter is the point equidistant from all three vertices.
     * Uses the mathematical formulas derived from solving the system of distance equations.
     */
    public void calculateCircumcircle(){
        // STEP 1: Extract coordinate from the three vertces
        double x1 = vertices[0].getX();
        double y1 = vertices[0].getY();
        double x2 = vertices[1].getX();
        double y2 = vertices[1].getY();
        double x3 = vertices[2].getX();
        double y3 = vertices[2].getY();

        // STEP 2: Calculate the determinant (d) using Cramer's rule
        // This value is used to solve the linear system for the circumcenter
        double d = 2 * (x1 * (y2 - y3) + x2 * (y3 - y1) + x3 * (y1 - y2));

        // STEP 3: Check if the three points are collinear (d ≈ 0)
        // If d is nearly zero, the points are on the same line and cannot form a valid triangle
        if(Math.abs(d) < 1e-10){
            // Set the circumcenter to the centroid of the triagle
            this.circumcenter = new Point((x1 + x2 + x3) / 3, (y1 + y2 + y3) /3);
            // Set circumradius to infinity since there's no valid circle
            this.circumRadius = Double.MAX_VALUE;
            return;
        }

        // STEP 4: Calulate the x-coordinate of the circumcenter (ux)
        // Using the formula derived from the system of linear equations
        double ux = ((x1*x1 + y1*y1) * (y2 - y3) + (x2*x2 + y2*y2) * (y3 - y1) + (x3*x3 + y3*y3) * (y1 - y2)) / d;
    
        // STEP 5: Calculate the y-coordnate of the circumcenter (uy)
        // Using the formula derived from the system of linear equations
        double uy = ((x1*x1 + y1*y1) * (x3 - x2) + (x2*x2 + y2*y2) * (x1 - x3) + (x3*x3 + y3*y3) * (x2 - x1)) / d;

        // STEP 6: Create the circumcenter point with calculated coordinates
        this.circumcenter = new Point(ux, uy);

        // STEP 7: Calculate the circumradius (distnce form circumcenter to any vertex)
        // Using the Euclidean distance formula
        this.circumRadius = Math.sqrt((ux - x1) * (ux - x1) + (uy - y1) * (uy - y1));
    }

    /**
     * Checks whether a given water tank lies inside the circumcircle of this triangle.
     *
     * @param point the water tank to test
     * @return {@code true} if the point is inside the circumcircle (with numerical tolerance)
     */
    public boolean isPointInCircumcircle(WaterTank point) {
        if (circumcenter == null) return false;
        double dx = point.getX() - circumcenter.getX();
        double dy = point.getY() - circumcenter.getY();
        return (dx * dx + dy * dy) < (circumRadius * circumRadius + 1e-10);
    }

}
