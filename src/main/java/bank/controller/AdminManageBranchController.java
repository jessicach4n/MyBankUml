package bank.controller;

import bank.branch.Bank;
import bank.branch.Branch;
import bank.branch.BranchManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminManageBranchController implements Initializable {

    // --- LEFT SIDEBAR BUTTONS ---
    @FXML
    private Button btnUsers;

    @FXML
    private Button btnBranch;

    // Sidebar profile UI
    @FXML private Circle profileCircle;
    @FXML private Label initialsLabel;
    @FXML private Label fullNameLabel;

    // Table
    @FXML private TableView<Branch> branchTable;
    @FXML private TableColumn<Branch, Integer> idColumn;
    @FXML private TableColumn<Branch, String> nameColumn;
    @FXML private TableColumn<Branch, String> locationColumn;
    @FXML private TableColumn<Branch, Void> deleteColumn;

    // Add button
    @FXML private Button addBranchButton;

    // Branch manager reference
    private BranchManager branchManager;
    private Bank currentBank;

    // Observable list for TableView
    private ObservableList<Branch> branchObservableList = FXCollections.observableArrayList();

    /** Dependency injection of manager and bank */
    public void setBranchManager(BranchManager manager, Bank bank) {
        this.branchManager = manager;
        this.currentBank = bank;
        reloadBranches();
//        loadTempBranches();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("branchID"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("branchName"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("address"));

        // Setup delete column
        setupDeleteColumn();

        // Load admin info
        loadAdminInfo();

        btnUsers.setOnAction(event -> navigateToUsers());
    }


    /** Adds temporary sample branches to the table for testing */
    private void loadTempBranches() {
        branchObservableList.clear();

        branchObservableList.addAll(
                new Branch( "Downtown Branch", "123 Main St", currentBank),
                new Branch( "Uptown Branch", "456 Elm St", currentBank),
                new Branch( "Suburban Branch", "789 Oak St", currentBank)
        );

        branchTable.setItems(branchObservableList);
    }

    /** Reloads the branches from the manager into the table */
    private void reloadBranches() {
        if (branchManager != null && currentBank != null) {
            branchObservableList.setAll(branchManager.getBranchList(currentBank));
            branchTable.setItems(branchObservableList);
        }
    }

    /** Configure the delete column with a button for each row */
    private void setupDeleteColumn() {
        deleteColumn.setCellFactory(col -> new TableCell<>() {
            private final Button deleteBtn = new Button("Delete");

            {
                deleteBtn.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-font-weight: bold;");
                deleteBtn.setOnAction(e -> {
                    Branch branch = getTableView().getItems().get(getIndex());
                    handleDeleteBranch(branch);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteBtn);
                }
            }
        });
    }

    /** Handles deleting a branch */
    private void handleDeleteBranch(Branch branch) {
        boolean removed = branchManager.removeBranch(branch);
        if (removed) {
            branchObservableList.remove(branch);
            System.out.println("Branch removed: " + branch.getBranchName());
        } else {
            System.out.println("Failed to remove branch: " + branch.getBranchName());
        }
    }

    @FXML
    private void handleAddNewBranch() {
        try {
            // Load the FXML for adding a new branch
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/bank/gui/AdminAddNewBranchPage.fxml"));
            Parent root = loader.load();

            // Create a new stage (window) for the add branch form
            Stage stage = new Stage();
            stage.setTitle("Add New Branch");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Blocks input to other windows until closed
            stage.showAndWait(); // Wait for the window to close

            // Optional: reload the branches after adding a new one
            reloadBranches();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load AdminAddNewBranch.fxml");
        }
    }

    /** Load the admin profile info dynamically */
    private void loadAdminInfo() {
        String fullName = "John Doe"; // Replace with actual session info
        fullNameLabel.setText(fullName);

        String[] parts = fullName.split(" ");
        if (parts.length >= 2) {
            String initials = (parts[0].charAt(0) + "" + parts[1].charAt(0)).toUpperCase();
            initialsLabel.setText(initials);
        } else if (parts.length == 1) {
            initialsLabel.setText(parts[0].substring(0, 1).toUpperCase());
        }
    }

    // --- Navigation Handlers ---
    private void navigateToUsers() {
        try {
            System.out.println("Already on Users page");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/bank/gui/AdminHomePage.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnBranch.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}