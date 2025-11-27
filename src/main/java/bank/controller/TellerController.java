package bank.controller;

import bank.account.Account;
import bank.account.Card;
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

public class TellerController { // Renamed from AccountController

    // --- FXML IDs Injected from Scene Builder ---
    @FXML
    private TableView<Account> accountsTable;

    @FXML
    private TableColumn<Account, String> accountNumberColumn;

    @FXML
    private TableColumn<Account, String> holderNameColumn;

    @FXML
    private TableColumn<Account, String> accountTypeColumn;

    @FXML
    private Button exitButton; // The Exit button (for logout)

    private static final InternalLogger LOGGER = new InternalLogger();

    // --- Initialization ---
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

        // Optional: Select the first item for demonstration
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
            controller.setAccount(account);

            Stage stage = (Stage) accountsTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            LOGGER.error("Failed to navigate to the Customer information page.: " + e.getMessage());
        }
    }


    // --- Logout Handler (Navigation Logic) ---
    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            System.out.println("Teller logged out. Redirecting to logoutSuccessful.fxml...");

            // Load the logoutSuccessful.fxml page
            // NOTE: Update the path below to match your actual file location!
            Parent root = FXMLLoader.load(getClass().getResource("/bank/gui/LogoutSuccessful.fxml"));

            // Get the current stage (window)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            LOGGER.error("Could not load logoutSuccessful.fxml. Check path: " + e.getMessage());
        }
    }

    private ObservableList<Account> getAccountData() {
        // Make sure the JSON is loaded
        Users.load();

        ArrayList<Account> backendList = new ArrayList<>();

        for (Users.User u : Users.get()) {
            String name = u.name();
            String username = u.username();
            // You could map u.role() to your Customer/User objects if needed

            // Create a Customer object
            Customer customer = new Customer(new UserDetails(username, u.password(), u.email(), name),
                    0, null, null);

            // Add accounts
            if (u.accounts() != null) {
                for (Users.Account acc : u.accounts()) {
                    Account account = new Card(acc.number(), acc.balance(), acc.type(), customer);
                    backendList.add(account);
                }
            }
        }

        return FXCollections.observableArrayList(backendList);
    }
}