
El proyecto utiliza una base de datos MySQL llamada `renta_herramientas`. Las tablas principales son:

1.  **usuarios:** Centraliza credenciales y roles.
2.  **herramientas:** Almacena el catálogo vinculado a cada proveedor.
3.  **reservas:** Gestiona el flujo de alquiler (fechas, estados y montos).
4.  **pagos:** Registro de transacciones vinculadas a las reservas.



---

## ⚙️ Configuración e Instalación

### 1. Requisitos
* JDK 17 o superior.
* Maven 3.6+.
* Instancia de MySQL corriendo.

### 2. Configuración de la Base de Datos
Crea la base de datos y las tablas utilizando el script SQL incluido en la carpeta `/scripts` (o los proporcionados en la documentación del proyecto). Asegúrate de configurar tus credenciales en el archivo `src/main/resources/application.properties`:

properties
spring.datasource.url=jdbc:mysql://localhost:3306/renta_herramientas
spring.datasource.username=TU_USUARIO
spring.datasource.password=TU_CONTRASEÑA
spring.jpa.hibernate.ddl-auto=update

### 3. Ejecución
Clona el repositorio y ejecuta:

Bash
mvn spring-boot:run
El servidor iniciará por defecto en: http://localhost:8081

📑 Endpoints Principales (API)MétodoEndpointDescripciónPOST/api/auth/loginLogin y obtención de Token JWT.GET/api/herramientasListado del catálogo (Público/Cliente).POST/api/reservasCreación de nueva reserva (Cliente).GET/api/admin/reportes/resumenEstadísticas globales (Solo Admin).POST/api/reservas/pagar/descargarGeneración de factura PDF.

👨‍💻 Autor
Duban - Duban0110
