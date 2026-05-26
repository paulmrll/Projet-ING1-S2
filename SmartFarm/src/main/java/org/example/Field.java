package org.example;

import java.util.Arrays;
import java.util.Objects;

public class Field {
    private Point[] waterPlants;
    private final int xStart;
    private final int xStop;
    private final int yStart;
    private final int yStop;

    private String name;
    public Field(String name, int xStart, int xStop, int yStart, int yStop){
        this.name = name;
        this.xStart = xStart;
        this.xStop = xStop;
        this.yStart = yStart;
        this.yStop = yStop;
    }
    public String getName(){
        return name;
    }
    public int getxStart(){
        return xStart;
    }
    public int getxStop(){
        return xStop;
    }
    public int getyStart(){
        return yStart;
    }
    public int getyStop(){
        return yStop;
    }
    public Point[] getWaterPlants(){
        return this.waterPlants;
    }
    public boolean equals(Object O){
        if (O instanceof Field f){
           if (this.name.equals(f.getName())){
               if (xStop == f.getxStop() && xStart == f.getxStart() && yStart == f.getyStart() && yStop == f.getyStop()){
                   Point [] waterPlantsF = f.getWaterPlants();
                   for (int i = 0; i < this.waterPlants.length; i++){
                       if (!waterPlants[i].equals(waterPlantsF[i])){
                           return false;
                       }
                   }
               }
           }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.deepHashCode(waterPlants), name, xStart, xStop, yStart, yStop);
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < waterPlants.length; i++){
            sb.append(waterPlants[i].toString());
            sb.append("\n");
        }
        return sb.toString();
    }
}
