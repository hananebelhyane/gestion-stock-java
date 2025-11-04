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
public class LIGNE_COMMANDE {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID ligneCommandeId;

    @ManyToOne
    @JoinColumn(name = "commande_id")
    private COMMANDE_CLIENT commande;

    @ManyToOne
    @JoinColumn(name = "produit_id")
    private PRODUIT produit;

    private Integer quantite;
    private BigDecimal prixUnitaire;
    private BigDecimal montantTotal;
}
