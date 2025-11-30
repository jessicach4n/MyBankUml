package bank.controller;

import java.util.Objects;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import bank.account.Account;
import bank.user.Teller;
import bank.user.UserDetails;
import bank.user.Customer;
import bank.user.Users;
import bank.account.Check;
import bank.account.Saving;
import bank.account.Card;

import java.util.ArrayList;

public class OpenNewAccountController {
    
    @FXML
    private ComboBox<String> accountTypeComboBox;
    
    // Store the logged-in teller (you may need to pass this from login or session)
    private Teller currentTeller;
    
    // You can set this via a setter or through a session manager
    public void setCurrentTeller(Teller teller) {
        this.currentTeller = teller;
    }
    
    @FXML
    private void handleSubmit() {
        String selectedType = accountTypeComboBox.getValue();
        
        // Validate account type selection
        if (selectedType == null || selectedType.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an account type.");
            return;
        }
        else {
            showAlert(Alert.AlertType.INFORMATION, "Account created!", "Account Created!!!");
            addAccount(selectedType);
        }
    }
    
    
    private void addAccount(String type) {
        try {
            // Initialize teller if not set
            if (currentTeller == null) {
                UserDetails tellerDetails = new UserDetails(
                    "tellerUser", 
                    "tellerPass", 
                    "teller@example.com", 
                    "Teller Name"
                );
                currentTeller = new Teller(tellerDetails);
            }

        Account account = null;

        String temp = "tempID";
        switch (type) {
            case "Check":
                account = new Check(temp, 0, "Active");
                break;
            case "Card":
                account = new Card(temp, 0, "Active");
                break;
            case "Savings":
                account = new Saving(temp, 0, "Active");
                break;
            default:
                showAlert(Alert.AlertType.ERROR, "Invalid Type", "Unknown account type selected.");
                return; // exit if type is invalid
        }

        // Right now, always open an account under user customerId=1
        currentTeller.openNewAccount(1, account);
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(
                Alert.AlertType.ERROR,
                "Error",
                "An error occurred while creating the customer: " + e.getMessage()
            );
        }
    }
    
    @FXML
    private void handleCancel(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(
                getClass().getResource("/bank/gui/TellerHomePage.fxml")
            ));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not return to home page.");
        }
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}