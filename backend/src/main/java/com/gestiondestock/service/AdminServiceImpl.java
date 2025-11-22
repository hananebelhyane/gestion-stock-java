package com.gestiondestock.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.gestiondestock.entity.Admin;
import com.gestiondestock.repository.AdminRepository;

@Service
public class AdminServiceImpl implements AdminService {
    
    private final AdminRepository adminRepository;
    
    @Autowired
    public AdminServiceImpl(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }
    
    @Override
    public Admin registerAdmin(Admin admin) {
        return adminRepository.save(admin);
    }
}