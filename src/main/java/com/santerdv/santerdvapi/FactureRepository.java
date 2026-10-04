package com.santerdv.santerdvapi;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FactureRepository extends JpaRepository<Facture, Integer> {
    Optional<Facture> findByRendezVousId(Integer idRendezVous);
}
