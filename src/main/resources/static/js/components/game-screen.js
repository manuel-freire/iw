const modal = document.querySelector("#exampleModal");
const bsModal = new bootstrap.Modal(document.querySelector("#exampleModal"));
const rounds = [
    {
        instrumentName: "Batería",
        instrumentMIDI: 128,
        notes: [
            { pitch: 35, label: "acoustic bass drum", isBlack: false, showLabel: true },
            { pitch: 36, label: "bass drum 1", isBlack: false, showLabel: true },
            { pitch: 38, label: "acoustic snare", isBlack: false, showLabel: true },
            { pitch: 40, label: "electric snare", isBlack: false, showLabel: true },
            { pitch: 42, label: "closed hi-hat", isBlack: false, showLabel: true },
            { pitch: 44, label: "pedal hi-hat", isBlack: false, showLabel: true },
            { pitch: 46, label: "open hi-hat", isBlack: false, showLabel: true },
            { pitch: 41, label: "low floor tom", isBlack: false, showLabel: true },
            { pitch: 43, label: "high floor tom", isBlack: false, showLabel: true },
            { pitch: 45, label: "low tom", isBlack: false, showLabel: true },
            { pitch: 48, label: "hi mid tom", isBlack: false, showLabel: true },
            { pitch: 50, label: "high tom", isBlack: false, showLabel: true },
            { pitch: 49, label: "crash cymbal 1", isBlack: false, showLabel: true },
            { pitch: 51, label: "ride cymbal 1", isBlack: false, showLabel: true },
            { pitch: 53, label: "ride bell", isBlack: false, showLabel: true },
            { pitch: 55, label: "splash cymbal", isBlack: false, showLabel: true },
        ],
    },

    {
        instrumentName: "Bajo",
        instrumentMIDI: 34,
        notes: [
            // Octave 1
            { pitch: 24, label: "C1", isBlack: false, showLabel: true },
            { pitch: 25, label: "C#1", isBlack: true, showLabel: false },
            { pitch: 26, label: "D1", isBlack: false, showLabel: false },
            { pitch: 27, label: "D#1", isBlack: true, showLabel: false },
            { pitch: 28, label: "E1", isBlack: false, showLabel: false },
            { pitch: 29, label: "F1", isBlack: false, showLabel: false },
            { pitch: 30, label: "F#1", isBlack: true, showLabel: false },
            { pitch: 31, label: "G1", isBlack: false, showLabel: false },
            { pitch: 32, label: "G#1", isBlack: true, showLabel: false },
            { pitch: 33, label: "A1", isBlack: false, showLabel: false },
            { pitch: 34, label: "A#1", isBlack: true, showLabel: false },
            { pitch: 35, label: "B1", isBlack: false, showLabel: false },

            // Octave 2
            { pitch: 36, label: "C2", isBlack: false, showLabel: true },
            { pitch: 37, label: "C#2", isBlack: true, showLabel: false },
            { pitch: 38, label: "D2", isBlack: false, showLabel: false },
            { pitch: 39, label: "D#2", isBlack: true, showLabel: false },
            { pitch: 40, label: "E2", isBlack: false, showLabel: false },
            { pitch: 41, label: "F2", isBlack: false, showLabel: false },
            { pitch: 42, label: "F#2", isBlack: true, showLabel: false },
            { pitch: 43, label: "G2", isBlack: false, showLabel: false },
            { pitch: 44, label: "G#2", isBlack: true, showLabel: false },
            { pitch: 45, label: "A2", isBlack: false, showLabel: false },
            { pitch: 46, label: "A#2", isBlack: true, showLabel: false },
            { pitch: 47, label: "B2", isBlack: false, showLabel: false },

            // Octave 3
            { pitch: 48, label: "C3", isBlack: false, showLabel: true },
            { pitch: 49, label: "C#3", isBlack: true, showLabel: false },
            { pitch: 50, label: "D3", isBlack: false, showLabel: false },
            { pitch: 51, label: "D#3", isBlack: true, showLabel: false },
            { pitch: 52, label: "E3", isBlack: false, showLabel: false },
            { pitch: 53, label: "F3", isBlack: false, showLabel: false },
            { pitch: 54, label: "F#3", isBlack: true, showLabel: false },
            { pitch: 55, label: "G3", isBlack: false, showLabel: false },
            { pitch: 56, label: "G#3", isBlack: true, showLabel: false },
            { pitch: 57, label: "A3", isBlack: false, showLabel: false },
            { pitch: 58, label: "A#3", isBlack: true, showLabel: false },
            { pitch: 59, label: "B3", isBlack: false, showLabel: false },
        ],
    },
    {
        instrumentName: "Piano",
        instrumentMIDI: 1,
        notes: [
            // Octave 3
            { pitch: 48, label: "C3", isBlack: false, showLabel: true },
            { pitch: 49, label: "C#3", isBlack: true, showLabel: false },
            { pitch: 50, label: "D3", isBlack: false, showLabel: false },
            { pitch: 51, label: "D#3", isBlack: true, showLabel: false },
            { pitch: 52, label: "E3", isBlack: false, showLabel: false },
            { pitch: 53, label: "F3", isBlack: false, showLabel: false },
            { pitch: 54, label: "F#3", isBlack: true, showLabel: false },
            { pitch: 55, label: "G3", isBlack: false, showLabel: false },
            { pitch: 56, label: "G#3", isBlack: true, showLabel: false },
            { pitch: 57, label: "A3", isBlack: false, showLabel: false },
            { pitch: 58, label: "A#3", isBlack: true, showLabel: false },
            { pitch: 59, label: "B3", isBlack: false, showLabel: false },

            // Octave 4
            { pitch: 60, label: "C4", isBlack: false, showLabel: true },
            { pitch: 61, label: "C#4", isBlack: true, showLabel: false },
            { pitch: 62, label: "D4", isBlack: false, showLabel: false },
            { pitch: 63, label: "D#4", isBlack: true, showLabel: false },
            { pitch: 64, label: "E4", isBlack: false, showLabel: false },
            { pitch: 65, label: "F4", isBlack: false, showLabel: false },
            { pitch: 66, label: "F#4", isBlack: true, showLabel: false },
            { pitch: 67, label: "G4", isBlack: false, showLabel: false },
            { pitch: 68, label: "G#4", isBlack: true, showLabel: false },
            { pitch: 69, label: "A4", isBlack: false, showLabel: false },
            { pitch: 70, label: "A#4", isBlack: true, showLabel: false },
            { pitch: 71, label: "B4", isBlack: false, showLabel: false },

            // Octave 5
            { pitch: 72, label: "C5", isBlack: false, showLabel: true },
            { pitch: 73, label: "C#5", isBlack: true, showLabel: false },
            { pitch: 74, label: "D5", isBlack: false, showLabel: false },
            { pitch: 75, label: "D#5", isBlack: true, showLabel: false },
            { pitch: 76, label: "E5", isBlack: false, showLabel: false },
            { pitch: 77, label: "F5", isBlack: false, showLabel: false },
            { pitch: 78, label: "F#5", isBlack: true, showLabel: false },
            { pitch: 79, label: "G5", isBlack: false, showLabel: false },
            { pitch: 80, label: "G#5", isBlack: true, showLabel: false },
            { pitch: 81, label: "A5", isBlack: false, showLabel: false },
            { pitch: 82, label: "A#5", isBlack: true, showLabel: false },
            { pitch: 83, label: "B5", isBlack: false, showLabel: false },
        ],
    },

    {
        instrumentName: "Trompeta",
        instrumentMIDI: 56,
        notes: [
            // Octave 3
            { pitch: 48, label: "C3", isBlack: false, showLabel: true },
            { pitch: 49, label: "C#3", isBlack: true, showLabel: false },
            { pitch: 50, label: "D3", isBlack: false, showLabel: false },
            { pitch: 51, label: "D#3", isBlack: true, showLabel: false },
            { pitch: 52, label: "E3", isBlack: false, showLabel: false },
            { pitch: 53, label: "F3", isBlack: false, showLabel: false },
            { pitch: 54, label: "F#3", isBlack: true, showLabel: false },
            { pitch: 55, label: "G3", isBlack: false, showLabel: false },
            { pitch: 56, label: "G#3", isBlack: true, showLabel: false },
            { pitch: 57, label: "A3", isBlack: false, showLabel: false },
            { pitch: 58, label: "A#3", isBlack: true, showLabel: false },
            { pitch: 59, label: "B3", isBlack: false, showLabel: false },

            // Octave 4
            { pitch: 60, label: "C4", isBlack: false, showLabel: true },
            { pitch: 61, label: "C#4", isBlack: true, showLabel: false },
            { pitch: 62, label: "D4", isBlack: false, showLabel: false },
            { pitch: 63, label: "D#4", isBlack: true, showLabel: false },
            { pitch: 64, label: "E4", isBlack: false, showLabel: false },
            { pitch: 65, label: "F4", isBlack: false, showLabel: false },
            { pitch: 66, label: "F#4", isBlack: true, showLabel: false },
            { pitch: 67, label: "G4", isBlack: false, showLabel: false },
            { pitch: 68, label: "G#4", isBlack: true, showLabel: false },
            { pitch: 69, label: "A4", isBlack: false, showLabel: false },
            { pitch: 70, label: "A#4", isBlack: true, showLabel: false },
            { pitch: 71, label: "B4", isBlack: false, showLabel: false },

            // Octave 5
            { pitch: 72, label: "C5", isBlack: false, showLabel: true },
            { pitch: 73, label: "C#5", isBlack: true, showLabel: false },
            { pitch: 74, label: "D5", isBlack: false, showLabel: false },
            { pitch: 75, label: "D#5", isBlack: true, showLabel: false },
            { pitch: 76, label: "E5", isBlack: false, showLabel: false },
            { pitch: 77, label: "F5", isBlack: false, showLabel: false },
            { pitch: 78, label: "F#5", isBlack: true, showLabel: false },
            { pitch: 79, label: "G5", isBlack: false, showLabel: false },
            { pitch: 80, label: "G#5", isBlack: true, showLabel: false },
            { pitch: 81, label: "A5", isBlack: false, showLabel: false },
            { pitch: 82, label: "A#5", isBlack: true, showLabel: false },
            { pitch: 83, label: "B5", isBlack: false, showLabel: false },
        ],
    },
];
async function getSequence() {
    const res = await fetch(`/api/game/${document.documentElement.dataset.lobbyCode}/sequence/get`);
    return await res.json();
}

