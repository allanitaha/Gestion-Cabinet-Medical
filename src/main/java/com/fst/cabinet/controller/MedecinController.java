package com.fst.cabinet.controller;

import com.fst.cabinet.entity.Medecin;
import com.fst.cabinet.service.MedecinService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/medecins")
public class MedecinController {

    private final MedecinService medecinService;

    public MedecinController(MedecinService medecinService) {
        this.medecinService = medecinService;
    }

    @GetMapping
    public String liste(Model model) {
        model.addAttribute("medecins", medecinService.findAll());
        return "medecins/liste";
    }

    @GetMapping("/nouveau")
    public String nouveauForm(Model model) {
        model.addAttribute("medecin", new Medecin());
        return "medecins/form";
    }

    @GetMapping("/modifier/{id}")
    public String modifierForm(@PathVariable Long id, Model model) {
        model.addAttribute("medecin", medecinService.findById(id));
        return "medecins/form";
    }

    @PostMapping("/enregistrer")
    public String enregistrer(@ModelAttribute Medecin medecin, RedirectAttributes ra) {
        try {
            if (medecin.getId() == null) {
                medecinService.save(medecin);
                ra.addFlashAttribute("succes", "Médecin ajouté avec succès.");
            } else {
                medecinService.update(medecin.getId(), medecin);
                ra.addFlashAttribute("succes", "Médecin modifié avec succès.");
            }
        } catch (RuntimeException e) {
            ra.addFlashAttribute("erreur", e.getMessage());
            return "redirect:/medecins/" +
                    (medecin.getId() == null ? "nouveau" : "modifier/" + medecin.getId());
        }
        return "redirect:/medecins";
    }

    @GetMapping("/supprimer/{id}")
    public String supprimer(@PathVariable Long id, RedirectAttributes ra) {
        try {
            medecinService.delete(id);
            ra.addFlashAttribute("succes", "Médecin désactivé.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/medecins";
    }
}
