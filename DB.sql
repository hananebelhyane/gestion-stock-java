CREATE DATABASE IF NOT EXISTS gestionStock;
USE gestionStock;

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
    statut ENUM('non_lu', 'traite') DEFAULT 'non_lu',  -- ✅ minuscules
    FOREIGN KEY (produit_id) REFERENCES PRODUIT(id) ON DELETE CASCADE
);

-- =====================================================
-- TABLE COMMANDE_CLIENT
-- =====================================================
CREATE TABLE COMMANDE_CLIENT (
    id BINARY(16) PRIMARY KEY,
    client_id BINARY(16) NOT NULL,
    date_commande DATETIME DEFAULT CURRENT_TIMESTAMP,
    statut ENUM('en_attente', 'confirmee', 'annulee') DEFAULT 'en_attente',  -- ✅ minuscules
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
INSERT INTO ADMIN (nom, prenom, email, username, telephone, motDePasse) 
VALUES ('Admin', 'System', 'admin@gestionstock.com', 'admin', '0000000000', 'admin123');

-- =====================================================
-- DONNÉES D'EXEMPLE
-- =====================================================

-- 1. CLIENTS (UUID générés avec UUID_TO_BIN)
INSERT INTO CLIENT (id, nom, prenom, username, telephone, motDePasse, adresse) VALUES
(UNHEX(REPLACE(UUID(), '-', '')), 'Alami', 'Sara', 'sara.alami', '0612345678', 'pass123', '123 Rue Mohammed V, Casablanca'),
(UNHEX(REPLACE(UUID(), '-', '')), 'Bennani', 'Youssef', 'youssef.b', '0623456789', 'pass456', '45 Avenue Hassan II, Rabat'),
(UNHEX(REPLACE(UUID(), '-', '')), 'El Amrani', 'Fatima', 'fatima.ea', '0634567890', 'pass789', '78 Boulevard Zerktouni, Marrakech');

-- 2. MAGASINIERS
INSERT INTO MAGASINIER (id, nom, prenom, username, telephone, motDePasse) VALUES
(UNHEX(REPLACE(UUID(), '-', '')), 'Khalil', 'Ahmed', 'ahmed.k', '0645678901', 'mag123'),
(UNHEX(REPLACE(UUID(), '-', '')), 'Rahmani', 'Samira', 'samira.r', '0656789012', 'mag456');

-- 3. FOURNISSEURS
INSERT INTO FOURNISSEUR (id, nom, prenom, email, telephone, adresse) VALUES
(UNHEX(REPLACE(UUID(), '-', '')), 'TechSupply', 'Mohamed', 'contact@techsupply.ma', '0522123456', 'Zone Industrielle Ain Sebaa, Casablanca'),
(UNHEX(REPLACE(UUID(), '-', '')), 'ElectroMaroc', 'Karim', 'info@electromaroc.ma', '0537654321', 'Quartier Industriel, Tanger'),
(UNHEX(REPLACE(UUID(), '-', '')), 'BureauPro', 'Nadia', 'contact@bureaupro.ma', '0524987654', 'Avenue Allal Ben Abdellah, Fès');

-- 4. CATÉGORIES
INSERT INTO CATEGORIE (id, nom, description) VALUES
(UNHEX(REPLACE(UUID(), '-', '')), 'Électronique', 'Appareils et composants électroniques'),
(UNHEX(REPLACE(UUID(), '-', '')), 'Informatique', 'Ordinateurs, périphériques et accessoires'),
(UNHEX(REPLACE(UUID(), '-', '')), 'Bureautique', 'Fournitures et équipements de bureau');

-- 5. PRODUITS (liés aux catégories et fournisseurs)
INSERT INTO PRODUIT (id, nom, description, prix_unitaire, url_image, categorie_id, fournisseur_id) VALUES
(
    UNHEX(REPLACE(UUID(), '-', '')), 
    'Ordinateur Portable HP', 
    'HP ProBook 450 G8, Intel i5, 8GB RAM, 256GB SSD', 
    6500.00, 
    'https://example.com/hp-laptop.jpg',
    (SELECT id FROM CATEGORIE WHERE nom = 'Informatique' LIMIT 1),
    (SELECT id FROM FOURNISSEUR WHERE nom = 'TechSupply' LIMIT 1)
),
(
    UNHEX(REPLACE(UUID(), '-', '')), 
    'Souris Sans Fil Logitech', 
    'Logitech M185, optique, sans fil 2.4GHz', 
    120.00, 
    'https://example.com/logitech-mouse.jpg',
    (SELECT id FROM CATEGORIE WHERE nom = 'Informatique' LIMIT 1),
    (SELECT id FROM FOURNISSEUR WHERE nom = 'ElectroMaroc' LIMIT 1)
),
(
    UNHEX(REPLACE(UUID(), '-', '')), 
    'Imprimante Canon', 
    'Canon PIXMA G3420, multifonction, Wifi', 
    2800.00, 
    'https://example.com/canon-printer.jpg',
    (SELECT id FROM CATEGORIE WHERE nom = 'Bureautique' LIMIT 1),
    (SELECT id FROM FOURNISSEUR WHERE nom = 'BureauPro' LIMIT 1)
),
(
    UNHEX(REPLACE(UUID(), '-', '')), 
    'Clavier Mécanique', 
    'Clavier gaming RGB rétroéclairé', 
    450.00, 
    'https://example.com/keyboard.jpg',
    (SELECT id FROM CATEGORIE WHERE nom = 'Informatique' LIMIT 1),
    (SELECT id FROM FOURNISSEUR WHERE nom = 'TechSupply' LIMIT 1)
);

-- 6. STOCK (quantités disponibles)
INSERT INTO STOCK (id, produit_id, quantite_disponible, seuil_alerte) VALUES
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT id FROM PRODUIT WHERE nom = 'Ordinateur Portable HP' LIMIT 1), 15, 5),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT id FROM PRODUIT WHERE nom = 'Souris Sans Fil Logitech' LIMIT 1), 50, 10),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT id FROM PRODUIT WHERE nom = 'Imprimante Canon' LIMIT 1), 3, 5),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT id FROM PRODUIT WHERE nom = 'Clavier Mécanique' LIMIT 1), 25, 8);

