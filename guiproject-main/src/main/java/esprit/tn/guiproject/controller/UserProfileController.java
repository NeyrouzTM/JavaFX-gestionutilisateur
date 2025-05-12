package esprit.tn.guiproject.controller;

import esprit.tn.guiproject.model.User;
import esprit.tn.guiproject.services.AuthService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.Modality;

import java.io.IOException;

public class UserProfileController {
    
    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField emailField;
    @FXML private Button logoutButton;
    
    private User currentUser;
    private AuthService authService;
    
    public void initialize() {
        authService = new AuthService();
    }
    
    public void setUser(User user) {
        this.currentUser = user;
        updateFields();
    }
    
    private void updateFields() {
        if (currentUser != null) {
            nomField.setText(currentUser.getNom());
            prenomField.setText(currentUser.getPrenom());
            emailField.setText(currentUser.getEmail());
        }
    }
    
    @FXML
    private void handleUpdateProfile() {
        if (validateFields()) {
            User updatedUser = authService.updateUser(
                currentUser.getId(),
                nomField.getText(),
                prenomField.getText(),
                null // Pas de changement de mot de passe ici
            );
            
            if (updatedUser != null) {
                currentUser = updatedUser;
                showAlert("Succès", "Profil mis à jour avec succès", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Erreur", "Erreur lors de la mise à jour du profil", Alert.AlertType.ERROR);
            }
        }
    }
    
    @FXML
    private void handleChangePassword() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/change-password.fxml"));
            Parent root = loader.load();
            ChangePasswordController controller = loader.getController();
            controller.setUserId(currentUser.getId());
            Scene scene = new Scene(root);
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement de la page", Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    private void handleForgotPassword() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/forgot-password.fxml"));
            Parent root = loader.load();
            
            ForgotPasswordController controller = loader.getController();
            controller.setEmail(currentUser.getEmail());
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Mot de passe oublié");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la fenêtre de mot de passe oublié", Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/welcome.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setTitle("Smart Move - Bienvenue");
            stage.setScene(scene);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private boolean validateFields() {
        if (nomField.getText().isEmpty() || prenomField.getText().isEmpty() || emailField.getText().isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs", Alert.AlertType.ERROR);
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