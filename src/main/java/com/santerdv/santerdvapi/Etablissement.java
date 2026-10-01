package com.santerdv.santerdvapi;

import jakarta.persistence.*;

@Entity
@Table(name = "etablissement")
public class Etablissement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_etablissement")
    private Integer id;

    @Column(name = "nom")
    private String nom;

    @Column(name = "type_etablissement")
    private String typeEtablissement; // hopital, clinique, cabinet, centre_sante

    @Column(name = "adresse")
    private String adresse;

    @Column(name = "quartier")
    private String quartier;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    public String getQuartier() { return quartier; }
    public void setQuartier(String quartier) { this.quartier = quartier; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    @Column(name = "telephone")
    private String telephone;

    @Column(name = "actif")
    private Boolean actif = true;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getTypeEtablissement() { return typeEtablissement; }
    public void setTypeEtablissement(String typeEtablissement) { this.typeEtablissement = typeEtablissement; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public Boolean getActif() { return actif; }
    public void setActif(Boolean actif) { this.actif = actif; }
}
