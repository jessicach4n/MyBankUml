package bank.controller;

import java.io.IOException;
import java.util.Objects;

import bank.account.Account;
import bank.user.Customer;
import bank.utils.InternalLogger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ClosingAccountController {

    // FXML elements updated to match the new structure
    @FXML private TextField customer_name_fld;
    @FXML private TextField account_num_fld;
    @FXML private Button confirm_btn;
    @FXML private Button cancel_btn;
    @FXML private Button back_btn;


    private Account accountToClose;
    private static final InternalLogger LOGGER = new InternalLogger();

    /**
     * @param account The account object selected for closure.
     */
    public void setAccount(Account account) {
        this.accountToClose = account;
        fillFields();
    }

    /**
     * Fills UI fields with data from the selected account.
     */
    private void fillFields() {
        if (accountToClose == null) return;

        Customer c = accountToClose.getCustomer();

        customer_name_fld.setText(c.getName());
        account_num_fld.setText(accountToClose.getAccountNumber());
    }
    
    /**
     * Initializes the controller and sets up button actions if not defined in FXML.
     */
    @FXML
    private void initialize() {
        // Assuming back_btn handles navigation back, similar to the pattern in CustomerInformationController
        if (back_btn != null) {
            back_btn.setOnAction(e -> goBack());
        }
    }


    /**
     * Handler for the sidebar 'Manage Accounts' button (or assumed to be handled by back_btn).
     * Navigates back to the main teller view (TellerHomePage).
     */
    @FXML
    private void navigateToManageAccounts() {
        goBack();
    }

    /**
     * Handler for the 'Cancel' button.
     * Cancels the closing operation and navigates back to the main teller view.
     */
    @FXML
    private void handleCancel() {
        goBack();
    }

    /**
     * Handler for the 'Confirm' button.
     * Contains the core logic to close the account after confirmation.
     */
    @FXML
    private void handleConfirm() {
        if (accountToClose == null) {
            LOGGER.warn("Attempted to close a null account.");
            // Display an error message to the user here
            return;
        }

        try {
            // fix name
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/bank/gui/CloseSuccess.fxml")));
            Stage stage = (Stage) back_btn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException ex) {
            LOGGER.error("Failed to open CustomerInformation.fxml: " + ex.getMessage());
        }
git checkout -b <new-branch-name>

        LOGGER.info("Confirmed closure for account: " + accountToClose.getAccountNumber());
        
        // In a real scenario, you'd add logic here to check balance and close account.
        
        LOGGER.info("Account " + accountToClose.getAccountNumber() + " successfully processed for closure.");
        // --- End of Logic Placeholder ---

        // Navigate back or to a confirmation screen
        goBack();
    }

  
    private void goBack() {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/bank/gui/CustomerInformation.fxml")));
            Stage stage = (Stage) back_btn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException ex) {
            LOGGER.error("Failed to open CustomerInformation.fxml: " + ex.getMessage());
        }
    }
}
