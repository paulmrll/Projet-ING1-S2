package org.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.util.List;

public class MapView {

    private static final String BG_DARK   = "-fx-background-color: #2b4a27;";
    private static final String BG_DARKER = "-fx-background-color: #1f3a1c;";

    private final Ground ground;
    private VBox infoContent;

    public MapView(Ground ground) {
        this.ground = ground;
    }

    public Scene getScene(Stage stage) {
        java.util.Random random = new java.util.Random(42);

        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE;
        for (Field f : ground.getFields()) {
            minX = Math.min(minX, f.getxStart()); minY = Math.min(minY, f.getyStart());
            maxX = Math.max(maxX, f.getxStop());  maxY = Math.max(maxY, f.getyStop());
        }

        double gW = maxX - minX, gH = maxY - minY;

        double mult  = Math.min(920.0 / gW, 600.0 / gH);
        double ofsX  = (970 - gW * mult) / 2.0;
        double ofsY  = (650 - gH * mult) / 2.0;

        // map
        Pane mapPane = new Pane();
        mapPane.setStyle("-fx-background-color: #1a2b18;");

        for (Field f : ground.getFields()) {
            double x = (f.getxStart() - minX) * mult + ofsX;
            double y = (f.getyStart() - minY) * mult + ofsY;
            double w = (f.getxStop()  - f.getxStart()) * mult;
            double h = (f.getyStop()  - f.getyStart()) * mult;

            Rectangle rect = new Rectangle(x, y, w, h);
            rect.setFill(Color.color(0.18 + random.nextDouble() * 0.18,
                                     0.40 + random.nextDouble() * 0.25,
                                     0.10 + random.nextDouble() * 0.15));
            rect.setStroke(Color.color(1, 1, 1, 0.20));
            rect.setStrokeWidth(1.0);

            Label lbl = new Label(f.getName());
            lbl.setStyle("-fx-text-fill: rgba(255,255,255,0.65); -fx-font-size: 11px;");
            lbl.setLayoutX(x + 5); lbl.setLayoutY(y + 4);
            mapPane.getChildren().addAll(rect, lbl);
        }

        if (ground.getTanks() != null && ground.getTanks().size() >= 3) {
            VoronoiDiagram diagram = new VoronoiDiagram(ground.getTanks());

            for (VoronoiCell cell : diagram.getCells()) {
                List<Point> verts = cell.getVertices();
                if (verts.size() < 3) continue;
                Polygon poly = new Polygon();
                for (Point p : verts)
                    poly.getPoints().addAll((p.getX()-minX)*mult+ofsX, (p.getY()-minY)*mult+ofsY);
                poly.setFill(Color.TRANSPARENT);
                poly.setStroke(Color.color(1, 1, 1, 0.30));
                poly.setStrokeWidth(1.0);
                mapPane.getChildren().add(poly);
            }

            for (DelaunayTriangle tri : diagram.getTriangles()) {
                WaterTank[] v = tri.getVertices();
                double[] sx = new double[3], sy = new double[3];
                for (int i = 0; i < 3; i++) {
                    sx[i] = (v[i].getX()-minX)*mult+ofsX;
                    sy[i] = (v[i].getY()-minY)*mult+ofsY;
                }
                for (int i = 0; i < 3; i++) {
                    Line l = new Line(sx[i], sy[i], sx[(i+1)%3], sy[(i+1)%3]);
                    l.setStroke(Color.color(1.0, 0.6, 0.1, 0.55));
                    l.setStrokeWidth(0.9);
                    mapPane.getChildren().add(l);
                }
            }
        }

        for (Sprinkler s : ground.getSprinklers()) {
            double cx = (s.getX()-minX)*mult+ofsX, cy = (s.getY()-minY)*mult+ofsY;
            Circle c = new Circle(cx, cy, 6, Color.web("#5599ee"));
            c.setStroke(Color.WHITE); c.setStrokeWidth(1.5);
            c.setStyle("-fx-cursor: hand;");
            c.setOnMouseClicked(e -> showSprinklerInfo(s));
            c.setOnMouseEntered(e -> c.setRadius(8));
            c.setOnMouseExited(e  -> c.setRadius(6));
            mapPane.getChildren().add(c);
        }

        for (WaterTank w : ground.getTanks()) {
            double cx = (w.getX()-minX)*mult+ofsX, cy = (w.getY()-minY)*mult+ofsY;
            Circle c = new Circle(cx, cy, 7, Color.web("#dd4444"));
            c.setStroke(Color.WHITE); c.setStrokeWidth(1.5);
            c.setStyle("-fx-cursor: hand;");
            c.setOnMouseClicked(e -> showWaterTankInfo(w));
            c.setOnMouseEntered(e -> c.setRadius(9));
            c.setOnMouseExited(e  -> c.setRadius(7));
            mapPane.getChildren().add(c);
        }

        String ownerName = ground.getOwner().getFirstname() + " " + ground.getOwner().getName();
        HBox topBar = SmartFarmUI.getTopBar(stage, "SmartFarm — " + ownerName);

        VBox rightPanel = buildRightPanel(stage);
        rightPanel.setPrefWidth(220);

        BorderPane root = new BorderPane();
        root.setCenter(mapPane);
        root.setRight(rightPanel);
        root.setTop(topBar); // en dernier → au-dessus de la carte

        Scene scene = new Scene(root, 1200, 700);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        return scene;
    }

