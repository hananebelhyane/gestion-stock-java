#  Application de Gestion de Stock

Application desktop de gestion d'inventaire avec **JavaFX** (Frontend) + **Spring Boot** (Backend) + **MySQL** (Base de données).

---

##  Prérequis

Installez ces logiciels avant de commencer :
- **Java JDK 17+** 
- **Maven 3.6+** 
- **MySQL 8.0+** 

---

##  Installation

### 1️ Configuration de la Base de Données

**Ouvrir MySQL Workbench ou ligne de commande MySQL :**
```sql
CREATE DATABASE gestionStock;
```

**Importer le script de création des tables :**
```powershell
Get-Content DB.sql | mysql -u root -p gestionStock
```
*(Entrez votre mot de passe MySQL quand demandé)*

### 2️ Configuration du Backend

Ouvrir le fichier : `backend/src/main/resources/application.properties`

Modifier ces lignes :
```properties
spring.datasource.username=root
spring.datasource.password=VOTRE_MOT_DE_PASSE_MYSQL
```

---

##  Lancement du Projet

### Ouvrir 2 Terminaux PowerShell

**Terminal 1 - Backend :**
```powershell
cd backend
mvn spring-boot:run
```
✅ Attendez le message : **"Started BackendApplication in X seconds"**

**Terminal 2 - Frontend :**
```powershell
cd frontend
mvn javafx:run
```
✅ L'interface graphique s'ouvre

---

## 🔑 Connexion

| Rôle | Identifiant | Mot de passe |
|------|-------------|--------------|
| Admin | admin | admin123 |
| Magasinier | magasinier | mag123 |
| Client | client | client123 |

---

## 🐛 Problèmes Courants

**Erreur "Port 8080 déjà utilisé" :**
```powershell
netstat -ano | findstr :8080
taskkill /PID <NUMERO_PID> /F
```

**Erreur de connexion MySQL :**
- Vérifiez que MySQL est démarré (Services Windows)
- Vérifiez le mot de passe dans `application.properties`

**JavaFX ne démarre pas :**
```powershell
cd frontend
mvn clean install
mvn javafx:run
```

---

**Technologies** : Spring Boot 3.5.7 | JavaFX 25.0.1 | MySQL 8.0 | Maven 3.9.11
