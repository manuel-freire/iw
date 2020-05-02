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
	'Alumno', '00A', 1050, 5, 1, 1
);

INSERT INTO user(id,enabled,username,password,roles,first_name,last_name,elo,correct,passed,perfect) VALUES (
	3, 1, 'ST.00B', 
	'{bcrypt}$2a$10$xLFtBIXGtYvAbRqM95JhcOaG23fHRpDoZIJrsF2cCff9xEHTTdK1u',
	'USER',
	'Alumno', '00B', 1035, 3, 1, 0
);

INSERT INTO user(id,enabled,username,password,roles,first_name,last_name,elo,correct,passed,perfect) VALUES (
	4, 1, 'ST.00C', 
	'{bcrypt}$2a$10$xLFtBIXGtYvAbRqM95JhcOaG23fHRpDoZIJrsF2cCff9xEHTTdK1u',
	'USER',
	'Alumna', '00C', 1030, 2, 1, 0
);

INSERT INTO user(id,enabled,username,password,roles,first_name,last_name,elo,correct,passed,perfect) VALUES (
	5, 1, 'ST.00D', 
	'{bcrypt}$2a$10$xLFtBIXGtYvAbRqM95JhcOaG23fHRpDoZIJrsF2cCff9xEHTTdK1u',
	'USER',
	'Alumna', '00D', 1043, 4, 1, 0
);
 
