package com.GestionDeStock.demo.service;

import com.GestionDeStock.demo.model.CommandeClient;
import com.GestionDeStock.demo.repository.CommandeClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommandeClientService {
    
    @Autowired
    private CommandeClientRepository commandeClientRepository;
    
    // Créer une commande
    public CommandeClient creerCommande(CommandeClient commande) {
        return commandeClientRepository.save(commande);
    }
    
    // Récupérer toutes les commandes
    public List<CommandeClient> getAllCommandes() {
        return commandeClientRepository.findAll();
    }
    
    // Récupérer une commande par ID
    public Optional<CommandeClient> getCommandeById(Integer id) {
        return commandeClientRepository.findById(id);
    }
    
    // Modifier une commande
    public CommandeClient modifierCommande(Integer id, CommandeClient commande) {
        commande.setCommandeId(id);
        return commandeClientRepository.save(commande);
    }
    
    // Supprimer une commande
    public void supprimerCommande(Integer id) {
        commandeClientRepository.deleteById(id);
    }
    
    // Rechercher par statut
    public List<CommandeClient> getCommandesByStatut(String statut) {
        return commandeClientRepository.findByStatut(statut);
    }
}