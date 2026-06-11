package org.example;

import javafx.application.Application;

public class Launcher {
    public static void main(String[] args) {
        System.setProperty("prism.lcdtext", "false");
        Application.launch(SmartFarmUI.class, args);
    }
}