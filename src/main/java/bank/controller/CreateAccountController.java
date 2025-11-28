package bank.controller;

import bank.user.UserManager;
import bank.user.Users;
import bank.user.repository.JsonUserRepository;
import bank.utils.InternalLogger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class CreateAccountController {

    // ------------------------
    // FXML Bindings
    // ------------------------
    @FXML
    private TextField nameField;

    @FXML
    private TextField positionField;

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private Button submitButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Button backButton;

    @FXML
    private Label messageLabel;

    private UserManager userManager;
    private static final InternalLogger LOGGER = new InternalLogger();

    // ------------------------
    // Initialization
    // ------------------------
    @FXML
    public void initialize() {
        // Initialize UserManager
        JsonUserRepository userRepository = new JsonUserRepository();
        userManager = new UserManager(userRepository);

        // Populate roleComboBox with roles
        roleComboBox.getItems().addAll("Customer", "Teller", "Manager", "Admin");
    }

    // ------------------------
    // Button Handlers
    // ------------------------
    @FXML
    private void handleSubmit(ActionEvent event) {
        String name = nameField.getText().trim();
        String position = positionField.getText().trim();
        String role = roleComboBox.getValue();

        // Basic validation
        if (name.isEmpty() || position.isEmpty() || role == null) {
            messageLabel.setText("Please fill in all fields.");
            messageLabel.setStyle("-fx-text-fill: #FF0000;");
            return;
        }

        try {
            // Convert role to uppercase for consistency with Role enum
            String roleUpperCase = role.toUpperCase();

            // Generate a unique user ID
            long userId = generateUniqueUserId();

            // Generate username from name (lowercase, no spaces)
            String username = name.toLowerCase().replace(" ", "");

            // Generate email from username
            String email = username + "@mybank.com";

            // Default password (should be changed on first login in production)
            String password = username + "123";

            // Create new user record
            Users.User newUser = new Users.User(
                userId,
                username,
                name,
                roleUpperCase,
                password,
                email,
                new ArrayList<>()  // Empty accounts list
            );

            // Add user to the system
            Users.add(newUser);
            Users.save();

            LOGGER.info("Created new user: " + name + " (" + roleUpperCase + ")");

            // Show success message
            messageLabel.setText("Account created successfully! Username: " + username + ", Password: " + password);
            messageLabel.setStyle("-fx-text-fill: #20A88E;");

            // Clear fields after successful submission
            nameField.clear();
            positionField.clear();
            roleComboBox.getSelectionModel().clearSelection();

        } catch (Exception e) {
            LOGGER.error("Failed to create user: " + e.getMessage());
            messageLabel.setText("Error creating account. Please try again.");
            messageLabel.setStyle("-fx-text-fill: #FF0000;");
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        // Clear all fields
        nameField.clear();
        positionField.clear();
        roleComboBox.getSelectionModel().clearSelection();
        messageLabel.setText("");
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            // Navigate back to AdminHomePage
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/bank/gui/AdminHomePage.fxml"));
            Parent root = loader.load();

            // Get the controller and refresh the user list
            AdminController adminController = loader.getController();
            adminController.refreshUsers();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Admin Home Page");

            LOGGER.info("Navigated back to AdminHomePage");

        } catch (IOException e) {
            LOGGER.error("Failed to navigate back to AdminHomePage: " + e.getMessage());
        }
    }

    // ------------------------
    // Helper Methods
    // ------------------------
    private long generateUniqueUserId() {
        // Get all existing users
        Users.load();
        var allUsers = Users.get();

        // Find the maximum ID and add 1
        long maxId = allUsers.stream()
                .mapToLong(Users.User::id)
                .max()
                .orElse(0);

        return maxId + 1;
    }
}