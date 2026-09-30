package com.santerdv.santerdvapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Gestion des établissements — réservée au Super Admin (contrôle fait côté
 * mobile via le rôle stocké à la connexion ; les endpoints restent accessibles
 * à tout compte admin authentifié pour rester simples dans cette version).
 */
@RestController
@RequestMapping("/api/etablissements")
public class EtablissementController {

    @Autowired
    private EtablissementRepository etablissementRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private EmailService emailService;

    @GetMapping
    public List<Etablissement> getTousLesEtablissements() {
        return etablissementRepository.findAll();
    }

    /**
     * Crée un établissement ET son administrateur en une seule opération
     * (comme un vrai admin d'établissement, il reçoit ses identifiants par email).
     */
    @PostMapping("/creer-avec-admin")
    public ResponseEntity<?> creerEtablissementAvecAdmin(@RequestBody CreerEtablissementAvecAdminRequest requete) {
        if (!ValidationUtil.estNomValide(requete.getNomAdmin()) || !ValidationUtil.estNomValide(requete.getPrenomAdmin())) {
            return ResponseEntity.badRequest().body("Le nom et le prénom de l'administrateur ne doivent contenir que des lettres.");
        }
        if (!ValidationUtil.estEmailValide(requete.getEmailAdmin())) {
            return ResponseEntity.badRequest().body("Adresse email invalide.");
        }
        if (!ValidationUtil.estTelephoneValide(requete.getTelephoneAdmin())) {
            return ResponseEntity.badRequest().body("Numéro de téléphone de l'admin invalide.");
        }
        if (requete.getNomEtablissement() == null || requete.getNomEtablissement().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Le nom de l'établissement est obligatoire.");
        }
        if (utilisateurRepository.findByEmail(requete.getEmailAdmin()).isPresent()) {
            return ResponseEntity.badRequest().body("Cet email est déjà utilisé.");
        }

        Etablissement etablissement = new Etablissement();
        etablissement.setNom(requete.getNomEtablissement().trim());
        etablissement.setTypeEtablissement(requete.getTypeEtablissement());
        etablissement.setAdresse(requete.getAdresse());
        etablissement.setTelephone(requete.getTelephoneEtablissement());
        etablissement.setActif(true);
        Etablissement etablissementCree = etablissementRepository.save(etablissement);

        String motDePasseTemporaire = SecuriteUtil.genererMotDePasseTemporaire();

        Utilisateur admin = new Utilisateur();
        admin.setNom(requete.getNomAdmin());
        admin.setPrenom(requete.getPrenomAdmin());
        admin.setEmail(requete.getEmailAdmin());
        admin.setMotDePasse(SecuriteUtil.hacher(motDePasseTemporaire));
        admin.setTelephone(requete.getTelephoneAdmin());
        admin.setRole("admin");
        admin.setLanguePreferee("fr");
        admin.setActif(true);
        admin.setEmailVerifie(true);
        admin.setMotDePasseTemporaire(true);
        admin.setEtablissement(etablissementCree);
        utilisateurRepository.save(admin);

        emailService.envoyerIdentifiants(requete.getEmailAdmin(), requete.getPrenomAdmin(),
                "administrateur de " + etablissementCree.getNom(), motDePasseTemporaire);

        return ResponseEntity.ok(etablissementCree);
    }

    @PutMapping("/{id}/modifier")
    public ResponseEntity<?> modifierEtablissement(@PathVariable Integer id, @RequestBody Etablissement donnees) {
        Optional<Etablissement> etabOpt = etablissementRepository.findById(id);
        if (etabOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Établissement introuvable.");
        }
        Etablissement etab = etabOpt.get();
        if (donnees.getNom() != null) etab.setNom(donnees.getNom());
        if (donnees.getTypeEtablissement() != null) etab.setTypeEtablissement(donnees.getTypeEtablissement());
        if (donnees.getAdresse() != null) etab.setAdresse(donnees.getAdresse());
        if (donnees.getTelephone() != null) etab.setTelephone(donnees.getTelephone());
        etablissementRepository.save(etab);
        return ResponseEntity.ok(etab);
    }

    @PutMapping("/{id}/statut")
    public ResponseEntity<?> changerStatutEtablissement(@PathVariable Integer id, @RequestBody Map<String, Boolean> body) {
        Optional<Etablissement> etabOpt = etablissementRepository.findById(id);
        if (etabOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Établissement introuvable.");
        }
        Etablissement etab = etabOpt.get();
        etab.setActif(body.get("actif"));
        etablissementRepository.save(etab);
        return ResponseEntity.ok(etab);
    }
}
