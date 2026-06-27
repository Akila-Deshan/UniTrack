package com.akila.unitrack.model;

// Module represents course or sublect taken by the student
public class Module {

    // Unique id form the database
    private int id;

    // Full name of the module
    private String moduleName;

    // Module code
    private String moduleCode;

    // Credits
    private int credits;

    // Letter grade (A+, C, B+)
    private String grade;

    // Numerical grade point get by letter grade
    // Always calculate automatically students never enters this
    private double gradePoints;

    // Semester this module belongs
    private String semester;

    // Constructor for new modules entered by the student
    // Grade points are calculated automatically from the letter grade
    public Module(String moduleName, String moduleCode, int credits, String grade, String semester){
        this.moduleName = moduleName;
        this.moduleCode = moduleCode;
        this.credits = credits;
        this.grade = grade;
        this.semester = semester;
        // Auto convert letter grade to grade points
        this.gradePoints = GPACalculator.getGradePoints(grade);
    }

    // Constructor fo loading existing modules from the database.
    public Module(int id, String moduleName, String moduleCode, int credits, String grade, double gradePoints, String semester){
        this.id = id;
        this.moduleName = moduleName;
        this.moduleCode = moduleCode;
        this.credits = credits;
        this.grade = grade;
        this.gradePoints = gradePoints;
        this.semester = semester;
    }

    // Getters
    public int getId(){
        return id;
    }
    public String getModuleName(){
        return moduleName;
    }
    public String getModuleCode(){
        return moduleCode;
    }
    public int getCredits(){
        return credits;
    }
    public String getGrade(){
        return grade;
    }
    public double getGradePoints(){
        return gradePoints;
    }
    public String getSemester(){
        return semester;
    }

    // Setters
    public void setId(int id){
        this.id = id;
    }
    public void setModuleName(String moduleName){
        this.moduleName = moduleName;
    }
    public void setModuleCode(String moduleCode){
        this.moduleCode = moduleCode;
    }
    public void setCredits(int credits){
        this.credits = credits;
    }
    public void setSemester(String semester){
        this.semester = semester;
    }

    // When grade is updated grade points are recalculated automatically
    public void setGrade(String grade){
        this.grade = grade;
        this.gradePoints = GPACalculator.getGradePoints(grade);
    }

    // Only use when loading from DB
    public void setGradePoints(double gradePoints){
        this.gradePoints = gradePoints;
    }
}