package com.gestiondestock.service;

import com.gestiondestock.entity.Client;
import com.gestiondestock.repository.ClientRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    public ClientServiceImpl(ClientRepository clientRepository, PasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Client registerClient(Client client) {
        if (client.getMotDePasse() != null) {
            client.setMotDePasse(passwordEncoder.encode(client.getMotDePasse()));
        }
        return clientRepository.save(client);
    }

    @Override
    public java.util.List<Client> getAllActiveClients() {
        return clientRepository.findAllActive();
    }
}
