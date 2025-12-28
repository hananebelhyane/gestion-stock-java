package com.gestiondestock.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gestiondestock.entity.Panier;
import com.gestiondestock.entity.PanierItem;
import com.gestiondestock.repository.ClientRepository;
import com.gestiondestock.repository.PanierItemRepository;
import com.gestiondestock.repository.PanierRepository;
import com.gestiondestock.repository.ProduitRepository;

@Service
public class PanierService {

    @Autowired
    private PanierRepository panierRepository;

    @Autowired
    private PanierItemRepository panierItemRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ProduitRepository produitRepository;

    public Panier getOrCreatePanier(UUID clientId) {
        return panierRepository.findByClientId(clientId)
                .orElseGet(() -> {
                    Panier panier = new Panier();
                    panier.setClient(clientRepository.findById(clientId).orElseThrow());
                    return panierRepository.save(panier);
                });
    }

    public Panier ajouterProduit(UUID clientId, UUID produitId, int quantite) {
        Panier panier = getOrCreatePanier(clientId);

        for (PanierItem item : panier.getItems()) {
            if (item.getProduit().getId().equals(produitId)) {
                item.setQuantite(item.getQuantite() + quantite);
                return panierRepository.save(panier);
            }
        }

        PanierItem item = new PanierItem();
        item.setPanier(panier);
        item.setProduit(produitRepository.findById(produitId).orElseThrow());
        item.setQuantite(quantite);

        panier.getItems().add(item);
        return panierRepository.save(panier);
    }

    public void modifierQuantite(UUID itemId, int quantite) {
        PanierItem item = panierItemRepository.findById(itemId).orElseThrow();
        item.setQuantite(quantite);
        panierItemRepository.save(item);
    }

    public void supprimerItem(UUID itemId) {
        panierItemRepository.deleteById(itemId);
    }

    public void viderPanier(UUID clientId) {
        Panier panier = getOrCreatePanier(clientId);
        panier.getItems().clear();
        panierRepository.save(panier);
    }
}
