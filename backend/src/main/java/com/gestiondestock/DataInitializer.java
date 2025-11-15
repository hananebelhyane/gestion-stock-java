package com.gestiondestock;

import com.gestiondestock.entity.Admin;
import com.gestiondestock.entity.Client;
import com.gestiondestock.repository.AdminRepository;
import com.gestiondestock.repository.ClientRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(AdminRepository adminRepository, ClientRepository clientRepository, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        String adminUsername = "admin";
        if (adminRepository.findByUsername(adminUsername).isEmpty()) {
            Admin a = new Admin();

            try {
                a.setUsername(adminUsername);
                a.setNom("System");
                a.setPrenom("Administrator");
                a.setEmail("admin@example.com");
                a.setTelephone("0000000000");
                a.setMotDePasse(passwordEncoder.encode("admin123"));
            } catch (NoSuchMethodError | Exception ex) {

            }
            adminRepository.save(a);
            System.out.println("Seeded admin user: " + adminUsername + " / admin123");
        }

        String clientUsername = "client";
        if (clientRepository.findByUsername(clientUsername).isEmpty()) {
            Client c = new Client();
            c.setUsername(clientUsername);
            c.setNom("John");
            c.setPrenom("Client");
            c.setTelephone("1111111111");
            c.setMotDePasse(passwordEncoder.encode("client123"));
            clientRepository.save(c);
            System.out.println("Seeded client user: " + clientUsername + " / client123");
        }
    }
}
