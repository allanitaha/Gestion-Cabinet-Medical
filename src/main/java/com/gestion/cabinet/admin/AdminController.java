package com.gestion.cabinet.admin;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

	public AdminController(
			PatientRepository patientRepository,
			MedecinRepository medecinRepository,
			CreneauRepository creneauRepository,
			AppUserRepository appUserRepository,
			PasswordEncoder passwordEncoder
	) {
		this.patientRepository = patientRepository;
		this.medecinRepository = medecinRepository;
		this.creneauRepository = creneauRepository;
		this.appUserRepository = appUserRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@GetMapping
	public String adminHome(Model model) {
		model.addAttribute("patients", patientRepository.findAll());
		model.addAttribute("medecins", medecinRepository.findAll());
		model.addAttribute("creneaux", creneauRepository.findAll());
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
			@RequestParam(required = false) String dateNaissance
	) {
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
			@RequestParam String nom,
			@RequestParam String prenom,
			@RequestParam(required = false) String specialite,
			@RequestParam String numeroOrdre,
			@RequestParam(required = false) String telephone,
			@RequestParam(required = false) String email,
			@RequestParam(defaultValue = "true") boolean actif
	) {
		Medecin m = new Medecin();
		m.setNom(nom);
		m.setPrenom(prenom);
		m.setSpecialite(specialite);
		m.setNumeroOrdre(numeroOrdre);
		m.setTelephone(telephone);
		m.setEmail(email);
		m.setActif(actif);
		medecinRepository.save(m);
		return "redirect:/admin/medecins/new?created=1";
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
			@RequestParam String password
	) {
		if (appUserRepository.existsByUsername(username)) {
			return "redirect:/admin?tab=secretaires&error=user_exists";
		}

		AppUser user = new AppUser();
		user.setUsername(username);
		user.setPasswordHash(passwordEncoder.encode(password));
		user.setRole(UserRole.SECRETAIRE);
		user.setEnabled(true);
		appUserRepository.save(user);

		return "redirect:/admin?tab=secretaires&created=1";
	}
}
