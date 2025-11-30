package bank.controller;

import bank.account.Account;
import bank.user.Customer;
import bank.user.Role;
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

public class WithdrawController {

    @FXML
    private Text profileInitials;

    @FXML
    private Label profileName;

    @FXML
    private Label profileRole;

    @FXML
    private ComboBox<String> fromAccountCombo;

    @FXML
    private ComboBox<String> recipientCustomerCombo;

    @FXML
    private ComboBox<String> recipientAccountCombo;

    @FXML
    private TextField descriptionField;

    @FXML
    private TextField amountField;

    @FXML
    private Label messageLabel;

    @FXML
    private Button submitButton;

    @FXML
    private Button backButton;

    private User currentUser;
    private UserManager userManager;

    @FXML
    public void initialize() {
        userManager = new UserManager(new bank.user.repository.JsonUserRepository());
    }

    /**
     * Set the current user and populate the UI
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
        displayProfileInfo();
        populateAccountCombo();
        populateRecipientCustomers();
        setupRecipientCustomerListener();
    }

    /**
     * Display user profile information in the sidebar
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
     * Populate account combo box with customer's accounts
     */
    private void populateAccountCombo() {
        if (currentUser instanceof Customer) {
            Customer customer = (Customer) currentUser;
            for (Account account : customer.getAccounts()) {
                String display = getAccountDisplay(account);
                fromAccountCombo.getItems().add(display);
            }
        }
    }

    /**
     * Format account for display in combo box
     * Format: "Account {number} - ${balance}"
     */
    private String getAccountDisplay(Account account) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        return "Account " + account.getAccountNumber() + " - " + currencyFormat.format(account.getBalance());
    }

    /**
     * Extract account from combo box display string
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
     * Populate recipient customer combo box with all other customers
     */
    private void populateRecipientCustomers() {
        java.util.List<User> allUsers = userManager.getUsers();
        for (User user : allUsers) {
            // Only show other customers (not current user and only CUSTOMER role)
            if (user.getId() != currentUser.getId()
                && user instanceof Customer
                && user.getRole() == Role.CUSTOMER) {
                String display = user.getName() + " (ID: " + user.getId() + ")";
                recipientCustomerCombo.getItems().add(display);
            }
        }
    }

    /**
     * Setup listener for recipient customer selection to populate their accounts
     */
    private void setupRecipientCustomerListener() {
        recipientCustomerCombo.setOnAction(event -> {
            String selectedCustomer = recipientCustomerCombo.getValue();
            if (selectedCustomer != null) {
                populateRecipientAccounts(selectedCustomer);
            }
        });
    }

    /**
     * Populate recipient account combo box based on selected customer
     */
    private void populateRecipientAccounts(String customerDisplay) {
        recipientAccountCombo.getItems().clear();

        try {
            // Extract user ID from display string
            long userId = extractUserIdFromDisplay(customerDisplay);
            System.out.println("DEBUG: Looking for user ID: " + userId);

            User recipient = userManager.findUserById(userId);
            System.out.println("DEBUG: Found user: " + (recipient != null ? recipient.getName() : "null"));

            if (recipient instanceof Customer) {
                Customer customer = (Customer) recipient;
                System.out.println("DEBUG: Customer has " + customer.getAccounts().size() + " accounts");

                for (Account account : customer.getAccounts()) {
                    // Hide balance for security - only show account number and type
                    String display = getRecipientAccountDisplay(account);
                    System.out.println("DEBUG: Adding account: " + display);
                    recipientAccountCombo.getItems().add(display);
                }

                System.out.println("DEBUG: Total items in combo: " + recipientAccountCombo.getItems().size());
            } else {
                System.out.println("DEBUG: User is not a Customer, it's: " + (recipient != null ? recipient.getClass().getSimpleName() : "null"));
            }
        } catch (Exception e) {
            System.err.println("ERROR populating recipient accounts: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Format recipient account for display WITHOUT balance (for security)
     * Format: "Account {number} - {type}"
     */
    private String getRecipientAccountDisplay(Account account) {
        return "Account " + account.getAccountNumber() + " - " + account.getAccountType();
    }

    /**
     * Extract user ID from customer display string
     */
    private long extractUserIdFromDisplay(String display) {
        // Format: "Name (ID: 123)"
        String idPart = display.substring(display.indexOf("ID: ") + 4, display.lastIndexOf(")"));
        return Long.parseLong(idPart);
    }

    /**
     * Generate initials from a full name
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
     * Handle submit button - process transfer to another customer's account
     */
    @FXML
    private void handleSubmit(ActionEvent event) {
        // Step 1: Validate input
        String fromDisplay = fromAccountCombo.getValue();
        String recipientCustomerDisplay = recipientCustomerCombo.getValue();
        String recipientAccountDisplay = recipientAccountCombo.getValue();
        String description = descriptionField.getText().trim();
        String amountStr = amountField.getText().trim();

        if (fromDisplay == null || recipientCustomerDisplay == null ||
            recipientAccountDisplay == null || description.isEmpty() || amountStr.isEmpty()) {
            messageLabel.setText("Please fill in all fields.");
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
        if (fromAccount == null) {
            messageLabel.setText("Invalid source account selection.");
            messageLabel.setStyle("-fx-text-fill: #DC3545;");
            return;
        }

        if (fromAccount.getBalance() < amount) {
            messageLabel.setText("Insufficient funds.");
            messageLabel.setStyle("-fx-text-fill: #DC3545;");
            return;
        }

        // Get recipient user and account
        long recipientUserId = extractUserIdFromDisplay(recipientCustomerDisplay);
        User recipientUser = userManager.findUserById(recipientUserId);

        if (!(recipientUser instanceof Customer)) {
            messageLabel.setText("Invalid recipient customer.");
            messageLabel.setStyle("-fx-text-fill: #DC3545;");
            return;
        }

        // Extract recipient account number from display
        String recipientAccountNumber = recipientAccountDisplay.split(" - ")[0].replace("Account ", "");

        try {
            // Step 3: Execute transaction using the helper method
            Users.transaction(
                currentUser.getId(),
                fromAccount.getAccountNumber(),
                recipientUserId,
                recipientAccountNumber,
                amount,
                description
            );

            // Step 4: Success flow
            messageLabel.setText("Transfer successful!");
            messageLabel.setStyle("-fx-text-fill: #28A745;");

            // Clear form
            fromAccountCombo.setValue(null);
            recipientCustomerCombo.setValue(null);
            recipientAccountCombo.getItems().clear();
            recipientAccountCombo.setValue(null);
            descriptionField.clear();
            amountField.clear();

            // Navigate back after 1 second
            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(e -> handleBack(event));
            pause.play();

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error processing transfer: " + e.getMessage());
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
