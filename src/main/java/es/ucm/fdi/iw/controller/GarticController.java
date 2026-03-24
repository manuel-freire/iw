package es.ucm.fdi.iw.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.ucm.fdi.iw.auxiliar.GameUtils;
import es.ucm.fdi.iw.model.GarticGame;
import es.ucm.fdi.iw.model.MIDIGame;
import es.ucm.fdi.iw.model.MIDIInstrument;
import es.ucm.fdi.iw.model.MIDISequence;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.model.GarticGame.GarticGameStatus;
import es.ucm.fdi.iw.repository.MIDIGameRepository;
import es.ucm.fdi.iw.repository.MIDISequenceRepository;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Controller()
@RequestMapping("/gartic")
public class GarticController {

    private static final Logger log = LogManager.getLogger(GarticController.class);

    private final MIDIGameRepository midiGameRepository;
    private final MIDISequenceRepository midiSequenceRepository;

    public GarticController(MIDIGameRepository midiGameRepository, MIDISequenceRepository midiSequenceRepository) {
        this.midiSequenceRepository = midiSequenceRepository;
        this.midiGameRepository = midiGameRepository;
    }

    @ModelAttribute
    public void populateModel(HttpSession session, Model model) {
        User u = (User) session.getAttribute("u");
        model.addAttribute("u", u);
        model.addAttribute("logged", u != null);
        model.addAttribute("gameMode", "gartic");
        model.addAttribute("gameName", "Canción Sorpresa");
    }

    @ModelAttribute
    public void populateLobbyModel(@PathVariable(required = false) String lobbyCode, Model model) {
        if (lobbyCode != null) {
            model.addAttribute("lobbyCode", lobbyCode);
        }
    }

    @GetMapping("")
    public String mainPage(HttpSession session, Model model) {
        log.debug("Rendering main lobby page");
        return "lobby";
    }

    @PostMapping("/lobby/create")
    @Transactional
    public String createLobby(HttpSession session, Model model) throws IOException {
        User u = (User) session.getAttribute("u");
        if (u == null) {
            log.warn("Attempt to create lobby without being logged in");
            model.addAttribute("showError", true);
            model.addAttribute("errorTitleKey", "lobby.error.notlogged.title");
            model.addAttribute("errorBodyKey", "lobby.error.notlogged.body");
            return "lobby";
        }
        GarticGame game = new GarticGame();

        String lobbyCode;
        do {
            lobbyCode = GameUtils.generateRandomCode(6);
        } while (midiGameRepository.existsByLobbyCode(lobbyCode));

        game.setStatus(GarticGameStatus.WAITING);
        game.setLobbyCode(lobbyCode);
        game.setOwner(u);
        game.addPlayer(u);
        game.setCurrentRound(0);
        // TODO should be user generated
        game.setTotalRounds(4);
        List<Integer> roundInstruments = Arrays.asList(128, 34, 1, 56);
        game.setRoundInstruments(roundInstruments);
        midiGameRepository.save(game);

        session.setAttribute("currentGame", game);
        log.info("Created lobby {} for user {}", lobbyCode, u.getUsername());

        return "redirect:/gartic/lobby/" + lobbyCode;
    }

    @GetMapping("/lobby/{lobbyCode}")
    public String getLobby(HttpSession session, @PathVariable String lobbyCode, Model model) {
        User u = (User) session.getAttribute("u");
        if (u == null) {
            model.addAttribute("showError", true);
            model.addAttribute("errorTitleKey", "lobby.error.notlogged.title");
            model.addAttribute("errorBodyKey", "lobby.error.notlogged.body");
            return "lobby";
        }

        Optional<MIDIGame> optGame = midiGameRepository.findByLobbyCode(lobbyCode);
        if (optGame.isEmpty()) {
            log.warn("Lobby not found for code {}", lobbyCode);
            model.addAttribute("showError", true);
            model.addAttribute("errorTitleKey", "lobby.error.notfound.title");
            model.addAttribute("errorBodyKey", "lobby.error.notfound.body");
            return "lobby";
        }
        GarticGame game = (GarticGame)optGame.get();
        log.debug("User {} accessing lobby {}", u == null ? "anonymous" : u.getUsername(), lobbyCode);
        if(game.getCurrentRound() == game.getTotalRounds()){
            game.setStatus(GarticGameStatus.FINISHED);
            midiGameRepository.save(game);
        } else {
            model.addAttribute("instrument", game.getRoundInstruments().get(game.getCurrentRound()));
        }
        if (game.getOwner().getId() == u.getId()) 
            model.addAttribute("isOwner", true);
        model.addAttribute("currentRound", game.getCurrentRound());
        model.addAttribute("totalRounds", game.getTotalRounds());
        model.addAttribute("gameStatus", game.getStatus());
        model.addAttribute("playerList", game.getPlayers());
        log.info("Lobby {} has {} players", lobbyCode, game.getPlayers().size());
        return "gartic";
    }

    @PostMapping("/lobby/join")
    @Transactional
    public String joinLobby(HttpSession session, @RequestParam String lobbyCode, Model model) throws IOException {
        User u = (User) session.getAttribute("u");
        if (u == null) {
            log.warn("Unauthorized user tried to join lobby {}", lobbyCode);
            model.addAttribute("showError", true);
            model.addAttribute("errorTitleKey", "lobby.error.notlogged.title");
            model.addAttribute("errorBodyKey", "lobby.error.notlogged.body");
            return "lobby";
        }
        Optional<MIDIGame> optGame = midiGameRepository.findByLobbyCode(lobbyCode);
        if (optGame.isEmpty()) {
            log.warn("User {} tried to join missing lobby {}", u.getUsername(), lobbyCode);
            model.addAttribute("showError", true);
            model.addAttribute("errorTitleKey", "lobby.error.notfound.title");
            model.addAttribute("errorBodyKey", "lobby.error.notfound.body");
            return "lobby";
        }
        MIDIGame game = optGame.get();
        log.info("User {} joining lobby {}", u.getUsername(), lobbyCode);
        game.addPlayer(u);
        return "redirect:/gartic/lobby/" + lobbyCode;
    }

    @PostMapping("/lobby/start")
    public String startGame(HttpSession session, @RequestParam String lobbyCode, Model model) {
        // TODO esto lo deberia llamar una vez el owner y deberia cambiar la pagina de
        // todos con ws, de momento solo funciona para el owner
        GarticGame game = (GarticGame) midiGameRepository.findByLobbyCode(lobbyCode)
                .orElseThrow(() -> new IllegalArgumentException("Invalid lobby code"));
        log.info("Starting game for lobby {} with {} players", lobbyCode, game.getPlayers().size());
        for (User u : game.getPlayers()) {
            log.debug("Creating sequence for player {} in lobby {}", u.getUsername(), lobbyCode);
            MIDISequence seq = new MIDISequence();
            seq.setGame(game);
            game.getSequences().add(seq);
            midiSequenceRepository.save(seq);
            game.getTrackAssignments().put(u.getId(), seq.getId());
        }
        game.setStatus(GarticGameStatus.PLAYING);
        midiGameRepository.save(game);
        return "redirect:/gartic/lobby/" + lobbyCode;
    }
}
