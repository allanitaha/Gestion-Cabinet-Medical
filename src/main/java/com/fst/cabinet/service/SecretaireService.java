package com.fst.cabinet.service;

import com.fst.cabinet.entity.Secretaire;
import com.fst.cabinet.repository.SecretaireRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SecretaireService {

    private final SecretaireRepository secretaireRepository;

    public SecretaireService(SecretaireRepository secretaireRepository) {
        this.secretaireRepository = secretaireRepository;
    }

    public List<Secretaire> findAll() {
        return secretaireRepository.findAll();
    }

    public Secretaire findById(Long id) {
        return secretaireRepository.findById(id).orElse(null);
    }

    public Secretaire save(Secretaire secretaire) {
        return secretaireRepository.save(secretaire);
    }

    public void delete(Long id) {
        secretaireRepository.deleteById(id);
    }

    // --- Méthode update ---
    public Secretaire update(Long id, Secretaire secretaireDetails) {
        Secretaire existing = secretaireRepository.findById(id).orElse(null);
        if (existing != null) {
            existing.setNom(secretaireDetails.getNom());
            existing.setPrenom(secretaireDetails.getPrenom());
            existing.setTelephone(secretaireDetails.getTelephone());
            existing.setEmail(secretaireDetails.getEmail());
            return secretaireRepository.save(existing);
        }
        return null;
    }
}
