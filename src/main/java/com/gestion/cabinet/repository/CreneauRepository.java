package com.gestion.cabinet.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gestion.cabinet.domain.Creneau;

public interface CreneauRepository extends JpaRepository<Creneau, Long> {
	List<Creneau> findByDisponibleTrueAndDateHeureGreaterThanEqualOrderByDateHeureAsc(LocalDateTime from);
	List<Creneau> findByMedecin_IdOrderByDateHeureDesc(Long medecinId);
	List<Creneau> findByMedecin_IdAndDisponibleTrueAndDateHeureGreaterThanEqualOrderByDateHeureAsc(Long medecinId, LocalDateTime from);
	Optional<Creneau> findByMedecin_IdAndDateHeure(Long medecinId, LocalDateTime dateHeure);
	boolean existsByMedecin_IdAndDateHeure(Long medecinId, LocalDateTime dateHeure);
}
