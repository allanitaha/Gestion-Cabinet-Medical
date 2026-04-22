package com.gestion.cabinet.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
		name = "patients",
		uniqueConstraints = { @UniqueConstraint(name = "uk_patient_cin", columnNames = { "cin" }) }
)
public class Patient {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 32)
	private String cin;

	@Column(nullable = false, length = 80)
	private String nom;

	@Column(nullable = false, length = 80)
	private String prenom;

	private LocalDate dateNaissance;

	@Column(length = 30)
	private String telephone;

	@Column(length = 120)
	private String email;

	@Column(columnDefinition = "TEXT")
	private String antecedents;

	@Column(nullable = false)
	private LocalDateTime dateCreation;

	@OneToMany(mappedBy = "patient")
	private Set<RendezVous> rendezVous = new LinkedHashSet<>();

	@PrePersist
	void onCreate() {
		if (dateCreation == null) {
			dateCreation = LocalDateTime.now();
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCin() {
		return cin;
	}

	public void setCin(String cin) {
		this.cin = cin;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getPrenom() {
		return prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public LocalDate getDateNaissance() {
		return dateNaissance;
	}

	public void setDateNaissance(LocalDate dateNaissance) {
		this.dateNaissance = dateNaissance;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAntecedents() {
		return antecedents;
	}

	public void setAntecedents(String antecedents) {
		this.antecedents = antecedents;
	}

	public LocalDateTime getDateCreation() {
		return dateCreation;
	}

	public void setDateCreation(LocalDateTime dateCreation) {
		this.dateCreation = dateCreation;
	}

	public Set<RendezVous> getRendezVous() {
		return rendezVous;
	}

	public void setRendezVous(Set<RendezVous> rendezVous) {
		this.rendezVous = rendezVous;
	}
}

