package com.gestion.cabinet;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.gestion.cabinet.security.AppUser;
import com.gestion.cabinet.security.AppUserRepository;

@ControllerAdvice(annotations = Controller.class)
public class CurrentUserModelAdvice {

	private final AppUserRepository users;

	public CurrentUserModelAdvice(AppUserRepository users) {
		this.users = users;
	}

	@ModelAttribute("currentUser")
	public AppUser currentUser(Authentication auth) {
		if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
			return null;
		}
		return users.findByUsername(auth.getName()).orElse(null);
	}
}
