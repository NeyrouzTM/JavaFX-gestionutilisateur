package esprit.tn.guiproject.controller;

import esprit.tn.guiproject.model.User;
import esprit.tn.guiproject.services.AuthService;
import esprit.tn.guiproject.util.PDFGenerator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import java.io.File;

import java.io.IOException;
import java.util.List;

public class ManagerDashboardController {
    
    @FXML private Text managerNameText;
    @FXML private Text managerEmailText;
    @FXML private TextField searchField;
    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, Integer> idColumn;
    @FXML private TableColumn<User, String> nomColumn;
    @FXML private TableColumn<User, String> prenomColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> statutColumn;
    @FXML private TableColumn<User, Void> actionsColumn;
    
    private User currentManager;
    private AuthService authService;
    
    public void initialize() {
        authService = new AuthService();
        setupTable();
        setupSearch();
    }
    
    public void setManager(User manager) {
        this.currentManager = manager;
        updateManagerInfo();
        loadUsers();
    }
    
    private void updateManagerInfo() {
        if (currentManager != null) {
            managerNameText.setText(currentManager.getNom() + " " + currentManager.getPrenom());
            managerEmailText.setText(currentManager.getEmail());
        }
    }
    
    private void setupTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));
        
        actionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button acceptButton = new Button("Accepter");
            private final Button rejectButton = new Button("Refuser");
            
            {
                acceptButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                rejectButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                
                acceptButton.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    handleAcceptUser(user);
                });
                
                rejectButton.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    handleRejectUser(user);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    User user = getTableView().getItems().get(getIndex());
                    if (user.getStatut().equals("0")) {
                        HBox buttons = new HBox(10, acceptButton, rejectButton);
                        setGraphic(buttons);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });
    }
    
    private void setupSearch() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            FilteredList<User> filteredList = new FilteredList<>(usersTable.getItems());
            filteredList.setPredicate(user -> {
                if (newVal == null || newVal.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newVal.toLowerCase();
                return user.getNom().toLowerCase().contains(lowerCaseFilter) ||
                       user.getPrenom().toLowerCase().contains(lowerCaseFilter) ||
                       user.getEmail().toLowerCase().contains(lowerCaseFilter);
            });
            usersTable.setItems(filteredList);
        });
    }
    
    private void loadUsers() {
        List<User> users = authService.getAllUsers();
        usersTable.setItems(FXCollections.observableArrayList(users));
    }
    
    private void handleAcceptUser(User user) {
        if (authService.updateUserStatus(user.getId(), 1)) {
            showAlert("Succès", "Compte client activé avec succès", Alert.AlertType.INFORMATION);
            loadUsers();
        } else {
            showAlert("Erreur", "Erreur lors de l'activation du compte", Alert.AlertType.ERROR);
        }
    }
    
    private void handleRejectUser(User user) {
        if (authService.updateUserStatus(user.getId(), 0)) {
            showAlert("Succès", "Compte client bloqué avec succès", Alert.AlertType.INFORMATION);
            loadUsers();
        } else {
            showAlert("Erreur", "Erreur lors du blocage du compte", Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/welcome.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            
            Stage stage = (Stage) managerNameText.getScene().getWindow();
            stage.setTitle("Smart Move - Bienvenue");
            stage.setScene(scene);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleEditProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user-profile.fxml"));
            Parent root = loader.load();
            UserProfileController controller = loader.getController();
            if (currentManager != null) {
                controller.setUser(currentManager);
            }
            Scene scene = new Scene(root);
            Stage stage = (Stage) managerNameText.getScene().getWindow();
            stage.setTitle("Smart Move - Mon Profil Manager");
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement de la page de profil", Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase();
        FilteredList<User> filteredList = new FilteredList<>(usersTable.getItems());
        filteredList.setPredicate(user -> {
            if (searchText.isEmpty()) {
                return true;
            }
            return user.getNom().toLowerCase().contains(searchText) ||
                   user.getPrenom().toLowerCase().contains(searchText) ||
                   user.getEmail().toLowerCase().contains(searchText);
        });
        usersTable.setItems(filteredList);
    }

    @FXML
    private void handleFilterActive() {
        FilteredList<User> filteredList = new FilteredList<>(usersTable.getItems());
        filteredList.setPredicate(user -> user.getStatut().equals("1"));
        usersTable.setItems(filteredList);
    }

    @FXML
    private void handleRefresh() {
        loadUsers();
        searchField.clear();
    }
    
    @FXML
    private void handleExportPDF() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer le rapport PDF");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
            );
            fileChooser.setInitialFileName("rapport_utilisateurs.pdf");
            
            File file = fileChooser.showSaveDialog(usersTable.getScene().getWindow());
            if (file != null) {
                PDFGenerator.generateUserReport(usersTable.getItems(), file.getAbsolutePath());
                showAlert("Succès", "Le rapport PDF a été généré avec succès!", Alert.AlertType.INFORMATION);
            }
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la génération du PDF: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
    
    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 