package com.santerdv.santerdvapi;

import java.time.LocalDateTime;

public class RendezVousRequest {
    private Integer idPatient;
    private Integer idMedecin;
    private LocalDateTime dateHeure;

    public Integer getIdPatient() { return idPatient; }
    public void setIdPatient(Integer idPatient) { this.idPatient = idPatient; }

    public Integer getIdMedecin() { return idMedecin; }
    public void setIdMedecin(Integer idMedecin) { this.idMedecin = idMedecin; }

    public LocalDateTime getDateHeure() { return dateHeure; }
    public void setDateHeure(LocalDateTime dateHeure) { this.dateHeure = dateHeure; }
}