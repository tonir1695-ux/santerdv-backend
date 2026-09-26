package com.santerdv.santerdvapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/rappels")
public class RappelSmsController {

    @Autowired
    private RappelSmsRepository rappelSmsRepository;

    @Autowired
    private RendezVousRepository rendezVousRepository;

    @Autowired
    private SmsService smsService;

    @GetMapping
    public List<RappelSms> getAllRappels() {
        return rappelSmsRepository.findAll();
    }

    @PostMapping("/envoyer/{idRendezVous}")
    public ResponseEntity<?> envoyerRappel(@PathVariable Integer idRendezVous) {
        Optional<RendezVous> rdvOpt = rendezVousRepository.findById(idRendezVous);

        if (rdvOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Rendez-vous introuvable.");
        }

        RendezVous rdv = rdvOpt.get();
        String telephone = rdv.getPatient().getId() != null ? "numéro du patient" : "inconnu";

        String message = "Rappel : vous avez un rendez-vous le "
                + rdv.getDateHeure() + " avec le médecin.";

        boolean envoye = smsService.envoyerSms(telephone, message);

        RappelSms rappel = new RappelSms();
        rappel.setRendezVous(rdv);
        rappel.setDateEnvoi(LocalDateTime.now());
        rappel.setStatutEnvoi(envoye ? "envoye" : "echoue");

        RappelSms nouveauRappel = rappelSmsRepository.save(rappel);
        return ResponseEntity.ok(nouveauRappel);
    }
    @GetMapping("/patient/{idPatient}")
    public List<RappelSms> getRappelsByPatient(@PathVariable Integer idPatient) {
        return rappelSmsRepository.findAll()
                .stream()
                .filter(r -> r.getRendezVous().getPatient().getId().equals(idPatient))
                .sorted((a, b) -> {
                    if (a.getDateEnvoi() == null) return 1;
                    if (b.getDateEnvoi() == null) return -1;
                    return b.getDateEnvoi().compareTo(a.getDateEnvoi());
                })
                .toList();
    }
}