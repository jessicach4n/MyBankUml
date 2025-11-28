package bank.controller;

import bank.branch.Bank;
import bank.branch.Branch;
import bank.branch.BranchManager;
import bank.user.User;
import bank.user.UserManager;
import bank.user.repository.JsonUserRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

/**
 * Controller for the Login Page.
 * Handles user authentication and navigation to appropriate pages based on user role.
 */
public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    private UserManager userManager;
    private User currentUser;  // Stores the authenticated user
    private BranchManager branchManager;
    private Bank bank;
    /**
     * Initialize the controller.
     * Sets up the UserManager and configures the login button action.
     */
    @FXML
    public void initialize() {
        // Initialize UserManager with JsonUserRepository
        JsonUserRepository userRepository = new JsonUserRepository();
        userManager = new UserManager(userRepository);

        // Set up login button action
        loginButton.setOnAction(this::handleLogin);

        // Optional: Add Enter key support for password field
        passwordField.setOnAction(this::handleLogin);

        branchManager = new BranchManager();
        bank = new Bank("BankUML");
    }

    /**
     * Handle the login button click.
     * Validates credentials and navigates to the appropriate page based on user role.
     */
    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // Validate input
        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Login Failed",
                     "Please enter both username and password.");
            return;
        }

        // Attempt authentication
        Optional<User> loginResult = userManager.login(username, password);

        if (loginResult.isPresent()) {
            currentUser = loginResult.get();
            System.out.println("Login successful: " + currentUser.getUsername() +
                             " (Role: " + currentUser.getRole() + ")");

            // Navigate based on role
            navigateBasedOnRole(event);
        } else {
            // Authentication failed
            showAlert(Alert.AlertType.ERROR, "Login Failed",
                     "Invalid username or password. Please try again.");
            passwordField.clear();
            usernameField.requestFocus();
        }
    }

    /**
     * Navigate to the appropriate page based on the user's role.
     * - CUSTOMER: CustomerInformation page
     * - TELLER: TellerHomePage
     * - MANAGER: ManagerHomePage (to be implemented)
     * - ADMIN: AdminHomePage (to be implemented)
     */
    private void navigateBasedOnRole(ActionEvent event) {
        try {
            String fxmlPath;
            String roleDescription;

            switch (currentUser.getRole()) {
                case CUSTOMER:
                    fxmlPath = "/bank/gui/CustomerInformation.fxml";
                    roleDescription = "Customer Portal";
                    System.out.println("Redirecting to Customer page for: " + currentUser.getName());
                    break;

                case TELLER:
                    fxmlPath = "/bank/gui/TellerHomePage.fxml";
                    roleDescription = "Teller Portal";
                    System.out.println("Redirecting to Teller page for: " + currentUser.getName());
                    break;

                case ADMIN:
                    fxmlPath = "/bank/gui/AdminHomePage.fxml";
                    roleDescription = "Admin Portal";
                    System.out.println("Redirecting to Admin page for: " + currentUser.getName());
                    break;

                default:
                    showAlert(Alert.AlertType.ERROR, "Error",
                             "Unknown user role: " + currentUser.getRole());
                    return;
            }

            // Load the appropriate FXML page
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Get the controller and pass the current user if needed
            Object controller = loader.getController();
            if (controller instanceof TellerController) {
                // You can add setCurrentUser method to TellerController later
                // ((TellerController) controller).setCurrentUser(currentUser);
                System.out.println("Loaded TellerController for " + roleDescription);
            } else if (controller instanceof CustomerInformationController) {
                // Pass user info to CustomerInformationController
                ((CustomerInformationController) controller).setCurrentUser(currentUser);
                System.out.println("Loaded CustomerInformationController with user: " + currentUser.getName());
            } else if (controller instanceof AdminController adminController) {
                branchManager.loadBranches(bank);
                adminController.initializeAdminContext(branchManager, bank);
            }

            // Get the current stage and set the new scene
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

            System.out.println("Successfully navigated to " + roleDescription);

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                     "Could not load the home page. Error: " + e.getMessage());
        }
    }

    /**
     * Display an alert dialog.
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Get the currently authenticated user.
     * This can be used by other controllers to access the logged-in user.
     */
    public User getCurrentUser() {
        return currentUser;
    }
}
