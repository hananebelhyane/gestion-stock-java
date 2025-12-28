package com.gestiondestock.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Facture {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "commande_id")
    private CommandeClient commande;

    private LocalDateTime dateFacture = LocalDateTime.now();
    private Double montantTotal;

    @Column(nullable = false)
    private boolean estPayee = false;
}
