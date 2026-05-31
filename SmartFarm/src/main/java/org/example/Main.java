package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main(String[] args) {
        System.out.println("Hello and welcome!");
        for (int i = 1; i <= 5; i++) {
            System.out.println("i = " + i);
        }
        Sprinkler sprinkler = new Sprinkler(0.0,1.0,2.0,3.0);
        WaterTank waterTank = new WaterTank(1.0,1.0,2.0,3.0);
        System.out.println(sprinkler);
        System.out.println(waterTank);

        Random random = new Random();
        List<WaterTank> points = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            points.add(new WaterTank(random.nextDouble(100.0),random.nextDouble(100.0),3.0));
        }
        System.out.println(points.getFirst());
        List<DelaunayTriangle> triangles = DelaunayTriangulation.triangulate(points);
        System.out.println("Calcule triangle terminé");
        List<VoronoiCell> cells = VoronoiBuilder.fromTriangulation(points, triangles);
        System.out.println("Calcule cellule voronoi terminé");
    }
}
