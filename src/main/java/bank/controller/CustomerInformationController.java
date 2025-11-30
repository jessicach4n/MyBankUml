package bank.controller;

import bank.account.Account;
import bank.transaction.Transaction;
import bank.user.Customer;
import bank.user.Role;
import bank.user.Teller;
import bank.user.User;
import bank.user.UserDetails;
import bank.user.UserManager;
import bank.user.repository.JsonUserRepository;
import bank.utils.InternalLogger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.List;
import java.util.Locale;

public class CustomerInformationController {

    @FXML private TextField customer_name_fld;
    @FXML private TextField account_num_fld;
    @FXML private Label balance_lbl;

    @FXML private Button back_btn;

    private Account account;

    private static final InternalLogger LOGGER = new InternalLogger();

    private static final Teller STATIC_TELLER;

    static {
        try {
            UserManager staticUserManager = new UserManager(new JsonUserRepository());
            UserDetails staticTellerDetails = new UserDetails("tellerUser", "tellerPass", "teller@example.com", "Teller Name");
            STATIC_TELLER = (Teller) staticUserManager.createUser(staticTellerDetails, Role.TELLER, Role.ADMIN);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize static teller", e);
        }
    }

    public void setAccount(Account account) {
        this.account = account;
        fillFields();
    }

    private void fillFields() {
        if (account == null) return;

        Customer c = account.getCustomer();

        customer_name_fld.setText(c.getName());
        account_num_fld.setText(account.getAccountNumber());
        balance_lbl.setText("$ " + account.getBalance());
    }

    @FXML
    private Label welcomeLabel;

    @FXML
    private Text profileInitials;

    @FXML
    private Label profileName;

    @FXML
    private Label profileRole;

    @FXML
    private TextField customerNameField;

    @FXML
    private TextField accountNumberField;

    @FXML
    private Label balanceLabel;

    @FXML
    private VBox transactionList;

    @FXML
    private FlowPane accountCardsContainer;

    @FXML
    private javafx.scene.control.Button depositButton;

    @FXML
    private javafx.scene.control.Button withdrawButton;

    private Account selectedAccount;
    private User currentUser;

    public void setCurrentUser(User user) {
        this.currentUser = user;
        displayCustomerInfo();
    }

    private void displayCustomerInfo() {
        if (currentUser == null) return;

        // Set profile information
        if (profileInitials != null) {
            profileInitials.setText(getInitials(currentUser.getName()));
        }

        if (profileName != null) {
            profileName.setText(currentUser.getName());
        }

        if (profileRole != null) {
            profileRole.setText(currentUser.getRole().toString());
        }

        if (customerNameField != null) {
            customerNameField.setText(currentUser.getName());
        }

        // Display account information if user is a customer
        if (currentUser instanceof Customer) {
            Customer customer = (Customer) currentUser;

            populateAccountCards(customer);

            if (customer.getAccounts() != null && !customer.getAccounts().isEmpty()) {
                selectAccount(customer.getAccount(0));
            } else {
                // No accounts
                if (accountNumberField != null) {
                    accountNumberField.setText("No account");
                }
                if (balanceLabel != null) {
                    balanceLabel.setText("$ 0.00");
                }
            }
        }
    }

    /**
     * Populate the account cards panel with the customer's accounts and balances.
     */
    private void populateAccountCards(Customer customer) {
        if (accountCardsContainer == null) return;

        accountCardsContainer.getChildren().clear();

        if (customer.getAccounts() == null || customer.getAccounts().isEmpty()) {
            Label emptyLabel = new Label("No accounts available");
            emptyLabel.setStyle("-fx-text-fill: #666666;");
            accountCardsContainer.getChildren().add(emptyLabel);
            return;
        }

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        String[] backgrounds = new String[] {"#70FFD6", "#98E8D5", "#b8f7e6"};

        int index = 0;
        for (Account acct : customer.getAccounts()) {
            VBox card = new VBox(6);
            card.setPrefWidth(220);
            String baseStyle = "-fx-background-color: " + backgrounds[index % backgrounds.length] + "; -fx-background-radius: 8; -fx-padding: 12;";
            if (acct.equals(selectedAccount)) {
                baseStyle += "; -fx-border-color: #1e1e1e; -fx-border-width: 2;";
            }
            card.setStyle(baseStyle);

            Label title = new Label(acct.getAccountType() + " Account");
            title.setFont(Font.font("System Bold", 12));

            Label subtype = new Label(deriveAccountLabel(acct));
            subtype.setFont(Font.font(11));

            Label balance = new Label(currencyFormat.format(acct.getBalance()));
            balance.setFont(Font.font("System Bold", 14));

            Pane spacer = new Pane();
            VBox.setVgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

            Label acctNumber = new Label(acct.getAccountNumber());
            acctNumber.setFont(Font.font(12));

            card.getChildren().addAll(title, subtype, balance, spacer, acctNumber);
            card.setOnMouseClicked(event -> selectAccount(acct));
            accountCardsContainer.getChildren().add(card);
            index++;
        }
    }

