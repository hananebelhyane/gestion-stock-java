
CREATE DATABASE IF NOT EXISTS gestionstock;
USE gestionstock;

-- =====================================================
-- TABLE ADMIN (utilise BIGSERIAL pour auto-increment)
-- =====================================================
CREATE TABLE ADMIN (
    admin_id BIGSERIAL PRIMARY KEY,
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
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nom VARCHAR(255),
    prenom VARCHAR(255),
    username VARCHAR(255),
    telephone VARCHAR(255),
    motDePasse VARCHAR(255),
    adresse VARCHAR(255),
    deleted_by UUID NULL,
    deleted_at TIMESTAMP NULL
);

-- =====================================================
-- TABLE MAGASINIER
-- =====================================================
CREATE TABLE MAGASINIER (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nom VARCHAR(255),
    prenom VARCHAR(255),
    username VARCHAR(255),
    telephone VARCHAR(255),
    motDePasse VARCHAR(255),
    deleted_by UUID NULL,
    deleted_at TIMESTAMP NULL
);

-- =====================================================
-- TABLE FOURNISSEUR
-- =====================================================
CREATE TABLE FOURNISSEUR (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nom VARCHAR(255),
    prenom VARCHAR(255),
    email VARCHAR(255),
    telephone VARCHAR(255),
    adresse VARCHAR(255),
    deleted_by UUID NULL,
    deleted_at TIMESTAMP NULL
);

-- =====================================================
-- TABLE CATEGORIE
-- =====================================================
CREATE TABLE CATEGORIE (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nom VARCHAR(255),
    description VARCHAR(255)
);

-- =====================================================
-- TABLE PRODUIT
-- =====================================================
CREATE TABLE PRODUIT (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nom VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    prix_unitaire DOUBLE PRECISION,
    url_image VARCHAR(255),
    categorie_id UUID,
    fournisseur_id UUID,
    FOREIGN KEY (categorie_id) REFERENCES CATEGORIE(id) ON DELETE SET NULL,
    FOREIGN KEY (fournisseur_id) REFERENCES FOURNISSEUR(id) ON DELETE SET NULL
);

-- =====================================================
-- TABLE STOCK
-- =====================================================
CREATE TABLE STOCK (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    produit_id UUID,
    quantite_disponible INTEGER DEFAULT 0,
    seuil_alerte INTEGER DEFAULT 5,
    FOREIGN KEY (produit_id) REFERENCES PRODUIT(id) ON DELETE CASCADE
);

-- =====================================================
-- TYPE ENUM pour STATUT ALERTE
-- =====================================================
CREATE TYPE statut_alerte_enum AS ENUM ('NON_LU', 'TRAITE');

-- =====================================================
-- TABLE ALERTE_STOCK
-- =====================================================
CREATE TABLE ALERTE_STOCK (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    produit_id UUID,
    date_alerte TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    message VARCHAR(255),
    statut statut_alerte_enum DEFAULT 'NON_LU',
    FOREIGN KEY (produit_id) REFERENCES PRODUIT(id) ON DELETE CASCADE
);

-- =====================================================
-- TYPE ENUM pour STATUT COMMANDE CLIENT
-- =====================================================
CREATE TYPE statut_commande_enum AS ENUM ('en_attente', 'confirmee', 'annulee');

-- =====================================================
-- TABLE COMMANDE_CLIENT
-- =====================================================
CREATE TABLE COMMANDE_CLIENT (
<<<<<<< HEAD
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    client_id UUID NOT NULL,
    date_commande TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    statut statut_commande_enum DEFAULT 'en_attente',
    seuil_max INTEGER,
=======
    id BINARY(16) PRIMARY KEY,
    client_id BINARY(16) NOT NULL,
    date_commande DATETIME DEFAULT CURRENT_TIMESTAMP,
    statut ENUM('en_attente', 'confirmee', 'annulee') DEFAULT 'en_attente',
    seuil_max INT,
>>>>>>> origin/meryem2
    FOREIGN KEY (client_id) REFERENCES CLIENT(id) ON DELETE CASCADE
);

