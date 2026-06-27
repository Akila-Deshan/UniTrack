package com.akila.unitrack.db;

import com.akila.unitrack.model.GPACalculator;
import com.akila.unitrack.model.Module;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// ModuleDAO (Data Access Object) handles all database operations for modules
public class ModuleDAO {

    // Save new module to the database
    public static void insertModule(Module module){
        // Recalculate grade points from letter grade to guarantee accuracy
        double gradePoints = GPACalculator.getGradePoints(module.getGrade());

        String sql = "INSERT INTO modules (module_name, module_code, credits, " +
                "grade, grade_points, semester) VALUES (?, ?, ?, ?, ?, ?)";


        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, module.getModuleName());
            stmt.setString(2, module.getModuleCode());
            stmt.setInt   (3, module.getCredits());
            stmt.setString(4, module.getGrade());
            stmt.setDouble(5, gradePoints);  // Calculated value not from student input
            stmt.setString(6, module.getSemester());

            stmt.executeUpdate();
            System.out.println("Module saved   : " + module.getModuleName());
            System.out.println("Grade          : " + module.getGrade());
            System.out.println("Grade Points   : " + gradePoints);

        } catch (Exception e) {
            System.out.println("Insert error: " + e.getMessage());
        }
    }

    // Counts how many modules are already saved for a given semester
    public static int getModuleCountBySemester(String semester){
        String sql = "SELECT COUNT(*) FROM modules WHERE semester = ?";
        int count = 0;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1, semester);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()){
                count = rs.getInt(1);
            }
        } catch (Exception e){
            System.out.println("Count error: " + e.getMessage());
        }
        return count;
    }

    // Get all modules from the database across all semesters
    public static List<Module> getAllModules(){
        List<Module> modules = new ArrayList<>();
        String sql = "SELECT * FROM modules";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // Build a module object for each row returned from theDB
            while (rs.next()){
                modules.add(new Module(
                        rs.getInt   ("id"),
                        rs.getString("module_name"),
                        rs.getString("module_code"),
                        rs.getInt   ("credits"),
                        rs.getString("grade"),
                        rs.getDouble("grade_points"),
                        rs.getString("semester")
                ));
            }
        }catch (Exception e){
            System.out.println("Fetch error: " + e.getMessage());
        }
        return modules;
    }

    // Get all modules for a specific semester
    public static List<Module> getModuleBySemester(String semester){
        List<Module> modules = new ArrayList<>();
        String sql = "SELECT * FROM modules WHERE semester = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1, semester);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                modules.add(new Module(
                        rs.getInt   ("id"),
                        rs.getString("module_name"),
                        rs.getString("module_code"),
                        rs.getInt   ("credits"),
                        rs.getString("grade"),
                        rs.getDouble("grade_points"),
                        rs.getString("semester")
                ));
            }
        } catch (Exception e){
            System.out.println("Fetch by semester error: " + e.getMessage());
        }
        return modules;
    }

    // Delete a module from the database by its ID
    public static void deleteModule(int id){
        String sql = "DELETE FROM modules WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("Module deleted with ID: " + id);

        }catch (Exception e){
            System.out.println("Delete error: " + e.getMessage());
        }
    }
}
