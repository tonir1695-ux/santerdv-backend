package com.santerdv.santerdvapi;

import jakarta.persistence.*;

@Entity
@Table(name = "utilisateur")
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_utilisateur")
    private Integer id;

    @Column(name = "nom")
    private String nom;

    @Column(name = "prenom")
    private String prenom;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "mot_de_passe")
    private String motDePasse;

    @Column(name = "telephone")
    private String telephone;

    @Column(name = "role")
    private String role;

    @Column(name = "langue_preferee")
    private String languePreferee;

    @Column(name = "actif")
    private Boolean actif;

    public Boolean getActif() { return actif; }
    public void setActif(Boolean actif) { this.actif = actif; }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getLanguePreferee() { return languePreferee; }
    public void setLanguePreferee(String languePreferee) { this.languePreferee = languePreferee; }
}