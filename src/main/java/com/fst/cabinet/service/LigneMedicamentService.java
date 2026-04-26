package com.fst.cabinet.service;

import com.fst.cabinet.repository.OrdonnanceRepository;
import com.fst.cabinet.entity.Ordonnance;
import com.fst.cabinet.entity.LigneMedicament;
import com.fst.cabinet.repository.LigneMedicamentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LigneMedicamentService {

    private final LigneMedicamentRepository repository;
    private final OrdonnanceRepository ordonnanceRepository;

    public LigneMedicamentService(LigneMedicamentRepository repository,
                                  OrdonnanceRepository ordonnanceRepository) {
        this.repository = repository;
        this.ordonnanceRepository = ordonnanceRepository;
    }

    public LigneMedicament creerLigne(LigneMedicament ligne) {
        // récupérer l'ID envoyé dans le JSON
        Long ordonnanceId = ligne.getOrdonnance().getId();

        // recharger l'ordonnance depuis la base
        Ordonnance ordonnance = ordonnanceRepository.findById(ordonnanceId)
                .orElseThrow(() -> new RuntimeException("Ordonnance introuvable avec ID " + ordonnanceId));

        // rattacher l'ordonnance complète à la ligne
        ligne.setOrdonnance(ordonnance);

        // sauvegarder
        return repository.save(ligne);
    }

    public List<LigneMedicament> getAllLignes() {
        return repository.findAll();
    }
}

