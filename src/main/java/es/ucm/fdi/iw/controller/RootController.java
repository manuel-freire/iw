package es.ucm.fdi.iw.controller;

import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.model.User.Role;
import es.ucm.fdi.iw.repository.ScoreRepository;
import jakarta.persistence.EntityManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

/**
 *  Non-authenticated requests only.
 */
@Controller
public class RootController {
    private static final Logger log = LogManager.getLogger(RootController.class);

    @Autowired
    private ScoreRepository scoreRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @ModelAttribute
    public void populateModel(HttpSession session, Model model) {        
        for (String name : new String[] { "u", "url", "ws", "topics"}) {
          model.addAttribute(name, session.getAttribute(name));
        }
    }

	@GetMapping("/login")
    public String login(Model model, HttpServletRequest request) {
        boolean error = request.getQueryString() != null && request.getQueryString().indexOf("error") != -1;
        boolean registered = request.getQueryString() != null && request.getQueryString().indexOf("registered") != -1;
        model.addAttribute("loginError", error);
        model.addAttribute("registered", registered);
        return "login";
    }

	@GetMapping("/register")
    public String register(Model model) {
        if (!model.containsAttribute("registerUser")) {
            model.addAttribute("registerUser", new User());
        }
        if (!model.containsAttribute("registerError")) {
            model.addAttribute("registerError", false);
        }
        return "register";
    }

	@PostMapping("/register")
    @Transactional
    public String registerUser(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String pass2,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            Model model) {

        User registerUser = new User();
        registerUser.setUsername(username == null ? null : username.trim());
        registerUser.setFirstName(firstName == null ? null : firstName.trim());
        registerUser.setLastName(lastName == null ? null : lastName.trim());
        model.addAttribute("registerUser", registerUser);

        if (registerUser.getUsername() == null || registerUser.getUsername().isBlank() ||
                password == null || password.isBlank()) {
            model.addAttribute("registerError", "register.error.required");
            return "register";
        }

        if (!password.equals(pass2)) {
            model.addAttribute("registerError", "register.error.passwordMismatch");
            return "register";
        }

        long existingUsers = entityManager.createNamedQuery("User.hasUsername", Long.class)
                .setParameter("username", registerUser.getUsername())
                .getSingleResult();

        if (existingUsers > 0) {
            model.addAttribute("registerError", "register.error.usernameExists");
            return "register";
        }

        registerUser.setPassword(passwordEncoder.encode(password));
        registerUser.setEnabled(true);
        registerUser.setRoles(Role.USER.name());
        entityManager.persist(registerUser);
        entityManager.flush();

        log.info("Nuevo usuario registrado: {} ({})", registerUser.getUsername(), registerUser.getId());

        return "redirect:/login?registered";
    }

	@GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    @GetMapping("/about")
    public String about(Model model) {
        return "about";
    }

    /*@GetMapping("/authors")
    public String authors(Model model) {
        return "authors";
    }*/

    @GetMapping("/games")
    public String games() {
        return "games";
    }

     @GetMapping("/favoriteSongs")
    public String favoriteSongs() {
        return "favoriteSongs";
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
        model.addAttribute("scores", scoreRepository.findAllByOrderByTotalPointsDesc());
        return "leaderboard";
    }
}
