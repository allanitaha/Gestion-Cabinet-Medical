package com.gestion.cabinet;

import java.io.IOException;
import java.nio.file.Files;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gestion.cabinet.domain.Medecin;
import com.gestion.cabinet.domain.Patient;
import com.gestion.cabinet.repository.MedecinRepository;
import com.gestion.cabinet.repository.PatientRepository;
import com.gestion.cabinet.security.AppUser;
import com.gestion.cabinet.security.AppUserRepository;

@Controller
@RequestMapping("/profile")
public class ProfileController {

	private final AppUserRepository users;
	private final PatientRepository patients;
	private final MedecinRepository medecins;
	private final PasswordEncoder passwordEncoder;
	private final ProfileImageService profileImageService;

	public ProfileController(
			AppUserRepository users,
			PatientRepository patients,
			MedecinRepository medecins,
			PasswordEncoder passwordEncoder,
			ProfileImageService profileImageService
	) {
		this.users = users;
		this.patients = patients;
		this.medecins = medecins;
		this.passwordEncoder = passwordEncoder;
		this.profileImageService = profileImageService;
	}

	@GetMapping
	public String profile(Authentication auth, Model model) {
		model.addAttribute("user", currentUser(auth));
		return "profile/profile";
	}

	@PostMapping
	public String updateProfile(
			Authentication auth,
			@RequestParam String username,
			@RequestParam(required = false) String password,
			@RequestParam(required = false) String telephone,
			@RequestParam(required = false) String email,
			@RequestParam(required = false) MultipartFile image,
			RedirectAttributes redirectAttributes
	) throws IOException {
		AppUser user = currentUser(auth);
		String normalizedUsername = username.trim();
		boolean usernameChanged = !user.getUsername().equals(normalizedUsername);
		if (normalizedUsername.isBlank()) {
			redirectAttributes.addFlashAttribute("errorMessage", "Le nom d'utilisateur est obligatoire.");
			return "redirect:/profile";
		}
		if (usernameChanged && users.existsByUsername(normalizedUsername)) {
			redirectAttributes.addFlashAttribute("errorMessage", "Ce nom d'utilisateur existe deja.");
			return "redirect:/profile";
		}

		user.setUsername(normalizedUsername);
		if (password != null && !password.isBlank()) {
			user.setPasswordHash(passwordEncoder.encode(password));
		}
		try {
			String filename = profileImageService.save(image);
			if (filename != null) {
				user.setProfileImageFilename(filename);
			}
		} catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
			return "redirect:/profile";
		}

		updateEditableContactInfo(user, telephone, email);
		users.save(user);
		if (usernameChanged) {
			refreshAuthentication(user, auth);
		}

		redirectAttributes.addFlashAttribute("successMessage", "Profil mis a jour.");
		return "redirect:/profile";
	}

	@PostMapping("/image")
	public String uploadImage(
			Authentication auth,
			@RequestParam("image") MultipartFile image,
			RedirectAttributes redirectAttributes
	) throws IOException {
		if (image == null || image.isEmpty()) {
			redirectAttributes.addFlashAttribute("errorMessage", "Choisissez une image.");
			return "redirect:/profile";
		}
		String filename;
		try {
			filename = profileImageService.save(image);
		} catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
			return "redirect:/profile";
		}

		AppUser user = currentUser(auth);
		user.setProfileImageFilename(filename);
		users.save(user);

		redirectAttributes.addFlashAttribute("successMessage", "Image de profil mise a jour.");
		return "redirect:/profile";
	}

	@GetMapping("/image/{filename}")
	public ResponseEntity<Resource> image(@PathVariable String filename) throws IOException {
		var imagePath = profileImageService.resolve(filename);
		if (!imagePath.startsWith(profileImageService.uploadDirectory()) || !Files.exists(imagePath)) {
			return ResponseEntity.notFound().build();
		}

		Resource resource = new UrlResource(imagePath.toUri());
		String contentType = Files.probeContentType(imagePath);
		if (contentType == null) {
			contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
		}
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_TYPE, contentType)
				.body(resource);
	}

	private AppUser currentUser(Authentication auth) {
		return users.findByUsername(auth.getName()).orElseThrow();
	}

	private void refreshAuthentication(AppUser user, Authentication auth) {
		var principal = User.builder()
				.username(user.getUsername())
				.password(user.getPasswordHash())
				.disabled(!user.isEnabled())
				.authorities(auth.getAuthorities())
				.build();
		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken(principal, auth.getCredentials(), auth.getAuthorities())
		);
	}

	private void updateEditableContactInfo(
			AppUser user,
			String telephone,
			String email
	) {
		Patient patient = user.getPatient();
		if (patient != null) {
			patient.setTelephone(clean(telephone));
			patient.setEmail(clean(email));
			patients.save(patient);
		}

		Medecin medecin = user.getMedecin();
		if (medecin != null) {
			medecin.setTelephone(clean(telephone));
			medecin.setEmail(clean(email));
			medecins.save(medecin);
		}
	}

	private String clean(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		return value.trim();
	}
}
