package com.GestionDeStock.demo.repository;

import com.GestionDeStock.demo.model.CommandeClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CommandeClientRepository extends JpaRepository<CommandeClient, Integer> {
    
    // Méthodes de recherche personnalisées
    List<CommandeClient> findByStatut(String statut);
    
    List<CommandeClient> findByDateCommandeBetween(LocalDateTime dateDebut, LocalDateTime dateFin);
    
    // Vous ajouterez d'autres méthodes selon vos besoins
}