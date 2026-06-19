# Deporte Connect Backend

API REST para coordinar actividades deportivas: registro y autenticación de usuarios, perfiles, deportes, ubicaciones, actividades, participantes y mensajería asociada a cada actividad.

## Tecnologías

- Java 17
- Spring Boot 3.2.5
- Spring Web, Spring Security y Spring Data JPA
- PostgreSQL en entornos desplegados y H2 para ejecución local
- JWT con JJWT 0.12.5
- OpenAPI/Swagger UI con Springdoc 2.5.0
- Maven

## Arquitectura

El código sigue una separación por capas:

```text
src/main/java/com/deporteconnect/
├── config/       # Seguridad, CORS, OpenAPI y filtros HTTP
├── controller/   # Endpoints REST
├── dto/          # Contratos de entrada y salida
├── exception/    # Excepciones y manejo global de errores
├── model/        # Entidades JPA y enumeraciones
├── repository/   # Repositorios Spring Data JPA
├── security/     # Generación y validación de JWT
└── service/      # Reglas de negocio
```

Entidades principales: `User`, `Sport`, `Location`, `Activity`, `Participation` y `Message`.

## Requisitos

- JDK 17
- Maven Wrapper incluido (descarga Maven 3.9.9) o Maven 3.9 instalado
- PostgreSQL opcional; sin variables de entorno se utiliza H2 en memoria

## Configuración

La aplicación no necesita credenciales para arrancar localmente. Por defecto utiliza H2 en memoria y una clave JWT exclusiva para desarrollo.

Para PostgreSQL o despliegue, configura estas variables de entorno:

| Variable | Requerida en producción | Valor local predeterminado |
|---|---:|---|
| `DB_URL` | Sí | H2 en memoria, modo PostgreSQL |
| `DB_USERNAME` | Sí | `sa` |
| `DB_PASSWORD` | Sí | vacío |
| `JWT_SECRET` | Sí | clave de desarrollo; no usar en producción |
| `JWT_EXPIRATION_MS` | No | `86400000` (24 horas) |
| `JPA_DDL_AUTO` | No | `update` |
| `SQL_INIT_MODE` | No | `never` |
| `CORS_ALLOWED_ORIGINS` | Sí | `*` |

Ejemplo en PowerShell:

```powershell
$env:DB_URL = "jdbc:postgresql://localhost:5432/deporte_connect"
$env:DB_USERNAME = "deporte_connect"
$env:DB_PASSWORD = "cambia-este-valor"
$env:JWT_SECRET = "una-clave-aleatoria-de-al-menos-32-bytes"
.\mvnw.cmd spring-boot:run
```

No guardes valores reales en `application.properties`, archivos `.env` ni documentación versionada. Los archivos locales y formatos habituales de claves están cubiertos por `.gitignore`.

## Ejecución

```bash
./mvnw clean spring-boot:run
```

La API queda disponible en `http://localhost:8080`.

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Especificación OpenAPI: `http://localhost:8080/v3/api-docs`

## Endpoints principales

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| `POST` | `/auth/register` | Público | Registrar usuario |
| `POST` | `/auth/login` | Público | Iniciar sesión y obtener JWT |
| `POST` | `/auth/forgot-password` | Público | Iniciar recuperación de contraseña |
| `POST` | `/auth/reset-password` | Público | Restablecer contraseña |
| `GET` | `/deportes` | Público | Listar deportes |
| `GET`, `PUT` | `/usuarios/me` | JWT | Consultar o actualizar el perfil propio |
| `PUT` | `/usuarios/me/avatar` | JWT | Actualizar avatar |
| `GET` | `/usuarios/{id}` | JWT | Consultar usuario |
| `GET`, `POST` | `/ubicaciones` | JWT | Listar o crear ubicaciones |
| `GET`, `POST` | `/actividades` | JWT | Listar o crear actividades |
| `GET` | `/actividades/mis-partidos` | JWT | Listar actividades del usuario |
| `GET`, `DELETE` | `/actividades/{id}` | JWT | Consultar o cancelar actividad |
| `POST` | `/actividades/{id}/unirse` | JWT | Unirse a una actividad |
| `DELETE` | `/actividades/{id}/salirse` | JWT | Salir de una actividad |
| `GET`, `POST` | `/actividades/{activityId}/mensajes` | JWT | Consultar o enviar mensajes |

Para rutas protegidas envía `Authorization: Bearer <jwt>`.

## Compilación y pruebas

```bash
./mvnw clean verify
```

Actualmente el proyecto declara las dependencias de prueba de Spring Boot y Spring Security, pero no contiene pruebas automatizadas en `src/test`. Antes de un despliegue productivo se recomienda cubrir al menos autenticación, autorización, participación en actividades y repositorios.

## Consideraciones de seguridad

- Las contraseñas se almacenan con BCrypt.
- La API utiliza sesiones stateless y JWT.
- `JWT_SECRET`, las credenciales de base de datos y los orígenes CORS deben configurarse por entorno.
- El valor CORS predeterminado (`*`) solo es apropiado para desarrollo.
- El flujo actual de `reset-password` es académico: permite cambiar una contraseña conociendo solamente el correo. Debe reemplazarse por un token temporal, de un solo uso y enviado por un canal verificado antes de exponer la API públicamente.
- Si una credencial real fue versionada alguna vez, cambiar el archivo no basta: hay que revocarla/rotarla y limpiar el historial antes de publicar.

## Estado del proyecto

La aplicación tiene una estructura coherente, pero no debe considerarse lista para producción mientras no se corrija el restablecimiento de contraseña, se restrinja CORS y se añadan pruebas automatizadas. Para publicar el código como portafolio o proyecto académico, revisa primero la lista de seguridad anterior y usa únicamente secretos gestionados fuera de Git.

## Licencia

El repositorio no incluye una licencia. Añade una antes de publicarlo si quieres conceder permisos explícitos de uso, modificación o distribución.
