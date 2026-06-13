package org.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
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

/**
 * The {@code MapView} class manages the primary interactive dashboard and map view
 * of the smart farm. It handles spatial coordinate transformations, zooming, panning,
 * rendering of fields, sprinklers, and water tanks, as well as visualizing Delaunay
 * triangulations and Voronoi diagrams. It also facilitates real-time element drag-and-drop
 * and context-sensitive action handling.
 *
 * @author SmartFarm Team
 * @version 1.0
 */
public class MapView {

    /** Inline CSS style for the primary dark background on side panels. */
    private static final String BG_DARK = "-fx-background-color: #2b4a27;";

    /** Inline CSS style for the darker background variant used in sub-sections. */
    private static final String BG_DARKER = "-fx-background-color: #1f3a1c;";

    /** The constant zoom adjustment value applied per zoom step. */
    private static final double ZOOM_STEP = 0.25;

    /** The minimum bounding limit for the layout zoom scale. */
    private static final double ZOOM_MIN = 0.25;

    /** The maximum bounding limit for the layout zoom scale. */
    private static final double ZOOM_MAX = 3.0;

    /** Reference used to track a highlighted or newly updated circle on the map canvas. */
    private Circle temporaryCircle = null;

    /** Collection tracking dynamically drawn context indicators for sprinklers. */
    private List<Circle> temporaryCircleListSprinklers = new ArrayList<>();

    /** Horizontal mouse offset tracking variable used during node drag-and-drop operations. */
    private double nodeDragOffsetX;

    /** Vertical mouse offset tracking variable used during node drag-and-drop operations. */
    private double nodeDragOffsetY;

    /** Real calculated mathematical horizontal coordinate of an element post-translation. */
    double realX = -2;

    /** Real calculated mathematical vertical coordinate of an element post-translation. */
    double realY = -2;

    /** Extents tracking variables used to establish the terrain global coordinate bounds. */
    double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
    double maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE;

    /** Fixed default render width constraint for the graphical map area. */
    private static final double MAP_W = 970;

    /** Fixed default render height constraint for the graphical map area. */
    private static final double MAP_H = 650;

    /** The underlying farm architecture data model linked to this view panel. */
    private final Ground ground;

    /** Container layout managing the dynamic display of context properties. */
    private VBox infoContent;

    /** Core graphical parent node combining map layouts to manage scale transforms. */
    private Group mapGroup;

    /** Primary interactive window canvas processing mouse inputs and panning events. */
    private Pane viewport;

    /** JavaFX transformation tracking structure scaling map canvas coordinates. */
    private final Scale scaleTransform = new Scale(1, 1, 0, 0);

    /** Active numeric value representing the map visualization scale. */
    private double zoomLevel = 1.0;

    /** Text component displaying the current map magnification percentage. */
    private Label zoomLabel;

    /** Horizontal coordinate tracking origin point when initiating canvas pan dragging. */
    private double dragOriginX;

    /** Vertical coordinate tracking origin point when initiating canvas pan dragging. */
    private double dragOriginY;

    /** Logical gate keeping track of map drag-to-pan movements. */
    private boolean isDragging = false;

    /** Token tracking active placement operations ("TANK", "SPRINKLER", or "FIELD"). */
    private String placingMode = null;

    /** Reference tracking the initial vertex registered when building a custom field layout. */
    private Point fieldFirstPoint = null;

    /** Interactive text input tracking field name inputs during sequential creation. */
    private javafx.scene.control.TextField fieldNameInput = null;

    /** Visual marker highlighting the initial anchor point of a newly drafted field layout. */
    private Circle fieldMarker = null;

    /** Flag controlling the visibility of the Delaunay triangulation network overlays. */
    private boolean showDelaunay = false;

    /** Flag controlling the visibility of the Voronoi partition geometry overlays. */
    private boolean showVoronoi = false;

    /** Flag controlling whether operational sprinkler spray radiuses are drawn. */
    private boolean showRadius = false;

    /**
     * Constructs a new {@code MapView} instance mapped to the given ground infrastructure.
     *
     * @param ground the {@link Ground} terrain object tracking farm asset records.
     */
    public MapView(Ground ground) {
        this.ground = ground;
    }

