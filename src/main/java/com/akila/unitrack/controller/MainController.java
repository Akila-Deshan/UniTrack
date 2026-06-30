package com.akila.unitrack.controller;

import com.akila.unitrack.db.ModuleDAO;
import com.akila.unitrack.model.GPACalculator;
import com.akila.unitrack.model.Module;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

// MainController handles all UI logic for the UniTrack dashboard
public class MainController {

    // Add Module Form fields
    @FXML
    private TextField moduleNameField;         //User type module name here

    @FXML
    private TextField moduleCodeField;         //User type module code

    @FXML
    private TextField creditsField;            //User type credits

    @FXML
    private ComboBox<String> gradeComboBox;    //Dropdown for grade selection

    @FXML
    private ComboBox<String> semesterComboBox; //Dropdown for semester selection

    @FXML
    private Label statusLabel;                 //Shoe success or error messages

    // Module table
    // Display all the modules for the selected semester
    @FXML
    private TableView<Module> moduleTable;

    @FXML
    private TableColumn<Module, String> nameColumn;

    @FXML
    private TableColumn<Module, String> codeColumn;

    @FXML
    private TableColumn<Module, String> creditsColumn;

    @FXML
    private TableColumn<Module, String> gradeColumn;

    @FXML
    private TableColumn<Module, Double> gradePointsColumn;

    // GPA Display Labels
    @FXML
    private Label semesterGPALabel;

    @FXML
    private Label semesterClassLabel;

    @FXML
    private Label cumulativeGPALabel;

    @FXML
    private Label cumulativeClassLabel;

    // Academic Report
    @FXML
    private TextArea academicReportArea;  //Show full academic report when semester is locked.

    // Semester filter dropdown (For viewing results by semester)
    @FXML
    private ComboBox<String> viewSemesterComboBox;

