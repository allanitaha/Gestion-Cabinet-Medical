package com.gestion.cabinet;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.mock.web.MockMultipartFile;

import com.gestion.cabinet.domain.Creneau;
import com.gestion.cabinet.domain.Medecin;
import com.gestion.cabinet.domain.Ordonnance;
import com.gestion.cabinet.domain.Patient;
import com.gestion.cabinet.domain.RendezVous;
import com.gestion.cabinet.domain.RendezVousStatut;
import com.gestion.cabinet.repository.CreneauRepository;
import com.gestion.cabinet.repository.MedecinRepository;
import com.gestion.cabinet.repository.OrdonnanceRepository;
import com.gestion.cabinet.repository.PatientRepository;
import com.gestion.cabinet.repository.RendezVousRepository;
import com.gestion.cabinet.security.AppUser;
import com.gestion.cabinet.security.AppUserRepository;
import com.gestion.cabinet.security.UserRole;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
class WebPageRenderingTests {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private MedecinRepository medecinRepository;

	@Autowired
	private RendezVousRepository rendezVousRepository;

	@Autowired
	private CreneauRepository creneauRepository;

	@Autowired
	private AppUserRepository appUserRepository;

	@Autowired
	private OrdonnanceRepository ordonnanceRepository;

	@Test
	void loginPageRenders() throws Exception {
		mvc.perform(get("/login"))
				.andExpect(status().isOk())
				.andExpect(view().name("auth/login"));

		mvc.perform(get("/register"))
				.andExpect(status().isOk())
				.andExpect(view().name("auth/register"));
	}

