package com.gestiondestock.service;

import com.gestiondestock.entity.Client;
import java.util.List;

public interface ClientService {
    Client registerClient(Client client);
    List<Client> getAllActiveClients();
}
