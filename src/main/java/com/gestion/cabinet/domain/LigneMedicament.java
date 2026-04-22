package com.gestion.cabinet.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ligne_medicaments")
public class LigneMedicament {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 160)
	private String nomMedicament;

	@Column(length = 255)
	private String posologie;

	@Column(length = 120)
	private String duree;

	@ManyToOne(optional = false)
	@JoinColumn(name = "ordonnance_id", nullable = false)
	private Ordonnance ordonnance;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNomMedicament() {
		return nomMedicament;
	}

	public void setNomMedicament(String nomMedicament) {
		this.nomMedicament = nomMedicament;
	}

	public String getPosologie() {
		return posologie;
	}

	public void setPosologie(String posologie) {
		this.posologie = posologie;
	}

	public String getDuree() {
		return duree;
	}

	public void setDuree(String duree) {
		this.duree = duree;
	}

	public Ordonnance getOrdonnance() {
		return ordonnance;
	}

	public void setOrdonnance(Ordonnance ordonnance) {
		this.ordonnance = ordonnance;
	}
}

