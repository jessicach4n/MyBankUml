module bank {
    requires javafx.controls;
    requires javafx.fxml;

    exports bank.gui;
    opens bank.gui to javafx.fxml;
}