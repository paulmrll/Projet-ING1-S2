package org.example;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
            for (Field f : ground.getFields()) {
                writer.write("F;"+f.getxStart()+";"+f.getyStart()+";"+f.getxStop()+";"+f.getyStop()+";"+f.getName()+";"+f.getArea()+";");
                writer.newLine();
            }
            for (WaterTank w : ground.getTanks()){
                writer.write("W;"+w.getX()+";"+w.getY()+";"+w.getCapacity());
                writer.newLine();
            }
            for (Sprinkler s : ground.getSprinklers()){
                writer.write("S;"+s.getX()+";"+s.getY()+s.getFlow()+s.getRadius());
                writer.newLine();
            }
            System.out.println("Lines correctly added");
        } catch (IOException e) {
            System.err.println("Error while writing " + e.getMessage());
        }
    }
}
