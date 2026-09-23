package com.santerdv.santerdvapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/utilisateurs")
public class UtilisateurController {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private EmailService emailService;

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

    /**
     * Inscription d'un patient. Le compte est créé INACTIF/NON VÉRIFIÉ :
     * un code OTP est envoyé par email et doit être validé via /verifier-otp
     * avant que la connexion ne soit possible.
     */
    @PostMapping("/inscription")
    public ResponseEntity<?> inscription(@RequestBody Utilisateur utilisateur) {
        if (utilisateurRepository.findByEmail(utilisateur.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Cet email est déjà utilisé.");
        }
        if (utilisateur.getEmail() == null || !utilisateur.getEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            return ResponseEntity.badRequest().body("Adresse email invalide.");
        }

        utilisateur.setMotDePasse(SecuriteUtil.hacher(utilisateur.getMotDePasse()));
        utilisateur.setActif(true);
        utilisateur.setEmailVerifie(false);
        utilisateur.setMotDePasseTemporaire(false);

        String code = SecuriteUtil.genererCodeOtp();
        utilisateur.setCodeOtp(code);
        utilisateur.setDateExpirationOtp(LocalDateTime.now().plusMinutes(15));

        Utilisateur nouvel = utilisateurRepository.save(utilisateur);

        if ("patient".equals(nouvel.getRole())) {
            Patient patient = new Patient();
            patient.setId(nouvel.getId());
            patientRepository.save(patient);
        }

        emailService.envoyerCodeOtp(nouvel.getEmail(), nouvel.getPrenom(), code);

        return ResponseEntity.ok(Map.of(
                "message", "Compte créé. Un code de vérification a été envoyé par email.",
                "id", nouvel.getId(),
                "email", nouvel.getEmail()
        ));
    }

    /** Vérification du code OTP reçu par email pour activer définitivement le compte. */
    @PostMapping("/verifier-otp")
    public ResponseEntity<?> verifierOtp(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String code = body.get("code");

        Optional<Utilisateur> trouve = utilisateurRepository.findByEmail(email);
        if (trouve.isEmpty()) {
            return ResponseEntity.badRequest().body("Compte introuvable.");
        }

        Utilisateur u = trouve.get();
        if (Boolean.TRUE.equals(u.getEmailVerifie())) {
            return ResponseEntity.ok(Map.of("message", "Compte déjà vérifié."));
        }
        if (u.getCodeOtp() == null || !u.getCodeOtp().equals(code)) {
            return ResponseEntity.badRequest().body("Code incorrect.");
        }
        if (u.getDateExpirationOtp() == null || LocalDateTime.now().isAfter(u.getDateExpirationOtp())) {
            return ResponseEntity.badRequest().body("Code expiré, merci de demander un nouveau code.");
        }

        u.setEmailVerifie(true);
        u.setCodeOtp(null);
        u.setDateExpirationOtp(null);
        utilisateurRepository.save(u);

        return ResponseEntity.ok(Map.of("message", "Compte vérifié avec succès.", "utilisateur", u));
    }

    /** Renvoie un nouveau code si l'utilisateur n'a pas reçu ou a laissé expirer le précédent. */
    @PostMapping("/renvoyer-otp")
    public ResponseEntity<?> renvoyerOtp(@RequestBody Map<String, String> body) {
        Optional<Utilisateur> trouve = utilisateurRepository.findByEmail(body.get("email"));
        if (trouve.isEmpty()) {
            return ResponseEntity.badRequest().body("Compte introuvable.");
        }
        Utilisateur u = trouve.get();
        if (Boolean.TRUE.equals(u.getEmailVerifie())) {
            return ResponseEntity.ok(Map.of("message", "Compte déjà vérifié."));
        }

        String code = SecuriteUtil.genererCodeOtp();
        u.setCodeOtp(code);
        u.setDateExpirationOtp(LocalDateTime.now().plusMinutes(15));
        utilisateurRepository.save(u);

        emailService.envoyerCodeOtp(u.getEmail(), u.getPrenom(), code);
        return ResponseEntity.ok(Map.of("message", "Nouveau code envoyé."));
    }

    @PostMapping("/connexion")
    public ResponseEntity<?> connexion(@RequestBody Utilisateur utilisateur) {
        Optional<Utilisateur> trouve = utilisateurRepository.findByEmail(utilisateur.getEmail());
        if (trouve.isEmpty() || !SecuriteUtil.verifier(utilisateur.getMotDePasse(), trouve.get().getMotDePasse())) {
            return ResponseEntity.status(401).body("Email ou mot de passe incorrect.");
        }

        Utilisateur u = trouve.get();
        if (Boolean.FALSE.equals(u.getEmailVerifie())) {
            return ResponseEntity.status(403).body("Compte non vérifié. Merci de valider le code reçu par email.");
        }
        if (Boolean.FALSE.equals(u.getActif())) {
            return ResponseEntity.status(403).body("Ce compte a été désactivé. Contactez l'administration.");
        }

        return ResponseEntity.ok(u);
    }

    /** Changement de mot de passe (utilisé notamment après une première connexion avec mot de passe temporaire). */
    @PutMapping("/{id}/mot-de-passe")
    public ResponseEntity<?> changerMotDePasse(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        Optional<Utilisateur> trouve = utilisateurRepository.findById(id);
        if (trouve.isEmpty()) {
            return ResponseEntity.badRequest().body("Utilisateur introuvable.");
        }
        Utilisateur u = trouve.get();
        if (!SecuriteUtil.verifier(body.get("ancienMotDePasse"), u.getMotDePasse())) {
            return ResponseEntity.status(401).body("Ancien mot de passe incorrect.");
        }
        u.setMotDePasse(SecuriteUtil.hacher(body.get("nouveauMotDePasse")));
        u.setMotDePasseTemporaire(false);
        utilisateurRepository.save(u);
        return ResponseEntity.ok(Map.of("message", "Mot de passe modifié."));
    }

    /** Utilisé par l'admin pour désactiver/réactiver n'importe quel compte (médecin, réceptionniste). */
    @PutMapping("/admin/{id}/statut")
    public ResponseEntity<?> changerStatutCompte(@PathVariable Integer id, @RequestBody Map<String, Boolean> body) {
        Optional<Utilisateur> trouve = utilisateurRepository.findById(id);
        if (trouve.isEmpty()) {
            return ResponseEntity.badRequest().body("Utilisateur introuvable.");
        }
        Utilisateur u = trouve.get();
        u.setActif(body.get("actif"));
        utilisateurRepository.save(u);
        return ResponseEntity.ok(u);
    }
}
