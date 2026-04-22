package com.gestion.cabinet.domain;

import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
		name = "medecins",
		uniqueConstraints = { @UniqueConstraint(name = "uk_medecin_numero_ordre", columnNames = { "numero_ordre" }) }
)
public class Medecin {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 80)
	private String nom;

	@Column(nullable = false, length = 80)
	private String prenom;

	@Column(length = 120)
	private String specialite;

	@Column(name = "numero_ordre", nullable = false, length = 64)
	private String numeroOrdre;

	@Column(length = 30)
	private String telephone;

	@Column(length = 120)
	private String email;

	@Column(nullable = false)
	private boolean actif;

	@OneToMany(mappedBy = "medecin")
	private Set<RendezVous> rendezVous = new LinkedHashSet<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getSpecialite() {
		return specialite;
	}

	public void setSpecialite(String specialite) {
		this.specialite = specialite;
	}

	public String getNumeroOrdre() {
		return numeroOrdre;
	}

	public void setNumeroOrdre(String numeroOrdre) {
		this.numeroOrdre = numeroOrdre;
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

	public boolean isActif() {
		return actif;
	}

	public void setActif(boolean actif) {
		this.actif = actif;
	}

	public Set<RendezVous> getRendezVous() {
		return rendezVous;
	}

	public void setRendezVous(Set<RendezVous> rendezVous) {
		this.rendezVous = rendezVous;
	}
}

