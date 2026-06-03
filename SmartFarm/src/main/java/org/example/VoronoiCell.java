package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a Voronoi cell associated with a water tank tank.
 * <p>
 * A Voronoi cell is a convex polygon whose vertices are the circumcenters
 * of the adjacent Delaunay triangles. Any point inside this cell is closer
 * to its tank than to any other tank in the diagram.
 * </p>
 *
 * @author Tom LEMENAND, Oscar LUIGGI
 * @version 1.1
 */
public class VoronoiCell {
    /** The WaterTank tank associated with this Voronoi cell. */
    private WaterTank tank;

    /** The list of points representing the vertices of the cell's polygon. */
    private List<Point> vertices;

    /** The list of neighboring Voronoi cells adjacent to this cell. */
    private List<VoronoiCell> neighbors;

    /**
     * Constructs a new {@code VoronoiCell} associated with a specific water tank tank
     * and defined by its vertices.
     *
     * @param tank the water tank that acts as the site (center) of this Voronoi cell
     * @param vertices  the list of points representing the vertices of the cell's polygon
     */
    public VoronoiCell(WaterTank tank, List<Point> vertices) {
        this.tank = tank;
        this.vertices = vertices;
        this.neighbors = new ArrayList<>();
    }

    /**
     * Returns the water tank tank associated with this cell.
     *
     * @return the {@link WaterTank} acting as the site of this cell
     */
    public WaterTank getTank() {
        return tank;
    }

    /**
     * Returns the list of vertices defining the polygon of this Voronoi cell.
     *
     * @return the list of vertices
     */
    public List<Point> getVertices() {
        return vertices;
    }

    /**
     * Calculates and returns the area of this Voronoi cell using the Shoelace formula.
     *
     * @return the area of the cell, or 0.0 if the cell has fewer than 3 vertices
     */
    public double getArea() {
        List<Point> v = vertices;
        int n = v.size();
        if (n < 3) return 0;
        double area = 0;
        for (int i = 0; i < n; i++) {
            Point a = v.get(i), b = v.get((i + 1) % n);
            area += a.getX() * b.getY() - b.getX() * a.getY();
        }
        return Math.abs(area) / 2.0;
    }

    /**
     * Returns the list of neighboring Voronoi cells adjacent to this cell.
     *
     * @return the list of neighboring cells
     */
    public List<VoronoiCell> getNeighbors() {
        return neighbors;
    }

    /**
     * Determines whether a given point lies inside this Voronoi cell
     * using the ray casting algorithm.
     * <p>
     * A horizontal ray is cast from the given point toward positive infinity.
     * For each edge of the cell's polygon, the method checks whether the ray
     * crosses that edge. If the total number of crossings is odd, the point
     * is inside the cell; if even, it is outside.
     * </p>
     *
     * @param p the point to test
     * @return {@code true} if the point is inside this cell; {@code false} otherwise
     */
    public boolean contains(Point p) {
        int crossings = 0;
        int n = vertices.size();
        //On boucle sur deux points consecutifs pour avoir une arête de la cellule Voronoi
        for (int i = 0; i < n; i++) {
            int next = (i + 1) % n;
            Point a = vertices.get(i);
            Point b = vertices.get(next);

            //Si un point est au dessus et un en dessous de notre point alors l'arête "enjambe" notre point p
            boolean aEstEnDessous = a.getY() <= p.getY();
            boolean bEstEnDessous = b.getY() <= p.getY();
            boolean areteEnjambe  = aEstEnDessous != bEstEnDessous;

            if (areteEnjambe) {
                // Condition 2 : calculer le x de l'arête à la hauteur du point
                double pente = (b.getX() - a.getX()) / (b.getY() - a.getY()); //le coef directeur de l'arête qui enjambe
                double xIntersection = a.getX() + pente * (p.getY() - a.getY());
                //on utilise le coef directeur pour savoir
                // si je descends de a.y jusqu'à p.y, de combien ai-je avancé en x ?

                // Le croisement est-il à droite du point ?
                if (p.getX() < xIntersection) {
                    crossings++; //notre rayon passe par +1 arête du polygone
                }
            }
        }

        // Impair = dedans, pair = dehors
        return crossings % 2 == 1;
    }

    /**
     * Compares this Voronoi cell to the specified object for equality.
     * Two Voronoi cells are considered equal if and only if they are associated
     * with the exact same water tank tank.
     *
     * @param o the object to compare with this cell
     * @return {@code true} if the given object is equivalent to this cell; {@code false} otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VoronoiCell that = (VoronoiCell) o;
        return Objects.equals(tank, that.tank);
    }

    /**
     * Returns a hash code value for this Voronoi cell.
     * The hash code is generated based solely on its associated tank.
     *
     * @return a hash code value for this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(tank);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("VoronoiCell{\n");
        sb.append("  tank=").append(tank).append("\n");
        sb.append("  vertices(").append(vertices.size()).append(")=\n");
        for (Point v : vertices) {
            sb.append("    ").append(v).append("\n");
        }
        sb.append("  area=").append(String.format("%.2f", getArea())).append("\n");
        sb.append("  neighbors(").append(neighbors.size()).append(")=[");
        for (int i = 0; i < neighbors.size(); i++) {
            sb.append(neighbors.get(i).getTank());
            if (i < neighbors.size() - 1) sb.append(", ");
        }
        sb.append("]\n}");
        return sb.toString();
    }

}
