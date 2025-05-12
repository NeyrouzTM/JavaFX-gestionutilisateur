package esprit.tn.guiproject.controller;

import esprit.tn.guiproject.services.AuthService;
import esprit.tn.guiproject.util.EmailUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Random;

public class ForgotPasswordController {
    @FXML private TextField emailField;
    @FXML private TextField verificationCodeField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label errorLabel;
    
    private AuthService authService;
    private String generatedCode;
    
    public void initialize() {
        authService = new AuthService();
    }
    
    public void setEmail(String email) {
        emailField.setText(email);
    }
    
    @FXML
    private void handleSendCode() {
        String email = emailField.getText();
        if (email.isEmpty()) {
            errorLabel.setText("Veuillez entrer votre email");
            return;
        }
        
        if (!authService.emailExists(email)) {
            errorLabel.setText("Cet email n'est pas associé à un compte");
            return;
        }
        
        // Générer un code de vérification
        generatedCode = generateVerificationCode();
        
        try {
            // Envoyer l'email avec le code
            EmailUtil.sendPasswordResetEmail(email, generatedCode);
            errorLabel.setText("Un code de vérification a été envoyé à votre email");
        } catch (Exception e) {
            errorLabel.setText("Erreur lors de l'envoi de l'email: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleReset() {
        String email = emailField.getText();
        String code = verificationCodeField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        
        if (email.isEmpty() || code.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            errorLabel.setText("Veuillez remplir tous les champs");
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            errorLabel.setText("Les mots de passe ne correspondent pas");
            return;
        }
        
        if (!code.equals(generatedCode)) {
            errorLabel.setText("Code de vérification incorrect");
            return;
        }
        
        if (authService.resetPassword(email, newPassword)) {
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Votre mot de passe a été réinitialisé avec succès");
            handleCancel();
        } else {
            errorLabel.setText("Erreur lors de la réinitialisation du mot de passe");
        }
    }
    
    @FXML
    private void handleCancel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Erreur lors du chargement de la page");
        }
    }
    
    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // Code à 6 chiffres
        return String.valueOf(code);
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 