package com.santerdv.santerdvapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/utilisateurs")
public class UtilisateurController {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @GetMapping
    public List<Utilisateur> getAllUtilisateurs() {
        return utilisateurRepository.findAll();
    }

    @GetMapping("/recherche")
    public List<Utilisateur> rechercherUtilisateurs(@RequestParam String query, @RequestParam(required = false) String role) {
        return utilisateurRepository.findAll().stream()
                .filter(u -> role == null || role.equals(u.getRole()))
                .filter(u ->
                        (u.getNom() != null && u.getNom().toLowerCase().contains(query.toLowerCase())) ||
                                (u.getPrenom() != null && u.getPrenom().toLowerCase().contains(query.toLowerCase())) ||
                                (u.getEmail() != null && u.getEmail().toLowerCase().contains(query.toLowerCase())) ||
                                (u.getTelephone() != null && u.getTelephone().contains(query))
                )
                .toList();
    }

    @Autowired
    private PatientRepository patientRepository;

    @PostMapping("/inscription")
    public ResponseEntity<?> inscription(@RequestBody Utilisateur utilisateur) {
        if (utilisateurRepository.findByEmail(utilisateur.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Cet email est déjà utilisé.");
        }

        utilisateur.setActif(true);
        Utilisateur nouvel = utilisateurRepository.save(utilisateur);

        // Si c'est un patient, on crée aussi sa ligne dans la table patient
        if ("patient".equals(nouvel.getRole())) {
            Patient patient = new Patient();
            patient.setId(nouvel.getId());
            patientRepository.save(patient);
        }

        return ResponseEntity.ok(nouvel);
    }
    @PostMapping("/connexion")
    public ResponseEntity<?> connexion(@RequestBody Utilisateur utilisateur) {
        Optional<Utilisateur> trouve = utilisateurRepository.findByEmail(utilisateur.getEmail());
        if (trouve.isPresent() && trouve.get().getMotDePasse().equals(utilisateur.getMotDePasse())) {
            return ResponseEntity.ok(trouve.get());
        }
        return ResponseEntity.status(401).body("Email ou mot de passe incorrect.");
    }
}