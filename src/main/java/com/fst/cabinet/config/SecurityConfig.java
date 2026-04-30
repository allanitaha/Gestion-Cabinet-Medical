package com.fst.cabinet.config;

import com.fst.cabinet.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    // ── Encodeur BCrypt ──────────────────────────────────────
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ── Provider d'authentification ──────────────────────────
    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // ── Règles d'accès par rôle ──────────────────────────────
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authenticationProvider(authProvider())
                .authorizeHttpRequests(auth -> auth
                        // Ressources publiques
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()

                        // Dashboard : tous les rôles connectés
                        .requestMatchers("/dashboard").authenticated()

                        // Patients : ADMIN et SECRETAIRE peuvent tout faire ; MEDECIN peut voir
                        .requestMatchers("/patients/nouveau", "/patients/modifier/**",
                                "/patients/supprimer/**").hasAnyRole("ADMIN","SECRETAIRE")
                        .requestMatchers("/patients/**").hasAnyRole("ADMIN","SECRETAIRE","MEDECIN")

                        // Médecins : ADMIN uniquement pour CRUD
                        .requestMatchers("/medecins/nouveau", "/medecins/modifier/**",
                                "/medecins/supprimer/**").hasRole("ADMIN")
                        .requestMatchers("/medecins/**").hasAnyRole("ADMIN","SECRETAIRE","MEDECIN")

                        // Rendez-vous : SECRETAIRE et ADMIN gèrent ; MEDECIN consulte
                        .requestMatchers("/rendezvous/nouveau", "/rendezvous/modifier/**",
                                "/rendezvous/annuler/**").hasAnyRole("ADMIN","SECRETAIRE")
                        .requestMatchers("/rendezvous/**").hasAnyRole("ADMIN","SECRETAIRE","MEDECIN")

                        // Ordonnances : MEDECIN et ADMIN
                        .requestMatchers("/ordonnances/**").hasAnyRole("ADMIN","MEDECIN")

                        // Tout le reste : authentifié
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }
}