package com.santerdv.santerdvapi;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "rappel_sms")
public class RappelSms {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rappel")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_rendezvous")
    private RendezVous rendezVous;

    @Column(name = "date_envoi")
    private LocalDateTime dateEnvoi;

    @Column(name = "statut_envoi")
    private String statutEnvoi;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public RendezVous getRendezVous() { return rendezVous; }
    public void setRendezVous(RendezVous rendezVous) { this.rendezVous = rendezVous; }

    public LocalDateTime getDateEnvoi() { return dateEnvoi; }
    public void setDateEnvoi(LocalDateTime dateEnvoi) { this.dateEnvoi = dateEnvoi; }

    public String getStatutEnvoi() { return statutEnvoi; }
    public void setStatutEnvoi(String statutEnvoi) { this.statutEnvoi = statutEnvoi; }

    @Column(name = "message")
    private String message;

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}