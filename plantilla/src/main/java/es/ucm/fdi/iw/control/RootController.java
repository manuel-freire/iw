package es.ucm.fdi.iw.control;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import es.ucm.fdi.iw.model.User;

/**
 * Landing-page controller
 * 
 * @author mfreire
 */
@Controller
public class RootController {
	
	private static final Logger log = LogManager.getLogger(RootController.class);

	@GetMapping("/")
	public String index(Model model) {
		return "index";
	}

	@GetMapping("/chat")
	public String chat(Model model, HttpServletRequest request) {
		return "chat";
	}
	
	@GetMapping("/error")
	public String error(Model model) {
		return "error";
	}

	@GetMapping("/class")
	public String classes(Model model) {
		return "class";
	}

	@GetMapping("/contest")
	public String contest(Model model) {
		return "contest";
	}
	
	@GetMapping("/play")
	public String play(Model model) {
		return "play";
	}

	@GetMapping("/profile")
	public String profile(Model model) {
		return "profile";
	}

	@GetMapping("/team")
	public String team(Model model) {
		return "team";
	}
	
	@GetMapping("/rankings")
	public String rankings(Model model) {
		return "rankings";
	}
}
