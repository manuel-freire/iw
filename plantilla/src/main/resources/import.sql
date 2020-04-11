-- 
-- El contenido de este fichero se cargará al arrancar la aplicación, suponiendo que uses
-- 		application-default ó application-externaldb en modo 'create'
--

--Creación de un profesor y alumnos de prueba
INSERT INTO user(id,enabled,username,password,roles,first_name,last_name,elo,correct,passed,perfect) VALUES (
	1, 1, 'a', 
	'{bcrypt}$2a$10$xLFtBIXGtYvAbRqM95JhcOaG23fHRpDoZIJrsF2cCff9xEHTTdK1u',
	'ADMIN',
	'Profesor', 'Fesor', 0, 0, 0, 0
);

INSERT INTO user(id,enabled,username,password,roles,first_name,last_name,elo,correct,passed,perfect) VALUES (
	2, 1, 'ST.00A', 
	'{bcrypt}$2a$10$xLFtBIXGtYvAbRqM95JhcOaG23fHRpDoZIJrsF2cCff9xEHTTdK1u',
	'USER',
	'Alumno', '00A', 1000, 0, 0, 0
);

INSERT INTO user(id,enabled,username,password,roles,first_name,last_name,elo,correct,passed,perfect) VALUES (
	3, 1, 'ST.00B', 
	'{bcrypt}$2a$10$xLFtBIXGtYvAbRqM95JhcOaG23fHRpDoZIJrsF2cCff9xEHTTdK1u',
	'USER',
	'Alumno', '00B', 1000, 0, 0, 0
);

INSERT INTO user(id,enabled,username,password,roles,first_name,last_name,elo,correct,passed,perfect) VALUES (
	4, 1, 'ST.00C', 
	'{bcrypt}$2a$10$xLFtBIXGtYvAbRqM95JhcOaG23fHRpDoZIJrsF2cCff9xEHTTdK1u',
	'USER',
	'Alumna', '00C', 1000, 0, 0, 0
);

INSERT INTO user(id,enabled,username,password,roles,first_name,last_name,elo,correct,passed,perfect) VALUES (
	5, 1, 'ST.00D', 
	'{bcrypt}$2a$10$xLFtBIXGtYvAbRqM95JhcOaG23fHRpDoZIJrsF2cCff9xEHTTdK1u',
	'USER',
	'Alumna', '00D', 1000, 0, 0, 0
);
 
INSERT INTO user(id,enabled,username,password,roles,first_name,last_name,elo,correct,passed,perfect) VALUES (
	6, 1, 'ST.00E', 
	'{bcrypt}$2a$10$xLFtBIXGtYvAbRqM95JhcOaG23fHRpDoZIJrsF2cCff9xEHTTdK1u',
	'USER',
	'Alumna', '00E', 1000, 0, 0, 0
);
 
INSERT INTO user(id,enabled,username,password,roles,first_name,last_name,elo,correct,passed,perfect) VALUES (
	7, 1, 'ST.00F', 
	'{bcrypt}$2a$10$xLFtBIXGtYvAbRqM95JhcOaG23fHRpDoZIJrsF2cCff9xEHTTdK1u',
	'USER',
	'Alumno', '00F', 1000, 0, 0, 0
);

----------------------------------------------------------------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------------------------------------------------------------

-- Creación de una clase de prueba
INSERT INTO st_class(id,name) VALUES (
	1, 'Clase del Profesor Fesor'
);

-- Asignación del profesor y los alumnos a la clase

UPDATE st_class SET teacher=1 where id=1;
UPDATE st_class SET st_class_list=1 where id=1;

UPDATE user SET st_class=1 WHERE id=2;
UPDATE user SET students=1 WHERE id=2;
UPDATE user SET st_class=1 WHERE id=3;
UPDATE user SET students=1 WHERE id=3;
UPDATE user SET st_class=1 WHERE id=4;
UPDATE user SET students=1 WHERE id=4;
UPDATE user SET st_class=1 WHERE id=5;
UPDATE user SET students=1 WHERE id=5;
UPDATE user SET st_class=1 WHERE id=6;
UPDATE user SET students=1 WHERE id=6;
UPDATE user SET st_class=1 WHERE id=7;
UPDATE user SET students=1 WHERE id=7;

----------------------------------------------------------------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------------------------------------------------------------

-- Creación de los equipos y asignación de los alumnos

INSERT INTO st_team (id, bronze, correct, elo, gold, silver, team_name) VALUES (1, 0, 0, 1000, 0, 0,'Equipo A');

INSERT INTO st_team (id, bronze, correct, elo, gold, silver, team_name) VALUES (2, 0, 0, 1000, 0, 0,'Equipo B');

INSERT INTO st_team (id, bronze, correct, elo, gold, silver, team_name) VALUES (3, 0, 0, 1000, 0, 0,'Equipo C');

