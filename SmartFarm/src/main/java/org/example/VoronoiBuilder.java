package org.example;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for building a Voronoi diagram from a Delaunay triangulation,
 * including clipping algorithms to constrain cells within ground boundaries.
 * <p>
 * This class cannot be instantiated. All methods are static.
 * The Voronoi diagram is derived from the duality between Delaunay triangulation
 * and Voronoi diagrams: the circumcenter of each Delaunay triangle is a vertex
 * of the Voronoi polygon surrounding the corresponding water tank.
 * </p>
 *
 * @author Oscar LUIGGI
 * @version 1.2
 */
public class VoronoiBuilder {

    /**
     * Builds a list of Voronoi cells from a Delaunay triangulation,
     * and computes the neighbors of each cell.
     * <p>
     * For each water tank, the method:
     * <ul>
     *   <li>Collects all adjacent Delaunay triangles (triangles that share this tank as a vertex)</li>
     *   <li>Extracts their circumcenters as polygon vertices</li>
     *   <li>Sorts them by angle around the tank to form a valid non-self-intersecting polygon</li>
     *   <li>Links neighboring cells: two cells are neighbors if their tanks share a Delaunay triangle</li>
     * </ul>
     * </p>
     *
     * @param tanks     the list of water tanks used as Voronoi sites
     * @param triangles the list of Delaunay triangles computed from the same tanks
     * @return a list of {@link VoronoiCell}, one per water tank,
     * each containing its polygon vertices in counter-clockwise order
     * and its neighboring cells already populated
     */
    public static List<VoronoiCell> fromTriangulation(List<WaterTank> tanks, List<DelaunayTriangle> triangles, double minX, double minY, double maxX, double maxY) {

        List<VoronoiCell> cells = new ArrayList<>();

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

            // Clipping Sutherland-Hodgman sur les bounds des fields
            List<Point> clipped = sutherlandHodgman(polygonVertices, minX, minY, maxX, maxY);

            // On construit la cellule avec ces sommets
            VoronoiCell cell = new VoronoiCell(tank, clipped);
            cells.add(cell);
        }

        // Relier les cellules voisines
        for (DelaunayTriangle triangle : triangles) {
            WaterTank[] vertices = triangle.getVertices();

            // Pour chaque paire de sommets du triangle
            for (int i = 0; i < 3; i++) {
                for (int j = i + 1; j < 3; j++) {
                    VoronoiCell cellA = null;
                    VoronoiCell cellB = null;

                    // Trouver les cellules correspondantes
                    for (VoronoiCell cell : cells) {
                        if (cell.getTank() == vertices[i]) cellA = cell;
                        if (cell.getTank() == vertices[j]) cellB = cell;

                        // Dès qu'on a trouvé les deux, inutile de continuer
                        if (cellA != null && cellB != null) {
                            if (!cellA.getNeighbors().contains(cellB)) cellA.getNeighbors().add(cellB);
                            if (!cellB.getNeighbors().contains(cellA)) cellB.getNeighbors().add(cellA);
                            break;
                        }
                    }
                }
            }
        }

        return cells;
    }

    private static List<Point> sutherlandHodgman(List<Point> polygon, double minX, double minY, double maxX, double maxY) {
        List<Point> result = new ArrayList<>(polygon);
        result = clipLeft(result, minX);
        result = clipRight(result, maxX);
        result = clipBottom(result, minY);
        result = clipTop(result, maxY);
        return result;
    }

    private static List<Point> clipLeft(List<Point> polygon, double minX) {
        List<Point> output = new ArrayList<>();
        int n = polygon.size();
        for (int i = 0; i < n; i++) {
            Point prev = polygon.get((i - 1 + n) % n);
            Point curr = polygon.get(i);
            boolean prevIn = prev.getX() >= minX;
            boolean currIn = curr.getX() >= minX;
            if (prevIn && currIn){
                output.add(curr);
            }else if (prevIn){
                output.add(intersectVertical(prev, curr, minX));
            }else if (currIn){
                output.add(intersectVertical(prev, curr, minX));
                output.add(curr);
            }
        }
        return output;
    }

    private static List<Point> clipRight(List<Point> polygon, double maxX) {
        List<Point> output = new ArrayList<>();
        int n = polygon.size();
        for (int i = 0; i < n; i++) {
            Point prev = polygon.get((i - 1 + n) % n);
            Point curr = polygon.get(i);
            boolean prevIn = prev.getX() <= maxX;
            boolean currIn = curr.getX() <= maxX;
            if (prevIn && currIn){
                output.add(curr);
            }else if (prevIn){
                output.add(intersectVertical(prev, curr, maxX));
            }else if (currIn){
                output.add(intersectVertical(prev, curr, maxX));
                output.add(curr);
            }
        }
        return output;
    }

    private static List<Point> clipBottom(List<Point> polygon, double minY) {
        List<Point> output = new ArrayList<>();
        int n = polygon.size();
        for (int i = 0; i < n; i++) {
            Point prev = polygon.get((i - 1 + n) % n);
            Point curr = polygon.get(i);
            boolean prevIn = prev.getY() >= minY;
            boolean currIn = curr.getY() >= minY;
            if (prevIn && currIn){
                output.add(curr);
            }else if (prevIn){
                output.add(intersectHorizontal(prev, curr, minY));
            }else if (currIn){
                output.add(intersectHorizontal(prev, curr, minY));
                output.add(curr);
            }
        }
        return output;
    }

    private static List<Point> clipTop(List<Point> polygon, double maxY) {
        List<Point> output = new ArrayList<>();
        int n = polygon.size();
        for (int i = 0; i < n; i++) {
            Point prev = polygon.get((i - 1 + n) % n);
            Point curr = polygon.get(i);
            boolean prevIn = prev.getY() <= maxY;
            boolean currIn = curr.getY() <= maxY;
            if (prevIn && currIn){
                output.add(curr);
            }else if (prevIn){
                output.add(intersectHorizontal(prev, curr, maxY));
            }else if (currIn){
                output.add(intersectHorizontal(prev, curr, maxY));
                output.add(curr);
            }
        }
        return output;
    }

    /**
     * Computes the intersection point between a line segment and a vertical boundary line.
     * <p>
     * This method calculates the precise entry or exit intersection coordinate using
     * linear interpolation (parametric equation of a line) based on the target vertical line's
     * X coordinate.
     * </p>
     *
     * @param a the starting point of the segment
     * @param b the ending point of the segment
     * @param x the fixed X coordinate of the vertical clipping boundary
     * @return a new {@link Point} object representing the exact intersection coordinate
     */
    private static Point intersectVertical(Point a, Point b, double x) {
        double t = (x - a.getX()) / (b.getX() - a.getX());
        double y = a.getY() + t * (b.getY() - a.getY());
        return new Point(x, y);
    }

    /**
     * Computes the intersection point between a line segment and a horizontal boundary line.
     * <p>
     * This method calculates the precise entry or exit intersection coordinate using
     * linear interpolation (parametric equation of a line) based on the target horizontal line's
     * Y coordinate.
     * </p>
     *
     * @param a the starting point of the segment
     * @param b the ending point of the segment
     * @param y the fixed Y coordinate of the horizontal clipping boundary
     * @return a new {@link Point} object representing the exact intersection coordinate
     */
    private static Point intersectHorizontal(Point a, Point b, double y) {
        double t = (y - a.getY()) / (b.getY() - a.getY());
        double x = a.getX() + t * (b.getX() - a.getX());
        return new Point(x, y);
    }
}
