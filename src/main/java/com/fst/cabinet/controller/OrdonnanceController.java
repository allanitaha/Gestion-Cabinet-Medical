package com.fst.cabinet.controller;

import com.fst.cabinet.entity.LigneMedicament;
import com.fst.cabinet.entity.Ordonnance;
import com.fst.cabinet.service.OrdonnanceService;
import com.fst.cabinet.service.RendezVousService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/ordonnances")
public class OrdonnanceController {

    private final OrdonnanceService ordService;
    private final RendezVousService rdvService;

    public OrdonnanceController(OrdonnanceService ordService,
                                RendezVousService rdvService) {
        this.ordService = ordService;
        this.rdvService = rdvService;
    }

    @GetMapping
    public String liste(Model model) {
        model.addAttribute("ordonnances", ordService.findAll());
        return "ordonnances/liste";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("ordonnance", ordService.findById(id));
        return "ordonnances/detail";
    }

    // Formulaire de création lié à un RDV
    @GetMapping("/nouveau/{rdvId}")
    public String nouveauForm(@PathVariable Long rdvId, Model model) {
        model.addAttribute("rdv",          rdvService.findById(rdvId));
        model.addAttribute("ordonnance",   new Ordonnance());
        model.addAttribute("lignes",       new ArrayList<LigneMedicament>());
        return "ordonnances/form";
    }

    @PostMapping("/enregistrer")
    public String enregistrer(@RequestParam Long rdvId,
                              @RequestParam String observations,
                              @RequestParam List<String> nomMedicament,
                              @RequestParam List<String> posologie,
                              @RequestParam List<String> duree,
                              RedirectAttributes ra) {
        try {
            List<LigneMedicament> lignes = new ArrayList<>();
            for (int i = 0; i < nomMedicament.size(); i++) {
                LigneMedicament l = new LigneMedicament();
                l.setNomMedicament(nomMedicament.get(i));
                l.setPosologie(posologie.get(i));
                l.setDuree(duree.get(i));
                lignes.add(l);
            }
            ordService.creer(rdvId, observations, lignes);
            ra.addFlashAttribute("succes", "Ordonnance créée avec succès.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("erreur", e.getMessage());
            return "redirect:/ordonnances/nouveau/" + rdvId;
        }
        return "redirect:/ordonnances";
    }

    @GetMapping("/supprimer/{id}")
    public String supprimer(@PathVariable Long id, RedirectAttributes ra) {
        ordService.delete(id);
        ra.addFlashAttribute("succes", "Ordonnance supprimée.");
        return "redirect:/ordonnances";
    }
}