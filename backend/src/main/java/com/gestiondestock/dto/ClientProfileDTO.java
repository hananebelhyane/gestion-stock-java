package com.gestiondestock.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ClientProfileDTO {

    private UUID id;

    @NotBlank
    private String nom;

    @NotBlank
    private String prenom;

    private String username;

    @NotBlank
    private String telephone;

    private String adresse;
}
