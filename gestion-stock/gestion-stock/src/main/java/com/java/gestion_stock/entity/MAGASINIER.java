package com.java.gestion_stock.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MAGASINIER {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID magasinier_id;
    @Column(nullable = false)
    private  String nom;
    @Column(nullable = false)
    private String prenom;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private String telephone;
    @Column(nullable = false)
    private String motDePasse;
    @Column(nullable = false)
    private LocalDateTime dateCreation;
    @Column(nullable=true)
    private String deletedBy;
   @Column(nullable=true)
    private LocalDateTime deletedAt;
}
