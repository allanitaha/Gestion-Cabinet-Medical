package com.gestion.cabinet.admin;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.gestion.cabinet.ProfileImageService;
import com.gestion.cabinet.domain.Medecin;
import com.gestion.cabinet.domain.Patient;
import com.gestion.cabinet.domain.Creneau;
import com.gestion.cabinet.repository.CreneauRepository;
import com.gestion.cabinet.repository.MedecinRepository;
import com.gestion.cabinet.repository.PatientRepository;
import com.gestion.cabinet.security.AppUser;
import com.gestion.cabinet.security.AppUserRepository;
import com.gestion.cabinet.security.UserRole;

@Controller
@RequestMapping("/admin")
public class AdminController {

	private final PatientRepository patientRepository;
	private final MedecinRepository medecinRepository;
	private final CreneauRepository creneauRepository;
	private final AppUserRepository appUserRepository;
	private final PasswordEncoder passwordEncoder;
	private final ProfileImageService profileImageService;

	public AdminController(
			PatientRepository patientRepository,
			MedecinRepository medecinRepository,
			CreneauRepository creneauRepository,
			AppUserRepository appUserRepository,
			PasswordEncoder passwordEncoder,
			ProfileImageService profileImageService
	) {
		this.patientRepository = patientRepository;
		this.medecinRepository = medecinRepository;
		this.creneauRepository = creneauRepository;
		this.appUserRepository = appUserRepository;
		this.passwordEncoder = passwordEncoder;
		this.profileImageService = profileImageService;
	}

	@GetMapping
	public String adminHome(Model model) {
		model.addAttribute("patients", patientRepository.findAll());
		model.addAttribute("medecins", medecinRepository.findAll());
		model.addAttribute("creneaux", creneauRepository.findAll());
		model.addAttribute("pendingMedecins", appUserRepository.findAll().stream()
				.filter(u -> u.getRole() == UserRole.MEDECIN)
				.filter(u -> u.getMedecin() != null)
				.filter(u -> !u.isEnabled() || !u.getMedecin().isActif())
				.toList());
		model.addAttribute("secretaires", appUserRepository.findAll().stream()
				.filter(u -> u.getRole() == UserRole.SECRETAIRE)
				.toList());
		return "admin/panel";
	}

	@GetMapping("/patients/new")
	public String newPatient(Model model) {
		model.addAttribute("patients", patientRepository.findAll());
		return "admin/patients-new";
	}

	@PostMapping("/patients")
	public String createPatient(
			@RequestParam String username,
			@RequestParam String password,
			@RequestParam String cin,
			@RequestParam String nom,
			@RequestParam String prenom,
			@RequestParam(required = false) String telephone,
			@RequestParam(required = false) String email,
			@RequestParam(required = false) String dateNaissance,
			@RequestParam(required = false) MultipartFile profileImage
	) throws IOException {
		if (appUserRepository.existsByUsername(username)) {
			return "redirect:/admin/patients/new?error=user_exists";
		}

		Patient p = new Patient();
		p.setCin(cin);
		p.setNom(nom);
		p.setPrenom(prenom);
		p.setTelephone(telephone);
		p.setEmail(email);
		if (dateNaissance != null && !dateNaissance.isBlank()) {
			p.setDateNaissance(LocalDate.parse(dateNaissance));
		}
		p = patientRepository.save(p);

		AppUser u = new AppUser();
		u.setUsername(username);
		u.setPasswordHash(passwordEncoder.encode(password));
		u.setRole(UserRole.PATIENT);
		u.setEnabled(true);
		u.setPatient(p);
		applyProfileImage(u, profileImage);
		appUserRepository.save(u);

		return "redirect:/admin/patients/new?created=1";
	}

	@GetMapping("/medecins/new")
	public String newMedecin(Model model) {
		model.addAttribute("medecins", medecinRepository.findAll());
		return "admin/medecins-new";
	}

	@PostMapping("/medecins")
	public String createMedecin(
			@RequestParam(required = false) String username,
			@RequestParam(required = false) String password,
			@RequestParam String nom,
			@RequestParam String prenom,
			@RequestParam(required = false) String specialite,
			@RequestParam String numeroOrdre,
			@RequestParam(required = false) String telephone,
			@RequestParam(required = false) String email,
			@RequestParam(defaultValue = "true") boolean actif,
			@RequestParam(required = false) MultipartFile profileImage
	) throws IOException {
		if (username != null && !username.isBlank() && appUserRepository.existsByUsername(username)) {
			return "redirect:/admin/medecins/new?error=user_exists";
		}
		String normalizedNumeroOrdre = numeroOrdre.trim();
		if (medecinRepository.existsByNumeroOrdre(normalizedNumeroOrdre)) {
			return "redirect:/admin/medecins/new?error=numero_ordre_exists";
		}

		Medecin m = new Medecin();
		m.setNom(nom);
		m.setPrenom(prenom);
		m.setSpecialite(specialite);
		m.setNumeroOrdre(normalizedNumeroOrdre);
		m.setTelephone(telephone);
		m.setEmail(email);
		m.setActif(actif);
		m = medecinRepository.save(m);

		if (username != null && !username.isBlank() && password != null && !password.isBlank()) {
			AppUser user = new AppUser();
			user.setUsername(username);
			user.setPasswordHash(passwordEncoder.encode(password));
			user.setRole(UserRole.MEDECIN);
			user.setEnabled(true);
			user.setMedecin(m);
			applyProfileImage(user, profileImage);
			appUserRepository.save(user);
		}

		return "redirect:/admin/medecins/new?created=1";
	}

	@PostMapping("/medecins/{userId}/approve")
	public String approveMedecin(@PathVariable Long userId) {
		AppUser user = appUserRepository.findById(userId).orElseThrow();
		if (user.getRole() == UserRole.MEDECIN && user.getMedecin() != null) {
			user.getMedecin().setActif(true);
			medecinRepository.save(user.getMedecin());
			user.setEnabled(true);
			appUserRepository.save(user);
		}
		return "redirect:/admin?tab=medecins&approved=1";
	}

	@PostMapping("/creneaux")
	public String createCreneau(
			@RequestParam Long medecinId,
			@RequestParam String dateHeure,
			@RequestParam Integer dureeMinutes
	) {
		Medecin m = medecinRepository.findById(medecinId).orElseThrow();

		Creneau c = new Creneau();
		c.setMedecin(m);
		c.setDateHeure(LocalDateTime.parse(dateHeure));
		c.setDureeMinutes(dureeMinutes);
		c.setDisponible(true);
		creneauRepository.save(c);

		return "redirect:/admin?tab=creneaux&created=1";
	}

	@PostMapping("/secretaires")
	public String createSecretaire(
			@RequestParam String username,
			@RequestParam String password,
			@RequestParam(required = false) MultipartFile profileImage
	) throws IOException {
		if (appUserRepository.existsByUsername(username)) {
			return "redirect:/admin?tab=secretaires&error=user_exists";
		}

		AppUser user = new AppUser();
		user.setUsername(username);
		user.setPasswordHash(passwordEncoder.encode(password));
		user.setRole(UserRole.SECRETAIRE);
		user.setEnabled(true);
		applyProfileImage(user, profileImage);
		appUserRepository.save(user);

		return "redirect:/admin?tab=secretaires&created=1";
	}

	private void applyProfileImage(AppUser user, MultipartFile profileImage) throws IOException {
		if (profileImage == null || profileImage.isEmpty()) {
			return;
		}
		user.setProfileImageFilename(profileImageService.save(profileImage));
	}
}
