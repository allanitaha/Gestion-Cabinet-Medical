package com.fst.cabinet.controller;

import com.fst.cabinet.service.MedecinService;
import com.fst.cabinet.service.PatientService;
import com.fst.cabinet.service.RendezVousService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final RendezVousService rdvService;
    private final PatientService    patientService;
    private final MedecinService    medecinService;

    public DashboardController(RendezVousService rdvService,
                               PatientService patientService,
                               MedecinService medecinService) {
        this.rdvService     = rdvService;
        this.patientService = patientService;
        this.medecinService = medecinService;
    }

    // Redirection racine → dashboard
    @GetMapping("/")
    public String root() { return "redirect:/dashboard"; }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("rdvDuJour",       rdvService.rdvDuJour());
        model.addAttribute("rdvDeLaSemaine",  rdvService.rdvDeLaSemaine());
        model.addAttribute("enAttente",       rdvService.patientsEnAttente());
        model.addAttribute("nbPatients",      patientService.findAll().size());
        model.addAttribute("nbMedecins",      medecinService.findActifs().size());
        return "dashboard/index";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }
}