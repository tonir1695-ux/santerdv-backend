package com.santerdv.santerdvapi;

import org.springframework.stereotype.Service;

@Service
public class SmsService {

    public boolean envoyerSms(String numeroTelephone, String message) {
        // SIMULATION D'ENVOI SMS
        // En production, remplacer ce bloc par un appel à l'API Twilio (ou autre fournisseur SMS)
        System.out.println("========================================");
        System.out.println("[SIMULATION SMS]");
        System.out.println("Destinataire : " + numeroTelephone);
        System.out.println("Message      : " + message);
        System.out.println("Statut       : ENVOYÉ (simulé)");
        System.out.println("========================================");

        return true; // simule un envoi toujours réussi
    }
}