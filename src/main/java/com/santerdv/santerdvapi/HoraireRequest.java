package com.santerdv.santerdvapi;

public class HoraireRequest {
    private Integer jourSemaine;
    private String heureDebut;
    private String heureFin;

    public Integer getJourSemaine() { return jourSemaine; }
    public void setJourSemaine(Integer jourSemaine) { this.jourSemaine = jourSemaine; }

    public String getHeureDebut() { return heureDebut; }
    public void setHeureDebut(String heureDebut) { this.heureDebut = heureDebut; }

    public String getHeureFin() { return heureFin; }
    public void setHeureFin(String heureFin) { this.heureFin = heureFin; }
}