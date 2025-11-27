package bank.controller;

import bank.account.Account;
import bank.user.Administrator;
import bank.user.Customer;
import bank.user.Role;
import bank.user.Teller;
import bank.user.UserDetails;
import bank.user.UserManager;
import bank.user.repository.JsonUserRepository;
import bank.utils.InternalLogger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

public class CustomerInformationController {

    @FXML private TextField customer_name_fld;
    @FXML private TextField account_num_fld;
    @FXML private Label balance_lbl;

    @FXML private Button back_btn;

    private Account account;

    private static final InternalLogger LOGGER = new InternalLogger();

    private static final Teller STATIC_TELLER;

    static {
        try {
            UserManager staticUserManager = new UserManager(new JsonUserRepository());
            UserDetails staticTellerDetails = new UserDetails("tellerUser", "tellerPass", "teller@example.com", "Teller Name");
            STATIC_TELLER = (Teller) staticUserManager.createUser(staticTellerDetails, Role.TELLER, Role.ADMIN);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize static teller", e);
        }
    }

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

    @FXML
    private void handleOpenNewAccount(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/bank/gui/TellerOpenNewAccount.fxml")));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            LOGGER.error("Could not navigate to TellerOpenNewAccount.fxml: " + e.getMessage());
        }
    }

    @FXML
    private void handleCloseAccount(ActionEvent event) {
        try {
            STATIC_TELLER.closeAccount(
            account.getCustomer().getId(),
            account.getAccountNumber()
            );
            System.out.println("Deleted user " + account.getCustomer().getName() + " / " + account.getId());
            Parent root = FXMLLoader.load(
                Objects.requireNonNull(getClass().getResource("/bank/gui/TellerAccountClosed.fxml"))
            );

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            LOGGER.error("Could not navigate to TellerAccountClosed.fxml: " + e.getMessage());
        }
    }
}