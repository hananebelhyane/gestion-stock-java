package com.GestionDeStock.demo.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "CLIENT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Client {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_id")
    private Integer clientId;
    
    @Column(name = "nom")
    private String nom;
    
    @Column(name = "prenom")
    private String prenom;
    
    @Column(name = "username")
    private String username;
    
    @Column(name = "telephone")
    private String telephone;
    
    @Column(name = "mot_de_passe")
    private String motDePasse;
    
    @Column(name = "adresse")
    private String adresse;
    
    @Column(name = "deletedBy")
    private Integer deletedBy;
    
    @Column(name = "deletedAt")
    private LocalDateTime deletedAt;
    
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<CommandeClient> commandes;
}
