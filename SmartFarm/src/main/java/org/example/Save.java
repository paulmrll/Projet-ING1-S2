package org.example;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class Save {
    private Path path = null;
    public Save(String path){
        this.path = Paths.get(path);
    }

    public void writeSave(Ground ground) throws FileNotFoundException {
        if (path == null) {
            throw new FileNotFoundException("File not found : " + path);
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toString(), false))) {
            writer.write("name;" + ground.getOwner().getName() + ";" + ground.getOwner().getFirstname() + ";" + ground.getOwner().getEmail()
                    + ";" + ground.getOwner().getAge());
            writer.newLine();
            writer.write("G;"+ground.getArea());
            writer.newLine();
            for (Field f : ground.getFields()) {
                writer.write("F;"+f.getxStart()+";"+f.getyStart()+";"+f.getxStop()+";"+f.getyStop()+";"+f.getName()+";"+f.getArea());
                writer.newLine();
            }
            for (WaterTank w : ground.getTanks()){
                writer.write("W;"+w.getX()+";"+w.getY()+";"+w.getCapacity()+";"+w.getFlow());
                writer.newLine();
            }
            for (Sprinkler s : ground.getSprinklers()){
                writer.write("S;"+s.getX()+";"+s.getY()+";"+s.getFlow()+";"+s.getRadius());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error while writing " + e.getMessage());
        }
    }

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
        for (String line : lines){

            String[] element = line.split(";");
            if (element.length == 0 || line.trim().isEmpty()) continue;
            if (count == 0){
                owner = createPerson(Integer.parseInt(element[4]), element[1], element[2], element[3]);
                count = 1;
            } else {
                if (element[0].equals("G")){
                    ground = createGround(Double.parseDouble(element[1]), owner);
                    System.out.println();
                } else if (element[0].equals("F")){
                    assert ground != null;
                    ground.addField(createField(element[5],Double.parseDouble(element[1]), Double.parseDouble(element[3]), Double.parseDouble(element[2]), Double.parseDouble(element[4]), Double.parseDouble(element[6])));
                } else if (element[0].equals("W")){
                    assert ground != null;
                    ground.addTank(createWaterTank(Double.parseDouble(element[1]), Double.parseDouble(element[2]), Double.parseDouble(element[3]), Double.parseDouble(element[4])));
                } else if (element[0].equals("S")){
                    assert ground != null;
                    ground.addSprinkler(createSprinkler(Double.parseDouble(element[1]), Double.parseDouble(element[2]), Double.parseDouble(element[3]), Double.parseDouble(element[4])));
                }
            }
        }
        return ground;
    }
    private Person createPerson(int age, String name, String firstname, String email){
        return new Person(age, name, firstname, email);
    }
    private Ground createGround(double area, Person owner){
        return new Ground(area, owner);
    }
    private Field createField(String name, double xStart, double xStop, double yStart, double yStop, double area){
        return new Field(name, xStart, xStop, yStart, yStop, area);
    }
    private WaterTank createWaterTank(double x, double y, double capacity, double flow){
        return new WaterTank(x, y, capacity, flow);
    }
    private Sprinkler createSprinkler(double x, double y, double flow, double radius){
        return new Sprinkler(x, y, flow, radius);
    }
}
