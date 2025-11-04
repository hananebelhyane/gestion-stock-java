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
public class COMMANDE_CLIENT {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID commandeId;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private CLIENT client;

    private LocalDateTime dateCommande = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private Statut statut = Statut.EN_ATTENTE;

    public enum Statut {
        EN_ATTENTE, CONFIRMEE, ANNULEE
    }
}
