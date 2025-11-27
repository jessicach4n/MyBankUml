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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CustomerInformationController {

    @FXML private TextField customer_name_fld;
    @FXML private TextField account_num_fld;
    @FXML private Label balance_lbl;

    @FXML private Button back_btn;
    @FXML private Button close_account_btn;

    private Account account;

    private static final InternalLogger LOGGER = new InternalLogger();

    /** Called by TellerController when switching screens */
    public void setAccount(Account account) {
        this.account = account;
        fillFields();
    }

    /** Fill UI fields using selected account */
    private void fillFields() {
        if (account == null) return;

        Customer c = account.getCustomer();

        customer_name_fld.setText(c.getName());
        account_num_fld.setText(account.getAccountNumber());
        balance_lbl.setText("$ " + account.getBalance());
    }

    /** Back button: return to Teller page */
    @FXML
    private void initialize() {
        back_btn.setOnAction(e -> goBack());
        close_account_btn.setOnAction(e -> openClosingAccount());
    }

    private void openClosingAccount() {
        if (account == null) {
            LOGGER.error("Cannot open closing page: Account data is missing.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/bank/gui/ClosingAccount.fxml")));
            Parent root = loader.load();

            ClosingAccountController controller = loader.getController();
            controller.setAccount(account);
            Stage stage = (Stage) close_account_btn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            LOGGER.error("Failed to navigate to the Closing Account page: " + e.getMessage());
        }
    }

    private void goBack() {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/bank/gui/TellerHomePage.fxml")));
            Stage stage = (Stage) back_btn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException ex) {
            LOGGER.error("Failed to open CustomerInformation.fxml: " + ex.getMessage());
        }
    }
}