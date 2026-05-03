package com.gestion.cabinet;

import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gestion.cabinet.domain.RendezVous;
import com.gestion.cabinet.domain.RendezVousStatut;
import com.gestion.cabinet.repository.CreneauRepository;
import com.gestion.cabinet.repository.MedecinRepository;
import com.gestion.cabinet.repository.PatientRepository;
import com.gestion.cabinet.repository.RendezVousRepository;
import com.gestion.cabinet.security.AppUser;
import com.gestion.cabinet.security.AppUserRepository;

@Controller
public class MainController {

	private final PatientRepository patientRepository;
	private final MedecinRepository medecinRepository;
	private final RendezVousRepository rendezVousRepository;
	private final CreneauRepository creneauRepository;
	private final AppUserRepository appUserRepository;

	public MainController(
			PatientRepository patientRepository,
			MedecinRepository medecinRepository,
			RendezVousRepository rendezVousRepository,
			CreneauRepository creneauRepository,
			AppUserRepository appUserRepository
	) {
		this.patientRepository = patientRepository;
		this.medecinRepository = medecinRepository;
		this.rendezVousRepository = rendezVousRepository;
		this.creneauRepository = creneauRepository;
		this.appUserRepository = appUserRepository;
	}

	@GetMapping("/dashboard")
	public String dashboard(Authentication authentication, Model model) {
		if (isPatientOnly(authentication)) {
			return "redirect:/patient/rdv/new";
		}
		model.addAttribute("patientsCount", patientRepository.count());
		model.addAttribute("medecinsCount", medecinRepository.count());
		model.addAttribute("rdvCount", rendezVousRepository.count());
		return "dashboard";
	}

	@GetMapping("/choose")
	public String choose(Authentication authentication, Model model) {
		boolean isAdmin = authentication.getAuthorities().stream()
				.anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
		boolean isPatient = authentication.getAuthorities().stream()
				.anyMatch(a -> "ROLE_PATIENT".equals(a.getAuthority()));
		boolean isSecretaire = authentication.getAuthorities().stream()
				.anyMatch(a -> "ROLE_SECRETAIRE".equals(a.getAuthority()));
		boolean isMedecin = authentication.getAuthorities().stream()
				.anyMatch(a -> "ROLE_MEDECIN".equals(a.getAuthority()));

		model.addAttribute("isAdmin", isAdmin);
		model.addAttribute("isPatient", isPatient);
		model.addAttribute("isSecretaire", isSecretaire);
		model.addAttribute("isMedecin", isMedecin);
		return "choose";
	}

	// Préparation pour la page patients (à créer ensuite)
    @GetMapping("/patients/list")
    public String listPatients(Model model) {
		model.addAttribute(
				"patients",
				patientRepository.findAll(Sort.by("nom").ascending().and(Sort.by("prenom").ascending()))
		);
        return "patients/list";
    }

    @GetMapping("/medecins/list")
    public String listMedecins(Model model) {
		model.addAttribute(
				"medecins",
				medecinRepository.findAll(Sort.by("actif").descending().and(Sort.by("nom").ascending()).and(Sort.by("prenom").ascending()))
		);
        return "medecins/list";
    }

    @GetMapping("/rdv/list")
    public String listRendezVous(Authentication authentication, Model model) {
		if (isMedecinOnly(authentication)) {
			AppUser user = appUserRepository.findByUsername(authentication.getName()).orElseThrow();
			if (user.getMedecin() == null) {
				model.addAttribute("rdvs", java.util.List.of());
				return "rdv/list";
			}
			model.addAttribute("rdvs", rendezVousRepository.findByMedecin_IdOrderByDateHeureDesc(user.getMedecin().getId()));
			return "rdv/list";
		}
		model.addAttribute("rdvs", rendezVousRepository.findAll(Sort.by("dateHeure").descending()));
        return "rdv/list";
    }

	@PostMapping("/rdv/{id}/annuler")
	public String annulerRendezVous(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
		RendezVous rdv = rendezVousRepository.findById(id).orElse(null);
		if (rdv == null) {
			redirectAttributes.addFlashAttribute("error", "Rendez-vous introuvable.");
			return "redirect:/rdv/list";
		}
		if (isMedecinOnly(authentication)) {
			AppUser user = appUserRepository.findByUsername(authentication.getName()).orElseThrow();
			if (user.getMedecin() == null || rdv.getMedecin() == null || !rdv.getMedecin().getId().equals(user.getMedecin().getId())) {
				redirectAttributes.addFlashAttribute("error", "Rendez-vous introuvable.");
				return "redirect:/rdv/list";
			}
		}
		if (rdv.getStatut() == RendezVousStatut.ANNULE) {
			redirectAttributes.addFlashAttribute("info", "Ce rendez-vous est deja annule.");
			return "redirect:/rdv/list";
		}
		if (rdv.getStatut() == RendezVousStatut.TERMINE) {
			redirectAttributes.addFlashAttribute("info", "Un rendez-vous termine ne peut pas etre annule.");
			return "redirect:/rdv/list";
		}

		rdv.setStatut(RendezVousStatut.ANNULE);
		rendezVousRepository.save(rdv);

		creneauRepository.findByMedecin_IdAndDateHeure(rdv.getMedecin().getId(), rdv.getDateHeure())
				.ifPresent(creneau -> {
					creneau.setDisponible(true);
					creneauRepository.save(creneau);
				});

		redirectAttributes.addFlashAttribute("success", "Rendez-vous annule avec succes.");
		return "redirect:/rdv/list";
	}
    
    // Page d'accueil publique avant la connexion
    @GetMapping("/")
    public String index() {
        return "home";
    }

	private boolean isPatientOnly(Authentication authentication) {
		if (authentication == null) {
			return false;
		}
		boolean isPatient = authentication.getAuthorities().stream()
				.anyMatch(a -> "ROLE_PATIENT".equals(a.getAuthority()));
		boolean isStaff = authentication.getAuthorities().stream()
				.anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()) || "ROLE_SECRETAIRE".equals(a.getAuthority()) || "ROLE_MEDECIN".equals(a.getAuthority()));
		return isPatient && !isStaff;
	}

	private boolean isMedecinOnly(Authentication authentication) {
		if (authentication == null) {
			return false;
		}
		boolean isMedecin = authentication.getAuthorities().stream()
				.anyMatch(a -> "ROLE_MEDECIN".equals(a.getAuthority()));
		boolean isAdminOrSecretaire = authentication.getAuthorities().stream()
				.anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()) || "ROLE_SECRETAIRE".equals(a.getAuthority()));
		return isMedecin && !isAdminOrSecretaire;
	}
}
