package org.example;

import java.util.Objects;

public class Point {
    private enum TYPE {
        SPRINKLER, NATURE;
    }
    private enum STATE{
        DRY, WET;
    }

    private final Coordinates coordinates;
    private final int id;
    private static int nbId;
    public Point (int x, int y){
        coordinates = new Coordinates(x, y);
        id = nbId;
        nbId++;
    }

    public int getId(){
        return this.id;
    }
    @Override
    public boolean equals(Object O) {
        if (O instanceof Point p){
            if (p.getId() == this.id && p.coordinates.equals(this.coordinates)){
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, coordinates);
    }
}
