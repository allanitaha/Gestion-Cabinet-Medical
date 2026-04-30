package com.gestion.cabinet.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gestion.cabinet.domain.Patient;

public interface PatientRepository extends JpaRepository<Patient, Long> {}

