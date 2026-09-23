package com.santerdv.santerdvapi;

public class MedecinDisponibiliteReponse {
    private Integer id;
    private Specialite specialite;
    private Utilisateur utilisateur;
    private boolean disponibleAujourdhui;
    private String texteDisponibilite;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Specialite getSpecialite() { return specialite; }
    public void setSpecialite(Specialite specialite) { this.specialite = specialite; }

    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }

    public boolean isDisponibleAujourdhui() { return disponibleAujourdhui; }
    public void setDisponibleAujourdhui(boolean disponibleAujourdhui) { this.disponibleAujourdhui = disponibleAujourdhui; }

    public String getTexteDisponibilite() { return texteDisponibilite; }
    public void setTexteDisponibilite(String texteDisponibilite) { this.texteDisponibilite = texteDisponibilite; }
}