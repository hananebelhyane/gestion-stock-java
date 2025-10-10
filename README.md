# 🌐 Full Web Application - Gestion de Stock

## 📘 Description du Projet
Ce projet a pour objectif de **concevoir et développer une application web complète** pour la **gestion de stock**, en utilisant une architecture **Full Stack moderne** intégrant le **cloud** et la **conteneurisation**.

##  Objectif
Application pour digitaliser et optimiser la gestion d’inventaire d’une entreprise :
- Suivi en temps réel des niveaux de stock
- Gestion des entrées et sorties produits
- Import automatique des catalogues fournisseurs
- Génération des documents commerciaux (bons de livraison, bons de sortie)
- Réduction des erreurs et optimisation des stocks

L’application sera constituée de trois parties principales :
- **Frontend** : développé avec Angular (version récente ≥ 10)
- **Backend** : développé avec Spring Boot
- **Base de données** : déployée dans un conteneur séparé (ex : MySQL ou PostgreSQL)

---

## 🧱 Architecture du Projet


Chaque composant sera **dockerisé** afin de faciliter le déploiement, la portabilité et la gestion des environnements.

---

## ⚙️ Stack Technique

| Composant        | Technologie utilisée         |
|------------------|------------------------------|
| Frontend         | Angular 10+                  |
| Backend          | Spring Boot                  |
| Base de Données  | MySQL / PostgreSQL           |
| Conteneurisation | Docker, Docker Compose       |
| Cloud            | AWS (EC2, S3, RDS, ECS, etc.)|
| IaC              | Terraform                    |

---

## 🐳 Dockerisation
L’environnement de développement sera totalement conteneurisé à l’aide de **Docker** :
- Un conteneur pour la **Base de Données**
- Un conteneur pour l’**Application Backend**
- Un conteneur pour l’**Application Frontend**

L’orchestration se fera via un fichier **docker-compose.yml**.

---

## ☁️ Déploiement Cloud (AWS)
Le déploiement s’effectuera sur **Amazon Web Services (AWS)** :
- Gestion via **AWS Console Management**
- Automatisation du déploiement grâce à **Terraform** (Infrastructure as Code)

Terraform permettra de :
- Créer les ressources AWS nécessaires (EC2, S3, RDS, etc.)
- Gérer la configuration et la mise à jour de l’infrastructure
- Assurer la reproductibilité de l’environnement cloud

---

## 📅 Étapes principales

1. **Conception du projet**
   - Diagrammes UML, architecture logicielle
2. **Développement du Backend**
   - API REST avec Spring Boot
3. **Développement du Frontend**
   - Interface Angular consommant l’API
4. **Mise en place de Docker**
   - Conteneurisation de chaque module
5. **Déploiement sur AWS**
   - Infrastructure et automatisation via Terraform
---

## 📄 Licence
Projet académique – usage pédagogique et expérimental.
