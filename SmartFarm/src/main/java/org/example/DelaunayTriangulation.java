package org.example;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements the Bowyer-Watson algorithm to compute a Delaunay triangulation
 * from a set of WaterTank points.
 *
 * @author Tom LEMENAND
 * @version 1.0
 */
public class DelaunayTriangulation {

    /**
     * Represents an undirected edge between two WaterTank vertices.
     * Equality is based on reference identity of the two endpoints (order-independent).
     */
    private static class Edge {
        final WaterTank a;
        final WaterTank b;

        Edge(WaterTank a, WaterTank b) {
            this.a = a;
            this.b = b;
        }

        /**
         * Two edges are equal if they connect the same pair of vertices,
         * regardless of order. Uses reference equality (==) because all
         * vertices are shared object instances within the triangulation.
         */
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Edge)) return false;
            Edge e = (Edge) o;
            return (a == e.a && b == e.b) || (a == e.b && b == e.a);
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(a) + System.identityHashCode(b);
        }
    }

    /**
     * Computes the Delaunay triangulation of a list of WaterTank points
     * using the Bowyer-Watson incremental insertion algorithm.
     *
     * @param points the list of WaterTank points to triangulate (minimum 3)
     * @return a list of DelaunayTriangle forming the triangulation,
     * or an empty list if fewer than 3 points are provided
     */
    public static List<DelaunayTriangle> triangulate(List<WaterTank> points) {
        List<DelaunayTriangle> triangles = new ArrayList<>();
        if (points == null || points.size() < 3) return triangles;

        // --- STEP 1: bounding box ---
        double minX = points.get(0).getX();
        double maxX = minX;
        double minY = points.get(0).getY();
        double maxY = minY;

        for (WaterTank p : points) {
            if (p.getX() < minX) minX = p.getX();
            if (p.getX() > maxX) maxX = p.getX();
            if (p.getY() < minY) minY = p.getY();
            if (p.getY() > maxY) maxY = p.getY();
        }

        // --- STEP 2: super-triangle large enough to contain all points ---
        double dx = maxX - minX;
        double dy = maxY - minY;
        double delta = Math.max(dx, dy) * 10;
        double midX = (minX + maxX) / 2.0;
        double midY = (minY + maxY) / 2.0;

        // Capacity = 1 is a placeholder; these vertices are removed at the end
        WaterTank st0 = new WaterTank(midX - 20 * delta, midY - delta, 1);
        WaterTank st1 = new WaterTank(midX, midY + 20 * delta, 1);
        WaterTank st2 = new WaterTank(midX + 20 * delta, midY - delta, 1);

        triangles.add(new DelaunayTriangle(st0, st1, st2));

        // --- STEP 3: insert each point ---
        for (WaterTank point : points) {

            // 3a. Collect "bad" triangles whose circumcircle contains the point
            List<DelaunayTriangle> badTriangles = new ArrayList<>();
            for (DelaunayTriangle t : triangles) {
                if (t.isPointInCircumcircle(point)) {
                    badTriangles.add(t);
                }
            }

            // 3b. Find the boundary polygon:
            //     keep only edges that belong to exactly ONE bad triangle
            List<Edge> polygon = new ArrayList<>();
            for (DelaunayTriangle t : badTriangles) {
                WaterTank[] v = t.getVertices();
                Edge[] edges = {
                        new Edge(v[0], v[1]),
                        new Edge(v[1], v[2]),
                        new Edge(v[2], v[0])
                };
                for (Edge edge : edges) {
                    boolean sharedWithOther = false;
                    for (DelaunayTriangle other : badTriangles) {
                        if (other == t) {
                            continue;
                        }
                        WaterTank[] ov = other.getVertices();
                        Edge[] otherEdges = {
                                new Edge(ov[0], ov[1]),
                                new Edge(ov[1], ov[2]),
                                new Edge(ov[2], ov[0])
                        };
                        for (Edge oe : otherEdges) {
                            if (edge.equals(oe)) {
                                sharedWithOther = true;
                                break;
                            }
                        }
                        if (sharedWithOther) break;
                    }
                    if (!sharedWithOther) polygon.add(edge);
                }
            }

            // 3c. Remove bad triangles from the triangulation
            triangles.removeAll(badTriangles);

            // 3d. Re-triangulate the cavity: connect point to each boundary edge
            for (Edge edge : polygon) {
                triangles.add(new DelaunayTriangle(edge.a, edge.b, point));
            }
        }

        // --- STEP 4: remove triangles that share a vertex with the super-triangle ---
        triangles.removeIf(t -> {
            WaterTank[] v = t.getVertices();
            return v[0] == st0 || v[0] == st1 || v[0] == st2
                    || v[1] == st0 || v[1] == st1 || v[1] == st2
                    || v[2] == st0 || v[2] == st1 || v[2] == st2;
        });

        return triangles;
    }
}
