package es.ucm.fdi.iw.control;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import es.ucm.fdi.iw.LocalData;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.model.User.Role;

/**
 * Landing-page controller
 * 
 * @author mfreire
 */
@Controller
public class RootController {
	
	private static final Logger log = LogManager.getLogger(RootController.class);
	
	@Autowired 
	private EntityManager entityManager;
	
	@Autowired
	private LocalData localData;

	@Autowired 
	private AuthenticationManager authenticationManager;

	@GetMapping("/")
	public String index(final Model model) {
		return "index";
	}

	@GetMapping("/chat")
	public String chat(final Model model, final HttpServletRequest request) {
		return "chat";
	}

	@GetMapping("/error")
	public String error(final Model model) {
		return "error";
	}

	@GetMapping("/token/{token}")
	public String token(@PathVariable final String token, final HttpSession session, final HttpServletRequest request) {
		User u = null;
		try {
			u = entityManager.createNamedQuery("User.byToken", User.class).setParameter("token", token)
					.getSingleResult();
			// if no exception here, the group code is valid - yay!
		} catch (final Exception e) {
			log.warn("Invalid token: {}", token);
			return "index";
		}

		doAutoLogin(u.getUsername(), u.getToken(), request);
		log.info("Logging in student {}, with ID {} and token {}", u.getUsername(), u.getId(), u.getToken());

		// add 'u' and 'g' session attributes
		session.setAttribute("u", u);
		// add a 'ws' session variable
		session.setAttribute("ws", request.getRequestURL().toString().replaceFirst("[^:]*", "ws") // http[s]://... =>
																									// ws://...
				.replaceFirst("/clase.*", "/ws"));

		return "redirect:/user/" + u.getId();
	}

	/**
	 * Non-interactive authentication; user and password must already exist
	 * 
	 * @param username
	 * @param password
	 * @param request
	 */
	private void doAutoLogin(final String username, final String password, final HttpServletRequest request) {
		try {
			// Must be called from request filtered by Spring Security, otherwise
			// SecurityContextHolder is not updated
			final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username,
					password);
			token.setDetails(new WebAuthenticationDetails(request));
			final Authentication authentication = authenticationManager.authenticate(token);
			log.debug("Logging in with [{}]", authentication.getPrincipal());
			SecurityContextHolder.getContext().setAuthentication(authentication);
		} catch (final Exception e) {
	        SecurityContextHolder.getContext().setAuthentication(null);
	        log.error("Failure in autoLogin", e);
	    }
	}
}
