package com.gestiondestock.controller;

import com.gestiondestock.dto.AuthResponse;
import com.gestiondestock.dto.LoginRequest;
import com.gestiondestock.entity.Admin;
import com.gestiondestock.entity.Client;
import com.gestiondestock.entity.Magasinier;
import com.gestiondestock.repository.AdminRepository;
import com.gestiondestock.repository.ClientRepository;
import com.gestiondestock.repository.MagasinierRepository;
import com.gestiondestock.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AdminRepository adminRepository;
    private final ClientRepository clientRepository;
    private final MagasinierRepository magasinierRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(AdminRepository adminRepository,
            ClientRepository clientRepository,
            MagasinierRepository magasinierRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.adminRepository = adminRepository;
        this.clientRepository = clientRepository;
        this.magasinierRepository = magasinierRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        // Admin login
        Optional<Admin> adminOpt = adminRepository.findByUsername(request.getUsername());
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            if (passwordEncoder.matches(request.getPassword(), admin.getMotDePasse())) {
                String token = jwtService.generateToken(admin.getUsername(), "ADMIN", 24 * 60 * 60);
                return ResponseEntity
                        .ok(new AuthResponse(token, "ADMIN", admin.getUsername(), admin.getId().toString()));
            }
            return ResponseEntity.status(401).build();
        }

        // Client login
        Optional<Client> clientOpt = clientRepository.findByUsername(request.getUsername());
        if (clientOpt.isPresent()) {
            Client client = clientOpt.get();
            if (passwordEncoder.matches(request.getPassword(), client.getMotDePasse())) {
                String token = jwtService.generateToken(client.getUsername(), "CLIENT", 24 * 60 * 60);
                return ResponseEntity
                        .ok(new AuthResponse(token, "CLIENT", client.getUsername(), client.getId().toString()));
            }
            return ResponseEntity.status(401).build();
        }

        // Magasinier login
        Optional<Magasinier> magasinierOpt = magasinierRepository.findByUsername(request.getUsername());
        if (magasinierOpt.isPresent()) {
            Magasinier magasinier = magasinierOpt.get();
            if (passwordEncoder.matches(request.getPassword(), magasinier.getMotDePasse())) {
                String token = jwtService.generateToken(magasinier.getUsername(), "MAGASINIER", 24 * 60 * 60);
                return ResponseEntity.ok(
                        new AuthResponse(token, "MAGASINIER", magasinier.getUsername(), magasinier.getId().toString()));
            }
            return ResponseEntity.status(401).build();
        }

        // No user found
        return ResponseEntity.status(401).build();
    }
}
