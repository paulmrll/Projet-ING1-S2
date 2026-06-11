package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class representing an agricultural ground.
 * A ground has an area and contains fields, water tanks and sprinkler systems
 * for intelligent irrigation management.
 * 
 * @author Tom LEMENAND, Oscar LUIGGI
 * @version 1.0
 */
public class Ground {
    /**
     * The area of the ground in square units (typically in m²).
     */
    private double area;

    /**
     * The person responsible for managing this ground.
     */
    private Person owner;
    
    /**
     * List of fields present on the ground.
     */
    private List<Field> fields;
    
    /**
     * List of water tanks available on the ground.
     */
    private List<WaterTank> tanks;
    
    /**
     * List of sprinkler systems installed on the ground.
     */
    private List<Sprinkler> sprinklers;

    /**
     * The Voronoi diagram computed from the water tanks on this ground.
     * Null until computeVoronoi() is called.
     */
    private VoronoiDiagram voronoiDiagram;

    /**
     * Constructor for the Ground class.
     * Initializes a new ground with a given area and creates empty lists
     * for fields, tanks and sprinklers.
     * 
     * @param area the area of the ground in square units
     */
    public Ground(double area, Person owner) {
        this.area = area;
        this.fields = new ArrayList<>();
        this.tanks = new ArrayList<>();
        this.sprinklers = new ArrayList<>();
        this.owner = owner;
    }

    /**
     * Gets the area of the ground.
     * 
     * @return the area of the ground
     */
    public double getArea() {
        return area;
    }

    /**
     * Returns the person managing this ground.
     *
     * @return the owner, or null if not assigned
     */
    public Person getOwner() {
        return owner;
    }


    /**
     * Gets the list of all fields on the ground.
     * 
     * @return the list of fields
     */
    public List<Field> getFields() {
        return fields;
    }

    /**
     * Gets the list of all water tanks on the ground.
     * 
     * @return the list of tanks
     */
    public List<WaterTank> getTanks() {
        return tanks;
    }

    /**
     * Gets the list of all sprinkler systems on the ground.
     * 
     * @return the list of sprinklers
     */
    public List<Sprinkler> getSprinklers() {
        return sprinklers;
    }

    /**
     * Sets the area of the ground.
     * 
     * @param area the new area of the ground
     */
    public void setArea(double area) {
        this.area = area;
    }

    /**
     * Adds a new field to the ground.
     * 
     * @param field the field to add
     */
    public void addField(Field field) {
        fields.add(field);
    }

    /**
     * Adds a new water tank to the ground and recomputes the Voronoi diagram.
     *
     * @param tank the tank to add
     */
    public void addTank(WaterTank tank) {
        tanks.add(tank);
        if (tanks.size() >= 3) {
            computeVoronoi();
            sprinklers.forEach(this::findSource);
        }
    }

    /**
     * Adds a new sprinkler system to the ground.
     * 
     * @param sprinkler the sprinkler to add
     */
    public void addSprinkler(Sprinkler sprinkler) {
        findSource(sprinkler);
        sprinklers.add(sprinkler);
    }

    /**
     * Removes a field from the ground.
     *
     * @param field the field to remove
     * @return true if the field was removed, false if it wasn't found
     */
    public boolean removeField(Field field) {
        return fields.remove(field);
    }

    /**
     * Removes a water tank from the ground.
     *
     * @param tank the tank to remove
     * @return true if the tank was removed, false if it wasn't found
     */
    public boolean removeTank(WaterTank tank) {
        boolean removed = tanks.remove(tank);
        if (removed) {
            if (tanks.size() >= 3) computeVoronoi();
            sprinklers.forEach(this::findSource);
        }
        return removed;
    }

    /**
     * Removes a sprinkler from the ground.
     *
     * @param sprinkler the sprinkler to remove
     * @return true if the sprinkler was removed, false if it wasn't found
     */
    public boolean removeSprinkler(Sprinkler sprinkler) {
        return sprinklers.remove(sprinkler);
    }

    /**
     * Replaces an existing field with a new one.
     *
     * @param oldField the field to replace
     * @param newField the new field
     * @return true if the field was updated, false if oldField wasn't found
     */
    public boolean updateField(Field oldField, Field newField) {
        int index = fields.indexOf(oldField);
        if (index == -1) return false;
        fields.set(index, newField);
        return true;
    }

    /**
     * Replaces an existing water tank with a new one.
     *
     * @param oldTank the tank to replace
     * @param newTank the new tank
     * @return true if the tank was updated, false if oldTank wasn't found
     */
    public boolean updateTank(WaterTank oldTank, WaterTank newTank) {
        int index = tanks.indexOf(oldTank);
        if (index == -1) return false;
        tanks.set(index, newTank);
        if (tanks.size() >= 3) computeVoronoi();
        return true;
    }

    /**
     * Replaces an existing sprinkler with a new one.
     *
     * @param oldSprinkler the sprinkler to replace
     * @param newSprinkler the new sprinkler
     * @return true if the sprinkler was updated, false if oldSprinkler wasn't found
     */
    public boolean updateSprinkler(Sprinkler oldSprinkler, Sprinkler newSprinkler) {
        int index = sprinklers.indexOf(oldSprinkler);
        if (index == -1) {
            return false;
        }
        findSource(newSprinkler);
        sprinklers.set(index, newSprinkler);
        return true;
    }

