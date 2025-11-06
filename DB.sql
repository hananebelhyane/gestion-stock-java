CREATE DATABASE IF NOT EXISTS gestionStock;
USE gestionStock;

CREATE TABLE ADMIN (
    admin_id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(50),
    prenom VARCHAR(50),
    email VARCHAR(60),
    username VARCHAR(50),
    telephone VARCHAR(20),
    mot_de_passe VARCHAR(255)
);

CREATE TABLE CLIENT (
    client_id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(50),
    prenom VARCHAR(50),
    username VARCHAR(50),
    telephone VARCHAR(20),
    mot_de_passe VARCHAR(255),
    adresse VARCHAR(100),
    deletedBy INT NULL,
    deletedAt DATETIME NULL
);

CREATE TABLE MAGASINIER (
    magasinier_id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(50),
    prenom VARCHAR(50),
    username VARCHAR(50),
    telephone VARCHAR(20),
    mot_de_passe VARCHAR(255),
    deletedBy INT NULL,
    deletedAt DATETIME NULL
);

CREATE TABLE FOURNISSEUR (
    fournisseur_id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(50),
    prenom VARCHAR(50),
    email VARCHAR(60),
    telephone VARCHAR(20),
    adresse VARCHAR(100),
    deletedBy INT NULL,
    deletedAt DATETIME NULL
);
CREATE TABLE CATEGORIE(
    categorie_id INT AUTOINCREMENT PRIMARY KEY,
    nom VARCHAR(100),
    description varchar(100)
);

CREATE TABLE PRODUIT (
    produit_id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    description TEXT,
    categorie_id int,
    prix_unitaire DECIMAL(10,2),
    url_image VARCHAR(255),
    fournisseur_id INT,
    FOREIGN KEY (fournisseur_id) REFERENCES FOURNISSEUR(fournisseur_id),
    FOREIGN KEY (categorie_id) REFERENCES CATEGORIE(categorie_id)
);

CREATE TABLE COMMANDE_FOURNISSEUR (
    commande_id INT AUTO_INCREMENT PRIMARY KEY,
    produit_id INT,
    commande_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    commande_status ENUM('en_attente', 'livree', 'annulee') DEFAULT 'en_attente',
    FOREIGN KEY (produit_id) REFERENCES PRODUIT(produit_id)
);

CREATE TABLE COMMANDE_CLIENT (
    commande_id INT AUTO_INCREMENT PRIMARY KEY,
    client_id INT NOT NULL,
    date_commande DATETIME DEFAULT CURRENT_TIMESTAMP,
    statut ENUM('en_attente', 'confirmee', 'annulee') DEFAULT 'en_attente',
    seuil_max INT,
    FOREIGN KEY (client_id) REFERENCES CLIENT(client_id)
);

CREATE TABLE LIGNE_COMMANDE (
    ligne_commande_id INT AUTO_INCREMENT PRIMARY KEY,
    commande_id INT,
    produit_id INT,
    quantite INT,
    prix_unitaire DECIMAL(10,2),
    montant_total DECIMAL(10,2),
    FOREIGN KEY (commande_id) REFERENCES COMMANDE_CLIENT(commande_id),
    FOREIGN KEY (produit_id) REFERENCES PRODUIT(produit_id)
);

CREATE TABLE STOCK (
    id INT AUTO_INCREMENT PRIMARY KEY,
    produit_id INT NOT NULL,
    quantite_disponible INT DEFAULT 0,
    seuil_alerte INT DEFAULT 5,
    FOREIGN KEY (produit_id) REFERENCES PRODUIT(produit_id)
);

CREATE TABLE ENTREES_STOCK (
    id INT AUTO_INCREMENT PRIMARY KEY,
    produit_id INT NOT NULL,
    quantite INT NOT NULL,
    date_entree DATETIME DEFAULT CURRENT_TIMESTAMP,
    magasinier_id INT,
    commande_fournisseur_id INT,
    FOREIGN KEY (produit_id) REFERENCES PRODUIT(produit_id),
    FOREIGN KEY (magasinier_id) REFERENCES MAGASINIER(magasinier_id),
    FOREIGN KEY (commande_fournisseur_id) REFERENCES COMMANDE_FOURNISSEUR(commande_id)
);

CREATE TABLE SORTIES_STOCK (
    id INT AUTO_INCREMENT PRIMARY KEY,
    produit_id INT NOT NULL,
    quantite INT NOT NULL,
    date_sortie DATETIME DEFAULT CURRENT_TIMESTAMP,
    magasinier_id INT,
    ligne_commande_id INT,
    FOREIGN KEY (produit_id) REFERENCES PRODUIT(produit_id),
    FOREIGN KEY (magasinier_id) REFERENCES MAGASINIER(magasinier_id),
    FOREIGN KEY (ligne_commande_id) REFERENCES LIGNE_COMMANDE(ligne_commande_id)
);

CREATE TABLE ALERTES_STOCK (
    id INT AUTO_INCREMENT PRIMARY KEY,
    produit_id INT NOT NULL,
    date_alerte DATETIME DEFAULT CURRENT_TIMESTAMP,
    message TEXT,
    statut ENUM('non_lu', 'traite') DEFAULT 'non_lu',
    FOREIGN KEY (produit_id) REFERENCES PRODUIT(produit_id)
);
CREATE TABLE FACTURE (
    id_facture INT AUTO_INCREMENT PRIMARY KEY,
    commande_id INT,
    date_facture DATETIME DEFAULT CURRENT_TIMESTAMP,
    montant_total DECIMAL(10,2),
    FOREIGN KEY (commande_id) REFERENCES COMMANDE_CLIENT(commande_id)
);
