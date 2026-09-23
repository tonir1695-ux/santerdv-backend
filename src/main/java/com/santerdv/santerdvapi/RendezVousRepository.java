package com.santerdv.santerdvapi;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RendezVousRepository extends JpaRepository<RendezVous, Integer> {
    List<RendezVous> findByPatientId(Integer idPatient);
    List<RendezVous> findByMedecinId(Integer idMedecin);
}