package com.gestiondestock.controller;

import com.gestiondestock.dto.AuthResponse;
import com.gestiondestock.dto.LoginRequest;
import com.gestiondestock.entity.Admin;
import com.gestiondestock.entity.Client;
import com.gestiondestock.repository.AdminRepository;
import com.gestiondestock.repository.ClientRepository;
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
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(AdminRepository adminRepository,
                          ClientRepository clientRepository,
                          PasswordEncoder passwordEncoder,
                          JwtService jwtService) {
        this.adminRepository = adminRepository;
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        // PArtie ta3 Admin
        Optional<Admin> adminOpt = adminRepository.findByUsername(request.getUsername());
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            if (passwordEncoder.matches(request.getPassword(), admin.getMotDePasse())) {
                String token = jwtService.generateToken(admin.getUsername(), "ADMIN", 24 * 60 * 60);
                return ResponseEntity.ok(new AuthResponse(token, "ADMIN", admin.getUsername()));
            }
            return ResponseEntity.status(401).build();
        }

        // PArtie ta3 Client
        return clientRepository.findByUsername(request.getUsername())
                .filter(c -> passwordEncoder.matches(request.getPassword(), c.getMotDePasse()))
                .map(c -> ResponseEntity.ok(new AuthResponse(
                        jwtService.generateToken(c.getUsername(), "CLIENT", 24 * 60 * 60),
                        "CLIENT",
                        c.getUsername()
                )))
                .orElseGet(() -> ResponseEntity.status(401).build());
    }
}
