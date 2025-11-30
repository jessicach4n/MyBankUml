package bank.controller;

import bank.user.Users;
import bank.user.Users.User;
import bank.branch.Bank;
import bank.branch.Branch;
import bank.branch.BranchManager;
import bank.utils.InternalLogger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.event.ActionEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AdminController {

    // LEFT SIDEBAR BUTTONS
    @FXML
    private Button btnUsers;

    @FXML
    private Button btnBranch;

    // SEARCH BAR
    @FXML
    private TextField searchField;

    // USERS TABLE
    @FXML
    private TableView<User> usersTable;

    @FXML
    private TableColumn<User, String> colName;

    @FXML
    private TableColumn<User, String> colPosition;

    @FXML
    private TableColumn<User, String> colRole;

    // Observable list of all users
    private ObservableList<User> allUsers;

    private static final InternalLogger LOGGER = new InternalLogger();

    private BranchManager branchManager;
    private Bank currentBank;

    @FXML
    public void initialize() {
        loadUsers();
        configureTableColumns();
        setupEventHandlers();
    }

    public void initializeAdminContext(BranchManager branchManager, Bank bank) {
        System.out.println("Initializing Admin Context");
        this.branchManager = branchManager;
        this.currentBank = bank;
    }

    private void loadUsers() {
        // Load users from the Users class
        Users.load();
        List<User> usersList = Users.get();
        allUsers = FXCollections.observableArrayList(usersList);

        // Load data into the table
        usersTable.setItems(allUsers);
        LOGGER.info("Loaded " + allUsers.size() + " users into table");
    }

    private void configureTableColumns() {
        // Configure table columns
        colName.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().name())
        );

        colPosition.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(getPositionFromRole(cellData.getValue().role()))
        );

        colRole.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().role())
        );
    }

    private void setupEventHandlers() {
        // Table row double click handler
        usersTable.setRowFactory(tv -> {
            TableRow<User> row = new TableRow<>();
            row.setOnMouseClicked((MouseEvent event) -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    User rowData = row.getItem();
                    openUserDetails(rowData);
                }
            });
            return row;
        });

        // Search bar listener
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterUsers(newValue);
        });

        // navigation buttons
        btnUsers.setOnAction(event -> navigateToUsers());
        btnBranch.setOnAction(event -> navigateToBranch());
    }

    // Refreshes the user list (can be called when returning from create page)
    public void refreshUsers() {
        loadUsers();
        searchField.clear();
    }

    // helper to convert role to human-readable
    private String getPositionFromRole(String role) {
        return switch (role.toUpperCase()) {
            case "ADMIN" -> "Administrator";
            case "TELLER" -> "Bank Teller";
            case "CUSTOMER" -> "Customer";
            default -> role;
        };
    }

    // Filter users by name or role
    private void filterUsers(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            usersTable.setItems(allUsers);
            return;
        }

        String lowerKeyword = keyword.toLowerCase();
        List<User> filtered = allUsers.stream()
                .filter(user -> user.name().toLowerCase().contains(lowerKeyword) ||
                        user.role().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());

        usersTable.setItems(FXCollections.observableArrayList(filtered));
    }

    // Open AdminManageRolePage in a popup window
    private void openUserDetails(User user) {
        try {
            // Load the FXML for the AdminManageRolePage
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/bank/gui/AdminManageRolePage.fxml"));
            Parent root = loader.load();

            // Pass the user to the controller
            AdminManageRoleController controller = loader.getController();
            controller.setUserData(user.name(), getPositionFromRole(user.role()), user.role());

            // Create a new stage (popup window)
            Stage stage = new Stage();
            stage.setTitle("Manage Roles - " + user.name());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // block interactions with other windows
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to open AdminManageRolePage for user: " + user.name());
        }
    }


    // Handle Create Account button
    @FXML
    private void handleCreateAccount(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/bank/gui/AdminCreateAccountPage.fxml")
            );
            Parent root = loader.load();

            AdminCreateAccountController controller = loader.getController();
            controller.setBankContext(this.currentBank, this.branchManager);

            // Navigate in the same window
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Create New Account");
            stage.show();

        } catch (IOException e) {
            LOGGER.error("Failed to navigate to AdminCreateAccountPage.fxml: " + e.getMessage());
        }
    }


    // Navigation Handlers
    private void navigateToUsers() {
        System.out.println("Already on Users page");
    }

    private void navigateToBranch() {
        System.out.println("Navigating to Branch page...");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/bank/gui/AdminManageBranchPage.fxml"));
            Parent root = loader.load();
            AdminManageBranchController controller = loader.getController();
            controller.setBranchManager(this.branchManager, this.currentBank);
            Stage stage = (Stage) btnBranch.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            System.out.println("Teller logged out. Redirecting to logoutSuccessful.fxml...");

            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/bank/gui/LogoutSuccessful.fxml")));

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            LOGGER.error("Could not load logoutSuccessful.fxml. Check path: " + e.getMessage());
        }
    }
}
