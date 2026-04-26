package com.fst.cabinet.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class RendezVous {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateHeure;
    private String motif;

    @Enumerated(EnumType.STRING)
    private StatutRDV statut;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "medecin_id")
    private Medecin medecin;

    // --- Relation avec Secretaire ---
    @ManyToOne
    @JoinColumn(name = "secretaire_id")
    private Secretaire secretaire;

    // --- Getters et setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getDateHeure() { return dateHeure; }
    public void setDateHeure(LocalDateTime dateHeure) { this.dateHeure = dateHeure; }

    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }

    public StatutRDV getStatut() { return statut; }
    public void setStatut(StatutRDV statut) { this.statut = statut; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public Medecin getMedecin() { return medecin; }
    public void setMedecin(Medecin medecin) { this.medecin = medecin; }

    public Secretaire getSecretaire() { return secretaire; }
    public void setSecretaire(Secretaire secretaire) { this.secretaire = secretaire; }
}

































