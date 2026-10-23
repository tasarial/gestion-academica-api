# Instrucciones AWS – Base de datos (RDS) y Despliegue

Guía paso a paso para tu API de **Gestión Académica**.

---

# PARTE 1: Base de datos en AWS (RDS MySQL)

## 1.1 Crear cuenta / entrar a AWS

1. Entra a https://aws.amazon.com y crea una cuenta (o inicia sesión).
2. En la barra superior elige la **región** (ej. `us-east-1` N. Virginia o `us-east-2`).
3. Busca el servicio **RDS** → **Databases**.

## 1.2 Crear instancia RDS MySQL

1. Clic en **Create database**.
2. Elige:
   - **Standard create**
   - Engine: **MySQL**
   - Version: 8.0.x
3. **Templates**: elige **Free tier** (si está disponible en tu cuenta).
4. **Settings**:
   - DB instance identifier: `gestion-academica-db`
   - Master username: `admin`
   - Master password: **una contraseña fuerte** (guárdala)
5. **Instance configuration**:
   - Clase: `db.t3.micro` o `db.t4g.micro` (free tier)
6. **Storage**:
   - Tipo: General Purpose SSD (gp2/gp3)
   - Tamaño: 20 GB (mínimo free tier)
7. **Connectivity**:
   - VPC: default
   - **Public access: Yes** (para poder conectar desde tu PC y desde Railway/Render/EC2)
   - VPC security group: crear nuevo, nombre `gestion-academica-sg`
   - Availability Zone: No preference
8. **Database authentication**: Password authentication
9. **Additional configuration**:
   - Initial database name: `gestion_academica`
   - Backup: puedes dejar 1 día o desactivar si es solo prueba
10. Clic en **Create database**.

Espera 5–10 minutos hasta que el estado sea **Available**.

## 1.3 Abrir el puerto 3306 (Security Group)

1. En RDS → tu instancia → pestaña **Connectivity & security**.
2. Clic en el **VPC security group**.
3. Pestaña **Inbound rules** → **Edit inbound rules**.
4. Añade:
   - Type: **MySQL/Aurora**
   - Port: **3306**
   - Source: **My IP** (para probar desde tu PC)
   - Opcional (despliegue en cloud): otra regla con Source `0.0.0.0/0`  
     ⚠️ Solo para tareas académicas. En producción limita por IP o usa private subnet.
5. Guardar.

## 1.4 Anotar datos de conexión

En la instancia RDS copia:

| Dato | Dónde está |
|------|------------|
| **Endpoint** | Connectivity & security → Endpoint (ej. `gestion-academica-db.xxxxx.us-east-1.rds.amazonaws.com`) |
| **Port** | 3306 |
| **DB name** | `gestion_academica` |
| **User** | `admin` |
| **Password** | la que definiste |

URL JDBC resultante:

```text
jdbc:mysql://TU-ENDPOINT:3306/gestion_academica?useSSL=true&serverTimezone=UTC&allowPublicKeyRetrieval=true
```

## 1.5 Probar conexión desde tu PC (opcional)

Con MySQL Workbench, DBeaver o línea de comandos:

```bash
mysql -h TU-ENDPOINT -P 3306 -u admin -p
```

Si entra, la BD está lista. **No hace falta crear tablas**: Hibernate las crea al arrancar la API (`ddl-auto: update`).

---

# PARTE 2: Probar la API en local con Docker

## 2.1 Requisitos

- Docker Desktop instalado y corriendo
- (Opcional) Java 17 + Maven si quieres correr sin Docker

## 2.2 Opción A – Todo local (MySQL en Docker)

```bash
cd gestion-academica-api
docker compose up --build
```

Prueba: http://localhost:8080/api/cursos

## 2.3 Opción B – API local apuntando a RDS de AWS

```bash
docker build -t gestion-academica-api .

docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL="jdbc:mysql://TU-ENDPOINT:3306/gestion_academica?useSSL=true&serverTimezone=UTC&allowPublicKeyRetrieval=true" \
  -e SPRING_DATASOURCE_USERNAME="admin" \
  -e SPRING_DATASOURCE_PASSWORD="TuPassword" \
  gestion-academica-api
```

Si el contenedor arranca sin errores de conexión, la API ya usa tu BD en AWS.

## 2.4 Probar con curl o Postman

```bash
# Crear curso
curl -X POST http://localhost:8080/api/cursos \
  -H "Content-Type: application/json" \
  -d "{\"nombre\":\"Ingeniería de Requisitos\",\"codigo\":\"IR-301\",\"creditos\":3,\"horario\":\"Lunes 8-10\",\"docente\":\"Prof. García\"}"

# Listar cursos
curl http://localhost:8080/api/cursos

# Crear inscripción (usa el id del curso creado)
curl -X POST http://localhost:8080/api/inscripciones \
  -H "Content-Type: application/json" \
  -d "{\"estudianteNombre\":\"Ana López\",\"estudianteEmail\":\"ana@uni.edu\",\"estado\":\"ACTIVA\",\"curso\":{\"id\":1}}"
```

---

# PARTE 3: Despliegue de la API