    /**
     * Constructs, styles, and links the complete interactive JavaFX scene of the dashboard map view.
     * Processes scaling algorithms to compute aspect fits, generates geometric overlays for assets,
     * sets up structural interaction events, and anchors coordinate status bars.
     *
     * @param stage the primary operational {@link Stage} context used for navigation routing.
     * @return a structured, style-sheet linked interactive {@link Scene}.
     */
    public Scene getScene(Stage stage) {
        java.util.Random random = new java.util.Random(42);

        // borne
        for (Field f : ground.getFields()) {
            minX = Math.min(minX, f.getxStart());
            minY = Math.min(minY, f.getyStart());
            maxX = Math.max(maxX, f.getxStop());
            maxY = Math.max(maxY, f.getyStop());
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
            double w = (f.getxStop() - f.getxStart()) * mult;
            double h = (f.getyStop() - f.getyStart()) * mult;

            Rectangle rect = new Rectangle(x, y, w, h);
            rect.setFill(Color.color(0.18 + random.nextDouble() * 0.18,
                    0.40 + random.nextDouble() * 0.25,
                    0.10 + random.nextDouble() * 0.15));
            rect.setStroke(Color.color(1, 1, 1, 0.20));
            rect.setStrokeWidth(1.0);
            rect.setStyle("-fx-cursor: hand;");
            rect.setOnMouseClicked(e -> {
                if (placingMode == null) {
                    showFieldInfo(stage, f);
                    e.consume();
                }
            });
            rect.setOnMouseEntered(e -> {
                if (placingMode == null) rect.setStroke(Color.color(1, 1, 0.4, 0.8));
            });
            rect.setOnMouseExited(e -> rect.setStroke(Color.color(1, 1, 1, 0.20)));

            Label lbl = new Label(f.getName());
            lbl.setStyle("-fx-text-fill: rgba(255,255,255,0.80); -fx-font-size: 11px; -fx-cursor: hand;");
            lbl.setLayoutX(x + 5);
            lbl.setLayoutY(y + 4);
            lbl.setOnMouseClicked(e -> {
                if (placingMode == null) {
                    showFieldInfo(stage, f);
                    e.consume();
                }
            });
            mapPane.getChildren().addAll(rect, lbl);
        }

        if (ground.getTanks() != null && ground.getTanks().size() >= 3) {
            VoronoiDiagram diagram = ground.getVoronoiDiagram();

            if (showVoronoi) {
                for (VoronoiCell cell : diagram.getCells()) {
                    List<Point> verts = cell.getVertices();
                    if (verts.size() < 3) continue;
                    Polygon poly = new Polygon();
                    for (Point p : verts)
                        poly.getPoints().addAll((p.getX() - minX) * mult + ofsX, (p.getY() - minY) * mult + ofsY);
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
                        sx[i] = (v[i].getX() - minX) * mult + ofsX;
                        sy[i] = (v[i].getY() - minY) * mult + ofsY;
                    }
                    for (int i = 0; i < 3; i++) {
                        Line l = new Line(sx[i], sy[i], sx[(i + 1) % 3], sy[(i + 1) % 3]);
                        l.setStroke(Color.color(1.0, 0.6, 0.1, 0.55));
                        l.setStrokeWidth(0.9);
                        mapPane.getChildren().add(l);
                    }
                }
            }
        }

        for (Sprinkler s : ground.getSprinklers()) {
            double cx = (s.getX() - minX) * mult + ofsX, cy = (s.getY() - minY) * mult + ofsY;

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
            c.setStroke(Color.WHITE);
            c.setStrokeWidth(1.5);
            c.setStyle("-fx-cursor: hand;");
            c.setOnMouseClicked(e -> {
                showSprinklerInfo(c, stage, s, mult, ofsX, ofsY, mapPane);
                e.consume();
            });
            c.setOnMouseEntered(e -> c.setRadius(8));
            c.setOnMouseExited(e -> c.setRadius(6));
            mapPane.getChildren().add(c);
        }