	@Test
	@WithMockUser(username = "profile-user", roles = "PATIENT")
	void userCanUploadProfileImage() throws Exception {
		AppUser user = new AppUser();
		user.setUsername("profile-user");
		user.setPasswordHash("test");
		user.setRole(UserRole.PATIENT);
		user.setEnabled(true);
		appUserRepository.save(user);

		MockMultipartFile image = new MockMultipartFile(
				"image",
				"avatar.png",
				"image/png",
				new byte[] { 1, 2, 3, 4 }
		);

		mvc.perform(multipart("/profile/image").file(image).with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/profile"));

		AppUser updatedUser = appUserRepository.findByUsername("profile-user").orElseThrow();
		assertThat(updatedUser.getProfileImageFilename()).isNotBlank();

		mvc.perform(get("/profile"))
				.andExpect(status().isOk())
				.andExpect(view().name("profile/profile"));
	}

	@Test
	@WithMockUser(username = "profile-edit", roles = "PATIENT")
	void userCanEditAllowedProfileDetailsAndImageTogether() throws Exception {
		Patient patient = new Patient();
		patient.setCin("PROFILEEDIT001");
		patient.setNom("Old");
		patient.setPrenom("Name");
		patient = patientRepository.save(patient);

		AppUser user = new AppUser();
		user.setUsername("profile-edit");
		user.setPasswordHash("test");
		user.setRole(UserRole.PATIENT);
		user.setEnabled(true);
		user.setPatient(patient);
		appUserRepository.save(user);

		MockMultipartFile image = new MockMultipartFile(
				"image",
				"avatar.jpg",
				"image/jpeg",
				new byte[] { 5, 6, 7 }
		);

		mvc.perform(multipart("/profile")
						.file(image)
						.param("username", "profile-edited")
						.param("nom", "New")
						.param("prenom", "Patient")
						.param("telephone", "0611111111")
						.param("email", "new.patient@example.com")
						.param("dateNaissance", "1990-01-01")
						.param("antecedents", "Aucun")
						.with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/profile"));

		AppUser updatedUser = appUserRepository.findByUsername("profile-edited").orElseThrow();
		assertThat(updatedUser.getProfileImageFilename()).isNotBlank();
		assertThat(updatedUser.getPatient().getNom()).isEqualTo("Old");
		assertThat(updatedUser.getPatient().getTelephone()).isEqualTo("0611111111");
		assertThat(updatedUser.getPatient().getEmail()).isEqualTo("new.patient@example.com");
	}

	@Test
	void userCanAddProfileImageWhenRegistering() throws Exception {
		MockMultipartFile image = new MockMultipartFile(
				"profileImage",
				"register.png",
				"image/png",
				new byte[] { 9, 8, 7 }
		);

		mvc.perform(multipart("/register")
						.file(image)
						.param("username", "patient-image")
						.param("password", "patient123")
						.param("cin", "IMAGE12345")
						.param("nom", "Image")
						.param("prenom", "Patient")
						.with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/login?registered=1"));

		assertThat(appUserRepository.findByUsername("patient-image").orElseThrow().getProfileImageFilename())
				.isNotBlank();
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	void adminPagesRenderWithBackendData() throws Exception {
		mvc.perform(get("/dashboard"))
				.andExpect(status().isOk())
				.andExpect(view().name("dashboard"));

		mvc.perform(get("/choose"))
				.andExpect(status().isOk())
				.andExpect(view().name("choose"));

		mvc.perform(get("/admin"))
				.andExpect(status().isOk())
				.andExpect(view().name("admin/panel"));

		mvc.perform(get("/patients/list"))
				.andExpect(status().isOk())
				.andExpect(view().name("patients/list"));

		mvc.perform(get("/medecins/list"))
				.andExpect(status().isOk())
				.andExpect(view().name("medecins/list"));

		mvc.perform(get("/rdv/list"))
				.andExpect(status().isOk())
				.andExpect(view().name("rdv/list"));
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	void adminFormsPostToBackendControllers() throws Exception {
		mvc.perform(post("/admin/patients")
						.with(csrf())
						.param("username", "new-patient")
						.param("password", "patient123")
						.param("cin", "ZZ999999")
						.param("nom", "Test")
						.param("prenom", "Patient")
						.param("telephone", "0600000099")
						.param("email", "test.patient@example.com")
						.param("dateNaissance", "1999-01-02"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/admin/patients/new?created=1"));

		mvc.perform(post("/admin/medecins")
						.with(csrf())
						.param("nom", "Test")
						.param("prenom", "Medecin")
						.param("specialite", "Generaliste")
						.param("numeroOrdre", "ORD-TEST-999")
						.param("telephone", "0700000099")
						.param("email", "test.medecin@example.com")
						.param("actif", "true"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/admin/medecins/new?created=1"));

		mvc.perform(post("/admin/secretaires")
						.with(csrf())
						.param("username", "secretary-test")
						.param("password", "secret123"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/admin?tab=secretaires&created=1"));
	}

	@Test
	void patientCanCreateAccountWithOwnPassword() throws Exception {
		mvc.perform(post("/register")
						.with(csrf())
						.param("username", "patient-self")
						.param("password", "patient123")
						.param("cin", "SELF12345")
						.param("nom", "Self")
						.param("prenom", "Patient")
						.param("telephone", "0600000123")
						.param("email", "self.patient@example.com")
						.param("dateNaissance", "2001-03-04"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/login?registered=1"));
	}

	@Test
	void medecinCanRequestAccountAndMustWaitForAdminApproval() throws Exception {
		mvc.perform(post("/register")
						.with(csrf())
						.param("username", "doctor-pending")
						.param("password", "doctor123")
						.param("role", "MEDECIN")
						.param("nom", "Pending")
						.param("prenom", "Doctor")
						.param("specialite", "Cardiologie")
						.param("numeroOrdre", "ORD-PENDING-001")
						.param("telephone", "0700000123")
						.param("email", "pending.doctor@example.com"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/login?doctor_pending=1"));

		AppUser pendingUser = appUserRepository.findByUsername("doctor-pending").orElseThrow();
		assertThat(pendingUser.getRole()).isEqualTo(UserRole.MEDECIN);
		assertThat(pendingUser.isEnabled()).isFalse();
		assertThat(pendingUser.getMedecin()).isNotNull();
		assertThat(pendingUser.getMedecin().isActif()).isFalse();
	}

	@Test
	void medecinRegistrationReusesUnlinkedExistingProfile() throws Exception {
		Medecin orphanMedecin = new Medecin();
		orphanMedecin.setNom("Old");
		orphanMedecin.setPrenom("Profile");
		orphanMedecin.setNumeroOrdre("ORD-ORPHAN-001");
		orphanMedecin.setActif(false);
		orphanMedecin = medecinRepository.save(orphanMedecin);

		mvc.perform(post("/register")
						.with(csrf())
						.param("username", "doctor-orphan")
						.param("password", "doctor123")
						.param("role", "MEDECIN")
						.param("nom", "Recovered")
						.param("prenom", "Doctor")
						.param("specialite", "Dermatologie")
						.param("numeroOrdre", "ORD-ORPHAN-001")
						.param("email", "recovered.doctor@example.com"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/login?doctor_pending=1"));

		AppUser user = appUserRepository.findByUsername("doctor-orphan").orElseThrow();
		assertThat(user.getMedecin().getId()).isEqualTo(orphanMedecin.getId());
		assertThat(user.getMedecin().getNom()).isEqualTo("Recovered");
	}

	@Test
	void medecinRegistrationRejectsNumeroOrdreAlreadyLinkedToUser() throws Exception {
		Medecin medecin = new Medecin();
		medecin.setNom("Linked");
		medecin.setPrenom("Doctor");
		medecin.setNumeroOrdre("ORD-LINKED-001");
		medecin.setActif(true);
		medecin = medecinRepository.save(medecin);

		AppUser existingUser = new AppUser();
		existingUser.setUsername("doctor-linked");
		existingUser.setPasswordHash("test");
		existingUser.setRole(UserRole.MEDECIN);
		existingUser.setEnabled(true);
		existingUser.setMedecin(medecin);
		appUserRepository.save(existingUser);

		mvc.perform(post("/register")
						.with(csrf())
						.param("username", "doctor-linked-new")
						.param("password", "doctor123")
						.param("role", "MEDECIN")
						.param("nom", "Duplicate")
						.param("prenom", "Doctor")
						.param("numeroOrdre", "ORD-LINKED-001"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/register?error=numero_ordre_exists"));
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	void adminCannotCreateMedecinWithDuplicateNumeroOrdre() throws Exception {
		Medecin medecin = new Medecin();
		medecin.setNom("Admin");
		medecin.setPrenom("Duplicate");
		medecin.setNumeroOrdre("ORD-ADMIN-DUP-001");
		medecin.setActif(true);
		medecinRepository.save(medecin);

		mvc.perform(post("/admin/medecins")
						.with(csrf())
						.param("nom", "Other")
						.param("prenom", "Doctor")
						.param("numeroOrdre", "ORD-ADMIN-DUP-001"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/admin/medecins/new?error=numero_ordre_exists"));
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	void adminCanApprovePendingMedecinProfile() throws Exception {
		Medecin medecin = new Medecin();
		medecin.setNom("Approve");
		medecin.setPrenom("Doctor");
		medecin.setNumeroOrdre("ORD-APPROVE-001");
		medecin.setActif(false);
		medecin = medecinRepository.save(medecin);

		AppUser user = new AppUser();
		user.setUsername("doctor-approve");
		user.setPasswordHash("test");
		user.setRole(UserRole.MEDECIN);
		user.setEnabled(false);
		user.setMedecin(medecin);
		user = appUserRepository.save(user);

		mvc.perform(post("/admin/medecins/" + user.getId() + "/approve").with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/admin?tab=medecins&approved=1"));

		AppUser approvedUser = appUserRepository.findByUsername("doctor-approve").orElseThrow();
		assertThat(approvedUser.isEnabled()).isTrue();
		assertThat(medecinRepository.findById(medecin.getId()).orElseThrow().isActif()).isTrue();
	}

	@Test
	@WithMockUser(username = "secretaire", roles = "SECRETAIRE")
	void secretaireCanAccessOperationalPages() throws Exception {
		mvc.perform(get("/choose"))
				.andExpect(status().isOk())
				.andExpect(view().name("choose"));

		mvc.perform(get("/dashboard"))
				.andExpect(status().isOk())
				.andExpect(view().name("dashboard"));

		mvc.perform(get("/patients/list"))
				.andExpect(status().isOk())
				.andExpect(view().name("patients/list"));

		mvc.perform(get("/rdv/list"))
				.andExpect(status().isOk())
				.andExpect(view().name("rdv/list"));
	}

	@Test
	@WithMockUser(username = "patient", roles = "PATIENT")
	void patientRdvPageRenders() throws Exception {
		mvc.perform(get("/patient/rdv/new"))
				.andExpect(status().isOk())
				.andExpect(view().name("patient/rdv-new"));
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	void adminCanCancelRendezVousAndReleaseSlot() throws Exception {
		Medecin medecin = new Medecin();
		medecin.setNom("Cancel");
		medecin.setPrenom("Doctor");
		medecin.setNumeroOrdre("ORD-CANCEL-001");
		medecin.setActif(true);
		medecin = medecinRepository.save(medecin);

		Patient patient = new Patient();
		patient.setCin("CANCEL001");
		patient.setNom("Cancel");
		patient.setPrenom("Patient");
		patient = patientRepository.save(patient);

		LocalDateTime dateHeure = LocalDateTime.now().plusDays(3).withSecond(0).withNano(0);
		Creneau creneau = new Creneau();
		creneau.setMedecin(medecin);
		creneau.setDateHeure(dateHeure);
		creneau.setDureeMinutes(30);
		creneau.setDisponible(false);
		creneau = creneauRepository.save(creneau);

		RendezVous rdv = new RendezVous();
		rdv.setPatient(patient);
		rdv.setMedecin(medecin);
		rdv.setDateHeure(dateHeure);
		rdv.setDureeMinutes(30);
		rdv.setMotif("Annulation test");
		rdv.setStatut(RendezVousStatut.PLANIFIE);
		rdv = rendezVousRepository.save(rdv);

		mvc.perform(post("/rdv/" + rdv.getId() + "/annuler").with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/rdv/list"));

		assertThat(rendezVousRepository.findById(rdv.getId()).orElseThrow().getStatut())
				.isEqualTo(RendezVousStatut.ANNULE);
		assertThat(creneauRepository.findById(creneau.getId()).orElseThrow().isDisponible())
				.isTrue();
	}

	@Test
	@WithMockUser(username = "patient-cancel-ok", roles = "PATIENT")
	void patientCanCancelOwnRendezVousAtLeastOneDayBefore() throws Exception {
		TestRendezVousData data = createPatientRendezVous("patient-cancel-ok", "PCANCEL001", "ORD-PCANCEL-001", LocalDateTime.now().plusDays(2));

		mvc.perform(post("/patient/rdv/" + data.rendezVous.getId() + "/annuler").with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/patient/rdv/new"));

		assertThat(rendezVousRepository.findById(data.rendezVous.getId()).orElseThrow().getStatut())
				.isEqualTo(RendezVousStatut.ANNULE);
		assertThat(creneauRepository.findById(data.creneau.getId()).orElseThrow().isDisponible())
				.isTrue();
	}

	@Test
	@WithMockUser(username = "patient-cancel-late", roles = "PATIENT")
	void patientCannotCancelOwnRendezVousLessThanOneDayBefore() throws Exception {
		TestRendezVousData data = createPatientRendezVous("patient-cancel-late", "PCANCEL002", "ORD-PCANCEL-002", LocalDateTime.now().plusHours(23));

		mvc.perform(post("/patient/rdv/" + data.rendezVous.getId() + "/annuler").with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/patient/rdv/new"));

		assertThat(rendezVousRepository.findById(data.rendezVous.getId()).orElseThrow().getStatut())
				.isEqualTo(RendezVousStatut.PLANIFIE);
		assertThat(creneauRepository.findById(data.creneau.getId()).orElseThrow().isDisponible())
				.isFalse();
	}

	@Test
	@WithMockUser(username = "doctor-prescription", roles = "MEDECIN")
	void medecinCanWriteOrdonnanceForRendezVous() throws Exception {
		TestRendezVousData data = createRendezVousWithUsers(
				"patient-prescription",
				"doctor-prescription",
				"ORD-PRESCRIPTION-001",
				"PRESC001",
				LocalDateTime.now().minusHours(2)
		);

		mvc.perform(post("/medecin/ordonnances")
						.with(csrf())
						.param("rdvId", data.rendezVous.getId().toString())
						.param("observations", "Repos et hydratation")
						.param("nomMedicament", "Paracetamol")
						.param("posologie", "1 comprime matin et soir")
						.param("duree", "5 jours"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/medecin/ordonnances"));

		assertThat(ordonnanceRepository.findByRendezVous_Id(data.rendezVous.getId()))
				.isPresent()
				.get()
				.satisfies(ordonnance -> {
					assertThat(ordonnance.getObservations()).isEqualTo("Repos et hydratation");
					assertThat(ordonnance.getLignes()).hasSize(1);
				});
	}

	@Test
	@WithMockUser(username = "doctor-own-rdv", roles = "MEDECIN")
	void medecinRdvListShowsOnlyOwnConsultations() throws Exception {
		TestRendezVousData ownData = createRendezVousWithUsers(
				"patient-own-rdv",
				"doctor-own-rdv",
				"ORD-OWN-RDV-001",
				"OWNRDV001",
				LocalDateTime.now().plusDays(2)
		);
		TestRendezVousData otherData = createRendezVousWithUsers(
				"patient-other-rdv",
				"doctor-other-rdv",
				"ORD-OTHER-RDV-001",
				"OTHERRDV001",
				LocalDateTime.now().plusDays(3)
		);

		MvcResult result = mvc.perform(get("/rdv/list"))
				.andExpect(status().isOk())
				.andExpect(view().name("rdv/list"))
				.andReturn();

		@SuppressWarnings("unchecked")
		java.util.List<RendezVous> rdvs = (java.util.List<RendezVous>) result.getModelAndView().getModel().get("rdvs");
		assertThat(rdvs).extracting(RendezVous::getId).contains(ownData.rendezVous.getId());
		assertThat(rdvs).extracting(RendezVous::getId).doesNotContain(otherData.rendezVous.getId());
	}

	@Test
	@WithMockUser(username = "doctor-creneau", roles = "MEDECIN")
	void medecinCanCreateOwnCreneau() throws Exception {
		Medecin medecin = new Medecin();
		medecin.setNom("Creneau");
		medecin.setPrenom("Doctor");
		medecin.setNumeroOrdre("ORD-CRENEAU-001");
		medecin.setActif(true);
		medecin = medecinRepository.save(medecin);

		AppUser user = new AppUser();
		user.setUsername("doctor-creneau");
		user.setPasswordHash("test");
		user.setRole(UserRole.MEDECIN);
		user.setEnabled(true);
		user.setMedecin(medecin);
		appUserRepository.save(user);

		LocalDateTime dateHeure = LocalDateTime.now().plusDays(5).withHour(14).withMinute(0).withSecond(0).withNano(0);
		mvc.perform(post("/medecin/creneaux")
						.with(csrf())
						.param("dateHeure", dateHeure.toString())
						.param("dureeMinutes", "45"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/medecin/creneaux"));

		Creneau creneau = creneauRepository.findByMedecin_IdAndDateHeure(medecin.getId(), dateHeure).orElseThrow();
		assertThat(creneau.getDureeMinutes()).isEqualTo(45);
		assertThat(creneau.isDisponible()).isTrue();
	}

	@Test
	@WithMockUser(username = "patient-view-prescription", roles = "PATIENT")
	void patientCanViewOwnOrdonnance() throws Exception {
		TestRendezVousData data = createRendezVousWithUsers(
				"patient-view-prescription",
				"doctor-view-prescription",
				"ORD-PRESCRIPTION-002",
				"PRESC002",
				LocalDateTime.now().minusHours(2)
		);
		Ordonnance ordonnance = createOrdonnance(data.rendezVous);

		mvc.perform(get("/patient/ordonnances"))
				.andExpect(status().isOk())
				.andExpect(view().name("patient/ordonnances"));

		mvc.perform(get("/patient/ordonnances/" + ordonnance.getId()))
				.andExpect(status().isOk())
				.andExpect(view().name("patient/ordonnance-detail"));
	}

	private TestRendezVousData createPatientRendezVous(String username, String cin, String numeroOrdre, LocalDateTime dateHeure) {
		return createRendezVousWithUsers(username, null, numeroOrdre, cin, dateHeure);
	}

	private TestRendezVousData createRendezVousWithUsers(String patientUsername, String medecinUsername, String numeroOrdre, String cin, LocalDateTime dateHeure) {
		Medecin medecin = new Medecin();
		medecin.setNom("Patient");
		medecin.setPrenom("Doctor");
		medecin.setNumeroOrdre(numeroOrdre);
		medecin.setActif(true);
		medecin = medecinRepository.save(medecin);

		Patient patient = new Patient();
		patient.setCin(cin);
		patient.setNom("Patient");
		patient.setPrenom("Cancel");
		patient = patientRepository.save(patient);

		AppUser user = new AppUser();
		user.setUsername(patientUsername);
		user.setPasswordHash("test");
		user.setRole(UserRole.PATIENT);
		user.setPatient(patient);
		appUserRepository.save(user);

		if (medecinUsername != null) {
			AppUser medecinUser = new AppUser();
			medecinUser.setUsername(medecinUsername);
			medecinUser.setPasswordHash("test");
			medecinUser.setRole(UserRole.MEDECIN);
			medecinUser.setMedecin(medecin);
			appUserRepository.save(medecinUser);
		}

		LocalDateTime normalizedDateHeure = dateHeure.withSecond(0).withNano(0);
		Creneau creneau = new Creneau();
		creneau.setMedecin(medecin);
		creneau.setDateHeure(normalizedDateHeure);
		creneau.setDureeMinutes(30);
		creneau.setDisponible(false);
		creneau = creneauRepository.save(creneau);

		RendezVous rdv = new RendezVous();
		rdv.setPatient(patient);
		rdv.setMedecin(medecin);
		rdv.setDateHeure(normalizedDateHeure);
		rdv.setDureeMinutes(30);
		rdv.setMotif("Annulation patient");
		rdv.setStatut(RendezVousStatut.PLANIFIE);
		rdv = rendezVousRepository.save(rdv);

		return new TestRendezVousData(rdv, creneau);
	}

	private Ordonnance createOrdonnance(RendezVous rdv) {
		Ordonnance ordonnance = new Ordonnance();
		ordonnance.setRendezVous(rdv);
		ordonnance.setDateEmission(java.time.LocalDate.now());
		ordonnance.setObservations("Controle dans une semaine");

		com.gestion.cabinet.domain.LigneMedicament ligne = new com.gestion.cabinet.domain.LigneMedicament();
		ligne.setNomMedicament("Amoxicilline");
		ligne.setPosologie("1 gelule matin et soir");
		ligne.setDuree("7 jours");
		ligne.setOrdonnance(ordonnance);
		ordonnance.getLignes().add(ligne);

		return ordonnanceRepository.save(ordonnance);
	}

	private record TestRendezVousData(RendezVous rendezVous, Creneau creneau) {
	}
}
