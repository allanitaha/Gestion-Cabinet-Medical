package com.fst.cabinet.controller;

import com.fst.cabinet.entity.Secretaire;
import com.fst.cabinet.service.SecretaireService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/secretaires")
public class SecretaireController {

    private final SecretaireService secretaireService;

    public SecretaireController(SecretaireService secretaireService) {
        this.secretaireService = secretaireService;
    }

    // --- Liste des secrétaires ---
    @GetMapping("/list")
    public String listSecretaires(Model model) {
        List<Secretaire> secretaires = secretaireService.findAll();
        model.addAttribute("secretaires", secretaires);
        return "secretaires-list"; // nom de ta vue Thymeleaf
    }

    // --- Formulaire ajout ---
    @GetMapping("/new")
    public String newSecretaireForm(Model model) {
        model.addAttribute("secretaire", new Secretaire());
        return "secretaire-form"; // vue Thymeleaf pour formulaire
    }

    // --- Sauvegarde ---
    @PostMapping("/save")
    public String saveSecretaire(@ModelAttribute Secretaire secretaire) {
        secretaireService.save(secretaire);
        return "redirect:/secretaires/list";
    }

    // --- Formulaire édition ---
    @GetMapping("/edit/{id}")
    public String editSecretaire(@PathVariable Long id, Model model) {
        Secretaire secretaire = secretaireService.findById(id);
        model.addAttribute("secretaire", secretaire);
        return "secretaire-form";
    }

    // --- Mise à jour ---
    @PostMapping("/update/{id}")
    public String updateSecretaire(@PathVariable Long id, @ModelAttribute Secretaire secretaireDetails) {
        Secretaire secretaire = secretaireService.findById(id);
        if (secretaire != null) {
            secretaire.setNom(secretaireDetails.getNom());
            secretaire.setPrenom(secretaireDetails.getPrenom());
            secretaire.setTelephone(secretaireDetails.getTelephone());
            secretaire.setEmail(secretaireDetails.getEmail());
            secretaireService.save(secretaire);
        }
        return "redirect:/secretaires/list";
    }

    // --- Suppression ---
    @GetMapping("/delete/{id}")
    public String deleteSecretaire(@PathVariable Long id) {
        secretaireService.delete(id);
        return "redirect:/secretaires/list";
    }
}
