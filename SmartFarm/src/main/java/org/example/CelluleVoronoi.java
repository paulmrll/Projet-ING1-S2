package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a Voronoi cell associated with a water tank reservoir.
 * <p>
 * A Voronoi cell is a convex polygon whose vertices are the circumcenters
 * of the adjacent Delaunay triangles. Any point inside this cell is closer
 * to its reservoir than to any other reservoir in the diagram.
 * </p>
 *
 * @author Tom LEMENAND, Oscar LUIGGI
 * @version 1.1
 */
public class CelluleVoronoi {
    /** The waterTank of the voronoi cell */
    private WaterTank reservoir;
    /** List of the vertices of the cell */
    private List<Point> vertices;
    /** List of the neighbors of this cell */
    private List<CelluleVoronoi> neighbors;

    /**
     * Constructs a new CelluleVoronoi associated with a water tank reservoir.
     *
     * @param reservoir the water tank that acts as the site of this Voronoi cell
     */
    public CelluleVoronoi(WaterTank reservoir) {
        this.reservoir = reservoir;
        this.vertices = new ArrayList<>();
        this.neighbors = new ArrayList<>();
    }

    /**
     * Returns the water tank reservoir associated with this cell.
     *
     * @return the {@link WaterTank} acting as the site of this cell
     */
    public WaterTank getReservoir() {
        return reservoir;
    }

    /**
     * Gets the list of vertices.
     * 
     * @return the vertices list
     */
    public List<Point> getVertices() {
        return vertices;
    }

    /**
     * Gets the area with the shoelace formula
     *
     * @return the area
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
     * Compares two CelluleVoronoi objects based on their associated reservoir.
     * 
     * @param o the object to compare
     * @return true if both cells are associated with the same reservoir
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CelluleVoronoi that = (CelluleVoronoi) o;
        return Objects.equals(reservoir, that.reservoir);
    }

    /**
     * Returns the hash code based on the associated reservoir.
     * 
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(reservoir);
    }

}
