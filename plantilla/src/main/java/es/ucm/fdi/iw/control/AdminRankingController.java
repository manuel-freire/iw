package es.ucm.fdi.iw.control;

import java.util.ArrayList;
import java.util.List;

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

import es.ucm.fdi.iw.model.StClass;
import es.ucm.fdi.iw.model.StTeam;
import es.ucm.fdi.iw.model.User;

/**
 * Admin-only controller
 * @author aitorcay
 */

@Controller()
@RequestMapping("admin/{id}/rankings")
public class AdminRankingController {
	
	private static final Logger log = LogManager.getLogger(AdminRankingController.class);
	
	@Autowired 
	private EntityManager entityManager;
	
	@Autowired
	private Environment env;
	
	/**
	 * Vista con los rankings asociados a cada clase creada por un profesor/a
	 * 
	 * @param id		id del usuario loggeado
	 * @param model		modelo que contendrá la información
	 * @param session	sesión asociada al usuario
	 * @return			vista a mostrar
	 */
	@GetMapping("")
	public String rankings(@PathVariable long id, Model model, HttpSession session) {
		User u = entityManager.find(User.class, id);
		model.addAttribute("user", u);
		
		//Lista de clases creadas por un profesor/a
		List<StClass> classList = entityManager.createNamedQuery("StClass.byTeacher", StClass.class)
				.setParameter("userId", u.getId()).getResultList();
		model.addAttribute("classList", classList);
		
		return "rankings";
	}
	
	/**
	 * Vista con el ranking global de las puntuaciones acumuladas por estudiantes y equipos
	 * 
	 * @param id		id del usuario loggeado
	 * @param classId	id de la clase
	 * @param model		modelo que contendrá la información
	 * @param session	sesión asociada al usuario
	 * @return			vista a mostrar
	 */
	@GetMapping("/{classId}")
	public String rankings(@PathVariable long id, @PathVariable("classId") long classId,
			Model model, HttpSession session) {
		User u = entityManager.find(User.class, id);
		model.addAttribute("user", u);
		
		//Lista de estudiantes pertenecientes a una clase
		List<User> rankingUser = entityManager.createNamedQuery("User.ranking", User.class)
				.setParameter("classId", classId).getResultList();
		model.addAttribute("rankingUser", rankingUser);
		
		int pos, max;
		//En caso de que varios estudiantes hayan logrado la misma puntuación compartirán la posición de la clasificación
		if(!rankingUser.isEmpty()) {
			List<Integer> positionUser = new ArrayList<>();
			pos = 1;
			max = rankingUser.get(0).getElo();
			positionUser.add(pos);
			for (int i=1; i < rankingUser.size(); i++) {
				if (rankingUser.get(i).getElo() < max) {
					pos++; max = rankingUser.get(i).getElo();
					positionUser.add(pos);
				} else {
					positionUser.add(pos);
				}
			}
			model.addAttribute("positionUser", positionUser);
		}
		
		//Lista de equipos asociados a una clase
		List<StTeam> rankingTeam = entityManager.createNamedQuery("StTeam.ranking", StTeam.class)
				.setParameter("classId", classId).getResultList();
		model.addAttribute("rankingTeam", rankingTeam);	

		//En caso de que varios equipos hayan logrado la misma puntuación compartirán la posición de la clasificación
		if(!rankingTeam.isEmpty()) {
			List<Integer> positionTeam = new ArrayList<>();
			pos = 1;
			max = rankingTeam.get(0).getElo();
			positionTeam.add(pos);
			for (int i=1; i < rankingTeam.size(); i++) {
				if (rankingTeam.get(i).getElo() < max) {
					pos++; max = rankingTeam.get(i).getElo();
					positionTeam.add(pos);
				} else {
					positionTeam.add(pos);
				}
			}
			model.addAttribute("positionTeam", positionTeam);	
		}
		
		return rankings(id, model, session);
	}
}
