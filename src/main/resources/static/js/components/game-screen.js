async function getInstrument(gameModel) {
    const res = await fetch(`/api/game/instrument/get/${gameModel.instrument}`);
    return res.json();
}

async function getSequence(gameModel) {
    const res = await fetch(`/api/game/${gameModel.lobbyCode}/sequence/get`);
    return res.json();
}

async function logSequence(gameModel) {
    const res = await fetch(`/api/game/${gameModel.lobbyCode}/sequence/get`);
    const sequence = await res.json();
    console.log(sequence);
}

async function saveSequence(pr) {
    const res = await fetch(`/api/game/${gameModel.lobbyCode}/sequence/addtrack`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "X-CSRF-TOKEN": config.csrf.value,
        },
        body: JSON.stringify(pr.getEditableTrack()),
    });
    return await res.json();
}

async function setupPianoRoll(gameModel, selectors) {
    const sequence = await getSequence(gameModel);
    const instrumentData = await getInstrument(gameModel);
    let pianoRoll = new PianoRoll({ instrument: instrumentData.program });
    pianoRoll.createVisualElement(selectors.pianoRollContainer, instrumentData.notes);
    pianoRoll.setFixedTracks(sequence.tracks);
    pianoRoll.bindControls(selectors);
    document.querySelector(selectors.sendButton).addEventListener("click", async (e) => {
        let res = await saveSequence(pianoRoll);
        window.location.reload();
    });
}

async function showInstructionsModal(gameModel, selectors) {
    const instrumentData = await getInstrument(gameModel);
    document.querySelector(selectors.instructionsModalLabel).textContent = `Ronda ${gameModel.currentRound + 1} de ${gameModel.totalRounds}`;
    document.querySelector(selectors.instructionsModalBody).textContent = `Crea una pista de ${instrumentData.instrumentName} para la canción!`;
    const bsModal = new bootstrap.Modal(document.querySelector(selectors.instructionsModal));
    bsModal.show();
}

const selectors = {
    playButton: "#play",
    stopButton: "#stop",
    progressBar: "#progress",
    loopButton: "#loop",
    pauseButton: "#pause",
    clearButton: "#clear",
    pianoRollContainer: "#piano-roll",
    instructionsModal: "#instructions-modal",
    instructionsModalLabel: "#instructions-modal-label",
    instructionsModalBody: "#instructions-modal-body",
    sendButton: "#tmpSaveButton",
};

setupPianoRoll(gameModel, selectors);
showInstructionsModal(gameModel, selectors);