    // right side

    private VBox buildRightPanel(Stage stage) {
        // section buttons
        VBox buttonsSection = new VBox(12);
        buttonsSection.setStyle(BG_DARK);
        buttonsSection.setAlignment(Pos.TOP_CENTER);
        buttonsSection.setPadding(new Insets(20, 14, 20, 14));
        VBox.setVgrow(buttonsSection, Priority.ALWAYS);

        Label actionsTitle = new Label("Actions");
        actionsTitle.setStyle("-fx-text-fill: #c8e6c4; -fx-font-size: 12px; -fx-font-weight: bold;");

        Button btnAddTank = sideButton("Ajouter un WaterTank");
        Button btnAddSprinkler = sideButton("Ajouter un Sprinkler");
        Button btnAddField = sideButton("Ajouter un Field");
        AddForm addForm = new AddForm();
        btnAddTank.setOnAction(e->{
            stage.setScene(addForm.getWaterTankForm(stage, ground));
        });
        btnAddSprinkler.setOnAction(e->{
            stage.setScene(addForm.getSprinklerForm(stage, ground));
        });
        btnAddField.setOnAction(e->{
            stage.setScene(addForm.getFieldForm(stage, ground));
        });
        Button btnSave = sideButton("Sauvegarder");
        btnSave.setStyle(btnSave.getStyle() + " -fx-background-color: #8a6a10;");
        btnSave.setOnAction(e -> saveGround());

        buttonsSection.getChildren().addAll(actionsTitle, btnAddTank, btnAddSprinkler, btnAddField, btnSave);

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #3a5a30;");

        // section infos
        infoContent = new VBox(10);
        infoContent.setAlignment(Pos.TOP_LEFT);

        Label placeholder = new Label("Cliquez sur un\nélément de la carte.");
        placeholder.setStyle("-fx-text-fill: #7aaa74; -fx-font-size: 12px;");
        infoContent.getChildren().add(placeholder);

        VBox infoSection = new VBox(infoContent);
        infoSection.setStyle(BG_DARKER);
        infoSection.setAlignment(Pos.TOP_LEFT);
        infoSection.setPadding(new Insets(16, 14, 16, 14));
        VBox.setVgrow(infoSection, Priority.ALWAYS);

        Label infoTitle = new Label("Informations");
        infoTitle.setStyle("-fx-text-fill: #c8e6c4; -fx-font-size: 12px; -fx-font-weight: bold;");
        infoSection.getChildren().add(0, infoTitle);
        infoSection.getChildren().add(1, new Label(" ") {{ setStyle("-fx-text-fill: transparent; -fx-font-size: 2px;"); }});


        VBox panel = new VBox(buttonsSection, sep, infoSection);
        panel.setStyle(BG_DARK);
        VBox.setVgrow(buttonsSection, Priority.ALWAYS);
        VBox.setVgrow(infoSection, Priority.ALWAYS);
        return panel;
    }

    private Button sideButton(String text) {
        Button btn = new Button(text);
        btn.setPrefWidth(192);
        btn.setStyle(
            "-fx-background-color: #3a5a30;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 5;" +
            "-fx-padding: 8 12 8 12;" +
            "-fx-font-size: 13px;" +
            "-fx-cursor: hand;"
        );
        return btn;
    }

    // show info
    private void showWaterTankInfo(WaterTank w) {
        infoContent.getChildren().setAll(
            infoRow("ID", String.valueOf(w.getId())),
            infoRow("X", String.format("%.1f", w.getX())),
            infoRow("Y", String.format("%.1f", w.getY())),
            infoRow("Débit", String.format("%.2f", w.getFlow())),
            infoRow("Capacité", String.format("%.2f", w.getCapacity())),
            infoRow("Asperseurs", String.valueOf(ground.countSprinklersFor(w)))
        );
    }

    private void showSprinklerInfo(Sprinkler s) {
        String src = s.getSource() != null ? "Tank #" + s.getSource().getId() : "—";
        infoContent.getChildren().setAll(
            infoRow("ID",     String.valueOf(s.getId())),
            infoRow("X",      String.format("%.1f", s.getX())),
            infoRow("Y",      String.format("%.1f", s.getY())),
            infoRow("Débit",  String.format("%.2f", s.getFlow())),
            infoRow("Rayon",  String.format("%.2f", s.getRadius())),
            infoRow("Source", src)
        );
    }

    private HBox infoRow(String key, String value) {
        Label k = new Label(key + " :");
        k.setStyle("-fx-text-fill: #8dbb88; -fx-font-size: 12px; -fx-min-width: 72px;");
        Label v = new Label(value);
        v.setStyle("-fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold;");
        HBox row = new HBox(6, k, v);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    // Save
    private void saveGround() {
        String name = ground.getOwner().getFirstname().toLowerCase() + "_" + ground.getOwner().getName().toLowerCase() + "_save";
        Save save = new Save("./SmartFarm/src/main/resources/Saves/" + name);
        try {
            save.writeSave(ground);
        } catch (FileNotFoundException e) {
            System.err.println("Erreur sauvegarde : " + e.getMessage());
        }
    }
}
