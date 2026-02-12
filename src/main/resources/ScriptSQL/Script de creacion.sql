-- Creación de la base de datos
CREATE DATABASE IF NOT EXISTS renta_herramientas;
USE renta_herramientas;

-- 1. Usuarios: Almacena Clientes, Proveedores y Administradores
CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    apellido VARCHAR(50) NOT NULL,
    correo VARCHAR(100) NOT NULL UNIQUE,
    contrasena VARCHAR(255) NOT NULL,
    rol ENUM('CLIENTE', 'PROVEEDOR', 'ADMINISTRADOR') NOT NULL,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_auth (correo) -- Optimiza el proceso de Login
);

-- 2. Herramientas: Relación 1:N con Usuarios (Proveedor)
CREATE TABLE herramientas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    precio_dia DECIMAL(10, 2) NOT NULL,
    stock INT NOT NULL DEFAULT 1,
    disponible BOOLEAN DEFAULT TRUE,
    imagen_url VARCHAR(500),
    proveedor_id BIGINT NOT NULL,
    CONSTRAINT fk_herramienta_proveedor FOREIGN KEY (proveedor_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    INDEX idx_catalogo (disponible, precio_dia) -- Optimiza la búsqueda en el catálogo
);

-- 3. Reservas: Relación N:M entre Usuarios y Herramientas (Tabla de hechos)
CREATE TABLE reservas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    herramienta_id BIGINT NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    estado ENUM('PENDIENTE', 'ACTIVA', 'COMPLETADA', 'CANCELADA') DEFAULT 'PENDIENTE',
    total DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_reserva_cliente FOREIGN KEY (cliente_id) REFERENCES usuarios(id),
    CONSTRAINT fk_reserva_herramienta FOREIGN KEY (herramienta_id) REFERENCES herramientas(id),
    INDEX idx_r_cliente (cliente_id),
    INDEX idx_r_estado (estado)
);

-- 4. Pagos: Relación 1:1 con Reservas
CREATE TABLE pagos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reserva_id BIGINT NOT NULL UNIQUE,
    monto DECIMAL(10, 2) NOT NULL,
    fecha_pago TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    metodo_pago VARCHAR(50) DEFAULT 'Tarjeta de Crédito',
    CONSTRAINT fk_pago_reserva FOREIGN KEY (reserva_id) REFERENCES reservas(id) ON DELETE CASCADE
);