package com.gestion.cabinet.security;

import com.gestion.cabinet.domain.Patient;
import com.gestion.cabinet.domain.Medecin;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
		name = "users",
		uniqueConstraints = { @UniqueConstraint(name = "uk_user_username", columnNames = { "username" }) }
)
public class AppUser {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 80)
	private String username;

	@Column(nullable = false, length = 200)
	private String passwordHash;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20, columnDefinition = "varchar(20)")
	private UserRole role;

	@Column(nullable = false)
	private boolean enabled = true;

	@Column(name = "profile_image_filename", length = 255)
	private String profileImageFilename;

	@OneToOne(optional = true)
	@JoinColumn(name = "patient_id", unique = true)
	private Patient patient;

	@OneToOne(optional = true)
	@JoinColumn(name = "medecin_id", unique = true)
	private Medecin medecin;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getProfileImageFilename() {
		return profileImageFilename;
	}

	public void setProfileImageFilename(String profileImageFilename) {
		this.profileImageFilename = profileImageFilename;
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
}
