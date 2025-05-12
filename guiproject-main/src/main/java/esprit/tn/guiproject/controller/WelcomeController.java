package esprit.tn.guiproject.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class WelcomeController {
    
    @FXML
    private Button signInButton;
    
    @FXML
    private Button signUpButton;
    
    @FXML
    private void handleSignIn() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            
            Stage stage = (Stage) signInButton.getScene().getWindow();
            stage.setTitle("Smart Move - Connexion");
            stage.setScene(scene);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleSignUp() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/signup.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            
            Stage stage = (Stage) signUpButton.getScene().getWindow();
            stage.setTitle("Smart Move - Inscription");
            stage.setScene(scene);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 