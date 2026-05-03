package com.gestion.cabinet;

import java.io.IOException;
import java.time.LocalDate;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;

import com.gestion.cabinet.domain.Medecin;
import com.gestion.cabinet.domain.Patient;
import com.gestion.cabinet.repository.MedecinRepository;
import com.gestion.cabinet.repository.PatientRepository;
import com.gestion.cabinet.security.AppUser;
import com.gestion.cabinet.security.AppUserRepository;
import com.gestion.cabinet.security.UserRole;

@Controller
public class AuthController {

	private final PatientRepository patientRepository;
	private final MedecinRepository medecinRepository;
	private final AppUserRepository appUserRepository;
	private final PasswordEncoder passwordEncoder;
	private final ProfileImageService profileImageService;

	public AuthController(
			PatientRepository patientRepository,
			MedecinRepository medecinRepository,
			AppUserRepository appUserRepository,
			PasswordEncoder passwordEncoder,
			ProfileImageService profileImageService
	) {
		this.patientRepository = patientRepository;
		this.medecinRepository = medecinRepository;
		this.appUserRepository = appUserRepository;
		this.passwordEncoder = passwordEncoder;
		this.profileImageService = profileImageService;
	}

	@GetMapping("/login")
	public String login() {
		return "auth/login";
	}

	@GetMapping("/register")
	public String register() {
		return "auth/register";
	}

	@PostMapping("/register")
	public String createUserAccount(
			@RequestParam String username,
			@RequestParam String password,
			@RequestParam(defaultValue = "PATIENT") UserRole role,
			@RequestParam(required = false) String cin,
			@RequestParam String nom,
			@RequestParam String prenom,
			@RequestParam(required = false) String telephone,
			@RequestParam(required = false) String email,
			@RequestParam(required = false) String dateNaissance,
			@RequestParam(required = false) String specialite,
			@RequestParam(required = false) String numeroOrdre,
			@RequestParam(required = false) MultipartFile profileImage
	) throws IOException {
		if (appUserRepository.existsByUsername(username)) {
			return "redirect:/register?error=user_exists";
		}
		if (role != UserRole.PATIENT && role != UserRole.MEDECIN) {
			return "redirect:/register?error=invalid_role";
		}

		AppUser user = new AppUser();
		user.setUsername(username);
		user.setPasswordHash(passwordEncoder.encode(password));
		user.setRole(role);
		if (profileImage != null && !profileImage.isEmpty()) {
			try {
				user.setProfileImageFilename(profileImageService.save(profileImage));
			} catch (IllegalArgumentException ex) {
				return "redirect:/register?error=invalid_image";
			}
		}

		if (role == UserRole.MEDECIN) {
			if (numeroOrdre == null || numeroOrdre.isBlank()) {
				return "redirect:/register?error=missing_profile";
			}
			String normalizedNumeroOrdre = numeroOrdre.trim();
			Medecin medecin = medecinRepository.findByNumeroOrdre(normalizedNumeroOrdre).orElseGet(Medecin::new);
			if (medecin.getId() != null && appUserRepository.existsByMedecin_Id(medecin.getId())) {
				return "redirect:/register?error=numero_ordre_exists";
			}
			medecin.setNom(nom);
			medecin.setPrenom(prenom);
			medecin.setTelephone(telephone);
			medecin.setEmail(email);
			medecin.setSpecialite(specialite);
			medecin.setNumeroOrdre(normalizedNumeroOrdre);
			medecin.setActif(false);
			medecin = medecinRepository.save(medecin);

			user.setEnabled(false);
			user.setMedecin(medecin);
			appUserRepository.save(user);
			return "redirect:/login?doctor_pending=1";
		}

		if (cin == null || cin.isBlank()) {
			return "redirect:/register?error=missing_profile";
		}
		Patient patient = new Patient();
		patient.setCin(cin);
		patient.setNom(nom);
		patient.setPrenom(prenom);
		patient.setTelephone(telephone);
		patient.setEmail(email);
		if (dateNaissance != null && !dateNaissance.isBlank()) {
			patient.setDateNaissance(LocalDate.parse(dateNaissance));
		}
		patient = patientRepository.save(patient);

		user.setEnabled(true);
		user.setPatient(patient);
		appUserRepository.save(user);

		return "redirect:/login?registered=1";
	}
}
