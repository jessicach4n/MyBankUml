package bank.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller for the Logout Successful page.
 * Handles navigation back to the login page.
 */
public class LogoutSuccessfulController {

    /**
     * Handle the "Return to Login" link click.
     * Navigates back to the login page.
     */
    @FXML
    private void handleReturnToLogin(ActionEvent event) {
        try {
            System.out.println("Returning to login page...");

            // Load the LoginPage.fxml
            Parent root = FXMLLoader.load(getClass().getResource("/bank/gui/LoginPage.fxml"));

            // Get the current stage (window)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Could not load LoginPage.fxml: " + e.getMessage());
        }
    }
}
