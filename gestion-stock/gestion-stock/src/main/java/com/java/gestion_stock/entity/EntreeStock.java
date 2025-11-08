package com.java.gestion_stock.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.UUID;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EntreeStock {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private Integer quantite;
    private LocalDateTime date_entree = LocalDateTime.now();
    @ManyToOne
    @JoinColumn(name = "produit_id", nullable = true)
    private Produit produit;
    @ManyToOne
    @JoinColumn(name = "magasinier_id", nullable = true)
    private Magasinier magasinier;
    @ManyToOne
    @JoinColumn(name = "commande_fournisseur_id", nullable = true)
    private CommandeFournisseur commandefournisseur;

}
