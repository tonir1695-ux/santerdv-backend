package com.santerdv.santerdvapi;

public class TarifConsultationRequest {
    private Integer idEtablissement;
    private Integer idSpecialite;
    private Integer idMedecin;
    private Double montant;

    public Integer getIdEtablissement() { return idEtablissement; }
    public Integer getIdSpecialite() { return idSpecialite; }
    public Integer getIdMedecin() { return idMedecin; }
    public Double getMontant() { return montant; }
}
