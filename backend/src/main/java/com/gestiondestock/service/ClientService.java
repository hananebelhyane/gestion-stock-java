package com.gestiondestock.service;

import com.gestiondestock.entity.Client;
import java.util.List;
import java.util.UUID;

public interface ClientService {
    Client registerClient(Client client);
    List<Client> getAllActiveClients();
    Client createClient(Client client);
    Client updateClient(UUID id, Client client);
    Client getClientById(UUID id);
    void deleteClient(UUID id, UUID deletedBy);
    List<Client> searchClients(String keyword);
    List<Client> getDeletedClients();
    Client restoreClient(UUID id);
}
