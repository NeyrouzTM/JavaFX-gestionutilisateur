package esprit.tn.guiproject.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import esprit.tn.guiproject.util.PasswordHasher;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;
    private static final String DB_NAME = "pidev";
    private static final String URL = "jdbc:mysql://localhost:3306/" + DB_NAME + "?useUnicode=true&characterEncoding=UTF-8&useSSL=false";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private DatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Pilote JDBC chargé avec succès");
            
            // Établir la connexion à la base de données existante
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connexion à la base de données établie avec succès");
            
            // Recréer la table utilisateur
            recreateUserTable();
            
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Erreur lors de l'initialisation de la base de données : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void recreateUserTable() {
        try (Statement stmt = connection.createStatement()) {
            // Supprimer la table si elle existe
            stmt.executeUpdate("DROP TABLE IF EXISTS utilisateur");
            
            // Créer la nouvelle table
            String createTableSQL = "CREATE TABLE utilisateur (" +
                "id INT PRIMARY KEY AUTO_INCREMENT, " +
                "nom VARCHAR(50) NOT NULL, " +
                "prenom VARCHAR(50) NOT NULL, " +
                "email VARCHAR(100) NOT NULL UNIQUE, " +
                "mot_de_passe VARCHAR(255) NOT NULL, " +
                "role VARCHAR(20) NOT NULL, " +
                "statut TINYINT NOT NULL DEFAULT 0)";
            
            stmt.executeUpdate(createTableSQL);
            System.out.println("Table utilisateur recréée avec succès");
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recréation de la table : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createDatabaseIfNotExists() {
        try (Connection tempConn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = tempConn.createStatement()) {
            
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
            System.out.println("Base de données créée ou déjà existante");
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création de la base de données : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createAdminIfNotExists() {
        try (Statement stmt = connection.createStatement()) {
            // Vérifier si l'admin existe déjà
            var rs = stmt.executeQuery("SELECT COUNT(*) FROM utilisateur WHERE role = 'ADMIN'");
            if (rs.next() && rs.getInt(1) == 0) {
                // Créer l'admin avec le mot de passe haché
                String hashedPassword = PasswordHasher.hash("admin123");
                String insertAdminSQL = String.format(
                    "INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, role, statut) " +
                    "VALUES ('Administrateur', 'Admin', 'admin@example.com', '%s', 'ADMIN', 'ACTIVE')",
                    hashedPassword
                );
                stmt.executeUpdate(insertAdminSQL);
                System.out.println("Utilisateur admin créé avec succès");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création de l'admin : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Nouvelle connexion établie");
            }
            return connection;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de la connexion : " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connexion à la base de données fermée");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la fermeture de la connexion : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void initializeDatabase() {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS utilisateur (" +
            "id INT PRIMARY KEY AUTO_INCREMENT," +
            "nom VARCHAR(50) NOT NULL," +
            "prenom VARCHAR(50) NOT NULL," +
            "email VARCHAR(100) NOT NULL UNIQUE," +
            "mot_de_passe VARCHAR(255) NOT NULL," +
            "role VARCHAR(20) NOT NULL," +
            "statut TINYINT NOT NULL DEFAULT 0" +
            ")";
            
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableQuery);
            System.out.println("Table utilisateur vérifiée/créée avec succès");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'initialisation de la base de données : " + e.getMessage());
            e.printStackTrace();
        }
    }
}