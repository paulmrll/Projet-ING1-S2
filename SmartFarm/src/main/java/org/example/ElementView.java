package org.example;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public abstract class ElementView {
    private final Ground ground;
    public ElementView(Ground g){
        ground = g;
    }
    public Scene getScene(Stage stage, Point p) {
        String title = "Element";
        if (p instanceof WaterTank w) {
            title = "WaterTank n°" + w.getId();
        } else if (p instanceof Sprinkler s){
            title = "Sprinkler n°" + s.getId();
        }
        HBox topBar = SmartFarmUI.getTopBar(stage, title);
        BorderPane main = new BorderPane();
        main.setCenter(uploadInfo(p, stage));
        main.setTop(topBar);

        Scene scene = new Scene(main, 1200, 700);
        return scene;
    }

    protected abstract Node uploadInfo(Point p, Stage stage);

    public Ground getGround(){
        return ground;
    }
}
