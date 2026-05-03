package com.gestion.cabinet.medecin;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gestion.cabinet.domain.LigneMedicament;
import com.gestion.cabinet.domain.Medecin;
import com.gestion.cabinet.domain.Ordonnance;
import com.gestion.cabinet.domain.RendezVous;
import com.gestion.cabinet.repository.OrdonnanceRepository;
import com.gestion.cabinet.repository.RendezVousRepository;
import com.gestion.cabinet.security.AppUser;
import com.gestion.cabinet.security.AppUserRepository;

@Controller
@RequestMapping("/medecin/ordonnances")
public class MedecinOrdonnanceController {

	private final AppUserRepository users;
	private final RendezVousRepository rendezVousRepository;
	private final OrdonnanceRepository ordonnanceRepository;

	public MedecinOrdonnanceController(
			AppUserRepository users,
			RendezVousRepository rendezVousRepository,
			OrdonnanceRepository ordonnanceRepository
	) {
		this.users = users;
		this.rendezVousRepository = rendezVousRepository;
		this.ordonnanceRepository = ordonnanceRepository;
	}

	@GetMapping
	public String list(Authentication auth, Model model) {
		Medecin medecin = currentMedecin(auth);
		model.addAttribute("rendezVous", rendezVousRepository.findByMedecin_IdOrderByDateHeureDesc(medecin.getId()));
		model.addAttribute("ordonnances", ordonnanceRepository.findByRendezVous_Medecin_IdOrderByDateEmissionDesc(medecin.getId()));
		return "medecin/ordonnances";
	}

	@GetMapping("/new/{rdvId}")
	public String form(Authentication auth, @PathVariable Long rdvId, Model model, RedirectAttributes redirectAttributes) {
		Medecin medecin = currentMedecin(auth);
		RendezVous rdv = rendezVousRepository.findById(rdvId).orElse(null);
		if (rdv == null || rdv.getMedecin() == null || !rdv.getMedecin().getId().equals(medecin.getId())) {
			redirectAttributes.addFlashAttribute("errorMessage", "Rendez-vous introuvable.");
			return "redirect:/medecin/ordonnances";
		}
		model.addAttribute("rdv", rdv);
		model.addAttribute("ordonnance", ordonnanceRepository.findByRendezVous_Id(rdvId).orElse(null));
		return "medecin/ordonnance-form";
	}

	@PostMapping
	public String save(
			Authentication auth,
			@RequestParam Long rdvId,
			@RequestParam(required = false) String observations,
			@RequestParam List<String> nomMedicament,
			@RequestParam List<String> posologie,
			@RequestParam List<String> duree,
			RedirectAttributes redirectAttributes
	) {
		Medecin medecin = currentMedecin(auth);
		RendezVous rdv = rendezVousRepository.findById(rdvId).orElse(null);
		if (rdv == null || rdv.getMedecin() == null || !rdv.getMedecin().getId().equals(medecin.getId())) {
			redirectAttributes.addFlashAttribute("errorMessage", "Rendez-vous introuvable.");
			return "redirect:/medecin/ordonnances";
		}

		Ordonnance ordonnance = ordonnanceRepository.findByRendezVous_Id(rdvId).orElseGet(Ordonnance::new);
		ordonnance.setRendezVous(rdv);
		ordonnance.setDateEmission(LocalDate.now());
		ordonnance.setObservations(observations);
		ordonnance.getLignes().clear();

		for (int i = 0; i < nomMedicament.size(); i++) {
			if (nomMedicament.get(i) == null || nomMedicament.get(i).isBlank()) {
				continue;
			}
			LigneMedicament ligne = new LigneMedicament();
			ligne.setNomMedicament(nomMedicament.get(i));
			ligne.setPosologie(valueAt(posologie, i));
			ligne.setDuree(valueAt(duree, i));
			ligne.setOrdonnance(ordonnance);
			ordonnance.getLignes().add(ligne);
		}

		if (ordonnance.getLignes().isEmpty()) {
			redirectAttributes.addFlashAttribute("errorMessage", "Ajoutez au moins un medicament.");
			return "redirect:/medecin/ordonnances/new/" + rdvId;
		}

		ordonnanceRepository.save(ordonnance);
		redirectAttributes.addFlashAttribute("successMessage", "Ordonnance enregistree.");
		return "redirect:/medecin/ordonnances";
	}

	private Medecin currentMedecin(Authentication auth) {
		AppUser user = users.findByUsername(auth.getName()).orElseThrow();
		if (user.getMedecin() == null) {
			throw new IllegalStateException("Compte medecin non lie a un profil medecin.");
		}
		return user.getMedecin();
	}

	private String valueAt(List<String> values, int index) {
		return values != null && index < values.size() ? values.get(index) : "";
	}
}