    /**
     * Update UI for the selected account.
     */
    private void selectAccount(Account account) {
        this.selectedAccount = account;
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);

        if (accountNumberField != null) {
            accountNumberField.setText(account.getAccountNumber());
        }
        if (balanceLabel != null) {
            balanceLabel.setText(currencyFormat.format(account.getBalance()));
        }

        displayTransactions(account);

        // refresh cards to show selection border
        if (currentUser instanceof Customer customer) {
            populateAccountCards(customer);
        }
    }

    private String deriveAccountLabel(Account account) {
        String type = account.getAccountType();
        if (type == null) return "Account";
        return switch (type.toLowerCase(Locale.ROOT)) {
            case "check", "checking" -> "Checking";
            case "saving", "savings" -> "Savings";
            case "card", "visa" -> "Card";
            default -> type;
        };
    }

    /**
     * Display transactions for the given account
     */
    private void displayTransactions(Account account) {
        if (transactionList == null) return;

        // Clear existing content
        transactionList.getChildren().clear();

        List<Transaction> transactions = account.getTransactions();
        if (transactions == null || transactions.isEmpty()) {
            Label noTransactionsLabel = new Label("No transactions found");
            noTransactionsLabel.setStyle("-fx-padding: 20; -fx-text-fill: #999999;");
            transactionList.getChildren().add(noTransactionsLabel);
            return;
        }

        // Sort transactions by date (newest first)
        transactions.sort(Comparator.comparing(Transaction::getDate).reversed());

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);

        // Add each transaction as a row
        for (Transaction transaction : transactions) {
            HBox transactionRow = createTransactionRow(transaction, dateFormat, currencyFormat);
            transactionList.getChildren().add(transactionRow);
        }
    }

    /**
     * Create a transaction row UI element
     */
    private HBox createTransactionRow(Transaction transaction, SimpleDateFormat dateFormat, NumberFormat currencyFormat) {
        HBox row = new HBox(10);
        row.setStyle("-fx-padding: 12; -fx-border-color: #E0E0E0; -fx-border-width: 0 0 1 0;");

        Label dateLabel = new Label(dateFormat.format(transaction.getDate()));
        dateLabel.setPrefWidth(100);
        dateLabel.setStyle("-fx-text-fill: #666666;");
        dateLabel.setFont(Font.font(12));

        // Description (transaction details and recipient)
        String description = transaction.getRecipient();
        if (transaction.getAmount() >= 0) {
            description = "Received from " + description;
        } else {
            description = "Sent to " + description;
        }

        Label descLabel = new Label(description);
        descLabel.setPrefWidth(250);
        descLabel.setFont(Font.font(12));

        Pane spacer = new Pane();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        double amount = transaction.getAmount();
        Label amountLabel = new Label(currencyFormat.format(Math.abs(amount)));
        amountLabel.setPrefWidth(100);
        amountLabel.setFont(Font.font("System Bold", 12));
        amountLabel.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

        if (amount >= 0) {
            amountLabel.setStyle("-fx-text-fill: #28A745;"); // Green for deposits
            amountLabel.setText("+ " + currencyFormat.format(amount));
        } else {
            amountLabel.setStyle("-fx-text-fill: #DC3545;"); // Red for withdrawals
            amountLabel.setText("- " + currencyFormat.format(Math.abs(amount)));
        }

        row.getChildren().addAll(dateLabel, descLabel, spacer, amountLabel);
        return row;
    }

    /**
     * Generate initials from a full name.
     * E.g., "Kanye West" becomes "KW"
     */
    private String getInitials(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "??";
        }

        String[] parts = fullName.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        } else {
            return (parts[0].charAt(0) + "" + parts[parts.length - 1].charAt(0)).toUpperCase();
        }
    }

    /**
     * Navigates to the Deposit page.
     */
    @FXML
    private void handleNavigateDeposit(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/bank/gui/Deposit.fxml"));
            Parent root = loader.load();

            DepositController controller = loader.getController();
            controller.setCurrentUser(currentUser);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Could not load Deposit.fxml: " + e.getMessage());
        }
    }

    /**
     * Navigates to the Withdraw page.
     */
    @FXML
    private void handleNavigateWithdraw(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/bank/gui/Withdraw.fxml"));
            Parent root = loader.load();

            WithdrawController controller = loader.getController();
            controller.setCurrentUser(currentUser);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Could not load Withdraw.fxml: " + e.getMessage());
        }
    }

    /**
     * Navigates to the logout successful page
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
    private void handleCloseAccount(ActionEvent event) {
        try {
            STATIC_TELLER.closeAccount(
            account.getCustomer().getId(),
            account.getAccountNumber()
            );
            System.out.println("Deleted user " + account.getCustomer().getName() + " / " + account.getId());
            Parent root = FXMLLoader.load(
                Objects.requireNonNull(getClass().getResource("/bank/gui/TellerAccountClosed.fxml"))
            );

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            LOGGER.error("Could not navigate to TellerAccountClosed.fxml: " + e.getMessage());
        }
    }
    public void initialize() {
    }
}
