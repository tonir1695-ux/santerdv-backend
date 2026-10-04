package com.santerdv.santerdvapi;

/**
 * Calculs de facturation : distance (Haversine) et frais de déplacement par
 * tranches. Valeurs de démonstration pour le prototype — à rendre
 * configurables par établissement dans une version future (cf. mémoire,
 * perspectives d'évolution).
 */
public class FacturationUtil {

    public static double calculerDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        double rayonTerre = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return rayonTerre * c;
    }

    /** Frais de déplacement par tranche de distance (valeurs de démonstration). */
    public static double fraisDeplacement(Double distanceKm) {
        if (distanceKm == null) return 3000; // adresse sans coordonnées GPS : tranche moyenne par défaut
        if (distanceKm <= 3) return 2000;
        if (distanceKm <= 7) return 3000;
        if (distanceKm <= 12) return 5000;
        if (distanceKm <= 20) return 7000;
        return 10000; // "sur devis" dans le document — on applique un plafond de démo
    }
}
