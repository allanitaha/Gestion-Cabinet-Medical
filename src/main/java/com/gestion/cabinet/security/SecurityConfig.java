package com.gestion.cabinet.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/", "/login", "/register", "/error").permitAll()
						.requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
						.requestMatchers("/admin/**").hasRole("ADMIN")
						.requestMatchers("/patient/**").hasAnyRole("ADMIN", "PATIENT")
						.requestMatchers("/medecin/**").hasAnyRole("ADMIN", "MEDECIN")
						.requestMatchers("/patients/**", "/rdv/**").hasAnyRole("ADMIN", "SECRETAIRE", "MEDECIN")
						.requestMatchers("/medecins/**").hasAnyRole("ADMIN", "PATIENT", "SECRETAIRE", "MEDECIN")
						.requestMatchers("/dashboard", "/choose").hasAnyRole("ADMIN", "PATIENT", "SECRETAIRE", "MEDECIN")
						.anyRequest().authenticated()
				)
				.formLogin(form -> form
						.loginPage("/login")
						.defaultSuccessUrl("/choose", true)
						.permitAll()
				)
				.logout(logout -> logout
						.logoutUrl("/logout")
						.logoutSuccessUrl("/login?logout")
				)
				.httpBasic(Customizer.withDefaults())
				.build();
	}
}
