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

        // Partie Admin
        Optional<Admin> adminOpt = adminRepository.findByUsername(request.getUsername());
        if (adminOpt.isPresent() && passwordEncoder.matches(request.getPassword(), adminOpt.get().getMotDePasse())) {
            Admin admin = adminOpt.get();
            String token = jwtService.generateToken(admin.getUsername(), "ADMIN", 24 * 60 * 60);
            return ResponseEntity.ok(new AuthResponse(token, "ADMIN", admin.getUsername()));
        }

        // Partie Client
        Optional<Client> clientOpt = clientRepository.findByUsername(request.getUsername());
        if (clientOpt.isPresent() && passwordEncoder.matches(request.getPassword(), clientOpt.get().getMotDePasse())) {
            Client client = clientOpt.get();
            String token = jwtService.generateToken(client.getUsername(), "CLIENT", 24 * 60 * 60);
            return ResponseEntity.ok(new AuthResponse(token, "CLIENT", client.getUsername()));
        }

        // Partie Magasinier
        Optional<Magasinier> magasinierOpt = magasinierRepository.findByUsername(request.getUsername());
        if (magasinierOpt.isPresent() && passwordEncoder.matches(request.getPassword(), magasinierOpt.get().getMotDePasse())) {
            Magasinier magasinier = magasinierOpt.get();
            String token = jwtService.generateToken(magasinier.getUsername(), "MAGASINIER", 24 * 60 * 60);
            return ResponseEntity.ok(new AuthResponse(token, "MAGASINIER", magasinier.getUsername()));
        }

        // Aucun utilisateur trouvé ou mot de passe incorrect
        return ResponseEntity.status(401).build();
    }}
