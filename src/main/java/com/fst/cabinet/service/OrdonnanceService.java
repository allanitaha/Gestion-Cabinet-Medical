package com.fst.cabinet.service;

import com.fst.cabinet.entity.Ordonnance;
import com.fst.cabinet.entity.RendezVous;
import com.fst.cabinet.entity.LigneMedicament;
import com.fst.cabinet.repository.OrdonnanceRepository;
import com.fst.cabinet.repository.RendezVousRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrdonnanceService {

    private final OrdonnanceRepository ordRepo;
    private final RendezVousRepository rdvRepo;

    public OrdonnanceService(OrdonnanceRepository ordRepo, RendezVousRepository rdvRepo) {
        this.ordRepo = ordRepo;
        this.rdvRepo = rdvRepo;
    }

    public List<Ordonnance> findAll() {
        return ordRepo.findAll();
    }

    public Ordonnance findById(Long id) {
        return ordRepo.findById(id).orElse(null);
    }

    public List<Ordonnance> getOrdonnancesByRdv(Long rdvId) {
        List<Ordonnance> ordonnances = ordRepo.findByRendezVousId(rdvId);
        if (ordonnances.isEmpty()) {
            throw new RuntimeException("Aucune ordonnance pour ce RDV");
        }
        return ordonnances;
    }

    public Ordonnance save(Ordonnance ordonnance) {
        return ordRepo.save(ordonnance);
    }

    public void delete(Long id) {
        ordRepo.deleteById(id);
    }

    // --- Nouvelle méthode creer ---
    public Ordonnance creer(Long rdvId, String observations, List<LigneMedicament> lignes) {
        RendezVous rdv = rdvRepo.findById(rdvId)
                .orElseThrow(() -> new RuntimeException("RDV introuvable"));

        Ordonnance ordonnance = new Ordonnance();
        ordonnance.setRendezVous(rdv);
        ordonnance.setObservations(observations);
        ordonnance.setLignes(lignes);

        return ordRepo.save(ordonnance);
    }
}
