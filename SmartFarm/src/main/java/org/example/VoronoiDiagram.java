package org.example;

import java.util.List;
import java.util.Objects;

/**
 * Represents a Voronoi diagram generated from a set of water tanks.
 * <p>
 * The diagram is constructed by first performing a Delaunay triangulation
 * on the provided water tanks, and then building the corresponding Voronoi cells
 * from that triangulation.
 * </p>
 *
 * @author Oscar LUIGGI
 * @version 1.0
 */
public class VoronoiDiagram {

    /** The list of Voronoi cells that make up this diagram. */
    private List<VoronoiCell> cells;

    /** The list of water tanks serving as the sites (centers) for the diagram. */
    private List<WaterTank> tanks;

    /** The list of Delaunay triangles generated from the water tanks. */
    private List<DelaunayTriangle> triangles;

    /**
     * Constructs a new {@code VoronoiDiagram} from a given list of water tanks.
     * <p>
     * This constructor automatically generates the Delaunay triangulation
     * from the tanks and subsequently builds the associated Voronoi cells.
     * </p>
     *
     * @param tanks the list of water tanks acting as the sites for the diagram
     */
    public VoronoiDiagram(List<WaterTank> tanks) {
        this.tanks = tanks;
        this.triangles = DelaunayTriangulation.triangulate(this.tanks);
        this.cells = VoronoiBuilder.fromTriangulation(this.tanks, this.triangles);
    }

    /**
     * Returns the list of Voronoi cells in this diagram.
     *
     * @return the list of {@link VoronoiCell} objects
     */
    public List<VoronoiCell> getCells() {
        return cells;
    }

    /**
     * Returns the list of Delaunay triangles used to build this diagram.
     *
     * @return the list of {@link DelaunayTriangle} objects
     */
    public List<DelaunayTriangle> getTriangles() {
        return triangles;
    }

    /**
     * Returns the list of water tanks that act as the sites for this diagram.
     *
     * @return the list of {@link WaterTank} objects
     */
    public List<WaterTank> getTanks() {
        return tanks;
    }

    /**
     * Returns a string representation of this Voronoi diagram.
     *
     * @return a string representation of the diagram
     */
    @Override
    public String toString() {
        return "";
    }

    /**
     * Compares this Voronoi diagram to the specified object for equality.
     * Two Voronoi diagrams are considered equal if they contain the identical
     * lists of tanks, cells, and triangles.
     *
     * @param o the object to compare with this diagram
     * @return {@code true} if the given object is equivalent to this diagram; {@code false} otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof VoronoiDiagram voronoiDiagram) {
            return (this.tanks.equals(voronoiDiagram.tanks) &&
                    this.cells.equals(voronoiDiagram.cells) &&
                    this.triangles.equals(voronoiDiagram.triangles));
        }
        return false;
    }

    /**
     * Returns a hash code value for this Voronoi diagram.
     * The hash code is generated based on its tanks, cells, and triangles.
     *
     * @return a hash code value for this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.tanks, this.cells, this.triangles);
    }
}