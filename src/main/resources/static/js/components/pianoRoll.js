let audioCtx = new AudioContext();
let seq,
    dynamicTrackNumber,
    staticTrackNumber,
    synth,
    loop = false;
let bpm = 140;
let inst = 1;
let beats = 32;

const staticTrack = [
    {
        "pitch": 35,
        "start": 0,
        "instrument": 128
    },
    {
        "pitch": 42,
        "start": 0,
        "instrument": 128
    },
    {
        "pitch": 42,
        "start": 0.125,
        "instrument": 128
    },
    {
        "pitch": 38,
        "start": 0.25,
        "instrument": 128
    },
    {
        "pitch": 42,
        "start": 0.25,
        "instrument": 128
    },
    {
        "pitch": 42,
        "start": 0.375,
        "instrument": 128
    },
    {
        "pitch": 35,
        "start": 0.5,
        "instrument": 128
    },
    {
        "pitch": 42,
        "start": 0.5,
        "instrument": 128
    },
    {
        "pitch": 42,
        "start": 0.625,
        "instrument": 128
    },
    {
        "pitch": 38,
        "start": 0.75,
        "instrument": 128
    },
    {
        "pitch": 42,
        "start": 0.75,
        "instrument": 128
    },
    {
        "pitch": 42,
        "start": 0.875,
        "instrument": 128
    },
    {
        "pitch": 35,
        "start": 1,
        "instrument": 128
    },
    {
        "pitch": 42,
        "start": 1,
        "instrument": 128
    },
    {
        "pitch": 42,
        "start": 1.125,
        "instrument": 128
    },
    {
        "pitch": 38,
        "start": 1.25,
        "instrument": 128
    },
    {
        "pitch": 42,
        "start": 1.25,
        "instrument": 128
    },
    {
        "pitch": 42,
        "start": 1.375,
        "instrument": 128
    },
    {
        "pitch": 35,
        "start": 1.5,
        "instrument": 128
    },
    {
        "pitch": 42,
        "start": 1.5,
        "instrument": 128
    },
    {
        "pitch": 42,
        "start": 1.625,
        "instrument": 128
    },
    {
        "pitch": 38,
        "start": 1.75,
        "instrument": 128
    },
    {
        "pitch": 42,
        "start": 1.75,
        "instrument": 128
    },
    {
        "pitch": 42,
        "start": 1.875,
        "instrument": 128
    },
    {
        "pitch": 35,
        "start": 2,
        "instrument": 128
    },
    {
        "pitch": 42,
        "start": 2,
        "instrument": 128
    },
    {
        "pitch": 42,
        "start": 2.125,
        "instrument": 128
    },
    {
        "pitch": 38,
        "start": 2.25,
        "instrument": 128
    },
    {
        "pitch": 42,
        "start": 2.25,
        "instrument": 128
    },
    {
        "pitch": 42,
        "start": 2.375,
        "instrument": 128
    },
    {
        "pitch": 35,
        "start": 2.5,
        "instrument": 128
    },
    {
        "pitch": 42,
        "start": 2.5,
        "instrument": 128
    },
    {
        "pitch": 42,
        "start": 2.625,
        "instrument": 128
    },
    {
        "pitch": 38,
        "start": 2.75,
        "instrument": 128
    },
    {
        "pitch": 42,
        "start": 2.75,
        "instrument": 128
    },
    {
        "pitch": 42,
        "start": 2.875,
        "instrument": 128
    },
    {
        "pitch": 35,
        "start": 3,
        "instrument": 128
    },
    {
        "pitch": 42,
        "start": 3,
        "instrument": 128
    },
    {
        "pitch": 42,
        "start": 3.125,
        "instrument": 128
    },
    {
        "pitch": 38,
        "start": 3.25,
        "instrument": 128
    },
    {
        "pitch": 42,
        "start": 3.25,
        "instrument": 128
    },
    {
        "pitch": 42,
        "start": 3.375,
        "instrument": 128
    },
    {
        "pitch": 35,
        "start": 3.5,
        "instrument": 128
    },
    {
        "pitch": 42,
        "start": 3.5,
        "instrument": 128
    },
    {
        "pitch": 42,
        "start": 3.625,
        "instrument": 128
    },
    {
        "pitch": 38,
        "start": 3.75,
        "instrument": 128
    },
    {
        "pitch": 42,
        "start": 3.75,
        "instrument": 128
    },
    {
        "pitch": 42,
        "start": 3.875,
        "instrument": 128
    }
]

