package com.fst.cabinet.service;

import com.fst.cabinet.entity.RendezVous;
import com.fst.cabinet.entity.StatutRDV;
import com.fst.cabinet.repository.RendezVousRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RendezVousService {

    private final RendezVousRepository rendezVousRepository;

    public RendezVousService(RendezVousRepository rendezVousRepository) {
        this.rendezVousRepository = rendezVousRepository;
    }

    // --- CRUD de base ---
    public List<RendezVous> findAll() {
        return rendezVousRepository.findAll();
    }

    public RendezVous findById(Long id) {
        return rendezVousRepository.findById(id).orElse(null);
    }

    public RendezVous save(RendezVous rdv) {
        return rendezVousRepository.save(rdv);
    }

    public void delete(Long id) {
        rendezVousRepository.deleteById(id);
    }

    // --- Recherche par patient ---
    public List<RendezVous> findByPatient(Long patientId) {
        return rendezVousRepository.findByPatientId(patientId);
    }

    // --- Mise à jour ---
    public RendezVous update(Long id, RendezVous rdv) {
        RendezVous existing = rendezVousRepository.findById(id).orElse(null);
        if (existing != null) {
            existing.setDateHeure(rdv.getDateHeure());
            existing.setMotif(rdv.getMotif());
            existing.setStatut(rdv.getStatut());
            existing.setPatient(rdv.getPatient());
            existing.setMedecin(rdv.getMedecin());
            existing.setSecretaire(rdv.getSecretaire());
            return rendezVousRepository.save(existing);
        }
        return null;
    }

    // --- Changer statut ---
    public RendezVous changerStatut(Long id, StatutRDV statut) {
        RendezVous rdv = rendezVousRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("RDV introuvable"));
        rdv.setStatut(statut);
        return rendezVousRepository.save(rdv);
    }

    // --- Rendez-vous du jour ---
    public List<RendezVous> rdvDuJour() {
        LocalDate today = LocalDate.now();
        return rendezVousRepository.findAll().stream()
                .filter(r -> r.getDateHeure().toLocalDate().equals(today))
                .collect(Collectors.toList());
    }

    // --- Rendez-vous de la semaine ---
    public List<RendezVous> rdvDeLaSemaine() {
        LocalDate today = LocalDate.now();
        LocalDate endOfWeek = today.plusDays(7);
        return rendezVousRepository.findAll().stream()
                .filter(r -> !r.getDateHeure().toLocalDate().isBefore(today)
                        && !r.getDateHeure().toLocalDate().isAfter(endOfWeek))
                .collect(Collectors.toList());
    }

    // --- Patients en attente ---
    public List<RendezVous> patientsEnAttente() {
        return rendezVousRepository.findAll().stream()
                .filter(r -> r.getStatut() == StatutRDV.PLANIFIE)
                .collect(Collectors.toList());
    }
}
