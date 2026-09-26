package com.santerdv.santerdvapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/rendezvous")
public class RendezVousController {

    @Autowired
    private RendezVousRepository rendezVousRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private MedecinRepository medecinRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private RappelSmsRepository rappelSmsRepository;

    @Autowired
    private SmsService smsService;

    @Autowired
    private JourDisponibiliteRepository jourDisponibiliteRepository;

    @Autowired
    private EmailService emailService;


    @PutMapping("/{id}/annuler")
    public ResponseEntity<?> annulerRendezVous(@PathVariable Integer id, @RequestParam Integer idUtilisateurConnecte) {
        Optional<RendezVous> rdvOpt = rendezVousRepository.findById(id);
        if (rdvOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Rendez-vous introuvable.");
        }

        Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findById(idUtilisateurConnecte);
        if (utilisateurOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Utilisateur introuvable.");
        }

        RendezVous rdv = rdvOpt.get();
        String role = utilisateurOpt.get().getRole();

        boolean autorise =
                ("patient".equals(role) && rdv.getPatient().getId().equals(idUtilisateurConnecte)) ||
                        ("medecin".equals(role) && rdv.getMedecin().getId().equals(idUtilisateurConnecte)) ||
                        "admin".equals(role) || "receptionniste".equals(role);

        if (!autorise) {
            return ResponseEntity.status(403).body("Vous n'êtes pas autorisé à annuler ce rendez-vous.");
        }

        if ("annule".equals(rdv.getStatut())) {
            return ResponseEntity.badRequest().body("Ce rendez-vous est déjà annulé.");
        }

        String nomComplet = utilisateurOpt.get().getPrenom() + " " + utilisateurOpt.get().getNom();

        rdv.setStatut("annule");
        rdv.setAnnuleParNom(nomComplet);
        rdv.setAnnuleParRole(role);
        rdv.setAnnuleLe(java.time.LocalDateTime.now());
        rendezVousRepository.save(rdv);

        String nomMedecin = rdv.getMedecin().getUtilisateur() != null
                ? "Dr " + rdv.getMedecin().getUtilisateur().getPrenom() + " " + rdv.getMedecin().getUtilisateur().getNom()
                : "le médecin";
        String dateTexte = rdv.getDateHeure().toString().replace("T", " à ");

        String messageNotif;
        if ("patient".equals(role)) {
            messageNotif = "Vous avez annulé votre rendez-vous du " + dateTexte + " avec " + nomMedecin + ".";
        } else {
            messageNotif = "⚠ Votre rendez-vous du " + dateTexte + " avec " + nomMedecin + " a été annulé. Merci de reprendre un rendez-vous si besoin.";
        }

        RappelSms notif = new RappelSms();
        notif.setRendezVous(rdv);
        notif.setDateEnvoi(java.time.LocalDateTime.now());
        notif.setStatutEnvoi("envoye");
        notif.setMessage(messageNotif);
        rappelSmsRepository.save(notif);

        return ResponseEntity.ok(rdv);
    }

    /**
     * Fait évoluer le statut du rendez-vous suivant le cycle :
     * confirme → en_cours → termine (le passage de l'heure seule ne suffit
     * pas : c'est le médecin, ou l'admin/réceptionniste, qui déclenche chaque étape).
     */
    @PutMapping("/{id}/demarrer")
    public ResponseEntity<?> demarrerConsultation(@PathVariable Integer id, @RequestParam Integer idUtilisateurConnecte) {
        return changerStatutCycle(id, idUtilisateurConnecte, "confirme", "en_cours");
    }

    @PutMapping("/{id}/terminer")
    public ResponseEntity<?> terminerConsultation(@PathVariable Integer id, @RequestParam Integer idUtilisateurConnecte) {
        return changerStatutCycle(id, idUtilisateurConnecte, "en_cours", "termine");
    }

    private ResponseEntity<?> changerStatutCycle(Integer id, Integer idUtilisateurConnecte, String statutAttendu, String nouveauStatut) {
        Optional<RendezVous> rdvOpt = rendezVousRepository.findById(id);
        if (rdvOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Rendez-vous introuvable.");
        }

        Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findById(idUtilisateurConnecte);
        if (utilisateurOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Utilisateur introuvable.");
        }

        RendezVous rdv = rdvOpt.get();
        String role = utilisateurOpt.get().getRole();

        boolean autorise =
                ("medecin".equals(role) && rdv.getMedecin().getId().equals(idUtilisateurConnecte)) ||
                        "admin".equals(role) || "receptionniste".equals(role);

        if (!autorise) {
            return ResponseEntity.status(403).body("Vous n'êtes pas autorisé à modifier ce rendez-vous.");
        }

        if (!statutAttendu.equals(rdv.getStatut())) {
            return ResponseEntity.badRequest().body(
                    "Transition impossible : le rendez-vous doit être au statut \"" + statutAttendu
                            + "\" (statut actuel : \"" + rdv.getStatut() + "\").");
        }

        rdv.setStatut(nouveauStatut);
        rendezVousRepository.save(rdv);

        return ResponseEntity.ok(rdv);
    }

