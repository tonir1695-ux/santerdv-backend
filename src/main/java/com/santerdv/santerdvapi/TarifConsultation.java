package com.santerdv.santerdvapi;

import jakarta.persistence.*;

@Entity
@Table(name = "tarif_consultation")
public class TarifConsultation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tarif")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_etablissement")
    private Etablissement etablissement;

    @ManyToOne
    @JoinColumn(name = "id_specialite")
    private Specialite specialite;

    // Optionnel : si renseigné, ce tarif prime sur le tarif établissement+spécialité.
    @ManyToOne
    @JoinColumn(name = "id_medecin")
    private Medecin medecin;

    @Column(name = "montant")
    private Double montant;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Etablissement getEtablissement() { return etablissement; }
    public void setEtablissement(Etablissement etablissement) { this.etablissement = etablissement; }

    public Specialite getSpecialite() { return specialite; }
    public void setSpecialite(Specialite specialite) { this.specialite = specialite; }

    public Medecin getMedecin() { return medecin; }
    public void setMedecin(Medecin medecin) { this.medecin = medecin; }

    public Double getMontant() { return montant; }
    public void setMontant(Double montant) { this.montant = montant; }
}
