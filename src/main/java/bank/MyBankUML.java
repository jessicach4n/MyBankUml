package bank; 

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class MyBankUML extends Application { 

    // The main entry point for all JavaFX applications.
    @Override
    public void start(Stage primaryStage) throws Exception {
        
        // src/main/resources/bank/gui/TellerHomePage.fxml
        URL fxmlUrl = getClass().getResource("/bank/gui/TellerHomePage.fxml");
        // --------------------------------
        
        if (fxmlUrl == null) {
            System.err.println("FATAL ERROR: Cannot find LoginPage.fxml.");
            return;
        }

        // 2. Load the FXML file
        Parent root = FXMLLoader.load(fxmlUrl);

        // 3. Set up the Scene
        Scene scene = new Scene(root, 1000, 700); 

        // 4. Set the Stage properties
        primaryStage.setTitle("MyBankUML");
        primaryStage.setScene(scene);
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(900);
        primaryStage.show();
    }

    // Standard Java main method to launch the JavaFX application
    public static void main(String[] args) {
        launch(args);
    }
}