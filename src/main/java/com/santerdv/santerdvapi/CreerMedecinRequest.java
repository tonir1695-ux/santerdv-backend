package com.santerdv.santerdvapi;

public class CreerMedecinRequest {
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private Integer idSpecialite;
    private String disponibilites;

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public Integer getIdSpecialite() { return idSpecialite; }
    public void setIdSpecialite(Integer idSpecialite) { this.idSpecialite = idSpecialite; }

    public String getDisponibilites() { return disponibilites; }
    public void setDisponibilites(String disponibilites) { this.disponibilites = disponibilites; }
}