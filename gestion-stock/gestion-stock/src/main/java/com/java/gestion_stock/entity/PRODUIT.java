package com.java.gestion_stock.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PRODUIT {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID produitId;

    @Column(nullable = false)
    private String nom;

    private String description;

    @ManyToOne
    @JoinColumn(name = "categorie_id")
    private CATEGORIE categorie;

    private BigDecimal prixUnitaire;
    private String urlImage;

    private Integer seuilAlerte = 5;
    private Integer seuilMax;

    @ManyToOne
    @JoinColumn(name = "fournisseur_id")
    private FOURNISSEUR fournisseur;
}
