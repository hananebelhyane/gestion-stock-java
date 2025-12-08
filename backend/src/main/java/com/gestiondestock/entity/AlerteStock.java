package com.gestiondestock.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entité représentant une alerte de stock
 * Une alerte est créée automatiquement quand :
 * - Un produit tombe en rupture (quantité = 0)
 * - Un produit passe sous le seuil d'alerte
 */
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AlerteStock {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "produit_id", nullable = false)
    private Produit produit;

    @Column(nullable = false)
    private LocalDateTime dateAlerte = LocalDateTime.now();

    @Column(nullable = false, length = 500)
    private String message;

    /**
     * Niveau de gravité de l'alerte
     * CRITIQUE = Rupture de stock (quantité = 0)
     * MOYENNE = Stock très faible (< seuil/2)
     * FAIBLE = Stock faible (< seuil)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NiveauGravite niveauGravite;

    /**
     * Statut de l'alerte
     * NON_LU = Nouvelle alerte non consultée
     * TRAITE = Alerte traitée par le magasinier
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutAlerte statut = StatutAlerte.NON_LU;

    /**
     * Quantité actuelle au moment de la création de l'alerte
     */
    private Integer quantiteActuelle;

    /**
     * Seuil d'alerte du produit
     */
    private Integer seuilAlerte;

    // Énumérations
    
    public enum NiveauGravite {
        CRITIQUE,  // Rupture de stock (0 unités)
        MOYENNE,   // Stock critique (< seuil/2)
        FAIBLE     // Stock faible (< seuil)
    }

    public enum StatutAlerte {
        NON_LU,    // Non consultée
        TRAITE     // Traitée par le magasinier
    }

    // Constructeur personnalisé pour créer une alerte
    public AlerteStock(Produit produit, String message, NiveauGravite niveauGravite, 
                      Integer quantiteActuelle, Integer seuilAlerte) {
        this.produit = produit;
        this.message = message;
        this.niveauGravite = niveauGravite;
        this.quantiteActuelle = quantiteActuelle;
        this.seuilAlerte = seuilAlerte;
        this.dateAlerte = LocalDateTime.now();
        this.statut = StatutAlerte.NON_LU;
    }
}