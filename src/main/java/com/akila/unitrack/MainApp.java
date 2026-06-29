package com.akila.unitrack;

import com.akila.unitrack.db.DatabaseManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;

// MainApp is the entry point of JavaFX application
public class MainApp extends Application {
    // start() is called automatically when the app launches
    // Stage is the main window of the app
    @Override
    public void start(Stage stage) throws Exception {

        // Initialize the database when app starts
        DatabaseManager.initializeDatabase();

        // Load the dashboard layout from FXML file
        // FXMLLoader reads dashboard.fxml and connect it to MainController
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/akila/unitrack/dashboard.fxml")
        );

        // Create the scene with the loaded layout(1000x700)
        Scene scene = new Scene(loader.load(), 1280, 850);

        // set the window title and attach the scene
        stage.setTitle("UniTrack - Academic Record Manager");
        stage.setScene(scene);
        stage.setResizable(true);

        // Set app icon for the window title bar
        stage.getIcons().add(new Image(
                getClass().getResourceAsStream("/com/akila/unitrack/icon.png")
        ));

        // Apply stylesheet to the entire app
        scene.getStylesheets().add(
                getClass().getResource("/com/akila/unitrack/styles.css").toExternalForm()
        );


        // Show the window
        stage.show();
    }
    // main() is the very first method Java runs
    // launch() hands control over to JavaFX which then calls start()
    public static void main(String[] args){
        launch(args);
    }
}
