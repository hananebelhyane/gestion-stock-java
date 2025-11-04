package com.java.gestion_stock.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SORTIES_STOCK {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    private Integer quantite;
    private LocalDateTime dateSortie = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "magasinier_id")
    private MAGASINIER magasinier;

    @ManyToOne
    @JoinColumn(name = "ligne_commande_id")
    private LIGNE_COMMANDE ligneCommande;
}
