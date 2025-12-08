package com.gestiondestock.entity;

import jakarta.persistence.*;
import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@EntityListeners(EntityIdGenerator.class)
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Client {

    @Id
    @JsonProperty("id")
    private UUID id;

    @JsonProperty("nom")
    private String nom;

    @JsonProperty("prenom")
    private String prenom;

    @JsonProperty("username")
    private String username;

    @JsonProperty("telephone")
    private String telephone;

    @JsonIgnore
    private String motDePasse;

    @JsonProperty("adresse")
    private String adresse;

    @JsonIgnore
    private UUID deleted_by;

    @JsonIgnore
    private LocalDateTime deleted_at;
}
