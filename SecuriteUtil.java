package com.santerdv.santerdvapi;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.SecureRandom;

/**
 * Utilitaires de sécurité :
 * - hachage des mots de passe (BCrypt, au lieu du stockage en clair)
 * - génération de codes OTP et de mots de passe temporaires
 */
public class SecuriteUtil {

    private static final BCryptPasswordEncoder ENCODEUR = new BCryptPasswordEncoder();
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String CARACTERES_MDP = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";

    public static String hacher(String motDePasseEnClair) {
        return ENCODEUR.encode(motDePasseEnClair);
    }

    public static boolean verifier(String motDePasseEnClair, String motDePasseHache) {
        return ENCODEUR.matches(motDePasseEnClair, motDePasseHache);
    }

    /** Génère un code OTP à 6 chiffres. */
    public static String genererCodeOtp() {
        int code = 100000 + RANDOM.nextInt(900000);
        return String.valueOf(code);
    }

    /** Génère un mot de passe temporaire lisible (10 caractères). */
    public static String genererMotDePasseTemporaire() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(CARACTERES_MDP.charAt(RANDOM.nextInt(CARACTERES_MDP.length())));
        }
        return sb.toString();
    }
}
