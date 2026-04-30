package com.gestion.cabinet.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gestion.cabinet.domain.Medecin;

public interface MedecinRepository extends JpaRepository<Medecin, Long> {}

