package com.java.gestion_stock.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "COMMANDE_FOURNISSEUR")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class COMMANDE_FOURNISSEUR {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID commandeId;

    @ManyToOne
    @JoinColumn(name = "produit_id")
    private PRODUIT produit;

    private LocalDateTime commandeDate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private StatutCommande statut = StatutCommande.EN_ATTENTE;

    public enum StatutCommande {
        EN_ATTENTE, LIVREE, ANNULEE
    }
}
