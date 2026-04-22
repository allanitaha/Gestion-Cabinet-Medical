package com.gestion.cabinet.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "rendez_vous")
public class RendezVous {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private LocalDateTime dateHeure;

	@Column(nullable = false)
	private Integer dureeMinutes;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private RendezVousStatut statut;

	@Column(length = 255)
	private String motif;

	@ManyToOne(optional = false)
	@JoinColumn(name = "patient_id", nullable = false)
	private Patient patient;

	@ManyToOne(optional = false)
	@JoinColumn(name = "medecin_id", nullable = false)
	private Medecin medecin;

	@OneToOne(mappedBy = "rendezVous")
	private Ordonnance ordonnance;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDateTime getDateHeure() {
		return dateHeure;
	}

	public void setDateHeure(LocalDateTime dateHeure) {
		this.dateHeure = dateHeure;
	}

	public Integer getDureeMinutes() {
		return dureeMinutes;
	}

	public void setDureeMinutes(Integer dureeMinutes) {
		this.dureeMinutes = dureeMinutes;
	}

	public RendezVousStatut getStatut() {
		return statut;
	}

	public void setStatut(RendezVousStatut statut) {
		this.statut = statut;
	}

	public String getMotif() {
		return motif;
	}

	public void setMotif(String motif) {
		this.motif = motif;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public Medecin getMedecin() {
		return medecin;
	}

	public void setMedecin(Medecin medecin) {
		this.medecin = medecin;
	}

	public Ordonnance getOrdonnance() {
		return ordonnance;
	}

	public void setOrdonnance(Ordonnance ordonnance) {
		this.ordonnance = ordonnance;
	}
}

