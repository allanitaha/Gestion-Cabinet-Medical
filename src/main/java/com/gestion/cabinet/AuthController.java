package com.gestion.cabinet;

import java.time.LocalDate;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;

import com.gestion.cabinet.domain.Patient;
import com.gestion.cabinet.repository.PatientRepository;
import com.gestion.cabinet.security.AppUser;
import com.gestion.cabinet.security.AppUserRepository;
import com.gestion.cabinet.security.UserRole;

@Controller
public class AuthController {

	private final PatientRepository patientRepository;
	private final AppUserRepository appUserRepository;
	private final PasswordEncoder passwordEncoder;

	public AuthController(
			PatientRepository patientRepository,
			AppUserRepository appUserRepository,
			PasswordEncoder passwordEncoder
	) {
		this.patientRepository = patientRepository;
		this.appUserRepository = appUserRepository;
		this.passwordEncoder = passwordEncoder;
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
	public String createPatientAccount(
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
			return "redirect:/register?error=user_exists";
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

		AppUser user = new AppUser();
		user.setUsername(username);
		user.setPasswordHash(passwordEncoder.encode(password));
		user.setRole(UserRole.PATIENT);
		user.setEnabled(true);
		user.setPatient(patient);
		appUserRepository.save(user);

		return "redirect:/login?registered=1";
	}
}
