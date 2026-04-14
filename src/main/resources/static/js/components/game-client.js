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
  endScreenCardsContainer: "#end-screen-cards-container",
  waitingRoomTemplate: "#waiting-room",
  gameScreenTemplate: "#game-screen",
  trackSentTemplate: "#track-sent",
  endScreenTemplate: "#end-screen",
  instrumentSelectContainer: "#instrument-select-container",
  numberOfRoundsSelector: "#select-rounds",
};

let gameData,
  pianoRoll,
  availableInstruments = null;

function subscribeWhenReady(lobbyCode) {
  const interval = setInterval(() => {
    if (ws.stompClient && ws.stompClient.connected) {
      try {
        ws.stompClient.subscribe("/topic/gartic/lobby/" + lobbyCode, (m) => handleMessage(JSON.parse(m.body)));
        ws.stompClient.subscribe("/user/queue/gartic/lobby/" + lobbyCode, (m) => handleMessage(JSON.parse(m.body)));
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
    case "GAMEENDED":
      showScreen(selectors.endScreenTemplate);
      setupEndScreen(m.data);
  }
}

function updatePlayers(list) {
  console.log("Updating players...");
  document.querySelector(selectors.playerList).innerHTML = "";
  document.querySelectorAll(selectors.playerCounter).forEach((el) => (el.textContent = list.length));
  const ownerBadge = `<span class="badge bg-warning text-dark">Owner</span>`;
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
    `;
    document.querySelector(selectors.playerList).insertAdjacentHTML("beforeend", html);
  });
}

function showScreen(selector) {
  console.log(`Showing screen "${selector}"`);
  const gameContainer = document.querySelector(selectors.gameContainer);
  const template = document.querySelector(`${selector}`);
  const instance = template.content.cloneNode(true);
  gameContainer.replaceChildren();
  gameContainer.appendChild(instance);
}

function sendStartRequest() {
  console.log("Sending start request...");
  let body = {
    userId: config.userId,
    totalRounds: parseInt(document.querySelector(selectors.numberOfRoundsSelector).value),
    roundInstruments: [],
  };
  for (let i = 0; i < body.totalRounds; i++)
    body.roundInstruments.push(parseInt(document.querySelector(`#select-instrument-round-${i}`).value));
  ws.stompClient.send(`/gartic/lobby/${lobbyCode}/start`, {}, JSON.stringify(body));
}

function sendTrack() {
  console.log("Sending created track...");
  ws.stompClient.send(
    `/gartic/lobby/${lobbyCode}/tracks/post`,
    {},
    JSON.stringify({ userId: config.userId, track: pianoRoll.getEditableTrack() }),
  );
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
  if (isOwner) {
    document.querySelector(selectors.startButton).onclick = sendStartRequest;
    if (availableInstruments == null) {
      fetch("/api/game/instrument/getall").then((r) => {
        if (r.ok)
          r.json().then((list) => {
            availableInstruments = list;
            createInstrumentSelects();
          });
      });
    } else createInstrumentSelects();
    document.querySelector(selectors.numberOfRoundsSelector).addEventListener("change", () => {
      createInstrumentSelects();
    });
  }
}

function createInstrumentSelects() {
  console.log("dasfkjnl", parseInt(document.querySelector(selectors.numberOfRoundsSelector).value));
  document.querySelector(selectors.instrumentSelectContainer).innerHTML = "";
  for (let i = 0; i < parseInt(document.querySelector(selectors.numberOfRoundsSelector).value); i++) {
    document.querySelector(selectors.instrumentSelectContainer).insertAdjacentHTML(
      "beforeend",
      `
      <div class="mb-3 form-floating">
        <select id="select-instrument-round-${i}" class="form-select">
          ${"".concat(
            ...availableInstruments.map(
              (ins, idx) =>
                `
            <option value=${ins.program} ${idx > 0 && ins.program != 128 && idx == i - 1 ? "selected" : ""} ${i == 0 && ins.program == 128 ? "selected" : ""}>
              ${ins.instrumentName}
            </option>
            `,
            ),
          )}
        </select>
        <label for="select-instrument-round-${i}" class="form-label">Ronda ${i + 1}</label>
      </div>
      `,
    );
  }
}

function setupGameScreen() {
  setupPianoRoll(selectors);
  showInstructionsModal(selectors);
  console.log(gameData.roundData);
}

function createCardHTML(params) {
  const html = `
    <div class="card mb-3">
      <div class="card-body row align-items-center py-5">
        <div class="col col-8">
          <input id="${params.progressBarId}" type="range" class="form-range">
        </div>
        <div class="col col-4">                        
          <div class="btn-group" role="group">
            <button id="${params.playButtonId}" type="button" class="btn btn-primary" th:title="#{topSongs.play}">
              <i class="bi bi-play-fill"></i>
            </button>
            <button id="${params.pauseButtonId}" type="button" class="btn btn-primary" th:title="#{topSongs.pause}">
              <i class="bi bi-pause-fill"></i>
            </button>
            <button id="${params.stopButtonId}" type="button" class="btn btn-primary" th:title="#{topSongs.stop}">
              <i class="bi bi-stop-fill"></i>
            </button>
          </div>
        </div>
      </div>
    </div>
    `;
  return html;
}

function setupCards(sequences) {
  for (let i in sequences) {
    document.querySelector(selectors.endScreenCardsContainer).insertAdjacentHTML(
      "beforeend",
      createCardHTML({
        progressBarId: `progressBarEnd${i}`,
        playButtonId: `playButtonEnd${i}`,
        pauseButtonId: `pauseButtonEnd${i}`,
        stopButtonId: `stopButtonEnd${i}`,
      }),
    );
    let pr = new PianoRoll({});
    pr.setFixedTracks(sequences[i].tracks);
    pr.bindControls({
      playButton: `#playButtonEnd${i}`,
      pauseButton: `#pauseButtonEnd${i}`,
      stopButton: `#stopButtonEnd${i}`,
      progressBar: `#progressBarEnd${i}`,
    });
  }
}

function setupEndScreen(sequences) {
  console.log(sequences);
  setupCards(sequences);
}

document.addEventListener("DOMContentLoaded", (e) => {
  subscribeWhenReady(lobbyCode);
  showScreen(selectors.waitingRoomTemplate);
  setupWaitingRoom();
});
