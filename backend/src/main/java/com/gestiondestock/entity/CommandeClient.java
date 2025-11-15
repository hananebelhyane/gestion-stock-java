package com.gestiondestock.entity;

import jakarta.persistence.*;
import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "COMMANDE_CLIENT")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommandeClient {

    @Id
    @JsonProperty("id")
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne
    @JsonProperty("client")
    @JoinColumn(name = "client_id")
    @JsonIgnoreProperties({"commandeClients", "motDePasse", "deletedAt", "deletedBy"})
    private Client client;

    @JsonProperty("dateCommande")
    @Column(name = "date_commande")
    private LocalDateTime dateCommande = LocalDateTime.now();

    @JsonProperty("statut")
    @Enumerated(EnumType.STRING)
    @Column(name = "statut")
    private StatutCommande statut = StatutCommande.en_attente;

    @JsonProperty("seuilMax")
    @Column(name = "seuil_max")
    private Integer seuilMax;

    public enum StatutCommande {
        en_attente, confirmee, annulee
    }
}
