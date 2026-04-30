package com.gestion.cabinet.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DbUserDetailsService implements UserDetailsService {

	private final AppUserRepository appUserRepository;

	public DbUserDetailsService(AppUserRepository appUserRepository) {
		this.appUserRepository = appUserRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		AppUser u = appUserRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("Unknown user: " + username));

		return User.builder()
				.username(u.getUsername())
				.password(u.getPasswordHash())
				.disabled(!u.isEnabled())
				.authorities(new SimpleGrantedAuthority("ROLE_" + u.getRole().name()))
				.build();
	}
}

