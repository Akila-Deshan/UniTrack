package com.akila.unitrack.model;

import java.util.List;
import java.util.Map;

// GPACalculator handles all GOA-related calculations for UniTrack.
// NGPA models are excluded
public class GPACalculator {
    //Fixed semester names
    public static final String[] SEMESTERS = {
            "Level 1 - Semester 1",
            "Level 1 - Semester 2",
            "Level 2 - Semester 1",
            "Level 2 - Semester 2",
            "Level 3 - Semester 1",
            "Level 3 - Semester 2",
            "Level 4 - Semester 1",
            "Level 4 - Semester 2"
    };

    // Maximum number of GPA modules allowed per semester based on the SLTC curriculum
    public static final Map<String, Integer> SEMESTER_MODULE_LIMITS = Map.of(
            "Level 1 - Semester 1", 5,
            "Level 1 - Semester 2", 5,
            "Level 2 - Semester 1", 5,
            "Level 2 - Semester 2", 5,
            "Level 3 - Semester 1", 5,
            "Level 3 - Semester 2", 5,
            "Level 4 - Semester 1", 5,
            "Level 4 - Semester 2", 3
    );

    // checks a semester reached its limit,
    //If it returns true add module button will be locked for that semester
    public static boolean isSemesterFull(String semester, int currentCount){
        int limit = SEMESTER_MODULE_LIMITS.getOrDefault(semester, 5);
        return currentCount >= limit;
    }

    // Coverts letter grade to its grade point value.
    public static double getGradePoints(String grade){
        return switch (grade.trim().toUpperCase()) {
            case "A+", "A" -> 4.00;  // 80-100 marks
            case "A-"      -> 3.70;  // 75-79 marks
            case "B+"      -> 3.30;  // 70-74 marks
            case "B"       -> 3.00;  // 65-69 marks
            case "B-"      -> 2.70;  // 60-64 marks
            case "C+"      -> 2.30;  // 55-59 marks
            case "C"       -> 2.00;  // 50-54 marks
            case "C-"      -> 1.70;  // 45-49 marks
            case "D+"      -> 1.30;  // 40-44 marks
            case "D"       -> 1.00;  // 35-39 marks
            case "E"       -> 0.00;  // 00-34 marks (fail)
            default        -> 0.00;
        };
    }
    // Calculate weighted GPA for a given list of modules
    public static double calculateGPA(List<Module> modules){
        if (modules == null || modules.isEmpty()){
            return 0.00;
        }
        double totalCreditPoints = 0.0;
        int totalCredits = 0;

        for (Module module : modules){
            totalCreditPoints += module.getCredits() * module.getGradePoints();
            totalCredits += module.getCredits();
        }
        // Avoid division by zero
        if (totalCredits == 0) {
            return 0.00;
        }
        double gpa = totalCreditPoints / totalCredits;

        // Round to 2 decimal places
        return Math.round(gpa * 100.0) / 100.0;
    }

    // Returns the class for a given GPA value.
    // Used for both semester and cumulative degree GPA
    public static String getClassStanding(Double gpa){
        if (gpa >= 3.70) return "First Class";
        if (gpa >= 3.30) return "Second Class (Upper Division)";
        if (gpa >= 3.00) return "Second Class (Lower Division)";
        if (gpa >= 2.00) return "Pass";
        return "Fail";
    }

    // Calculate the GPA a student needs in remaining semesters
    // to reach a specific target GPA in the future
    public static double calculateRequiredGPA(double currentGPA, int totalCreditsEarned,
                                              int remainingCredits, double targetGPA){
        // Already at or above target
        if (currentGPA >= targetGPA){
            return -1;
        }
        // No remaining credits (the target is no longer reachable
        if (remainingCredits == 0){
            return -1;
        }
        double currentPoints = currentGPA * totalCreditsEarned;
        double totalCredits = totalCreditsEarned + remainingCredits;
        double requiredPoints = (targetGPA * totalCredits) - currentPoints;
        double requiredGPA = requiredPoints / remainingCredits;

        return Math.round(requiredGPA * 100.0) / 100.0;
    }

    // Generates a full academic standing report after a semester is completed
    public static String generateAcademicReport(List<Module> semesterModules, List<Module> allModules,
                                                int totalCreditsEarned, int remainingCredits){
        // calculate the semester GPA only for this semester modules
        double semesterGPA = calculateGPA(semesterModules);

        // calculate cumulative GPA across all semesters completed so far
        double cumulativeGPA = calculateGPA(allModules);

        // Get class standing for both
        String semesterClass = getClassStanding(semesterGPA);
        String cumulativeClass = getClassStanding(cumulativeGPA);

        StringBuilder report = new StringBuilder();

        // Semester result
        report.append("=== Semester Result ===\n");
        report.append("Semester GPA   : ").append(semesterGPA).append("\n");
        report.append("Semester Class : ").append(semesterClass).append("\n\n");

        // Cumulative result
        report.append("=== Cumulative Result ===\n");
        report.append("Cumulative GPA : ").append(cumulativeGPA).append("\n");
        report.append("Current Class  : ").append(cumulativeClass).append("\n\n");

        if (remainingCredits > 0){
            // Future planing section
            report.append("=== What You Need to Achieve ===\n");

            // Calculate required GPA for each degree class target
            double neededFirst     = calculateRequiredGPA(cumulativeGPA, totalCreditsEarned, remainingCredits, 3.70);
            double neededSecondUp  = calculateRequiredGPA(cumulativeGPA, totalCreditsEarned, remainingCredits, 3.30);
            double neededSecondLow = calculateRequiredGPA(cumulativeGPA, totalCreditsEarned, remainingCredits, 3.00);
            double neededPass      = calculateRequiredGPA(cumulativeGPA, totalCreditsEarned, remainingCredits, 2.00);

            // -1 means already on track for the class
            report.append("First Class (3.70)        : ")
                    .append(neededFirst == -1
                            ? "Already on track ✓"
                            : "Need " + neededFirst + " GPA in remaining semesters")
                    .append("\n");

            report.append("Second Class Upper (3.30) : ")
                    .append(neededSecondUp == -1
                            ? "Already on track ✓"
                            : "Need " + neededSecondUp + " GPA in remaining semesters")
                    .append("\n");

            report.append("Second Class Lower (3.00) : ")
                    .append(neededSecondLow == -1
                            ? "Already on track ✓"
                            : "Need " + neededSecondLow + " GPA in remaining semesters")
                    .append("\n");

            report.append("Pass (2.00)               : ")
                    .append(neededPass == -1
                            ? "Already on track ✓"
                            : "Need " + neededPass + " GPA in remaining semesters")
                    .append("\n");
        } else {
            // final degree result (no remaining credits)
            report.append("=== Final Degree Result ===\n");
            report.append("Degree Class : ").append(cumulativeClass).append("\n");
        }

        return report.toString();
    }
}