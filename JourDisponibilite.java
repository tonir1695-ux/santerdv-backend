package com.santerdv.santerdvapi;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "jour_disponibilite")
public class JourDisponibilite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_disponibilite")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_medecin")
    private Medecin medecin;

    @Column(name = "jour_semaine")
    private Integer jourSemaine; // 1=Lundi ... 7=Dimanche

    @Column(name = "heure_debut")
    private LocalTime heureDebut;

    @Column(name = "heure_fin")
    private LocalTime heureFin;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Medecin getMedecin() { return medecin; }
    public void setMedecin(Medecin medecin) { this.medecin = medecin; }

    public Integer getJourSemaine() { return jourSemaine; }
    public void setJourSemaine(Integer jourSemaine) { this.jourSemaine = jourSemaine; }

    public LocalTime getHeureDebut() { return heureDebut; }
    public void setHeureDebut(LocalTime heureDebut) { this.heureDebut = heureDebut; }

    public LocalTime getHeureFin() { return heureFin; }
    public void setHeureFin(LocalTime heureFin) { this.heureFin = heureFin; }
}