package com.GestionDeStock.demo.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "COMMANDE_CLIENT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommandeClient {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "commande_id")
    private Integer commandeId;
    
    @Column(name = "date_commande")
    private LocalDateTime dateCommande;
    
    @Column(name = "statut")
    private String statut;
    
    @Column(name = "montant_total")
    private Double montantTotal;
    
    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
    
    @OneToMany(mappedBy = "commandeClient", cascade = CascadeType.ALL)
    private List<LigneCommande> lignesCommande;
}
