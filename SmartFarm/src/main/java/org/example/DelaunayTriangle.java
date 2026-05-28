package org.example;

/**
 * Represents a Delaunay triangle.
 * 
 * @author SmartFarm Project
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
}