    @GetMapping("/annules")
    public List<RendezVous> getRendezVousAnnules() {
        return rendezVousRepository.findAll().stream()
                .filter(r -> "annule".equals(r.getStatut()))
                .sorted((a, b) -> {
                    if (a.getAnnuleLe() == null) return 1;
                    if (b.getAnnuleLe() == null) return -1;
                    return b.getAnnuleLe().compareTo(a.getAnnuleLe());
                })
                .toList();
    }

    @GetMapping
    public List<RendezVous> getAllRendezVous() {
        return rendezVousRepository.findAll();
    }

    @GetMapping("/jour")
    public List<RendezVous> getRendezVousDuJour(@RequestParam String date) {
        java.time.LocalDate jour = java.time.LocalDate.parse(date);
        return rendezVousRepository.findAll()
                .stream()
                .filter(rdv -> rdv.getDateHeure().toLocalDate().equals(jour))
                .filter(rdv -> !"annule".equals(rdv.getStatut()))
                .sorted((a, b) -> a.getDateHeure().compareTo(b.getDateHeure()))
                .toList();
    }

    @GetMapping("/patient/{idPatient}")
    public List<RendezVous> getRendezVousByPatient(@PathVariable Integer idPatient) {
        return rendezVousRepository.findByPatientId(idPatient);
    }

    @GetMapping("/medecin/{idMedecin}")
    public ResponseEntity<?> getRendezVousByMedecin(
            @PathVariable Integer idMedecin,
            @RequestParam Integer idUtilisateurConnecte) {

        Optional<Utilisateur> utilisateur = utilisateurRepository.findById(idUtilisateurConnecte);

        if (utilisateur.isEmpty() || !"medecin".equals(utilisateur.get().getRole())) {
            return ResponseEntity.status(403).body("Accès refusé : réservé aux médecins.");
        }

        if (!idUtilisateurConnecte.equals(idMedecin)) {
            return ResponseEntity.status(403).body("Accès refusé : vous ne pouvez consulter que vos propres rendez-vous.");
        }

        return ResponseEntity.ok(rendezVousRepository.findByMedecinId(idMedecin));
    }

