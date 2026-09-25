package com.santerdv.santerdvapi;

import java.time.LocalDateTime;

public class RendezVousRequest {
    private Integer idPatient;
    private Integer idMedecin;
    private LocalDateTime dateHeure;
    private String typeConsultation = "cabinet";
    private String adresseDomicile;
    private Double latitudeDomicile;
    private Double longitudeDomicile;

    public Double getLatitudeDomicile() { return latitudeDomicile; }
    public void setLatitudeDomicile(Double latitudeDomicile) { this.latitudeDomicile = latitudeDomicile; }

    public Double getLongitudeDomicile() { return longitudeDomicile; }
    public void setLongitudeDomicile(Double longitudeDomicile) { this.longitudeDomicile = longitudeDomicile; }

    public Integer getIdPatient() { return idPatient; }
    public void setIdPatient(Integer idPatient) { this.idPatient = idPatient; }

    public Integer getIdMedecin() { return idMedecin; }
    public void setIdMedecin(Integer idMedecin) { this.idMedecin = idMedecin; }

    public LocalDateTime getDateHeure() { return dateHeure; }
    public void setDateHeure(LocalDateTime dateHeure) { this.dateHeure = dateHeure; }

    public String getTypeConsultation() { return typeConsultation; }
    public void setTypeConsultation(String typeConsultation) { this.typeConsultation = typeConsultation; }

    public String getAdresseDomicile() { return adresseDomicile; }
    public void setAdresseDomicile(String adresseDomicile) { this.adresseDomicile = adresseDomicile; }
}