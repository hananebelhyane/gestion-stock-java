CREATE DATABASE IF NOT EXISTS gestionstock;
USE gestionstock;

-- =====================================================
-- TABLE ADMIN (utilise BIGINT pour compatibilité existante)
-- =====================================================
CREATE TABLE ADMIN (
    admin_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(255),
    prenom VARCHAR(255),
    email VARCHAR(255),
    username VARCHAR(255),
    telephone VARCHAR(255),
    motDePasse VARCHAR(255)
);

-- =====================================================
-- TABLE CLIENT 
-- =====================================================
CREATE TABLE CLIENT (
    id BINARY(16) PRIMARY KEY,
    nom VARCHAR(255),
    prenom VARCHAR(255),
    username VARCHAR(255),
    telephone VARCHAR(255),
    motDePasse VARCHAR(255),
    adresse VARCHAR(255),
    deleted_by BINARY(16) NULL,
    deleted_at DATETIME NULL
);

-- =====================================================
-- TABLE MAGASINIER 
-- =====================================================
CREATE TABLE MAGASINIER (
    id BINARY(16) PRIMARY KEY,
    nom VARCHAR(255),
    prenom VARCHAR(255),
    username VARCHAR(255),
    telephone VARCHAR(255),
    motDePasse VARCHAR(255),
    deleted_by BINARY(16) NULL,
    deleted_at DATETIME NULL
);

-- =====================================================
-- TABLE FOURNISSEUR 
-- =====================================================
CREATE TABLE FOURNISSEUR (
    id BINARY(16) PRIMARY KEY,
    nom VARCHAR(255),
    prenom VARCHAR(255),
    email VARCHAR(255),
    telephone VARCHAR(255),
    adresse VARCHAR(255),
    deleted_by BINARY(16) NULL,
    deleted_at DATETIME NULL
);

-- =====================================================
-- TABLE CATEGORIE
-- =====================================================
CREATE TABLE CATEGORIE (
    id BINARY(16) PRIMARY KEY,
    nom VARCHAR(255),
    description VARCHAR(255)
);

-- =====================================================
-- TABLE PRODUIT
-- =====================================================
CREATE TABLE PRODUIT (
    id BINARY(16) PRIMARY KEY,
    nom VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    prix_unitaire DOUBLE,
    url_image VARCHAR(255),
    categorie_id BINARY(16),
    fournisseur_id BINARY(16),
    FOREIGN KEY (categorie_id) REFERENCES CATEGORIE(id) ON DELETE SET NULL,
    FOREIGN KEY (fournisseur_id) REFERENCES FOURNISSEUR(id) ON DELETE SET NULL
);

-- =====================================================
-- TABLE STOCK
-- =====================================================
CREATE TABLE STOCK (
    id BINARY(16) PRIMARY KEY,
    produit_id BINARY(16),
    quantite_disponible INT DEFAULT 0,
    seuil_alerte INT DEFAULT 5,
    FOREIGN KEY (produit_id) REFERENCES PRODUIT(id) ON DELETE CASCADE
);

-- =====================================================
-- TABLE ALERTE_STOCK
-- =====================================================
CREATE TABLE ALERTE_STOCK (
    id BINARY(16) PRIMARY KEY,
    produit_id BINARY(16),
    date_alerte DATETIME DEFAULT CURRENT_TIMESTAMP,
    message VARCHAR(255),
    statut ENUM('NON_LU', 'TRAITE') DEFAULT 'NON_LU',
    FOREIGN KEY (produit_id) REFERENCES PRODUIT(id) ON DELETE CASCADE
);

-- =====================================================
-- TABLE COMMANDE_CLIENT
-- =====================================================
CREATE TABLE COMMANDE_CLIENT (
    id BINARY(16) PRIMARY KEY,
    client_id BINARY(16) NOT NULL,
    date_commande DATETIME DEFAULT CURRENT_TIMESTAMP,
    statut ENUM('en_attente', 'confirmee', 'annulee') DEFAULT 'en_attente',
    seuil_max INT,
    FOREIGN KEY (client_id) REFERENCES CLIENT(id) ON DELETE CASCADE
);