    @PostMapping
    public ResponseEntity<?> creerRendezVous(@RequestBody RendezVousRequest requete) {
        Optional<Patient> patient = patientRepository.findById(requete.getIdPatient());
        Optional<Medecin> medecin = medecinRepository.findById(requete.getIdMedecin());

        if (patient.isEmpty() || medecin.isEmpty()) {
            return ResponseEntity.badRequest().body("Patient ou médecin introuvable.");
        }

        String typeConsultation = requete.getTypeConsultation() == null ? "cabinet" : requete.getTypeConsultation();
        if (!"cabinet".equals(typeConsultation) && !"domicile".equals(typeConsultation)) {
            return ResponseEntity.badRequest().body("Type de consultation invalide (cabinet ou domicile attendu).");
        }
        if ("domicile".equals(typeConsultation) &&
                (requete.getAdresseDomicile() == null || requete.getAdresseDomicile().trim().length() < 5)) {
            return ResponseEntity.badRequest().body("Merci de préciser une adresse pour la consultation à domicile.");
        }

        LocalDateTime nouvelleDateHeure = requete.getDateHeure();


        // Règle 1 : l'écart minimum entre deux rendez-vous dépend de la durée de consultation propre à la spécialité du médecin
        Integer dureeMinutes = medecin.get().getSpecialite() != null && medecin.get().getSpecialite().getDureeConsultation() != null
                ? medecin.get().getSpecialite().getDureeConsultation()
                : 30;

        boolean creneauTropProche = rendezVousRepository.findByMedecinId(requete.getIdMedecin())
                .stream()
                .filter(rdv -> !"annule".equals(rdv.getStatut()))
                .anyMatch(rdv -> {
                    long minutesEcart = Math.abs(java.time.Duration.between(rdv.getDateHeure(), nouvelleDateHeure).toMinutes());
                    return minutesEcart < dureeMinutes;
                });

        if (creneauTropProche) {
            return ResponseEntity.badRequest().body("Ce créneau est trop proche d'un autre rendez-vous de ce médecin. Laissez au moins " + dureeMinutes + " minutes d'écart.");
        }

        // Règle 2 : un patient ne peut pas avoir 2 RDV le même jour avec le même médecin
        boolean dejaRdvMemeJourMemeMedecin = rendezVousRepository.findByPatientId(requete.getIdPatient())
                .stream()
                .filter(rdv -> !"annule".equals(rdv.getStatut()))
                .anyMatch(rdv -> rdv.getMedecin().getId().equals(requete.getIdMedecin())
                        && rdv.getDateHeure().toLocalDate().equals(nouvelleDateHeure.toLocalDate()));

        if (dejaRdvMemeJourMemeMedecin) {
            return ResponseEntity.badRequest().body("Vous avez déjà un rendez-vous avec ce médecin aujourd'hui. Un seul rendez-vous par jour avec le même médecin.");
        }

        // Règle 3 : un patient ne peut pas avoir 2 RDV à la même heure, même avec des médecins différents
        boolean conflitHeurePatient = rendezVousRepository.findByPatientId(requete.getIdPatient())
                .stream()
                .filter(rdv -> !"annule".equals(rdv.getStatut()))
                .anyMatch(rdv -> rdv.getDateHeure().toLocalDate().equals(nouvelleDateHeure.toLocalDate())
                        && rdv.getDateHeure().getHour() == nouvelleDateHeure.getHour()
                        && rdv.getDateHeure().getMinute() == nouvelleDateHeure.getMinute());

        if (conflitHeurePatient) {
            return ResponseEntity.badRequest().body("Vous avez déjà un rendez-vous à cette heure avec un autre médecin.");
        }

        // Règle 4 : le RDV doit correspondre à un horaire déclaré du médecin pour ce jour de la semaine
        List<JourDisponibilite> tousLesHoraires = jourDisponibiliteRepository.findByMedecinId(requete.getIdMedecin());

        if (!tousLesHoraires.isEmpty()) {
            // Le médecin a des horaires structurés définis : on les applique strictement
            List<JourDisponibilite> horairesDuJour = tousLesHoraires.stream()
                    .filter(h -> h.getJourSemaine().equals(nouvelleDateHeure.getDayOfWeek().getValue()))
                    .toList();

            if (horairesDuJour.isEmpty()) {
                return ResponseEntity.badRequest().body("Ce médecin ne travaille pas ce jour-là.");
            }

            java.time.LocalTime heureDemandee = nouvelleDateHeure.toLocalTime();
            boolean dansUnePlage = horairesDuJour.stream()
                    .anyMatch(h -> !heureDemandee.isBefore(h.getHeureDebut()) && !heureDemandee.isAfter(h.getHeureFin()));

            if (!dansUnePlage) {
                String plages = horairesDuJour.stream()
                        .map(h -> h.getHeureDebut() + " - " + h.getHeureFin())
                        .reduce((a, b) -> a + ", " + b).orElse("");
                return ResponseEntity.badRequest().body("Ce médecin n'est disponible qu'entre : " + plages);
            }
        } else {
            // Aucun horaire structuré défini du tout : repli sur l'ancien champ texte
            String dispo = medecin.get().getDisponibilites();
            if (dispo != null && dispo.contains("-")) {
                try {
                    String[] parts = dispo.split("-");
                    java.time.LocalTime heureDebut = java.time.LocalTime.parse(parts[0].trim());
                    java.time.LocalTime heureFin = java.time.LocalTime.parse(parts[1].trim());
                    java.time.LocalTime heureDemandee = nouvelleDateHeure.toLocalTime();

                    if (heureDemandee.isBefore(heureDebut) || heureDemandee.isAfter(heureFin)) {
                        return ResponseEntity.badRequest().body("Ce médecin n'est disponible qu'entre " + parts[0].trim() + " et " + parts[1].trim() + ".");
                    }
                } catch (Exception e) {
                    // format invalide : on ignore sans bloquer
                }
            }
        }

        // Règle 5 : pas de rendez-vous le dimanche
        if (nouvelleDateHeure.getDayOfWeek() == java.time.DayOfWeek.SUNDAY) {
            return ResponseEntity.badRequest().body("Les rendez-vous ne sont pas possibles le dimanche.");
        }

        // Règle 6 : le médecin doit être disponible (actif)
        if (medecin.get().getActif() != null && !medecin.get().getActif()) {
            return ResponseEntity.badRequest().body("Ce médecin n'est actuellement pas disponible.");
        }

        // Règle 7 : pause déjeuner fixe pour tous les médecins (12h00 - 13h00)
        java.time.LocalTime heureRdv = nouvelleDateHeure.toLocalTime();
        java.time.LocalTime pauseDebut = java.time.LocalTime.of(12, 0);
        java.time.LocalTime pauseFin = java.time.LocalTime.of(13, 0);

        if (!heureRdv.isBefore(pauseDebut) && heureRdv.isBefore(pauseFin)) {
            return ResponseEntity.badRequest().body("Les rendez-vous ne sont pas possibles entre 12h00 et 13h00 (pause déjeuner).");
        }

        // Règle 8 : fenêtre de réservation glissante, élargie dès le samedi pour couvrir la semaine suivante
        java.time.LocalDateTime maintenant = java.time.LocalDateTime.now();

        if (nouvelleDateHeure.isBefore(maintenant)) {
            return ResponseEntity.badRequest().body("Impossible de prendre un rendez-vous dans le passé.");
        }

        java.time.LocalDate aujourdHui = maintenant.toLocalDate();
        java.time.LocalDate prochainDimanche = aujourdHui;
        while (prochainDimanche.getDayOfWeek() != java.time.DayOfWeek.SUNDAY) {
            prochainDimanche = prochainDimanche.plusDays(1);
        }
        if (prochainDimanche.equals(aujourdHui)) {
            prochainDimanche = prochainDimanche.plusDays(7);
        }
        // Dès le samedi, on ouvre déjà la semaine suivante
        if (aujourdHui.getDayOfWeek() == java.time.DayOfWeek.SATURDAY) {
            prochainDimanche = prochainDimanche.plusDays(7);
        }

        java.time.LocalDateTime limiteMax = prochainDimanche.atStartOfDay();

        if (!nouvelleDateHeure.isBefore(limiteMax)) {
            return ResponseEntity.badRequest().body("Les rendez-vous ne peuvent être pris que jusqu'au dimanche à venir (ou la semaine suivante si on est déjà samedi).");
        }

        RendezVous rdv = new RendezVous();
        rdv.setPatient(patient.get());
        rdv.setMedecin(medecin.get());
        rdv.setDateHeure(nouvelleDateHeure);
        rdv.setStatut("confirme");
        rdv.setDateCreation(LocalDateTime.now());
        rdv.setTypeConsultation(typeConsultation);
        rdv.setAdresseDomicile("domicile".equals(typeConsultation) ? requete.getAdresseDomicile().trim() : null);
        rdv.setLatitudeDomicile("domicile".equals(typeConsultation) ? requete.getLatitudeDomicile() : null);
        rdv.setLongitudeDomicile("domicile".equals(typeConsultation) ? requete.getLongitudeDomicile() : null);

        RendezVous nouveau = rendezVousRepository.save(rdv);

        // Envoi automatique du rappel : SMS (simulé) + email (réel, via EmailService)
        String nomMedecinMsg = medecin.get().getUtilisateur() != null
                ? "Dr " + medecin.get().getUtilisateur().getPrenom() + " " + medecin.get().getUtilisateur().getNom()
                : "le médecin";
        String dateFormatee = nouveau.getDateHeure().toString().replace("T", " à ");
        String lieuMsg = "domicile".equals(typeConsultation)
                ? " (consultation à domicile : " + rdv.getAdresseDomicile() + ")"
                : " (consultation au cabinet)";
        String texteMessage = "Rappel : vous avez un rendez-vous le " + dateFormatee + " avec " + nomMedecinMsg + lieuMsg + ".";
        boolean envoye = smsService.envoyerSms("numéro du patient", texteMessage);

        RappelSms rappel = new RappelSms();
        rappel.setRendezVous(nouveau);
        rappel.setDateEnvoi(LocalDateTime.now());
        rappel.setStatutEnvoi(envoye ? "envoye" : "echoue");
        rappel.setMessage(texteMessage);
        rappelSmsRepository.save(rappel);

        if (patient.get().getUtilisateur() != null && patient.get().getUtilisateur().getEmail() != null) {
            emailService.envoyerRappelRendezVous(
                    patient.get().getUtilisateur().getEmail(),
                    patient.get().getUtilisateur().getPrenom(),
                    texteMessage
            );
        }

        return ResponseEntity.ok(nouveau);
    }
}