const notes = [
    // Octave 3
    { pitch: 48, note: "C", octave: 3, isBlack: false },
    { pitch: 49, note: "C#", octave: 3, isBlack: true },
    { pitch: 50, note: "D", octave: 3, isBlack: false },
    { pitch: 51, note: "D#", octave: 3, isBlack: true },
    { pitch: 52, note: "E", octave: 3, isBlack: false },
    { pitch: 53, note: "F", octave: 3, isBlack: false },
    { pitch: 54, note: "F#", octave: 3, isBlack: true },
    { pitch: 55, note: "G", octave: 3, isBlack: false },
    { pitch: 56, note: "G#", octave: 3, isBlack: true },
    { pitch: 57, note: "A", octave: 3, isBlack: false },
    { pitch: 58, note: "A#", octave: 3, isBlack: true },
    { pitch: 59, note: "B", octave: 3, isBlack: false },

    // Octave 4
    { pitch: 60, note: "C", octave: 4, isBlack: false },
    { pitch: 61, note: "C#", octave: 4, isBlack: true },
    { pitch: 62, note: "D", octave: 4, isBlack: false },
    { pitch: 63, note: "D#", octave: 4, isBlack: true },
    { pitch: 64, note: "E", octave: 4, isBlack: false },
    { pitch: 65, note: "F", octave: 4, isBlack: false },
    { pitch: 66, note: "F#", octave: 4, isBlack: true },
    { pitch: 67, note: "G", octave: 4, isBlack: false },
    { pitch: 68, note: "G#", octave: 4, isBlack: true },
    { pitch: 69, note: "A", octave: 4, isBlack: false },
    { pitch: 70, note: "A#", octave: 4, isBlack: true },
    { pitch: 71, note: "B", octave: 4, isBlack: false },

    // Octave 5
    { pitch: 72, note: "C", octave: 5, isBlack: false },
    { pitch: 73, note: "C#", octave: 5, isBlack: true },
    { pitch: 74, note: "D", octave: 5, isBlack: false },
    { pitch: 75, note: "D#", octave: 5, isBlack: true },
    { pitch: 76, note: "E", octave: 5, isBlack: false },
    { pitch: 77, note: "F", octave: 5, isBlack: false },
    { pitch: 78, note: "F#", octave: 5, isBlack: true },
    { pitch: 79, note: "G", octave: 5, isBlack: false },
    { pitch: 80, note: "G#", octave: 5, isBlack: true },
    { pitch: 81, note: "A", octave: 5, isBlack: false },
    { pitch: 82, note: "A#", octave: 5, isBlack: true },
    { pitch: 83, note: "B", octave: 5, isBlack: false },
];

async function buildPianoRoll(notes) {
    notes.forEach((n) => {
        roll = document.createElement("div");
        roll.setAttribute("id", `roll-${n}`);
        roll.classList.add("note-roll");
        for (let i = 0; i < beats; i++) {
            cb = document.createElement("div");
            cb.setAttribute("title", n.note);
            if (i == 0 && n.note === "C")
                cb.insertAdjacentHTML(
                    "beforeend",
                    `<span class="note-text">${n.note + n.octave}</span>`,
                );
            cb.setAttribute("id", `${n.abc}${i}`);
            cb.classList.add("note");
            if (n.isBlack) cb.classList.add("black-key");
            else cb.classList.add("white-key");
            if (i % 8 == 7) cb.classList.add("section-end");
            Object.assign(cb.dataset, n);
            cb.dataset.time = `${i}`;
            roll.append(cb);
        }
        pianoRoll.prepend(roll);
    });
}

async function addNoteListeners() {
    document.querySelectorAll(".note").forEach((key) => {
        key.addEventListener("click", (e) => {
            sequenceChanged = true;
            key.classList.toggle("active");
            if (key.classList.contains("active")) {
                addNote(
                    key.dataset.pitch,
                    (4 * parseInt(key.dataset.time)) / beats,
                    dynamicTrackNumber,
                    seq.currentInstrument[dynamicTrackNumber],
                );
            } else {
                removeNote(
                    key.dataset.pitch,
                    (4 * parseInt(key.dataset.time)) / beats,
                    dynamicTrackNumber,
                    seq.currentInstrument[dynamicTrackNumber],
                );
            }
        });
    });
}

async function createSequence() {
    seq = new ABCJS.synth.SynthSequence();
    dynamicTrackNumber = seq.addTrack();
    seq.setInstrument(dynamicTrackNumber, inst);
    seq.totalDuration = 4;
}

async function createSynth() {
    synth = new ABCJS.synth.CreateSynth();
}

async function addNote(pitch, start, trackNumber, instrument) {
    var note = {
        cmd: "note",
        duration: 4 / beats,
        gap: 0,
        instrument: instrument,
        pitch: parseInt(pitch),
        start: start,
        volume: 80,
    };
    seq.tracks[trackNumber].push(note);
}

