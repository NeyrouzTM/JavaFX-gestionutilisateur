package esprit.tn.guiproject.util;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            // Charger le fichier FXML de l'écran d'accueil
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/welcome.fxml"));
            Parent root = loader.load();
            
            // Créer la scène
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            
            // Configurer la fenêtre principale
            primaryStage.setTitle("Smart Move - Bienvenue");
            primaryStage.setScene(scene);
            primaryStage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}