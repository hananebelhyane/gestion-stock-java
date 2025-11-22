package com.gestiondestock.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 🔒 Désactiver CSRF pour API REST
            .csrf(csrf -> csrf.disable())

            // 🌍 Activer CORS
            .cors(Customizer.withDefaults())

            // 🔐 JWT = application sans session
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // 🔑 Règles d'autorisation
            .authorizeHttpRequests(auth -> auth

                // --- ROUTES PUBLIQUES (login / register) ---
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/admins/register").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/clients/register").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/magasiniers/register").permitAll()

                // --- API MAGASINIERS : ADMIN uniquement ---
                .requestMatchers("/api/magasiniers/**").hasRole("ADMIN")
                    
                // --- API CLIENTS : ADMIN uniquement ---
                .requestMatchers("/api/clients/**").hasRole("ADMIN")

                // --- API générale : nécessite auth ---
                .requestMatchers("/api/**").authenticated()

                // --- Routes non API : accès libre ---
                .anyRequest().permitAll()
            )

            // 🔄 Ajouter le filtre JWT avant UsernamePasswordAuthenticationFilter
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
