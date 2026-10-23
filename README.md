# API REST – Gestión Académica

API REST con **Spring Boot** (arquitectura en capas) para el dominio **Gestión Académica**.

**Entidades relacionadas (1:N):**
- **Curso** (1) → tiene muchas → **Inscripción** (N)

---

## Stack

| Tecnología | Uso |
|------------|-----|
| Java 17 | Lenguaje |
| Spring Boot 3.2 | Framework |
| Spring Data JPA | Persistencia |
| MySQL 8 | Base de datos (AWS RDS o local) |
| Docker | Contenedor de la API |
| Maven | Build |

---

## Arquitectura en capas

```
Controller  →  recibe HTTP
Service     →  lógica de negocio
Repository  →  acceso a datos (JPA)
Entity      →  modelo / tablas
```

---

## Endpoints

### Cursos `/api/cursos`

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/cursos` | Listar todos |
| GET | `/api/cursos/{id}` | Obtener uno |
| POST | `/api/cursos` | Crear |
| PUT | `/api/cursos/{id}` | Actualizar |
| DELETE | `/api/cursos/{id}` | Eliminar |
| GET | `/api/cursos/{id}/inscripciones` | Inscripciones de un curso |

### Inscripciones `/api/inscripciones`

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/inscripciones` | Listar todas |
| GET | `/api/inscripciones/{id}` | Obtener una |
| POST | `/api/inscripciones` | Crear |
| PUT | `/api/inscripciones/{id}` | Actualizar |
| DELETE | `/api/inscripciones/{id}` | Eliminar |

---

## Ejemplos de body (JSON)

**Crear curso**
```json
{
  "nombre": "Ingeniería de Requisitos",
  "codigo": "IR-301",
  "creditos": 3,
  "horario": "Lunes 8:00-10:00",
  "docente": "Prof. García"
}
```

**Crear inscripción**
```json
{
  "estudianteNombre": "Ana López",
  "estudianteEmail": "ana.lopez@universidad.edu",
  "estado": "ACTIVA",
  "curso": { "id": 1 }
}
```

---

## Variables de entorno (conexión a BD)

| Variable | Ejemplo |
|----------|---------|
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://TU-ENDPOINT.rds.amazonaws.com:3306/gestion_academica?useSSL=true&serverTimezone=UTC` |
| `SPRING_DATASOURCE_USERNAME` | `admin` |
| `SPRING_DATASOURCE_PASSWORD` | `TuPasswordSeguro` |

---

## Cómo ejecutar

### 1) Local con Docker Compose (BD incluida)

```bash
docker compose up --build
```

API: http://localhost:8080/api/cursos

### 2) Solo API (BD en AWS RDS)

```bash
docker build -t gestion-academica-api .
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL="jdbc:mysql://TU-RDS-ENDPOINT:3306/gestion_academica?useSSL=true&serverTimezone=UTC" \
  -e SPRING_DATASOURCE_USERNAME="admin" \
  -e SPRING_DATASOURCE_PASSWORD="TuPassword" \
  gestion-academica-api
```

### 3) Sin Docker (Maven + MySQL local)

```bash
# Crear BD: CREATE DATABASE gestion_academica;
mvn spring-boot:run
```

---

## Despliegue

Ver la sección **Instrucciones AWS y despliegue** más abajo, o el archivo de pasos detallados entregado junto al proyecto.

Plataformas posibles:
- **Railway** / **Render** (más fácil con Docker)
- **AWS** (EC2, Elastic Beanstalk o ECS) + **RDS**

---

## Estructura del proyecto

```
gestion-academica-api/
├── Dockerfile
├── docker-compose.yml
├── pom.xml
├── README.md
└── src/main/java/com/gestionacademica/
    ├── GestionAcademicaApplication.java
    ├── controller/
    ├── service/
    ├── repository/
    ├── entity/
    └── exception/
```

---

## Autor

Proyecto académico – Gestión Académica (Curso ↔ Inscripción)
