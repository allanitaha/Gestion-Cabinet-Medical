package com.fst.cabinet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class UserConfig {

    @Bean
    public UserDetailsService users() {
        UserDetails admin = User.withDefaultPasswordEncoder()
                .username("admin")
                .password("admin123")
                .roles("ADMIN")
                .build();

        UserDetails medecin = User.withDefaultPasswordEncoder()
                .username("medecin")
                .password("medecin123")
                .roles("MEDECIN")
                .build();

        UserDetails secretaire = User.withDefaultPasswordEncoder()
                .username("secretaire")
                .password("sec123")
                .roles("SECRETAIRE")
                .build();

        return new InMemoryUserDetailsManager(admin, medecin, secretaire);
    }
}
