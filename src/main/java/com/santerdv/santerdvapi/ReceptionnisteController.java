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

    @GetMapping
    public List<Utilisateur> getAllReceptionnistes() {
        return utilisateurRepository.findByRole("receptionniste");
    }

    @PostMapping("/creer")
    public ResponseEntity<?> creerReceptionniste(@RequestBody CreerReceptionnisteRequest requete) {
        if (utilisateurRepository.findByEmail(requete.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Cet email est déjà utilisé.");
        }

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(requete.getNom());
        utilisateur.setPrenom(requete.getPrenom());
        utilisateur.setEmail(requete.getEmail());
        utilisateur.setMotDePasse(requete.getMotDePasse());
        utilisateur.setTelephone(requete.getTelephone());
        utilisateur.setRole("receptionniste");
        utilisateur.setLanguePreferee("fr");
        utilisateur.setActif(true);

        Utilisateur cree = utilisateurRepository.save(utilisateur);
        return ResponseEntity.ok(cree);
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