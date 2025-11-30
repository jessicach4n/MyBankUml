package bank.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class AdminManageRoleController {

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

    private String userId;

    @FXML
    public void initialize() {
        
    }

    public void setUserData(String fullName, String position, String role) {

        // Full name in both places
        fullNameLabel.setText(fullName);
        userNameLabel.setText(fullName);

        if (position != null) {
            positionField.setText(position);
        }

        // Role dropdown
        if (role != null) {
            roleComboBox.setValue(role);
        }

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

    @FXML
    private void handleSubmit() {
        String newPosition = positionField.getText();
        String newRole = roleComboBox.getValue();

        System.out.println("New Position: " + newPosition);
        System.out.println("New Role: " + newRole);

        closeWindow();
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }
    
    private void closeWindow() {
        Stage stage = (Stage) submitButton.getScene().getWindow();
        stage.close();
    }

}
