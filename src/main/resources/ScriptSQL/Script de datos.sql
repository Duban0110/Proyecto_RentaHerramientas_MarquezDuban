-- USUARIOS (20 registros: 1 Admin, 9 Proveedores, 10 Clientes)
INSERT INTO usuarios (nombre, apellido, correo, contrasena, rol) VALUES
('Admin', 'Sistemas', 'admin@renta.com', 'pass123', 'ADMINISTRADOR'),
('Carlos', 'Pérez', 'carlos@proveedor.com', 'pass123', 'PROVEEDOR'),
('Marta', 'Gómez', 'marta@proveedor.com', 'pass123', 'PROVEEDOR'),
('Roberto', 'Sánchez', 'roberto@prov.com', 'pass123', 'PROVEEDOR'),
('Elena', 'Torres', 'elena@prov.com', 'pass123', 'PROVEEDOR'),
('Luis', 'Ramírez', 'luis@prov.com', 'pass123', 'PROVEEDOR'),
('Sofía', 'Castro', 'sofia@prov.com', 'pass123', 'PROVEEDOR'),
('Miguel', 'Hernández', 'miguel@prov.com', 'pass123', 'PROVEEDOR'),
('Isabel', 'Vargas', 'isabel@prov.com', 'pass123', 'PROVEEDOR'),
('Javier', 'Ríos', 'javier@prov.com', 'pass123', 'PROVEEDOR'),
('Juan', 'Díaz', 'juan@cliente.com', 'pass123', 'CLIENTE'),
('Lucía', 'Fernández', 'lucia@cliente.com', 'pass123', 'CLIENTE'),
('Pedro', 'Morales', 'pedro@cli.com', 'pass123', 'CLIENTE'),
('Ana', 'Ruiz', 'ana@cli.com', 'pass123', 'CLIENTE'),
('Diego', 'Ortega', 'diego@cli.com', 'pass123', 'CLIENTE'),
('Paula', 'Jiménez', 'paula@cli.com', 'pass123', 'CLIENTE'),
('Andrés', 'Mendoza', 'andres@cli.com', 'pass123', 'CLIENTE'),
('Laura', 'Pascual', 'laura@cli.com', 'pass123', 'CLIENTE'),
('Oscar', 'Blanco', 'oscar@cli.com', 'pass123', 'CLIENTE'),
('Clara', 'Soto', 'clara@cli.com', 'pass123', 'CLIENTE');

-- HERRAMIENTAS (20 registros variados)
INSERT INTO herramientas (nombre, descripcion, precio_dia, stock, imagen_url, proveedor_id, disponible) VALUES
('Taladro Percutor Bosch', '800W, profesional', 15.00, 5, 'http://markemstore.com/cdn/shop/collections/herramientas-manuales-253628.png', 2, 1),
('Sierra Circular Dewalt', 'Corte madera 7-1/4', 22.00, 3, 'http://markemstore.com/cdn/shop/collections/herramientas-manuales-253628.png', 2, 1),
('Hidrolavadora Karcher', 'Presión 1800 PSI', 20.00, 4, 'http://markemstore.com/cdn/shop/collections/herramientas-manuales-253628.png', 2, 1),
('Escalera Telescópica', 'Aluminio 5.2m', 12.00, 8, 'http://markemstore.com/cdn/shop/collections/herramientas-manuales-253628.png', 2, 1),
('Martillo Demoledor', 'Uso industrial 15kg', 45.00, 2, 'http://markemstore.com/cdn/shop/collections/herramientas-manuales-253628.png', 2, 1),
('Pulidora Angular', '4-1/2 pulgadas 900W', 10.00, 6, 'http://markemstore.com/cdn/shop/collections/herramientas-manuales-253628.png', 2, 1),
('Motosierra Stihl', 'Gasolina, espada 20p', 35.00, 3, 'http://markemstore.com/cdn/shop/collections/herramientas-manuales-253628.png', 2, 1),
('Compresor de Aire', '50 Litros 2HP', 25.00, 4, 'http://markemstore.com/cdn/shop/collections/herramientas-manuales-253628.png', 2, 1),
('Generador Eléctrico', '3500W a gasolina', 50.00, 2, 'http://markemstore.com/cdn/shop/collections/herramientas-manuales-253628.png', 2, 1),
('Lijadora de Banda', 'Cinta 3x21 pulg', 14.00, 5, 'http://markemstore.com/cdn/shop/collections/herramientas-manuales-253628.png', 2, 1),
('Mezcladora de Cemento', 'Bulto y medio, motor 1HP', 60.00, 2, 'http://markemstore.com/cdn/shop/collections/herramientas-manuales-253628.png', 2, 1),
('Rotomartillo SDS Max', 'Energía impacto 12J', 38.00, 4, 'http://markemstore.com/cdn/shop/collections/herramientas-manuales-253628.png', 2, 1),
('Soldadora Inverter', '200 Amp multi-volt', 30.00, 5, 'http://markemstore.com/cdn/shop/collections/herramientas-manuales-253628.png', 2, 1),
('Podadora de Césped', 'Motor 4 tiempos 150cc', 18.00, 6, 'http://markemstore.com/cdn/shop/collections/herramientas-manuales-253628.png', 2, 1),
('Gato Hidráulico', 'Tipo caimán 3 toneladas', 8.00, 10, 'http://markemstore.com/cdn/shop/collections/herramientas-manuales-253628.png', 2, 1),
('Aspiradora Industrial', 'Seco/Mojado 12 gal', 16.00, 7, 'http://markemstore.com/cdn/shop/collections/herramientas-manuales-253628.png', 2, 1),
('Nivel Láser Verde', 'Alcance 30m autonivel', 25.00, 4, 'https://images.unsplash.com/photo-1572981779307-38b8cabb2407?q=80&w=500', 2, 1),
('Cortadora de Azulejo', 'Manual 60cm reforzada', 9.00, 12, 'https://images.unsplash.com/photo-1572981779307-38b8cabb2407?q=80&w=500', 2, 1),
('Andamio Estándar', 'Cuerpo completo con tijera', 5.00, 30, 'https://images.unsplash.com/photo-1572981779307-38b8cabb2407?q=80&w=500', 2, 1),
('Roto-excavadora Manual', 'Punta diamante 2HP', 40.00, 2, 'https://images.unsplash.com/photo-1572981779307-38b8cabb2407?q=80&w=500', 2, 1);

