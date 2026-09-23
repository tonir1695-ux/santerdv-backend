package com.santerdv.santerdvapi;

import jakarta.persistence.*;

@Entity
@Table(name = "specialite")
public class Specialite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_specialite")
    private Integer id;

    @Column(name = "nom_specialite")
    private String nomSpecialite;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNomSpecialite() {
        return nomSpecialite;
    }

    public void setNomSpecialite(String nomSpecialite) {
        this.nomSpecialite = nomSpecialite;
    }

    @Column(name = "duree_consultation")
    private Integer dureeConsultation;

    public Integer getDureeConsultation() {
        return dureeConsultation;
    }

    public void setDureeConsultation(Integer dureeConsultation) {
        this.dureeConsultation = dureeConsultation;
    }
}