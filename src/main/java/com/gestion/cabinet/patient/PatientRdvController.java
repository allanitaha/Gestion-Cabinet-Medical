package com.gestion.cabinet.patient;

import java.time.LocalDateTime;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
			Authentication auth,
			@RequestParam(required = false) Long medecinId,
			Model model
	) {
		model.addAttribute("medecins", medecins.findAll());
		model.addAttribute("selectedMedecinId", medecinId);
		model.addAttribute("cancelDeadline", LocalDateTime.now().plusDays(1));
		if (medecinId != null) {
			model.addAttribute(
					"creneaux",
					creneaux.findByMedecin_IdAndDisponibleTrueAndDateHeureGreaterThanEqualOrderByDateHeureAsc(medecinId, LocalDateTime.now())
			);
		} else {
			model.addAttribute("creneaux", creneaux.findByDisponibleTrueAndDateHeureGreaterThanEqualOrderByDateHeureAsc(LocalDateTime.now()));
		}
		if (auth != null) {
			users.findByUsername(auth.getName())
					.map(AppUser::getPatient)
					.ifPresent(patient -> model.addAttribute(
							"mesRendezVous",
							rdvs.findByPatient_IdOrderByDateHeureDesc(patient.getId())
					));
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

	@PostMapping("/{id}/annuler")
	public String cancel(
			Authentication auth,
			@PathVariable Long id,
			RedirectAttributes redirectAttributes
	) {
		AppUser u = users.findByUsername(auth.getName()).orElseThrow();
		Patient p = u.getPatient();
		if (p == null) {
			return "redirect:/patient/rdv/new?error=no_patient_profile";
		}

		RendezVous rdv = rdvs.findById(id).orElse(null);
		if (rdv == null || rdv.getPatient() == null || !rdv.getPatient().getId().equals(p.getId())) {
			redirectAttributes.addFlashAttribute("errorMessage", "Rendez-vous introuvable.");
			return "redirect:/patient/rdv/new";
		}
		if (rdv.getStatut() == RendezVousStatut.ANNULE) {
			redirectAttributes.addFlashAttribute("infoMessage", "Ce rendez-vous est deja annule.");
			return "redirect:/patient/rdv/new";
		}
		if (rdv.getStatut() == RendezVousStatut.TERMINE) {
			redirectAttributes.addFlashAttribute("infoMessage", "Un rendez-vous termine ne peut pas etre annule.");
			return "redirect:/patient/rdv/new";
		}
		if (rdv.getDateHeure().isBefore(LocalDateTime.now().plusDays(1))) {
			redirectAttributes.addFlashAttribute("errorMessage", "Annulation possible uniquement au moins 24h avant le rendez-vous.");
			return "redirect:/patient/rdv/new";
		}

		rdv.setStatut(RendezVousStatut.ANNULE);
		rdvs.save(rdv);

		creneaux.findByMedecin_IdAndDateHeure(rdv.getMedecin().getId(), rdv.getDateHeure())
				.ifPresent(creneau -> {
					creneau.setDisponible(true);
					creneaux.save(creneau);
				});

		redirectAttributes.addFlashAttribute("successMessage", "Votre rendez-vous a ete annule.");
		return "redirect:/patient/rdv/new";
	}
}
