package com.fst.cabinet.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class LigneMedicament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomMedicament;
    private String posologie;
    private String duree;

    @ManyToOne
    @JoinColumn(name = "ordonnance_id")
    private Ordonnance ordonnance;
}