INSERT INTO user(id,enabled,username,password,roles,first_name,last_name,elo,correct,passed,perfect) VALUES (
	6, 1, 'ST.00E', 
	'{bcrypt}$2a$10$xLFtBIXGtYvAbRqM95JhcOaG23fHRpDoZIJrsF2cCff9xEHTTdK1u',
	'USER',
	'Alumna', '00E', 1013, 1, 0, 0
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

INSERT INTO st_team (id, bronze, correct, elo, gold, silver, team_name) VALUES (1, 0, 9, 1093, 0, 0,'Equipo A');

INSERT INTO st_team (id, bronze, correct, elo, gold, silver, team_name) VALUES (2, 0, 4, 1048, 0, 0,'Equipo B');

INSERT INTO st_team (id, bronze, correct, elo, gold, silver, team_name) VALUES (3, 0, 2, 1030, 0, 0,'Equipo C');

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

INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (1, 1, 1, 5, 2, null);
INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (2, 3, 1, 1, 2, null);
INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (3, 4, 1, 1, 2, null);
INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (4, 5, 0, 1050, 2, null);
INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (5, 8, 0, 0, 2, null);

INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (6, 1, 0, 3, 3, null);
INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (7, 3, 1, 1, 3, null);
INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (8, 4, 0, 0, 3, null);
INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (9, 5, 0, 1035, 3, null);
INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (10, 8, 0, 0, 3, null);

INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (11, 1, 0, 2, 4, null);
INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (12, 3, 1, 1, 4, null);
INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (13, 4, 0, 0, 4, null);
INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (14, 5, 0, 1030, 4, null);
INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (15, 8, 0, 0, 4, null);

INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (16, 1, 0, 4, 5, null);
INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (17, 3, 1, 1, 5, null);
INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (18, 4, 0, 0, 5, null);
INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (19, 5, 0, 1043, 5, null);
INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (20, 8, 0, 0, 5, null);

INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (21, 1, 0, 1, 6, null);
INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (22, 3, 0, 0, 6, null);
INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (23, 4, 0, 0, 6, null);
INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (24, 5, 0, 1013, 6, null);
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

INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (31, 2, 0, 9, null, 1);
INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (32, 6, 0, 1093, null, 1);
INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (33, 7, 0, 0, null, 1);

INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (34, 2, 0, 4, null, 2);
INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (35, 6, 0, 1048, null, 2);
INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (36, 7, 0, 0, null, 2);

INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (37, 2, 0, 2, null, 3);
INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (38, 6, 0, 1030, null, 3);
INSERT INTO achievement (id, goal_id, level, progress, achievement_user, achievement_team) VALUES (39, 7, 0, 0, null, 3);

----------------------------------------------------------------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------------------------------------------------------------

-- Creación de un concurso

INSERT INTO contest (id, enabled, complete, name) VALUES (1, 1, 0, 'Mi primer concurso');

UPDATE contest SET teacher=1 where id=1;
UPDATE contest SET contest_list=1 where id=1;
UPDATE contest SET st_class=1 WHERE id=1;
UPDATE contest SET class_contest=1 WHERE id=1;

INSERT INTO question (id, text) VALUES (1, 'Pregunta 1: la repuesta es 1');

UPDATE question SET questions=1 WHERE id=1;
UPDATE question SET question_list=1 WHERE id=1;


INSERT INTO answer (id, score, text) VALUES (1, 0, 'Sin responder');
UPDATE answer SET answers=1 WHERE id=1;
UPDATE answer SET answer_list=1 WHERE id=1;

INSERT INTO answer (id, score, text) VALUES (2, 1, 'Respuesta 1');
INSERT INTO answer (id, score, text) VALUES (3, 0, 'Respuesta 2');
INSERT INTO answer (id, score, text) VALUES (4, 0.5, 'Respuesta 3');
INSERT INTO answer (id, score, text) VALUES (5, 0, 'Respuesta 4');

UPDATE answer SET answers=1 WHERE id=2;
UPDATE answer SET answer_list=1 WHERE id=2;
UPDATE answer SET answers=1 WHERE id=3;
UPDATE answer SET answer_list=1 WHERE id=3;
UPDATE answer SET answers=1 WHERE id=4;
UPDATE answer SET answer_list=1 WHERE id=4;
UPDATE answer SET answers=1 WHERE id=5;
UPDATE answer SET answer_list=1 WHERE id=5;

INSERT INTO question (id, text) VALUES (2, 'Pregunta 2: la repuesta es 2');

UPDATE question SET questions=1 WHERE id=2;
UPDATE question SET question_list=1 WHERE id=2;

INSERT INTO answer (id, score, text) VALUES (6, 0, 'Sin responder');
UPDATE answer SET answers=2 WHERE id=6;
UPDATE answer SET answer_list=2 WHERE id=6;

INSERT INTO answer (id, score, text) VALUES (7, 0.5, 'Respuesta 1');
INSERT INTO answer (id, score, text) VALUES (8, 1, 'Respuesta 2');
INSERT INTO answer (id, score, text) VALUES (9, 0.5, 'Respuesta 3');
INSERT INTO answer (id, score, text) VALUES (10, 0, 'Respuesta 4');

UPDATE answer SET answers=2 WHERE id=7;
UPDATE answer SET answer_list=2 WHERE id=7;
UPDATE answer SET answers=2 WHERE id=8;
UPDATE answer SET answer_list=2 WHERE id=8;
UPDATE answer SET answers=2 WHERE id=9;
UPDATE answer SET answer_list=2 WHERE id=9;
UPDATE answer SET answers=2 WHERE id=10;
UPDATE answer SET answer_list=2 WHERE id=10;

INSERT INTO question (id, text) VALUES (3, 'Pregunta 3: la repuesta es 3');

UPDATE question SET questions=1 WHERE id=3;
UPDATE question SET question_list=1 WHERE id=3;

INSERT INTO answer (id, score, text) VALUES (11, 0, 'Sin responder');
UPDATE answer SET answers=3 WHERE id=11;
UPDATE answer SET answer_list=3 WHERE id=11;

INSERT INTO answer (id, score, text) VALUES (12, 0.25, 'Respuesta 1');
INSERT INTO answer (id, score, text) VALUES (13, 0.25, 'Respuesta 2');
INSERT INTO answer (id, score, text) VALUES (14, 1, 'Respuesta 3');
INSERT INTO answer (id, score, text) VALUES (15, 0.5, 'Respuesta 4');

UPDATE answer SET answers=3 WHERE id=12;
UPDATE answer SET answer_list=3 WHERE id=12;
UPDATE answer SET answers=3 WHERE id=13;
UPDATE answer SET answer_list=3 WHERE id=13;
UPDATE answer SET answers=3 WHERE id=14;
UPDATE answer SET answer_list=3 WHERE id=14;
UPDATE answer SET answers=3 WHERE id=15;
UPDATE answer SET answer_list=3 WHERE id=15;

INSERT INTO question (id, text) VALUES (4, 'Pregunta 4: la repuesta es 4');

UPDATE question SET questions=1 WHERE id=4;
UPDATE question SET question_list=1 WHERE id=4;

INSERT INTO answer (id, score, text) VALUES (16, 0, 'Sin responder');
UPDATE answer SET answers=4 WHERE id=16;
UPDATE answer SET answer_list=4 WHERE id=16;

INSERT INTO answer (id, score, text) VALUES (17, 0.25, 'Respuesta 1');
INSERT INTO answer (id, score, text) VALUES (18, 0, 'Respuesta 2');
INSERT INTO answer (id, score, text) VALUES (19, 0, 'Respuesta 3');
INSERT INTO answer (id, score, text) VALUES (20, 1, 'Respuesta 4');

UPDATE answer SET answers=4 WHERE id=17;
UPDATE answer SET answer_list=4 WHERE id=17;
UPDATE answer SET answers=4 WHERE id=18;
UPDATE answer SET answer_list=4 WHERE id=18;
UPDATE answer SET answers=4 WHERE id=19;
UPDATE answer SET answer_list=4 WHERE id=19;
UPDATE answer SET answers=4 WHERE id=20;
UPDATE answer SET answer_list=4 WHERE id=20;

INSERT INTO question (id, text) VALUES (5, 'Pregunta 5: la repuesta es Ninguna');

UPDATE question SET questions=1 WHERE id=5;
UPDATE question SET question_list=1 WHERE id=5;

INSERT INTO answer (id, score, text) VALUES (21, 0, 'Sin responder');
UPDATE answer SET answers=5 WHERE id=21;
UPDATE answer SET answer_list=5 WHERE id=21;

INSERT INTO answer (id, score, text) VALUES (22, 0.5, 'Respuesta 1');
INSERT INTO answer (id, score, text) VALUES (23, 0.5, 'Respuesta 2');
INSERT INTO answer (id, score, text) VALUES (24, 0.5, 'Respuesta 3');
INSERT INTO answer (id, score, text) VALUES (25, 1, 'Ninguna');

UPDATE answer SET answers=5 WHERE id=22;
UPDATE answer SET answer_list=5 WHERE id=22;
UPDATE answer SET answers=5 WHERE id=23;
UPDATE answer SET answer_list=5 WHERE id=23;
UPDATE answer SET answers=5 WHERE id=24;
UPDATE answer SET answer_list=5 WHERE id=24;
UPDATE answer SET answers=5 WHERE id=25;
UPDATE answer SET answer_list=5 WHERE id=25;

----------------------------------------------------------------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------------------------------------------------------------

-- Creación de las respuestas de los alumnos

insert into result (id, correct, passed, perfect, score) values (1, 5, true, true, 50.0);

UPDATE result SET results=1 WHERE id=1;
UPDATE result SET contest=1 WHERE id=1;
UPDATE result SET result_list=2 WHERE id=1;
UPDATE result SET user=2 WHERE id=1;

insert into result_answers (result_id, answers_id) values (1, 2);
insert into result_answers (result_id, answers_id) values (1, 8);
insert into result_answers (result_id, answers_id) values (1, 14);
insert into result_answers (result_id, answers_id) values (1, 20);
insert into result_answers (result_id, answers_id) values (1,25);

insert into result (id, correct, passed, perfect, score) values (2, 3, true, false, 35.0);

UPDATE result SET results=1 WHERE id=2;
UPDATE result SET contest=1 WHERE id=2;
UPDATE result SET result_list=3 WHERE id=2;
UPDATE result SET user=3 WHERE id=2;

insert into result_answers (result_id, answers_id) values (2, 3);
insert into result_answers (result_id, answers_id) values (2, 7);
insert into result_answers (result_id, answers_id) values (2, 14);
insert into result_answers (result_id, answers_id) values (2, 20);
insert into result_answers (result_id, answers_id) values (2, 25);

insert into result (id, correct, passed, perfect, score) values (3, 2, true, false, 30.0);

UPDATE result SET results=1 WHERE id=3;
UPDATE result SET contest=1 WHERE id=3;
UPDATE result SET result_list=4 WHERE id=3;
UPDATE result SET user=4 WHERE id=3;

insert into result_answers (result_id, answers_id) values (3, 4);
insert into result_answers (result_id, answers_id) values (3, 9);
insert into result_answers (result_id, answers_id) values (3, 14);
insert into result_answers (result_id, answers_id) values (3, 20);
insert into result_answers (result_id, answers_id) values (3, 21);

insert into result (id, correct, passed, perfect, score) values (4, 4, true, false, 43.0);

UPDATE result SET results=1 WHERE id=4;
UPDATE result SET contest=1 WHERE id=4;
UPDATE result SET result_list=5 WHERE id=4;
UPDATE result SET user=5 WHERE id=4;

insert into result_answers (result_id, answers_id) values (4, 2);
insert into result_answers (result_id, answers_id) values (4, 8);
insert into result_answers (result_id, answers_id) values (4, 14);
insert into result_answers (result_id, answers_id) values (4, 17);
insert into result_answers (result_id, answers_id) values (4,25);

insert into result (id, correct, passed, perfect, score) values (5, 1, false, false, 13.0);

UPDATE result SET results=1 WHERE id=5;
UPDATE result SET contest=1 WHERE id=5;
UPDATE result SET result_list=6 WHERE id=5;
UPDATE result SET user=6 WHERE id=5;

insert into result_answers (result_id, answers_id) values (5, 2);
insert into result_answers (result_id, answers_id) values (5, 6);
insert into result_answers (result_id, answers_id) values (5, 11);
insert into result_answers (result_id, answers_id) values (5, 17);
insert into result_answers (result_id, answers_id) values (5,25);

insert into result (id, correct, passed, perfect, score) values (6, 0, false, false, 0.0);

UPDATE result SET results=1 WHERE id=6;
UPDATE result SET contest=1 WHERE id=6;
UPDATE result SET result_list=7 WHERE id=6;
UPDATE result SET user=7 WHERE id=6;

insert into result_answers (result_id, answers_id) values (6, 1);
insert into result_answers (result_id, answers_id) values (6, 6);
insert into result_answers (result_id, answers_id) values (6, 11);
insert into result_answers (result_id, answers_id) values (6, 16);
insert into result_answers (result_id, answers_id) values (6,21);