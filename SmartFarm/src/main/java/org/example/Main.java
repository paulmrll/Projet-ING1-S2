package org.example;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Main {
    public static void main(String[] args) {

        // --- Création du terrain ---
        Person owner = new Person(35, "Dupont", "Jean", "jean.dupont@farm.fr");
        Ground ground = new Ground(10000,owner);

        // --- Ajout des réservoirs ---
        ground.addTank(new WaterTank(1.0, 1.0, 100.0, 80.0));
        ground.addTank(new WaterTank(8.0, 1.0, 100.0, 60.0));
        ground.addTank(new WaterTank(4.5, 8.0, 100.0, 90.0));
        ground.addTank(new WaterTank(2.0, 5.0, 80.0,  50.0));
        ground.addTank(new WaterTank(7.0, 5.0, 80.0,  70.0));

        // --- Ajout des champs ---
        Field wheat  = new Field("Blé",   0, 5,  0, 5,  25.0);
        Field corn   = new Field("Maïs",  5, 10, 0, 5,  25.0);
        Field barley = new Field("Orge",  0, 10, 5, 10, 50.0);
        ground.addField(wheat);
        ground.addField(corn);
        ground.addField(barley);

        // --- Ajout des arroseurs ---
        ground.addSprinkler(new Sprinkler(1.5, 1.5, 10.0, 2.0));
        ground.addSprinkler(new Sprinkler(7.5, 1.5, 10.0, 2.0));
        ground.addSprinkler(new Sprinkler(4.5, 7.0, 10.0, 2.0));
        ground.addSprinkler(new Sprinkler(3.0, 4.0, 10.0, 2.0));
        ground.addSprinkler(new Sprinkler(6.5, 4.5, 10.0, 2.0));

        // --- Affichage du terrain ---
        System.out.println("=== TERRAIN ===");
        System.out.println(ground);

        // --- Affichage des réservoirs ---
        System.out.println("\n=== RÉSERVOIRS ===");
        for (WaterTank tank : ground.getTanks()) {
            System.out.println(tank);
        }

        // --- Affichage du diagramme de Voronoï ---
        System.out.println("\n=== DIAGRAMME DE VORONOÏ ===");
        System.out.println(ground.getVoronoiDiagram());

        // --- Affichage des arroseurs et leur source ---
        System.out.println("\n=== ARROSEURS ===");
        for (Sprinkler s : ground.getSprinklers()) {
            System.out.println(s);
            System.out.println("  → source : " + s.getSource());
        }

        // --- Test activation des arroseurs ---
        System.out.println("\n=== ACTIVATION DES ARROSEURS ===");
        for (Sprinkler s : ground.getSprinklers()) {
            boolean ok = s.activate();
            System.out.println(s + " → activé : " + ok);
        }

        // --- État des réservoirs après activation ---
        System.out.println("\n=== RÉSERVOIRS APRÈS ACTIVATION ===");
        for (WaterTank tank : ground.getTanks()) {
            System.out.println(tank);
        }
        Save s = new Save("save.txt");
        try {
            s.writeSave(ground);
        } catch (FileNotFoundException e) {
            System.err.println("file not found " + e.getMessage());
        }


    }
}