UPDATE user SET team=1 WHERE id=2;
UPDATE user SET members=1 WHERE id=2;
UPDATE user SET team=1 WHERE id=5;
UPDATE user SET members=1 WHERE id=5;

UPDATE user SET team=2 WHERE id=3;
UPDATE user SET members=2 WHERE id=3;
UPDATE user SET team=2 WHERE id=6;
UPDATE user SET members=2 WHERE id=6;

UPDATE user SET team=3 WHERE id=4;
UPDATE user SET members=3 WHERE id=4;
UPDATE user SET team=3 WHERE id=7;
UPDATE user SET members=3 WHERE id=7;

UPDATE st_team SET st_class=1 WHERE id=1;
UPDATE st_team SET team_list=1 WHERE id=1;
UPDATE st_team SET st_class=1 WHERE id=2;
UPDATE st_team SET team_list=1 WHERE id=2;
UPDATE st_team SET st_class=1 WHERE id=3;
UPDATE st_team SET team_list=1 WHERE id=3;

----------------------------------------------------------------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------------------------------------------------------------

-- Creación de las metas de los logros

INSERT INTO goal(id,description,levels,target,key) VALUES (
	1, 'XXX preguntas respondidas correctamente', '5,25,50,100,150', 'USER', 'CORRECT'
);

INSERT INTO goal(id,description,levels,target,key) VALUES (
	2, 'Acumulad XXX respuestas correctas', '50,250,500,1000,1500', 'TEAM', 'CORRECT'
);

INSERT INTO goal(id,description,levels,target,key) VALUES (
	3, 'XXX pruebas superadas', '1,5,10,15,25', 'USER', 'PASSED'
);

INSERT INTO goal(id,description,levels,target,key) VALUES (
	4, 'XXX pruebas con puntuación perfecta', '1,3,5,10,15', 'USER', 'PERFECT'
);

INSERT INTO goal(id,description,levels,target,key) VALUES (
	5, 'Alcanza una puntuación de jugador de XXX', '1250,1500,2000,2500,3000', 'USER', 'ELO'
);

INSERT INTO goal(id,description,levels,target,key) VALUES (
	6, 'Acumulad una puntuación de equipo de XXX', '2000,3000,5000,7500,10000', 'TEAM', 'ELO'
);

INSERT INTO goal(id,description,levels,target,key) VALUES (
	7, 'Acumulad XXX trofeos', '1,3,5,10,15', 'TEAM', 'TROPHY'
);

INSERT INTO goal(id,description,levels,target,key) VALUES (
	8, 'Termina XXX pruebas en el TOP 3 de la clasificación', '1,5,10,15,25', 'USER', 'TOP'
);

----------------------------------------------------------------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------------------------------------------------------------

-- Creación de los logros para los jugadores

INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (1, 1, 0, 0, 2, null);
INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (2, 3, 0, 0, 2, null);
INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (3, 4, 0, 0, 2, null);
INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (4, 5, 0, 1000, 2, null);
INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (5, 8, 0, 0, 2, null);

INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (6, 1, 0, 0, 3, null);
INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (7, 3, 0, 0, 3, null);
INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (8, 4, 0, 0, 3, null);
INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (9, 5, 0, 1000, 3, null);
INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (10, 8, 0, 0, 3, null);

INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (11, 1, 0, 0, 4, null);
INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (12, 3, 0, 0, 4, null);
INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (13, 4, 0, 0, 4, null);
INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (14, 5, 0, 1000, 4, null);
INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (15, 8, 0, 0, 4, null);

INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (16, 1, 0, 0, 5, null);
INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (17, 3, 0, 0, 5, null);
INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (18, 4, 0, 0, 5, null);
INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (19, 5, 0, 1000, 5, null);
INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (20, 8, 0, 0, 5, null);

INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (21, 1, 0, 0, 6, null);
INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (22, 3, 0, 0, 6, null);
INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (23, 4, 0, 0, 6, null);
INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (24, 5, 0, 1000, 6, null);
INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (25, 8, 0, 0, 6, null);

INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (26, 1, 0, 0, 7, null);
INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (27, 3, 0, 0, 7, null);
INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (28, 4, 0, 0, 7, null);
INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (29, 5, 0, 1000, 7, null);
INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (30, 8, 0, 0, 7, null);

----------------------------------------------------------------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------------------------------------------------------------

-- Creación de los logros para los equipos

INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (31, 2, 0, 0, null, 1);
INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (32, 6, 0, 1000, null, 1);
INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (33, 7, 0, 0, null, 1);

INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (34, 2, 0, 0, null, 2);
INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (35, 6, 0, 1000, null, 2);
INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (36, 7, 0, 0, null, 2);

INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (37, 2, 0, 0, null, 3);
INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (38, 6, 0, 1000, null, 3);
INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (39, 7, 0, 0, null, 3);