package bank.controller;

import java.io.IOException;
import java.util.List;
import java.util.Objects; // Required for lists

import bank.account.Account;
import bank.utils.InternalLogger;
import javafx.collections.FXCollections; // Required for ComboBox data
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class TransactionController {

    // --- FXML IDs ---
    @FXML private TextField customer_name_fld;
    @FXML private TextField account_num_fld;
    
    @FXML private ComboBox<String> fromAccountComboBox; 
    @FXML private ComboBox<String> toAccountComboBox;   
    
    @FXML private Label senderBalanceLabel;
    @FXML private Label recipientBalanceLabel; 
    
    @FXML private TextField amountField;
    @FXML private CheckBox confirmCheckbox;
    @FXML private Button submitButton;

    // The account currently selected as the "Sender"
    private Account selectedSenderAccount;
    // The account currently selected as the "Recipient" (if doing a transfer)
    private Account selectedRecipientAccount;
    
    private static final InternalLogger LOGGER = new InternalLogger();

    @FXML
    private void initialize() {
        submitButton.setDisable(true);
        
        // Setup the listeners for the dropdowns and amount field
        setupListeners();
    }

    public void setAccount(Account account) {
        // Set the initial account
        this.selectedSenderAccount = account;
        fillFields();
    }

    private void fillFields() {
        if (selectedSenderAccount == null) return;
        
        // 1. Fill basic text fields
        if (customer_name_fld != null) customer_name_fld.setText(selectedSenderAccount.getCustomer().getName());
        if (account_num_fld != null) account_num_fld.setText(selectedSenderAccount.getAccountNumber());
        
        // 2. Populate the Dropdowns (ComboBoxes)
        try {
            List<Account> customerAccounts = selectedSenderAccount.getCustomer().getAccounts();
            
            // --- MODIFICATION START ---
            
            // This list will hold the formatted strings for the ComboBox: "AccountType (XXXX)"
            var displayStrings = FXCollections.<String>observableArrayList();
            
            // The display string for the currently selected account
            String initialDisplayValue = null;
            
            for (Account acc : customerAccounts) {
                String accNumber = acc.getAccountNumber();
                String accType = acc.getAccountType();
                
                // Extract the last 4 digits of the account number
                String lastFourDigits = accNumber.length() > 4 ? 
                                        accNumber.substring(accNumber.length() - 4) : 
                                        accNumber; // Use full number if less than 4 digits
                
                // Create the combined display string, e.g., "Savings (1234)"
                String formattedString = accType + " (" + lastFourDigits + ")";
                displayStrings.add(formattedString);
                
                // Check if this is the account to select initially
                if (acc.equals(selectedSenderAccount)) {
                    initialDisplayValue = formattedString;
                }
            }
    
            fromAccountComboBox.setItems(displayStrings);
            toAccountComboBox.setItems(displayStrings);
    
            // Select the initial account using the combined display string
            if (initialDisplayValue != null) {
                fromAccountComboBox.setValue(initialDisplayValue);
            }
            
            // --- MODIFICATION END ---
                
        } catch (Exception e) {
            LOGGER.error("Could not load accounts for dropdown: " + e.getMessage());
        }
    
        updateBalanceLabel();
    }

    private void setupListeners() {
        // LISTENER 1: When user changes "From Account"
        fromAccountComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if (newValue != null) {
                // Find the Account object that matches this new number
                selectedSenderAccount = findAccountByNumber(newValue);
                // Update the Balance Label to match the new account
                updateBalanceLabel();
                // Update the Read-only text field to match
                account_num_fld.setText(newValue);
            }
        });

        // LISTENER 2: When user changes "To Account"
        toAccountComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if (newValue != null) {
                selectedRecipientAccount = findAccountByNumber(newValue);
                if (selectedRecipientAccount != null) {
                    recipientBalanceLabel.setText("$ " + String.format("%.2f", selectedRecipientAccount.getBalance()));
                }
            }
        });

        // LISTENER 3: Reset error styles when typing amount
        amountField.textProperty().addListener((observable, oldValue, newValue) -> {
            amountField.setStyle(""); 
        });
    }

    /** Helper to find the Account object from the Customer's list based on account number string */
    private Account findAccountByNumber(String accNum) {
        for (Account acc : selectedSenderAccount.getCustomer().getAccounts()) {
            if (acc.getAccountNumber().equals(accNum)) {
                return acc;
            }
        }
        return selectedSenderAccount; // Default fallback
    }

    private void updateBalanceLabel() {
        if (senderBalanceLabel != null && selectedSenderAccount != null) {
            senderBalanceLabel.setText("$ " + String.format("%.2f", selectedSenderAccount.getBalance()));
        }
    }

    @FXML
    private void handleCheckboxAction() {
        boolean isChecked = confirmCheckbox.isSelected();
        submitButton.setDisable(!isChecked);
        
        if (isChecked) {
            validateAmountInput(); 
        }
    }
    
    private boolean validateAmountInput() {
        String amountText = amountField.getText();
        try {
            double amount = Double.parseDouble(amountText);
            
            // Check 1: Positive
            if (amount <= 0) {
                LOGGER.error("Amount must be positive.");
                amountField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                return false;
            }
            
            // Check 2: Sufficient Funds in the SELECTED sender account
            if (amount > selectedSenderAccount.getBalance()) {
                LOGGER.error("Insufficient Funds in account " + selectedSenderAccount.getAccountNumber());
                amountField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                return false;
            }
            
            return true;
        } catch (NumberFormatException e) {
            amountField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            return false;
        }
    }

    @FXML
    private void handleSubmit() {
        if (selectedSenderAccount == null) return;

        if (!validateAmountInput()) {
            confirmCheckbox.setSelected(false);
            submitButton.setDisable(true);
            return;
        }

        double amount = Double.parseDouble(amountField.getText());

        // 1. Deduct from Sender
        double newSenderBalance = selectedSenderAccount.getBalance() - amount;
        selectedSenderAccount.setBalance(newSenderBalance);

        // 2. (Optional) Add to Recipient if one is selected
        if (selectedRecipientAccount != null && !selectedRecipientAccount.equals(selectedSenderAccount)) {
            double newRecipientBalance = selectedRecipientAccount.getBalance() + amount;
            selectedRecipientAccount.setBalance(newRecipientBalance);
            // Update recipient label immediately
            recipientBalanceLabel.setText("$ " + String.format("%.2f", selectedRecipientAccount.getBalance()));
        }

        // 3. Update UI
        updateBalanceLabel();
        amountField.clear();
        amountField.setStyle("-fx-border-color: green; -fx-border-width: 2px;"); 

        confirmCheckbox.setSelected(false);
        submitButton.setDisable(true);
        
        LOGGER.info("Transaction successful: $" + amount + " transferred/deducted.");
    }

    @FXML
    private void handleCancel() {
        goBack();
    }

    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/bank/gui/CustomerInformation.fxml")));
            Parent root = loader.load();

            CustomerInformationController controller = loader.getController();
            controller.setAccount(selectedSenderAccount); 

            Stage stage = (Stage) submitButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            LOGGER.error("Failed to return to CustomerInformation page: " + e.getMessage());
        }
    }
}