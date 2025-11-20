package com.gestiondestock.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@EntityListeners(EntityIdGenerator.class)
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommandeFournisseur {

    @Id
    @JsonProperty("id")
    private UUID id;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "produit_id")
    @JsonProperty("produit")
    @JsonIgnoreProperties({"commandeFournisseurs", "deletedAt", "deletedBy"})
    private Produit produit;

    @JsonProperty("commandeDate")
    @Column(name = "commande_date")
    private LocalDateTime commandeDate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @JsonProperty("statut")
    private StatutCommande statut = StatutCommande.en_attente;

    public enum StatutCommande {
        en_attente, livree, annulee
    }
}
