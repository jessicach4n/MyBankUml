package bank.controller;

import bank.branch.Bank;
import bank.branch.Branch;
import bank.branch.BranchManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class AdminAddNewBranchController {

    @FXML
    private TextField branchNameField;

    @FXML
    private TextField locationField;

    @FXML
    private Button submitButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Label userInitialsLabel;

    @FXML
    private Label userNameLabel;

    @FXML
    private Label userRoleLabel;

    // Bank context
    private Bank bank;
    private BranchManager branchManager;

    /** Call this after loading FXML to set the bank context */
    public void setBankContext(Bank bank, BranchManager branchManager) {
        this.bank = bank;
        this.branchManager = branchManager;
    }

    @FXML
    public void initialize() {
        submitButton.setOnAction(event -> handleSubmit());
        cancelButton.setOnAction(event -> handleCancel());
    }

    private void handleSubmit() {
        String branchName = branchNameField.getText().trim();
        String location = locationField.getText().trim();

        if (branchName.isEmpty() || location.isEmpty()) {
            showError("Please fill out all fields.");
            return;
        }

        if (bank == null || branchManager == null) {
            showError("Bank context is not set!");
            return;
        }

        try {
            Branch newBranch = branchManager.addBranch(branchName, location, bank);
            if (newBranch != null) {
                // Close the window after successful addition
                Stage stage = (Stage) submitButton.getScene().getWindow();
                stage.close();
            } else {
                showError("Failed to add branch. Please try again.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("An error occurred while adding the branch.");
        }
    }

    private void handleCancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Branch Creation Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
