package com.santerdv.santerdvapi;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface JourDisponibiliteRepository extends JpaRepository<JourDisponibilite, Integer> {
    List<JourDisponibilite> findByMedecinId(Integer idMedecin);

    @Transactional
    void deleteByMedecinId(Integer idMedecin);
}