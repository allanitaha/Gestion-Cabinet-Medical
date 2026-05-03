package com.gestion.cabinet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gestion.cabinet.domain.RendezVous;

public interface RendezVousRepository extends JpaRepository<RendezVous, Long> {
	List<RendezVous> findByPatient_IdOrderByDateHeureDesc(Long patientId);
	List<RendezVous> findByMedecin_IdOrderByDateHeureDesc(Long medecinId);
}
