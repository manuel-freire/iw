package es.ucm.fdi.iw.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 *  Non-authenticated requests only.
 */
@Controller
public class RootController {

    private static final Logger log = LogManager.getLogger(RootController.class);

    @ModelAttribute
    public void populateModel(HttpSession session, Model model) {        
        for (String name : new String[] { "u", "url", "ws", "topics"}) {
          model.addAttribute(name, session.getAttribute(name));
        }
    }

	@GetMapping("/login")
    public String login(Model model, HttpServletRequest request) {
        boolean error = request.getQueryString() != null && request.getQueryString().indexOf("error") != -1;
        model.addAttribute("loginError", error);
        return "login";
    }

	@GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    @GetMapping("/about")
    public String about(Model model) {
        return "about";
    }

    @GetMapping("/authors")
    public String authors(Model model) {
        return "authors";
    }

    @GetMapping("/games")
    public String games() {
        return "games";
    }

     @GetMapping("/favoriteSongs")
    public String favoriteSongs() {
        return "favoriteSongs";
    }
      
    @GetMapping("/guess")
    public String guess() {
        return "guess";
    }

    @GetMapping("/lobby/{mode}")
    public String lobby(@PathVariable String mode, Model model) {
        String view = "lobby";
        String gameName;

        switch (mode) {
            case "guess":
                gameName = "🎵 Adivina la canción";
                view = "guess";
                break;
            case "sorpresa":
                gameName = "🎲 Canción sorpresa";
                break;
            case "continuacion":
                gameName = "▶️ Continuación de canción";
                break;
            default:
                gameName = "Juego";
        }

        model.addAttribute("gameName", gameName);
        model.addAttribute("gameMode", mode);

        return view;
    }

    @GetMapping("/lobby/{mode}/{action}")
    public String lobbyAction(
            @PathVariable String mode,
            @PathVariable String action,
            Model model) {

        if (!action.equals("create") && !action.equals("join")) {
            return "redirect:/lobby/" + mode;
        }
        
        if (mode.equals("gartic")) {
            return "gartic";
        }
        
        if (mode.equals("continue")) {
            return "continue";
        }

        return "redirect:/lobby/" + mode;
    }

    @GetMapping("/leaderboard")
    public String leaderboard(Model model) {
        return "leaderboard";
    }
}
