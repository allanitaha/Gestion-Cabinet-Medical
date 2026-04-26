package com.fst.cabinet.repository;

import com.fst.cabinet.entity.Ordonnance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdonnanceRepository extends JpaRepository<Ordonnance, Long> {
    List<Ordonnance> findByRendezVousId(Long rdvId);
}
