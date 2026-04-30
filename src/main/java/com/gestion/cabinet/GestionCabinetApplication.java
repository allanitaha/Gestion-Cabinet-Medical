package com.gestion.cabinet;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.gestion.cabinet.domain.Creneau;
import com.gestion.cabinet.domain.Medecin;
import com.gestion.cabinet.domain.Patient;
import com.gestion.cabinet.repository.CreneauRepository;
import com.gestion.cabinet.repository.MedecinRepository;
import com.gestion.cabinet.repository.PatientRepository;
import com.gestion.cabinet.security.AppUser;
import com.gestion.cabinet.security.AppUserRepository;
import com.gestion.cabinet.security.UserRole;

@SpringBootApplication
public class GestionCabinetApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestionCabinetApplication.class, args);
	}

	@Bean
	CommandLineRunner seedData(
			AppUserRepository users,
			PatientRepository patients,
			MedecinRepository medecins,
			CreneauRepository creneaux,
			PasswordEncoder encoder
	) {
		return args -> {
			if (!users.existsByUsername("admin")) {
				AppUser admin = new AppUser();
				admin.setUsername("admin");
				admin.setPasswordHash(encoder.encode("admin123"));
				admin.setRole(UserRole.ADMIN);
				admin.setEnabled(true);
				users.save(admin);
			}

			if (!users.existsByUsername("secretaire")) {
				AppUser secretaire = new AppUser();
				secretaire.setUsername("secretaire");
				secretaire.setPasswordHash(encoder.encode("secretaire123"));
				secretaire.setRole(UserRole.SECRETAIRE);
				secretaire.setEnabled(true);
				users.save(secretaire);
			}

			if (patients.findAll().stream().noneMatch(p -> "AA123456".equals(p.getCin()))) {
				Patient p1 = new Patient();
				p1.setCin("AA123456");
				p1.setNom("Benali");
				p1.setPrenom("Sara");
				p1.setDateNaissance(LocalDate.of(1994, 5, 14));
				p1.setTelephone("0600000001");
				p1.setEmail("sara.benali@example.com");
				p1.setAntecedents("Allergie légère aux pénicillines");
				patients.save(p1);
			}

			if (patients.findAll().stream().noneMatch(p -> "BB223344".equals(p.getCin()))) {
				Patient p2 = new Patient();
				p2.setCin("BB223344");
				p2.setNom("El Idrissi");
				p2.setPrenom("Yassine");
				p2.setDateNaissance(LocalDate.of(1988, 11, 3));
				p2.setTelephone("0600000002");
				p2.setEmail("yassine.elidrissi@example.com");
				p2.setAntecedents("Hypertension suivie");
				patients.save(p2);
			}

			if (patients.findAll().stream().noneMatch(p -> "CC556677".equals(p.getCin()))) {
				Patient p3 = new Patient();
				p3.setCin("CC556677");
				p3.setNom("Khaldi");
				p3.setPrenom("Meriem");
				p3.setDateNaissance(LocalDate.of(2000, 2, 20));
				p3.setTelephone("0600000003");
				p3.setEmail("meriem.khaldi@example.com");
				p3.setAntecedents("Aucun antécédent majeur");
				patients.save(p3);
			}

			if (medecins.findAll().stream().noneMatch(m -> "ORD-1001".equals(m.getNumeroOrdre()))) {
				Medecin m1 = new Medecin();
				m1.setNom("Amrani");
				m1.setPrenom("Hicham");
				m1.setSpecialite("Cardiologie");
				m1.setNumeroOrdre("ORD-1001");
				m1.setTelephone("0700000001");
				m1.setEmail("hicham.amrani@clinique.ma");
				m1.setActif(true);
				medecins.save(m1);
			}

			if (medecins.findAll().stream().noneMatch(m -> "ORD-1002".equals(m.getNumeroOrdre()))) {
				Medecin m2 = new Medecin();
				m2.setNom("Lahlou");
				m2.setPrenom("Nadia");
				m2.setSpecialite("Médecine Générale");
				m2.setNumeroOrdre("ORD-1002");
				m2.setTelephone("0700000002");
				m2.setEmail("nadia.lahlou@clinique.ma");
				m2.setActif(true);
				medecins.save(m2);
			}

			if (creneaux.count() == 0 && medecins.count() > 0) {
				LocalDateTime base = LocalDateTime.now().plusDays(1).withHour(9).withMinute(0).withSecond(0).withNano(0);
				for (Medecin medecin : medecins.findAll()) {
					for (int i = 0; i < 4; i++) {
						Creneau creneau = new Creneau();
						creneau.setMedecin(medecin);
						creneau.setDateHeure(base.plusDays(i).plusHours(medecin.getId() % 2));
						creneau.setDureeMinutes(30);
						creneau.setDisponible(true);
						creneaux.save(creneau);
					}
				}
			}

			if (!users.existsByUsername("patient")) {
				Patient freePatient = patients.findAll().stream()
						.filter(p -> p.getId() != null && !users.existsByPatient_Id(p.getId()))
						.findFirst()
						.orElse(null);
				if (freePatient != null) {
					AppUser patientUser = new AppUser();
					patientUser.setUsername("patient");
					patientUser.setPasswordHash(encoder.encode("patient123"));
					patientUser.setRole(UserRole.PATIENT);
					patientUser.setEnabled(true);
					patientUser.setPatient(freePatient);
					users.save(patientUser);
				}
			}
		};
	}
}
