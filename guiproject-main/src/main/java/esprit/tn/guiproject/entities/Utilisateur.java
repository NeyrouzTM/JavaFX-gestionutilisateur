package esprit.tn.guiproject.entities;

public class Utilisateur {
    private int id;
    private String nom;
    private String email;
    private String motDePasse;
    private String role;
    private String statut;

    /**
     * Constructor without ID (for creating a new user before database insertion).
     * @param nom The user's name.
     * @param email The user's email.
     * @param motDePasse The user's password.
     * @param role The user's role.
     * @param statut The user's status.
     */
    public Utilisateur(String nom, String email, String motDePasse, String role, String statut) {
        this.nom = nom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.role = role;
        this.statut = statut;
    }

    /**
     * Constructor with ID (for retrieving an existing user from the database, including password).
     * @param id The user's ID.
     * @param nom The user's name.
     * @param email The user's email.
     * @param motDePasse The user's password.
     * @param role The user's role.
     * @param statut The user's status.
     */
    public Utilisateur(int id, String nom, String email, String motDePasse, String role, String statut) {
        this.id = id;
        this.nom = nom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.role = role;
        this.statut = statut;
    }

    /**
     * Constructor with ID but without password (for retrieving users for display purposes).
     * @param id The user's ID.
     * @param nom The user's name.
     * @param email The user's email.
     * @param role The user's role.
     * @param statut The user's status.
     */
    public Utilisateur(int id, String nom, String email, String role, String statut) {
        this.id = id;
        this.nom = nom;
        this.email = email;
        this.role = role;
        this.statut = statut;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
}