"use strict";

const selectors = {
  gameContainer: "#game-container",
  startButton: "#start-button",
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
  playerList: "#player-list",
  waitingRoomTemplate: "#waiting-room",
  gameScreenTemplate: "#game-screen",
};

let gameData, pianoRoll;

function subscribeWhenReady(topic) {
  const interval = setInterval(() => {
    if (ws.stompClient && ws.stompClient.connected) {
      try {
        ws.stompClient.subscribe(topic, (m) => handleMessage(JSON.parse(m.body)));
        console.log("Hopefully subscribed to " + topic);
      } catch (e) {
        console.log("Error, could not subscribe to " + topic, e);
      }
      clearInterval(interval);
    }
  }, 100);
}

function handleMessage(m) {
  console.log(m.data);
  switch (m.type) {
    case "PLAYERSUPDATED":
      updatePlayers(m.data);
      break;
    case "GAMESTARTED":
      showScreen(selectors.gameScreenTemplate);
      gameData = m.data;
      setupGameScreen();
      break;
  }
}

function updatePlayers(list) {
  console.log("Updating players...");
  document.querySelector(selectors.playerList).innerHTML = "";
  list.forEach((element) => {
    document.querySelector(selectors.playerList).insertAdjacentHTML("beforeend", `<li>${element}</li>`);
  });
}

function showScreen(selector) {
  console.log(`Showing screen "${selector}"`)
  const gameContainer = document.querySelector(selectors.gameContainer);
  const template = document.querySelector(`${selector}`);
  const instance = template.content.cloneNode(true);
  gameContainer.replaceChildren();
  gameContainer.appendChild(instance);
}

function sendStartRequest() {
  console.log("Sending start request...");
  ws.stompClient.send(`/gartic/lobby/${lobbyCode}/start`, {}, JSON.stringify({ userId: config.userId }));
}

async function getInstrument() {
  const res = await fetch(`/api/game/instrument/get/${gameData.instrument}`);
  return res.json();
}

async function getSequence() {
  const res = await fetch(`/api/game/${lobbyCode}/sequence/get`);
  return res.json();
}

async function logSequence() {
  const res = await fetch(`/api/game/${lobbyCode}/sequence/get`);
  const sequence = await res.json();
  console.log(sequence);
}

async function saveSequence(pr) {
  const res = await fetch(`/api/game/${lobbyCode}/sequence/addtrack`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "X-CSRF-TOKEN": config.csrf.value,
    },
    body: JSON.stringify(pr.getEditableTrack()),
  });
  return await res.json();
}

async function setupPianoRoll(selectors) {
  const sequence = await getSequence();
  const instrumentData = await getInstrument();
  pianoRoll = new PianoRoll({ instrument: instrumentData.program });
  pianoRoll.createVisualElement(selectors.pianoRollContainer, instrumentData.notes);
  pianoRoll.setFixedTracks(sequence.tracks);
  pianoRoll.bindControls(selectors);
  document.querySelector(selectors.sendButton).addEventListener("click", async (e) => {
    let res = await saveSequence(pianoRoll);
  });
}

async function showInstructionsModal(selectors) {
  const instrumentData = await getInstrument();
  document.querySelector(selectors.instructionsModalLabel).textContent =
    `Ronda ${gameData.currentRound + 1} de ${gameData.totalRounds}`;
  document.querySelector(selectors.instructionsModalBody).textContent =
    `Crea una pista de ${instrumentData.instrumentName} para la canción!`;
  const bsModal = new bootstrap.Modal(document.querySelector(selectors.instructionsModal));
  bsModal.show();
}

function setupWaitingRoom() {
  document.querySelector(selectors.startButton).onclick = sendStartRequest;
}

function setupGameScreen() {
  setupPianoRoll(selectors);
  showInstructionsModal(selectors);
}

document.addEventListener("DOMContentLoaded", (e) => {
  subscribeWhenReady("/topic/gartic/lobby/" + lobbyCode);
  showScreen(selectors.waitingRoomTemplate);
  setupWaitingRoom();
});
