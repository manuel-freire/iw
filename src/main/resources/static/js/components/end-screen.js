async function getSequences() {
    const res = await fetch(`/api/game/${document.documentElement.dataset.lobbyCode}/sequence/getall`);
    return await res.json();
}

function createCardHTML(params){
    const html = 
        `<div class="card mb-3">
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
        `
    return html
}

let songs = [] 

const audioContext = new (window.AudioContext || window.webkitAudioContext)();

async function setupCards(){
    const sequences = await getSequences();
    for(i in sequences){
        document.querySelector("#cards-container").insertAdjacentHTML("beforeend", createCardHTML({
            progressBarId: `progressBar${i}`,
            playButtonId: `playButton${i}`,
            pauseButtonId: `pauseButton${i}`,
            stopButtonId: `stopButton${i}`,
        }))
        let pr = new PianoRoll({audioContext: audioContext});
        pr.setFixedTracks(sequences[i].tracks)
        pr.bindControls({
            playButton: `#playButton${i}`,
            pauseButton: `#pauseButton${i}`,
            stopButton: `#stopButton${i}`,
            progressBar: `#progressBar${i}`,
        })
        songs.push(pr)
    }
}

setupCards();




