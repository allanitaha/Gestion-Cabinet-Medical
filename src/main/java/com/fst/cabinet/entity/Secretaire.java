package com.fst.cabinet.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Secretaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String prenom;

    private String telephone;   // <-- champ manquant
    private String email;       // <-- champ manquant

    @OneToMany(mappedBy = "secretaire")
    private List<RendezVous> rendezVousList;

    // --- Getters et setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public List<RendezVous> getRendezVousList() { return rendezVousList; }
    public void setRendezVousList(List<RendezVous> rendezVousList) { this.rendezVousList = rendezVousList; }
}
