package com.fst.cabinet.controller;

import com.fst.cabinet.entity.Patient;
import com.fst.cabinet.service.PatientService;
import com.fst.cabinet.service.RendezVousService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/patients")
public class PatientController {

    private final PatientService    patientService;
    private final RendezVousService rdvService;

    public PatientController(PatientService patientService,
                             RendezVousService rdvService) {
        this.patientService = patientService;
        this.rdvService     = rdvService;
    }

    // ── Liste avec recherche ─────────────────────────────────
    @GetMapping
    public String liste(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("patients", patientService.search(q));
        model.addAttribute("q", q);
        return "patients/liste";
    }

    // ── Fiche patient complète ───────────────────────────────
    @GetMapping("/{id}")
    public String fiche(@PathVariable Long id, Model model) {
        model.addAttribute("patient",    patientService.findById(id));
        model.addAttribute("rendezvous", rdvService.findByPatient(id));
        return "patients/fiche";
    }

    // ── Formulaire nouveau ───────────────────────────────────
    @GetMapping("/nouveau")
    public String nouveauForm(Model model) {
        model.addAttribute("patient", new Patient());
        return "patients/form";
    }

    // ── Formulaire modifier ──────────────────────────────────
    @GetMapping("/modifier/{id}")
    public String modifierForm(@PathVariable Long id, Model model) {
        model.addAttribute("patient", patientService.findById(id));
        return "patients/form";
    }

    // ── Enregistrer (nouveau ou modifier) ────────────────────
    @PostMapping("/enregistrer")
    public String enregistrer(@ModelAttribute Patient patient,
                              RedirectAttributes ra) {
        try {
            if (patient.getId() == null) {
                patientService.save(patient);
                ra.addFlashAttribute("succes", "Patient ajouté avec succès.");
            } else {
                patientService.update(patient.getId(), patient);
                ra.addFlashAttribute("succes", "Patient modifié avec succès.");
            }
        } catch (RuntimeException e) {
            ra.addFlashAttribute("erreur", e.getMessage());
            return "redirect:/patients/" +
                    (patient.getId() == null ? "nouveau" : "modifier/" + patient.getId());
        }
        return "redirect:/patients";
    }

    // ── Supprimer ────────────────────────────────────────────
    @GetMapping("/supprimer/{id}")
    public String supprimer(@PathVariable Long id, RedirectAttributes ra) {
        try {
            patientService.delete(id);
            ra.addFlashAttribute("succes", "Patient supprimé.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/patients";
    }
}