-- 7. ALERTES STOCK (pour produits en dessous du seuil)
INSERT INTO ALERTE_STOCK (id, produit_id, date_alerte, message, statut) VALUES
(
    UNHEX(REPLACE(UUID(), '-', '')), 
    (SELECT id FROM PRODUIT WHERE nom = 'Imprimante Canon' LIMIT 1), 
    NOW(), 
    'Stock faible : seulement 3 unités disponibles', 
    'non_lu'  -- ✅ minuscule
);

-- 8. COMMANDES CLIENT
INSERT INTO COMMANDE_CLIENT (id, client_id, date_commande, statut, seuil_max) VALUES
(
    UNHEX(REPLACE(UUID(), '-', '')), 
    (SELECT id FROM CLIENT WHERE username = 'sara.alami' LIMIT 1), 
    '2025-11-01 10:30:00', 
    'confirmee',  -- ✅ minuscule
    10000
),
(
    UNHEX(REPLACE(UUID(), '-', '')), 
    (SELECT id FROM CLIENT WHERE username = 'youssef.b' LIMIT 1), 
    '2025-11-05 14:15:00', 
    'en_attente',  -- ✅ minuscule
    5000
),
(
    UNHEX(REPLACE(UUID(), '-', '')), 
    (SELECT id FROM CLIENT WHERE username = 'fatima.ea' LIMIT 1), 
    '2025-11-08 09:00:00', 
    'confirmee',  -- ✅ minuscule
    15000
);