async function removeNote(pitch, start, trackNumber, instrument) {
    const idx = seq.tracks[trackNumber].findIndex(
        (note) =>
            note.pitch === parseInt(pitch) &&
            note.start === start,
    );
    if (idx !== -1) seq.tracks[trackNumber].splice(idx, 1);
}

document.querySelector("#tempo").addEventListener("change", (e) => {
    bpm = document.querySelector("#tempo").value;
});

document.querySelector("#inst").addEventListener("change", (e) => {
    inst = document.querySelector("#inst").value;
    console.log(inst)
    seq.tracks[dynamicTrackNumber].forEach((el) => {
        el.instrument = inst;
    });
});

document.querySelector("#play").addEventListener("click", async (e) => {
    await synth.init({
        sequence: seq,
        audioContext: audioCtx,
        millisecondsPerMeasure: (4 * 60000) / bpm,
    });
    await synth.prime();
    await synth.start();
});

function playLoop() {
    if (!loop) return;

    synth.prime().then(() => {
        synth.start();

        // Schedule the next loop after the total duration
        setTimeout(() => {
            playLoop();
        }, synth.duration * 1000); // duration in ms
    });
}

async function addStaticTrack(track) {
    staticTrackNumber = seq.addTrack();
    for (note of track) {
        addNote(note.pitch, note.start, staticTrackNumber, note.instrument);
    }
}

document.querySelector("#loop").addEventListener("click", async () => {
    loop = true;
    await synth.init({
        sequence: seq,
        audioContext: audioCtx,
        millisecondsPerMeasure: (4 * 60000) / bpm,
    });
    playLoop();
});

document.querySelector("#stop").addEventListener("click", () => {
    loop = false;
    synth.stop();
});
document.querySelector("#clear").addEventListener("click", () => {
    if (synth) {
        synth.stop();
    }
    
    // 2. Clear the timeline array
    timeline = Array.from({ length: beats }, () => new Set());
    
    // 3. Remove all notes from the dynamic track in the sequence
    if (seq && seq.tracks[dynamicTrackNumber]) {
        seq.tracks[dynamicTrackNumber] = [];  // Empty the track
    }
    
    // 4. Remove visual active class from all note elements
    document.querySelectorAll(".note.active").forEach(note => {
        note.classList.remove("active");
    });

});

document.addEventListener("DOMContentLoaded", (e) => {
    document.getElementById("tempo").value = bpm;
    buildPianoRoll(notes)
        .then(() => createSequence())
        .then(() => addNoteListeners())
        .then(() => createSynth()).then(()=>getSequence()).then((sequence)=>fillSequence(sequence))
});

async function fillSequence(sequence){
    console.log(sequence)
    for(t of sequence.tracks){
        let trackNo = seq.addTrack()
        for(n of t.notes){
            console.log(n, t.instrument)
            addNote(n.pitch, n.time, trackNo, t.instrument)
        }
    }
}

async function getSequence() {
    const res = await fetch(`/api/game/${document.documentElement.dataset.lobbyCode}/sequence/get`)
    const sequence = await res.json();
    return sequence
}

async function logSequence() {
    const res = await fetch(`/api/game/${document.documentElement.dataset.lobbyCode}/sequence/get`)
    const sequence = await res.json();
    console.log(sequence)
}

async function dummySequence(){
    sequence = await getSequence()
    console.log(sequence);
    track = {
        instrument: 128,
        notes:[]
    }
    for(note of staticTrack){
        track.notes.push({pitch: note.pitch, time: note.time})
    }
    sequence.tracks.push(track)

    const res = await fetch(`/api/game/${document.documentElement.dataset.lobbyCode}/sequence/update`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', "X-CSRF-TOKEN": config.csrf.value },
        body: JSON.stringify(sequence)
    });

    return await res.json()
}

async function saveSequence(){
    sequence = await getSequence()
    newTrack = {
        instrument: inst,
        notes: []
    }
    for(el of seq.tracks[dynamicTrackNumber]){
        if(el.cmd == "note"){
            newTrack.notes.push({pitch: el.pitch, time: el.start})
        }
    }
    sequence.tracks.push(newTrack)
    const res = await fetch(`/api/game/${document.documentElement.dataset.lobbyCode}/sequence/update`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', "X-CSRF-TOKEN": config.csrf.value },
        body: JSON.stringify(sequence)
    });

    return await res.json()
}

document.querySelector("#tmpSaveButton").addEventListener("click", async e=>{
    let res = await saveSequence();
    window.location.reload();
})
