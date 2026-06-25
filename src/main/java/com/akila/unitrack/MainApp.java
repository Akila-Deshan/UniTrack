package com.akila.unitrack;

import com.akila.unitrack.db.DatabaseManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

// MainApp is the entry point of JavaFX application
public class MainApp extends Application {
    // start() is called automatically when the app launches
    // Stage is the main window of the app
    @Override
    public void start(Stage stage){

        // Initialize the database when app starts
        DatabaseManager.initializeDatabase();

        Label label = new Label("UniTrack is running! DB connected.");

        //  StackPane is a simple layout that centers its content
        StackPane root = new StackPane(label);

        // Scene is what goes inside the stage
        // 800X600 is the window size in pixels
        Scene scene = new Scene(root, 800, 600);

        // set the window title and attach the scene
        stage.setTitle("UniTrack - Academic Record Manager");
        stage.setScene(scene);

        // Show the window
        stage.show();
    }
    // main() is the very first method Java runs
    // launch() hands control over to JavaFX which then calls start()
    public static void main(String[] args){
        launch(args);
    }
}
