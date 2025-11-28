package bank.gui;

import bank.user.Users;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.Objects;
import java.util.Optional;

public class GUI extends Application {

    private void style_text(Node node, double size, boolean bold, int rgb) {
        String family = "Arial";
        String existing = node.getStyle();
        if (existing == null) existing = "";
        String weight = bold ? "bold" : "normal";
        String color_hex = String.format("#%06X", rgb & 0xFFFFFF);
        node.setStyle(
                existing +
                        (existing.isEmpty() ? "" : ";") +
                        "-fx-font-family: '" + family + "';" +
                        "-fx-font-size: " + size + "px;" +
                        "-fx-font-weight: " + weight + ";" +
                        "-fx-text-fill: " + color_hex + ";"
        );
    }

    private StackPane content_stack;
    private Pane home_view;
    private Pane withdraw_view;
    private Pane deposit_view;
    private Pane success_view;
    private Pane loginOverlay;

    private AuthManager authManager = new AuthManager();

    private ToggleButton accounts_btn;
    private ToggleButton deposit_btn;
    private ToggleButton withdraw_btn;
    private Button settings_btn;

    private TextField loginUserIdField;
    private PasswordField loginPasswordField;

    private Label sidebarInitials;
    private Label sidebarName;
    private Label sidebarRole;
    private VBox profileBox;
    private Button sidebarLoginButton;
    private Button sidebarLogoutButton;

    private Label acctTypeValue;
    private Label acctNumValue;
    private Label balValue;
    private VBox accountTilesBox;
    private TableView<Transaction> transactionsTable;

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        content_stack = new StackPane();
        home_view = build_home_view();
        withdraw_view = build_withdraw_view();
        deposit_view = build_deposit_view();
        success_view = build_success_view();
        loginOverlay = build_login_overlay();

        content_stack.getChildren().addAll(home_view, withdraw_view, deposit_view, success_view, loginOverlay);
        show_view(home_view);

        Node sidebar = build_sidebar();
        root.setLeft(sidebar);
        root.setCenter(content_stack);

