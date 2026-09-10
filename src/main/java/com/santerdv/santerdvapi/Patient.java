package com.santerdv.santerdvapi;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "patient")
public class Patient {

    @Id
    @Column(name = "id_patient")
    private Integer id;

    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    @Column(name = "adresse")
    private String adresse;

    @OneToOne
    @JoinColumn(name = "id_patient", referencedColumnName = "id_utilisateur", insertable = false, updatable = false)
    private Utilisateur utilisateur;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }
}