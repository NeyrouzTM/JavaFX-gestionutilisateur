package esprit.tn.guiproject.services;

import esprit.tn.guiproject.model.User;
import esprit.tn.guiproject.connection.DatabaseConnection;
import esprit.tn.guiproject.util.PasswordHasher;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuthService {
    private final DatabaseConnection dbConnection;

    public AuthService() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    public User register(String nom, String prenom, String email, String motDePasse, String role) {
        String query = "INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, role, statut) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = dbConnection.getConnection();
            if (conn == null) {
                throw new SQLException("Impossible d'établir la connexion à la base de données");
            }
            
            // Vérifier si l'email existe déjà
            if (isEmailExists(email)) {
                throw new SQLException("Cet email est déjà utilisé");
            }
            
            stmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            
            stmt.setString(1, nom);
            stmt.setString(2, prenom);
            stmt.setString(3, email);
            stmt.setString(4, PasswordHasher.hash(motDePasse));
            stmt.setString(5, role); // Le rôle est maintenant stocké comme une chaîne simple
            int statut = role.equals("MANAGER") ? 1 : 0;
            stmt.setInt(6, statut);
            
            System.out.println("Exécution de la requête d'inscription...");
            System.out.println("SQL: " + query);
            System.out.println("Paramètres: " + nom + ", " + prenom + ", " + email + ", [mot de passe haché], " + role + ", " + statut);
            
            int affectedRows = stmt.executeUpdate();
            System.out.println("Nombre de lignes affectées : " + affectedRows);
            
            if (affectedRows > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    System.out.println("Utilisateur créé avec l'ID : " + id);
                    
                    // Vérifier que l'utilisateur a bien été créé
                    String verifyQuery = "SELECT * FROM utilisateur WHERE id = ?";
                    try (PreparedStatement verifyStmt = conn.prepareStatement(verifyQuery)) {
                        verifyStmt.setInt(1, id);
                        ResultSet verifyRs = verifyStmt.executeQuery();
                        if (verifyRs.next()) {
                            System.out.println("Vérification réussie : utilisateur trouvé dans la base de données");
                            return new User(
                                id,
                                nom,
                                prenom,
                                email,
                                motDePasse,
                                role,
                                String.valueOf(statut)
                            );
                        }
                    }
                }
            }
            throw new SQLException("Échec de l'insertion de l'utilisateur");
            
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de l'inscription : " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'inscription : " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public User login(String email, String motDePasse) {
        String query = "SELECT * FROM utilisateur WHERE LOWER(email) = LOWER(?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = dbConnection.getConnection();
            if (conn == null) {
                throw new SQLException("Impossible d'établir la connexion à la base de données");
            }
            
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, email);
            
            System.out.println("Tentative de connexion pour l'email : " + email);
            System.out.println("Requête SQL : " + query);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String hashedPassword = rs.getString("mot_de_passe");
                String dbEmail = rs.getString("email");
                System.out.println("Utilisateur trouvé dans la base de données :");
                System.out.println("Email dans la base : " + dbEmail);
                
                // Vérifier le mot de passe
                if (PasswordHasher.verify(motDePasse, hashedPassword)) {
                    System.out.println("Mot de passe vérifié avec succès");
                    return new User(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("mot_de_passe"),
                        rs.getString("role"),
                        rs.getString("statut")
                    );
                } else {
                    System.out.println("Mot de passe incorrect");
                }
            } else {
                System.out.println("Aucun utilisateur trouvé avec cet email");
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la connexion : " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        String query = "SELECT mot_de_passe FROM utilisateur WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = dbConnection.getConnection();
            pstmt = conn.prepareStatement(query);
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String currentPassword = rs.getString("mot_de_passe");
                if (PasswordHasher.verify(oldPassword, currentPassword)) {
                    String hashedNewPassword = PasswordHasher.hash(newPassword);
                    
                    String updateQuery = "UPDATE utilisateur SET mot_de_passe = ? WHERE id = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                        updateStmt.setString(1, hashedNewPassword);
                        updateStmt.setInt(2, userId);
                        return updateStmt.executeUpdate() > 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean updateUserStatus(int userId, int newStatus) {
        String query = "UPDATE utilisateur SET statut = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = dbConnection.getConnection();
            pstmt = conn.prepareStatement(query);
            
            pstmt.setInt(1, newStatus);
            pstmt.setInt(2, userId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean verifyPassword(String email, String password) {
        String query = "SELECT mot_de_passe FROM utilisateur WHERE email = ?";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String hashedPassword = rs.getString("mot_de_passe");
                return PasswordHasher.verify(password, hashedPassword);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public User updateUser(int userId, String newNom, String newPrenom, String newPassword) {
        StringBuilder query = new StringBuilder("UPDATE utilisateur SET nom = ?, prenom = ?");
        if (newPassword != null && !newPassword.isEmpty()) {
            query.append(", mot_de_passe = ?");
        }
        query.append(" WHERE id = ?");

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query.toString())) {
            stmt.setString(1, newNom);
            stmt.setString(2, newPrenom);
            if (newPassword != null && !newPassword.isEmpty()) {
                stmt.setString(3, PasswordHasher.hash(newPassword));
                stmt.setInt(4, userId);
            } else {
                stmt.setInt(3, userId);
            }

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                String selectQuery = "SELECT * FROM utilisateur WHERE id = ?";
                try (PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {
                    selectStmt.setInt(1, userId);
                    ResultSet rs = selectStmt.executeQuery();
                    if (rs.next()) {
                        return new User(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("email"),
                            rs.getString("mot_de_passe"),
                            rs.getString("role"),
                            rs.getString("statut")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM utilisateur WHERE role = 'CLIENT'";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = dbConnection.getConnection();
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                users.add(new User(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("email"),
                    rs.getString("mot_de_passe"),
                    rs.getString("role"),
                    rs.getString("statut")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return users;
    }

    public boolean isEmailExists(String email) {
        String query = "SELECT COUNT(*) FROM utilisateur WHERE email = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = dbConnection.getConnection();
            pstmt = conn.prepareStatement(query);
            
            pstmt.setString(1, email);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean sendPasswordResetEmail(String email) {
        try (Connection conn = dbConnection.getConnection()) {
            String query = "SELECT * FROM utilisateur WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Ici, vous pouvez ajouter la logique pour envoyer un email
                // Pour l'instant, nous retournons simplement true pour simuler l'envoi
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean resetPassword(String email, String newPassword) {
        String query = "UPDATE utilisateur SET mot_de_passe = ? WHERE email = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            String hashedPassword = PasswordHasher.hash(newPassword);
            stmt.setString(1, hashedPassword);
            stmt.setString(2, email);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean emailExists(String email) {
        String query = "SELECT COUNT(*) FROM utilisateur WHERE email = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
} 