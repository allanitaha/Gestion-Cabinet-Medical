package com.gestion.cabinet.patient;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gestion.cabinet.domain.Ordonnance;
import com.gestion.cabinet.domain.Patient;
import com.gestion.cabinet.repository.OrdonnanceRepository;
import com.gestion.cabinet.security.AppUser;
import com.gestion.cabinet.security.AppUserRepository;

@Controller
@RequestMapping("/patient/ordonnances")
public class PatientOrdonnanceController {

	private final AppUserRepository users;
	private final OrdonnanceRepository ordonnanceRepository;

	public PatientOrdonnanceController(AppUserRepository users, OrdonnanceRepository ordonnanceRepository) {
		this.users = users;
		this.ordonnanceRepository = ordonnanceRepository;
	}

	@GetMapping
	public String list(Authentication auth, Model model) {
		Patient patient = currentPatient(auth);
		model.addAttribute("ordonnances", ordonnanceRepository.findByRendezVous_Patient_IdOrderByDateEmissionDesc(patient.getId()));
		return "patient/ordonnances";
	}

	@GetMapping("/{id}")
	public String detail(Authentication auth, @PathVariable Long id, Model model) {
		Patient patient = currentPatient(auth);
		Ordonnance ordonnance = ordonnanceRepository.findById(id)
				.filter(o -> o.getRendezVous().getPatient().getId().equals(patient.getId()))
				.orElse(null);
		model.addAttribute("ordonnance", ordonnance);
		return "patient/ordonnance-detail";
	}

	private Patient currentPatient(Authentication auth) {
		AppUser user = users.findByUsername(auth.getName()).orElseThrow();
		if (user.getPatient() == null) {
			throw new IllegalStateException("Compte patient non lie a un profil patient.");
		}
		return user.getPatient();
	}
}
