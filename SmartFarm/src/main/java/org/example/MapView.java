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
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class MapView {
    private Ground ground;

    public MapView(Ground ground){
        this.ground = ground;
    }

    public Scene getScene(Stage stage, Ground ground){
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

            // Couleur aléatoire lumineuse
            double r = 0.3 + random.nextDouble() * 0.4;
            double g = 0.6 + random.nextDouble() * 0.4; // Teinte verte dominante
            double b = 0.2 + random.nextDouble() * 0.4;
            fieldSurface.setFill(Color.color(r, g, b));

            fieldSurface.setStroke(Color.WHITE);
            fieldSurface.setStrokeWidth(1.5);

            root.getChildren().add(fieldSurface);
        }

        // 4. Dessiner les Sprinklers (Arroseurs)
        for (Sprinkler s : ground.getSprinklers()){
            // Calcul de sa position ajustée à l'écran
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

        HBox topBar = getTopBar(stage);
        HBox bottomBar = getBottomBar(stage);
        VBox RightBar = getRightBar(stage);

        BorderPane mainLayout = new BorderPane();
        mainLayout.setTop(topBar);
        mainLayout.setCenter(root);
        mainLayout.setBottom(bottomBar);
        mainLayout.setRight(RightBar);


        return new Scene(mainLayout, 1200, 700);
    }

    private VBox getRightBar(Stage stage){
        Button addSprinklers = new Button("Add Sprinklers");
        Button addWaterTank = new Button("Add WaterTank");
        VBox rightBar = new VBox(40,addWaterTank, addSprinklers);
        rightBar.setStyle("-fx-background-color: #3a5a30;");
        rightBar.setAlignment(Pos.CENTER);
        return rightBar;
    }

    private HBox getBottomBar(Stage stage){
        Label legend = new Label("Légende : "+ " 🔴: WaterTanks" + " 🔵 : Sprinklers");
        HBox bottomBar = new HBox(legend);
        bottomBar.setAlignment(Pos.CENTER);
        bottomBar.setStyle("-fx-border-color: #3a5a30;; -fx-border-width: 2px;");
        return bottomBar;
    }

    private HBox getTopBar(Stage stage){
        Label title = new Label("SmartFarm - " + ground.getOwner().getFirstname() + " " + ground.getOwner().getName());
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        title.setTextFill(Color.WHITE);

        Button btnBack = new Button("<- Retour");
        btnBack.setStyle(
                "-fx-background-color : #3a5a30;" + "-fx-text-fill : white;" + "-fx-background-radius : 6;"
        );

        HBox topBar = new HBox(20, btnBack, title);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color : #1e3e1a; -fx-padding : 10 20 10 20");
        btnBack.setOnAction(e -> {
            SmartFarmUI menu = new SmartFarmUI();
            try {
                menu.start(stage);
            } catch (Exception ex) {
                System.out.println("Erreur retour menu : " + ex.getMessage());
            }
        });
        return topBar;
    }
}
