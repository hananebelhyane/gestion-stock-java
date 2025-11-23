package com.gestiondestock.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.gestiondestock.entity.Admin;
import com.gestiondestock.repository.AdminRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class AdminServiceImpl implements AdminService {
    
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public AdminServiceImpl(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    public Admin registerAdmin(Admin admin) {
        if (admin.getMotDePasse() != null) {
            admin.setMotDePasse(passwordEncoder.encode(admin.getMotDePasse()));
        }
        return adminRepository.save(admin);
    }
}