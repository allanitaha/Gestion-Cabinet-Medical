package com.gestion.cabinet.medecin;

import java.time.LocalDateTime;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gestion.cabinet.domain.Creneau;
import com.gestion.cabinet.domain.Medecin;
import com.gestion.cabinet.repository.CreneauRepository;
import com.gestion.cabinet.security.AppUser;
import com.gestion.cabinet.security.AppUserRepository;

@Controller
@RequestMapping("/medecin/creneaux")
public class MedecinCreneauController {

	private final AppUserRepository users;
	private final CreneauRepository creneaux;

	public MedecinCreneauController(AppUserRepository users, CreneauRepository creneaux) {
		this.users = users;
		this.creneaux = creneaux;
	}

	@GetMapping
	public String list(Authentication auth, Model model) {
		Medecin medecin = currentMedecin(auth);
		model.addAttribute("creneaux", creneaux.findByMedecin_IdOrderByDateHeureDesc(medecin.getId()));
		return "medecin/creneaux";
	}

	@PostMapping
	public String create(
			Authentication auth,
			@RequestParam String dateHeure,
			@RequestParam(defaultValue = "30") Integer dureeMinutes,
			RedirectAttributes redirectAttributes
	) {
		Medecin medecin = currentMedecin(auth);
		LocalDateTime parsedDateHeure = LocalDateTime.parse(dateHeure);
		if (parsedDateHeure.isBefore(LocalDateTime.now())) {
			redirectAttributes.addFlashAttribute("errorMessage", "Choisissez une date future.");
			return "redirect:/medecin/creneaux";
		}
		if (dureeMinutes == null || dureeMinutes < 5) {
			redirectAttributes.addFlashAttribute("errorMessage", "La duree doit etre au moins 5 minutes.");
			return "redirect:/medecin/creneaux";
		}
		if (creneaux.existsByMedecin_IdAndDateHeure(medecin.getId(), parsedDateHeure)) {
			redirectAttributes.addFlashAttribute("errorMessage", "Ce creneau existe deja.");
			return "redirect:/medecin/creneaux";
		}

		Creneau creneau = new Creneau();
		creneau.setMedecin(medecin);
		creneau.setDateHeure(parsedDateHeure);
		creneau.setDureeMinutes(dureeMinutes);
		creneau.setDisponible(true);
		creneaux.save(creneau);

		redirectAttributes.addFlashAttribute("successMessage", "Creneau ajoute.");
		return "redirect:/medecin/creneaux";
	}

	private Medecin currentMedecin(Authentication auth) {
		AppUser user = users.findByUsername(auth.getName()).orElseThrow();
		if (user.getMedecin() == null) {
			throw new IllegalStateException("Compte medecin non lie a un profil medecin.");
		}
		return user.getMedecin();
	}
}
