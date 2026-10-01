package com.santerdv.santerdvapi;

public class CreerEtablissementAvecAdminRequest {
    private String nomEtablissement;
    private String typeEtablissement;
    private String adresse;
    private String quartier;
    private Double latitude;
    private Double longitude;
    private String telephoneEtablissement;

    public String getQuartier() { return quartier; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }

    private String nomAdmin;
    private String prenomAdmin;
    private String emailAdmin;
    private String telephoneAdmin;

    public String getNomEtablissement() { return nomEtablissement; }
    public String getTypeEtablissement() { return typeEtablissement; }
    public String getAdresse() { return adresse; }
    public String getTelephoneEtablissement() { return telephoneEtablissement; }
    public String getNomAdmin() { return nomAdmin; }
    public String getPrenomAdmin() { return prenomAdmin; }
    public String getEmailAdmin() { return emailAdmin; }
    public String getTelephoneAdmin() { return telephoneAdmin; }
}
