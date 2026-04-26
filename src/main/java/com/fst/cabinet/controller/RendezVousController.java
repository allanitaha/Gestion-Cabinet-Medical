package com.fst.cabinet.controller;

import com.fst.cabinet.entity.RendezVous;
import com.fst.cabinet.entity.StatutRDV;   // <-- import manquant
import com.fst.cabinet.service.RendezVousService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/rendezvous")
public class RendezVousController {

    private final RendezVousService rdvService;

    public RendezVousController(RendezVousService rdvService) {
        this.rdvService = rdvService;
    }

    @GetMapping("/new")
    public String newRdvForm(Model model) {
        model.addAttribute("rendezvous", new RendezVous());
        model.addAttribute("statuts", StatutRDV.values()); // fonctionne maintenant
        return "rendezvous-form";
    }

    @PostMapping("/save")
    public String saveRdv(@ModelAttribute RendezVous rdv) {
        rdvService.save(rdv);
        return "redirect:/rendezvous/list";
    }

    @GetMapping("/edit/{id}")
    public String editRdv(@PathVariable Long id, Model model) {
        RendezVous rdv = rdvService.findById(id);
        model.addAttribute("rendezvous", rdv);
        model.addAttribute("statuts", StatutRDV.values()); // fonctionne maintenant
        return "rendezvous-form";
    }

    @PostMapping("/update")
    public String updateRdv(@ModelAttribute RendezVous rdv) {
        rdvService.update(rdv.getId(), rdv);
        return "redirect:/rendezvous/list";
    }

    @PostMapping("/annuler/{id}")
    public String annulerRdv(@PathVariable Long id) {
        rdvService.changerStatut(id, StatutRDV.ANNULE); // fonctionne maintenant
        return "redirect:/rendezvous/list";
    }
}
