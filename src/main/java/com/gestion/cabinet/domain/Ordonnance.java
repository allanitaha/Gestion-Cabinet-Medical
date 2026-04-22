package com.gestion.cabinet.domain;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
		name = "ordonnances",
		uniqueConstraints = { @UniqueConstraint(name = "uk_ordonnance_rendez_vous", columnNames = { "rendez_vous_id" }) }
)
public class Ordonnance {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private LocalDate dateEmission;

	@Column(columnDefinition = "TEXT")
	private String observations;

	@OneToOne(optional = false)
	@JoinColumn(name = "rendez_vous_id", nullable = false)
	private RendezVous rendezVous;

	@OneToMany(mappedBy = "ordonnance", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<LigneMedicament> lignes = new LinkedHashSet<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDate getDateEmission() {
		return dateEmission;
	}

	public void setDateEmission(LocalDate dateEmission) {
		this.dateEmission = dateEmission;
	}

	public String getObservations() {
		return observations;
	}

	public void setObservations(String observations) {
		this.observations = observations;
	}

	public RendezVous getRendezVous() {
		return rendezVous;
	}

	public void setRendezVous(RendezVous rendezVous) {
		this.rendezVous = rendezVous;
	}

	public Set<LigneMedicament> getLignes() {
		return lignes;
	}

	public void setLignes(Set<LigneMedicament> lignes) {
		this.lignes = lignes;
	}
}

