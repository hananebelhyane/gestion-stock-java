package com.java.gestion_stock.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FACTURE {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID idFacture;

    @ManyToOne
    @JoinColumn(name = "commande_id")
    private COMMANDE_CLIENT commande;

    private LocalDateTime dateFacture = LocalDateTime.now();
    private BigDecimal montantTotal;
}