        Scene scene = new Scene(root, 1200, 720);
        scene.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/bank/bank.css")).toExternalForm()
        );
        scene.getRoot().setStyle("-fx-font-family: 'Arial';");

        stage.setTitle("MYBank");
        stage.setScene(scene);
        stage.show();

        showLoginPanel();
    }

    private Node build_sidebar() {
        Label app_label = new Label("BankUML");
        Circle avatar_circle = new Circle(50, Color.BLACK);
        sidebarInitials = new Label("??");
        sidebarName = new Label("Guest");
        sidebarRole = new Label("Not signed in");

        ToggleGroup group = new ToggleGroup();
        accounts_btn = new ToggleButton("Accounts");
        deposit_btn = new ToggleButton("Deposit");
        withdraw_btn = new ToggleButton("Withdraw");

        settings_btn = new Button("Settings");
        sidebarLoginButton = new Button("Log In");
        sidebarLogoutButton = new Button("Log Out");
        Region spacer = new Region();

        style_text(app_label, 16, false, 0x000000);
        style_text(sidebarInitials, 40, true, 0xFFFFFF);
        style_text(sidebarName, 16, true, 0x000000);
        style_text(sidebarRole, 14, false, 0x000000);
        style_text(settings_btn, 16, false, 0xFFFFFF);
        style_text(sidebarLoginButton, 14, true, 0xFFFFFF);
        style_text(sidebarLogoutButton, 14, true, 0xFFFFFF);

        accounts_btn.setToggleGroup(group);
        deposit_btn.setToggleGroup(group);
        withdraw_btn.setToggleGroup(group);

        accounts_btn.setMaxWidth(Double.MAX_VALUE);
        deposit_btn.setMaxWidth(Double.MAX_VALUE);
        withdraw_btn.setMaxWidth(Double.MAX_VALUE);

        accounts_btn.setAlignment(Pos.CENTER_LEFT);
        deposit_btn.setAlignment(Pos.CENTER_LEFT);
        withdraw_btn.setAlignment(Pos.CENTER_LEFT);

        apply_sidebar_button_style(accounts_btn, true);
        apply_sidebar_button_style(deposit_btn, false);
        apply_sidebar_button_style(withdraw_btn, false);

        accounts_btn.selectedProperty().addListener((obs, was_sel, is_sel) ->
                apply_sidebar_button_style(accounts_btn, is_sel)
        );
        deposit_btn.selectedProperty().addListener((obs, was_sel, is_sel) ->
                apply_sidebar_button_style(deposit_btn, is_sel)
        );
        withdraw_btn.selectedProperty().addListener((obs, was_sel, is_sel) ->
                apply_sidebar_button_style(withdraw_btn, is_sel)
        );

        accounts_btn.setSelected(true);
        accounts_btn.setOnAction(e -> {
            if (!accounts_btn.isSelected()) {
                accounts_btn.setSelected(true);
            }
            if (ensureLoggedIn()) show_view(home_view);
        });
        deposit_btn.setOnAction(e -> {
            if (!deposit_btn.isSelected()) {
                deposit_btn.setSelected(true);
            }
            if (ensureLoggedIn()) show_view(deposit_view);
        });
        withdraw_btn.setOnAction(e -> {
            if (!withdraw_btn.isSelected()) {
                withdraw_btn.setSelected(true);
            }
            if (ensureLoggedIn()) show_view(withdraw_view);
        });

        sidebarLoginButton.setMaxWidth(Double.MAX_VALUE);
        sidebarLoginButton.setPrefHeight(44);
        sidebarLoginButton.setStyle("-fx-background-color: #00796b; -fx-text-fill: white; -fx-background-radius: 8;");
        sidebarLoginButton.setOnAction(e -> showLoginPanel());

        sidebarLogoutButton.setMaxWidth(Double.MAX_VALUE);
        sidebarLogoutButton.setPrefHeight(44);
        sidebarLogoutButton.setStyle("-fx-background-color: #cc4b37; -fx-text-fill: white; -fx-background-radius: 8;");
        sidebarLogoutButton.setOnAction(e -> handleLogout());
        sidebarLogoutButton.setVisible(false);
        sidebarLogoutButton.setManaged(false);

        VBox.setVgrow(spacer, Priority.ALWAYS);

        settings_btn.setMaxWidth(Double.MAX_VALUE);
        settings_btn.setPrefHeight(56);
        settings_btn.setAlignment(Pos.CENTER_LEFT);
        settings_btn.setStyle(
                "-fx-background-color: #333333;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 0;" +
                        "-fx-background-insets: 0;" +
                        "-fx-padding: 0 0 0 20;"
        );
        style_text(settings_btn, 16, false, 0xFFFFFF);

        StackPane avatar = new StackPane(avatar_circle, sidebarInitials);
        avatar.setAlignment(Pos.CENTER);

        profileBox = new VBox(6, avatar, sidebarName, sidebarRole);
        profileBox.setAlignment(Pos.CENTER);
        profileBox.setPadding(new Insets(10, 0, 10, 0));
        profileBox.setMaxWidth(Double.MAX_VALUE);
        profileBox.setVisible(false);
        profileBox.setManaged(false);

        VBox header_box = new VBox(12, app_label, profileBox, sidebarLoginButton, sidebarLogoutButton);
        header_box.setPadding(new Insets(20, 20, 10, 20));
        header_box.setFillWidth(true);
        header_box.setAlignment(Pos.TOP_LEFT);

        // ⬇️ main fix: remove vertical gaps between nav buttons
        VBox nav_box = new VBox(accounts_btn, deposit_btn, withdraw_btn);
        nav_box.setSpacing(0);
        nav_box.setFillWidth(true);

        VBox root = new VBox();
        root.setPrefWidth(220);
        root.setFillWidth(true);
        root.setStyle("-fx-background-color: #00a693;");
        root.getChildren().addAll(header_box, nav_box, spacer, settings_btn);
        return root;
    }

    private Pane build_home_view() {
        Label acct_type_label = new Label("Account Type");
        acctTypeValue = new Label("-");
        Label acct_num_label = new Label("Account Number");
        acctNumValue = new Label("-");
        Label bal_label = new Label("Balance");
        balValue = new Label("$ -");

        Button transfer_btn = new Button("Make A Transfer");
        Button pay_bills_btn = new Button("Pay Bills");

        Label accounts_title = new Label("Accounts");

        Label history_label = new Label("Transaction History");
        Label filter_label = new Label("Filter by: Date");
        Region spacer = new Region();

        transactionsTable = new TableView<>();
        TableColumn<Transaction, String> merchant_col = create_header_column("Merchant");
        TableColumn<Transaction, String> date_col = create_header_column("Date");
        TableColumn<Transaction, String> card_col = create_header_column("Card");
        TableColumn<Transaction, String> amount_col = create_header_column("Amount");

        style_text(acct_type_label, 12, false, 0x000000);
        style_text(acctTypeValue, 28, true, 0x000000);
        style_text(acct_num_label, 12, false, 0x000000);
        style_text(acctNumValue, 18, true, 0x000000);
        style_text(bal_label, 12, false, 0x000000);
        style_text(balValue, 24, true, 0x000000);

        style_text(transfer_btn, 14, true, 0x000000);
        style_text(pay_bills_btn, 14, true, 0x000000);

        style_text(accounts_title, 24, true, 0x000000);
        style_text(history_label, 24, true, 0x000000);
        style_text(filter_label, 14, false, 0x000000);

        transfer_btn.setMaxWidth(Double.MAX_VALUE);
        pay_bills_btn.setMaxWidth(Double.MAX_VALUE);

        String card_button_style =
                "-fx-background-color: #e3e3e3;" +
                        "-fx-text-fill: black;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 3;";
        transfer_btn.setStyle(card_button_style);
        pay_bills_btn.setStyle(card_button_style);

        transfer_btn.setOnAction(e -> show_view(deposit_view));
        pay_bills_btn.setOnAction(e -> show_view(withdraw_view));

        transactionsTable.setPrefHeight(230);
        transactionsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        transactionsTable.setStyle(
                "-fx-background-color: white;" +
                        "-fx-control-inner-background: white;" +
                        "-fx-control-inner-background-alt: white;" +
                        "-fx-table-cell-border-color: transparent;" +
                        "-fx-padding: 0;" +
                        "-fx-selection-bar: white;" +
                        "-fx-selection-bar-non-focused: white;"
        );

        HBox.setHgrow(spacer, Priority.ALWAYS);

        merchant_col.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(c.getValue().getMerchant()));
        date_col.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(c.getValue().getDate()));
        card_col.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(c.getValue().getCard()));
        amount_col.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(c.getValue().getAmount()));

        style_body_column(merchant_col);
        style_body_column(date_col);
        style_body_column(card_col);
        style_body_column(amount_col);

        transactionsTable.getColumns().addAll(merchant_col, date_col, card_col, amount_col);

        VBox summary_box = new VBox(8,
                acct_type_label, acctTypeValue,
                acct_num_label, acctNumValue,
                bal_label, balValue
        );

        VBox button_box = new VBox(10, transfer_btn, pay_bills_btn);
        button_box.setPadding(new Insets(20, 0, 0, 0));

        VBox left_card = new VBox(14, summary_box, button_box);
        left_card.setPadding(new Insets(24));
        left_card.setPrefWidth(360);
        left_card.setStyle("-fx-background-color: white; -fx-background-radius: 4;");

        accountTilesBox = new VBox(14);

        VBox right_card = new VBox(18, accounts_title, accountTilesBox);
        right_card.setPadding(new Insets(24));
        right_card.setPrefWidth(420);
        right_card.setStyle("-fx-background-color: white; -fx-background-radius: 4;");

        HBox top_cards = new HBox(30, left_card, right_card);
        top_cards.setAlignment(Pos.TOP_LEFT);

        HBox header = new HBox(history_label, spacer, filter_label);

        VBox trans_card = new VBox(16, header, transactionsTable);
        trans_card.setPadding(new Insets(24));
        trans_card.setMaxWidth(Double.MAX_VALUE);
        trans_card.setStyle("-fx-background-color: white; -fx-background-radius: 4;");

        VBox root = new VBox(24, top_cards, trans_card);
        root.setPadding(new Insets(30, 40, 30, 40));
        root.setStyle("-fx-background-color: #f4fffb;");
        return root;
    }

    private Pane build_withdraw_view() {
        Label title = new Label("Withdraw");
        Label subtitle = new Label("Make a payment");
        Label recipient_label = new Label("Recipient");
        Label account_label = new Label("Account to Pay From");
        Label amount_label = new Label("Amount to Transfer");

        Button confirm = new Button("Confirm Payment");
        Button cancel = new Button("Cancel");

        TextField amount_field = new TextField();
        ComboBox<String> recipient_box = new ComboBox<>();
        ComboBox<String> account_box = new ComboBox<>();

        style_text(title, 24, true, 0x000000);
        style_text(subtitle, 14, false, 0x000000);
        style_text(recipient_label, 14, true, 0x00A693);
        style_text(account_label, 14, true, 0x00A693);
        style_text(amount_label, 14, true, 0x00A693);
        style_text(confirm, 14, true, 0x000000);
        style_text(cancel, 14, false, 0x000000);
        style_text(amount_field, 14, false, 0x000000);
        style_text(recipient_box, 14, false, 0x000000);
        style_text(account_box, 14, false, 0x000000);

        recipient_box.getItems().addAll("Hydro-Quebec", "Bell", "Vidéotron");
        account_box.getItems().addAll("Checking Account (*** 1825)", "Visa Account (*** 5628)");

        HBox buttons = new HBox(10);
        confirm.setOnAction(e -> show_view(success_view));
        cancel.setOnAction(e -> show_view(home_view));
        buttons.getChildren().addAll(confirm, cancel);

        GridPane form = new GridPane();
        form.setVgap(15);
        form.setHgap(20);
        form.add(recipient_label, 0, 0);
        form.add(recipient_box, 0, 1);
        form.add(account_label, 1, 0);
        form.add(account_box, 1, 1);
        form.add(amount_label, 0, 2);
        form.add(amount_field, 0, 3, 2, 1);

        VBox root = new VBox(20, title, subtitle, form, buttons);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #f4fffb;");
        return root;
    }

    private Pane build_deposit_view() {
        Label title = new Label("Deposit");
        Label subtitle = new Label("Make a transfer to another account");
        Label from_label = new Label("From");
        ComboBox<String> from_box = new ComboBox<>();
        Label to_label = new Label("To");
        ComboBox<String> to_box = new ComboBox<>();
        Label sender_balance_label = new Label("Sender Balance");
        Label sender_balance = new Label("$ 1000.80");
        Label recipient_balance_label = new Label("Recipient Balance");
        Label recipient_balance = new Label("$ -");
        Label amount_label = new Label("Amount to Transfer");
        TextField amount_field = new TextField();
        CheckBox confirm_check = new CheckBox("I confirm the transfer amount is correct.");
        Button submit = new Button("Submit");
        Button cancel = new Button("Cancel");

        style_text(title, 24, true, 0x000000);
        style_text(subtitle, 14, false, 0x000000);
        style_text(from_label, 14, true, 0x00A693);
        style_text(from_box, 14, false, 0x000000);
        style_text(to_label, 14, true, 0x00A693);
        style_text(to_box, 14, false, 0x000000);
        style_text(sender_balance_label, 12, false, 0x808080);
        style_text(sender_balance, 24, true, 0x000000);
        style_text(recipient_balance_label, 12, false, 0x808080);
        style_text(recipient_balance, 24, true, 0x000000);
        style_text(amount_label, 14, true, 0x00A693);
        style_text(amount_field, 14, false, 0x000000);
        style_text(confirm_check, 13, false, 0x000000);
        style_text(submit, 14, true, 0x000000);
        style_text(cancel, 14, false, 0x000000);

        from_box.getItems().add("Checking Account (*** 1825)");
        to_box.getItems().addAll("Visa Account (*** 5628)", "Savings");
        amount_field.setPromptText("e.g. 250.00");

        String inputStyle = "-fx-background-color: white;" +
                "-fx-border-color: #D0D7DE;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 6 10;" +
                "-fx-background-insets: 0;";

        from_box.setPrefWidth(260);
        to_box.setPrefWidth(260);
        amount_field.setPrefWidth(260);

        from_box.setStyle(from_box.getStyle() + ";" + inputStyle);
        to_box.setStyle(to_box.getStyle() + ";" + inputStyle);
        amount_field.setStyle(amount_field.getStyle() + ";" + inputStyle);

        HBox buttons = new HBox(10, submit, cancel);
        submit.setOnAction(e -> show_view(success_view));
        cancel.setOnAction(e -> show_view(home_view));

        GridPane form = new GridPane();
        form.setVgap(12);
        form.setHgap(30);
        form.add(from_label, 0, 0);
        form.add(from_box, 0, 1);
        form.add(to_label, 1, 0);
        form.add(to_box, 1, 1);
        form.add(sender_balance_label, 0, 2);
        form.add(sender_balance, 0, 3);
        form.add(recipient_balance_label, 1, 2);
        form.add(recipient_balance, 1, 3);
        form.add(amount_label, 0, 4);
        form.add(amount_field, 0, 5, 2, 1);

        VBox root = new VBox(20, title, subtitle, form, confirm_check, buttons);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #f4fffb;");
        return root;
    }

    private Pane build_success_view() {
        Label title = new Label("Withdraw Successful");
        Label summary_title = new Label("Your Payment Summary");
        GridPane grid = new GridPane();
        Button another = new Button("Make another Payment");
        Button home = new Button("Return to Home");

        style_text(title, 24, true, 0x000000);
        style_text(summary_title, 16, false, 0x000000);
        style_text(another, 14, true, 0x000000);
        style_text(home, 14, false, 0x000000);

        grid.setVgap(8);
        grid.setHgap(20);
        add_summary_row(grid, 0, "Recipient Name / Biller", "Hydro-Quebec");
        add_summary_row(grid, 1, "Account / Reference Number", "879302145678");
        add_summary_row(grid, 2, "Amount Paid", "$ 87.00");
        add_summary_row(grid, 3, "Payment Method", "Checking Account (*** 1825)");
        add_summary_row(grid, 4, "Total Paid", "$ 87.00");
        add_summary_row(grid, 5, "Payment Date", "October 24, 2025");

        HBox buttons = new HBox(10, another, home);
        another.setOnAction(e -> show_view(withdraw_view));
        home.setOnAction(e -> show_view(home_view));

        VBox root = new VBox(20, title, summary_title, grid, buttons);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #f4fffb;");
        return root;
    }



    private Pane create_account_card(String title, String type, String number, String bg_color) {
        Label l1 = new Label(title);
        Label l2 = new Label(type);
        Label l3 = new Label(number);

        style_text(l1, 16, true, 0x000000);
        style_text(l2, 13, false, 0x000000);
        style_text(l3, 13, false, 0x000000);

        l1.setMinHeight(Region.USE_PREF_SIZE);

        VBox card = new VBox(4, l1, l2, l3);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + bg_color + "; -fx-background-radius: 14;");
        return card;
    }

    private void add_summary_row(GridPane grid, int row, String label, String value) {
        Label l = new Label(label + ":");
        Label v = new Label(value);
        style_text(l, 14, false, 0x000000);
        style_text(v, 14, false, 0x000000);
        grid.add(l, 0, row);
        grid.add(v, 1, row);
    }

    private Pane build_login_overlay() {
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.4);");

        VBox card = new VBox(20);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(24));
        card.setMaxWidth(380);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12;");

        HBox header = new HBox();
        Region spacer = new Region();
        Button close = new Button("X");
        close.setOnAction(e -> hideLoginPanel());
        close.setStyle("-fx-background-color: transparent; -fx-text-fill: #333; -fx-font-size: 16;");
        header.getChildren().addAll(spacer, close);
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label title = new Label("Sign in to MYBank");
        style_text(title, 24, true, 0x000000);
        Label subtitle = new Label("Enter your user ID and password to continue.");
        style_text(subtitle, 14, false, 0x000000);

        loginUserIdField = new TextField();
        loginUserIdField.setPromptText("User ID");
        loginUserIdField.setPrefWidth(280);
        loginPasswordField = new PasswordField();
        loginPasswordField.setPromptText("Password");
        loginPasswordField.setPrefWidth(280);
        Button loginButton = new Button("Log In");
        loginButton.setPrefWidth(280);
        loginButton.setDefaultButton(true);

        style_text(loginUserIdField, 14, false, 0x000000);
        style_text(loginPasswordField, 14, false, 0x000000);
        style_text(loginButton, 16, true, 0xFFFFFF);

        String inputStyle = "-fx-background-color: white;" +
                "-fx-border-color: #D0D7DE;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 10 12;";
        loginUserIdField.setStyle(inputStyle);
        loginPasswordField.setStyle(inputStyle);
        loginButton.setStyle(
                "-fx-background-color: #00A693;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 12 0;" +
                        "-fx-border-radius: 8;"
        );

        loginButton.setOnAction(e -> handleLogin());
        loginUserIdField.setOnAction(e -> handleLogin());
        loginPasswordField.setOnAction(e -> handleLogin());

        VBox.setMargin(loginUserIdField, new Insets(0, 0, 4, 0));
        VBox.setMargin(loginPasswordField, new Insets(0, 0, 4, 0));

        card.getChildren().addAll(header, title, subtitle, loginUserIdField, loginPasswordField, loginButton);
        overlay.getChildren().add(card);
        StackPane.setAlignment(card, Pos.CENTER);
        overlay.setVisible(false);
        return overlay;
    }

    private void showLoginPanel() {
        loginOverlay.setVisible(true);
    }

    private void hideLoginPanel() {
        loginOverlay.setVisible(false);
    }

    private void unlockAppAfterLogin() {
        loginOverlay.setVisible(false);
        profileBox.setVisible(true);
        profileBox.setManaged(true);
        sidebarLoginButton.setVisible(false);
        sidebarLoginButton.setManaged(false);
        sidebarLogoutButton.setVisible(true);
        sidebarLogoutButton.setManaged(true);
    }

    private boolean ensureLoggedIn() {
        if (authManager.isLoggedIn()) return true;
        showLoginPanel();
        showLoginError("Please log in first.");
        return false;
    }

    private void handleLogin() {
        try {
            long userId = Long.parseLong(loginUserIdField.getText().trim());
            Optional<Users.User> user = authManager.login(userId, loginPasswordField.getText());
            if (user.isPresent()) {
                applyUserToUI(user.get());
                unlockAppAfterLogin();
                show_view(home_view);
            } else {
                showLoginError("Invalid user id or password. Please try again.");
            }
        } catch (NumberFormatException ex) {
            showLoginError("User id must be a number.");
        }
    }

    private void handleLogout() {
        authManager.logout();
        sidebarInitials.setText("??");
        sidebarName.setText("Guest");
        sidebarRole.setText("Not signed in");
        acctTypeValue.setText("-");
        acctNumValue.setText("-");
        balValue.setText("$ -");
        accountTilesBox.getChildren().clear();
        transactionsTable.getItems().clear();
        profileBox.setVisible(false);
        profileBox.setManaged(false);
        sidebarLoginButton.setVisible(true);
        sidebarLoginButton.setManaged(true);
        sidebarLogoutButton.setVisible(false);
        sidebarLogoutButton.setManaged(false);
        show_view(home_view);
        showLoginPanel();
    }

    private void applyUserToUI(Users.User user) {
        String initials = user.name() != null && !user.name().isBlank()
                ? user.name().codePoints()
                .filter(Character::isLetter)
                .mapToObj(cp -> String.valueOf((char) cp))
                .limit(2)
                .reduce("", String::concat)
                .toUpperCase()
                : "US";
        sidebarInitials.setText(initials);
        sidebarName.setText(user.name() == null ? "Unknown" : user.name());
        sidebarRole.setText(user.role() == null ? "Unknown role" : user.role());

        Users.Account first = (user.accounts() != null && !user.accounts().isEmpty())
                ? user.accounts().get(0)
                : null;
        if (first != null) {
            acctTypeValue.setText(first.type());
            acctNumValue.setText(String.valueOf(first.number()));
            balValue.setText("$ " + first.balance());
        } else {
            acctTypeValue.setText("-");
            acctNumValue.setText("-");
            balValue.setText("$ -");
        }

        accountTilesBox.getChildren().clear();
        if (user.accounts() != null) {
            for (Users.Account acc : user.accounts()) {
                accountTilesBox.getChildren().add(
                        create_account_card(acc.type(), acc.type(), String.valueOf(acc.number()), "#b8ffe9")
                );
            }
        }

        transactionsTable.getItems().clear();
        if (first != null && first.transactions() != null) {
            for (Users.Transaction t : first.transactions()) {
                transactionsTable.getItems().add(
                        new Transaction(
                                t.details(),
                                String.valueOf(t.date()),
                                accLabelForTransaction(first),
                                formatAmount(t.amount())
                        )
                );
            }
        }
    }

    private String formatAmount(double amount) {
        return String.format("$ %.2f", amount);
    }

    private String accLabelForTransaction(Users.Account acc) {
        return acc.type() + " (" + acc.number() + ")";
    }

    private void showLoginError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setHeaderText(null);
        alert.setTitle("Login Required");
        alert.showAndWait();
    }

    private TableColumn<Transaction, String> create_header_column(String title) {
        TableColumn<Transaction, String> col = new TableColumn<>();
        Label header_label = new Label(title);
        style_text(header_label, 18, true, 0x00A693);
        col.setGraphic(header_label);
        col.setText(null);
        return col;
    }


    private void apply_sidebar_button_style(ToggleButton btn, boolean selected) {
        String bg = selected ? "white" : "transparent";
        btn.setStyle(
                "-fx-background-color: " + bg + ";" +
                        "-fx-background-radius: 0;" +
                        "-fx-background-insets: 0;" +
                        "-fx-padding: 10 0 10 20;"
        );
        style_text(btn, 16, selected, 0x000000);
    }

    private void style_body_column(TableColumn<Transaction, String> col) {
        col.setCellFactory(column -> new TableCell<>() {
            {
                setAlignment(Pos.CENTER);
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    style_text(this, 16, false, 0x000000);
                }
            }
        });
    }

    private void show_view(Pane view) {
        for (Node child : content_stack.getChildren()) {
            if (child == loginOverlay) continue;
            child.setVisible(child == view);
        }
    }

    public static class Transaction {
        public final String merchant;
        public final String date;
        public final String card;
        public final String amount;

        public Transaction(String merchant, String date, String card, String amount) {
            this.merchant = merchant;
            this.date = date;
            this.card = card;
            this.amount = amount;
        }

        public String getMerchant() { return merchant; }
        public String getDate() { return date; }
        public String getCard() { return card; }
        public String getAmount() { return amount; }
    }
}
