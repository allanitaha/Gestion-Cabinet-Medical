package com.gestion.cabinet.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
		name = "creneaux",
		uniqueConstraints = {
				@UniqueConstraint(
						name = "uk_creneau_medecin_date",
						columnNames = { "medecin_id", "date_heure" }
				)
		}
)
public class Creneau {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "medecin_id", nullable = false)
	private Medecin medecin;

	@Column(name = "date_heure", nullable = false)
	private LocalDateTime dateHeure;

	@Column(nullable = false)
	private Integer dureeMinutes;

	@Column(nullable = false)
	private boolean disponible = true;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Medecin getMedecin() {
		return medecin;
	}

	public void setMedecin(Medecin medecin) {
		this.medecin = medecin;
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

	public boolean isDisponible() {
		return disponible;
	}

	public void setDisponible(boolean disponible) {
		this.disponible = disponible;
	}
}