-- 9. LIGNES DE COMMANDE (détails des commandes)
INSERT INTO LIGNE_COMMANDE (id, commande_id, produit_id, quantite, prix_unitaire, montant_total) VALUES
-- Commande Sara (2 produits)
(
    UNHEX(REPLACE(UUID(), '-', '')),
    (SELECT id FROM COMMANDE_CLIENT WHERE client_id = (SELECT id FROM CLIENT WHERE username = 'sara.alami' LIMIT 1) LIMIT 1),
    (SELECT id FROM PRODUIT WHERE nom = 'Ordinateur Portable HP' LIMIT 1),
    2,
    6500.00,
    13000.00
),
(
    UNHEX(REPLACE(UUID(), '-', '')),
    (SELECT id FROM COMMANDE_CLIENT WHERE client_id = (SELECT id FROM CLIENT WHERE username = 'sara.alami' LIMIT 1) LIMIT 1),
    (SELECT id FROM PRODUIT WHERE nom = 'Souris Sans Fil Logitech' LIMIT 1),
    5,
    120.00,
    600.00
),
-- Commande Youssef
(
    UNHEX(REPLACE(UUID(), '-', '')),
    (SELECT id FROM COMMANDE_CLIENT WHERE client_id = (SELECT id FROM CLIENT WHERE username = 'youssef.b' LIMIT 1) LIMIT 1),
    (SELECT id FROM PRODUIT WHERE nom = 'Clavier Mécanique' LIMIT 1),
    3,
    450.00,
    1350.00
),
-- Commande Fatima
(
    UNHEX(REPLACE(UUID(), '-', '')),
    (SELECT id FROM COMMANDE_CLIENT WHERE client_id = (SELECT id FROM CLIENT WHERE username = 'fatima.ea' LIMIT 1) LIMIT 1),
    (SELECT id FROM PRODUIT WHERE nom = 'Imprimante Canon' LIMIT 1),
    1,
    2800.00,
    2800.00
);

-- 10. FACTURES (générées pour commandes confirmées)
INSERT INTO FACTURE (id, commande_id, date_facture, montant_total) VALUES
(
    UNHEX(REPLACE(UUID(), '-', '')),
    (SELECT id FROM COMMANDE_CLIENT WHERE client_id = (SELECT id FROM CLIENT WHERE username = 'sara.alami' LIMIT 1) LIMIT 1),
    '2025-11-01 11:00:00',
    13600.00
),
(
    UNHEX(REPLACE(UUID(), '-', '')),
    (SELECT id FROM COMMANDE_CLIENT WHERE client_id = (SELECT id FROM CLIENT WHERE username = 'fatima.ea' LIMIT 1) LIMIT 1),
    '2025-11-08 10:30:00',
    2800.00
);

-- 11. COMMANDES FOURNISSEUR (réapprovisionnement)
INSERT INTO COMMANDE_FOURNISSEUR (id, produit_id, commande_date, statut) VALUES
(
    UNHEX(REPLACE(UUID(), '-', '')),
    (SELECT id FROM PRODUIT WHERE nom = 'Imprimante Canon' LIMIT 1),
    '2025-11-07 08:00:00',
    'en_attente'  -- ✅ minuscule
),
(
    UNHEX(REPLACE(UUID(), '-', '')),
    (SELECT id FROM PRODUIT WHERE nom = 'Souris Sans Fil Logitech' LIMIT 1),
    '2025-10-28 14:30:00',
    'livree'  -- ✅ minuscule
);

-- 12. ENTRÉES DE STOCK (réceptions de marchandises)
INSERT INTO ENTREE_STOCK (id, quantite, date_entree, produit_id, magasinier_id, commande_fournisseur_id) VALUES
(
    UNHEX(REPLACE(UUID(), '-', '')),
    50,
    '2025-10-30 09:15:00',
    (SELECT id FROM PRODUIT WHERE nom = 'Souris Sans Fil Logitech' LIMIT 1),
    (SELECT id FROM MAGASINIER WHERE username = 'ahmed.k' LIMIT 1),
    (SELECT id FROM COMMANDE_FOURNISSEUR WHERE statut = 'livree' LIMIT 1)  -- ✅ minuscule
);

-- 13. SORTIES DE STOCK (expéditions)
INSERT INTO SORTIE_STOCK (id, quantite, date_sortie, produit_id, magasinier_id, ligne_commande_id) VALUES
(
    UNHEX(REPLACE(UUID(), '-', '')),
    2,
    '2025-11-01 15:30:00',
    (SELECT id FROM PRODUIT WHERE nom = 'Ordinateur Portable HP' LIMIT 1),
    (SELECT id FROM MAGASINIER WHERE username = 'samira.r' LIMIT 1),
    (SELECT id FROM LIGNE_COMMANDE WHERE produit_id = (SELECT id FROM PRODUIT WHERE nom = 'Ordinateur Portable HP' LIMIT 1) LIMIT 1)
);
