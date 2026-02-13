Rentatools - Plataforma de Alquiler de Herramientas

🔗 **Repositorio del Backend:** [Herramientas-Frontend](https://github.com/Duban0110/Herramientas-Frontend.git)

Rentatools es una solución integral diseñada para digitalizar y optimizar el proceso de alquiler de maquinaria pesada y herramientas de construcción. La plataforma conecta a proveedores de equipos con clientes finales, garantizando la trazabilidad, seguridad financiera y control de inventario en tiempo real.

Tecnologías Utilizadas

    Lenguaje: Java 17+

    Framework: Spring Boot 3.x

    Base de Datos: MySQL 8.0

    Seguridad: Spring Security & JWT (JSON Web Tokens)

    Persistencia: Spring Data JPA

    Documentación: Swagger / OpenAPI 3.0

    Herramientas de Gestión: Trello (Tablero Kanban)


🛠️ Instalación y Configuración
1. Requisitos Previos

    JDK 17 o superior.

    Maven 3.6+.

    Servidor MySQL activo.

2. Clonar el repositorio
   git clone https://github.com/tu-usuario/rentatools.git
   cd rentatools
   
3. Configuración del entorno (application.properties)

Crea o edita el archivo src/main/resources/application.properties con los siguientes datos:
# Configuración de la Base de Datos
spring.datasource.url=jdbc:mysql://localhost:3306/rentatools_db?createDatabaseIfNotExist=true
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
spring.jpa.hibernate.ddl-auto=update

# Configuración de Seguridad (JWT)
jwt.secret.key=TuClaveSecretaMuyLargaParaSeguridadDe6Dias
jwt.expiration.time=86400000

# Puerto del servidor
server.port=8080

Arquitectura de Datos

El sistema utiliza un modelo relacional normalizado para garantizar la integridad de las transacciones y la gestión de stock.

Autenticación y Roles

El sistema implementa seguridad basada en roles (RBAC) mediante Spring Security:

    CLIENTE: Puede buscar herramientas, realizar reservas y gestionar su historial de pagos.

    PROVEEDOR: Puede gestionar su propio inventario (CRUD de herramientas) y procesar devoluciones.

    ADMIN: Acceso total a reportes financieros y gestión de categorías maestras.

Endpoints Principales (API)
Autenticación

    POST /api/auth/login

        Request: {"email": "admin@rentatools.com", "password": "123"}

        Response: {"token": "eyJhbG...", "role": "ADMIN"}

Herramientas

    GET /api/herramientas - Lista el catálogo completo.

    POST /api/herramientas - (Solo Proveedor) Registra nuevo equipo.

Reservas

    POST /api/reservas - Crea una solicitud de alquiler.
{
  "herramientaId": 5,
  "fechaInicio": "2026-02-15",
  "fechaFin": "2026-02-20"
    }

Pruebas

Para ejecutar las pruebas unitarias y de integración desarrolladas durante el sprint:
mvn test

AUTOR
Duban Marquez 
