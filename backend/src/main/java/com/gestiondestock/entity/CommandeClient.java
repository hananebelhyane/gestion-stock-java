package com.gestiondestock.entity;

import jakarta.persistence.*;
import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommandeClient {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    private LocalDateTime dateCommande = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private StatutCommande statut = StatutCommande.EN_ATTENTE;

    private Integer seuilMax;

    public enum StatutCommande {
        EN_ATTENTE, CONFIRMEE, ANNULEE
    }
}
