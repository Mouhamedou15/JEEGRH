package models;

public class Departement {
    private int id;
    private String nom;

    public Departement(int id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    // ✅ Getters et Setters
    public int getId() { return id; }
    public String getNom() { return nom; }
}
