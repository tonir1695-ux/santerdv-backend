package com.santerdv.santerdvapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service d'envoi d'emails (vérification OTP à l'inscription,
 * envoi des identifiants lors de la création d'un compte médecin/réceptionniste
 * par l'administrateur, et notifications de rendez-vous).
 *
 * Configuration réelle via SMTP (voir application.properties).
 * Contrairement au SmsService, celui-ci envoie de VRAIS emails :
 * il faut donc que les identifiants SMTP soient correctement renseignés
 * (variables d'environnement MAIL_USERNAME / MAIL_PASSWORD sur Render).
 */
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void envoyerEmail(String destinataire, String sujet, String contenu) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(destinataire);
            message.setSubject(sujet);
            message.setText(contenu);
            mailSender.send(message);
        } catch (Exception e) {
            // On ne fait pas planter la requête si l'email échoue :
            // on log l'erreur pour diagnostic (à surveiller dans les logs Render).
            System.err.println("[EmailService] Échec d'envoi à " + destinataire + " : " + e.getMessage());
        }
    }

    public void envoyerCodeOtp(String destinataire, String prenom, String code) {
        String sujet = "Santé RDV — Code de vérification";
        String contenu = "Bonjour " + prenom + ",\n\n"
                + "Voici votre code de vérification pour activer votre compte Santé RDV : " + code + "\n\n"
                + "Ce code est valable 15 minutes.\n\n"
                + "Si vous n'êtes pas à l'origine de cette demande, ignorez cet email.\n\n"
                + "L'équipe Santé RDV";
        envoyerEmail(destinataire, sujet, contenu);
    }

    public void envoyerIdentifiants(String destinataire, String prenom, String role, String motDePasseTemporaire) {
        String sujet = "Santé RDV — Votre compte a été créé";
        String contenu = "Bonjour " + prenom + ",\n\n"
                + "Un compte " + role + " a été créé pour vous sur l'application Santé RDV.\n\n"
                + "Identifiant (email) : " + destinataire + "\n"
                + "Mot de passe temporaire : " + motDePasseTemporaire + "\n\n"
                + "Merci de vous connecter et de changer ce mot de passe dès votre première connexion.\n\n"
                + "L'équipe Santé RDV";
        envoyerEmail(destinataire, sujet, contenu);
    }

    public void envoyerRappelRendezVous(String destinataire, String prenom, String infosRendezVous) {
        String sujet = "Santé RDV — Rappel de rendez-vous";
        String contenu = "Bonjour " + prenom + ",\n\n"
                + "Ceci est un rappel pour votre rendez-vous :\n" + infosRendezVous + "\n\n"
                + "L'équipe Santé RDV";
        envoyerEmail(destinataire, sujet, contenu);
    }
}
