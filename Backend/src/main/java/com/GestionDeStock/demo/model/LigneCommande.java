package com.GestionDeStock.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "LIGNE_COMMANDE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LigneCommande {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ligne_commande_id")
    private Integer ligneCommandeId;
    
    @ManyToOne
    @JoinColumn(name = "commande_id", nullable = false)
    private CommandeClient commandeClient;
    
    @ManyToOne
    @JoinColumn(name = "produit_id", nullable = false)
    private Produit produit;
    
    @Column(name = "quantite")
    private Integer quantite;
    
    @Column(name = "prix_unitaire")
    private Double prixUnitaire;
}
