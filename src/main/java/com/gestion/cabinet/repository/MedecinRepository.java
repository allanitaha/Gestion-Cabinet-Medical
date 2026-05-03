package com.gestion.cabinet.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gestion.cabinet.domain.Medecin;

import java.util.Optional;

public interface MedecinRepository extends JpaRepository<Medecin, Long> {
	Optional<Medecin> findByNumeroOrdre(String numeroOrdre);
	boolean existsByNumeroOrdre(String numeroOrdre);
}
