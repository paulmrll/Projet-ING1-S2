package org.example;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for building a Voronoi diagram from a Delaunay triangulation.
 * <p>
 * This class cannot be instantiated. All methods are static.
 * The Voronoi diagram is derived from the duality between Delaunay triangulation
 * and Voronoi diagrams: the circumcenter of each Delaunay triangle is a vertex
 * of the Voronoi polygon surrounding the corresponding water tank.
 * </p>
 *
 * @author Oscar LUIGGI
 * @version 1.0
 */
public class VoronoiBuilder {

    /**
     * Builds a list of Voronoi cells from a Delaunay triangulation.
     * <p>
     * For each water tank, the method collects all adjacent Delaunay triangles
     * (triangles that share this tank as a vertex), extracts their circumcenters,
     * and sorts them by angle around the tank to form a valid non-self-intersecting
     * polygon.
     * </p>
     *
     * @param tanks     the list of water tanks used as Voronoi sites
     * @param triangles the list of Delaunay triangles computed from the same tanks
     * @return a list of {@link CelluleVoronoi}, one per water tank,
     *         each containing its polygon vertices in counter-clockwise order
     */
    public static List<CelluleVoronoi> fromTriangulation( List<WaterTank> tanks, List<DelaunayTriangle> triangles) {

        List<CelluleVoronoi> cells = new ArrayList<>();

        for (WaterTank tank : tanks) {

            // On collecte tous les triangles qui ont ce (WaterTank) tank comme sommet
            List<DelaunayTriangle> adjacentTriangles = new ArrayList<>();
            for (DelaunayTriangle triangle : triangles) {
                WaterTank[] vertices = triangle.getVertices();
                if (vertices[0] == tank || vertices[1] == tank || vertices[2] == tank) {
                    adjacentTriangles.add(triangle);
                }
            }

            // On recupère les circumcenters, les sommets des arêtes du polygone
            List<Point> polygonVertices = new ArrayList<>();
            for (DelaunayTriangle triangle : adjacentTriangles) {
                polygonVertices.add(triangle.getCircumcenter());
            }

            // On trie les sommets par angle autour du tank
            //           pour former un polygone valide (non croisé)
            double tankX = tank.getX();
            double tankY = tank.getY();

            polygonVertices.sort((p1, p2) -> {
                double angle1 = Math.atan2(p1.getY() - tankY, p1.getX() - tankX);
                double angle2 = Math.atan2(p2.getY() - tankY, p2.getX() - tankX);
                return Double.compare(angle1, angle2);
            });

            // On construit la cellule avec ces sommets
            CelluleVoronoi cell = new CelluleVoronoi(tank, polygonVertices);
            cells.add(cell);
        }

        return cells;
    }
}
