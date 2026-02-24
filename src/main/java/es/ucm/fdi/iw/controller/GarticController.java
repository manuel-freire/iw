package es.ucm.fdi.iw.controller;

import java.io.IOException;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.repository.MIDIGameRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Controller()
@RequestMapping("/gartic")
public class GarticController {

    private static final Logger log = LogManager.getLogger(UserController.class);

    private final MIDIGameRepository midiGameRepository;

    public GarticController(MIDIGameRepository midiGameRepository) {
        this.midiGameRepository = midiGameRepository;
    }

    @ModelAttribute
    public void populateModel(HttpSession session, Model model) {
        User u = (User) session.getAttribute("u");
        model.addAttribute("u", u);
        model.addAttribute("logged", u != null);
        model.addAttribute("gameMode", "gartic");
        model.addAttribute("gameName", "Gartic Song");
    }

    @GetMapping("")
    public String mainPage(HttpSession session, Model model) {
        return "lobby";
    }

    @PostMapping("/lobby/create")
    @Transactional
    public String createLobby(HttpSession session, Model model) throws IOException {
        User u = (User) session.getAttribute("u");
        if(u == null){
            model.addAttribute("showError", true);
            model.addAttribute("errorTitleKey", "lobby.error.notlogged.title");
            model.addAttribute("errorBodyKey", "lobby.error.notlogged.body");
            return "lobby";
        }
        GarticGame game = new GarticGame();

        //TODO reemplazar esto con una funcion bien que revise que el codigo no sea repetido etc.
        //String lobbyCode = new Random().ints(6, 0, 26).mapToObj(i -> String.valueOf((char)('A' + i))).collect(Collectors.joining());
        String lobbyCode = GameUtils.generateRandomCode(6);

        game.setLobbyCode(lobbyCode);
        game.setOwner(u);
        game.addPlayer(u);
        midiGameRepository.save(game);

        session.setAttribute("currentGame", game);

        return "redirect:/gartic/lobby/" + lobbyCode + "/";
    }

    @GetMapping("/lobby/{lobbyCode}/")
    public String getLobby(HttpSession session, @PathVariable String lobbyCode, Model model) {
        User u = (User) session.getAttribute("u");

        Optional<MIDIGame> optGame = midiGameRepository.findByLobbyCode(lobbyCode);
        if(optGame.isEmpty()){
            model.addAttribute("showError", true);
            model.addAttribute("errorTitleKey", "lobby.error.notfound.title");
            model.addAttribute("errorBodyKey", "lobby.error.notfound.body");
            return "lobby";
        }
        MIDIGame game = optGame.get();
        if(game.getOwner().getId() == u.getId()){
            model.addAttribute("isOwner", true);
        }
        model.addAttribute("playerList", game.getPlayers());
        return "waitingRoom";
    }

    @PostMapping("/lobby/join")
    @Transactional
    public String joinLobby(HttpSession session, @RequestParam String lobbyCode, Model model) throws IOException {
        User u = (User) session.getAttribute("u");
        if(u == null){
            model.addAttribute("showError", true);
            model.addAttribute("errorTitleKey", "lobby.error.notlogged.title");
            model.addAttribute("errorBodyKey", "lobby.error.notlogged.body");
            return "lobby";
        }
        Optional<MIDIGame> optGame = midiGameRepository.findByLobbyCode(lobbyCode);
        if(optGame.isEmpty()){
            model.addAttribute("showError", true);
            model.addAttribute("errorTitleKey", "lobby.error.notfound.title");
            model.addAttribute("errorBodyKey", "lobby.error.notfound.body");
            return "lobby";
        }
        MIDIGame game = optGame.get();
        game.addPlayer(u);
        return "redirect:/gartic/lobby/" + lobbyCode + "/";
    }

    // TODO esto es tempora, cuando se implementen los websockets estos se encargaran de mostrar la pantalla apropiada en /lobby/{lobbyCode} de acuerdo al estado de la partida
    @GetMapping("/lobby/{lobbyCode}/gamescreen")
    public String showGameScreen(HttpSession session, @PathVariable String lobbyCode){
        return "gartic";
    }

}
