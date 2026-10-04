package com.santerdv.santerdvapi;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionPortefeuilleRepository extends JpaRepository<TransactionPortefeuille, Integer> {
    List<TransactionPortefeuille> findByPatientIdOrderByDateCreationDesc(Integer idPatient);
}
