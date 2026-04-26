package com.fst.cabinet.repository;

import com.fst.cabinet.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;   // <-- IMPORT OBLIGATOIRE

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);
}
