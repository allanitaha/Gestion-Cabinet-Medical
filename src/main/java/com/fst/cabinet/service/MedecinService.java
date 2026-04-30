package com.fst.cabinet.service;

import com.fst.cabinet.entity.Medecin;
import com.fst.cabinet.repository.MedecinRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedecinService {

    private final MedecinRepository medecinRepository;

    public MedecinService(MedecinRepository medecinRepository) {
        this.medecinRepository = medecinRepository;
    }

    public List<Medecin> findAll() {
        return medecinRepository.findAll();
    }

    public List<Medecin> findActifs() {
        return medecinRepository.findByActifTrue();
    }

    public Medecin findById(Long id) {
        return medecinRepository.findById(id).orElse(null);
    }

    public Medecin save(Medecin medecin) {
        return medecinRepository.save(medecin);
    }

    public void delete(Long id) {
        medecinRepository.deleteById(id);
    }

    // --- Nouvelle méthode update ---
    public Medecin update(Long id, Medecin medecin) {
        Medecin existing = medecinRepository.findById(id).orElse(null);
        if (existing != null) {
            existing.setNom(medecin.getNom());
            existing.setPrenom(medecin.getPrenom());
            existing.setSpecialite(medecin.getSpecialite());
            existing.setActif(medecin.isActif());
            // ajoute les autres champs nécessaires
            return medecinRepository.save(existing);
        }
        return null;
    }
}
