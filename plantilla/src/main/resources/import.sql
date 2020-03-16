-- 
-- El contenido de este fichero se cargará al arrancar la aplicación, suponiendo que uses
-- 		application-default ó application-externaldb en modo 'create'
--

-- Usuario de ejemplo con username = b y contraseña = aa  
INSERT INTO user(id,enabled,username,password,roles,first_name,last_name,elo) VALUES (
	1, 1, 'a', 
	'{bcrypt}$2a$10$xLFtBIXGtYvAbRqM95JhcOaG23fHRpDoZIJrsF2cCff9xEHTTdK1u',
	'USER,ADMIN',
	'Abundio', 'Ejémplez', 1000
);

-- Otro usuario de ejemplo con username = b y contraseña = aa  
INSERT INTO user(id,enabled,username,password,roles,first_name,last_name,elo) VALUES (
	2, 1, 'b', 
	'{bcrypt}$2a$10$xLFtBIXGtYvAbRqM95JhcOaG23fHRpDoZIJrsF2cCff9xEHTTdK1u',
	'USER',
	'Berta', 'Muéstrez', 1000
);

-- Otro usuario de ejemplo con username = b y contraseña = aa  
INSERT INTO user(id,enabled,username,password,roles,first_name,last_name,elo) VALUES (
	3, 1, 'pepe', 
	'{bcrypt}$2a$10$xLFtBIXGtYvAbRqM95JhcOaG23fHRpDoZIJrsF2cCff9xEHTTdK1u',
	'USER',
	'Pepe', 'Papa', 1000
);

INSERT INTO st_class(id,class_name) VALUES (
	1, 'c'
);