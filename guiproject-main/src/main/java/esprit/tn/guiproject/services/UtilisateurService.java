package esprit.tn.guiproject.services;

import esprit.tn.guiproject.connection.DatabaseConnection;
import esprit.tn.guiproject.entities.Utilisateur;
import esprit.tn.guiproject.util.EmailUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class to handle CRUD operations and user approval for the Utilisateur entity.
 */
public class UtilisateurService {
    private final DatabaseConnection dbConnection = DatabaseConnection.getInstance();
    private final EmailUtil emailUtil = new EmailUtil();

    /**
     * Creates a new user in the database with a pending status.
     * @param utilisateur The user to create.
     * @throws SQLException if a database error occurs.
     */
    public void createUtilisateur(Utilisateur utilisateur) throws SQLException {
        String query = "INSERT INTO Utilisateur (nom, email, mot_de_passe, role, statut) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, utilisateur.getNom());
            stmt.setString(2, utilisateur.getEmail());
            stmt.setString(3, utilisateur.getMotDePasse());
            stmt.setString(4, utilisateur.getRole());
            stmt.setString(5, "en_attente");
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    utilisateur.setId(rs.getInt(1));
                }
            }
            System.out.println("Created utilisateur: " + utilisateur.getEmail() + " (pending approval)");
        }
    }

    /**
     * Retrieves a user by their ID.
     * @param id The user ID.
     * @return The user object, or null if not found.
     * @throws SQLException if a database error occurs.
     */
    public Utilisateur getUtilisateurById(int id) throws SQLException {
        String query = "SELECT id, nom, email, mot_de_passe, role, statut FROM Utilisateur WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Utilisateur(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getString("email"),
                            rs.getString("mot_de_passe"),
                            rs.getString("role"),
                            rs.getString("statut")
                    );
                }
            }
            return null;
        }
    }

    /**
     * Retrieves a user by their email.
     * @param email The user email.
     * @return The user object, or null if not found.
     * @throws SQLException if a database error occurs.
     */
    public Utilisateur getUtilisateurByEmail(String email) throws SQLException {
        String query = "SELECT id, nom, email, mot_de_passe, role, statut FROM Utilisateur WHERE email = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Utilisateur(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getString("email"),
                            rs.getString("mot_de_passe"),
                            rs.getString("role"),
                            rs.getString("statut")
                    );
                }
            }
            return null;
        }
    }

    /**
     * Retrieves all users from the database.
     * @return A list of all users.
     * @throws SQLException if a database error occurs.
     */
    public List<Utilisateur> getAllUtilisateurs() throws SQLException {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        String query = "SELECT id, nom, email, role, statut FROM Utilisateur ORDER BY nom ASC";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                utilisateurs.add(new Utilisateur(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("email"),
                        rs.getString("role"),
                        rs.getString("statut")
                ));
            }
            return utilisateurs;
        }
    }

    /**
     * Updates an existing user in the database.
     * @param utilisateur The user to update.
     * @throws SQLException if a database error occurs.
     */
    public void updateUtilisateur(Utilisateur utilisateur) throws SQLException {
        String query = "UPDATE Utilisateur SET nom = ?, email = ?, mot_de_passe = ?, role = ?, statut = ? WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, utilisateur.getNom());
            stmt.setString(2, utilisateur.getEmail());
            stmt.setString(3, utilisateur.getMotDePasse());
            stmt.setString(4, utilisateur.getRole());
            stmt.setString(5, utilisateur.getStatut());
            stmt.setInt(6, utilisateur.getId());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Utilisateur not found");
            }
            System.out.println("Updated utilisateur: " + utilisateur.getEmail());
        }
    }

    /**
     * Deletes a user from the database.
     * @param id The user ID to delete.
     * @throws SQLException if a database error occurs.
     */
    public void deleteUtilisateur(int id) throws SQLException {
        String query = "DELETE FROM Utilisateur WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Utilisateur not found");
            }
        }
    }

    /**
     * Approves or refuses a user, sending an email if approved.
     * @param id The user ID.
     * @param approve True to approve, false to refuse.
     * @throws SQLException if a database error occurs.
     */
    public void approveUtilisateur(int id, boolean approve) throws SQLException {
        String newStatut = approve ? "approuve" : "refuse";
        String query = "UPDATE Utilisateur SET statut = ? WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, newStatut);
            stmt.setInt(2, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Utilisateur not found");
            }
            Utilisateur utilisateur = getUtilisateurById(id);
            if (utilisateur != null && newStatut.equals("approuve")) {
                emailUtil.sendApprovalEmail(utilisateur.getEmail(), utilisateur.getNom());
            }
            System.out.println("Utilisateur " + id + " " + newStatut);
        }
    }
}