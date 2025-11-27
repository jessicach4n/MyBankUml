package bank.controller;

import bank.user.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller for the Customer Information page.
 */
public class CustomerInformationController {

    @FXML
    private Label welcomeLabel;

    private User currentUser;

    /**
     * Set the current user (called from LoginController after successful login).
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
        displayCustomerInfo();
    }

    /**
     * Display customer information on the page.
     */
    private void displayCustomerInfo() {
        if (currentUser != null && welcomeLabel != null) {
            welcomeLabel.setText("Welcome, " + currentUser.getName());
        }
    }

    /**
     * Handle logout button click.
     * Navigates to the logout successful page.
     */
    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            System.out.println("Customer logged out. Redirecting to logout successful page...");

            // Load the LogoutSuccessful.fxml
            Parent root = FXMLLoader.load(getClass().getResource("/bank/gui/LogoutSuccessful.fxml"));

            // Get the current stage (window)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Could not load LogoutSuccessful.fxml: " + e.getMessage());
        }
    }

    @FXML
    public void initialize() {
        // Initialize any default UI elements if needed
    }
}
