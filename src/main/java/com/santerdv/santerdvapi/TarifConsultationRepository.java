package com.santerdv.santerdvapi;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TarifConsultationRepository extends JpaRepository<TarifConsultation, Integer> {
    List<TarifConsultation> findByEtablissementId(Integer idEtablissement);
    Optional<TarifConsultation> findByMedecinId(Integer idMedecin);
    Optional<TarifConsultation> findByEtablissementIdAndSpecialiteIdAndMedecinIsNull(Integer idEtablissement, Integer idSpecialite);
}
