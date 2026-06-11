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
import java.util.ArrayList;
import java.util.List;

public class MapView {

    private static final String BG_DARK = "-fx-background-color: #2b4a27;";
    private static final String BG_DARKER = "-fx-background-color: #1f3a1c;";
    private static final double ZOOM_STEP = 0.25;
    private static final double ZOOM_MIN = 0.25;
    private static final double ZOOM_MAX = 3.0;


    private Circle temporaryCircle = null;
    private List<Circle> temporaryCircleListSprinklers = new ArrayList<>();

    private double nodeDragOffsetX;
    private double nodeDragOffsetY;
    double realX = -2;
    double realY = -2;
    double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
    double maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE;

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
    private boolean isDragging = false;
    private String placingMode = null; // "TANK" ou "SPRINKLER"
    private boolean showDelaunay = false;
    private boolean showVoronoi = false;
    private boolean showRadius = false;

    public MapView(Ground ground) {
        this.ground = ground;
    }

    public Scene getScene(Stage stage) {
        java.util.Random random = new java.util.Random(42);

        // borne
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

            if (showVoronoi) {
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
            }

            if (showDelaunay) {
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
        }

        for (Sprinkler s : ground.getSprinklers()) {
            double cx = (s.getX()-minX)*mult+ofsX, cy = (s.getY()-minY)*mult+ofsY;

            if (showRadius) {
                Circle radiusCircle = new Circle(cx, cy, s.getRadius() * mult);
                radiusCircle.setFill(Color.color(0.33, 0.60, 0.93, 0.10));
                radiusCircle.setStroke(s.isActive()
                    ? Color.color(0.33, 0.93, 0.50, 0.60)
                    : Color.color(0.33, 0.60, 0.93, 0.40));
                radiusCircle.setStrokeWidth(1.0);
                radiusCircle.setMouseTransparent(true);
                mapPane.getChildren().add(radiusCircle);
            }

            Circle c = new Circle(cx, cy, 6, Color.web("#5599ee"));
            c.setStroke(Color.WHITE); c.setStrokeWidth(1.5);
            c.setStyle("-fx-cursor: hand;");
            c.setOnMouseClicked(e -> { showSprinklerInfo(c, stage, s, mult, ofsX, ofsY, mapPane); e.consume(); });
            c.setOnMouseEntered(e -> c.setRadius(8));
            c.setOnMouseExited(e  -> c.setRadius(6));
            mapPane.getChildren().add(c);
        }

        for (WaterTank w : ground.getTanks()) {
            double cx = (w.getX()-minX)*mult+ofsX, cy = (w.getY()-minY)*mult+ofsY;
            Circle c = new Circle(cx, cy, 7, Color.web("#dd4444"));
            c.setStroke(Color.WHITE); c.setStrokeWidth(1.5);
            c.setStyle("-fx-cursor: hand;");
            c.setOnMouseClicked(e -> { showWaterTankInfo(c, stage, w, mult, ofsX, ofsY, mapPane); e.consume(); });
            c.setOnMouseEntered(e -> c.setRadius(9));
            c.setOnMouseExited(e  -> c.setRadius(7));
            mapPane.getChildren().add(c);
        }

        // zoom + dezoom
        mapGroup = new Group(mapPane);
        mapGroup.getTransforms().add(scaleTransform);

        viewport = new Pane(mapGroup);
        viewport.setStyle("-fx-background-color: #1c1c1e;");
        viewport.setClip(new Rectangle(0, 0, 10000, 10000));

        // map movements
        viewport.setOnMousePressed(e -> {
            dragOriginX = e.getSceneX() - mapGroup.getTranslateX();
            dragOriginY = e.getSceneY() - mapGroup.getTranslateY();
            isDragging = false;
            viewport.setStyle("-fx-background-color: #1c1c1e; -fx-cursor: closed-hand;");
        });
        viewport.setOnMouseDragged(e -> {
            isDragging = true;
            mapGroup.setTranslateX(e.getSceneX() - dragOriginX);
            mapGroup.setTranslateY(e.getSceneY() - dragOriginY);
        });
        viewport.setOnMouseReleased(e -> {
            viewport.setStyle("-fx-background-color: #1c1c1e;");
            if (!isDragging && placingMode != null) {
                double clickX = (e.getX() - mapGroup.getTranslateX()) / zoomLevel;
                double clickY = (e.getY() - mapGroup.getTranslateY()) / zoomLevel;
                double realClickX = (clickX - ofsX) / mult + minX;
                double realClickY = (clickY - ofsY) / mult + minY;
                showPlacementForm(stage, realClickX, realClickY);
            }
        });

        // layout
        String ownerName = ground.getOwner().getFirstname() + " " + ground.getOwner().getName();
        HBox topBar = SmartFarmUI.getTopBar(stage, "SmartFarm — " + ownerName);

        VBox rightPanel = buildRightPanel(stage);
        rightPanel.setPrefWidth(220);

        BorderPane root = new BorderPane();
        root.setCenter(viewport);
        root.setRight(rightPanel);
        root.setTop(topBar);

        Scene scene = new Scene(root, 1300, 800);
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

        Button btnAddField = sideButton("Ajouter un Field");
        Button btnModifyUser = sideButton("Modify User");
        AddForm addForm = new AddForm();
        btnAddField.setOnAction(e -> stage.setScene(addForm.getFieldScene(stage, ground)));

        Button btnDelaunay = sideButton("Voir Delaunay");
        if (showDelaunay) btnDelaunay.setStyle(sideButtonStyle() + " -fx-background-color: #7a5010;");
        btnDelaunay.setOnAction(e -> {
            showDelaunay = !showDelaunay;
            stage.setScene(getScene(stage));
        });

        Button btnVoronoi = sideButton("Voir Voronoï");
        if (showVoronoi) btnVoronoi.setStyle(sideButtonStyle() + " -fx-background-color: #10507a;");
        btnVoronoi.setOnAction(e -> {
            showVoronoi = !showVoronoi;
            stage.setScene(getScene(stage));
        });

        Button btnRadius = sideButton("Voir Rayons");
        if (showRadius) btnRadius.setStyle(sideButtonStyle() + " -fx-background-color: #3a3a8b;");
        btnRadius.setOnAction(e -> {
            showRadius = !showRadius;
            stage.setScene(getScene(stage));
        });

        Button btnPlaceTank = sideButton("📍 Place Tank");
        Button btnPlaceSprinkler = sideButton("📍 Place Sprinkler");

        btnPlaceTank.setOnAction(e -> {
            if ("TANK".equals(placingMode)) {
                placingMode = null;
                btnPlaceTank.setStyle(sideButtonStyle());
            } else {
                placingMode = "TANK";
                btnPlaceTank.setStyle(sideButtonStyle() + " -fx-background-color: #1a6b3a;");
                btnPlaceSprinkler.setStyle(sideButtonStyle());
            }
        });

        btnPlaceSprinkler.setOnAction(e -> {
            if ("SPRINKLER".equals(placingMode)) {
                placingMode = null;
                btnPlaceSprinkler.setStyle(sideButtonStyle());
            } else {
                placingMode = "SPRINKLER";
                btnPlaceSprinkler.setStyle(sideButtonStyle() + " -fx-background-color: #1a4a6b;");
                btnPlaceTank.setStyle(sideButtonStyle());
            }
        });

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
            actionsTitle, btnAddField, btnModifyUser, btnSave,
            btnDelaunay, btnVoronoi, btnRadius,
            btnPlaceTank, btnPlaceSprinkler,
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

        javafx.scene.control.ScrollPane scrollButtons = new javafx.scene.control.ScrollPane(buttonsSection);
        scrollButtons.setFitToWidth(true);
        scrollButtons.setHbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);
        scrollButtons.setVbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);
        scrollButtons.setStyle("-fx-background: #2b4a27; -fx-background-color: #2b4a27; -fx-border-color: transparent;");
        VBox.setVgrow(scrollButtons, Priority.ALWAYS);

        VBox panel = new VBox(scrollButtons, sep, infoSection);
        panel.setStyle(BG_DARK);
        VBox.setVgrow(buttonsSection, Priority.ALWAYS);
        VBox.setVgrow(infoSection, Priority.ALWAYS);
        return panel;
    }

    private String sideButtonStyle() {
        return "-fx-background-color: #3a5a30;" +
               "-fx-text-fill: white;" +
               "-fx-background-radius: 5;" +
               "-fx-padding: 8 12 8 12;" +
               "-fx-font-size: 13px;" +
               "-fx-cursor: hand;";
    }

    private Button sideButton(String text) {
        Button btn = new Button(text);
        btn.setPrefWidth(192);
        btn.setStyle(sideButtonStyle());
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

    private void showPlacementForm(Stage stage, double x, double y) {
        javafx.scene.control.TextField flowField = new javafx.scene.control.TextField();
        flowField.setPromptText("Flow");
        flowField.setMaxWidth(192);

        if ("TANK".equals(placingMode)) {
            javafx.scene.control.TextField capacityField = new javafx.scene.control.TextField();
            capacityField.setPromptText("Capacity");
            capacityField.setMaxWidth(192);

            Button confirm = sideButton("Add Tank here");
            confirm.setStyle(sideButtonStyle() + " -fx-background-color: #1a6b3a;");
            confirm.setOnAction(e -> {
                try {
                    double flow = Double.parseDouble(flowField.getText());
                    double capacity = Double.parseDouble(capacityField.getText());
                    ground.addTank(new WaterTank(x, y, capacity, flow));
                    placingMode = null;
                    stage.setScene(getScene(stage));
                } catch (NumberFormatException ex) {
                    flowField.setStyle("-fx-border-color: red;");
                }
            });

            infoContent.getChildren().setAll(
                infoRow("X", String.format("%.1f", x)),
                infoRow("Y", String.format("%.1f", y)),
                flowField, capacityField, confirm
            );
        } else if ("SPRINKLER".equals(placingMode)) {
            javafx.scene.control.TextField radiusField = new javafx.scene.control.TextField();
            radiusField.setPromptText("Radius");
            radiusField.setMaxWidth(192);

            Button confirm = sideButton("Add Sprinkler here");
            confirm.setStyle(sideButtonStyle() + " -fx-background-color: #1a4a6b;");
            confirm.setOnAction(e -> {
                try {
                    double flow = Double.parseDouble(flowField.getText());
                    double radius = Double.parseDouble(radiusField.getText());
                    ground.addSprinkler(new Sprinkler(x, y, flow, radius));
                    placingMode = null;
                    stage.setScene(getScene(stage));
                } catch (NumberFormatException ex) {
                    flowField.setStyle("-fx-border-color: red;");
                }
            });

            infoContent.getChildren().setAll(
                infoRow("X", String.format("%.1f", x)),
                infoRow("Y", String.format("%.1f", y)),
                flowField, radiusField, confirm
            );
        }
    }

    private void showWaterTankInfo(Circle c, Stage stage, WaterTank w,double mult, double ofsX, double ofsY, Pane mapPane) {
        removeTemporaryCircle(mapPane);
        removeTemporaryCircleList(mapPane);
        infoContent.getChildren().setAll(
            infoRow("ID", String.valueOf(w.getId())),
            infoRow("X", String.format("%.1f", w.getX())),
            infoRow("Y", String.format("%.1f", w.getY())),
            infoRow("Débit", String.format("%.2f", w.getFlow())),
            infoRow("Capacité", String.format("%.2f", w.getCapacity())),
            infoRow("Asperseurs", String.valueOf(ground.countSprinklersFor(w)))
        );
        for (Sprinkler s : ground.getSprinklers()){
            if (s.getSource() == w){
                double cx = (s.getX()-minX)*mult+ofsX, cy = (s.getY()-minY)*mult+ofsY;
                Circle cover = new Circle(cx, cy, 7, Color.YELLOW);
                cover.setStroke(Color.WHITE); cover.setStrokeWidth(1.5);
                cover.setStyle("-fx-cursor: hand;");
                mapPane.getChildren().add(cover);
                temporaryCircleListSprinklers.add(cover);
            }
        }
        Button modify = sideButton("Modify");
        Button refill = sideButton("Refill");
        refill.setStyle(refill.getStyle() + " -fx-background-color: #1a5a8b;");
        refill.setOnAction(e -> {
            w.refill();
            showWaterTankInfo(c, stage, w, mult, ofsX, ofsY, mapPane);
        });
        Button delete = sideButton("Delete");
        delete.setStyle(delete.getStyle() + " -fx-background-color: #8b2020;");
        c.setOnMousePressed(e -> {
            nodeDragOffsetX = e.getSceneX() - c.getLayoutX();
            nodeDragOffsetY = e.getSceneY() - c.getLayoutY();
            c.setStyle("-fx-cursor: move;");
            e.consume();
        });

        c.setOnMouseDragged(e->{
            double newLayoutX = e.getSceneX() - nodeDragOffsetX;
            double newLayoutY = e.getSceneY() - nodeDragOffsetY;
            c.setLayoutX(newLayoutX);
            c.setLayoutY(newLayoutY);
            realX = ((newLayoutX + c.getCenterX() - ofsX) / mult) + minX;
            realY = ((newLayoutY + c.getCenterY() - ofsY) / mult) + minY;
            w.setX(realX);
            w.setY(realY);
            e.consume();
        });
        modify.setOnAction(e->{
            for (Sprinkler s : ground.getSprinklers()){
                ground.findSource(s);
            }
            stage.setScene(getScene(stage));
        });
        delete.setOnAction(e -> {
            ground.removeTank(w);
            stage.setScene(getScene(stage));
        });
        infoContent.getChildren().addAll(delete, refill, modify);
    }

    private void showSprinklerInfo(Circle c,Stage stage, Sprinkler s, double mult, double ofsX, double ofsY, Pane mapPane) {
        removeTemporaryCircleList(mapPane);
        for (Field f : ground.getFields()) {
            minX = Math.min(minX, f.getxStart());
            minY = Math.min(minY, f.getyStart());
        }
        String src = s.getSource() != null ? "Tank #" + s.getSource().getId() : "—";
        infoContent.getChildren().setAll(
            infoRow("ID",     String.valueOf(s.getId())),
            infoRow("X",      String.format("%.1f", s.getX())),
            infoRow("Y",      String.format("%.1f", s.getY())),
            infoRow("Débit",  String.format("%.2f", s.getFlow())),
            infoRow("Rayon",  String.format("%.2f", s.getRadius())),
            infoRow("Activate", String.valueOf(s.getStatusActivation())),
            infoRow("Source", src)
        );
        removeTemporaryCircle(mapPane);
        for (WaterTank w : ground.getTanks()){
            double cx = (w.getX()-minX)*mult+ofsX, cy = (w.getY()-minY)*mult+ofsY;
            if (w.getId() == s.getSource().getId()){
                Circle cover = new Circle(cx, cy, 7, Color.YELLOW);
                cover.setStroke(Color.WHITE); cover.setStrokeWidth(1.5);
                cover.setStyle("-fx-cursor: hand;");
                temporaryCircle = cover;
                mapPane.getChildren().add(cover);
                cover.setOnMouseClicked(e->{showWaterTankInfo(c, stage, w, mult, ofsX, ofsY, mapPane);});
            }
        }
        VBox button = new VBox();
        Button modify = sideButton("MODIFY");
        Button delete = sideButton("DELETE");
        delete.setStyle(delete.getStyle() + " -fx-background-color: #8b2020;");
        delete.setOnAction(e -> {
            ground.removeSprinkler(s);
            stage.setScene(getScene(stage));
        });
        button.setSpacing(20);
        if (s.getStatusActivation()) {
            Button activation = sideButton("DESACTIVATE");
            activation.setOnAction(e -> {
                s.deactivate();
                showSprinklerInfo(c, stage, s, mult, ofsX, ofsY, mapPane);
            });
            button.getChildren().addAll(delete, modify, activation);
        } else {
            Button activation = sideButton("ACTIVATE");
            activation.setOnAction(e -> {
                boolean ok = s.activate();
                if (!ok) {
                    Label warn = new Label("Tank vide ou non assigné !");
                    warn.setStyle("-fx-text-fill: #ff6b6b; -fx-font-size: 11px;");
                    button.getChildren().add(warn);
                } else {
                    showSprinklerInfo(c, stage, s, mult, ofsX, ofsY, mapPane);
                }
            });
            button.getChildren().addAll(delete, modify, activation);
        }

        c.setOnMousePressed(e -> {
            nodeDragOffsetX = e.getSceneX() - c.getLayoutX();
            nodeDragOffsetY = e.getSceneY() - c.getLayoutY();
            c.setStyle("-fx-cursor: move;");
            e.consume();
        });

        c.setOnMouseDragged(e->{
            double newLayoutX = e.getSceneX() - nodeDragOffsetX;
            double newLayoutY = e.getSceneY() - nodeDragOffsetY;
            c.setLayoutX(newLayoutX);
            c.setLayoutY(newLayoutY);
            realX = ((newLayoutX + c.getCenterX() - ofsX) / mult) + minX;
            realY = ((newLayoutY + c.getCenterY() - ofsY) / mult) + minY;
            s.setX(realX);
            s.setY(realY);
            e.consume();
        });
        modify.setOnAction(e->{
            ground.findSource(s);
            stage.setScene(getScene(stage));
        });

        infoContent.getChildren().add(button);
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

    private void saveGround() {
        String name = ground.getOwner().getFirstname().toLowerCase() + "_" + ground.getOwner().getName().toLowerCase() + "_save";
        Save save = new Save("./SmartFarm/src/main/resources/Saves/" + name);
        try {
            save.writeSave(ground);
        } catch (FileNotFoundException e) {
            System.err.println("Erreur sauvegarde : " + e.getMessage());
        }
    }

    private void removeTemporaryCircle(Pane mapPane){
        if (temporaryCircle != null){
            mapPane.getChildren().remove(temporaryCircle);
        }
    }
    private void removeTemporaryCircleList(Pane mapPane) {
        if (temporaryCircleListSprinklers != null) {
            mapPane.getChildren().removeAll(temporaryCircleListSprinklers);
            temporaryCircleListSprinklers.clear();
        }
    }
}