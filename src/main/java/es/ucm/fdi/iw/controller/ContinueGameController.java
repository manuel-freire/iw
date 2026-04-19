package es.ucm.fdi.iw.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
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

import es.ucm.fdi.iw.model.ContinueGame;
import es.ucm.fdi.iw.model.MIDIGame;
import es.ucm.fdi.iw.model.MIDIInstrument;
import es.ucm.fdi.iw.model.MIDISequence;
import es.ucm.fdi.iw.model.MIDITrack;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.model.ContinueGame.ContinueGameStatus;
import es.ucm.fdi.iw.repository.MIDIGameRepository;
import es.ucm.fdi.iw.repository.MIDIInstrumentRepository;
import es.ucm.fdi.iw.repository.MIDISequenceRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
@Controller()
@RequestMapping("/continue")
public class ContinueGameController {
    private static final Logger log = LogManager.getLogger(GarticController.class);
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private final MIDIGameRepository midiGameRepository;
    private final MIDISequenceRepository midiSequenceRepository;
    private final MIDIInstrumentRepository midiInstrumentRepository;

    public ContinueGameController(MIDIGameRepository midiGameRepository, MIDISequenceRepository midiSequenceRepository, MIDIInstrumentRepository midiInstrumentRepository) {
        this.midiSequenceRepository = midiSequenceRepository;
        this.midiGameRepository = midiGameRepository;
        this.midiInstrumentRepository = midiInstrumentRepository;
    }
    @ModelAttribute
    public void populateModel(HttpSession session, Model model) {
        for (String name : new String[] { "u", "url", "ws", "topics"}) {
          model.addAttribute(name, session.getAttribute(name));
        }
        model.addAttribute("gameMode", "continue");
        model.addAttribute("gameName", "Continuacion de cancion");
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
        ContinueGame game = new ContinueGame();

        String lobbyCode;
        do {
            lobbyCode = GameUtils.generateRandomCode(6);
        } while (midiGameRepository.existsByLobbyCode(lobbyCode));

        game.setStatus(ContinueGameStatus.WAITING);
        game.setLobbyCode(lobbyCode);
        game.setOwner(u);
        game.addPlayer(u);
        game.setCurrentRound(0);
        game.setTotalRounds(4);
        game.setRoundTime(60);
        List<Integer> roundInstruments = Arrays.asList(128, 34, 1, 56);
        game.setRoundInstruments(roundInstruments);
        midiGameRepository.save(game);

        session.setAttribute("currentGame", game);
        log.info("Created lobby {} for user {}", lobbyCode, u.getUsername());

        return "redirect:/continue/lobby/" + lobbyCode;
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
        ContinueGame game = (ContinueGame)optGame.get();
        if (!game.getPlayers().stream().anyMatch(lu->lu.getId() == u.getId())){
            model.addAttribute("showError", true);
            model.addAttribute("errorTitleKey", "lobby.error.notjoined.title");
            model.addAttribute("errorBodyKey", "lobby.error.notjoined.body");
            return "lobby";
        }

        log.debug("User {} accessing lobby {}", u == null ? "anonymous" : u.getUsername(), lobbyCode);
        if(game.getCurrentRound() == game.getTotalRounds()){
            game.setStatus(ContinueGameStatus.FINISHED);
            midiGameRepository.save(game);
        } else {
            model.addAttribute("instrument", game.getRoundInstruments().get(game.getCurrentRound()));
        }
        model.addAttribute("isOwner", game.getOwner().getId() == u.getId());
        model.addAttribute("currentRound", game.getCurrentRound());
        model.addAttribute("totalRounds", game.getTotalRounds());
        model.addAttribute("gameStatus", game.getStatus());
        model.addAttribute("playerList", game.getPlayers().stream().map((p)->new PlayerInfo(p.getUsername(), game.getOwner().getId() == p.getId())).toList());
        log.info("Lobby {} has {} players", lobbyCode, game.getPlayers().size());
        return "continue";
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
        ContinueGame game = (ContinueGame) optGame.get();
        log.info("User {} joining lobby {}", u.getUsername(), lobbyCode);
        game.addPlayer(u);
        GameUpdate up = new GameUpdate("PLAYERSUPDATED",
                game.getPlayers().stream()
                        .map((p) -> new PlayerInfo(p.getUsername(), game.getOwner().getId() == p.getId())).toList());
        messagingTemplate.convertAndSend("/topic/continue/lobby/" + lobbyCode, up);
        return "redirect:/continue/lobby/" + lobbyCode;
    }
    @MessageMapping("/continue/lobby/{lobbyCode}/start")
    @Transactional
    public void startGame(@DestinationVariable String lobbyCode, @Payload GameStartRequest request) {
        // Obtenemos la partida
        ContinueGame game = (ContinueGame) midiGameRepository.findByLobbyCode(lobbyCode)
                .orElseThrow(() -> new IllegalArgumentException("Invalid lobby code"));
        // Solo el propietario puede iniciar la partida
        if (game.getOwner().getId() != request.userId)
            return;
        log.info("Starting game for lobby {} with {} players", lobbyCode, game.getPlayers().size());
        // Indicamos que la partida ha iniciado
        game.setStatus(ContinueGameStatus.PLAYING);
        game.setTotalRounds(request.totalRounds);
        game.setRoundInstruments(request.roundInstruments);
        for (User p : game.getPlayers()) {
            log.debug("Creating sequence for player {} in lobby {}", p.getUsername(), lobbyCode);
            // Creamos una secuencia vacia para cada jugador
            MIDISequence seq = new MIDISequence();
            seq.setTracks(new LinkedList<MIDITrack>());
            seq.setGame(game);
            game.getSequences().add(seq);
            midiSequenceRepository.save(seq);
            // La asignamos al jugador
            game.getSequenceAssignments().put(p.getId(), seq.getId());
            game.getTrackSubmissions().put(p.getId(), false);
            // Enviamos los datos al jugador
            MIDIInstrument.Transfer instruData = midiInstrumentRepository
                    .findByProgram(game.getRoundInstruments().get(game.getCurrentRound()))
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Program")).toTransfer();
            GameData data = new GameData(game.getCurrentRound(), game.getTotalRounds(), game.getStatus().name(),
                    new RoundData(instruData, seq.toTransfer()));
            GameUpdate up = new GameUpdate("GAMESTARTED", data);
            messagingTemplate.convertAndSendToUser(p.getUsername(), "/queue/continue/lobby/" + lobbyCode, up);
        }
        
    }
    @MessageMapping("/continue/lobby/{lobbyCode}/tracks/post")
    @SendToUser("/queue/continue/lobby/{lobbyCode}")
    @Transactional
    public GameUpdate receiveTrack(@DestinationVariable String lobbyCode, @Payload TrackSubmission submission) {
        // Obtenemos la partida
        ContinueGame game = (ContinueGame) midiGameRepository.findByLobbyCode(lobbyCode)
                .orElseThrow(() -> new IllegalArgumentException("Invalid lobby code"));
        game.getTrackSubmissions().put(submission.userId, true);
        if (!game.getTrackSubmissions().containsValue(false)) {
            // Todos los jugadores han acabado
            // Actualizamos la ronda
            game.setCurrentRound(game.getCurrentRound() + 1);
            // El juego ha acabado
            if (game.getCurrentRound() == game.getTotalRounds()) {
                messagingTemplate.convertAndSend("/topic/continue/lobby/" + lobbyCode, new GameUpdate("GAMEENDED",
                        game.getSequences().stream().map(MIDISequence::toTransfer).toList()));
                return new GameUpdate("NULL", null);
            }
            // Ponemos que ningun jugador ha enviado su track
            game.getTrackSubmissions().replaceAll((k, v) -> false);
            // Desplazamos las secuencias de cada jugador
            //TODO:esto aqui no se deberia hacer, necesitamos iniciar un voto por el mejor track
            //game.setSequenceAssignments(GameUtils.shiftValuesRight(game.getSequenceAssignments()));

            Integer maxVotes=0;
            long bestSequenceId=-1;
            for (User p : game.getPlayers())
            {
                long playerSequenceId = game.getSequenceAssignments()
                        .get(p.getId());
                MIDISequence seq = midiSequenceRepository.findById(playerSequenceId)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid sequence ID"));
                Integer votes = game.get_sequenceVotes().get(playerSequenceId);
                if (votes != null) {
                    maxVotes = Math.max(votes, maxVotes);
                    if(maxVotes==votes)
                    {
                        bestSequenceId=playerSequenceId;
                    }
                }
            }
            // Anadimos el mejor track a la secuencia
            MIDISequence sequence = midiSequenceRepository.findById(bestSequenceId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid sequence ID"));
            sequence.getTracks().add(new MIDITrack(submission.track, sequence));
            midiSequenceRepository.save(sequence);
            midiGameRepository.save(game);
            // Notificamos que empieza una nueva ronda y enviamos a cada jugador su nueva
            // secuencia
            for (User p : game.getPlayers()) {
                long playerSequenceId = game.getSequenceAssignments()
                        .get(p.getId());
                MIDISequence seq = midiSequenceRepository.findById(playerSequenceId)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid sequence ID"));
                MIDIInstrument.Transfer instruData = midiInstrumentRepository
                        .findByProgram(game.getRoundInstruments().get(game.getCurrentRound()))
                        .orElseThrow(() -> new IllegalArgumentException("Invalid Program")).toTransfer();
                GameData data = new GameData(game.getCurrentRound(), game.getTotalRounds(), game.getStatus().name(),
                        new RoundData(instruData, seq.toTransfer()));
                GameUpdate up = new GameUpdate("NEWROUND", data);
                messagingTemplate.convertAndSendToUser(p.getUsername(), "/queue/continue/lobby/" + lobbyCode, up);
            }
            return new GameUpdate("NULL", null);
        }
        return new GameUpdate("TRACKRECEIVED", null);
    }

    @MessageMapping("/continue/lobby/{lobbyCode}/chat")
    public void chat(@DestinationVariable String lobbyCode, @Payload ChatMessage msg) {
        messagingTemplate.convertAndSend("/topic/continue/lobby/" + lobbyCode + "/chat", msg);
    }


    public record GameUpdate(String type, Object data) {}
    public record UserRequest(long userId) {} 
    public record GameStartRequest(long userId, int totalRounds, List<Integer> roundInstruments) {} 
    public record TrackSubmission(long userId, MIDITrack.Transfer track) {}
    public record RoundData(MIDIInstrument.Transfer instrumentData, MIDISequence.Transfer sequence) {}
    public record GameData(int currentRound, int totalRounds, String status, RoundData roundData) {}
    public record PlayerInfo(String username, boolean isOwner) {}
    public record ChatMessage(String username, String text) {}
}