async function logSequence() {
    const res = await fetch(`/api/game/${document.documentElement.dataset.lobbyCode}/sequence/get`);
    sequence = await res.json();
    console.log(sequence);
}
async function setupTracks() {
    const sequence = await getSequence();
    pianoRoll.setFixedTracks(sequence.tracks);
}

async function saveSequence() {
    const res = await fetch(`/api/game/${document.documentElement.dataset.lobbyCode}/sequence/addtrack`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "X-CSRF-TOKEN": config.csrf.value,
        },
        body: JSON.stringify(pianoRoll.getEditableTrack()),
    });
    return await res.json();
}
let params = {};
let round = 0;
if (!gameModel.finished) {
    round = rounds[gameModel.currentRound];
    params.instrument = round.instrumentMIDI;
}
let pianoRoll = new PianoRoll(params);

if (!gameModel.finished) {
    pianoRoll.createVisualElement("#pianoRoll", round.notes);
    document.querySelector("#exampleModalLabel").textContent = `Ronda ${gameModel.currentRound + 1} de ${gameModel.totalRounds}`;
    document.querySelector("#exampleModalBody").textContent = `Crea una pista de ${round.instrumentName} para la canción!`;
    bsModal.show();
    document.querySelector("#tmpSaveButton").addEventListener("click", async (e) => {
        let res = await saveSequence();
        window.location.reload();
        console.log(res);
    });
}

pianoRoll.bindControls({ playButton: "#play", stopButton: "#stop", progressBar: "#progress", loopButton: "#loop", pauseButton: "#pause" });
setupTracks();
