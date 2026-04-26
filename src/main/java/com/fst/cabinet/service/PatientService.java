package com.fst.cabinet.service;

import com.fst.cabinet.entity.Patient;
import com.fst.cabinet.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public List<Patient> findAll() {
        return patientRepository.findAll();
    }

    public Patient findById(Long id) {
        return patientRepository.findById(id).orElse(null);
    }

    public Patient save(Patient patient) {
        return patientRepository.save(patient);
    }

    public void delete(Long id) {
        patientRepository.deleteById(id);
    }

    public List<Patient> search(String q) {
        return patientRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(q, q);
    }

    // --- Nouvelle méthode update ---
    public Patient update(Long id, Patient patient) {
        Patient existing = patientRepository.findById(id).orElse(null);
        if (existing != null) {
            existing.setNom(patient.getNom());
            existing.setPrenom(patient.getPrenom());
            existing.setDateNaissance(patient.getDateNaissance());
            existing.setAdresse(patient.getAdresse());
            existing.setTelephone(patient.getTelephone());
            existing.setActif(patient.isActif());
            return patientRepository.save(existing);
        }
        return null;
    }
}

