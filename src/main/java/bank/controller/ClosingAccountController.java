package bank.controller;

import java.io.IOException;
import java.util.Objects;

import bank.account.Account;
import bank.user.Customer;
import bank.user.Teller;
import bank.user.UserDetails;
import bank.utils.InternalLogger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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
    private Teller currentTeller;
    private static final InternalLogger LOGGER = new InternalLogger();

    public void setCurrentTeller(Teller teller) {
        this.currentTeller = teller;
    }


    public void setAccountToClose(Account account) {
        this.accountToClose = account;
        fillFields();
    }

    public void setAccount(Account account) {
        this.accountToClose = account;
        fillFields();
    }

    private void fillFields() {
        if (accountToClose == null) return;

        Customer c = accountToClose.getCustomer();

        customer_name_fld.setText(c.getName());
        account_num_fld.setText(accountToClose.getAccountNumber());
    }
    

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


    @FXML
    private void navigateToManageAccounts() {
        goBack();
    }

    @FXML
    private void handleCancel() {
        goBack();
    }


    @FXML
    private void handleConfirm() {
        if (accountToClose == null) {
            LOGGER.warn("Attempted to close a null account.");
            showAlert(Alert.AlertType.WARNING, "No Account", "No account selected to close.");
            return;
        }

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

            long customerId = accountToClose.getCustomer().getId();
            String accountNumber = accountToClose.getAccountNumber();

            currentTeller.closeAccount(customerId, accountNumber);
            LOGGER.info("Account " + accountNumber + " closed for customer " + customerId);

            // Navigate to success page
            FXMLLoader loader = new FXMLLoader(
                    Objects.requireNonNull(getClass().getResource("/bank/gui/TellerAccountClosed.fxml"))
            );
            Parent root = loader.load();

            Stage stage = (Stage) back_btn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception ex) {
            LOGGER.error("Failed to close account: " + ex.getMessage());
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to close account: " + ex.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    

    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/bank/gui/TellerHomePage.fxml"));
            Parent root = loader.load();

            TellerController controller = loader.getController();
            if (currentTeller != null) {
                controller.setCurrentTeller(currentTeller);
            }

            Stage stage = (Stage) back_btn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            LOGGER.error("Failed to navigate back to Teller home page: " + e.getMessage());
        }
    }
}
