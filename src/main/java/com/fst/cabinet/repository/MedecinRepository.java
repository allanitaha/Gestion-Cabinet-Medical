package com.fst.cabinet.repository;

import com.fst.cabinet.entity.Medecin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedecinRepository extends JpaRepository<Medecin, Long> {
    List<Medecin> findByActifTrue();
}
