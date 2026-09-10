package com.santerdv.santerdvapi;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer> {
    Optional<Utilisateur> findByEmail(String email);
    java.util.List<Utilisateur> findByRole(String role);
}