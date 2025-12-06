package com.gestiondestock.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommandeClientRequest {

    private ClientInfo client;
    private String statut;
    private Integer seuilMax;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClientInfo {

        private String nom;
        private String prenom;
    }
}
