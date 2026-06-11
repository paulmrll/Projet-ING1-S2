package org.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.transform.Scale;
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

    private static final String BG_DARK = "-fx-background-color: #2b4a27;";
    private static final String BG_DARKER = "-fx-background-color: #1f3a1c;";
    private static final double ZOOM_STEP = 0.25;
    private static final double ZOOM_MIN = 0.25;
    private static final double ZOOM_MAX = 3.0;

    private static final double MAP_W = 970;
    private static final double MAP_H = 650;

    private final Ground ground;
    private VBox infoContent;
    private Group mapGroup;
    private Pane  viewport;
    private final Scale scaleTransform = new Scale(1, 1, 0, 0);
    private double zoomLevel = 1.0;
    private Label zoomLabel;

    // pour le pan
    private double dragOriginX, dragOriginY;

    public MapView(Ground ground) {
        this.ground = ground;
    }

    public Scene getScene(Stage stage) {
        java.util.Random random = new java.util.Random(42);

        // borne
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE;
        for (Field f : ground.getFields()) {
            minX = Math.min(minX, f.getxStart()); minY = Math.min(minY, f.getyStart());
            maxX = Math.max(maxX, f.getxStop());  maxY = Math.max(maxY, f.getyStop());
        }

        double gW = maxX - minX, gH = maxY - minY;
        double mult = Math.min(920.0 / gW, 600.0 / gH);
        double ofsX = (970 - gW * mult) / 2.0;
        double ofsY = (650 - gH * mult) / 2.0;

        // map
        Pane mapPane = new Pane();
        mapPane.setPrefSize(970, 650);

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
            c.setOnMouseClicked(e -> { showSprinklerInfo(stage, s); e.consume(); });
            c.setOnMouseEntered(e -> c.setRadius(8));
            c.setOnMouseExited(e  -> c.setRadius(6));
            mapPane.getChildren().add(c);
        }

        for (WaterTank w : ground.getTanks()) {
            double cx = (w.getX()-minX)*mult+ofsX, cy = (w.getY()-minY)*mult+ofsY;
            Circle c = new Circle(cx, cy, 7, Color.web("#dd4444"));
            c.setStroke(Color.WHITE); c.setStrokeWidth(1.5);
            c.setStyle("-fx-cursor: hand;");
            c.setOnMouseClicked(e -> { showWaterTankInfo(stage, w); e.consume(); });
            c.setOnMouseEntered(e -> c.setRadius(9));
            c.setOnMouseExited(e  -> c.setRadius(7));
            mapPane.getChildren().add(c);
        }

        // zoom + dezoom
        mapGroup = new Group(mapPane);
        mapGroup.getTransforms().add(scaleTransform); // pivot fixe à (0,0)

        viewport = new Pane(mapGroup);
        viewport.setStyle("-fx-background-color: #1c1c1e;");
        viewport.setClip(new Rectangle(0, 0, 10000, 10000));

        // map movements
        viewport.setOnMousePressed(e -> {
            dragOriginX = e.getSceneX() - mapGroup.getTranslateX();
            dragOriginY = e.getSceneY() - mapGroup.getTranslateY();
            viewport.setStyle("-fx-background-color: #1c1c1e; -fx-cursor: closed-hand;");
        });
        viewport.setOnMouseDragged(e -> {
            mapGroup.setTranslateX(e.getSceneX() - dragOriginX);
            mapGroup.setTranslateY(e.getSceneY() - dragOriginY);
        });
        viewport.setOnMouseReleased(e ->
            viewport.setStyle("-fx-background-color: #1c1c1e;")
        );

        // layout
        String ownerName = ground.getOwner().getFirstname() + " " + ground.getOwner().getName();
        HBox topBar = SmartFarmUI.getTopBar(stage, "SmartFarm — " + ownerName);

        VBox rightPanel = buildRightPanel(stage);
        rightPanel.setPrefWidth(220);

        BorderPane root = new BorderPane();
        root.setCenter(viewport);
        root.setRight(rightPanel);
        root.setTop(topBar);

        Scene scene = new Scene(root, 1200, 700);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        return scene;
    }


    private void applyZoom(double factor) {
        double oldZoom = zoomLevel;
        zoomLevel = Math.max(ZOOM_MIN, Math.min(ZOOM_MAX, zoomLevel + factor));

        double cx = viewport.getWidth()  / 2.0;
        double cy = viewport.getHeight() / 2.0;
        double mapCx = (cx - mapGroup.getTranslateX()) / oldZoom;
        double mapCy = (cy - mapGroup.getTranslateY()) / oldZoom;

        scaleTransform.setX(zoomLevel);
        scaleTransform.setY(zoomLevel);
        mapGroup.setTranslateX(cx - mapCx * zoomLevel);
        mapGroup.setTranslateY(cy - mapCy * zoomLevel);

        zoomLabel.setText((int)(zoomLevel * 100) + "%");
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
        Button btnModifyUser = sideButton("Modify User");
        AddForm addForm = new AddForm();
        btnAddTank.setOnAction(e      -> stage.setScene(addForm.getWaterTankScene(stage, ground)));
        btnAddSprinkler.setOnAction(e -> stage.setScene(addForm.getSprinklerScene(stage, ground)));
        btnAddField.setOnAction(e     -> stage.setScene(addForm.getFieldScene(stage, ground)));

        Button btnSave = sideButton("Sauvegarder");
        btnSave.setStyle(btnSave.getStyle() + " -fx-background-color: #8a6a10;");
        btnSave.setOnAction(e -> saveGround());
        btnModifyUser.setOnAction(e->{
            stage.setScene(AddForm.modifyUser(stage, ground));
        });

        // zoom visuals
        Label zoomTitle = new Label("Zoom");
        zoomTitle.setStyle("-fx-text-fill: #c8e6c4; -fx-font-size: 12px; -fx-font-weight: bold;");

        Button btnZoomOut = zoomButton("−");
        Button btnZoomIn  = zoomButton("+");
        zoomLabel = new Label("100%");
        zoomLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-min-width: 44px; -fx-alignment: center;");

        btnZoomIn.setOnAction(e  -> applyZoom(ZOOM_STEP));
        btnZoomOut.setOnAction(e -> applyZoom(-ZOOM_STEP));

        HBox zoomBar = new HBox(6, btnZoomOut, zoomLabel, btnZoomIn);
        zoomBar.setAlignment(Pos.CENTER);

        buttonsSection.getChildren().addAll(
            actionsTitle, btnAddTank, btnAddSprinkler, btnAddField, btnModifyUser, btnSave,
            zoomTitle, zoomBar
        );

        // separator
        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #3a5a30;");

        // section infos
        infoContent = new VBox(10);
        infoContent.setAlignment(Pos.TOP_LEFT);

        Label placeholder = new Label("Cliquez sur un\nélément de la carte.");
        placeholder.setStyle("-fx-text-fill: #7aaa74; -fx-font-size: 12px;");
        infoContent.getChildren().add(placeholder);

        Label infoTitle = new Label("Informations");
        infoTitle.setStyle("-fx-text-fill: #c8e6c4; -fx-font-size: 12px; -fx-font-weight: bold;");

        VBox infoSection = new VBox(8, infoTitle, infoContent);
        infoSection.setStyle(BG_DARKER);
        infoSection.setAlignment(Pos.TOP_LEFT);
        infoSection.setPadding(new Insets(16, 14, 16, 14));
        VBox.setVgrow(infoSection, Priority.ALWAYS);

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

    private Button zoomButton(String text) {
        Button btn = new Button(text);
        btn.setPrefWidth(36);
        btn.setStyle(
            "-fx-background-color: #3a5a30;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 5;" +
            "-fx-font-size: 16px;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 2 8 2 8;"
        );
        return btn;
    }

    // show info
    private void showWaterTankInfo(Stage stage, WaterTank w) {
        infoContent.getChildren().setAll(
            infoRow("ID", String.valueOf(w.getId())),
            infoRow("X", String.format("%.1f", w.getX())),
            infoRow("Y", String.format("%.1f", w.getY())),
            infoRow("Débit", String.format("%.2f", w.getFlow())),
            infoRow("Capacité", String.format("%.2f", w.getCapacity())),
            infoRow("Asperseurs", String.valueOf(ground.countSprinklersFor(w)))
        );
        Button modify = new Button("Modify");
        modify.setOnAction(e->{
            stage.setScene(AddForm.modifyTanksSprinklers(stage, w, ground));
        });
        infoContent.getChildren().add(modify);
    }

    private void showSprinklerInfo(Stage stage, Sprinkler s) {
        String src = s.getSource() != null ? "Tank #" + s.getSource().getId() : "—";
        infoContent.getChildren().setAll(
            infoRow("ID",     String.valueOf(s.getId())),
            infoRow("X",      String.format("%.1f", s.getX())),
            infoRow("Y",      String.format("%.1f", s.getY())),
            infoRow("Débit",  String.format("%.2f", s.getFlow())),
            infoRow("Rayon",  String.format("%.2f", s.getRadius())),
            infoRow("Source", src)
        );
        Button modify = new Button("Modify");
        modify.setOnAction(e->{
            stage.setScene(AddForm.modifyTanksSprinklers(stage, s, ground));
        });
        infoContent.getChildren().add(modify);
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