-- RESERVAS (20 registros: Diferentes clientes y estados)
INSERT INTO reservas (cliente_id, herramienta_id, fecha_inicio, fecha_fin, total, estado) VALUES
(11, 1, '2026-01-10', '2026-01-12', 30.00, 'COMPLETADA'),
(12, 2, '2026-01-15', '2026-01-16', 22.00, 'COMPLETADA'),
(13, 3, '2026-01-20', '2026-01-22', 40.00, 'COMPLETADA'),
(14, 4, '2026-02-01', '2026-02-05', 48.00, 'ACTIVA'),
(15, 5, '2026-02-02', '2026-02-03', 45.00, 'ACTIVA'),
(16, 6, '2026-01-25', '2026-01-26', 10.00, 'CANCELADA'),
(17, 7, '2026-02-10', '2026-02-12', 70.00, 'PENDIENTE'),
(18, 8, '2026-02-11', '2026-02-13', 50.00, 'PENDIENTE'),
(19, 9, '2026-01-05', '2026-01-07', 100.00, 'COMPLETADA'),
(20, 10, '2026-01-08', '2026-01-10', 28.00, 'COMPLETADA'),
(11, 11, '2026-01-12', '2026-01-15', 180.00, 'COMPLETADA'),
(12, 12, '2026-02-14', '2026-02-15', 38.00, 'PENDIENTE'),
(13, 13, '2026-02-14', '2026-02-16', 60.00, 'PENDIENTE'),
(14, 14, '2026-01-28', '2026-01-30', 36.00, 'COMPLETADA'),
(15, 15, '2026-02-01', '2026-02-10', 72.00, 'ACTIVA'),
(16, 16, '2026-01-05', '2026-01-06', 16.00, 'COMPLETADA'),
(17, 17, '2026-01-18', '2026-01-20', 50.00, 'COMPLETADA'),
(18, 18, '2026-02-05', '2026-02-07', 18.00, 'ACTIVA'),
(19, 19, '2026-01-01', '2026-01-10', 45.00, 'COMPLETADA'),
(20, 20, '2026-02-15', '2026-02-17', 80.00, 'PENDIENTE');

-- PAGOS (20 registros asociados a las reservas anteriores)
INSERT INTO pagos (reserva_id, monto, metodo_pago) VALUES
(1, 30.00, 'Efectivo'), (2, 22.00, 'Tarjeta Credito'), (3, 40.00, 'PayPal'),
(4, 48.00, 'Transferencia'), (5, 45.00, 'Tarjeta Debito'), (6, 0.00, 'N/A'),
(7, 70.00, 'Efectivo'), (8, 50.00, 'PayPal'), (9, 100.00, 'Tarjeta Credito'),
(10, 28.00, 'Transferencia'), (11, 180.00, 'Efectivo'), (12, 38.00, 'Tarjeta Debito'),
(13, 60.00, 'PayPal'), (14, 36.00, 'Visa'), (15, 72.00, 'MasterCard'),
(16, 16.00, 'Efectivo'), (17, 50.00, 'Transferencia'), (18, 18.00, 'PayPal'),
(19, 45.00, 'Visa'), (20, 80.00, 'Transferencia');