Tienes 3 caminos. El más fácil para estudiantes suele ser Railway o Render. AWS EC2 también cumple.

---

## Opción 3A – Railway (recomendada por facilidad)

1. Sube el proyecto a **GitHub** (repo público o privado).
2. Entra a https://railway.app → Login con GitHub.
3. **New Project** → **Deploy from GitHub repo** → elige tu repo.
4. Railway detecta el Dockerfile.
5. En **Variables** añade:
   - `SPRING_DATASOURCE_URL` = `jdbc:mysql://TU-ENDPOINT:3306/gestion_academica?useSSL=true&serverTimezone=UTC&allowPublicKeyRetrieval=true`
   - `SPRING_DATASOURCE_USERNAME` = `admin`
   - `SPRING_DATASOURCE_PASSWORD` = tu password
6. En **Settings** → Networking → **Generate Domain**.
7. Espera el deploy. URL final tipo: `https://gestion-academica-api-production.up.railway.app/api/cursos`

**Importante:** en el Security Group de RDS debe permitir conexiones desde internet (`0.0.0.0/0` en puerto 3306) o la IP de Railway no podrá conectar.

---

## Opción 3B – Render

1. Repo en GitHub.
2. https://render.com → **New** → **Web Service**.
3. Conecta el repo.
4. Environment: **Docker**.
5. Añade las mismas 3 variables de entorno de la BD.
6. Create Web Service.
7. Usa la URL que te dan + `/api/cursos`.

---

## Opción 3C – AWS EC2 (todo en AWS)

### Crear instancia EC2

1. Servicio **EC2** → **Launch instance**.
2. Name: `gestion-academica-api`
3. AMI: **Amazon Linux 2023** o Ubuntu 22.04
4. Instance type: `t2.micro` / `t3.micro` (free tier)
5. Key pair: crea una (descarga el `.pem`)
6. Security group:
   - SSH (22) desde My IP
   - Custom TCP **8080** desde Anywhere (`0.0.0.0/0`)
7. Launch.

### Conectar y preparar

```bash
# Desde tu PC (ajusta ruta del .pem y la IP pública de EC2)
ssh -i tu-clave.pem ec2-user@IP-PUBLICA-EC2
# Si es Ubuntu: usuario ubuntu en lugar de ec2-user
```

Dentro de la instancia:

```bash
# Instalar Docker (Amazon Linux 2023)
sudo yum update -y
sudo yum install -y docker
sudo systemctl start docker
sudo systemctl enable docker
sudo usermod -aG docker ec2-user
# Cierra sesión y vuelve a entrar para que el grupo docker aplique
```

### Subir imagen o construir en EC2

**Opción fácil – construir en EC2:**

```bash
# Instalar Git
sudo yum install -y git

git clone https://github.com/TU-USUARIO/gestion-academica-api.git
cd gestion-academica-api

sudo docker build -t gestion-academica-api .

sudo docker run -d -p 8080:8080 --name api \
  -e SPRING_DATASOURCE_URL="jdbc:mysql://TU-ENDPOINT:3306/gestion_academica?useSSL=true&serverTimezone=UTC&allowPublicKeyRetrieval=true" \
  -e SPRING_DATASOURCE_USERNAME="admin" \
  -e SPRING_DATASOURCE_PASSWORD="TuPassword" \
  gestion-academica-api
```

Prueba: `http://IP-PUBLICA-EC2:8080/api/cursos`

---

# PARTE 4: Entrega de la tarea

Debes entregar:

1. **Repositorio** (GitHub) con el código + Dockerfile + README
2. **Enlace vivo** de la API funcionando (Railway / Render / EC2)
3. README con:
   - Cómo levantar
   - Endpoints
   - Variables de entorno
   - Relación 1:N explicada

Ejemplo de comprobación del enlace vivo:

```
GET https://tu-url.railway.app/api/cursos
→ [] o lista de cursos (status 200)
```

---

# Checklist rápido

- [ ] RDS MySQL creado y **Available**
- [ ] Security group abre puerto 3306
- [ ] Base `gestion_academica` existe (o se crea sola)
- [ ] API corre en local con variables de entorno apuntando a RDS
- [ ] Dockerfile construye sin error
- [ ] Desplegado en Railway / Render / EC2
- [ ] Variables de entorno configuradas en el cloud
- [ ] GET /api/cursos responde 200
- [ ] CRUD de Curso e Inscripción probado
- [ ] Repo + README + enlace vivo listos

---

# Problemas comunes

| Error | Solución |
|-------|----------|
| `Communications link failure` | Security group no permite tu IP / 0.0.0.0/0 en 3306 |
| `Access denied for user` | Usuario o password incorrectos |
| `Unknown database` | Crea `gestion_academica` en RDS o ponla en Initial database name |
| Contenedor se cae al arrancar | Revisa logs: `docker logs nombre-contenedor` |
| 404 en cloud | Usa la ruta completa `/api/cursos` y puerto correcto |

---

Si te atascas en algún paso (RDS, Security Group, Docker o Railway), indícame en cuál y el mensaje de error exacto.
