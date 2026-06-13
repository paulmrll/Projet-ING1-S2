package org.example;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

/**
 * Handles persistence of {@link Ground} objects by reading and writing
 * plain-text save files.
 * <p>
 * The file format is line-based, with a semicolon ({@code ;}) as delimiter:
 * <ul>
 *   <li>{@code name;lastName;firstName;email;age} — owner information (first line)</li>
 *   <li>{@code G;area} — ground area</li>
 *   <li>{@code F;xStart;yStart;xStop;yStop;name;area} — a field</li>
 *   <li>{@code W;x;y;capacity;flow} — a water tank</li>
 *   <li>{@code S;x;y;flow;radius} — a sprinkler</li>
 * </ul>
 * </p>
 *
 * @author SmartFarm Team
 * @version 1.0
 */
public class Save {

    /**
     * The path of the save file to read from or write to.
     */
    private Path path = null;

    /**
     * Constructs a new {@code Save} instance bound to the given file path.
     *
     * @param path the path of the file to use for saving or loading
     */
    public Save(String path) {
        this.path = Paths.get(path);
    }

    /**
     * Serialises a {@link Ground} object and writes it to the save file.
     * <p>
     * The file is overwritten if it already exists. The ground's owner,
     * area, fields, water tanks and sprinklers are all persisted.
     * The Voronoi diagram is not saved and will be recomputed on load.
     * </p>
     *
     * @param ground the ground to save; must not be {@code null}
     * @throws FileNotFoundException if the configured path is {@code null}
     *                               or the parent directory does not exist
     */
    public void writeSave(Ground ground) throws FileNotFoundException {
        if (path == null) {
            throw new FileNotFoundException("File not found : " + path);
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toString(), false))) {
            writer.write("name;" + ground.getOwner().getName() + ";" + ground.getOwner().getFirstname() + ";" + ground.getOwner().getEmail()
                    + ";" + ground.getOwner().getAge());
            writer.newLine();
            writer.write("G;" + ground.getArea());
            writer.newLine();
            for (Field f : ground.getFields()) {
                writer.write("F;" + f.getxStart() + ";" + f.getyStart() + ";" + f.getxStop() + ";" + f.getyStop() + ";" + f.getName() + ";" + f.getArea());
                writer.newLine();
            }
            for (WaterTank w : ground.getTanks()) {
                writer.write("W;" + w.getX() + ";" + w.getY() + ";" + w.getCapacity() + ";" + w.getFlow());
                writer.newLine();
            }
            for (Sprinkler s : ground.getSprinklers()) {
                writer.write("S;" + s.getX() + ";" + s.getY() + ";" + s.getFlow() + ";" + s.getRadius());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error while writing " + e.getMessage());
        }
    }

    /**
     * Deserialises a {@link Ground} object from the save file.
     * <p>
     * Lines are parsed in order: the first line is always the owner,
     * followed by a ground line ({@code G}), then any number of field
     * ({@code F}), water tank ({@code W}) and sprinkler ({@code S}) lines.
     * The Voronoi diagram is recomputed automatically when tanks are added
     * via {@link Ground#addTank(WaterTank)}.
     * </p>
     *
     * @return the reconstructed {@link Ground}, or {@code null} if the file
     *         was empty or contained no ground line
     * @throws FileNotFoundException if the configured path is {@code null}
     *                               or the file does not exist on disk
     * @throws IOException           if an I/O error occurs while reading the file
     */
    public Ground readSave() throws IOException {
        if (path == null) {
            throw new FileNotFoundException("File not found : " + path);
        }
        if (!Files.exists(path) || !Files.isRegularFile(path)) {
            throw new FileNotFoundException("Le fichier n'a pas été trouvé : " + path);
        }

        List<String> lines = Files.readAllLines(path);
        int count = 0;
        Person owner = null;
        Ground ground = null;
        for (String line : lines) {

            String[] element = line.split(";");
            if (element.length == 0 || line.trim().isEmpty()) continue;
            if (count == 0) {
                try{
                    owner = createPerson(Integer.parseInt(element[4]), element[1], element[2], element[3]);
                    count = 1;
                } catch (NumberFormatException e) {
                    throw new RuntimeException(e);
                }

            } else {
                if (element[0].equals("G")) {
                    try{
                        ground = createGround(Double.parseDouble(element[1]), owner);
                    } catch (NumberFormatException e) {
                        throw new RuntimeException(e);
                    }
                } else if (element[0].equals("F")) {
                    assert ground != null;
                    try{
                        ground.addField(createField(element[5], Double.parseDouble(element[1]), Double.parseDouble(element[3]), Double.parseDouble(element[2]), Double.parseDouble(element[4]), Double.parseDouble(element[6])));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                } else if (element[0].equals("W")) {
                    assert ground != null;
                    try{
                        ground.addTank(createWaterTank(Double.parseDouble(element[1]), Double.parseDouble(element[2]), Double.parseDouble(element[3]), Double.parseDouble(element[4])));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                } else if (element[0].equals("S")) {
                    assert ground != null;
                    try{
                        ground.addSprinkler(createSprinkler(Double.parseDouble(element[1]), Double.parseDouble(element[2]), Double.parseDouble(element[3]), Double.parseDouble(element[4])));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return ground;
    }

    /**
     * Creates a new {@link Person} from the parsed save fields.
     *
     * @param age       the owner's age
     * @param name      the owner's last name
     * @param firstname the owner's first name
     * @param email     the owner's email address
     * @return a new {@link Person} instance
     */
    private Person createPerson(int age, String name, String firstname, String email) {
        return new Person(age, name, firstname, email);
    }

    /**
     * Creates a new {@link Ground} from the parsed save fields.
     *
     * @param area  the total area of the ground
     * @param owner the person who owns this ground
     * @return a new {@link Ground} instance
     */
    private Ground createGround(double area, Person owner) {
        return new Ground(area, owner);
    }

    /**
     * Creates a new {@link Field} from the parsed save fields.
     *
     * @param name   the name of the field
     * @param xStart the starting X-coordinate boundary
     * @param xStop  the ending X-coordinate boundary
     * @param yStart the starting Y-coordinate boundary
     * @param yStop  the ending Y-coordinate boundary
     * @param area   the area of the field
     * @return a new {@link Field} instance
     */
    private Field createField(String name, double xStart, double xStop, double yStart, double yStop, double area) {
        return new Field(name, xStart, xStop, yStart, yStop, area);
    }

    /**
     * Creates a new {@link WaterTank} from the parsed save fields.
     *
     * @param x        the X-coordinate of the tank
     * @param y        the Y-coordinate of the tank
     * @param capacity the maximum capacity of the tank
     * @param flow     the current flow rate of the tank
     * @return a new {@link WaterTank} instance
     */
    private WaterTank createWaterTank(double x, double y, double capacity, double flow) {
        return new WaterTank(x, y, capacity, flow);
    }

    /**
     * Creates a new {@link Sprinkler} from the parsed save fields.
     *
     * @param x      the X-coordinate of the sprinkler
     * @param y      the Y-coordinate of the sprinkler
     * @param flow   the flow rate of the sprinkler
     * @param radius the coverage radius of the sprinkler
     * @return a new {@link Sprinkler} instance
     */
    private Sprinkler createSprinkler(double x, double y, double flow, double radius) {
        return new Sprinkler(x, y, flow, radius);
    }
}