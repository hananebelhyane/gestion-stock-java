# Gestion de Stock

Application de gestion de stock avec JavaFX (frontend) et Spring Boot (backend).


### Lancer l'Application

1. **Démarrer le Backend** :
```bash
cd backend
mvn spring-boot:run
```
Le serveur démarre sur `http://localhost:8082`

2. **Démarrer le Frontend** :
```bash
cd frontend
mvn javafx:run
```


### Prérequis

- Java 25
- Maven 3.9+
- MySQL 8.0+
- JavaFX 25.0.1

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
