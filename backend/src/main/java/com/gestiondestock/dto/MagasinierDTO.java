package com.gestiondestock.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MagasinierDTO {
    private UUID id;
    private String nom;
    private String prenom;
    private String username;
    private String telephone;
    private UUID deletedBy;
    private LocalDateTime deletedAt;
}