package bank.controller;

import bank.user.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

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

    @FXML
    public void initialize() {
        // Initialize any default UI elements if needed
    }
}
