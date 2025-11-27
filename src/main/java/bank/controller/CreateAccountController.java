package bank.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;

public class CreateAccountController {

    // ------------------------
    // FXML Bindings
    // ------------------------
    @FXML
    private TextField nameField;

    @FXML
    private TextField positionField;

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private Button submitButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Label messageLabel; // optional: to display validation messages

    // ------------------------
    // Initialization
    // ------------------------
    @FXML
    public void initialize() {
        // Populate roleComboBox with roles
        roleComboBox.getItems().addAll("Customer", "Teller", "Admin");
    }

    // ------------------------
    // Button Handlers
    // ------------------------
    @FXML
    private void handleSubmit(ActionEvent event) {
        String name = nameField.getText().trim();
        String position = positionField.getText().trim();
        String role = roleComboBox.getValue();

        // Basic validation
        if (name.isEmpty() || position.isEmpty() || role == null) {
            messageLabel.setText("Please fill in all fields.");
            return;
        }

        // TODO: Add logic to create the user/account in your backend
        System.out.println("Creating account:");
        System.out.println("Name: " + name);
        System.out.println("Position: " + position);
        System.out.println("Role: " + role);

        messageLabel.setText("Account created successfully!");

        // Optional: clear fields after submission
        nameField.clear();
        positionField.clear();
        roleComboBox.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        // Optional: clear all fields or navigate back
        nameField.clear();
        positionField.clear();
        roleComboBox.getSelectionModel().clearSelection();
        messageLabel.setText("");
    }
}