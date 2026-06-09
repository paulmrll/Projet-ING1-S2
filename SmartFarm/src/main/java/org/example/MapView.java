package org.example;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.util.List;

public class MapView {
    private Ground ground;

    public MapView(Ground ground){
        this.ground = ground;
    }

    public Scene getScene(Stage stage){
        java.util.Random random = new java.util.Random();

        Pane root = new Pane();

        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE;

        for (Field f : ground.getFields()) {
            if (f.getxStart() < minX) minX = f.getxStart();
            if (f.getyStart() < minY) minY = f.getyStart();
            if (f.getxStop() > maxX) maxX = f.getxStop();
            if (f.getyStop() > maxY) maxY = f.getyStop();
        }

        double groundWidth = maxX - minX;
        double groundHeight = maxY - minY;

        double targetWidth = 1100;
        double targetHeight = 520;

        double scaleX = targetWidth / groundWidth;
        double scaleY = targetHeight / groundHeight;
        double multiplicator = Math.min(scaleX, scaleY);

        double margeX = (1200 - (groundWidth * multiplicator)) / 2;
        double margeY = (550 - (groundHeight * multiplicator)) / 2;

        for (Field f : ground.getFields()){
            double x = (f.getxStart() - minX) * multiplicator + margeX;
            double y = (f.getyStart() - minY) * multiplicator + margeY;
            double width = (f.getxStop() - f.getxStart()) * multiplicator;
            double height = (f.getyStop() - f.getyStart()) * multiplicator;


            Rectangle fieldSurface = new Rectangle(x, y, width, height);

            double r = 0.3 + random.nextDouble() * 0.4;
            double g = 0.6 + random.nextDouble() * 0.4; // Teinte verte dominante
            double b = 0.2 + random.nextDouble() * 0.4;
            fieldSurface.setFill(Color.color(r, g, b));

            fieldSurface.setStroke(Color.WHITE);
            fieldSurface.setStrokeWidth(1.5);

            root.getChildren().add(fieldSurface);
        }

        for (Sprinkler s : ground.getSprinklers()){
            double cx = (s.getX() - minX) * multiplicator + margeX;
            double cy = (s.getY() - minY) * multiplicator + margeY;

            Circle c = new Circle(cx, cy, 5);
            c.setFill(Color.BLUE);

            root.getChildren().add(c);
        }

        for (WaterTank w : ground.getTanks()){
            double cx = (w.getX() - minX) * multiplicator + margeX;
            double cy = (w.getY() - minY) * multiplicator + margeY;

            Circle c = new Circle(cx, cy, 5);
            c.setFill(Color.RED);

            root.getChildren().add(c);
        }

        if (ground.getTanks() != null && ground.getTanks().size() >= 3) {

            VoronoiDiagram diagram = new VoronoiDiagram(ground.getTanks());

            for (VoronoiCell cell : diagram.getCells()) {
                List<Point> vertices = cell.getVertices();
                if (vertices.size() < 3) continue;

                Polygon voronoiPolygon = new Polygon();

                for (Point p : vertices) {
                    double screenX = (p.getX() - minX) * multiplicator + margeX;
                    double screenY = (p.getY() - minY) * multiplicator + margeY;
                    voronoiPolygon.getPoints().addAll(screenX, screenY);
                }
                voronoiPolygon.setFill(Color.TRANSPARENT);
                voronoiPolygon.setStroke(Color.WHITE);
                voronoiPolygon.setStrokeWidth(1.5);

                root.getChildren().add(voronoiPolygon);
            }

            for (DelaunayTriangle triangle : diagram.getTriangles()) {
                WaterTank[] v = triangle.getVertices();

                double x0 = (v[0].getX() - minX) * multiplicator + margeX;
                double y0 = (v[0].getY() - minY) * multiplicator + margeY;

                double x1 = (v[1].getX() - minX) * multiplicator + margeX;
                double y1 = (v[1].getY() - minY) * multiplicator + margeY;

                double x2 = (v[2].getX() - minX) * multiplicator + margeX;
                double y2 = (v[2].getY() - minY) * multiplicator + margeY;

                Line line1 = new Line(x0, y0, x1, y1);
                Line line2 = new Line(x1, y1, x2, y2);
                Line line3 = new Line(x2, y2, x0, y0);

                Color delaunayColor = Color.ORANGE;
                double delaunayWidth = 1.0;

                line1.setStroke(delaunayColor); line1.setStrokeWidth(delaunayWidth);
                line2.setStroke(delaunayColor); line2.setStrokeWidth(delaunayWidth);
                line3.setStroke(delaunayColor); line3.setStrokeWidth(delaunayWidth);

                root.getChildren().addAll(line1, line2, line3);
            }
        }


        HBox topBar = SmartFarmUI.getTopBar(stage, "SmartFarm - " + ground.getOwner().getFirstname() + " " + ground.getOwner().getName());
        HBox bottomBar = getBottomBar(stage);
        VBox RightBar = getRightBar(stage, ground);

        BorderPane mainLayout = new BorderPane();
        mainLayout.setTop(topBar);
        mainLayout.setCenter(root);
        mainLayout.setBottom(bottomBar);
        mainLayout.setRight(RightBar);


        return new Scene(mainLayout, 1200, 700);
    }

    private VBox getRightBar(Stage stage, Ground ground){
        Button addSprinklers = new Button("Add Sprinklers");
        Button addWaterTank = new Button("Add WaterTank");
        Button save = new Button("Save your Ground");
        VBox rightBar = new VBox(40,addWaterTank, addSprinklers, save);
        rightBar.setStyle("-fx-background-color: #3a5a30;");
        rightBar.setAlignment(Pos.CENTER);

        save.setOnAction(E->{
            Save register = new Save(ground.getOwner().getFirstname().toLowerCase()+"_"+ground.getOwner().getName().toLowerCase()+"_save");
            try {
                register.writeSave(ground);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        return rightBar;
    }


    private HBox getBottomBar(Stage stage){
        Label legend = new Label("Légende : "+ " 🔴: WaterTanks" + " 🔵 : Sprinklers");
        HBox bottomBar = new HBox(legend);
        bottomBar.setAlignment(Pos.CENTER);
        bottomBar.setStyle("-fx-border-color: #3a5a30;; -fx-border-width: 2px;");
        return bottomBar;
    }
}
