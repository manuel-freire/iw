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
  playerCounter: ".player-counter",
  playerList: "#player-list",
  waitingRoomTemplate: "#waiting-room",
  gameScreenTemplate: "#game-screen",
  trackSentTemplate: "#track-sent"
};

let gameData, pianoRoll;

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
    case "NEWROUND":
      showScreen(selectors.gameScreenTemplate);
      gameData = m.data;
      setupGameScreen();
      break;
    case "TRACKRECEIVED":
      showScreen(selectors.trackSentTemplate);
      break;
  }
}

function updatePlayers(list) {
  console.log("Updating players...");
  document.querySelector(selectors.playerList).innerHTML = "";
  document.querySelectorAll(selectors.playerCounter).forEach(el => el.textContent = list.length)
  const ownerBadge = `<span class="badge bg-warning text-dark">Owner</span>`
  list.forEach((player) => {
    const html = `
    <div class="list-group-item d-flex bg-transparent align-items-center">
      <div class="bg-primary text-white rounded-circle d-flex align-items-center justify-content-center me-3" 
            style="width: 40px; height: 40px;">
          <span>${player.username.substring(0, 1)}</span>
      </div>
      <span class="flex-grow-1 text-start">${player.username}</span>
      ${player.isOwner ? ownerBadge : ""}
    </div>
    `
    document.querySelector(selectors.playerList).insertAdjacentHTML("beforeend", html);
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

async function setupPianoRoll(selectors) {
  const sequence = gameData.roundData.sequence;
  const instrumentData = gameData.roundData.instrumentData;
  pianoRoll = new PianoRoll({ instrument: instrumentData.program });
  pianoRoll.createVisualElement(selectors.pianoRollContainer, instrumentData.notes);
  pianoRoll.setFixedTracks(sequence.tracks);
  pianoRoll.bindControls(selectors);
  document.querySelector(selectors.sendButton).addEventListener("click", async (e) => {
    sendTrack();
  });
}

async function showInstructionsModal(selectors) {
  const instrumentData = gameData.roundData.instrumentData;
  document.querySelector(selectors.instructionsModalLabel).textContent =
    `Ronda ${gameData.currentRound + 1} de ${gameData.totalRounds}`;
  document.querySelector(selectors.instructionsModalBody).textContent =
    `Crea una pista de ${instrumentData.instrumentName} para la canción!`;
  const bsModal = new bootstrap.Modal(document.querySelector(selectors.instructionsModal));
  bsModal.show();
}

function setupWaitingRoom() {
  if(document.querySelector(selectors.startButton))
    document.querySelector(selectors.startButton).onclick = sendStartRequest;
}

function setupGameScreen() {
  setupPianoRoll(selectors);
  showInstructionsModal(selectors);
  console.log(gameData.roundData)
}

document.addEventListener("DOMContentLoaded", (e) => {
  subscribeWhenReady(lobbyCode);
  showScreen(selectors.waitingRoomTemplate);
  setupWaitingRoom();
});
