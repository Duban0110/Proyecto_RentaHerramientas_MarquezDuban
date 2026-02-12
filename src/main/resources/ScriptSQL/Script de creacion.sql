-- 1. Creación de la base de datos
CREATE DATABASE IF NOT EXISTS sistema_herramientas;
USE sistema_herramientas;

-- 2. Tabla de Usuarios
CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    correo VARCHAR(150) NOT NULL UNIQUE,
    contrasena VARCHAR(255) NOT NULL,
    rol ENUM('ADMINISTRADOR', 'PROVEEDOR', 'CLIENTE') NOT NULL
) ENGINE=InnoDB;

-- 3. Tabla de Herramientas
CREATE TABLE herramientas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    descripcion TEXT,
    precio_dia DECIMAL(10, 2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    disponible BOOLEAN DEFAULT TRUE,
    proveedor_id BIGINT,
    CONSTRAINT fk_herramienta_proveedor 
        FOREIGN KEY (proveedor_id) REFERENCES usuarios(id) 
        ON DELETE CASCADE
) ENGINE=InnoDB;

-- 4. Tabla de Reservas
CREATE TABLE reservas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    total DECIMAL(10, 2) NOT NULL,
    estado ENUM('ACTIVA', 'COMPLETADA', 'CANCELADA') DEFAULT 'ACTIVA',
    cargos_adicionales DECIMAL(10, 2) DEFAULT 0.00,
    observaciones_devolucion TEXT,
    cliente_id BIGINT,
    herramienta_id BIGINT,
    CONSTRAINT fk_reserva_cliente 
        FOREIGN KEY (cliente_id) REFERENCES usuarios(id) 
        ON DELETE CASCADE,
    CONSTRAINT fk_reserva_herramienta 
        FOREIGN KEY (herramienta_id) REFERENCES herramientas(id) 
        ON DELETE CASCADE
) ENGINE=InnoDB;

-- 5. Tabla de Pagos
CREATE TABLE pagos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha_pago DATETIME DEFAULT CURRENT_TIMESTAMP,
    metodo_pago VARCHAR(50) NOT NULL, -- Ej: 'TARJETA', 'TRANSFERENCIA'
    monto DECIMAL(10, 2) NOT NULL,
    numero_factura VARCHAR(100) UNIQUE,
    reserva_id BIGINT,
    CONSTRAINT fk_pago_reserva 
        FOREIGN KEY (reserva_id) REFERENCES reservas(id) 
        ON DELETE CASCADE
) ENGINE=InnoDB;
