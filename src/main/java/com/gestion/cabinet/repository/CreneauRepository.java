package com.gestion.cabinet.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gestion.cabinet.domain.Creneau;

public interface CreneauRepository extends JpaRepository<Creneau, Long> {
	List<Creneau> findByDisponibleTrueAndDateHeureGreaterThanEqualOrderByDateHeureAsc(LocalDateTime from);
	List<Creneau> findByMedecin_IdAndDisponibleTrueAndDateHeureGreaterThanEqualOrderByDateHeureAsc(Long medecinId, LocalDateTime from);
}