    // initialize() is called automatically by JavaFX after theFXML is loaded.
    @FXML
    public void initialize(){

        // Grade dropdown with all grades users selects from this list
        gradeComboBox.setItems(FXCollections.observableArrayList(
                "A+", "A", "A-", "B+", "B", "B-",
                "C+", "C", "C-", "D+", "D", "E"
        ));

        // Semester dropdown from the fixed list in GPACalculator
        ObservableList<String> semesters =
                FXCollections.observableArrayList(GPACalculator.SEMESTERS);

        semesterComboBox.setItems(semesters);
        viewSemesterComboBox.setItems(semesters);

        // Connect table columns to Module class fields
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("moduleName"));
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("moduleCode"));
        creditsColumn.setCellValueFactory(new PropertyValueFactory<>("credits"));
        gradeColumn.setCellValueFactory(new PropertyValueFactory<>("grade"));
        gradePointsColumn.setCellValueFactory(new PropertyValueFactory<>("gradePoints"));

        // Load Cumulative GPA on startup using all saved modules
        refreshCumulativeGPA();
    }

    // Called when user clicks the add module button
    @FXML
    public void handleAddModule(){

        // Input validation
        String moduleName = moduleNameField.getText().trim();
        String moduleCode = moduleCodeField.getText().trim();
        String grade = gradeComboBox.getValue();
        String semester = semesterComboBox.getValue();
        String creditsText =creditsField.getText().trim();

        // Check all fields are filled
        if (moduleName.isEmpty() || moduleCode.isEmpty() || grade == null
            || semester == null || creditsText.isEmpty()){
            statusLabel.setText("Please fill in all fields.");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        // Check credits is a valid number
        int credits;
        try {
            credits = Integer.parseInt(creditsText);
            if (credits <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            statusLabel.setText("Credits must be a positive number.");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        // Check semester module limit (Lock if full)
        int currentCount = ModuleDAO.getModuleCountBySemester(semester);
        if (GPACalculator.isSemesterFull(semester, currentCount)) {
            statusLabel.setText("Semester is full! All modules for " + semester + " have been entered.");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        // Save module to database
        Module module = new Module(moduleName, moduleCode, credits, grade, semester);
        ModuleDAO.insertModule(module);

        // Show success message
        statusLabel.setText("Module added successfully!");
        statusLabel.setStyle("-fx-text-fill: green;");

        // Clear the form fields after successful save
        clearForm();

        // Refresh table if viewing the same semester
        if (semester.equals(viewSemesterComboBox.getValue())){
            handleViewSemester();
        }

        // Refresh cumulative GPA
        refreshCumulativeGPA();

        // Check if semester is now full after addition if so generate the report
        int newCount = ModuleDAO.getModuleCountBySemester(semester);
        if (GPACalculator.isSemesterFull(semester, newCount)){
            generateReport(semester);
        }
    }

    // Call when user select a semester in the view dropdown
    @FXML
    public void handleViewSemester(){
        String selectedSemester = viewSemesterComboBox.getValue();
        if (selectedSemester == null){
            return;
        }
        // Load modules from selected semester from DB
        List<Module> semesterModules = ModuleDAO.getModulesBySemester(selectedSemester);

        // Populate the table with these modules
        moduleTable.setItems(FXCollections.observableArrayList(semesterModules));

        // Calculate and display semester GPA
        double semesterGPA = GPACalculator.calculateGPA(semesterModules);
        String semesterClass = GPACalculator.getClassStanding(semesterGPA);

        semesterGPALabel.setText("Semester GPA: " + semesterGPA);
        semesterClassLabel.setText("Class: " + semesterClass);

        // Show the report if the semester is already complete
        // This let users view past completed semesters reports anytime
        if (GPACalculator.isSemesterFull(selectedSemester, semesterModules.size())){
            generateReport(selectedSemester);
        } else {
            // Clear report area if semester isnt complete and show a msg
            academicReportArea.setText("This semester is still in progress. " +
                    "Complete all modules to see the academic report.");
        }
    }

    // Call when user click on the delete button on a selected module
    @FXML
    public void handleDeleteModule(){
        // Get the selected module from the table
        Module selected = moduleTable.getSelectionModel().getSelectedItem();

        if (selected == null){
            statusLabel.setText("Please select a module to delete.");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        // Confirm deletion with an alert dialog
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Module");
        alert.setHeaderText("Are you sure?");
        alert.setContentText("This will permanently delete: " + selected.getModuleName());

        // Only delete if user confirms
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK){
                ModuleDAO.deleteModule(selected.getId());
                handleViewSemester();
                refreshCumulativeGPA();
                statusLabel.setText("Module deleted.");
                statusLabel.setStyle("-fx-text-fill: orange;");
            }
        });
    }

    // Refresh the cumulative GPA and class standing labels, called after every add and delete
    private void refreshCumulativeGPA(){
        List<Module> allModules = ModuleDAO.getAllModules();
        double cumulativeGPA = GPACalculator.calculateGPA(allModules);
        String cumulativeClass = GPACalculator.getClassStanding(cumulativeGPA);

        cumulativeGPALabel.setText("Cumulative GPA: " + cumulativeGPA);
        cumulativeClassLabel.setText("Current Class: " + cumulativeClass);
    }

    // Generate and display full academic report when a semester is locked.
    private void generateReport(String completedSemester){
        List<Module> semesterModules = ModuleDAO.getModulesBySemester(completedSemester);
        List<Module> allModules = ModuleDAO.getAllModules();

        // Calculate total credits earned and remaining in the degree
        int totalCreditsEarned = allModules.stream()
                                           .mapToInt(Module::getCredits)
                                           .sum();

        // Total GPA credits without NGPA == 116
        int totalGPACredits = 116;
        int remainingCredits = totalGPACredits - totalCreditsEarned;

        // Generate report using GPACalculator
        String report = GPACalculator.generateAcademicReport(
                semesterModules, allModules, totalCreditsEarned, remainingCredits
        );

        // Display in the text area
        academicReportArea.setText(report);
    }

    // Clears all input fields in the add module from after a successful save
    private void clearForm(){
        moduleNameField.clear();
        moduleCodeField.clear();
        creditsField.clear();
        gradeComboBox.setValue(null);
        semesterComboBox.setValue(null);
    }
}