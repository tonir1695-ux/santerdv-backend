package com.santerdv.santerdvapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/portefeuille")
public class PortefeuilleController {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private TransactionPortefeuilleRepository transactionRepository;

    @GetMapping("/{idPatient}")
    public ResponseEntity<?> getPortefeuille(@PathVariable Integer idPatient) {
        Optional<Patient> patientOpt = patientRepository.findById(idPatient);
        if (patientOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Patient introuvable.");
        }
        List<TransactionPortefeuille> historique = transactionRepository.findByPatientIdOrderByDateCreationDesc(idPatient);
        return ResponseEntity.ok(Map.of(
                "solde", patientOpt.get().getSoldePortefeuille(),
                "historique", historique
        ));
    }

    /** Simule une recharge Mobile Money (Flooz/TMoney) — pas de vraie intégration opérateur. */
    @PostMapping("/{idPatient}/recharger")
    public ResponseEntity<?> recharger(@PathVariable Integer idPatient, @RequestBody Map<String, Object> body) {
        Optional<Patient> patientOpt = patientRepository.findById(idPatient);
        if (patientOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Patient introuvable.");
        }

        double montant;
        try {
            montant = ((Number) body.get("montant")).doubleValue();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Montant invalide.");
        }
        if (montant <= 0) {
            return ResponseEntity.badRequest().body("Le montant doit être positif.");
        }

        String moyen = (String) body.getOrDefault("moyen", "flooz");

        Patient patient = patientOpt.get();
        double nouveauSolde = (patient.getSoldePortefeuille() != null ? patient.getSoldePortefeuille() : 0) + montant;
        patient.setSoldePortefeuille(nouveauSolde);
        patientRepository.save(patient);

        TransactionPortefeuille transaction = new TransactionPortefeuille();
        transaction.setPatient(patient);
        transaction.setType("depot");
        transaction.setMontant(montant);
        transaction.setMoyen(moyen);
        transaction.setDescription("Dépôt " + ("tmoney".equals(moyen) ? "TMoney" : "Flooz") + " (simulé)");
        transaction.setDateCreation(LocalDateTime.now());
        transactionRepository.save(transaction);

        return ResponseEntity.ok(Map.of("message", "Recharge effectuée.", "nouveauSolde", nouveauSolde));
    }
}
