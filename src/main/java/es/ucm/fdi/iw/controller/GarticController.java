package es.ucm.fdi.iw.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
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
import es.ucm.fdi.iw.model.MIDISequence;
import es.ucm.fdi.iw.model.MIDITrack;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.model.GarticGame.GarticGameStatus;
import es.ucm.fdi.iw.repository.MIDIGameRepository;
import es.ucm.fdi.iw.repository.MIDISequenceRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Controller()
@RequestMapping("/gartic")
public class GarticController {

    private static final Logger log = LogManager.getLogger(GarticController.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private final MIDIGameRepository midiGameRepository;
    private final MIDISequenceRepository midiSequenceRepository;

    public GarticController(MIDIGameRepository midiGameRepository, MIDISequenceRepository midiSequenceRepository) {
        this.midiSequenceRepository = midiSequenceRepository;
        this.midiGameRepository = midiGameRepository;
    }

    @ModelAttribute
    public void populateModel(HttpSession session, Model model) {
        for (String name : new String[] { "u", "url", "ws", "topics"}) {
          model.addAttribute(name, session.getAttribute(name));
        }
        model.addAttribute("gameMode", "gartic");
        model.addAttribute("gameName", "Gartic Song");
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
        game.setRoundTime(60);
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
        if (!game.getPlayers().stream().anyMatch(lu->lu.getId() == u.getId())){
            model.addAttribute("showError", true);
            model.addAttribute("errorTitleKey", "lobby.error.notjoined.title");
            model.addAttribute("errorBodyKey", "lobby.error.notjoined.body");
            return "lobby";
        }

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
        model.addAttribute("playerList", game.getPlayers().stream().map((p)->p.getUsername()).toList());
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
        GarticGame game = (GarticGame)optGame.get();
        log.info("User {} joining lobby {}", u.getUsername(), lobbyCode);
        game.addPlayer(u);
        GameUpdate up = new GameUpdate("PLAYERSUPDATED", game.getPlayers().stream().map((p)->p.getUsername()).toList());
        messagingTemplate.convertAndSend("/topic/gartic/lobby/" + lobbyCode, up);
        return "redirect:/gartic/lobby/" + lobbyCode;
    }

    @MessageMapping("/gartic/lobby/{lobbyCode}/start")
    @Transactional
    public void startGame(@DestinationVariable String lobbyCode, @Payload StartRequest request) {
        GarticGame game = (GarticGame) midiGameRepository.findByLobbyCode(lobbyCode)
                .orElseThrow(() -> new IllegalArgumentException("Invalid lobby code"));
        if(game.getOwner().getId() != request.userId){
            return;
        }
        log.info("Starting game for lobby {} with {} players", lobbyCode, game.getPlayers().size());
        for (User p : game.getPlayers()) {
            log.debug("Creating sequence for player {} in lobby {}", p.getUsername(), lobbyCode);
            MIDISequence seq = new MIDISequence();
            seq.setGame(game);
            game.getSequences().add(seq);
            midiSequenceRepository.save(seq);
            game.getSequenceAssignments().put(p.getId(), seq.getId());
            game.getTrackSubmissions().put(p.getId(), false);
        }
        game.setStatus(GarticGameStatus.PLAYING);
        GameData data = new GameData(game.getCurrentRound(), game.getTotalRounds(), game.getStatus().name(), game.getRoundInstruments().get(game.getCurrentRound()));
        GameUpdate up = new GameUpdate("GAMESTARTED", data);
        messagingTemplate.convertAndSend("/topic/gartic/lobby/" + lobbyCode, up);
    }

    @MessageMapping("/gartic/lobby/{lobbyCode}/tracks/post")
    @SendToUser("/queue/gartic/lobby/{lobbyCode}")
    @Transactional
    public GameUpdate receiveTrack(@DestinationVariable String lobbyCode, @Payload TrackSubmission submission) {
        GarticGame game = (GarticGame) midiGameRepository.findByLobbyCode(lobbyCode).orElseThrow(() -> new IllegalArgumentException("Invalid lobby code"));
        long sequenceId = game.getSequenceAssignments()
            .get(submission.userId);
        MIDISequence sequence = midiSequenceRepository.findById(sequenceId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid sequence ID"));
        sequence.getTracks().add(new MIDITrack(submission.track, sequence));
        midiSequenceRepository.save(sequence);
        midiGameRepository.save(game);
        game.getTrackSubmissions().put(submission.userId, true);
        if(!game.getTrackSubmissions().containsValue(false)){
            // Todos los jugadores han acabado
            // Actualizamos la ronda
            game.setCurrentRound(game.getCurrentRound()+1);
            // Ponemos que ningun jugador ha enviado su track
            game.getTrackSubmissions().replaceAll((k, v) -> false);
            // Desplazamos las secuencias de cada jugador
            game.setSequenceAssignments(GameUtils.shiftValuesRight(game.getSequenceAssignments()));
            GameData data = new GameData(game.getCurrentRound(), game.getTotalRounds(), game.getStatus().name(), game.getRoundInstruments().get(game.getCurrentRound()));
            GameUpdate up = new GameUpdate("NEWROUND", data);
            messagingTemplate.convertAndSend("/topic/gartic/lobby/" + lobbyCode, up);
            return new GameUpdate("NULL", null);
        }
        return new GameUpdate("TRACKRECEIVED", null);
    }

    public record GameUpdate(String type, Object data) {}
    public record StartRequest(long userId) {} 
    public record TrackSubmission(long userId, MIDITrack.Transfer track) {}
    public record GameData(int currentRound, int totalRounds, String status, int instrument) {
    }
}
