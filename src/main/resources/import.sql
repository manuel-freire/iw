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
INSERT INTO Restaurante (id, nombre, direccion, horario, descripcion, valoracion, user_id)
VALUES (1,'Vips', 'Calle Falsa, 123', '24/7', 'Lorem ipsum', 4.0, 4);
INSERT INTO Restaurante (id, nombre, direccion, horario, descripcion, valoracion, user_id)
VALUES (2,'Fosters Hollywood', 'Calle Falsa, 123', '24/7', 'Lorem ipsum', 4.0, 4);
INSERT INTO Restaurante (id, nombre, direccion, horario, descripcion, valoracion, user_id)
VALUES (3,'Ginos Ristorante', 'Calle Falsa, 123', '24/7', 'Lorem ipsum', 4.0, 4);
INSERT INTO Restaurante (id, nombre, direccion, horario, descripcion, valoracion, user_id)
VALUES (4,'McDonalds', 'Calle Falsa, 123', '24/7', 'Lorem ipsum', 4.0, 4);
INSERT INTO Restaurante (id, nombre, direccion, horario, descripcion, valoracion, user_id)
VALUES (5,'Burger King', 'Calle Falsa, 123', '24/7', 'Lorem ipsum', 4.0, 4);
INSERT INTO Restaurante (id, nombre, direccion, horario, descripcion, valoracion, user_id)
VALUES (6,'Dominos Pizza', 'Calle Falsa, 123', '24/7', 'Lorem ipsum', 4.0, 4);
INSERT INTO Restaurante (id, nombre, direccion, horario, descripcion, valoracion, user_id)
VALUES (7,'Telepizza', 'Calle Falsa, 123', '24/7', 'Lorem ipsum', 4.0, 4);
INSERT INTO Restaurante (id, nombre, direccion, horario, descripcion, valoracion, user_id)
VALUES (8,'Rodilla', 'Calle Falsa, 123', '24/7', 'Lorem ipsum', 4.0, 4);
INSERT INTO Restaurante (id, nombre, direccion, horario, descripcion, valoracion, user_id)
VALUES (9,'Tagliatella', 'Calle Falsa, 123', '24/7', 'Lorem ipsum', 4.0, 4);
INSERT INTO Restaurante (id, nombre, direccion, horario, descripcion, valoracion, user_id)
VALUES (10,'Goiko', 'Calle Falsa, 123', '24/7', 'Lorem ipsum', 4.0, 4);
INSERT INTO Restaurante (id, nombre, direccion, horario, descripcion, valoracion, user_id)
VALUES (11,'The Good Burger', 'Calle Falsa, 123', '24/7', 'Lorem ipsum', 4.0, 4);
INSERT INTO Restaurante (id, nombre, direccion, horario, descripcion, valoracion, user_id)
VALUES (12,'Udon', 'Calle Falsa, 123', '24/7', 'Lorem ipsum', 4.0, 4);
INSERT INTO Restaurante (id, nombre, direccion, horario, descripcion, valoracion, user_id)
VALUES (13,'Dunkin Coffee', 'Calle Falsa, 123', '24/7', 'Lorem ipsum', 4.0, 4);
INSERT INTO Restaurante (id, nombre, direccion, horario, descripcion, valoracion, user_id)
VALUES (14,'Tony Romas', 'Calle Falsa, 123', '24/7', 'Lorem ipsum', 4.0, 4);
INSERT INTO Restaurante (id, nombre, direccion, horario, descripcion, valoracion, user_id)
VALUES (15,'Starbucks', 'Calle Falsa, 123', '24/7', 'Lorem ipsum', 4.0, 4);
INSERT INTO Restaurante (id, nombre, direccion, horario, descripcion, valoracion, user_id)
VALUES (16,'Subway', 'Calle Falsa, 123', '24/7', 'Lorem ipsum', 4.0, 4);
INSERT INTO Restaurante (id, nombre, direccion, horario, descripcion, valoracion, user_id)
VALUES (17,'Pans & company', 'Calle Falsa, 123', '24/7', 'Lorem ipsum', 4.0, 4);
INSERT INTO Restaurante (id, nombre, direccion, horario, descripcion, valoracion, user_id)
VALUES (18,'100 Montaditos', 'Calle Falsa, 123', '24/7', 'Lorem ipsum', 4.0, 4);
INSERT INTO Restaurante (id, nombre, direccion, horario, descripcion, valoracion, user_id)
VALUES (19,'Five Guys', 'Calle Falsa, 123', '24/7', 'Lorem ipsum', 4.0, 4);
-- TABLA PLATOS
INSERT INTO Plato (id, descripcion, nombre, precio, restaurante_id)
VALUES (1, 'Hamburguesa de carne y queso', 'Hamburguesa', 10.99, 1);
-- TABLA EXTRA
INSERT INTO Extra (id, nombre, precio, plato_id)
VALUES (1, 'Bacon', 3.0, 1);
-- TABLA COMENTARIO
INSERT INTO Comentario (id, texto, user_id, plato_id, restaurante_id)
VALUES (1, 'Tiene buen sabor', 2, 1, 1);
-- TABLA PEDIDO
INSERT INTO Pedido (id, dir_entrega, estado, fecha_pedido, precio_entrega, precio_servicio, propina, cliente_id, repartidor_id, restaurante_id, valoracion)
VALUES (1,'Calle Falsisima, 345', 0, CURRENT_TIMESTAMP, 3.54, 6.56, 1.20, 2, 3, 1, 0.0);
-- TABLA IWUSER-PEDIDOS
INSERT INTO IWUSER_PEDIDOS (usr_restaurante_id, pedidos_id, repartidor_id, cliente_id)
VALUES (4, 1, 3, 1);
-- TABLA PLATO-PEDIDO
INSERT INTO PLATO_PEDIDO (id, cantidad, plato_id, plato_pedido_id)
VALUES (1, 2, 1, 1);
--TABLA CATEGORIAS
INSERT INTO LABEL (id,nombre)
VALUES(1, 'Desayuno');
INSERT INTO LABEL (id,nombre)
VALUES(2, 'Pizzas');
INSERT INTO LABEL (id,nombre)
VALUES(3, 'Hamburguesas');
INSERT INTO LABEL (id,nombre)
VALUES(4, 'Pasta');
INSERT INTO LABEL (id,nombre)
VALUES(5, 'Meriendas');
INSERT INTO LABEL (id,nombre)
VALUES(6, 'Burritos');



