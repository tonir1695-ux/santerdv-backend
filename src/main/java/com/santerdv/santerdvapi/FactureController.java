package com.santerdv.santerdvapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/factures")
public class FactureController {

    @Autowired
    private FactureRepository factureRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private RappelSmsRepository rappelSmsRepository;

    @Autowired
    private TransactionPortefeuilleRepository transactionRepository;

    @GetMapping
    public List<Facture> getToutesLesFactures(@RequestParam(required = false) Integer idEtablissement) {
        List<Facture> toutes = factureRepository.findAll();
        if (idEtablissement == null) {
            return toutes;
        }
        return toutes.stream()
                .filter(f -> f.getRendezVous() != null && f.getRendezVous().getMedecin() != null
                        && f.getRendezVous().getMedecin().getUtilisateur() != null
                        && f.getRendezVous().getMedecin().getUtilisateur().getEtablissement() != null
                        && idEtablissement.equals(f.getRendezVous().getMedecin().getUtilisateur().getEtablissement().getId()))
                .toList();
    }

    @GetMapping("/rendezvous/{idRendezVous}")
    public ResponseEntity<?> getFactureParRendezVous(@PathVariable Integer idRendezVous) {
        Optional<Facture> factureOpt = factureRepository.findByRendezVousId(idRendezVous);
        if (factureOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Pas encore de facture pour ce rendez-vous (en attente de confirmation du médecin).");
        }
        return ResponseEntity.ok(factureOpt.get());
    }

    /** Paiement via le portefeuille interne — débite le solde, marque la facture payée, notifie patient + médecin. */
    @PostMapping("/{id}/payer")
    public ResponseEntity<?> payer(@PathVariable Integer id, @RequestParam Integer idUtilisateurConnecte) {
        Optional<Facture> factureOpt = factureRepository.findById(id);
        if (factureOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Facture introuvable.");
        }
        Facture facture = factureOpt.get();

        if ("payee".equals(facture.getStatut())) {
            return ResponseEntity.badRequest().body("Cette facture est déjà payée.");
        }

        Optional<Patient> patientOpt = patientRepository.findById(idUtilisateurConnecte);
        if (patientOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Patient introuvable.");
        }
        Patient patient = patientOpt.get();

        double solde = patient.getSoldePortefeuille() != null ? patient.getSoldePortefeuille() : 0;
        if (solde < facture.getMontantTotal()) {
            return ResponseEntity.badRequest().body("Solde insuffisant. Rechargez votre portefeuille.");
        }

        double nouveauSolde = solde - facture.getMontantTotal();
        patient.setSoldePortefeuille(nouveauSolde);
        patientRepository.save(patient);

        facture.setStatut("payee");
        facture.setDatePaiement(LocalDateTime.now());
        factureRepository.save(facture);

        TransactionPortefeuille transaction = new TransactionPortefeuille();
        transaction.setPatient(patient);
        transaction.setType("paiement");
        transaction.setMontant(facture.getMontantTotal());
        transaction.setDescription("Consultation du " + (facture.getRendezVous() != null ? facture.getRendezVous().getDateHeure() : ""));
        transaction.setDateCreation(LocalDateTime.now());
        transactionRepository.save(transaction);

        // Notifications : patient (reçu), médecin (confirmation de paiement).
        if (facture.getRendezVous() != null) {
            RendezVous rdv = facture.getRendezVous();
            String nomPatient = patient.getUtilisateur() != null
                    ? patient.getUtilisateur().getPrenom() + " " + patient.getUtilisateur().getNom() : "Le patient";
            String nomMedecin = rdv.getMedecin() != null && rdv.getMedecin().getUtilisateur() != null
                    ? "Dr " + rdv.getMedecin().getUtilisateur().getPrenom() + " " + rdv.getMedecin().getUtilisateur().getNom() : "votre médecin";

            if (patient.getUtilisateur() != null && patient.getUtilisateur().getEmail() != null) {
                emailService.envoyerConfirmationPaiement(patient.getUtilisateur().getEmail(),
                        patient.getUtilisateur().getPrenom(), nomMedecin, facture.getMontantTotal(), true);

                RappelSms recuPatient = new RappelSms();
                recuPatient.setRendezVous(rdv);
                recuPatient.setDateEnvoi(LocalDateTime.now());
                recuPatient.setStatutEnvoi("envoye");
                recuPatient.setMessage("💳 Paiement effectué : " + facture.getMontantTotal().intValue() + " FCFA pour votre consultation avec " + nomMedecin + ".");
                rappelSmsRepository.save(recuPatient);
            }
            if (rdv.getMedecin() != null && rdv.getMedecin().getUtilisateur() != null && rdv.getMedecin().getUtilisateur().getEmail() != null) {
                emailService.envoyerConfirmationPaiement(rdv.getMedecin().getUtilisateur().getEmail(),
                        rdv.getMedecin().getUtilisateur().getPrenom(), nomPatient, facture.getMontantTotal(), false);
            }
        }

        return ResponseEntity.ok(Map.of(
                "message", "Paiement effectué.",
                "nouveauSolde", nouveauSolde,
                "facture", facture
        ));
    }
}
