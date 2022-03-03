-- TABLA USUARIOS (Contraseña de todos: aa)
INSERT INTO IWUser (disc_rol, id, enabled, roles, username, password)
VALUES ('ADMIN', 1, TRUE, 'ADMIN,USER', 'a',
    '{bcrypt}$2a$10$2BpNTbrsarbHjNsUWgzfNubJqBRf.0Vz9924nRSHBqlbPKerkgX.W');
INSERT INTO IWUser (disc_rol, id, enabled, roles, username, password)
VALUES ('CLIENTE', 2, TRUE, 'USER', 'b',
    '{bcrypt}$2a$10$2BpNTbrsarbHjNsUWgzfNubJqBRf.0Vz9924nRSHBqlbPKerkgX.W');
INSERT INTO IWUser (disc_rol, id, enabled, roles, username, password, valoracion)
VALUES ('REPARTIDOR', 3, TRUE, 'USER', 'repartidor',
    '{bcrypt}$2a$10$2BpNTbrsarbHjNsUWgzfNubJqBRf.0Vz9924nRSHBqlbPKerkgX.W', 0.0);
INSERT INTO IWUser (disc_rol, id, enabled, roles, username, password)
VALUES ('RESTAURANTE', 4, TRUE, 'USER', 'dueñoRestaurante',
    '{bcrypt}$2a$10$2BpNTbrsarbHjNsUWgzfNubJqBRf.0Vz9924nRSHBqlbPKerkgX.W');
-- TABLA RESTAURANTE
INSERT INTO Restaurante (id, descripcion, direccion, horario, nombre, valoracion, user_id)
VALUES (1,'Restaurante de pruebas', 'Calle Falsa, 123', '24/7', 'Lorem ipsum', 4.0, 4);
-- TABLA PLATOS
INSERT INTO Plato (id, descripcion, nombre, precio)
VALUES (1, 'Hamburguesa de carne y queso', 'Hamburguesa', 10.99);
-- TABLA EXTRA
INSERT INTO Extra (id, nombre, precio, plato_id)
VALUES (1, 'Bacon', 3.0, 1);
-- TABLA COMENTARIO
INSERT INTO Comentario (id, texto, user_id, plato_id)
VALUES (1, 'Tiene buen sabor', 2, 1);
-- TABLA PEDIDO
INSERT INTO Pedido (id, dir_entrega, estado, fecha_pedido, precio_entrega, precio_servicio, propina, cliente_id, repartidor_id, restaurante_id, valoracion)
VALUES (1,'Calle Falsisima, 345', 0, CURRENT_TIMESTAMP, 3.54, 6.56, 1.20, 2, 3, 1, 0.0);
-- TABLA IWUSER-PEDIDOS
INSERT INTO IWUSER_PEDIDOS (usr_restaurante_id, pedidos_id, repartidor_id, cliente_id)
VALUES (4, 1, 3, 1);
-- TABLA PLATO-PEDIDO
INSERT INTO PLATO_PEDIDO (id, cantidad, plato_id)
VALUES (1, 2, 1);
