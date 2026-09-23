package com.santerdv.santerdvapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service d'envoi d'emails via l'API HTTP de Brevo (et non le SMTP direct :
 * Render bloque les connexions SMTP sortantes sur son infrastructure gratuite,
 * l'API HTTP passe par le port 443 qui n'est jamais bloqué).
 */
@Service
public class EmailService {

    @Value("${brevo.api.key:}")
    private String brevoApiKey;

    @Value("${brevo.sender.email:}")
    private String senderEmail;

    @Value("${brevo.sender.name:Santé RDV}")
    private String senderName;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void envoyerEmail(String destinataire, String sujet, String contenu) {
        if (brevoApiKey == null || brevoApiKey.isBlank()) {
            System.err.println("[EmailService] BREVO_API_KEY non configurée — email non envoyé à " + destinataire);
            return;
        }
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("sender", Map.of("name", senderName, "email", senderEmail));
            body.put("to", List.of(Map.of("email", destinataire)));
            body.put("subject", sujet);
            body.put("textContent", contenu);

            String jsonBody = objectMapper.writeValueAsString(body);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.brevo.com/v3/smtp/email"))
                    .timeout(Duration.ofSeconds(15))
                    .header("accept", "application/json")
                    .header("api-key", brevoApiKey)
                    .header("content-type", "application/json")
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
