package bank.controller;

import bank.account.Account;
import bank.account.Card;
import bank.user.Customer;
import bank.user.UserDetails;
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

import java.io.IOException;
import java.util.ArrayList;

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

    // --- Initialization ---
    @FXML
    public void initialize() {
        // 1. Configure the Table Columns
        accountNumberColumn.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));
        holderNameColumn.setCellValueFactory(new PropertyValueFactory<>("holderName"));
        accountTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

        // 2. Load the data into the TableView
        accountsTable.setItems(getAccountData());
        
        // Optional: Select the first item for demonstration
        if (!accountsTable.getItems().isEmpty()) {
             accountsTable.getSelectionModel().select(0);
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
            e.printStackTrace();
            // Handle file loading errors gracefully
            System.err.println("Could not load logoutSuccessful.fxml. Check path.");
        }
    }


    // --- Backend Data Simulation (Dynamic List) ---
    private ObservableList<Account> getAccountData() {
        // This simulates your backend ArrayList retrieval
        ArrayList<Account> backendList = new ArrayList<>();

        Customer johnDeer = new Customer(new UserDetails("John_Deer", "password123", "John_Deer@gmail.com", "John Deer"), 10, "(123) 456-7891", null);
        // Data populated from the provided image
        backendList.add(new Card("123", 0, "Status", johnDeer));
        backendList.add(new Card("123", 0, "Status", johnDeer));
        backendList.add(new Card("123", 0, "Status", johnDeer));
        
        return FXCollections.observableArrayList(backendList);
    }
}