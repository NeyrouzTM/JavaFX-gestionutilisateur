package esprit.tn.guiproject.controller;

import esprit.tn.guiproject.services.AuthService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

public class ChangePasswordController {
    @FXML
    private PasswordField currentPasswordField;
    
    @FXML
    private PasswordField newPasswordField;
    
    @FXML
    private PasswordField confirmPasswordField;
    
    @FXML
    private Label errorLabel;
    
    private AuthService authService;
    private int userId;
    
    public void initialize() {
        authService = new AuthService();
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    @FXML
    private void handleSave() {
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        
        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            errorLabel.setText("Veuillez remplir tous les champs");
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            errorLabel.setText("Les mots de passe ne correspondent pas");
            return;
        }
        
        if (authService.changePassword(userId, currentPassword, newPassword)) {
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Le mot de passe a été modifié avec succès");
            handleBack();
        } else {
            errorLabel.setText("Mot de passe actuel incorrect");
        }
    }
    
    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user-profile.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) currentPasswordField.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Erreur lors du chargement de la page");
        }
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 
