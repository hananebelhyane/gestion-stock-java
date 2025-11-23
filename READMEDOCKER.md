# 🐳 Guide Docker - Gestion de Stock

Ce guide explique comment démarrer l'application **Gestion de Stock** avec Docker sur votre machine Windows.

---

## 📋 Prérequis

- **Docker Desktop for Windows** (version 20.10 ou supérieure)
  - [Télécharger Docker Desktop](https://www.docker.com/products/docker-desktop/)
  - ⚠️ Redémarrer Windows après l'installation

Pour vérifier l'installation :
```powershell
docker --version
docker-compose --version
```

---

## 🚀 Démarrage rapide

### 1️⃣ Compiler le projet backend

**Important :** Compilez le projet avant de lancer Docker !

```cmd
cd backend
mvn clean package -DskipTests
cd ..
```

Cela génère le fichier `backend\target\backend-0.0.1-SNAPSHOT.jar` nécessaire pour Docker.

### 2️⃣ Lancer l'application

```powershell
docker-compose up --build
```

**Attendez que les services démarrent...**

Vous devriez voir :
```
mysql_gestion  | ready for connections. Version: '8.4.7'
gestion_backend | Started BackendApplication in X seconds
```

### 3️⃣ Accéder à l'application

- **Backend API** : http://localhost:8080
- **Base de données** : `localhost:3306`
  - Database : `gestionstock`
  - Username : `root`
  - Password : `mouad@2004`

---

## 🛑 Arrêter l'application

```powershell
docker-compose down
```

---

## 🔄 Après modifications du code

```powershell
# 1. Recompiler
cd backend
mvn clean package -DskipTests
cd ..

# 2. Relancer
docker-compose up --build
```

---

## 📁 Structure du projet

```
Gestion de stock/
├── backend/
│   ├── src/
│   ├── target/           # Généré après mvn package
│   ├── Dockerfile
│   └── pom.xml
├── docker-compose.yml
└── README-DOCKER.md
```

---

## 🐛 Problèmes courants

**"Cannot find backend/target/*.jar"**  
→ Vous avez oublié de compiler : `mvn clean package -DskipTests`

**"Port 3306 already in use"**  
→ Arrêtez MySQL local : Win + R → `services.msc` → Arrêter "MySQL"
---
