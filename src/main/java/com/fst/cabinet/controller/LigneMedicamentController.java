package com.fst.cabinet.controller;

import com.fst.cabinet.entity.LigneMedicament;
import com.fst.cabinet.service.LigneMedicamentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lignes")
public class LigneMedicamentController {

    private final LigneMedicamentService service;

    public LigneMedicamentController(LigneMedicamentService service) {
        this.service = service;
    }

    @GetMapping
    public List<LigneMedicament> getAllLignes() {
        return service.getAllLignes();
    }

    @PostMapping
    public LigneMedicament addLigne(@RequestBody LigneMedicament ligne) {
        return service.creerLigne(ligne);
    }
}
