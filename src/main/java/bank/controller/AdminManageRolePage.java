package bank.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class AdminManageRolePage {

    // Profile UI Elements
    @FXML private Label initialsLabel;
    @FXML private Label fullNameLabel;
    @FXML private Circle profileCircle;

    // Right-side form
    @FXML private Label userNameLabel;
    @FXML private TextField positionField;
    @FXML private ComboBox<String> roleComboBox;

    @FXML private Button submitButton;
    @FXML private Button cancelButton;

    private String userId; // if you need to store the user object/id


    // ---------------------------------------------------------
    // Initialization (called automatically after FXML loads)
    // ---------------------------------------------------------
    @FXML
    public void initialize() {
        // You can set defaults here if needed
    }


    // ---------------------------------------------------------
    // Dynamic Data Setter
    // ---------------------------------------------------------
    public void setUserData(String fullName, String position, String role) {

        // Full name in both places
        fullNameLabel.setText(fullName);
        userNameLabel.setText(fullName);

        // Position field
        if (position != null) {
            positionField.setText(position);
        }

        // Role dropdown
        if (role != null) {
            roleComboBox.setValue(role);
        }

        // Generate initials
        String initials = extractInitials(fullName);
        initialsLabel.setText(initials);
    }


    // Extract initials from a full name → EX: "Daniel Kim" → "DK"
    private String extractInitials(String fullName) {
        if (fullName == null || fullName.isBlank()) return "";

        String[] parts = fullName.trim().split("\\s+");

        if (parts.length == 1) {
            return parts[0].substring(0, 1).toUpperCase();
        }

        return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
    }


    // ---------------------------------------------------------
    // Button Handlers
    // ---------------------------------------------------------
    @FXML
    private void handleSubmit() {
        String newPosition = positionField.getText();
        String newRole = roleComboBox.getValue();

        // TODO: send updated data back to database or model
        System.out.println("SUBMIT CLICKED");
        System.out.println("New Position: " + newPosition);
        System.out.println("New Role: " + newRole);

        closeWindow();
    }

    @FXML
    private void handleCancel() {
        System.out.println("CANCEL CLICKED");
        closeWindow();
    }


    // Utility: Close the window of any button on the screen
    private void closeWindow() {
        Stage stage = (Stage) submitButton.getScene().getWindow();
        stage.close();
    }

}
