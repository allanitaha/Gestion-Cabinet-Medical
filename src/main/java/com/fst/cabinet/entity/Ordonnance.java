package com.fst.cabinet.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Ordonnance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String observations;   // <-- champ manquant

    @ManyToOne
    @JoinColumn(name = "rdv_id")
    private RendezVous rendezVous;

    @OneToMany(mappedBy = "ordonnance", cascade = CascadeType.ALL)
    private List<LigneMedicament> lignes;

    // --- Getters et setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }

    public RendezVous getRendezVous() { return rendezVous; }
    public void setRendezVous(RendezVous rendezVous) { this.rendezVous = rendezVous; }

    public List<LigneMedicament> getLignes() { return lignes; }
    public void setLignes(List<LigneMedicament> lignes) { this.lignes = lignes; }
}
