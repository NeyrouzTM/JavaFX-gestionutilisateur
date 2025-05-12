package esprit.tn.guiproject.controller;

import esprit.tn.guiproject.model.User;
import esprit.tn.guiproject.services.AuthService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> typeCombo;
    @FXML private Button loginButton;
    @FXML private Button signUpButton;
    @FXML private Label errorLabel;
    
    private AuthService authService;
    
    public void initialize() {
        authService = new AuthService();
        typeCombo.getItems().addAll("Utilisateur", "Manager");
        typeCombo.setValue("Utilisateur");
        
        // Configuration du champ de mot de passe
        passwordField.setPromptText("Mot de passe");
        passwordField.setStyle("-fx-font-size: 14px;");
        
        // Ajouter un listener pour effacer le message d'erreur quand l'utilisateur commence à taper
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty()) {
                errorLabel.setText("");
            }
        });
    }
    
    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();
        String role = typeCombo.getValue().equals("Manager") ? "MANAGER" : "CLIENT";
        
        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Veuillez remplir tous les champs");
            return;
        }
        
        User user = authService.login(email, password);
        if (user != null && user.getRole().equals(role)) {
            if (user.getRole().equals("CLIENT") && user.getStatut().equals("0")) {
                errorLabel.setText("Votre compte est inactif. Veuillez contacter le manager.");
                return;
            }
            if (user.getRole().equals("CLIENT")) {
                openUserProfile(user);
            } else if (user.getRole().equals("MANAGER")) {
                openManagerDashboard(user);
            }
        } else {
            errorLabel.setText("Email ou mot de passe incorrect");
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
    
    private void openUserProfile(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user-profile.fxml"));
            Parent root = loader.load();
            
            UserProfileController controller = loader.getController();
            controller.setUser(user);
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setTitle("Smart Move - Mon Profil");
            stage.setScene(scene);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void openManagerDashboard(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/manager-dashboard.fxml"));
            Parent root = loader.load();
            
            ManagerDashboardController controller = loader.getController();
            controller.setManager(user);
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setTitle("Smart Move - Tableau de bord Manager");
            stage.setScene(scene);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}