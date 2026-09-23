package com.santerdv.santerdvapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/receptionnistes")
public class ReceptionnisteController {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private EmailService emailService;

    @GetMapping
    public List<Utilisateur> getAllReceptionnistes() {
        return utilisateurRepository.findByRole("receptionniste");
    }

    @PostMapping("/creer")
    public ResponseEntity<?> creerReceptionniste(@RequestBody CreerReceptionnisteRequest requete) {
        if (utilisateurRepository.findByEmail(requete.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Cet email est déjà utilisé.");
        }

        String motDePasseTemporaire = SecuriteUtil.genererMotDePasseTemporaire();

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(requete.getNom());
        utilisateur.setPrenom(requete.getPrenom());
        utilisateur.setEmail(requete.getEmail());
        utilisateur.setMotDePasse(SecuriteUtil.hacher(motDePasseTemporaire));
        utilisateur.setTelephone(requete.getTelephone());
        utilisateur.setRole("receptionniste");
        utilisateur.setLanguePreferee("fr");
        utilisateur.setActif(true);
        utilisateur.setEmailVerifie(true);
        utilisateur.setMotDePasseTemporaire(true);

        Utilisateur cree = utilisateurRepository.save(utilisateur);

        emailService.envoyerIdentifiants(requete.getEmail(), requete.getPrenom(), "réceptionniste", motDePasseTemporaire);

        return ResponseEntity.ok(cree);
    }

    @PutMapping("/{id}/modifier")
    public ResponseEntity<?> modifierReceptionniste(@PathVariable Integer id, @RequestBody CreerReceptionnisteRequest requete) {
        Optional<Utilisateur> u = utilisateurRepository.findById(id);
        if (u.isEmpty() || !"receptionniste".equals(u.get().getRole())) {
            return ResponseEntity.badRequest().body("Réceptionniste introuvable.");
        }
        Utilisateur existant = u.get();
        if (requete.getNom() != null) existant.setNom(requete.getNom());
        if (requete.getPrenom() != null) existant.setPrenom(requete.getPrenom());
        if (requete.getTelephone() != null) existant.setTelephone(requete.getTelephone());
        utilisateurRepository.save(existant);
        return ResponseEntity.ok(existant);
    }

    @PutMapping("/{id}/statut")
    public ResponseEntity<?> changerStatut(@PathVariable Integer id, @RequestBody java.util.Map<String, Boolean> body) {
        Optional<Utilisateur> u = utilisateurRepository.findById(id);
        if (u.isEmpty() || !"receptionniste".equals(u.get().getRole())) {
            return ResponseEntity.badRequest().body("Réceptionniste introuvable.");
        }
        u.get().setActif(body.get("actif"));
        utilisateurRepository.save(u.get());
        return ResponseEntity.ok(u.get());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> supprimerReceptionniste(@PathVariable Integer id) {
        Optional<Utilisateur> u = utilisateurRepository.findById(id);
        if (u.isEmpty() || !"receptionniste".equals(u.get().getRole())) {
            return ResponseEntity.badRequest().body("Réceptionniste introuvable.");
        }
        utilisateurRepository.deleteById(id);
        return ResponseEntity.ok("Réceptionniste supprimé.");
    }
}