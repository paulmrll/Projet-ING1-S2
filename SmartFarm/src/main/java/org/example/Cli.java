package org.example;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Command-line interface for the SmartFarm application.
 * <p>
 * Provides a full interactive terminal menu to manage a {@link Ground}
 * (water tanks, sprinklers, fields), compute and inspect the Voronoi diagram
 * and its Delaunay triangulation, and load/save the farm state.
 * </p>
 *
 * @author SmartFarm Team
 * @version 1.0
 */
public class Cli {

    // ── ANSI colour codes ────────────────────────────────────────────────────
    private static final String RESET   = "\033[0m";
    private static final String BOLD    = "\033[1m";
    private static final String GREEN   = "\033[32m";
    private static final String CYAN    = "\033[36m";
    private static final String YELLOW  = "\033[33m";
    private static final String RED     = "\033[31m";
    private static final String MAGENTA = "\033[35m";
    private static final String BLUE    = "\033[34m";

    // ── State ─────────────────────────────────────────────────────────────
    private Ground  ground;
    private Scanner scanner;
    private Save    save;

    // ══════════════════════════════════════════════════════════════════════════
    //  Entry point
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Application entry point.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        new Cli().run();
    }

    /**
     * Launches the CLI: asks the user to create or load a farm, then enters
     * the main menu loop.
     */
    public void run() {
        scanner = new Scanner(System.in);
        save    = new Save("save.txt");

        printBanner();

        // ── Bootstrap: load or create a ground ────────────────────────────
        System.out.println(CYAN + "  1. " + RESET + "Load existing farm (save.txt)");
        System.out.println(CYAN + "  2. " + RESET + "Create new farm");
        System.out.println(CYAN + "  3. " + RESET + "Load demo farm (pre-filled data)");
        System.out.print(BOLD + "\n  Choice > " + RESET);

        int init = readInt(1, 3);
        switch (init) {
            case 1 -> loadFarm();
            case 2 -> createFarm();
            case 3 -> loadDemoFarm();
        }

        mainMenuLoop();
        System.out.println(GREEN + "\n  Goodbye!" + RESET);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Main menu
    // ══════════════════════════════════════════════════════════════════════════

    private void mainMenuLoop() {
        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readInt(0, 7);
            switch (choice) {
                case 1 -> menuWaterTanks();
                case 2 -> menuSprinklers();
                case 3 -> menuFields();
                case 4 -> menuVoronoi();
                case 5 -> menuDelaunay();
                case 6 -> menuSaveLoad();
                case 7 -> printGroundInfo();
                case 0 -> running = false;
            }
        }
    }

    private void printMainMenu() {
        System.out.println();
        printSeparator(BLUE);
        System.out.println(BOLD + BLUE + "  ╔══════════════  MAIN MENU  ══════════════╗" + RESET);
        printSeparator(BLUE);
        System.out.println(CYAN + "  1." + RESET + "  Water Tank management");
        System.out.println(CYAN + "  2." + RESET + "  Sprinkler management");
        System.out.println(CYAN + "  3." + RESET + "  Field management");
        System.out.println(CYAN + "  4." + RESET + "  Voronoi diagram");
        System.out.println(CYAN + "  5." + RESET + "  Delaunay triangulation");
        System.out.println(CYAN + "  6." + RESET + "  Save / Load");
        System.out.println(CYAN + "  7." + RESET + "  Ground info");
        System.out.println(RED   + "  0." + RESET + "  Quit");
        printSeparator(BLUE);
        System.out.print(BOLD + "  Choice > " + RESET);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  1. Water Tanks
    // ══════════════════════════════════════════════════════════════════════════

    private void menuWaterTanks() {
        boolean back = false;
        while (!back) {
            printSubMenu("WATER TANKS", new String[]{
                    "List all tanks",
                    "Add a tank",
                    "Add tanks in bulk (random positions)",
                    "Remove a tank",
                    "Move a tank",
                    "Refill a tank",
                    "Show tank details"
            });
            int c = readInt(0, 7);
            switch (c) {
                case 1 -> listTanks();
                case 2 -> addTank();
                case 3 -> addTanksBulk();
                case 4 -> removeTank();
                case 5 -> moveTank();
                case 6 -> refillTank();
                case 7 -> showTankDetails();
                case 0 -> back = true;
            }
        }
    }

    private void listTanks() {
        List<WaterTank> tanks = ground.getTanks();
        if (tanks.isEmpty()) {
            warn("No tanks on this ground.");
            return;
        }
        printSeparator(GREEN);
        System.out.println(BOLD + GREEN + "  Water Tanks (" + tanks.size() + ")" + RESET);
        printSeparator(GREEN);
        for (int i = 0; i < tanks.size(); i++) {
            WaterTank t = tanks.get(i);
            System.out.println("  ["+ i +"] "+ t);
        }
    }

    private void addTank() {
        System.out.print("  X coordinate > "); double x = readDouble();
        System.out.print("  Y coordinate > "); double y = readDouble();
        System.out.print("  Capacity     > "); double cap = readDouble();
        System.out.print("  Flow         > "); double flow = readDouble();
        ground.addTank(new WaterTank(x, y, cap, flow));
        success("Tank added at (" + x + ", " + y + ").");
    }

    private void addTanksBulk() {
        System.out.print("  Number of tanks to add > "); int n = readInt(1, 1000);
        System.out.print("  Area width  (0-?) > "); double w = readDouble();
        System.out.print("  Area height (0-?) > "); double h = readDouble();
        System.out.print("  Capacity for each > "); double cap = readDouble();
        Random rng = new Random();
        for (int i = 0; i < n; i++) {
            double x = rng.nextDouble() * w;
            double y = rng.nextDouble() * h;
            ground.addTank(new WaterTank(x, y, cap, cap));
        }
        success(n + " tanks added at random positions.");
    }

    private void removeTank() {
        listTanks();
        if (ground.getTanks().isEmpty()) return;
        System.out.print("  Index to remove > "); int idx = readInt(0, ground.getTanks().size() - 1);
        WaterTank t = ground.getTanks().get(idx);
        boolean ok = ground.removeTank(t);
        if (ok) success("Tank removed."); else warn("Removal failed.");
    }

    private void moveTank() {
        listTanks();
        if (ground.getTanks().isEmpty()) return;
        System.out.print("  Index to move > "); int idx = readInt(0, ground.getTanks().size() - 1);
        WaterTank old = ground.getTanks().get(idx);
        System.out.print("  New X > "); double x = readDouble();
        System.out.print("  New Y > "); double y = readDouble();
        old.setX(x);
        old.setY(y);
        success("Tank moved to (" + x + ", " + y + ").");
    }

    private void refillTank() {
        listTanks();
        if (ground.getTanks().isEmpty()) return;
        System.out.print("  Index to refill (or -1 for all) > ");
        int idx = readInt(-1, ground.getTanks().size() - 1);
        if (idx == -1) {
            ground.getTanks().forEach(WaterTank::refill);
            success("All tanks refilled.");
        } else {
            ground.getTanks().get(idx).refill();
            success("Tank refilled.");
        }
    }

    private void showTankDetails() {
        listTanks();
        if (ground.getTanks().isEmpty()) return;
        System.out.print("  Index > "); int idx = readInt(0, ground.getTanks().size() - 1);
        WaterTank t = ground.getTanks().get(idx);
        printSeparator(MAGENTA);
        System.out.println(BOLD + MAGENTA + "  Tank details" + RESET);
        printSeparator(MAGENTA);
        System.out.println("  Position   : (" + t.getX() + ", " + t.getY() + ")");
        System.out.println("  Capacity   : " + t.getCapacity());
        System.out.println("  Flow       : " + t.getFlow());
        System.out.println("  Empty?     : " + (t.isEmpty() ? RED + "YES" : GREEN + "NO") + RESET);

        // Voronoi cell linked to this tank
        if (ground.getVoronoiDiagram() != null) {
            for (VoronoiCell cell : ground.getVoronoiDiagram().getCells()) {
                if (cell.getTank() == t) {
                    System.out.printf("  Cell area  : %.4f%n", cell.getArea());
                    System.out.println("  Neighbors  : " + cell.getNeighbors().size());
                    break;
                }
            }
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  2. Sprinklers
    // ══════════════════════════════════════════════════════════════════════════

    private void menuSprinklers() {
        boolean back = false;
        while (!back) {
            printSubMenu("SPRINKLERS", new String[]{
                    "List all sprinklers",
                    "Add a sprinkler",
                    "Add sprinklers in bulk (random positions)",
                    "Remove a sprinkler",
                    "Move a sprinkler",
                    "Activate all sprinklers",
                    "Deactivate all sprinklers",
                    "Show sprinkler details"
            });
            int c = readInt(0, 8);
            switch (c) {
                case 1 -> listSprinklers();
                case 2 -> addSprinkler();
                case 3 -> addSprinklersBulk();
                case 4 -> removeSprinkler();
                case 5 -> moveSprinkler();
                case 6 -> activateAll();
                case 7 -> deactivateAll();
                case 8 -> showSprinklerDetails();
                case 0 -> back = true;
            }
        }
    }

    private void listSprinklers() {
        List<Sprinkler> list = ground.getSprinklers();
        if (list.isEmpty()) { warn("No sprinklers on this ground."); return; }
        printSeparator(CYAN);
        System.out.println(BOLD + CYAN + "  Sprinklers (" + list.size() + ")" + RESET);
        printSeparator(CYAN);
        for (int i = 0; i < list.size(); i++) {
            Sprinkler s = list.get(i);
            String status = s.isActive() ? GREEN + "ON " + RESET : RED + "OFF" + RESET;
            System.out.println("  ["+ i +"] ["+ status + "] " + s);
        }
    }

    private void addSprinkler() {
        System.out.print("  X coordinate > "); double x = readDouble();
        System.out.print("  Y coordinate > "); double y = readDouble();
        System.out.print("  Flow         > "); double flow = readDouble();
        System.out.print("  Radius       > "); double radius = readDouble();
        ground.addSprinkler(new Sprinkler(x, y, flow, radius));
        success("Sprinkler added. Source tank auto-assigned via Voronoi.");
    }

    private void addSprinklersBulk() {
        System.out.print("  Number of sprinklers > "); int n = readInt(1, 1000);
        System.out.print("  Area width  (0-?) > "); double w = readDouble();
        System.out.print("  Area height (0-?) > "); double h = readDouble();
        System.out.print("  Flow for each  > "); double flow = readDouble();
        System.out.print("  Radius for each > "); double radius = readDouble();
        Random rng = new Random();
        for (int i = 0; i < n; i++) {
            double x = rng.nextDouble() * w;
            double y = rng.nextDouble() * h;
            ground.addSprinkler(new Sprinkler(x, y, flow, radius));
        }
        success(n + " sprinklers added at random positions.");
    }

    private void removeSprinkler() {
        listSprinklers();
        if (ground.getSprinklers().isEmpty()) return;
        System.out.print("  Index to remove > "); int idx = readInt(0, ground.getSprinklers().size() - 1);
        boolean ok = ground.removeSprinkler(ground.getSprinklers().get(idx));
        if (ok) success("Sprinkler removed."); else warn("Removal failed.");
    }

    private void moveSprinkler() {
        listSprinklers();
        if (ground.getSprinklers().isEmpty()) return;
        System.out.print("  Index to move > "); int idx = readInt(0, ground.getSprinklers().size() - 1);
        Sprinkler sprinkler = ground.getSprinklers().get(idx);
        System.out.print("  New X > "); double x = readDouble();
        System.out.print("  New Y > "); double y = readDouble();
        sprinkler.setX(x);
        sprinkler.setY(y);
        boolean ok = ground.updateSprinkler(sprinkler, sprinkler);
        if (ok) {
            success("Sprinkler moved and source re-assigned.");
        } else {
            warn("Move failed.");
        }
    }

    private void activateAll() {
        int activated = 0;
        for (Sprinkler s : ground.getSprinklers()) {
            if (s.activate()) {
                activated = activated + 1;
            }
        }
        success(activated + " sprinkler(s) activated.");
    }

    private void deactivateAll() {
        ground.getSprinklers().forEach(Sprinkler::deactivate);
        success("All sprinklers deactivated.");
    }

    private void showSprinklerDetails() {
        listSprinklers();
        if (ground.getSprinklers().isEmpty()) return;
        System.out.print("  Index > "); int idx = readInt(0, ground.getSprinklers().size() - 1);
        Sprinkler s = ground.getSprinklers().get(idx);
        printSeparator(MAGENTA);
        System.out.println(BOLD + MAGENTA + "  Sprinkler details" + RESET);
        printSeparator(MAGENTA);
        System.out.println("  Position : (" + s.getX() + ", " + s.getY() + ")");
        System.out.println("  Flow     : " + s.getFlow());
        System.out.println("  Radius   : " + s.getRadius());
        System.out.println("  Active   : " + (s.isActive() ? GREEN + "YES" : RED + "NO") + RESET);
        System.out.println("  Source   : " + (s.getSource() != null ? s.getSource() : RED + "none" + RESET));

        // Show which Voronoi cell contains this sprinkler
        if (ground.getVoronoiDiagram() != null) {
            VoronoiCell cell = ground.findCellContaining(s.getX(), s.getY());
            if (cell != null) {
                System.out.println("  Cell tank: " + cell.getTank());
            }
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  3. Fields
    // ══════════════════════════════════════════════════════════════════════════

    private void menuFields() {
        boolean back = false;
        while (!back) {
            printSubMenu("FIELDS", new String[]{
                    "List all fields",
                    "Add a field",
                    "Remove a field",
                    "Show field details"
            });
            int c = readInt(0, 4);
            switch (c) {
                case 1 -> listFields();
                case 2 -> addField();
                case 3 -> removeField();
                case 4 -> showFieldDetails();
                case 0 -> back = true;
            }
        }
    }

    private void listFields() {
        List<Field> fields = ground.getFields();
        if (fields.isEmpty()) { warn("No fields on this ground."); return; }
        printSeparator(YELLOW);
        System.out.println(BOLD + YELLOW + "  Fields (" + fields.size() + ")" + RESET);
        printSeparator(YELLOW);
        for (int i = 0; i < fields.size(); i++) {
            Field f = fields.get(i);
            System.out.printf("  [%d] %-15s  area=%.2f  bounds=[%.1f,%.1f]-[%.1f,%.1f]%n",
                    i, f.getName(), f.getArea(),
                    f.getxStart(), f.getyStart(), f.getxStop(), f.getyStop());
        }
    }

    private void addField() {
        System.out.print("  Name   > "); String name = scanner.nextLine().trim();
        System.out.print("  xStart > "); double xs = readDouble();
        System.out.print("  xStop  > "); double xe = readDouble();
        System.out.print("  yStart > "); double ys = readDouble();
        System.out.print("  yStop  > "); double ye = readDouble();
        double area = Math.abs((xe - xs) * (ye - ys));
        System.out.printf("  Computed area = %.2f  (override? enter 0 to keep)%n", area);
        System.out.print("  Area > "); double inputArea = readDouble();
        if (inputArea != 0) {
            area = inputArea;
        }
        ground.addField(new Field(name, xs, xe, ys, ye, area));
        success("Field '" + name + "' added.");
    }

    private void removeField() {
        listFields();
        if (ground.getFields().isEmpty()) return;
        System.out.print("  Index to remove > "); int idx = readInt(0, ground.getFields().size() - 1);
        boolean ok = ground.removeField(ground.getFields().get(idx));
        if (ok) {
            success("Field removed.");
        } else {
            warn("Removal failed.");
        }
    }

    private void showFieldDetails() {
        listFields();
        if (ground.getFields().isEmpty()) {
            return;
        }
        System.out.print("  Index > "); int idx = readInt(0, ground.getFields().size() - 1);
        Field f = ground.getFields().get(idx);
        printSeparator(MAGENTA);
        System.out.println(BOLD + MAGENTA + "  Field details" + RESET);
        printSeparator(MAGENTA);
        System.out.println("  Name   : " + f.getName());
        System.out.println("  Bounds : [" + f.getxStart() + "," + f.getyStart() + "] → [" + f.getxStop() + "," + f.getyStop() + "]");
        System.out.printf("  Area   : %.2f%n", f.getArea());

        // Count sprinklers inside this field
        int sprinklersInside = 0;
        for (Sprinkler s : ground.getSprinklers()) {
            if (f.contains(s)) {
                sprinklersInside++;
            }
        }
        System.out.println("  Sprinklers inside : " + sprinklersInside);

        // Count tanks inside this field
        int tanksInside = 0;
        for (WaterTank t : ground.getTanks()) {
            if (f.contains(t)) {
                tanksInside++;
            }
        }
        System.out.println("  Tanks inside      : " + tanksInside);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  4. Voronoi
    // ══════════════════════════════════════════════════════════════════════════

    private void menuVoronoi() {
        if (ground.getTanks().size() < 3) {
            warn("Need at least 3 tanks to compute a Voronoi diagram.");
            return;
        }
        boolean back = false;
        while (!back) {
            printSubMenu("VORONOI DIAGRAM", new String[]{
                    "Display all cells",
                    "Inspect a cell",
                    "Find cell at position (x,y)",
                    "Cell statistics (area, distances)",
                    "Find nearest tank to a point",
                    "Recompute Voronoi"
            });
            int c = readInt(0, 6);
            switch (c) {
                case 1 -> displayAllCells();
                case 2 -> inspectCell();
                case 3 -> findCellAtPosition();
                case 4 -> cellStatistics();
                case 5 -> findNearestTank();
                case 6 -> { ground.computeVoronoi(); success("Voronoi diagram recomputed."); }
                case 0 -> back = true;
            }
        }
    }

    private void displayAllCells() {
        if (ground.getVoronoiDiagram() == null) {
            warn("No Voronoi diagram computed yet.");
            return;
        }
        List<VoronoiCell> cells = ground.getVoronoiDiagram().getCells();
        printSeparator(MAGENTA);
        System.out.println(BOLD + MAGENTA + "  Voronoi Cells (" + cells.size() + ")" + RESET);
        printSeparator(MAGENTA);
        for (int i = 0; i < cells.size(); i++) {
            VoronoiCell cell = cells.get(i);
            System.out.printf("  [%d] Tank(%s)  area=%.4f  vertices=%d  neighbors=%d%n",
                    i, cell.getTank(), cell.getArea(),
                    cell.getVertices().size(), cell.getNeighbors().size());
        }
    }

    private void inspectCell() {
        displayAllCells();
        if (ground.getVoronoiDiagram() == null) return;
        List<VoronoiCell> cells = ground.getVoronoiDiagram().getCells();
        if (cells.isEmpty()) return;
        System.out.print("  Cell index > "); int idx = readInt(0, cells.size() - 1);
        VoronoiCell cell = cells.get(idx);

        printSeparator(MAGENTA);
        System.out.println(BOLD + MAGENTA + "  Cell details [" + idx + "]" + RESET);
        printSeparator(MAGENTA);
        System.out.println("  Tank     : " + cell.getTank());
        System.out.printf ("  Area     : %.6f%n", cell.getArea());
        System.out.println("  Vertices (" + cell.getVertices().size() + ") :");
        for (Point v : cell.getVertices()) {
            System.out.printf("    (%.4f, %.4f)%n", v.getX(), v.getY());
        }
        System.out.println("  Neighbors (" + cell.getNeighbors().size() + ") :");
        for (VoronoiCell nb : cell.getNeighbors()) {
            System.out.println("    → " + nb.getTank());
        }

        // Distances to neighbor tanks
        double minDist = Double.MAX_VALUE, maxDist = 0, sumDist = 0;
        for (VoronoiCell nb : cell.getNeighbors()) {
            double d = cell.getTank().distanceTo(nb.getTank());
            minDist = Math.min(minDist, d);
            maxDist = Math.max(maxDist, d);
            sumDist += d;
        }
        if (!cell.getNeighbors().isEmpty()) {
            System.out.printf("  Dist to neighbors — min=%.4f  max=%.4f  avg=%.4f%n",
                    minDist, maxDist, sumDist / cell.getNeighbors().size());
        }
    }

    private void findCellAtPosition() {
        if (ground.getVoronoiDiagram() == null) {
            warn("No Voronoi diagram computed yet.");
            return;
        }
        System.out.print("  X > ");
        double x = readDouble();
        System.out.print("  Y > ");
        double y = readDouble();
        VoronoiCell cell = ground.findCellContaining(x, y);
        if (cell == null) {
            warn("Point (" + x + ", " + y + ") is not inside any Voronoi cell.");
        } else {
            success("Point is inside cell of tank: " + cell.getTank());
            System.out.printf("  Cell area: %.4f%n", cell.getArea());
        }
    }

    private void cellStatistics() {
        if (ground.getVoronoiDiagram() == null) {
            warn("No Voronoi diagram computed yet.");
            return;
        }
        List<VoronoiCell> cells = ground.getVoronoiDiagram().getCells();
        double minArea = Double.MAX_VALUE, maxArea = 0, sumArea = 0;
        VoronoiCell minCell = null, maxCell = null;
        for (VoronoiCell c : cells) {
            double a = c.getArea();
            sumArea += a;
            if (a < minArea) { minArea = a; minCell = c; }
            if (a > maxArea) { maxArea = a; maxCell = c; }
        }
        double avgArea = cells.isEmpty() ? 0 : sumArea / cells.size();

        printSeparator(CYAN);
        System.out.println(BOLD + CYAN + "  Voronoi Statistics" + RESET);
        printSeparator(CYAN);
        System.out.printf("  Cells      : %d%n", cells.size());
        System.out.printf("  Total area : %.4f%n", sumArea);
        System.out.printf("  Avg area   : %.4f%n", avgArea);
        System.out.printf("  Min area   : %.4f  (tank: %s)%n", minArea, minCell != null ? minCell.getTank() : "?");
        System.out.printf("  Max area   : %.4f  (tank: %s)%n", maxArea, maxCell != null ? maxCell.getTank() : "?");
    }

    private void findNearestTank() {
        System.out.print("  X > "); double x = readDouble();
        System.out.print("  Y > "); double y = readDouble();
        WaterTank nearest = ground.findNearestTank(x, y);
        if (nearest == null) {
            warn("No tanks available.");
        }else {
            double dist = nearest.distanceTo(new Point(x, y));
            success("Nearest tank: " + nearest);
            System.out.printf("  Distance: %.4f%n", dist);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  5. Delaunay
    // ══════════════════════════════════════════════════════════════════════════

    private void menuDelaunay() {
        if (ground.getTanks().size() < 3) {
            warn("Need at least 3 tanks to compute Delaunay triangulation.");
            return;
        }
        boolean back = false;
        while (!back) {
            printSubMenu("DELAUNAY TRIANGULATION", new String[]{
                    "List all triangles",
                    "Inspect a triangle",
                    "Triangle statistics"
            });
            int c = readInt(0, 3);
            switch (c) {
                case 1 -> listTriangles();
                case 2 -> inspectTriangle();
                case 3 -> triangleStatistics();
                case 0 -> back = true;
            }
        }
    }

    private void listTriangles() {
        if (ground.getVoronoiDiagram() == null) {
            warn("Voronoi not computed yet.");
            return;
        }
        List<DelaunayTriangle> triangles = ground.getVoronoiDiagram().getTriangles();
        printSeparator(YELLOW);
        System.out.println(BOLD + YELLOW + "  Delaunay Triangles (" + triangles.size() + ")" + RESET);
        printSeparator(YELLOW);
        for (int i = 0; i < triangles.size(); i++) {
            DelaunayTriangle t = triangles.get(i);
            WaterTank[] v = t.getVertices();
            System.out.printf("  [%d] (%.2f,%.2f) — (%.2f,%.2f) — (%.2f,%.2f)  R=%.4f%n",
                    i,
                    v[0].getX(), v[0].getY(),
                    v[1].getX(), v[1].getY(),
                    v[2].getX(), v[2].getY(),
                    t.getCircumRadius());
        }
    }

    private void inspectTriangle() {
        listTriangles();
        if (ground.getVoronoiDiagram() == null) {
            return;
        }
        List<DelaunayTriangle> triangles = ground.getVoronoiDiagram().getTriangles();
        if (triangles.isEmpty()) {
            return;
        }
        System.out.print("  Triangle index > ");
        int idx = readInt(0, triangles.size() - 1);
        DelaunayTriangle t = triangles.get(idx);
        WaterTank[] v = t.getVertices();

        printSeparator(YELLOW);
        System.out.println(BOLD + YELLOW + "  Triangle [" + idx + "] details" + RESET);
        printSeparator(YELLOW);
        System.out.printf("  Vertex A : (%.4f, %.4f)%n", v[0].getX(), v[0].getY());
        System.out.printf("  Vertex B : (%.4f, %.4f)%n", v[1].getX(), v[1].getY());
        System.out.printf("  Vertex C : (%.4f, %.4f)%n", v[2].getX(), v[2].getY());

        // Circumcircle
        Point cc = t.getCircumcenter();
        if (cc != null) {
            System.out.printf("  Circumcenter : (%.4f, %.4f)%n", cc.getX(), cc.getY());
        }
        System.out.printf("  Circumradius : %.4f%n", t.getCircumRadius());

        // Edge lengths
        double ab = v[0].distanceTo(v[1]);
        double bc = v[1].distanceTo(v[2]);
        double ca = v[2].distanceTo(v[0]);
        System.out.printf("  Edge AB  : %.4f%n", ab);
        System.out.printf("  Edge BC  : %.4f%n", bc);
        System.out.printf("  Edge CA  : %.4f%n", ca);

        System.out.printf("  Area     : %.4f%n", t.getArea());

        // Sprinklers per vertex (count by proximity to tank position)
        for (int j = 0; j < 3; j++) {
            long count = ground.countSprinklersFor(v[j]);
            System.out.printf("  Sprinklers on vertex %c : %d%n", (char)('A' + j), count);
        }

        // Imbalance
        System.out.println("  Imbalance (max-min sprinklers) : " + ground.getSprinklerImbalance(List.of(v[0], v[1], v[2])));
    }

    private void triangleStatistics() {
        if (ground.getVoronoiDiagram() == null) { warn("Voronoi not computed yet."); return; }
        List<DelaunayTriangle> triangles = ground.getVoronoiDiagram().getTriangles();
        if (triangles.isEmpty()) { warn("No triangles."); return; }

        double minR = Double.MAX_VALUE, maxR = 0, sumR = 0;
        for (DelaunayTriangle t : triangles) {
            double r = t.getCircumRadius();
            sumR += r; minR = Math.min(minR, r); maxR = Math.max(maxR, r);
        }
        printSeparator(YELLOW);
        System.out.println(BOLD + YELLOW + "  Delaunay Statistics" + RESET);
        printSeparator(YELLOW);
        System.out.printf("  Triangles          : %d%n", triangles.size());
        System.out.printf("  Circumradius min   : %.4f%n", minR);
        System.out.printf("  Circumradius max   : %.4f%n", maxR);
        System.out.printf("  Circumradius avg   : %.4f%n", sumR / triangles.size());
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  6. Save / Load
    // ══════════════════════════════════════════════════════════════════════════

    private void menuSaveLoad() {
        printSubMenu("SAVE / LOAD", new String[]{
                "Save to file (save.txt)",
                "Load from file (save.txt)"
        });
        int c = readInt(0, 2);
        switch (c) {
            case 1 -> saveFarm();
            case 2 -> loadFarm();
            case 0 -> { /* back */ }
        }
    }

    private void saveFarm() {
        try {
            save.writeSave(ground);
            success("Farm saved to save.txt");
        } catch (Exception e) {
            error("Save failed: " + e.getMessage());
        }
    }

    private void loadFarm() {
        try {
            Ground loaded = save.readSave();
            if (loaded != null) {
                ground = loaded;
                success("Farm loaded from save.txt");
            } else {
                warn("File was empty or invalid.");
            }
        } catch (IOException e) {
            error("Load failed: " + e.getMessage());
            System.out.println("  Creating a blank farm instead.");
            createFarm();
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  7. Ground info
    // ══════════════════════════════════════════════════════════════════════════

    private void printGroundInfo() {
        printSeparator(BLUE);
        System.out.println(BOLD + BLUE + "  Ground overview" + RESET);
        printSeparator(BLUE);
        System.out.println("  Owner      : " + ground.getOwner().getFirstname() + " " + ground.getOwner().getName());
        System.out.printf ("  Area       : %.2f%n", ground.getArea());
        System.out.println("  Fields     : " + ground.getFields().size());
        System.out.println("  Tanks      : " + ground.getTanks().size());
        System.out.println("  Sprinklers : " + ground.getSprinklers().size());
        System.out.println("  Voronoi    : " + (ground.getVoronoiDiagram() != null
                ? GREEN + "computed (" + ground.getVoronoiDiagram().getCells().size() + " cells)" + RESET
                : RED + "not computed" + RESET));

        // Tank fill summary
        if (!ground.getTanks().isEmpty()) {
            long empty = ground.countEmptyTanks();

            System.out.println("  Empty tanks: " + (empty == 0 ? GREEN : RED) + empty + RESET
                    + " / " + ground.getTanks().size());
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Bootstrap helpers
    // ══════════════════════════════════════════════════════════════════════════

    private void createFarm() {
        System.out.print("  Owner first name > "); String fn = scanner.nextLine().trim();
        System.out.print("  Owner last name  > "); String ln = scanner.nextLine().trim();
        System.out.print("  Owner email      > "); String em = scanner.nextLine().trim();
        System.out.print("  Owner age        > "); int age = readInt(1, 120);
        System.out.print("  Ground area      > "); double area = readDouble();
        Person owner = new Person(age, ln, fn, em);
        ground = new Ground(area, owner);
        success("New farm created for " + fn + " " + ln + ".");
    }

    private void loadDemoFarm() {
        Person owner = new Person(35, "Dupont", "Jean", "jean.dupont@farm.fr");
        ground = new Ground(10000, owner);

        ground.addTank(new WaterTank(1.0, 1.0, 100.0, 80.0));
        ground.addTank(new WaterTank(8.0, 1.0, 100.0, 60.0));
        ground.addTank(new WaterTank(4.5, 8.0, 100.0, 90.0));
        ground.addTank(new WaterTank(2.0, 5.0,  80.0, 50.0));
        ground.addTank(new WaterTank(7.0, 5.0,  80.0, 70.0));

        ground.addField(new Field("Wheat",  0, 5,  0, 5,  25.0));
        ground.addField(new Field("Corn",   5, 10, 0, 5,  25.0));
        ground.addField(new Field("Barley", 0, 10, 5, 10, 50.0));

        ground.addSprinkler(new Sprinkler(1.5, 1.5, 10.0, 2.0));
        ground.addSprinkler(new Sprinkler(7.5, 1.5, 10.0, 2.0));
        ground.addSprinkler(new Sprinkler(4.5, 7.0, 10.0, 2.0));
        ground.addSprinkler(new Sprinkler(3.0, 4.0, 10.0, 2.0));
        ground.addSprinkler(new Sprinkler(6.5, 4.5, 10.0, 2.0));

        success("Demo farm loaded (5 tanks, 3 fields, 5 sprinklers, Voronoi computed).");
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  UI helpers
    // ══════════════════════════════════════════════════════════════════════════

    private void printBanner() {
        System.out.println(GREEN + BOLD);
        System.out.println("  ███████╗███╗   ███╗ █████╗ ██████╗ ████████╗███████╗ █████╗ ██████╗ ███╗   ███╗");
        System.out.println("  ██╔════╝████╗ ████║██╔══██╗██╔══██╗╚══██╔══╝██╔════╝██╔══██╗██╔══██╗████╗ ████║");
        System.out.println("  ███████╗██╔████╔██║███████║██████╔╝   ██║   █████╗  ███████║██████╔╝██╔████╔██║");
        System.out.println("  ╚════██║██║╚██╔╝██║██╔══██║██╔══██╗   ██║   ██╔══╝  ██╔══██║██╔══██╗██║╚██╔╝██║");
        System.out.println("  ███████║██║ ╚═╝ ██║██║  ██║██║  ██║   ██║   ██║     ██║  ██║██║  ██║██║ ╚═╝ ██║");
        System.out.println("  ╚══════╝╚═╝     ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝   ╚═╝     ╚═╝  ╚═╝╚═╝  ╚═╝╚═╝     ╚═╝");
        System.out.println(RESET);
        System.out.println(CYAN + "  SmartFarm - Command Line Interface" + RESET);
        System.out.println(CYAN + "  Voronoi / Delaunay irrigation management system" + RESET);
        System.out.println();
    }

    private void printSubMenu(String title, String[] items) {
        System.out.println();
        printSeparator(YELLOW);
        System.out.println(BOLD + YELLOW + "  -- " + title + " --" + RESET);
        printSeparator(YELLOW);
        for (int i = 0; i < items.length; i++) {
            System.out.println(CYAN + "  " + (i + 1) + "." + RESET + "  " + items[i]);
        }
        System.out.println(RED + "  0." + RESET + "  Back");
        printSeparator(YELLOW);
        System.out.print(BOLD + "  Choice > " + RESET);
    }

    private void printSeparator(String colour) {
        System.out.println(colour + "  ─────────────────────────────────────────────" + RESET);
    }

    private void success(String msg) {
        System.out.println(GREEN + "  ✔ " + msg + RESET);
    }

    private void warn(String msg) {
        System.out.println(YELLOW + "  ⚠ " + msg + RESET);
    }

    private void error(String msg) {
        System.out.println(RED + "  ✘ " + msg + RESET);
    }

    // ── Safe input readers ────────────────────────────────────────────────

    /**
     * Reads an integer between {@code min} and {@code max} inclusive,
     * re-prompting on invalid input.
     */
    private int readInt(int min, int max) {
        while (true) {
            try {
                String line = scanner.nextLine().trim();
                int val = Integer.parseInt(line);
                if (val >= min && val <= max) {
                    return val;
                }
                System.out.print(YELLOW + "  Enter a number between " + min + " and " + max + " > " + RESET);
            } catch (NumberFormatException e) {
                System.out.print(YELLOW + "  Invalid input. Enter a number > " + RESET);
            }
        }
    }

    /** Reads a double, re-prompting on invalid input. */
    private double readDouble() {
        while (true) {
            try {
                String line = scanner.nextLine().trim();
                return Double.parseDouble(line);
            } catch (NumberFormatException e) {
                System.out.print(YELLOW + "  Invalid input. Enter a decimal number > " + RESET);
            }
        }
    }
}
