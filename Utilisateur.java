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

    @Column(name = "email_verifie")
    private Boolean emailVerifie = false;

    @Column(name = "code_otp")
    private String codeOtp;

    @Column(name = "date_expiration_otp")
    private java.time.LocalDateTime dateExpirationOtp;

    @Column(name = "mot_de_passe_temporaire")
    private Boolean motDePasseTemporaire = false;

    public Boolean getActif() { return actif; }
    public void setActif(Boolean actif) { this.actif = actif; }

    public Boolean getEmailVerifie() { return emailVerifie; }
    public void setEmailVerifie(Boolean emailVerifie) { this.emailVerifie = emailVerifie; }

    public String getCodeOtp() { return codeOtp; }
    public void setCodeOtp(String codeOtp) { this.codeOtp = codeOtp; }

    public java.time.LocalDateTime getDateExpirationOtp() { return dateExpirationOtp; }
    public void setDateExpirationOtp(java.time.LocalDateTime dateExpirationOtp) { this.dateExpirationOtp = dateExpirationOtp; }

    public Boolean getMotDePasseTemporaire() { return motDePasseTemporaire; }
    public void setMotDePasseTemporaire(Boolean motDePasseTemporaire) { this.motDePasseTemporaire = motDePasseTemporaire; }

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