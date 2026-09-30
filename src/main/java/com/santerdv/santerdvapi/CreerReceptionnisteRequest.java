package com.santerdv.santerdvapi;

public class CreerReceptionnisteRequest {
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private Integer idEtablissement;

    public Integer getIdEtablissement() { return idEtablissement; }
    public void setIdEtablissement(Integer idEtablissement) { this.idEtablissement = idEtablissement; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
}