-- =====================================================
-- TABLE LIGNE_COMMANDE
-- =====================================================
CREATE TABLE LIGNE_COMMANDE (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    commande_id UUID,
    produit_id UUID,
    quantite INTEGER,
    prix_unitaire DOUBLE PRECISION,
    montant_total DOUBLE PRECISION,
    FOREIGN KEY (commande_id) REFERENCES COMMANDE_CLIENT(id) ON DELETE CASCADE,
    FOREIGN KEY (produit_id) REFERENCES PRODUIT(id) ON DELETE CASCADE
);

-- =====================================================
-- TYPE ENUM pour STATUT COMMANDE FOURNISSEUR
-- =====================================================
CREATE TYPE statut_fournisseur_enum AS ENUM ('EN_ATTENTE', 'LIVREE', 'ANNULEE');

-- =====================================================
-- TABLE COMMANDE_FOURNISSEUR
-- =====================================================
CREATE TABLE COMMANDE_FOURNISSEUR (
<<<<<<< HEAD
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    produit_id UUID,
    commande_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    statut statut_fournisseur_enum DEFAULT 'EN_ATTENTE',
=======
    id BINARY(16) PRIMARY KEY,
    produit_id BINARY(16),
    commande_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    statut ENUM('en_attente', 'livree', 'annulee') DEFAULT 'en_attente',
>>>>>>> origin/meryem2
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
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    quantite INTEGER NOT NULL,
    date_entree TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    produit_id UUID,
    magasinier_id UUID,
    commande_fournisseur_id UUID,
    FOREIGN KEY (produit_id) REFERENCES PRODUIT(id) ON DELETE SET NULL,
    FOREIGN KEY (magasinier_id) REFERENCES MAGASINIER(id) ON DELETE SET NULL,
    FOREIGN KEY (commande_fournisseur_id) REFERENCES COMMANDE_FOURNISSEUR(id) ON DELETE SET NULL
);

-- =====================================================
-- TABLE SORTIE_STOCK
-- =====================================================
CREATE TABLE SORTIE_STOCK (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    quantite INTEGER NOT NULL,
    date_sortie TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    produit_id UUID,
    magasinier_id UUID,
    ligne_commande_id UUID,
    FOREIGN KEY (produit_id) REFERENCES PRODUIT(id) ON DELETE SET NULL,
    FOREIGN KEY (magasinier_id) REFERENCES MAGASINIER(id) ON DELETE SET NULL,
    FOREIGN KEY (ligne_commande_id) REFERENCES LIGNE_COMMANDE(id) ON DELETE SET NULL
);

-- =====================================================
-- TABLE FACTURE
-- =====================================================
CREATE TABLE FACTURE (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    commande_id UUID,
    date_facture TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    montant_total DOUBLE PRECISION,
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
CREATE INDEX idx_produit_categorie ON PRODUIT(categorie_id);
CREATE INDEX idx_produit_fournisseur ON PRODUIT(fournisseur_id);
CREATE INDEX idx_ligne_commande_commande ON LIGNE_COMMANDE(commande_id);
CREATE INDEX idx_ligne_commande_produit ON LIGNE_COMMANDE(produit_id);

-- =====================================================
-- DONNÉES INITIALES
-- =====================================================

-- Insérer un admin par défaut
<<<<<<< HEAD
INSERT INTO ADMIN (nom, prenom, email, username, telephone, motDePasse)
VALUES ('Admin', 'System', 'admin@gestionstock.com', 'admin', '0000000000', 'admin123');

-- Quelques catégories exemple
INSERT INTO CATEGORIE (nom, description) VALUES
('Électronique', 'Produits électroniques et accessoires'),
('Vêtements', 'Articles vestimentaires'),
('Alimentation', 'Produits alimentaires'),
('Meubles', 'Mobilier et décoration');

-- Afficher un message de confirmation
DO $$
BEGIN
    RAISE NOTICE 'Base de données gestionStock créée avec succès!';
    RAISE NOTICE 'Admin par défaut: username=admin, password=admin123';
END $$;
=======
INSERT INTO ADMIN (nom, prenom, email, username, telephone, mot_de_passe) 
VALUES ('Admin', 'System', 'admin@gestionstock.com', 'admin', '0000000000', 'admin123');
>>>>>>> origin/meryem2
