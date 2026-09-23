package com.santerdv.santerdvapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/medecins")
public class MedecinController {

    @Autowired
    private MedecinRepository medecinRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private SpecialiteRepository specialiteRepository;

    @Autowired
    private JourDisponibiliteRepository jourDisponibiliteRepository;

    @Autowired
    private RendezVousRepository rendezVousRepository;

    @Autowired
    private RappelSmsRepository rappelSmsRepository;

    @Autowired
    private EmailService emailService;

    @GetMapping
    public List<Medecin> getAllMedecins() {
        return medecinRepository.findAll();
    }

    @PostMapping("/creer")
    public ResponseEntity<?> creerMedecin(@RequestBody CreerMedecinRequest requete) {

        Optional<Specialite> specialite = specialiteRepository.findById(requete.getIdSpecialite());
        if (specialite.isEmpty()) {
            return ResponseEntity.badRequest().body("Spécialité introuvable.");
        }

        if (utilisateurRepository.findByEmail(requete.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Cet email est déjà utilisé.");
        }

        // Le mot de passe n'est jamais fourni par l'admin : on le génère,
        // on le hache pour le stockage, et on envoie la version en clair par email.
        String motDePasseTemporaire = SecuriteUtil.genererMotDePasseTemporaire();

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(requete.getNom());
        utilisateur.setPrenom(requete.getPrenom());
        utilisateur.setEmail(requete.getEmail());
        utilisateur.setMotDePasse(SecuriteUtil.hacher(motDePasseTemporaire));
        utilisateur.setTelephone(requete.getTelephone());
        utilisateur.setRole("medecin");
        utilisateur.setLanguePreferee("fr");
        utilisateur.setActif(true);
        utilisateur.setEmailVerifie(true); // compte créé par l'admin : pas besoin d'OTP
        utilisateur.setMotDePasseTemporaire(true);

        Utilisateur utilisateurCree = utilisateurRepository.save(utilisateur);

        Medecin medecin = new Medecin();
        medecin.setId(utilisateurCree.getId());
        medecin.setSpecialite(specialite.get());
        medecin.setDisponibilites(requete.getDisponibilites());
        medecin.setActif(true);

        Medecin medecinCree = medecinRepository.save(medecin);

        emailService.envoyerIdentifiants(requete.getEmail(), requete.getPrenom(), "médecin", motDePasseTemporaire);

        return ResponseEntity.ok(medecinCree);
    }

    @PutMapping("/{id}/modifier")
    public ResponseEntity<?> modifierMedecin(@PathVariable Integer id, @RequestBody CreerMedecinRequest requete) {
        Optional<Medecin> medecinOpt = medecinRepository.findById(id);
        if (medecinOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Médecin introuvable.");
        }
        Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findById(id);
        if (utilisateurOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Utilisateur introuvable.");
        }

        Utilisateur u = utilisateurOpt.get();
        if (requete.getNom() != null) u.setNom(requete.getNom());
        if (requete.getPrenom() != null) u.setPrenom(requete.getPrenom());
        if (requete.getTelephone() != null) u.setTelephone(requete.getTelephone());
        utilisateurRepository.save(u);

        Medecin medecin = medecinOpt.get();
        if (requete.getIdSpecialite() != null) {
            Optional<Specialite> specialite = specialiteRepository.findById(requete.getIdSpecialite());
            specialite.ifPresent(medecin::setSpecialite);
        }
        medecinRepository.save(medecin);

        return ResponseEntity.ok(medecin);
    }

    @PutMapping("/{id}/statut")
    public ResponseEntity<?> changerStatut(@PathVariable Integer id, @RequestBody java.util.Map<String, Boolean> body) {
        Optional<Medecin> medecin = medecinRepository.findById(id);
        if (medecin.isEmpty()) {
            return ResponseEntity.badRequest().body("Médecin introuvable.");
        }

        boolean nouveauStatut = body.get("actif");
        medecin.get().setActif(nouveauStatut);
        medecinRepository.save(medecin.get());

        // Si on rend le médecin indisponible, on annule ses RDV à venir et on prévient les patients
        if (!nouveauStatut) {
            java.time.LocalDateTime maintenant = java.time.LocalDateTime.now();
            List<RendezVous> rdvAVenir = rendezVousRepository.findByMedecinId(id).stream()
                    .filter(r -> r.getDateHeure().isAfter(maintenant))
                    .filter(r -> !"annule".equals(r.getStatut()))
                    .toList();

            String nomMedecin = medecin.get().getUtilisateur() != null
                    ? "Dr " + medecin.get().getUtilisateur().getPrenom() + " " + medecin.get().getUtilisateur().getNom()
                    : "votre médecin";

            for (RendezVous r : rdvAVenir) {
                r.setStatut("annule");
                rendezVousRepository.save(r);

                String dateRdvTexte = r.getDateHeure().toString().replace("T", " à ");
                RappelSms notif = new RappelSms();
                notif.setRendezVous(r);
                notif.setDateEnvoi(java.time.LocalDateTime.now());
                notif.setStatutEnvoi("envoye");
                notif.setMessage("⚠ Votre rendez-vous du " + dateRdvTexte + " avec " + nomMedecin + " a été annulé (médecin indisponible). Merci de reprendre un rendez-vous.");
                rappelSmsRepository.save(notif);
            }
        }

        return ResponseEntity.ok(medecin.get());
    }

    @GetMapping("/disponibles")
    public List<MedecinDisponibiliteReponse> getMedecinsDisponibles() {
        int jourSemaineAujourdhui = java.time.LocalDate.now().getDayOfWeek().getValue();

        return medecinRepository.findAll().stream()
                .filter(m -> m.getActif() == null || m.getActif())
                .map(m -> {
                    MedecinDisponibiliteReponse r = new MedecinDisponibiliteReponse();
                    r.setId(m.getId());
                    r.setSpecialite(m.getSpecialite());
                    r.setUtilisateur(m.getUtilisateur());

                    List<JourDisponibilite> horairesAujourdhui = jourDisponibiliteRepository.findByMedecinId(m.getId())
                            .stream()
                            .filter(h -> h.getJourSemaine().equals(jourSemaineAujourdhui))
                            .toList();

                    if (!horairesAujourdhui.isEmpty()) {
                        r.setDisponibleAujourdhui(true);
                        String plages = horairesAujourdhui.stream()
                                .map(h -> h.getHeureDebut() + " - " + h.getHeureFin())
                                .reduce((a, b) -> a + ", " + b).orElse("");
                        r.setTexteDisponibilite("Disponible aujourd'hui de " + plages);
                    } else {
                        r.setDisponibleAujourdhui(false);
                        r.setTexteDisponibilite("Indisponible aujourd'hui");
                    }

                    return r;
                })
                .toList();
    }

    @GetMapping("/{id}/horaires")
    public List<JourDisponibilite> getHoraires(@PathVariable Integer id) {
        return jourDisponibiliteRepository.findByMedecinId(id);
    }

    @PostMapping("/{id}/horaires")
    @org.springframework.transaction.annotation.Transactional
    public ResponseEntity<?> definirHoraires(@PathVariable Integer id, @RequestBody List<HoraireRequest> horaires) {
        Optional<Medecin> medecin = medecinRepository.findById(id);
        if (medecin.isEmpty()) {
            return ResponseEntity.badRequest().body("Médecin introuvable.");
        }

        jourDisponibiliteRepository.deleteByMedecinId(id);

        List<JourDisponibilite> creees = new java.util.ArrayList<>();
        for (HoraireRequest h : horaires) {
            try {
                JourDisponibilite jd = new JourDisponibilite();
                jd.setMedecin(medecin.get());
                jd.setJourSemaine(h.getJourSemaine());
                jd.setHeureDebut(java.time.LocalTime.parse(h.getHeureDebut()));
                jd.setHeureFin(java.time.LocalTime.parse(h.getHeureFin()));
                creees.add(jourDisponibiliteRepository.save(jd));
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Format d'horaire invalide pour le jour " + h.getJourSemaine() + ".");
            }
        }

        return ResponseEntity.ok(creees);
    }
}