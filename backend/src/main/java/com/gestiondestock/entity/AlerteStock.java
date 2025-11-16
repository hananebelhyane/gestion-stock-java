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
public class AlerteStock {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "produit_id", nullable = true)
    private Produit produit;

    private LocalDateTime dateAlerte = LocalDateTime.now();
    private String message;

    @Enumerated(EnumType.STRING)
    private StatutAlerte statut = StatutAlerte.NON_LU;

    public enum StatutAlerte {
        NON_LU, TRAITE
    }
}
