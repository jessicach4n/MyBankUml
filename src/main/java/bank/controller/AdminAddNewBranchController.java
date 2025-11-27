package bank.controller; // replace with your actual package

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class AdminAddNewBranchController {

    // Branch form fields
    @FXML
    private TextField branchNameField;

    @FXML
    private TextField locationField;

    // Buttons
    @FXML
    private Button submitButton;

    @FXML
    private Button cancelButton;

    // Optional: Sidebar labels
    @FXML
    private Label userInitialsLabel; // "JD"

    @FXML
    private Label userNameLabel; // "Daniel Kim"

    @FXML
    private Label userRoleLabel; // "Administrator"

    @FXML
    public void initialize() {
        // Initialize logic if needed
        submitButton.setOnAction(event -> handleSubmit());
        cancelButton.setOnAction(event -> handleCancel());
    }

    private void handleSubmit() {
        String branchName = branchNameField.getText().trim();
        String location = locationField.getText().trim();

        if (branchName.isEmpty() || location.isEmpty()) {
            System.out.println("Please fill out all fields.");
            return;
        }

        // Replace this with your actual branch creation logic
        System.out.println("Branch submitted:");
        System.out.println("Name: " + branchName);
        System.out.println("Location: " + location);

        // Optionally clear fields after submission
        branchNameField.clear();
        locationField.clear();
    }

    private void handleCancel() {
        // Clear fields (optional)
        branchNameField.clear();
        locationField.clear();
        System.out.println("Branch creation canceled.");

        // Close the current window
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
