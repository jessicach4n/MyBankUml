package bank.gui;

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

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        content_stack = new StackPane();
        home_view = build_home_view();
        withdraw_view = build_withdraw_view();
        deposit_view = build_deposit_view();
        success_view = build_success_view();

        content_stack.getChildren().addAll(home_view, withdraw_view, deposit_view, success_view);
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
    }

    private Node build_sidebar() {
        Label app_label = new Label("BankUML");
        Circle avatar_circle = new Circle(50, Color.BLACK);
        Label initials = new Label("JD");
        Label name = new Label("John Deer");
        Label role = new Label("Client");

        ToggleGroup group = new ToggleGroup();
        ToggleButton accounts_btn = new ToggleButton("Accounts");
        ToggleButton deposit_btn = new ToggleButton("Deposit");
        ToggleButton withdraw_btn = new ToggleButton("Withdraw");

        Button settings_btn = new Button("Settings");
        Region spacer = new Region();

        style_text(app_label, 16, false, 0x000000);
        style_text(initials, 40, true, 0xFFFFFF);
        style_text(name, 16, true, 0x000000);
        style_text(role, 14, false, 0x000000);
        style_text(settings_btn, 16, false, 0xFFFFFF);

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
            show_view(home_view);
        });
        deposit_btn.setOnAction(e -> {
            if (!deposit_btn.isSelected()) {
                deposit_btn.setSelected(true);
            }
            show_view(deposit_view);
        });
        withdraw_btn.setOnAction(e -> {
            if (!withdraw_btn.isSelected()) {
                withdraw_btn.setSelected(true);
            }
            show_view(withdraw_view);
        });

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

        StackPane avatar = new StackPane(avatar_circle, initials);
        avatar.setAlignment(Pos.CENTER);

        VBox profile_box = new VBox(6, avatar, name, role);
        profile_box.setAlignment(Pos.CENTER);
        profile_box.setPadding(new Insets(10, 0, 10, 0));
        profile_box.setMaxWidth(Double.MAX_VALUE);

        VBox header_box = new VBox(12, app_label, profile_box);
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
        Label acct_type = new Label("Student");
        Label acct_num_label = new Label("Account Number");
        Label acct_num = new Label("879302145678");
        Label bal_label = new Label("Balance");
        Label bal_value = new Label("$ 1187.89");

        Button transfer_btn = new Button("Make A Transfer");
        Button pay_bills_btn = new Button("Pay Bills");

        Label accounts_title = new Label("Accounts");

        Label history_label = new Label("Transaction History");
        Label filter_label = new Label("Filter by: Date");
        Region spacer = new Region();

        TableView<Transaction> table = new TableView<>();
        TableColumn<Transaction, String> merchant_col = create_header_column("Merchant");
        TableColumn<Transaction, String> date_col = create_header_column("Date");
        TableColumn<Transaction, String> card_col = create_header_column("Card");
        TableColumn<Transaction, String> amount_col = create_header_column("Amount");

        style_text(acct_type_label, 12, false, 0x000000);
        style_text(acct_type, 28, true, 0x000000);
        style_text(acct_num_label, 12, false, 0x000000);
        style_text(acct_num, 18, true, 0x000000);
        style_text(bal_label, 12, false, 0x000000);
        style_text(bal_value, 24, true, 0x000000);

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

        table.setPrefHeight(230);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setStyle(
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

        table.getColumns().addAll(merchant_col, date_col, card_col, amount_col);

        table.getItems().setAll(
                new Transaction("Concordia University", "10/22/2025", "Visa Debit", "$ 2671.89"),
                new Transaction("Tim Hortons", "10/22/2025", "Visa Credit", "$ 1.79"),
                new Transaction("Pharmaprix", "10/21/2025", "Visa Credit", "$ 89.12"),
                new Transaction("IGA", "10/21/2025", "Visa Credit", "$ 101.10"),
                new Transaction("Indigo", "10/20/2025", "Visa Credit", "$ 45.17")
        );

        VBox summary_box = new VBox(8,
                acct_type_label, acct_type,
                acct_num_label, acct_num,
                bal_label, bal_value
        );

        VBox button_box = new VBox(10, transfer_btn, pay_bills_btn);
        button_box.setPadding(new Insets(20, 0, 0, 0));

        VBox left_card = new VBox(14, summary_box, button_box);
        left_card.setPadding(new Insets(24));
        left_card.setPrefWidth(360);
        left_card.setStyle("-fx-background-color: white; -fx-background-radius: 4;");

        VBox account_tiles = new VBox(14,
                create_account_card("Checking Account", "Visa Debit",
                        "4519 8030 3345 1825", "#b8ffe9"),
                create_account_card("Visa Account", "Visa Credit",
                        "4515 1992 1276 5628", "#bcded7")
        );

        VBox right_card = new VBox(18, accounts_title, account_tiles);
        right_card.setPadding(new Insets(24));
        right_card.setPrefWidth(420);
        right_card.setStyle("-fx-background-color: white; -fx-background-radius: 4;");

        HBox top_cards = new HBox(30, left_card, right_card);
        top_cards.setAlignment(Pos.TOP_LEFT);

        HBox header = new HBox(history_label, spacer, filter_label);

        VBox trans_card = new VBox(16, header, table);
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