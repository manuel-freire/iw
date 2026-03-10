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

--------------------------------------------------
-- MIDI INSTRUMENTS
--------------------------------------------------
INSERT INTO MIDIINSTRUMENT (program, instrument_name, notes)
VALUES (128, 'Batería', '[{"pitch":35,"label":"acousticbassdrum","isBlack":false,"showLabel":true},{"pitch":36,"label":"bassdrum1","isBlack":false,"showLabel":true},{"pitch":38,"label":"acousticsnare","isBlack":false,"showLabel":true},{"pitch":40,"label":"electricsnare","isBlack":false,"showLabel":true},{"pitch":42,"label":"closedhi-hat","isBlack":false,"showLabel":true},{"pitch":44,"label":"pedalhi-hat","isBlack":false,"showLabel":true},{"pitch":46,"label":"openhi-hat","isBlack":false,"showLabel":true},{"pitch":41,"label":"lowfloortom","isBlack":false,"showLabel":true},{"pitch":43,"label":"highfloortom","isBlack":false,"showLabel":true},{"pitch":45,"label":"lowtom","isBlack":false,"showLabel":true},{"pitch":48,"label":"himidtom","isBlack":false,"showLabel":true},{"pitch":50,"label":"hightom","isBlack":false,"showLabel":true},{"pitch":49,"label":"crashcymbal1","isBlack":false,"showLabel":true},{"pitch":51,"label":"ridecymbal1","isBlack":false,"showLabel":true},{"pitch":53,"label":"ridebell","isBlack":false,"showLabel":true},{"pitch":55,"label":"splashcymbal","isBlack":false,"showLabel":true}]');

INSERT INTO MIDIINSTRUMENT (program, instrument_name, notes)
VALUES (34, 'Bajo', '[{"pitch":24,"label":"C1","isBlack":false,"showLabel":true},{"pitch":25,"label":"C#1","isBlack":true,"showLabel":false},{"pitch":26,"label":"D1","isBlack":false,"showLabel":false},{"pitch":27,"label":"D#1","isBlack":true,"showLabel":false},{"pitch":28,"label":"E1","isBlack":false,"showLabel":false},{"pitch":29,"label":"F1","isBlack":false,"showLabel":false},{"pitch":30,"label":"F#1","isBlack":true,"showLabel":false},{"pitch":31,"label":"G1","isBlack":false,"showLabel":false},{"pitch":32,"label":"G#1","isBlack":true,"showLabel":false},{"pitch":33,"label":"A1","isBlack":false,"showLabel":false},{"pitch":34,"label":"A#1","isBlack":true,"showLabel":false},{"pitch":35,"label":"B1","isBlack":false,"showLabel":false},{"pitch":36,"label":"C2","isBlack":false,"showLabel":true},{"pitch":37,"label":"C#2","isBlack":true,"showLabel":false},{"pitch":38,"label":"D2","isBlack":false,"showLabel":false},{"pitch":39,"label":"D#2","isBlack":true,"showLabel":false},{"pitch":40,"label":"E2","isBlack":false,"showLabel":false},{"pitch":41,"label":"F2","isBlack":false,"showLabel":false},{"pitch":42,"label":"F#2","isBlack":true,"showLabel":false},{"pitch":43,"label":"G2","isBlack":false,"showLabel":false},{"pitch":44,"label":"G#2","isBlack":true,"showLabel":false},{"pitch":45,"label":"A2","isBlack":false,"showLabel":false},{"pitch":46,"label":"A#2","isBlack":true,"showLabel":false},{"pitch":47,"label":"B2","isBlack":false,"showLabel":false},{"pitch":48,"label":"C3","isBlack":false,"showLabel":true},{"pitch":49,"label":"C#3","isBlack":true,"showLabel":false},{"pitch":50,"label":"D3","isBlack":false,"showLabel":false},{"pitch":51,"label":"D#3","isBlack":true,"showLabel":false},{"pitch":52,"label":"E3","isBlack":false,"showLabel":false},{"pitch":53,"label":"F3","isBlack":false,"showLabel":false},{"pitch":54,"label":"F#3","isBlack":true,"showLabel":false},{"pitch":55,"label":"G3","isBlack":false,"showLabel":false},{"pitch":56,"label":"G#3","isBlack":true,"showLabel":false},{"pitch":57,"label":"A3","isBlack":false,"showLabel":false},{"pitch":58,"label":"A#3","isBlack":true,"showLabel":false},{"pitch":59,"label":"B3","isBlack":false,"showLabel":false}]');

