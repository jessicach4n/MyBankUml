package bank.controller;

import bank.account.Account;
import bank.user.Customer;
import bank.user.User;
import bank.user.UserManager;
import bank.user.Users;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

public class DepositController {

    @FXML
    private Text profileInitials;

    @FXML
    private Label profileName;

    @FXML
    private Label profileRole;

    @FXML
    private ComboBox<String> fromAccountCombo;

    @FXML
    private ComboBox<String> toAccountCombo;

    @FXML
    private TextField amountField;

    @FXML
    private TextField descriptionField;

    @FXML
    private Label messageLabel;

    @FXML
    private Button submitButton;

    @FXML
    private Button backButton;

    private User currentUser;
    private UserManager userManager;

    /**
     * Initialize the controller.
     */
    @FXML
    public void initialize() {
        // Initialize UserManager
        userManager = new UserManager(new bank.user.repository.JsonUserRepository());
    }

    /**
     * Set the current user and populate the UI.
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
        displayProfileInfo();
        populateAccountCombos();
    }

    /**
     * Display user profile information in the sidebar.
     */
    private void displayProfileInfo() {
        if (currentUser == null) return;

        if (profileInitials != null) {
            profileInitials.setText(getInitials(currentUser.getName()));
        }

        if (profileName != null) {
            profileName.setText(currentUser.getName());
        }

        if (profileRole != null) {
            profileRole.setText(currentUser.getRole().toString());
        }
    }

    /**
     * Populate account combo boxes with customer's accounts.
     */
    private void populateAccountCombos() {
        if (currentUser instanceof Customer) {
            Customer customer = (Customer) currentUser;
            for (Account account : customer.getAccounts()) {
                String display = getAccountDisplay(account);
                fromAccountCombo.getItems().add(display);
                toAccountCombo.getItems().add(display);
            }
        }
    }

    /**
     * Format account for display in combo box.
     * Format: "Account {number} - ${balance}"
     */
    private String getAccountDisplay(Account account) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        return "Account " + account.getAccountNumber() + " - " + currencyFormat.format(account.getBalance());
    }

    /**
     * Extract account from combo box display string.
     */
    private Account getAccountFromDisplay(String display) {
        if (display == null) return null;
        String accountNumber = display.split(" - ")[0].replace("Account ", "");
        Customer customer = (Customer) currentUser;
        for (Account account : customer.getAccounts()) {
            if (account.getAccountNumber().equals(accountNumber)) {
                return account;
            }
        }
        return null;
    }

    /**
     * Generate initials from a full name.
     */
    private String getInitials(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "??";
        }

        String[] parts = fullName.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        } else {
            return (parts[0].charAt(0) + "" + parts[parts.length - 1].charAt(0)).toUpperCase();
        }
    }

    /**
     * Handle submit button - process deposit transaction.
     */
    @FXML
    private void handleSubmit(ActionEvent event) {
        // Step 1: Validate input
        String fromDisplay = fromAccountCombo.getValue();
        String toDisplay = toAccountCombo.getValue();
        String amountStr = amountField.getText().trim();
        String description = descriptionField.getText().trim();

        if (fromDisplay == null || toDisplay == null || amountStr.isEmpty() || description.isEmpty()) {
            messageLabel.setText("Please fill in all fields.");
            messageLabel.setStyle("-fx-text-fill: #DC3545;");
            return;
        }

        if (fromDisplay.equals(toDisplay)) {
            messageLabel.setText("Cannot transfer to the same account.");
            messageLabel.setStyle("-fx-text-fill: #DC3545;");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                messageLabel.setText("Amount must be positive.");
                messageLabel.setStyle("-fx-text-fill: #DC3545;");
                return;
            }
        } catch (NumberFormatException e) {
            messageLabel.setText("Invalid amount format.");
            messageLabel.setStyle("-fx-text-fill: #DC3545;");
            return;
        }

        // Step 2: Get accounts
        Account fromAccount = getAccountFromDisplay(fromDisplay);
        Account toAccount = getAccountFromDisplay(toDisplay);

        if (fromAccount == null || toAccount == null) {
            messageLabel.setText("Invalid account selection.");
            messageLabel.setStyle("-fx-text-fill: #DC3545;");
            return;
        }

        if (fromAccount.getBalance() < amount) {
            messageLabel.setText("Insufficient funds.");
            messageLabel.setStyle("-fx-text-fill: #DC3545;");
            return;
        }

        try {
            // Step 3: Execute transaction using the helper method
            Users.transaction(
                currentUser.getId(),                // User 1 ID (sender)
                fromAccount.getAccountNumber(),     // Account 1 number (from)
                currentUser.getId(),                // User 2 ID (receiver - same user)
                toAccount.getAccountNumber(),       // Account 2 number (to)
                amount,                             // Amount
                description                         // Details
            );

            // Step 4: Success flow
            messageLabel.setText("Deposit successful!");
            messageLabel.setStyle("-fx-text-fill: #28A745;");

            // Clear form
            fromAccountCombo.setValue(null);
            toAccountCombo.setValue(null);
            amountField.clear();
            descriptionField.clear();

            // Navigate back after 1 second
            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(e -> handleBack(event));
            pause.play();

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error processing deposit: " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: #DC3545;");
        }
    }

    /**
     * Handle back button - navigate back to Customer Information page.
     */
    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/bank/gui/CustomerInformation.fxml"));
            Parent root = loader.load();

            CustomerInformationController controller = loader.getController();

            // Reload the user to get updated data
            User refreshedUser = userManager.findUserById(currentUser.getId());
            controller.setCurrentUser(refreshedUser);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Could not load CustomerInformation.fxml: " + e.getMessage());
        }
    }
}
