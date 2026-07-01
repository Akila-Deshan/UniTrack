package com.akila.unitrack.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

// DatabaseManager handles all database connectivity for UniTrack
public class DatabaseManager {

    // Store the database in the users Appdata folder instead of the app's
    //install directory
    private static final String DB_FOLDER =
            System.getProperty("user.home") + File.separator + "UniTrack";

    private static final String DB_URL =
            "jdbc:sqlite:" + DB_FOLDER + File.separator + "unitrack.db";

    //  Gives a connection to the database
    public static Connection getConnection() throws Exception {
        // Ensure the UniTrack folder exists in the user's home directory
        File folder = new File(DB_FOLDER);
        if (!folder.exists()){
            folder.mkdirs();
        }

        return DriverManager.getConnection(DB_URL);

    }
    //This runs once when the app starts
    //it creates module table if it doesnt exists
    public static void initializeDatabase(){

        //SQL command to create the modules table
        // each module has id, name, credits, grade, grade points, semester
        String createModuleTable = """
                CREATE TABLE IF NOT EXISTS modules (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    module_name TEXT NOT NULL,
                    module_code TEXT NOT NULL,
                    credits INTEGER NOT NULL,
                    grade TEXT NOT NULL,
                    grade_points REAL NOT NULL,
                    semester TEXT NOT NULL
                );
                """;

        // Try to connect to db and run the SQL command
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()){

            stmt.execute(createModuleTable);
            System.out.println("Database initialized successfully.");

        }catch (Exception e) {
            // if something wrong prints an error
            System.out.println("DB Error: " + e.getMessage());
        }
    }
}
