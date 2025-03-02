package models;

public class Employe {
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private String role;
    private Departement departement; // Association avec un département

    public Employe(int id, String nom, String prenom, String email, String motDePasse, String role, Departement departement) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.role = role;
        this.departement = departement;
    }

    // ✅ Getters et Setters
    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getEmail() { return email; }
    public String getMotDePasse() { return motDePasse; }
    public String getRole() { return role; }
    public Departement getDepartement() { return departement; }

    public void setDepartement(Departement departement) {
        this.departement = departement;
    }
}
