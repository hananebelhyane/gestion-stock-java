package com.gestiondestock.entity;

import jakarta.persistence.*;

@Entity
@Table(name="ADMIN")

public class Admin{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long admin_id;
    private String nom;
    private String prenom;
    private String email;
    private String username;
    private String telephone;
    @Column(name = "mot_de_passe")
    private String motDePasse;

    public Admin(Long admin_id, String nom, String prenom, String email, String username, String telephone, String motDePasse) {
        this.admin_id = admin_id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.username = username;
        this.telephone = telephone;
        this.motDePasse = motDePasse;
    }

    public Admin() {
        
    }


    public Long getId() {
        return admin_id;
    }

    public void setId(Long admin_id) {
        this.admin_id = admin_id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }
}