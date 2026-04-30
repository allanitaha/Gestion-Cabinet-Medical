package com.gestion.cabinet;

import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.gestion.cabinet.repository.MedecinRepository;
import com.gestion.cabinet.repository.PatientRepository;
import com.gestion.cabinet.repository.RendezVousRepository;

@Controller
public class MainController {

	private final PatientRepository patientRepository;
	private final MedecinRepository medecinRepository;
	private final RendezVousRepository rendezVousRepository;

	public MainController(
			PatientRepository patientRepository,
			MedecinRepository medecinRepository,
			RendezVousRepository rendezVousRepository
	) {
		this.patientRepository = patientRepository;
		this.medecinRepository = medecinRepository;
		this.rendezVousRepository = rendezVousRepository;
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

		model.addAttribute("isAdmin", isAdmin);
		model.addAttribute("isPatient", isPatient);
		model.addAttribute("isSecretaire", isSecretaire);
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
    public String listRendezVous(Model model) {
		model.addAttribute("rdvs", rendezVousRepository.findAll(Sort.by("dateHeure").descending()));
        return "rdv/list";
    }
    
    // Redirection automatique de la racine vers le dashboard
    @GetMapping("/")
    public String index(Authentication authentication) {
		if (isPatientOnly(authentication)) {
			return "redirect:/patient/rdv/new";
		}
        return "redirect:/dashboard";
    }

	private boolean isPatientOnly(Authentication authentication) {
		if (authentication == null) {
			return false;
		}
		boolean isPatient = authentication.getAuthorities().stream()
				.anyMatch(a -> "ROLE_PATIENT".equals(a.getAuthority()));
		boolean isStaff = authentication.getAuthorities().stream()
				.anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()) || "ROLE_SECRETAIRE".equals(a.getAuthority()));
		return isPatient && !isStaff;
	}
}