    public void findSource(Sprinkler sprinkler) {
        if (voronoiDiagram != null) {
            VoronoiCell cell = findCellContaining(sprinkler.getX(), sprinkler.getY());
            if (cell != null) {
                sprinkler.setSource(cell.getTank());
            } else {
                sprinkler.setSource(findNearestTank(sprinkler.getX(), sprinkler.getY()));
            }
        } else {
            sprinkler.setSource(findNearestTank(sprinkler.getX(), sprinkler.getY()));
        }
    }

    /**
     * Finds the nearest water tank to a given position.
     *
     * @param x the x-coordinate of the position
     * @param y the y-coordinate of the position
     * @return the nearest WaterTank, or null if there are no tanks
     */
    public WaterTank findNearestTank(double x, double y) {
        WaterTank nearest = null;
        double minDist = Double.MAX_VALUE;
        Point target = new Point(x, y);
        for (WaterTank tank : tanks) {
            double dist = tank.distanceTo(target);
            if (dist < minDist) {
                minDist = dist;
                nearest = tank;
            }
        }
        return nearest;
    }

    /**
     * Finds the Voronoi cell containing a given position.
     * computeVoronoi() must be called before using this method.
     *
     * @param x the x-coordinate of the position
     * @param y the y-coordinate of the position
     * @return the VoronoiCell containing the point, or null if not found
     */
    public VoronoiCell findCellContaining(double x, double y) {
        if (voronoiDiagram == null) return null;
        Point p = new Point(x, y);
        for (VoronoiCell cell : voronoiDiagram.getCells()) {
            if (cell.contains(p)) return cell;
        }
        return null;
    }

    /**
     * Computes the Voronoi diagram from the water tanks on this ground.
     * Must be called after adding tanks.
     */
    public void computeVoronoi() {
        this.voronoiDiagram = new VoronoiDiagram(tanks);
    }

    /**
     * Returns the Voronoi diagram of this ground.
     * Returns null if computeVoronoi() has not been called yet.
     *
     * @return the Voronoi diagram, or null if not yet computed
     */
    public VoronoiDiagram getVoronoiDiagram() {
        return voronoiDiagram;
    }

    /**
     * Returns a string representation of the ground.
     * 
     * @return a string containing the main information of the ground
     */
    @Override
    public String toString() {
        return "Ground{" +
                "Owner=" + owner +
                "area=" + area +
                ", fields=" + fields.size() +
                ", tanks=" + tanks.size() +
                ", sprinklers=" + sprinklers.size() +
                '}';
    }

    /**
     * Compares two Ground objects based on their area, fields, tanks and sprinklers.
     * Two grounds are considered equal if they have the same area and identical
     * lists of fields, tanks and sprinklers.
     *
     * @param o the object to compare
     * @return true if both objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ground ground = (Ground) o;
        return Double.compare(ground.area, area) == 0
                && Objects.equals(fields, ground.fields)
                && Objects.equals(tanks, ground.tanks)
                && Objects.equals(sprinklers, ground.sprinklers);
    }

    /**
     * Returns a hash code value for this ground based on its area,
     * fields, tanks and sprinklers.
     *
     * @return a hash code value for this ground
     */
    @Override
    public int hashCode() {
        return Objects.hash(area, fields, tanks, sprinklers);
    }

    /**
     * Counts the number of sprinklers supplied by a specific water tank.
     *
     * @param tank the water tank source
     * @return the number of sprinklers linked to this tank
     */
    public long countSprinklersFor(WaterTank tank) {
        long count = 0;
        for (Sprinkler sp : this.sprinklers) {
            if (sp.getSource() == tank) {
                count++;
            }
        }
        return count;
    }

    /**
     * Counts the number of empty water tanks on the ground.
     *
     * @return the number of empty tanks
     */
    public long countEmptyTanks() {
        long count = 0;
        for (WaterTank tank : this.tanks) {
            if (tank.isEmpty()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Calculates the imbalance among a specific list of water tanks.
     *
     * @param selectedTanks the list of tanks to evaluate
     * @return the load imbalance among the selected tanks, or 0 if the list is empty
     */
    public long getSprinklerImbalance(List<WaterTank> selectedTanks) {
        if (selectedTanks == null || selectedTanks.isEmpty()) {
            return 0;
        }

        long max = Long.MIN_VALUE;
        long min = Long.MAX_VALUE;

        for (WaterTank tank : selectedTanks) {
            long count = countSprinklersFor(tank);
            if (count > max) max = count;
            if (count < min) min = count;
        }

        return max - min;
    }

    /**
     * Calculates the imbalance (max - min difference) in the number of sprinklers per tank.
     * A difference of 0 indicates a perfectly balanced network.
     *
     * @return the load imbalance, or 0 if there are no tanks.
     */
    public long getSprinklerImbalance() {
        return getSprinklerImbalance(tanks);
    }

    /**
     * Counts the total number of currently active sprinklers on the ground.
     *
     * @return the number of running sprinklers.
     */
    public long countActiveSprinklers() {
        long count = 0;
        for (Sprinkler sp : this.sprinklers) {
            if (sp.isActive()) {
                count++;
            }
        }
        return count;
    }
}