-- =====================================================
-- TABLE LIGNE_COMMANDE
-- =====================================================
CREATE TABLE LIGNE_COMMANDE (
    id BINARY(16) PRIMARY KEY,
    commande_id BINARY(16),
    produit_id BINARY(16),
    quantite INT,
    prix_unitaire DOUBLE,
    montant_total DOUBLE,
    FOREIGN KEY (commande_id) REFERENCES COMMANDE_CLIENT(id) ON DELETE CASCADE,
    FOREIGN KEY (produit_id) REFERENCES PRODUIT(id) ON DELETE CASCADE
);

-- =====================================================
-- TABLE COMMANDE_FOURNISSEUR
-- =====================================================
CREATE TABLE COMMANDE_FOURNISSEUR (
    id BINARY(16) PRIMARY KEY,
    produit_id BINARY(16),
    commande_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    statut ENUM('en_attente', 'livree', 'annulee') DEFAULT 'en_attente',
    FOREIGN KEY (produit_id) REFERENCES PRODUIT(id) ON DELETE CASCADE
);

-- Migration (exécuter après modification si tables déjà existantes):
-- ALTER TABLE COMMANDE_CLIENT MODIFY statut ENUM('en_attente','confirmee','annulee') DEFAULT 'en_attente';
-- ALTER TABLE COMMANDE_FOURNISSEUR MODIFY statut ENUM('en_attente','livree','annulee') DEFAULT 'en_attente';
-- UPDATE COMMANDE_CLIENT SET statut = LOWER(statut);
-- UPDATE COMMANDE_FOURNISSEUR SET statut = LOWER(statut);

-- =====================================================
-- TABLE ENTREE_STOCK
-- =====================================================
CREATE TABLE ENTREE_STOCK (
    id BINARY(16) PRIMARY KEY,
    quantite INT NOT NULL,
    date_entree DATETIME DEFAULT CURRENT_TIMESTAMP,
    produit_id BINARY(16),
    magasinier_id BINARY(16),
    commande_fournisseur_id BINARY(16),
    FOREIGN KEY (produit_id) REFERENCES PRODUIT(id) ON DELETE SET NULL,
    FOREIGN KEY (magasinier_id) REFERENCES MAGASINIER(id) ON DELETE SET NULL,
    FOREIGN KEY (commande_fournisseur_id) REFERENCES COMMANDE_FOURNISSEUR(id) ON DELETE SET NULL
);

-- =====================================================
-- TABLE SORTIE_STOCK
-- =====================================================
CREATE TABLE SORTIE_STOCK (
    id BINARY(16) PRIMARY KEY,
    quantite INT NOT NULL,
    date_sortie DATETIME DEFAULT CURRENT_TIMESTAMP,
    produit_id BINARY(16),
    magasinier_id BINARY(16),
    ligne_commande_id BINARY(16),
    FOREIGN KEY (produit_id) REFERENCES PRODUIT(id) ON DELETE SET NULL,
    FOREIGN KEY (magasinier_id) REFERENCES MAGASINIER(id) ON DELETE SET NULL,
    FOREIGN KEY (ligne_commande_id) REFERENCES LIGNE_COMMANDE(id) ON DELETE SET NULL
);

-- =====================================================
-- TABLE FACTURE
-- =====================================================
CREATE TABLE FACTURE (
    id BINARY(16) PRIMARY KEY,
    commande_id BINARY(16),
    date_facture DATETIME DEFAULT CURRENT_TIMESTAMP,
    montant_total DOUBLE,
    FOREIGN KEY (commande_id) REFERENCES COMMANDE_CLIENT(id) ON DELETE CASCADE
);

-- =====================================================
-- INDEX pour améliorer les performances
-- =====================================================
CREATE INDEX idx_client_username ON CLIENT(username);
CREATE INDEX idx_magasinier_username ON MAGASINIER(username);
CREATE INDEX idx_produit_nom ON PRODUIT(nom);
CREATE INDEX idx_stock_produit ON STOCK(produit_id);
CREATE INDEX idx_commande_client ON COMMANDE_CLIENT(client_id);
CREATE INDEX idx_commande_date ON COMMANDE_CLIENT(date_commande);

-- Insérer un admin par défaut
INSERT INTO ADMIN (nom, prenom, email, username, telephone, mot_de_passe) 
VALUES ('Admin', 'System', 'admin@gestionstock.com', 'admin', '0000000000', 'admin123');