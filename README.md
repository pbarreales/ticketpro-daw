[README.md](https://github.com/user-attachments/files/28546634/README.md)
# 🎫 TicketPro — Sistema de Gestión de Incidencias Técnicas

> Proyecto Final · 1.º DAW · Curso 2025/2026  
> **Pablo Barreales Ferrandis** — IES La Senia

---

## 📋 Descripción

**TicketPro** es una aplicación web full-stack para la gestión de incidencias técnicas en entornos empresariales o educativos. Permite a los usuarios reportar problemas, a los informáticos gestionarlos y a los administradores supervisar todo el sistema y gestionar roles.

El backend expone una **API REST** con Spring Boot y el frontend es HTML puro que se comunica con ella mediante `fetch()`, sin ningún framework de frontend.

---

## 🛠️ Stack tecnológico

| Capa | Tecnología | Versión |
|---|---|---|
| Lenguaje | Java | 17 |
| Backend | Spring Boot | 4.0.6 |
| Persistencia | Spring Data JPA + Hibernate | — |
| Base de datos | MySQL | 8.0 |
| Seguridad | Spring Security Crypto (BCrypt) | — |
| Frontend | HTML + CSS + Vanilla JS (Fetch API) | — |
| Plantillas | Thymeleaf (solo login/registro) | — |
| Contenedores | Docker + Docker Compose | — |
| Servidor web | Nginx (proxy inverso) | — |
| HTTPS | Let's Encrypt + Certbot | — |
| Build | Maven (Maven Wrapper incluido) | — |

---

## 🗂️ Estructura del proyecto

```
ticketpro-daw/
├── src/
│   └── main/
│       ├── java/com/ticketpro/
│       │   ├── controladores/      # @RestController — endpoints de la API
│       │   ├── modelos/            # Entidades JPA (Usuario, Incidencia, Categoria)
│       │   ├── repositorios/       # JpaRepository — acceso a base de datos
│       │   └── servicios/          # Lógica de negocio (autenticación, BCrypt)
│       └── resources/
│           ├── templates/          # login.html, registro.html, error.html
│           └── static/             # dashboard.html + dashboard.js + favicon
├── ticketpro.sql                   # Esquema + datos semilla
├── docker-compose.yaml             # Infraestructura completa
├── Dockerfile                      # Imagen de la app Spring Boot
└── pom.xml                         # Dependencias Maven
```

---

## 🚀 Cómo ejecutar el proyecto

### Opción A — Con Docker Compose (recomendado)

Es la forma más sencilla. No necesitas tener MySQL instalado localmente.

#### 1. Clona el repositorio

```bash
git clone https://github.com/pbarreales/ticketpro-daw.git
cd ticketpro-daw
```

#### 2. Crea el archivo `.env`

Crea un fichero `.env` en la raíz del proyecto con el siguiente contenido (puedes cambiar los valores):

```env
MYSQL_DB_NAME=ticketpro
MYSQL_ROOT_USER=root
MYSQL_ROOT_PASSWORD=tu_contraseña_segura
```

> ⚠️ Este fichero **no se sube al repositorio** (está en `.gitignore`). Contiene credenciales sensibles.

#### 3. Levanta los contenedores

```bash
docker compose up -d --build
```

Esto arranca dos servicios:
- **`base_datos`** → MySQL 8 en el puerto `3306`. Al crearse por primera vez ejecuta automáticamente `ticketpro.sql`, que crea las tablas e inserta los usuarios de prueba.
- **`aplicacion_spring`** → Spring Boot en el puerto `8080`. Espera a que MySQL esté listo antes de arrancar.

#### 4. Accede a la aplicación

Abre tu navegador en:

```
http://localhost:8080/login
```

---

### Opción B — Ejecución local sin Docker

Si tienes Java 17 y MySQL instalados en tu máquina:

#### 1. Crea la base de datos

```bash
mysql -u root -p < ticketpro.sql
```

#### 2. Configura `application.properties`

Edita `src/main/resources/application.properties` con tus credenciales de MySQL:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ticketpro
spring.datasource.username=root
spring.datasource.password=tu_contraseña
spring.jpa.hibernate.ddl-auto=update
```

#### 3. Compila y ejecuta

```bash
./mvnw spring-boot:run
```

O si `mvnw` no tiene permisos:

```bash
chmod +x mvnw && ./mvnw spring-boot:run
```

#### 4. Accede a la aplicación

```
http://localhost:8080/login
```

---

## 👤 Usuarios de prueba

La base de datos se inicializa con dos usuarios predefinidos. Sus contraseñas están hasheadas con **BCrypt**.

| Rol | Email | Contraseña |
|---|---|---|
| `ADMIN` | `admin@ticketpro.com` | `1234` |
| `INFORMATICO` | `tecnico@ticketpro.com` | `1234` |

Para probar como usuario normal, puedes registrarte desde la página `/registro`.

---

## 🔄 Flujo de uso de la aplicación

### Como Usuario (USUARIO)

1. Ve a `/registro` y crea una cuenta
2. Inicia sesión en `/login`
3. En el dashboard, haz clic en **"Nueva Incidencia"**
4. Rellena el formulario: título, descripción y nivel de prioridad
5. Envía el ticket — quedará en estado **Abierto**
6. Puedes ver el estado de todos tus tickets en la tabla

### Como Informático (INFORMATICO)

1. Inicia sesión con las credenciales de técnico
2. En el dashboard verás **todos los tickets** del sistema
3. Puedes cambiar el estado de cualquier ticket: `Abierto → En Progreso → Resuelto`
4. Una vez resuelto, el usuario verá el cambio en tiempo real

### Como Administrador (ADMIN)

1. Inicia sesión con las credenciales de administrador
2. Verás el dashboard completo y además el **Panel de Control Global**
3. Desde ahí puedes:
   - Ver todos los usuarios registrados
   - **Cambiar el rol** de cualquier usuario (`USUARIO`, `INFORMATICO`, `ADMIN`)
   - **Eliminar** usuarios del sistema

---

## 🗄️ Base de datos

El esquema tiene **3 tablas**:

- **`usuarios`** — email único, contraseña BCrypt, rol
- **`incidencias`** — título, descripción, estado, prioridad, fechas y tres FK:
  - `cliente_id` → `usuarios` · `ON DELETE CASCADE`
  - `informatico_id` → `usuarios` · `ON DELETE SET NULL`
  - `categoria_id` → `categorias` · `ON DELETE SET NULL`
- **`categorias`** — id y nombre

---

## 🔌 API REST — Endpoints principales

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/api/auth/login` | Autenticación, devuelve `{ usuarioId, nombre, rol }` |
| `POST` | `/api/auth/registro` | Registro de nuevo usuario |
| `GET` | `/api/incidencias` | Devuelve todas las incidencias |
| `GET` | `/api/incidencias/cliente/{id}` | Incidencias de un usuario concreto |
| `POST` | `/api/incidencias` | Crear nueva incidencia |
| `PUT` | `/api/incidencias/{id}/estado` | Actualizar estado de una incidencia |
| `GET` | `/api/usuarios` | Lista todos los usuarios (admin) |
| `PUT` | `/api/usuarios/{id}/rol` | Cambiar el rol de un usuario |
| `DELETE` | `/api/usuarios/{id}` | Eliminar un usuario |

---

## 👨‍💻 Autor

**Pablo Barreales Ferrandis**  
1.º DAW — Curso 2025/2026  
IES La Senia

---

## 📄 Licencia

Proyecto de fin de curso con fines educativos.
