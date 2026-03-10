"use strict";

class PianoRoll {
    constructor(params) {
        this.audioContext = params.audioContext || new AudioContext();
        this.bpm = params.bpm || 140;
        this.instrument = params.instrument || 1;
        this.measures = params.measures || 4;
        this.beatsPerMeasure = params.beatsPerMeasure || 8;
        this.sequence = new ABCJS.synth.SynthSequence();
        this.sequence.totalDuration = this.measures;
        this.editableTrackIdx = this.sequence.addTrack();
        this.sequence.setInstrument(this.editableTrackIdx, this.instrument);
        this.synth = new ABCJS.synth.CreateSynth();
    }

    setInstrument(program) {
        this.instrument = program;
        this.sequence.setInstrument(this.editableTrackIdx, this.instrument);
    }

    setTempo(bpm) {
        this.bpm = bpm;
    }

    getEditableTrack() {
        let trackTransfer = {
            instrument: this.sequence.currentInstrument[this.editableTrackIdx],
            notes: [],
        };
        for (const item of this.sequence.tracks[this.editableTrackIdx]) {
            if (item.cmd == "note") {
                trackTransfer.notes.push({
                    pitch: item.pitch,
                    time: item.start,
                });
            }
        }
        return trackTransfer;
    }

    setFixedTracks(tracks) {
        for (const t of tracks) {
            let idx = this.sequence.addTrack();
            this.sequence.setInstrument(idx, t.instrument);
            for (const n of t.notes) {
                this.sequence.tracks[idx].push({
                    cmd: "note",
                    duration: 1 / this.beatsPerMeasure,
                    gap: 0,
                    instrument: t.instrument,
                    pitch: parseInt(n.pitch),
                    start: n.time,
                    volume: 80,
                });
            }
        }
    }

    updateProgressBar(pianoRoll) {
        if (!pianoRoll.progressBar || !pianoRoll.synth.duration || !pianoRoll.isPlaying) return;
        const timeElapsed = pianoRoll.audioContext.currentTime - (pianoRoll.prevTime || 0);
        pianoRoll.percentPlayed = pianoRoll.percentPlayed + timeElapsed / pianoRoll.synth.duration;
        pianoRoll.prevTime += timeElapsed;
        pianoRoll.progressBar.value = pianoRoll.percentPlayed;
        if (pianoRoll.percentPlayed < 1) requestAnimationFrame(() => pianoRoll.updateProgressBar(pianoRoll));
        else {
            pianoRoll.isPlaying = false;
            pianoRoll.percentPlayed = 0;
            pianoRoll.progressBar.value = 0;
            if (pianoRoll.loop) pianoRoll.play();
        }
    }

    async play() {
        await this.synth.init({
            sequence: this.sequence,
            audioContext: this.audioContext,
            // 4/4 time
            millisecondsPerMeasure: (4 * 60000) / this.bpm,
        });
        await this.synth.prime();
        await this.synth.start();
        this.isPlaying = true;
        this.prevTime = this.audioContext.currentTime;
        this.synth.seek(this.progressBar.value, "percent");
        this.percentPlayed = parseFloat(this.progressBar.value);
        requestAnimationFrame(() => this.updateProgressBar(this));
    }

    stop() {
        this.progressBar.value = 0;
        this.percentPlayed = 0;
        this.isPlaying = false;
        this.synth.stop();
    }

    pause() {
        this.isPlaying = false;
        this.synth.stop();
    }

    clearTrack() {
        this.stop();
        this.sequence.tracks[this.editableTrackIdx] = [];
        document.querySelectorAll(".note.active").forEach((note) => {
            note.classList.remove("active");
        });
    }

    createVisualElement(selector, keys) {
        let container = document.querySelector(selector);
        container.classList.add("h-scroll");
        const nCols = this.measures * this.beatsPerMeasure;
        for (const k of keys) {
            let keyRow = document.createElement("div");
            keyRow.id = `keyRow-${k.pitch}`;
            keyRow.classList.add("key-row");
            for (let col = 0; col < nCols; col++) {
                let keyElm = document.createElement("div");
                keyElm.id = `key-${k.pitch}-${col}`;
                keyElm.title = k.label;
                keyElm.classList.add("note");
                keyElm.classList.add(k.isBlack ? "black-key" : "white-key");
                if (col == 0 && k.showLabel) keyElm.classList.add("show-label");
                if (col % 8 == 7) keyElm.classList.add("measure-end");
                keyElm.dataset.pitch = k.pitch;
                keyElm.dataset.start = col / this.beatsPerMeasure;
                keyElm.insertAdjacentHTML("beforeend", `<span class="note-text">${k.label}</span>`);
                keyElm.addEventListener("click", (e) => {
                    e.currentTarget.classList.toggle("active");
                    if (e.currentTarget.classList.contains("active")) {
                        this.sequence.tracks[this.editableTrackIdx].push({
                            cmd: "note",
                            duration: 1 / this.beatsPerMeasure,
                            gap: 0,
                            instrument: this.instrument,
                            pitch: parseInt(e.currentTarget.dataset.pitch),
                            start: parseFloat(e.currentTarget.dataset.start),
                            volume: 80,
                        });
                    } else {
                        const noteIdx = this.sequence.tracks[this.editableTrackIdx].findIndex(
                            (note) => note.pitch == parseInt(e.currentTarget.dataset.pitch) && note.start == parseFloat(e.currentTarget.dataset.start),
                        );
                        if (noteIdx != -1) this.sequence.tracks[this.editableTrackIdx].splice(noteIdx, 1);
                    }
                });
                keyRow.append(keyElm);
            }
            container.prepend(keyRow);
        }
    }

    /**
     * Binds event listeners to control elements for the piano roll player.
     * @param {Object} selectors - An object containing CSS selectors for control elements
     * @param {string} [selectors.playButton] - CSS selector for the play button
     * @param {string} [selectors.stopButton] - CSS selector for the stop button
     * @param {string} [selectors.pauseButton] - CSS selector for the pause button
     * @param {string} [selectors.clearButton] - CSS selector for the clear button
     * @param {string} [selectors.progressBar] - CSS selector for the progress bar input element
     * @param {string} [selectors.loopButton] - CSS selector for the loop button
     * @returns {void}
     */
    bindControls(selectors) {
        if (selectors.playButton && document.querySelector(selectors.playButton))
            document.querySelector(selectors.playButton).addEventListener("click", () => this.play());
        if (selectors.stopButton && document.querySelector(selectors.stopButton))
            document.querySelector(selectors.stopButton).addEventListener("click", () => this.stop());
        if (selectors.pauseButton && document.querySelector(selectors.pauseButton))
            document.querySelector(selectors.pauseButton).addEventListener("click", () => this.pause());
        if (selectors.clearButton && document.querySelector(selectors.clearButton))
            document.querySelector(selectors.clearButton).addEventListener("click", () => this.clearTrack());
        if (selectors.progressBar && document.querySelector(selectors.progressBar)) {
            this.progressBar = document.querySelector(selectors.progressBar);
            this.progressBar.min = 0;
            this.progressBar.max = 1;
            this.progressBar.step = 0.01;
            this.progressBar.value = 0;
        }
        if (selectors.loopButton && document.querySelector(selectors.loopButton))
            document.querySelector(selectors.loopButton).addEventListener("click", (e) => {
                e.currentTarget.classList.toggle("btn-secondary");
                e.currentTarget.classList.toggle("btn-primary");
                this.loop = !this.loop;
            });
    }
}
