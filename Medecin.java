package com.santerdv.santerdvapi;

import jakarta.persistence.*;

@Entity
@Table(name = "medecin")
public class Medecin {

    @Id
    @Column(name = "id_medecin")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_specialite")
    private Specialite specialite;

    @Column(name = "disponibilites")
    private String disponibilites;

    @OneToOne
    @JoinColumn(name = "id_medecin", referencedColumnName = "id_utilisateur", insertable = false, updatable = false)
    private Utilisateur utilisateur;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Specialite getSpecialite() { return specialite; }
    public void setSpecialite(Specialite specialite) { this.specialite = specialite; }

    public String getDisponibilites() { return disponibilites; }
    public void setDisponibilites(String disponibilites) { this.disponibilites = disponibilites; }

    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }

    @Column(name = "actif")
    private Boolean actif;

    public Boolean getActif() { return actif; }
    public void setActif(Boolean actif) { this.actif = actif; }
}