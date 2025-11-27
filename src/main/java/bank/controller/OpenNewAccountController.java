package bank.controller;

import java.util.Objects;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

public class OpenNewAccountController {

    @FXML
    private ComboBox<String> accountTypeComboBox;

    @FXML
    private void handleSubmit() {
        String selectedType = accountTypeComboBox.getValue();

        if (selectedType == null || selectedType.isEmpty()) {
            // Optional: show an alert if needed
            return;
        }

        // Call your backend logic
        openNewAccount(selectedType);
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/bank/gui/TellerHomePage.fxml")));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openNewAccount(String type) {
        // Replace this with your actual backend call
        System.out.println("Opening new account of type: " + type);

        // Example:
        // BankSystem.openNewAccount(type);

        // After successful creation you may want to return to home page
        // Or show a success message
    }
}