        for (WaterTank w : ground.getTanks()) {
            double cx = (w.getX() - minX) * mult + ofsX, cy = (w.getY() - minY) * mult + ofsY;
            Circle c = new Circle(cx, cy, 7, Color.web("#dd4444"));
            c.setStroke(Color.WHITE);
            c.setStrokeWidth(1.5);
            c.setStyle("-fx-cursor: hand;");
            c.setOnMouseClicked(e -> {
                showWaterTankInfo(c, stage, w, mult, ofsX, ofsY, mapPane);
                e.consume();
            });
            c.setOnMouseEntered(e -> c.setRadius(9));
            c.setOnMouseExited(e -> c.setRadius(7));
            mapPane.getChildren().add(c);
        }

        // zoom + dezoom (restaurer le pan après reconstruction)
        double savedTX = (mapGroup != null) ? mapGroup.getTranslateX() : 0;
        double savedTY = (mapGroup != null) ? mapGroup.getTranslateY() : 0;
        mapGroup = new Group(mapPane);
        mapGroup.getTransforms().add(scaleTransform);
        mapGroup.setTranslateX(savedTX);
        mapGroup.setTranslateY(savedTY);

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
                if ("FIELD".equals(placingMode)) {
                    if (fieldFirstPoint == null) {
                        // premier clic : coin haut-gauche
                        fieldFirstPoint = new Point(realClickX, realClickY);
                        fieldNameInput = new javafx.scene.control.TextField();
                        fieldNameInput.setPromptText("Nom du champ");
                        fieldNameInput.setMaxWidth(192);
                        // marqueur jaune sur la carte
                        if (fieldMarker != null) mapPane.getChildren().remove(fieldMarker);
                        fieldMarker = new Circle(
                                (realClickX - minX) * mult + ofsX,
                                (realClickY - minY) * mult + ofsY,
                                6, Color.web("#f0e040")
                        );
                        fieldMarker.setMouseTransparent(true);
                        mapPane.getChildren().add(fieldMarker);
                        Label tip = new Label("1er coin sélectionné.\nNommez le champ puis\ncliquez sur le coin bas-droit.");
                        tip.setStyle("-fx-text-fill: #f0e040; -fx-font-size: 11px;");
                        infoContent.getChildren().setAll(tip, fieldNameInput);
                    } else {
                        // second clic : on crée le champ
                        String name = (fieldNameInput != null && !fieldNameInput.getText().trim().isEmpty())
                                ? fieldNameInput.getText().trim() : "Champ";
                        double xStart = Math.min(fieldFirstPoint.getX(), realClickX);
                        double xStop = Math.max(fieldFirstPoint.getX(), realClickX);
                        double yStart = Math.min(fieldFirstPoint.getY(), realClickY);
                        double yStop = Math.max(fieldFirstPoint.getY(), realClickY);
                        ground.addField(new Field(name, xStart, xStop, yStart, yStop));
                        fieldFirstPoint = null;
                        fieldNameInput = null;
                        fieldMarker = null;
                        placingMode = null;
                        stage.setScene(getScene(stage));
                    }
                } else {
                    showPlacementForm(stage, realClickX, realClickY);
                }
            }
        });

        // label de coordonnées affiché en bas à gauche du viewport
        Label coordLabel = new Label("x: —   y: —");
        coordLabel.setStyle(
                "-fx-background-color: rgba(0,0,0,0.55);" +
                        "-fx-text-fill: #c8e6c4;" +
                        "-fx-font-size: 11px;" +
                        "-fx-padding: 3 8 3 8;" +
                        "-fx-background-radius: 4;"
        );
        coordLabel.setMouseTransparent(true);
        coordLabel.setLayoutX(8);
        coordLabel.setLayoutY(622);
        viewport.getChildren().add(coordLabel);

        viewport.setOnMouseMoved(e -> {
            double mapX = (e.getX() - mapGroup.getTranslateX()) / zoomLevel;
            double mapY = (e.getY() - mapGroup.getTranslateY()) / zoomLevel;
            double rx = (mapX - ofsX) / mult + minX;
            double ry = (mapY - ofsY) / mult + minY;
            coordLabel.setText(String.format("x: %.2f   y: %.2f", rx, ry));
        });
        viewport.setOnMouseExited(e -> coordLabel.setText("x: —   y: —"));

        // layout
        String ownerName = ground.getOwner().getFirstname() + " " + ground.getOwner().getName();
        HBox topBar = SmartFarmUI.getTopBar(stage, "SmartFarm — " + ownerName);

        VBox rightPanel = buildRightPanel(stage);
        rightPanel.setPrefWidth(300);

        BorderPane root = new BorderPane();
        root.setCenter(viewport);
        root.setRight(rightPanel);
        root.setTop(topBar);

        Scene scene = new Scene(root, 1300, 800);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        return scene;
    }

    /**
     * Updates the global layout scale magnification ratio by adjusting focal scale matrices
     * while maintaining center layout alignment configurations.
     *
     * @param factor the dynamic relative scaling factor offset to accumulate.
     */
    private void applyZoom(double factor) {
        double oldZoom = zoomLevel;
        zoomLevel = Math.max(ZOOM_MIN, Math.min(ZOOM_MAX, zoomLevel + factor));

        double cx = viewport.getWidth() / 2.0;
        double cy = viewport.getHeight() / 2.0;
        double mapCx = (cx - mapGroup.getTranslateX()) / oldZoom;
        double mapCy = (cy - mapGroup.getTranslateY()) / oldZoom;

        scaleTransform.setX(zoomLevel);
        scaleTransform.setY(zoomLevel);
        mapGroup.setTranslateX(cx - mapCx * zoomLevel);
        mapGroup.setTranslateY(cy - mapCy * zoomLevel);

        zoomLabel.setText((int) (zoomLevel * 100) + "%");
    }

    /**
     * Assembles the persistent side navigation panel container which hosts structural actions,
     * visibility toggles, geometry generation switches, placement mode buttons, zoom controls,
     * and contextual target information panels.
     *
     * @param stage the active primary application window reference context.
     * @return a structured, stylized menu sidebar layout wrapper ({@link VBox}).
     */
    private VBox buildRightPanel(Stage stage) {
        // section buttons
        VBox buttonsSection = new VBox(12);
        buttonsSection.setStyle(BG_DARK);
        buttonsSection.setAlignment(Pos.TOP_CENTER);
        buttonsSection.setPadding(new Insets(20, 14, 20, 14));
        VBox.setVgrow(buttonsSection, Priority.ALWAYS);

        Label actionsTitle = new Label("Actions");
        actionsTitle.setStyle("-fx-text-fill: #c8e6c4; -fx-font-size: 12px; -fx-font-weight: bold;");

        Button btnModifyUser = sideButton("Modify User");

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
        Button btnPlaceField = sideButton("📍 Place Field");

        btnPlaceTank.setOnAction(e -> {
            if ("TANK".equals(placingMode)) {
                placingMode = null;
                btnPlaceTank.setStyle(sideButtonStyle());
            } else {
                placingMode = "TANK";
                fieldFirstPoint = null;
                btnPlaceTank.setStyle(sideButtonStyle() + " -fx-background-color: #1a6b3a;");
                btnPlaceSprinkler.setStyle(sideButtonStyle());
                btnPlaceField.setStyle(sideButtonStyle());
            }
        });

        btnPlaceSprinkler.setOnAction(e -> {
            if ("SPRINKLER".equals(placingMode)) {
                placingMode = null;
                btnPlaceSprinkler.setStyle(sideButtonStyle());
            } else {
                placingMode = "SPRINKLER";
                fieldFirstPoint = null;
                btnPlaceSprinkler.setStyle(sideButtonStyle() + " -fx-background-color: #1a4a6b;");
                btnPlaceTank.setStyle(sideButtonStyle());
                btnPlaceField.setStyle(sideButtonStyle());
            }
        });

        btnPlaceField.setOnAction(e -> {
            if ("FIELD".equals(placingMode)) {
                placingMode = null;
                fieldFirstPoint = null;
                fieldNameInput = null;
                fieldMarker = null;
                btnPlaceField.setStyle(sideButtonStyle());
            } else {
                placingMode = "FIELD";
                fieldFirstPoint = null;
                btnPlaceField.setStyle(sideButtonStyle() + " -fx-background-color: #5a3a8b;");
                btnPlaceTank.setStyle(sideButtonStyle());
                btnPlaceSprinkler.setStyle(sideButtonStyle());
                Label tip = new Label("Cliquez sur le coin\nhaut-gauche du champ.");
                tip.setStyle("-fx-text-fill: #c8e6c4; -fx-font-size: 12px;");
                infoContent.getChildren().setAll(tip);
            }
        });
        if ("FIELD".equals(placingMode)) btnPlaceField.setStyle(sideButtonStyle() + " -fx-background-color: #5a3a8b;");

        Button btnSave = sideButton("Sauvegarder");
        btnSave.setStyle(btnSave.getStyle() + " -fx-background-color: #8a6a10;");
        btnSave.setOnAction(e -> saveGround());
        btnModifyUser.setOnAction(e -> {
            stage.setScene(AddForm.modifyUser(stage, ground));
        });

        // zoom visuals
        Label zoomTitle = new Label("Zoom");
        zoomTitle.setStyle("-fx-text-fill: #c8e6c4; -fx-font-size: 12px; -fx-font-weight: bold;");

        Button btnZoomOut = zoomButton("−");
        Button btnZoomIn = zoomButton("+");
        zoomLabel = new Label("100%");
        zoomLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-min-width: 44px; -fx-alignment: center;");

        btnZoomIn.setOnAction(e -> applyZoom(ZOOM_STEP));
        btnZoomOut.setOnAction(e -> applyZoom(-ZOOM_STEP));

        HBox zoomBar = new HBox(6, btnZoomOut, zoomLabel, btnZoomIn);
        zoomBar.setAlignment(Pos.CENTER);

        buttonsSection.getChildren().addAll(
                actionsTitle, btnModifyUser, btnSave,
                btnDelaunay, btnVoronoi, btnRadius,
                btnPlaceTank, btnPlaceSprinkler, btnPlaceField,
                zoomTitle, zoomBar
        );

        // separator
        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #3a5a30;");

        // section infos
        infoContent = new VBox(10);
        infoContent.setAlignment(Pos.CENTER);

        Label placeholder = new Label("Cliquez sur un\nélément de la carte.");
        placeholder.setStyle("-fx-text-fill: #7aaa74; -fx-font-size: 12px;");
        infoContent.getChildren().add(placeholder);

        Label infoTitle = new Label("Informations");
        infoTitle.setStyle("-fx-text-fill: #c8e6c4; -fx-font-size: 12px; -fx-font-weight: bold;");

        VBox infoSection = new VBox(8, infoTitle, infoContent);
        infoSection.setStyle(BG_DARKER);
        infoSection.setAlignment(Pos.TOP_CENTER);
        infoSection.setPadding(new Insets(16, 14, 16, 14));
        VBox.setVgrow(infoSection, Priority.ALWAYS);

        ScrollPane scrollButtons = new javafx.scene.control.ScrollPane(buttonsSection);
        scrollButtons.setFitToWidth(true);
        scrollButtons.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollButtons.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollButtons.setStyle("-fx-background: #2b4a27; -fx-background-color: #2b4a27; -fx-border-color: transparent;");
        VBox.setVgrow(scrollButtons, Priority.ALWAYS);

        VBox panel = new VBox(scrollButtons, sep, infoSection);
        panel.setStyle(BG_DARK);
        VBox.setVgrow(buttonsSection, Priority.ALWAYS);
        VBox.setVgrow(infoSection, Priority.ALWAYS);
        return panel;
    }

    /**
     * Builds standard style template string used across global dashboard side menu buttons.
     *
     * @return a JavaFX CSS instruction style string string.
     */
    private String sideButtonStyle() {
        return "-fx-background-color: #3a5a30;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 5;" +
                "-fx-padding: 8 12 8 12;" +
                "-fx-font-size: 13px;" +
                "-fx-cursor: hand;";
    }

    /**
     * Instantiates a standardized action push button component matched with dashboard theme properties.
     *
     * @param text the alphanumerical characters written on the button face.
     * @return a stylized JavaFX {@link Button}.
     */
    private Button sideButton(String text) {
        Button btn = new Button(text);
        btn.setPrefWidth(192);
        btn.setStyle(sideButtonStyle());
        return btn;
    }

    /**
     * Instantiates a compact square layout button designed for magnification scale increments.
     *
     * @param text character string indicating operations (e.g. "+", "−").
     * @return a stylized mini zoom {@link Button}.
     */
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

    /**
     * Dynamically displays localized parameters and properties within the information side pane
     * during interactive placement procedures for new water tanks or sprinklers.
     *
     * @param stage primary window context reference.
     * @param x     calculated canvas real horizontal insertion point coordinate.
     * @param y     calculated canvas real vertical insertion point coordinate.
     */
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

    /**
     * Populates the information sidebar with the details of a selected {@link WaterTank}.
     * Highlights linked sprinklers, registers drag-to-move repositioning listeners,
     * and mounts modify, refill, and delete interactions.
     *
     * @param c       the map node circle element representing the tank.
     * @param stage   primary window context reference.
     * @param w       the targeted data model instance of the {@link WaterTank}.
     * @param mult    scaling transformation multiplier factor.
     * @param ofsX    horizontal offset adjustment pixels.
     * @param ofsY    vertical offset adjustment pixels.
     * @param mapPane primary map surface container reference.
     */
    private void showWaterTankInfo(Circle c, Stage stage, WaterTank w, double mult, double ofsX, double ofsY, Pane mapPane) {
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
        for (Sprinkler s : ground.getSprinklers()) {
            if (s.getSource() == w) {
                double cx = (s.getX() - minX) * mult + ofsX, cy = (s.getY() - minY) * mult + ofsY;
                Circle cover = new Circle(cx, cy, 7, Color.YELLOW);
                cover.setStroke(Color.WHITE);
                cover.setStrokeWidth(1.5);
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

        c.setOnMouseReleased(e -> {
            ground.computeVoronoi();
            ground.getSprinklers().forEach(ground::findSource);
            stage.setScene(getScene(stage));
            e.consume();
        });

        c.setOnMouseDragged(e -> {
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
        modify.setOnAction(e -> {
            infoContent.getChildren().setAll(
                    AddForm.modifyTanksSprinklers(stage, w, ground)
            );
            for (Sprinkler s : ground.getSprinklers()) {
                ground.findSource(s);
            }
        });
        delete.setOnAction(e -> {
            ground.removeTank(w);
            stage.setScene(getScene(stage));
        });
        infoContent.getChildren().addAll(delete, refill, modify);
    }

    /**
     * Populates the information sidebar with the details of a selected {@link Sprinkler}.
     * Draws context indicators connecting it to its source tank, attaches drag repositioning,
     * handles structural modifications, toggle activation states, and deletion procedures.
     *
     * @param c       the map node circle element representing the sprinkler.
     * @param stage   primary window context reference.
     * @param s       the targeted data model instance of the {@link Sprinkler}.
     * @param mult    scaling transformation multiplier factor.
     * @param ofsX    horizontal offset adjustment pixels.
     * @param ofsY    vertical offset adjustment pixels.
     * @param mapPane primary map surface container reference.
     */
    private void showSprinklerInfo(Circle c, Stage stage, Sprinkler s, double mult, double ofsX, double ofsY, Pane mapPane) {
        removeTemporaryCircleList(mapPane);
        for (Field f : ground.getFields()) {
            minX = Math.min(minX, f.getxStart());
            minY = Math.min(minY, f.getyStart());
        }
        String src = s.getSource() != null ? "Tank #" + s.getSource().getId() : "—";
        infoContent.getChildren().setAll(
                infoRow("ID", String.valueOf(s.getId())),
                infoRow("X", String.format("%.1f", s.getX())),
                infoRow("Y", String.format("%.1f", s.getY())),
                infoRow("Débit", String.format("%.2f", s.getFlow())),
                infoRow("Rayon", String.format("%.2f", s.getRadius())),
                infoRow("Activate", String.valueOf(s.getStatusActivation())),
                infoRow("Source", src)
        );
        removeTemporaryCircle(mapPane);
        for (WaterTank w : ground.getTanks()) {
            double cx = (w.getX() - minX) * mult + ofsX, cy = (w.getY() - minY) * mult + ofsY;
            if (w.getId() == s.getSource().getId()) {
                Circle cover = new Circle(cx, cy, 7, Color.YELLOW);
                cover.setStroke(Color.WHITE);
                cover.setStrokeWidth(1.5);
                cover.setStyle("-fx-cursor: hand;");
                temporaryCircle = cover;
                mapPane.getChildren().add(cover);
                cover.setOnMouseClicked(e -> {
                    showWaterTankInfo(c, stage, w, mult, ofsX, ofsY, mapPane);
                });
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

        c.setOnMouseDragged(e -> {
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
        modify.setOnAction(e -> {
            infoContent.getChildren().setAll(
                    AddForm.modifyTanksSprinklers(stage, s, ground)
            );
            ground.findSource(s);
        });

        infoContent.getChildren().add(button);
    }

    /**
     * Formats a clean horizontal key-value readout component row styled for side data boxes.
     *
     * @param key   the attribute description label name string.
     * @param value the target value string tracking real metrics.
     * @return a synchronized {@link HBox} information block row.
     */
    public static HBox infoRow(String key, String value) {
        Label k = new Label(key + " :");
        k.setStyle("-fx-text-fill: #8dbb88; -fx-font-size: 12px; -fx-min-width: 72px;");
        Label v = new Label(value);
        v.setStyle("-fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold;");
        HBox row = new HBox(6, k, v);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    /**
     * Displays the structural properties and details of a selected {@link Field} in the
     * right-hand information panel. Evaluates and shows the internal counts of nested
     * sprinklers and water tanks, and mounts interaction inputs to rename or delete the field.
     *
     * @param stage the primary window ({@link Stage}) used to refresh the active scene view.
     * @param f     the target {@link Field} model instance to document and analyze.
     */
    private void showFieldInfo(Stage stage, Field f) {
        long sprInside = ground.getSprinklers().stream().filter(s -> f.contains(s)).count();
        long tankInside = ground.getTanks().stream().filter(t -> f.contains(t)).count();

        javafx.scene.control.TextField nameEdit = new javafx.scene.control.TextField(f.getName());
        nameEdit.setMaxWidth(192);

        Button btnRename = sideButton("Renommer");
        btnRename.setOnAction(e -> {
            String newName = nameEdit.getText().trim();
            if (!newName.isEmpty()) f.setName(newName);
            stage.setScene(getScene(stage));
        });

        Button btnDelete = sideButton("Supprimer");
        btnDelete.setStyle(sideButtonStyle() + " -fx-background-color: #8b2020;");
        btnDelete.setOnAction(e -> {
            ground.removeField(f);
            stage.setScene(getScene(stage));
        });

        infoContent.getChildren().setAll(
                infoRow("Nom", f.getName()),
                infoRow("X", String.format("%.1f → %.1f", f.getxStart(), f.getxStop())),
                infoRow("Y", String.format("%.1f → %.1f", f.getyStart(), f.getyStop())),
                infoRow("Superficie", String.format("%.2f", f.getArea())),
                infoRow("Arroseurs", String.valueOf(sprInside)),
                infoRow("Réservoirs", String.valueOf(tankInside)),
                nameEdit, btnRename, btnDelete
        );
    }

    /**
     * Serializes the current state of the {@link Ground} terrain data architecture.
     * Generates a file name based on the farmer's credentials and exports the complete
     * configuration into the standard backup resources repository.
     */
    private void saveGround() {
        String name = ground.getOwner().getFirstname().toLowerCase() + "_" + ground.getOwner().getName().toLowerCase() + "_save";
        Save save = new Save("./src/main/resources/Saves/" + name);
        try {
            save.writeSave(ground);
        } catch (FileNotFoundException e) {
            System.err.println("Erreur sauvegarde : " + e.getMessage());
        }
    }

    /**
     * Safely clears the highlighted single source node circle indicator from the interactive map canvas.
     *
     * @param mapPane the target primary layout {@link Pane} hosting the dynamic graphic nodes.
     */
    private void removeTemporaryCircle(Pane mapPane) {
        if (temporaryCircle != null) {
            mapPane.getChildren().remove(temporaryCircle);
        }
    }

    /**
     * Safely clears all highlighted dependency sprinkler nodes recorded during linked
     * asset preview routines and flushes the tracker collection.
     *
     * @param mapPane the target primary layout {@link Pane} hosting the dynamic graphic nodes.
     */
    private void removeTemporaryCircleList(Pane mapPane) {
        if (temporaryCircleListSprinklers != null) {
            mapPane.getChildren().removeAll(temporaryCircleListSprinklers);
            temporaryCircleListSprinklers.clear();
        }
    }
}