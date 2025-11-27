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
    
        if (back_btn != null) {
            back_btn.setOnAction(e -> goBack());
        }
        if (cancel_btn != null) {
            cancel_btn.setOnAction(e -> goBack());
        }
        if (confirm_btn != null) {
            confirm_btn.setOnAction(e -> handleConfirm());
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


    @FXML
    private void handleConfirm() {
        if (accountToClose == null) {
            LOGGER.warn("Attempted to close a null account.");
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

        LOGGER.info("Confirmed closure for account: " + accountToClose.getAccountNumber());
        
        
        LOGGER.info("Account " + accountToClose.getAccountNumber() + " successfully processed for closure.");
    
        goBack();
    }

  
    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/bank/gui/CustomerInformation.fxml"));
            Parent root = loader.load();

            CustomerInformationController controller = loader.getController();
            controller.setAccount(accountToClose);

            Stage stage = (Stage) back_btn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            LOGGER.error("Failed to navigate to the Customer information page.: " + e.getMessage());
        }
    }
}
