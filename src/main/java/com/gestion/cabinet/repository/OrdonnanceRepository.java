package com.gestion.cabinet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.gestion.cabinet.domain.Ordonnance;

public interface OrdonnanceRepository extends JpaRepository<Ordonnance, Long> {
	@Override
	@EntityGraph(attributePaths = "lignes")
	Optional<Ordonnance> findById(Long id);

	@EntityGraph(attributePaths = "lignes")
	Optional<Ordonnance> findByRendezVous_Id(Long rendezVousId);

	@EntityGraph(attributePaths = "lignes")
	List<Ordonnance> findByRendezVous_Patient_IdOrderByDateEmissionDesc(Long patientId);

	@EntityGraph(attributePaths = "lignes")
	List<Ordonnance> findByRendezVous_Medecin_IdOrderByDateEmissionDesc(Long medecinId);
}
