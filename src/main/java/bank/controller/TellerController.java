package bank.controller;

import bank.account.Account;
import bank.account.Card;
import bank.account.Check;
import bank.account.Saving;
import bank.user.Customer;
import bank.user.UserDetails;
import bank.utils.InternalLogger;
import bank.user.Users;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TellerController {

    @FXML
    private TableView<Account> accountsTable;

    @FXML
    private TableColumn<Account, String> accountNumberColumn;

    @FXML
    private TableColumn<Account, String> holderNameColumn;

    @FXML
    private TableColumn<Account, String> accountTypeColumn;

    @FXML
    private Button exitButton; // for logout

    @FXML
    private Button open_new_account_button;

    private static final InternalLogger LOGGER = new InternalLogger();

    private bank.user.User currentTellerUser;

    public void setCurrentTeller(bank.user.User teller) {
        this.currentTellerUser = teller;
    }

    @FXML
    public void initialize() {
        // 1. Configure the Table Columns
        accountNumberColumn.setCellValueFactory(
                cellData -> new ReadOnlyStringWrapper(cellData.getValue().getAccountNumber())
        );

        holderNameColumn.setCellValueFactory(
                cellData -> new ReadOnlyStringWrapper(cellData.getValue().getCustomer().getName())
        );

        accountTypeColumn.setCellValueFactory(
                cellData -> new ReadOnlyStringWrapper(cellData.getValue().getAccountType())
        );

        // 2. Load the data into the TableView
        accountsTable.setItems(getAccountData());

        if (!accountsTable.getItems().isEmpty()) {
            accountsTable.getSelectionModel().select(0);
        }

        accountsTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // double-click
                Account selected = accountsTable.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    openCustomerInformation(selected);
                }
            }
        });
    }

    private void openCustomerInformation(Account account) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/bank/gui/CustomerInformation.fxml"));
            Parent root = loader.load();

            CustomerInformationController controller = loader.getController();

            // Get the customer from the account and set it in the controller
            if (account != null && account.getCustomer() != null) {
                // Pass the customer to the controller
                controller.setCurrentUser(account.getCustomer());
            }

            Stage stage = (Stage) accountsTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            LOGGER.error("Failed to navigate to the Customer information page.: " + e.getMessage());
        }
    }

    @FXML
    private void handleOpenNewAccount(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/bank/gui/TellerOpenNewAccount.fxml"));
            Parent root = loader.load();

            OpenNewAccountController controller = loader.getController();
            // Pass teller if needed
            if (currentTellerUser instanceof bank.user.Teller) {
                controller.setCurrentTeller((bank.user.Teller) currentTellerUser);
            }

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            LOGGER.error("Failed to navigate to Open New Account page: " + e.getMessage());
        }
    }

    /**
     * Navigate to Close Account page with selected account
     */
    @FXML
    private void handleCloseAccount(javafx.event.ActionEvent event) {
        // Get selected account from table
        Account selectedAccount = accountsTable.getSelectionModel().getSelectedItem();

        if (selectedAccount == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Account Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select an account from the table to close.");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/bank/gui/ClosingAccount.fxml"));
            Parent root = loader.load();

            ClosingAccountController controller = loader.getController();

            // Pass the teller and account to close
            if (currentTellerUser instanceof bank.user.Teller) {
                controller.setCurrentTeller((bank.user.Teller) currentTellerUser);
            }
            controller.setAccountToClose(selectedAccount);

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            LOGGER.error("Failed to navigate to Close Account page: " + e.getMessage());
        }
    }


    // --- Logout Handler (Navigation Logic) ---
    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            System.out.println("Teller logged out. Redirecting to logoutSuccessful.fxml...");

            Parent root = FXMLLoader.load(getClass().getResource("/bank/gui/LogoutSuccessful.fxml"));

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            LOGGER.error("Could not load logoutSuccessful.fxml. Check path: " + e.getMessage());
        }
    }

    private ObservableList<Account> getAccountData() {
        Users.load();

        ArrayList<Account> backendList = new ArrayList<>();

        for (Users.User u : Users.get()) {
            String name = u.name();
            String username = u.username();

            // Create a Customer object
            Customer customer = new Customer(new UserDetails(username, u.password(), u.email(), name),
                    0, null, null);
            customer.setId(u.id());

            // Add accounts
            if (u.accounts() != null) {
                for (Users.Account acc : u.accounts()) {
                    Account account = null;
                    switch (acc.type()) {
                        case "Checking":
                        case "Check":
                            account = new Check(acc.number(), acc.balance(), acc.type(), customer);
                            break;

                        case "Card":
                            account = new Card(acc.number(), acc.balance(), acc.type(), customer);
                            break;

                        case "Savings":
                        case "Saving":
                            account = new Saving(acc.number(), acc.balance(), acc.type(), customer);
                            break;

                        default:
                            System.out.println(acc.type());
                    }
                    backendList.add(account);
                }
            }
        }

        return FXCollections.observableArrayList(backendList);
    }
}