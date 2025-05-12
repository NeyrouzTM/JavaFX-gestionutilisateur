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

public class SignUpController {
    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleCombo;
    @FXML private Label errorLabel;
    
    private AuthService authService;
    
    @FXML
    public void initialize() {
        authService = new AuthService();
        
        // Initialiser le ComboBox avec les types de compte
        roleCombo.getItems().addAll("Client", "Manager");
        roleCombo.setValue("Client"); // Valeur par défaut
    }
    
    @FXML
    private void handleSignUp() {
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String role = roleCombo.getValue().equals("Manager") ? "MANAGER" : "CLIENT";
        
        if (validateFields()) {
            if (authService.isEmailExists(email)) {
                errorLabel.setText("Cet email est déjà utilisé");
                return;
            }
            
            try {
                System.out.println("Tentative d'inscription avec le rôle : " + role);
                User user = authService.register(nom, prenom, email, password, role);
                if (user != null) {
                    showAlert("Succès", "Inscription réussie ! Vous pouvez maintenant vous connecter.", Alert.AlertType.INFORMATION);
                    handleBack();
                } else {
                    errorLabel.setText("Erreur lors de l'inscription. Veuillez réessayer.");
                }
            } catch (Exception e) {
                errorLabel.setText("Erreur lors de l'inscription : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/welcome.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            
            Stage stage = (Stage) nomField.getScene().getWindow();
            stage.setTitle("Smart Move - Bienvenue");
            stage.setScene(scene);
            
        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("Erreur lors du retour à l'écran d'accueil");
        }
    }
    
    private boolean validateFields() {
        if (nomField.getText().trim().isEmpty() ||
            prenomField.getText().trim().isEmpty() ||
            emailField.getText().trim().isEmpty() ||
            passwordField.getText().isEmpty()) {
            errorLabel.setText("Veuillez remplir tous les champs");
            return false;
        }
        
        if (!emailField.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            errorLabel.setText("Format d'email invalide");
            return false;
        }
        
        if (passwordField.getText().length() < 6) {
            errorLabel.setText("Le mot de passe doit contenir au moins 6 caractères");
            return false;
        }
        
        return true;
    }
    
    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 