package com.santerdv.santerdvapi;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "facture")
public class Facture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_facture")
    private Integer id;

    @OneToOne
    @JoinColumn(name = "id_rendezvous")
    private RendezVous rendezVous;

    @Column(name = "montant_consultation")
    private Double montantConsultation;

    @Column(name = "montant_deplacement")
    private Double montantDeplacement = 0.0;

    @Column(name = "montant_carnet")
    private Double montantCarnet = 0.0;

    @Column(name = "montant_service")
    private Double montantService = 0.0;

    @Column(name = "montant_total")
    private Double montantTotal;

    @Column(name = "statut")
    private String statut = "en_attente"; // en_attente, payee

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    @Column(name = "date_paiement")
    private LocalDateTime datePaiement;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public RendezVous getRendezVous() { return rendezVous; }
    public void setRendezVous(RendezVous rendezVous) { this.rendezVous = rendezVous; }

    public Double getMontantConsultation() { return montantConsultation; }
    public void setMontantConsultation(Double montantConsultation) { this.montantConsultation = montantConsultation; }

    public Double getMontantDeplacement() { return montantDeplacement; }
    public void setMontantDeplacement(Double montantDeplacement) { this.montantDeplacement = montantDeplacement; }

    public Double getMontantCarnet() { return montantCarnet; }
    public void setMontantCarnet(Double montantCarnet) { this.montantCarnet = montantCarnet; }

    public Double getMontantService() { return montantService; }
    public void setMontantService(Double montantService) { this.montantService = montantService; }

    public Double getMontantTotal() { return montantTotal; }
    public void setMontantTotal(Double montantTotal) { this.montantTotal = montantTotal; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public LocalDateTime getDatePaiement() { return datePaiement; }
    public void setDatePaiement(LocalDateTime datePaiement) { this.datePaiement = datePaiement; }
}
