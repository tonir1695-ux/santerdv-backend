package com.santerdv.santerdvapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

/**
 * Service d'envoi d'emails via l'API HTTP d'EmailJS (port 443, jamais bloqué
 * par Render). EmailJS envoie à travers un compte Gmail personnel connecté
 * par autorisation Google classique — pas de mot de passe d'application,
 * pas de vérification d'entreprise nécessaire.
 */
@Service
public class EmailService {

    @Value("${emailjs.service.id:}")
    private String serviceId;

    @Value("${emailjs.template.id:}")
    private String templateId;

    @Value("${emailjs.public.key:}")
    private String publicKey;

    @Value("${emailjs.private.key:}")
    private String privateKey;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void envoyerEmail(String destinataire, String sujet, String contenu) {
        if (serviceId == null || serviceId.isBlank() || templateId == null || templateId.isBlank()) {
            System.err.println("[EmailService] Configuration EmailJS incomplète — email non envoyé à " + destinataire);
            return;
        }
        try {
            Map<String, Object> body = Map.of(
                    "service_id", serviceId,
                    "template_id", templateId,
                    "user_id", publicKey,
                    "accessToken", privateKey,
                    "template_params", Map.of(
                            "email", destinataire,
                            "subject", sujet,
                            "message", contenu
                    )
            );
            String jsonBody = objectMapper.writeValueAsString(body);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.emailjs.com/api/v1.0/email/send"))
                    .timeout(Duration.ofSeconds(15))
                    .header("Content-Type", "application/json")
                    .header("origin", "http://localhost")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                System.out.println("[EmailService] Email envoyé à " + destinataire);
            } else {
                System.err.println("[EmailService] Échec d'envoi à " + destinataire
                        + " — code " + response.statusCode() + " : " + response.body());
            }
        } catch (Exception e) {
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
