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

    @FXML
    private ComboBox<String> customerComboBox;

    private Teller currentTeller;

    public void setCurrentTeller(Teller teller) {
        this.currentTeller = teller;
    }

    @FXML
    private void initialize() {
        populateCustomers();
    }

    private void populateCustomers() {
        Users.load();
        for (Users.User user : Users.get()) {
            // Only show customers (not tellers/managers/admins)
            if ("CUSTOMER".equals(user.role())) {
                String display = user.name() + " (ID: " + user.id() + ")";
                customerComboBox.getItems().add(display);
            }
        }
    }

    private long extractCustomerId(String display) {
        // Format: "Name (ID: 123)"
        String idPart = display.substring(display.indexOf("ID: ") + 4, display.lastIndexOf(")"));
        return Long.parseLong(idPart);
    }
    
    @FXML
    private void handleSubmit() {
        String selectedCustomer = customerComboBox.getValue();
        String selectedType = accountTypeComboBox.getValue();

        // Validate customer selection
        if (selectedCustomer == null || selectedCustomer.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a customer.");
            return;
        }

        // Validate account type selection
        if (selectedType == null || selectedType.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an account type.");
            return;
        }

        long customerId = extractCustomerId(selectedCustomer);
        addAccount(customerId, selectedType);
    }
    

    private void addAccount(long customerId, String type) {
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

            // Generate a unique account number following the customer's pattern
            String accountNumber = generateUniqueAccountNumber(customerId);

            // Create a dummy customer for the account (will be replaced by teller logic)
            Customer dummyCustomer = new Customer(
                new UserDetails("temp", "temp", "temp@temp.com", "Temporary"),
                0,
                null,
                new ArrayList<>()
            );

            Account account = null;
            switch (type) {
                case "Check":
                    account = new Check(accountNumber, 0, "Checking", dummyCustomer);
                    break;
                case "Card":
                    account = new Card(accountNumber, 0, "Card", dummyCustomer);
                    break;
                case "Savings":
                    account = new Saving(accountNumber, 0, "Savings", dummyCustomer);
                    break;
                default:
                    showAlert(Alert.AlertType.ERROR, "Invalid Type", "Unknown account type selected.");
                    return;
            }

            // Open account for the selected customer
            currentTeller.openNewAccount(customerId, account);

            showAlert(Alert.AlertType.INFORMATION, "Success",
                "Account " + accountNumber + " created successfully for customer ID " + customerId + "!");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(
                Alert.AlertType.ERROR,
                "Error",
                "An error occurred while creating the account: " + e.getMessage()
            );
        }
    }

    /**
     * Generate a unique account number following the customer's pattern.
     * Pattern: {customerId}{001, 002, 003, ...}
     * Example: Customer ID 2 gets accounts 2001, 2002, 2003, etc.
     */
    private String generateUniqueAccountNumber(long customerId) {
        Users.load();
        java.util.Set<String> existingNumbers = new java.util.HashSet<>();

        // Collect all existing account numbers
        for (Users.User u : Users.get()) {
            if (u.accounts() != null) {
                for (Users.Account acc : u.accounts()) {
                    existingNumbers.add(acc.number());
                }
            }
        }

        int suffix = 1;
        String accountNumber;

        do {
            // Format: customerId + zero padded 3 digit suffix
            accountNumber = String.format("%d%03d", customerId, suffix);
            suffix++;
        } while (existingNumbers.contains(accountNumber));

        return accountNumber;
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