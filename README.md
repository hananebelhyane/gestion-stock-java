# 🌐 Full Web Application - Gestion de Stock

## 📘 Description du Projet
Ce projet a pour objectif de **concevoir et développer une application web complète** pour la **gestion de stock**, en utilisant une architecture **Full Stack moderne**.

##  Objectif
Application pour digitaliser et optimiser la gestion d’inventaire d’une entreprise :
- Suivi en temps réel des niveaux de stock
- Gestion des entrées et sorties produits
- Import automatique des catalogues fournisseurs
- Génération des documents commerciaux (bons de livraison, bons de sortie)
- Réduction des erreurs et optimisation des stocks

L’application sera constituée de trois parties principales :
- **Frontend** : développé avec Javafx (version récente ≥ 10)
- **Backend** : développé avec Spring Boot.
- **Base de données** : déployée dans un conteneur séparé  MySQL.

<<<<<<< HEAD
---

## ⚙️ Stack Technique
=======
1. **Démarrer le Backend** :
```bash
cd backend
mvn spring-boot:run
mvn clean install
```
Le serveur démarre sur `http://localhost:8080`

2. **Démarrer le Frontend** :
```bash
cd frontend
mvn javafx:run
mvn clean install
```
>>>>>>> origin/meryem2

| Composant        | Technologie utilisée         |
|------------------|------------------------------|
| Frontend         | Javafx                       |
| Backend          | Spring Boot                  |
| Base de Données  | MySQL                        |
                  
---

## 📅 Étapes principales

1. **Conception du projet**
   - Diagrammes UML, architecture logicielle
2. **Développement du Backend**
   - API REST avec Spring Boot
3. **Développement du Frontend**
   - Interface Javafx consommant l’API
   ---------------------------------------

<<<<<<< HEAD
## 📄 Licence
Projet académique – usage pédagogique et expérimental.
=======
### Configuration Base de Données

1. Créer la base de données :
```sql
CREATE DATABASE gestionStock;
USE gestionStock;
```

2. Exécuter le script `DB.sql`

3. Configurer `backend/src/main/resources/application.properties` :
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/gestionStock
spring.datasource.username=root
spring.datasource.password=votre_mot_de_passe
```

## 🛠️ Technologies

**Backend :**
- Spring Boot 3.5.7
- MySQL 8.0.44
- Hibernate 6.6.33

**Frontend :**
- JavaFX 25.0.1
- Maven 3.9.11
- Java Version : 25
- Scene Builder (pour l'édition FXML)

## Architecture Globale :

┌─────────────────────┐
│   FRONTEND          │  ← Interface utilisateur (JavaFX)
│   JavaFX Desktop    │
└──────────┬──────────┘
           │ HTTP REST (JSON)
           │ Port 8082
┌──────────▼──────────┐
│   BACKEND           │  ← Logique métier (Spring Boot)
│   Spring Boot API   │
└──────────┬──────────┘
           │ JDBC
           │
┌──────────▼──────────┐
│   DATABASE          │  ← Persistance (MySQL)
│   MySQL 8.0.44      │
└─────────────────────┘



# Docker

# 1. Récupérer votre branche
git pull origin votre-branche

# 2. Compiler (chacun génère son propre JAR)
cd backend
mvn clean package -DskipTests
cd ..

# 3. Lancer Docker
docker-compose up --build
>>>>>>> origin/meryem2
