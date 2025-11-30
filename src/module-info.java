module bank {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    exports bank.gui;
    opens bank.gui to javafx.fxml;
}
