package com.java.gestion_stock.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "CATEGORIE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CATEGORIE {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID categorieId;

    private String nom;
    private String description;

    @OneToMany(mappedBy = "categorie", cascade = CascadeType.ALL)
    private List<PRODUIT> produits;
}
