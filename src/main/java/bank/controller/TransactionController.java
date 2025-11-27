package bank.controller;

import java.io.IOException;
import java.util.Objects;

import bank.account.Account;
import bank.utils.InternalLogger; // Required for customer name
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class TransactionController {

    // --- FXML IDs from your FXML snippet ---
    @FXML private TextField customer_name_fld; // New: Added from FXML
    @FXML private TextField account_num_fld;   // New: Added from FXML
    @FXML private TextField amountField;       // MATCHES FXML
    @FXML private Button submitButton;         // MATCHES FXML
    @FXML private Button cancel_btn;           // FXML uses onAction, but we'll use this ID if available

    // --- Labels for balances (Not present in FXML, using transfer labels as placeholder) ---
    // Since the FXML is built for transfers, we'll try to find a suitable label.
    // We'll use the 'senderBalanceLabel' from the FXML to display the main balance.
    @FXML private Label senderBalanceLabel; // MATCHES FXML (Used for current balance)

    private Account account;
    private static final InternalLogger LOGGER = new InternalLogger();

    /** Called by CustomerInformationController to pass the selected account */
    public void setAccount(Account account) {
        this.account = account;
        fillFields();
    }

    /** Fill account information */
    private void fillFields() {
        if (account == null) return;
        
        // Fill the fields present in the FXML structure
        customer_name_fld.setText(account.getCustomer().getName());
        account_num_fld.setText(account.getAccountNumber());
        // Use the senderBalanceLabel to show the current account balance
        senderBalanceLabel.setText("$ " + String.format("%.2f", account.getBalance()));
    }

    /** Initialize buttons */
    @FXML
    private void initialize() {
        submitButton.setOnAction(e -> performTransaction()); // Corrected ID
        // The FXML has an onAction for Cancel, but this is safer:
        // Assume 'cancel_btn' is mapped to the Cancel button via an ID or we rely on the FXML onAction.
        // If we must use FXML's onAction, we'd remove this.
        // For now, let's map the FXML's onAction 'handleCancel' to our goBack method.
    }

    // FXML's onAction for the Cancel button
    @FXML
    private void handleCancel() {
        goBack();
    }

    // FXML's onAction for the Submit button
    @FXML
    private void handleSubmit() {
        performTransaction();
    }

    /** Perform a dummy transaction (deduction) */
    private void performTransaction() {
        if (account == null) {
            LOGGER.error("No account selected.");
            return;
        }

        String amountText = amountField.getText(); // Corrected ID
        if (amountText.isEmpty()) {
            LOGGER.error("Please enter an amount.");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException ex) {
            LOGGER.error("Invalid amount: " + amountText);
            return;
        }

        // Add actual validation (e.g., check for negative amounts, sufficient balance)
        if (amount <= 0) {
            LOGGER.error("Amount must be positive.");
            return;
        }
        if (amount > account.getBalance()) {
            LOGGER.error("Insufficient funds for this deduction.");
            return;
        }

        // Dummy deduction
        account.setBalance(account.getBalance() - amount); 
        senderBalanceLabel.setText("$ " + String.format("%.2f", account.getBalance())); // Update UI
        amountField.clear(); // Clear input
        LOGGER.info("Transaction successful: $" + amount + " deducted.");

    }

    @FXML
    private void handleCheckboxAction() {
    }

    /** Go back to CustomerInformation page */
    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/bank/gui/CustomerInformation.fxml")));
            Parent root = loader.load();

            // Pass back the account to refresh data
            // NOTE: CustomerInformationController must be available for this to compile.
            CustomerInformationController controller = loader.getController();
            controller.setAccount(account);

            Stage stage = (Stage) submitButton.getScene().getWindow(); // Use any FXML component to get the stage
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            LOGGER.error("Failed to return to CustomerInformation page: " + e.getMessage());
        }
    }
}