INSERT INTO MIDIINSTRUMENT (program, instrument_name, notes)
VALUES (1, 'Piano', '[{"pitch":48,"label":"C3","isBlack":false,"showLabel":true},{"pitch":49,"label":"C#3","isBlack":true,"showLabel":false},{"pitch":50,"label":"D3","isBlack":false,"showLabel":false},{"pitch":51,"label":"D#3","isBlack":true,"showLabel":false},{"pitch":52,"label":"E3","isBlack":false,"showLabel":false},{"pitch":53,"label":"F3","isBlack":false,"showLabel":false},{"pitch":54,"label":"F#3","isBlack":true,"showLabel":false},{"pitch":55,"label":"G3","isBlack":false,"showLabel":false},{"pitch":56,"label":"G#3","isBlack":true,"showLabel":false},{"pitch":57,"label":"A3","isBlack":false,"showLabel":false},{"pitch":58,"label":"A#3","isBlack":true,"showLabel":false},{"pitch":59,"label":"B3","isBlack":false,"showLabel":false},{"pitch":60,"label":"C4","isBlack":false,"showLabel":true},{"pitch":61,"label":"C#4","isBlack":true,"showLabel":false},{"pitch":62,"label":"D4","isBlack":false,"showLabel":false},{"pitch":63,"label":"D#4","isBlack":true,"showLabel":false},{"pitch":64,"label":"E4","isBlack":false,"showLabel":false},{"pitch":65,"label":"F4","isBlack":false,"showLabel":false},{"pitch":66,"label":"F#4","isBlack":true,"showLabel":false},{"pitch":67,"label":"G4","isBlack":false,"showLabel":false},{"pitch":68,"label":"G#4","isBlack":true,"showLabel":false},{"pitch":69,"label":"A4","isBlack":false,"showLabel":false},{"pitch":70,"label":"A#4","isBlack":true,"showLabel":false},{"pitch":71,"label":"B4","isBlack":false,"showLabel":false},{"pitch":72,"label":"C5","isBlack":false,"showLabel":true},{"pitch":73,"label":"C#5","isBlack":true,"showLabel":false},{"pitch":74,"label":"D5","isBlack":false,"showLabel":false},{"pitch":75,"label":"D#5","isBlack":true,"showLabel":false},{"pitch":76,"label":"E5","isBlack":false,"showLabel":false},{"pitch":77,"label":"F5","isBlack":false,"showLabel":false},{"pitch":78,"label":"F#5","isBlack":true,"showLabel":false},{"pitch":79,"label":"G5","isBlack":false,"showLabel":false},{"pitch":80,"label":"G#5","isBlack":true,"showLabel":false},{"pitch":81,"label":"A5","isBlack":false,"showLabel":false},{"pitch":82,"label":"A#5","isBlack":true,"showLabel":false},{"pitch":83,"label":"B5","isBlack":false,"showLabel":false}]');

INSERT INTO MIDIINSTRUMENT (program, instrument_name, notes)
VALUES (56, 'Trompeta', '[{"pitch":48,"label":"C3","isBlack":false,"showLabel":true},{"pitch":49,"label":"C#3","isBlack":true,"showLabel":false},{"pitch":50,"label":"D3","isBlack":false,"showLabel":false},{"pitch":51,"label":"D#3","isBlack":true,"showLabel":false},{"pitch":52,"label":"E3","isBlack":false,"showLabel":false},{"pitch":53,"label":"F3","isBlack":false,"showLabel":false},{"pitch":54,"label":"F#3","isBlack":true,"showLabel":false},{"pitch":55,"label":"G3","isBlack":false,"showLabel":false},{"pitch":56,"label":"G#3","isBlack":true,"showLabel":false},{"pitch":57,"label":"A3","isBlack":false,"showLabel":false},{"pitch":58,"label":"A#3","isBlack":true,"showLabel":false},{"pitch":59,"label":"B3","isBlack":false,"showLabel":false},{"pitch":60,"label":"C4","isBlack":false,"showLabel":true},{"pitch":61,"label":"C#4","isBlack":true,"showLabel":false},{"pitch":62,"label":"D4","isBlack":false,"showLabel":false},{"pitch":63,"label":"D#4","isBlack":true,"showLabel":false},{"pitch":64,"label":"E4","isBlack":false,"showLabel":false},{"pitch":65,"label":"F4","isBlack":false,"showLabel":false},{"pitch":66,"label":"F#4","isBlack":true,"showLabel":false},{"pitch":67,"label":"G4","isBlack":false,"showLabel":false},{"pitch":68,"label":"G#4","isBlack":true,"showLabel":false},{"pitch":69,"label":"A4","isBlack":false,"showLabel":false},{"pitch":70,"label":"A#4","isBlack":true,"showLabel":false},{"pitch":71,"label":"B4","isBlack":false,"showLabel":false},{"pitch":72,"label":"C5","isBlack":false,"showLabel":true},{"pitch":73,"label":"C#5","isBlack":true,"showLabel":false},{"pitch":74,"label":"D5","isBlack":false,"showLabel":false},{"pitch":75,"label":"D#5","isBlack":true,"showLabel":false},{"pitch":76,"label":"E5","isBlack":false,"showLabel":false},{"pitch":77,"label":"F5","isBlack":false,"showLabel":false},{"pitch":78,"label":"F#5","isBlack":true,"showLabel":false},{"pitch":79,"label":"G5","isBlack":false,"showLabel":false},{"pitch":80,"label":"G#5","isBlack":true,"showLabel":false},{"pitch":81,"label":"A5","isBlack":false,"showLabel":false},{"pitch":82,"label":"A#5","isBlack":true,"showLabel":false},{"pitch":83,"label":"B5","isBlack":false,"showLabel":false}]');