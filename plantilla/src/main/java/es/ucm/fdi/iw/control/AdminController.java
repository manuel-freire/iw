package es.ucm.fdi.iw.control;


import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import es.ucm.fdi.iw.model.User;

/**
 * Admin-only controller
 * @author aitorcay
 */

@Controller()
@RequestMapping("admin")
public class AdminController {
	
	private static final Logger log = LogManager.getLogger(AdminController.class);
	
	@Autowired 
	private EntityManager entityManager;
	
	@Autowired
	private Environment env;

	/**
	 * Vista por defecto
	 * 
	 * @param model	modelo que contendrá la información
	 * @return vista a mostrar
	 */
	@GetMapping("/")
	public String index(Model model) {
		model.addAttribute("activeProfiles", env.getActiveProfiles());
		model.addAttribute("basePath", env.getProperty("es.ucm.fdi.base-path"));
		
		return "admin";
	}
	
	/**
	 * Vista del perfil del usuario
	 * 
	 * @param id		id del usuario loggeado
	 * @param model		modelo que contendrá la información
	 * @param session	sesión asociada al usuario
	 * @return			vista a mostrar
	 */
	@GetMapping("/{id}")
	public String getUser(@PathVariable long id, Model model, HttpSession session) {
		
		// ANTES User u = entityManager.find(User.class, id);
		User u = entityManager.find(User.class, ((User)session.getAttribute("u")).getId());

		model.addAttribute("user", u);
		return "admin";
	}
	
	/**
	 * Vista de error
	 * 
	 * @param model	modelo que contendrá la información
	 * @return		vista informando del error
	 */
	@GetMapping("/error")
	public String error(Model model) {
		return "error";
	}
}
