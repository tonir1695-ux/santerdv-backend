package com.santerdv.santerdvapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tarifs")
public class TarifController {

    @Autowired
    private TarifConsultationRepository tarifConsultationRepository;

    @Autowired
    private EtablissementRepository etablissementRepository;

    @Autowired
    private SpecialiteRepository specialiteRepository;

    @Autowired
    private MedecinRepository medecinRepository;

    @GetMapping("/etablissement/{idEtablissement}")
    public List<TarifConsultation> getTarifsEtablissement(@PathVariable Integer idEtablissement) {
        return tarifConsultationRepository.findByEtablissementId(idEtablissement);
    }

    @PostMapping("/creer")
    public ResponseEntity<?> creerOuModifierTarif(@RequestBody TarifConsultationRequest requete) {
        Optional<Etablissement> etabOpt = etablissementRepository.findById(requete.getIdEtablissement());
        if (etabOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Établissement introuvable.");
        }
        Optional<Specialite> specialiteOpt = specialiteRepository.findById(requete.getIdSpecialite());
        if (specialiteOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Spécialité introuvable.");
        }
        if (requete.getMontant() == null || requete.getMontant() <= 0) {
            return ResponseEntity.badRequest().body("Montant invalide.");
        }

        TarifConsultation tarif = new TarifConsultation();
        tarif.setEtablissement(etabOpt.get());
        tarif.setSpecialite(specialiteOpt.get());
        tarif.setMontant(requete.getMontant());
        if (requete.getIdMedecin() != null) {
            medecinRepository.findById(requete.getIdMedecin()).ifPresent(tarif::setMedecin);
        }

        TarifConsultation sauvegarde = tarifConsultationRepository.save(tarif);
        return ResponseEntity.ok(sauvegarde);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> supprimerTarif(@PathVariable Integer id) {
        if (!tarifConsultationRepository.existsById(id)) {
            return ResponseEntity.badRequest().body("Tarif introuvable.");
        }
        tarifConsultationRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
