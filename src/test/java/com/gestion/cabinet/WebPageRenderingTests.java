package com.gestion.cabinet;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class WebPageRenderingTests {

	@Autowired
	private MockMvc mvc;

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
}
