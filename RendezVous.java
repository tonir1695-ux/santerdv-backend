package com.santerdv.santerdvapi;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "rendezvous")
public class RendezVous {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rendezvous")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_patient")
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "id_medecin")
    private Medecin medecin;

    @Column(name = "date_heure")
    private LocalDateTime dateHeure;

    @Column(name = "statut")
    private String statut;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    @Column(name = "annule_par_nom")
    private String annuleParNom;

    @Column(name = "annule_par_role")
    private String annuleParRole;

    @Column(name = "annule_le")
    private LocalDateTime annuleLe;

    public String getAnnuleParNom() { return annuleParNom; }
    public void setAnnuleParNom(String annuleParNom) { this.annuleParNom = annuleParNom; }

    public String getAnnuleParRole() { return annuleParRole; }
    public void setAnnuleParRole(String annuleParRole) { this.annuleParRole = annuleParRole; }

    public LocalDateTime getAnnuleLe() { return annuleLe; }
    public void setAnnuleLe(LocalDateTime annuleLe) { this.annuleLe = annuleLe; }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public Medecin getMedecin() { return medecin; }
    public void setMedecin(Medecin medecin) { this.medecin = medecin; }

    public LocalDateTime getDateHeure() { return dateHeure; }
    public void setDateHeure(LocalDateTime dateHeure) { this.dateHeure = dateHeure; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
}