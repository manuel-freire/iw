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
  trackSentTemplate: "#track-sent"
};

let gameData, pianoRoll, roundData;

function subscribeWhenReady(lobbyCode) {
  const interval = setInterval(() => {
    if (ws.stompClient && ws.stompClient.connected) {
      try {
        ws.stompClient.subscribe("/topic/gartic/lobby/" + lobbyCode, (m) => handleMessage(JSON.parse(m.body)));
        ws.stompClient.subscribe("/user/queue/gartic/lobby/" + lobbyCode, (m) => handleMessage(JSON.parse(m.body)))
        console.log("Hopefully subscribed to topic and queue");
      } catch (e) {
        console.log("Error, could not subscribe", e);
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
      requestRoundData();
      break;
    case "NEWROUND":
      showScreen(selectors.gameScreenTemplate);
      gameData = m.data;
      requestRoundData();
      break;
    case "TRACKRECEIVED":
      showScreen(selectors.trackSentTemplate);
      break;
    case "ROUNDDATA":
      roundData = m.data;
      setupGameScreen();
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

function sendTrack() {
  console.log("Sending created track...");
  ws.stompClient.send(`/gartic/lobby/${lobbyCode}/tracks/post`, {}, JSON.stringify({userId: config.userId, track: pianoRoll.getEditableTrack()}));
}

function requestRoundData() {
  console.log("Requesting sequence to server...");
  ws.stompClient.send(`/gartic/lobby/${lobbyCode}/sequences/get`, {}, JSON.stringify({userId: config.userId}))
}

async function setupPianoRoll(selectors) {
  const sequence = roundData.sequence;
  const instrumentData = roundData.instrumentData;
  pianoRoll = new PianoRoll({ instrument: instrumentData.program });
  pianoRoll.createVisualElement(selectors.pianoRollContainer, instrumentData.notes);
  pianoRoll.setFixedTracks(sequence.tracks);
  pianoRoll.bindControls(selectors);
  document.querySelector(selectors.sendButton).addEventListener("click", async (e) => {
    // let res = await saveSequence(pianoRoll);
    sendTrack();
  });
}

async function showInstructionsModal(selectors) {
  const instrumentData = roundData.instrumentData;
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
  console.log(roundData)
}

document.addEventListener("DOMContentLoaded", (e) => {
  subscribeWhenReady(lobbyCode);
  showScreen(selectors.waitingRoomTemplate);
  setupWaitingRoom();
});
