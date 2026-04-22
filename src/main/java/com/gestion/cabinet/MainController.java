package com.gestion.cabinet;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

	@GetMapping("/dashboard")
	public String dashboard() {
	    System.out.println("Le contrôleur est bien appelé !"); // Cela s'affichera dans la console
	    return "dashboard"; 
	}

    // Préparation pour la page patients (à créer ensuite)
    @GetMapping("/patients/list")
    public String listPatients() {
        return "patients/list"; 
    }

    @GetMapping("/medecins/list")
    public String listMedecins() {
        return "medecins/list";
    }

    @GetMapping("/rdv/list")
    public String listRendezVous() {
        return "rdv/list";
    }
    
    // Redirection automatique de la racine vers le dashboard
    @GetMapping("/")
    public String index() {
        return "redirect:/dashboard";
    }
}