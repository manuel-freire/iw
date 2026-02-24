-- insert admin (username a, password aa)
INSERT INTO IWUser (id, enabled, roles, username, password)
VALUES (1, TRUE, 'ADMIN,USER', 'a',
    '{bcrypt}$2a$10$2BpNTbrsarbHjNsUWgzfNubJqBRf.0Vz9924nRSHBqlbPKerkgX.W');
INSERT INTO IWUser (id, enabled, roles, username, password)
VALUES (2, TRUE, 'USER', 'b',
    '{bcrypt}$2a$10$2BpNTbrsarbHjNsUWgzfNubJqBRf.0Vz9924nRSHBqlbPKerkgX.W');

-- start id numbering from a value that is larger than any assigned above
ALTER SEQUENCE "PUBLIC"."GEN" RESTART WITH 1024;

--------------------------------------------------
-- SONGS
--------------------------------------------------

INSERT INTO SONG (id, title, artist, genre, release_year, difficulty)
VALUES (1, 'Get Lucky', 'Daft Punk', 'Pop', 2013, 1);

INSERT INTO SONG (id, title, artist, genre, release_year, difficulty)
VALUES (2, 'Levitating', 'Dua Lipa', 'Pop', 2020, 1);

INSERT INTO SONG (id, title, artist, genre, release_year, difficulty)
VALUES (3, 'Billie Jean', 'Michael Jackson', 'Pop', 1982, 2);

INSERT INTO SONG (id, title, artist, genre, release_year, difficulty)
VALUES (4, 'Blinding Lights', 'The Weeknd', 'R&B/Soul', 2020, 1);

INSERT INTO SONG (id, title, artist, genre, release_year, difficulty)
VALUES (5, 'Take On Me', 'a-ha', 'Synth Pop', 1985, 2);


--------------------------------------------------
-- LAYERS FOR SONG 1 (Get Lucky)
--------------------------------------------------

INSERT INTO SONG_LAYER (id, song_id, idx, label, audio_url)
VALUES (101, 1, 0, 'DRUMS', '/music/song1/01_drums.mp3');

INSERT INTO SONG_LAYER (id, song_id, idx, label, audio_url)
VALUES (102, 1, 1, 'BASS', '/music/song1/02_drums_bass.mp3');

INSERT INTO SONG_LAYER (id, song_id, idx, label, audio_url)
VALUES (103, 1, 2, 'MELODY', '/music/song1/03_drums_bass_melody.mp3');

INSERT INTO SONG_LAYER (id, song_id, idx, label, audio_url)
VALUES (104, 1, 3, 'FULL', '/music/song1/04_full.mp3');


--------------------------------------------------
-- LAYERS FOR SONG 2 (Levitating)
--------------------------------------------------

INSERT INTO SONG_LAYER (id, song_id, idx, label, audio_url)
VALUES (201, 2, 0, 'DRUMS', '/music/song2/01_drums.mp3');

INSERT INTO SONG_LAYER (id, song_id, idx, label, audio_url)
VALUES (202, 2, 1, 'BASS', '/music/song2/02_drums_bass.mp3');

INSERT INTO SONG_LAYER (id, song_id, idx, label, audio_url)
VALUES (203, 2, 2, 'MELODY', '/music/song2/03_drums_bass_melody.mp3');

INSERT INTO SONG_LAYER (id, song_id, idx, label, audio_url)
VALUES (204, 2, 3, 'FULL', '/music/song2/04_full.mp3');


--------------------------------------------------
-- LAYERS FOR SONG 3 (Billie Jean)
--------------------------------------------------

INSERT INTO SONG_LAYER (id, song_id, idx, label, audio_url)
VALUES (301, 3, 0, 'DRUMS', '/music/song3/01_drums.mp3');

INSERT INTO SONG_LAYER (id, song_id, idx, label, audio_url)
VALUES (302, 3, 1, 'BASS', '/music/song3/02_drums_bass.mp3');

INSERT INTO SONG_LAYER (id, song_id, idx, label, audio_url)
VALUES (303, 3, 2, 'MELODY', '/music/song3/03_drums_bass_melody.mp3');

INSERT INTO SONG_LAYER (id, song_id, idx, label, audio_url)
VALUES (304, 3, 3, 'FULL', '/music/song3/04_full.mp3');


--------------------------------------------------
-- LAYERS FOR SONG 4 (Blinding Lights)
--------------------------------------------------

INSERT INTO SONG_LAYER (id, song_id, idx, label, audio_url)
VALUES (401, 4, 0, 'DRUMS', '/music/song4/01_drums.mp3');

INSERT INTO SONG_LAYER (id, song_id, idx, label, audio_url)
VALUES (402, 4, 1, 'BASS', '/music/song4/02_drums_bass.mp3');

INSERT INTO SONG_LAYER (id, song_id, idx, label, audio_url)
VALUES (403, 4, 2, 'MELODY', '/music/song4/03_drums_bass_melody.mp3');

INSERT INTO SONG_LAYER (id, song_id, idx, label, audio_url)
VALUES (404, 4, 3, 'FULL', '/music/song4/04_full.mp3');


--------------------------------------------------
-- LAYERS FOR SONG 5 (Take On Me)
--------------------------------------------------

INSERT INTO SONG_LAYER (id, song_id, idx, label, audio_url)
VALUES (501, 5, 0, 'DRUMS', '/music/song5/01_drums.mp3');

INSERT INTO SONG_LAYER (id, song_id, idx, label, audio_url)
VALUES (502, 5, 1, 'BASS', '/music/song5/02_drums_bass.mp3');

INSERT INTO SONG_LAYER (id, song_id, idx, label, audio_url)
VALUES (503, 5, 2, 'MELODY', '/music/song5/03_drums_bass_melody.mp3');

INSERT INTO SONG_LAYER (id, song_id, idx, label, audio_url)
VALUES (504, 5, 3, 'FULL', '/music/song5/04_full.mp3');

--------------------------------------------------
-- AUTHORS
--------------------------------------------------

INSERT INTO AUTHORS (id, first_name, last_name, git_hub_url, image_file_name)
VALUES (1001, 'Timofey', 'Matveev', 'https://github.com/TimotyEnder', 'TimofeyMatveev.jpeg');

INSERT INTO AUTHORS (id, first_name, last_name, git_hub_url, image_file_name)
VALUES (1002, 'Rishi', 'Pursnani Mirpuri', 'https://github.com/VaC306', 'RishiPursnaniMirpuri.jpg');

INSERT INTO AUTHORS (id, first_name, last_name, git_hub_url, image_file_name)
VALUES (1003, 'Victor', 'Sandu Stavita', 'https://github.com/VictorSS7', 'VictorSanduStavita.jpg');

INSERT INTO AUTHORS (id, first_name, last_name, git_hub_url, image_file_name)
VALUES (1004, 'Amaury Antonio', 'Valle Lopez', 'https://github.com/amauryav-ucm', 'AmauryValleLopez.jpg');