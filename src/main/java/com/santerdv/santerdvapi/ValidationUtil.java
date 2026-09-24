package com.santerdv.santerdvapi;

import java.util.regex.Pattern;

/**
 * Validations de format pour les champs saisis par l'utilisateur
 * (nom, prénom, email, téléphone togolais).
 * Utilisé à l'inscription patient ET à la création de comptes par l'admin,
 * pour ne pas se fier uniquement à la validation côté mobile (facilement contournable).
 */
public class ValidationUtil {

    // Lettres (avec accents), espaces, tirets, apostrophes — pas de chiffres.
    private static final Pattern NOM_PATTERN =
            Pattern.compile("^[A-Za-zÀ-ÖØ-öø-ÿ][A-Za-zÀ-ÖØ-öø-ÿ '-]{1,49}$");

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    // Numéro togolais : 8 chiffres, avec ou sans indicatif +228 / 00228.
    private static final Pattern TELEPHONE_PATTERN =
            Pattern.compile("^(\\+228|00228)?[0-9]{8}$");

    public static boolean estNomValide(String valeur) {
        return valeur != null && NOM_PATTERN.matcher(valeur.trim()).matches();
    }

    public static boolean estEmailValide(String valeur) {
        return valeur != null && EMAIL_PATTERN.matcher(valeur.trim()).matches();
    }

    public static boolean estTelephoneValide(String valeur) {
        if (valeur == null) return false;
        String nettoye = valeur.trim().replace(" ", "");
        return TELEPHONE_PATTERN.matcher(nettoye).matches();
    }
}
