package com.gestion.cabinet.patient;

import java.time.LocalDateTime;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.gestion.cabinet.domain.Creneau;
import com.gestion.cabinet.domain.Patient;
import com.gestion.cabinet.domain.RendezVous;
import com.gestion.cabinet.domain.RendezVousStatut;
import com.gestion.cabinet.repository.CreneauRepository;
import com.gestion.cabinet.repository.MedecinRepository;
import com.gestion.cabinet.repository.RendezVousRepository;
import com.gestion.cabinet.security.AppUser;
import com.gestion.cabinet.security.AppUserRepository;

@Controller
@RequestMapping("/patient/rdv")
public class PatientRdvController {

	private final AppUserRepository users;
	private final CreneauRepository creneaux;
	private final MedecinRepository medecins;
	private final RendezVousRepository rdvs;

	public PatientRdvController(
			AppUserRepository users,
			CreneauRepository creneaux,
			MedecinRepository medecins,
			RendezVousRepository rdvs
	) {
		this.users = users;
		this.creneaux = creneaux;
		this.medecins = medecins;
		this.rdvs = rdvs;
	}

	@GetMapping("/new")
	public String newRdv(
			@RequestParam(required = false) Long medecinId,
			Model model
	) {
		model.addAttribute("medecins", medecins.findAll());
		model.addAttribute("selectedMedecinId", medecinId);
		if (medecinId != null) {
			model.addAttribute(
					"creneaux",
					creneaux.findByMedecin_IdAndDisponibleTrueAndDateHeureGreaterThanEqualOrderByDateHeureAsc(medecinId, LocalDateTime.now())
			);
		} else {
			model.addAttribute("creneaux", creneaux.findByDisponibleTrueAndDateHeureGreaterThanEqualOrderByDateHeureAsc(LocalDateTime.now()));
		}
		return "patient/rdv-new";
	}

	@PostMapping
	public String book(
			Authentication auth,
			@RequestParam Long creneauId,
			@RequestParam(required = false) String motif
	) {
		AppUser u = users.findByUsername(auth.getName()).orElseThrow();
		Patient p = u.getPatient();
		if (p == null) {
			return "redirect:/patient/rdv/new?error=no_patient_profile";
		}

		Creneau c = creneaux.findById(creneauId).orElseThrow();
		if (!c.isDisponible()) {
			return "redirect:/patient/rdv/new?error=not_available";
		}

		RendezVous r = new RendezVous();
		r.setPatient(p);
		r.setMedecin(c.getMedecin());
		r.setDateHeure(c.getDateHeure());
		r.setDureeMinutes(c.getDureeMinutes());
		r.setMotif(motif);
		r.setStatut(RendezVousStatut.PLANIFIE);
		rdvs.save(r);

		c.setDisponible(false);
		creneaux.save(c);

		return "redirect:/patient/rdv/new?created=1";
	}
}

