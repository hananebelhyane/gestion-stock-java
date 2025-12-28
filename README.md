# 🏢 Application de Gestion de Stock

## 📘 Description du Projet
Application desktop complète de **gestion d'inventaire** avec architecture **Full Stack moderne** :
- **Frontend JavaFX** pour l'interface utilisateur
- **Backend Spring Boot** pour l'API REST
- **Base de données MySQL** pour la persistance des données

### 🎯 Objectifs
Digitaliser et optimiser la gestion d'inventaire d'une entreprise :
- ✅ Suivi en temps réel des niveaux de stock
- ✅ Gestion des entrées et sorties produits
- ✅ Import automatique des catalogues fournisseurs
- ✅ Génération des documents commerciaux (bons de livraison, bons de sortie)
- ✅ Gestion multi-utilisateurs (Admin, Magasinier, Client)
- ✅ Alertes de stock automatiques
- ✅ Export CSV/PDF des données
- ✅ Authentification sécurisée

---

## 🚀 Installation et Lancement du Projet

### 📋 Prérequis

Avant de commencer, assurez-vous d'avoir installé :
- **Java JDK 17 ou supérieur** ([Télécharger](https://www.oracle.com/java/technologies/downloads/))
- **Maven 3.6+** ([Télécharger](https://maven.apache.org/download.cgi))
- **MySQL 8.0+** ([Télécharger](https://dev.mysql.com/downloads/mysql/))
- **Git** (optionnel, pour cloner depuis GitHub)

---

## 📦 Méthode 1 : Installation depuis GitHub

### 1️⃣ Cloner le repository
```bash
git clone https://github.com/votre-username/gestion-stock-java.git
cd gestion-stock-java
```

### 2️⃣ Configurer la base de données

**Créer la base de données MySQL :**
```sql
CREATE DATABASE gestionStock CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

**Importer le schéma de données :**
```bash
# Windows (PowerShell)
Get-Content DB.sql | mysql -u root -p gestionStock

# Linux/Mac
mysql -u root -p gestionStock < DB.sql
```

### 3️⃣ Configurer le Backend

Éditer le fichier `backend/src/main/resources/application.properties` :
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/gestionStock
spring.datasource.username=root
spring.datasource.password=VOTRE_MOT_DE_PASSE
```

### 4️⃣ Lancer l'application

**Terminal 1 - Démarrer le Backend :**
```bash
cd backend
mvn clean install
mvn spring-boot:run
```
✅ Le serveur démarre sur `http://localhost:8080`

**Terminal 2 - Démarrer le Frontend :**
```bash
cd frontend
mvn clean install
mvn javafx:run
```
✅ L'interface graphique s'ouvre automatiquement

---

## 📂 Méthode 2 : Installation depuis ZIP

### 1️⃣ Extraire le fichier

**Windows :**
- Clic droit sur le fichier ZIP → Extraire tout
- Ou utiliser 7-Zip / WinRAR

**Linux/Mac :**
```bash
unzip gestion-stock-java.zip
cd gestion-stock-java
```

### 2️⃣ Configurer la base de données

**Créer la base de données MySQL :**
```sql
CREATE DATABASE gestionStock CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

**Importer le schéma de données :**
```bash
# Windows (PowerShell)
Get-Content DB.sql | mysql -u root -p gestionStock

# Linux/Mac
mysql -u root -p gestionStock < DB.sql
```

### 3️⃣ Configurer le Backend

Éditer le fichier `backend/src/main/resources/application.properties` :
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/gestionStock
spring.datasource.username=root
spring.datasource.password=VOTRE_MOT_DE_PASSE
```

### 4️⃣ Lancer l'application

**Windows (PowerShell) :**
```powershell
# Terminal 1 - Backend
cd backend
.\mvnw.cmd clean install
.\mvnw.cmd spring-boot:run

# Terminal 2 - Frontend
cd frontend
mvn clean install
mvn javafx:run
```

**Linux/Mac :**
```bash
# Terminal 1 - Backend
cd backend
./mvnw clean install
./mvnw spring-boot:run

# Terminal 2 - Frontend
cd frontend
mvn clean install
mvn javafx:run
```

---

## 🔑 Connexion par Défaut

Après le premier lancement, utilisez ces identifiants :

| Rôle | Username | Mot de passe |
|------|----------|--------------|
| Admin | admin | admin123 |
| Magasinier | magasinier | mag123 |
| Client | client | client123 |

⚠️ **Sécurité** : Changez ces mots de passe en production !

---

## 🛠️ Technologies Utilisées

### Backend
- **Spring Boot** 3.5.7 - Framework Java
- **MySQL** 8.0.44 - Base de données
- **Hibernate** 6.6.33 - ORM (Object-Relational Mapping)
- **Spring Security** - Authentification JWT
- **Maven** 3.9.11 - Gestionnaire de dépendances

### Frontend
- **JavaFX** 25.0.1 - Interface graphique
- **Java** 25 - Langage de programmation
- **Jackson** - Traitement JSON
- **HttpClient** - Communication avec l'API
- **iText** - Génération de PDF

---

## 📁 Structure du Projet

```
gestion-stock-java/
├── backend/                    # API Spring Boot
│   ├── src/main/java/
│   │   └── com/gestiondestock/
│   │       ├── controller/     # Controllers REST
│   │       ├── service/        # Logique métier
│   │       ├── entity/         # Entités JPA
│   │       └── security/       # JWT & Authentification
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   └── data.sql
│   └── pom.xml
├── frontend/                   # Application JavaFX
│   ├── src/main/java/
│   │   └── gestiondestock/
│   │       ├── controller/     # Controllers UI
│   │       ├── service/        # Services HTTP
│   │       ├── model/          # Modèles de données
│   │       └── app/            # Main Application
│   ├── src/main/resources/
│   │   ├── fxml/              # Fichiers FXML (UI)
│   │   ├── css/               # Styles CSS
│   │   └── assets/            # Images et ressources
│   └── pom.xml
├── DB.sql                      # Script de création de la base
└── README.md
```

---

## 🏗️ Architecture Globale

```
┌─────────────────────┐
│   FRONTEND          │  ← Interface utilisateur (JavaFX)
│   JavaFX Desktop    │
└──────────┬──────────┘
           │ HTTP REST (JSON)
           │ Port 8080
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
```

---

## 🐛 Dépannage

### ❌ Erreur : "Port 8080 déjà utilisé"
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID_NUMBER> /F

# Linux/Mac
lsof -ti:8080 | xargs kill -9
```

### ❌ Erreur de connexion MySQL
Vérifiez que :
- MySQL est démarré : `sudo service mysql status` (Linux) ou Services Windows
- Le port 3306 est accessible
- Les credentials dans `application.properties` sont corrects
- La base de données `gestionStock` existe

### ❌ Erreur "Access denied for user"
```sql
-- Se connecter en root et créer un utilisateur
CREATE USER 'gestionstock'@'localhost' IDENTIFIED BY 'votre_password';
GRANT ALL PRIVILEGES ON gestionStock.* TO 'gestionstock'@'localhost';
FLUSH PRIVILEGES;
```

### ❌ JavaFX ne se lance pas
```bash
# Vérifier la version Java
java -version

# Nettoyer et réinstaller les dépendances
cd frontend
mvn clean install -U
mvn javafx:run
```

### ❌ Erreur "mvn command not found"
Vérifiez que Maven est dans votre PATH :
```bash
# Windows
echo %PATH%

# Linux/Mac
echo $PATH
```
Sinon, utilisez les wrappers Maven fournis : `mvnw` (Linux/Mac) ou `mvnw.cmd` (Windows)

---

## 📅 Étapes de Développement

1. **Conception du projet**
   - Diagrammes UML (cas d'utilisation, classes, séquence)
   - Architecture logicielle et modélisation de la base de données

2. **Développement du Backend**
   - API REST avec Spring Boot
   - Sécurité et authentification JWT
   - Tests unitaires et d'intégration

3. **Développement du Frontend**
   - Interface JavaFX avec FXML
   - Communication avec l'API REST
   - Gestion des sessions utilisateur

---

## 🤝 Contribution

Les contributions sont les bienvenues ! Pour contribuer :
1. Fork le projet
2. Créer une branche (`git checkout -b feature/nouvelle-fonctionnalite`)
3. Commit les changements (`git commit -m 'Ajout nouvelle fonctionnalité'`)
4. Push vers la branche (`git push origin feature/nouvelle-fonctionnalite`)
5. Ouvrir une Pull Request

---

## 📄 Licence

Projet académique – Usage pédagogique et expérimental uniquement.

---

## 📞 Support

Pour toute question ou problème :
- 🐛 Issues GitHub : [Ouvrir un ticket](https://github.com/votre-username/gestion-stock-java/issues)
- 📧 Email : support@gestionstock.com

---

**Développé avec ❤️ par l'équipe Gestion de Stock**
