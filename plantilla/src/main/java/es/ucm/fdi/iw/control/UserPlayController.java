package es.ucm.fdi.iw.control;

import java.io.IOException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.itextpdf.text.DocumentException;

import es.ucm.fdi.iw.model.Achievement;
import es.ucm.fdi.iw.model.Contest;
import es.ucm.fdi.iw.model.Result;
import es.ucm.fdi.iw.model.StClass;
import es.ucm.fdi.iw.model.StTeam;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.model.User.Role;
import es.ucm.fdi.iw.utils.AutoCorrector;

/**
 * User-administration controller
 * 
 * @author aitorcay
 */
@Controller()
@RequestMapping("user/{id}/play/{classId}")
public class UserPlayController {
	
	private static final Logger log = LogManager.getLogger(UserPlayController.class);
	
	@Autowired 
	private EntityManager entityManager;
	
	/**
	 * Vista para resolver las pruebas asociadas a una clase
	 * 
	 * @param id		id del usuario loggeado
	 * @param classId	id de la clase
	 * @param model		modelo que contendrá la información
	 * @param session	sesión asociada al usuario
	 * @return			vista a mostrar
	 */
	@GetMapping("")
	public String play(@PathVariable("id") long id, @PathVariable("classId") long classId,
			Model model, HttpSession session) {
		User u = entityManager.find(User.class, id);
		model.addAttribute("user", u);
		StClass stc = entityManager.find(StClass.class, classId);
		model.addAttribute("stClass", stc);
		
		//Lista de pruebas asociadas a una clase
		List<Contest> contestList = entityManager.createNamedQuery("Contest.byClassUser", Contest.class)
				.setParameter("classId", classId).getResultList();
		model.addAttribute("contestList", contestList);		
		
		return "play";
	}	
	
	/**
	 * Vista para la resolución de una prueba concreta
	 * 
	 * @param id		id del usuario loggeado
	 * @param classId	id de la clase
	 * @param contestId id de la prueba
	 * @param model		modelo que contendrá la información
	 * @param session	sesión asociada al usuario
	 * @return			vista a mostrar
	 */
	@GetMapping("/{contestId}")
	public String playContest(@PathVariable("id") long id, @PathVariable("classId") long classId, @PathVariable("contestId") long contestId,
			Model model, HttpSession session) {
		User u = entityManager.find(User.class, id);
		model.addAttribute("user", u);
		StClass stc = entityManager.find(StClass.class, classId);
		model.addAttribute("stClass", stc);		
		Contest contest = entityManager.find(Contest.class, contestId);
		model.addAttribute("contest", contest);
		
		//Lista de concursos asociados a una clase
		List<Contest> contestList = entityManager.createNamedQuery("Contest.byClassUser", Contest.class)
				.setParameter("classId", classId).getResultList();
		model.addAttribute("contestList", contestList);
		//Si la prueba ya ha sido resuelta se mostrarán los resultados
		Long solved = (Long)entityManager.createNamedQuery("Result.hasAnswer")
				.setParameter("userId", id)
				.setParameter("contestId", contestId).getSingleResult();
		if (solved > 0) {
			Result result = entityManager.createNamedQuery("Result.getResult", Result.class)
					.setParameter("userId", id)
					.setParameter("contestId", contestId).getSingleResult();
			model.addAttribute("result", result);
		}		
		
		return play(id, classId, model, session);
	}
	
	/**
	 * Obtiene los resultados de un estudiante en una prueba
	 * 
	 * @param response		para gestión de las peticiones HTTP
	 * @param answerList	lista con las respuestas de la prueba
	 * @param id			id del usuario loggeado
	 * @param classId		id de la clase
	 * @param contestId		id de la prueba
	 * @param model			modelo que contendrá la información
	 * @param session		sesión asociada al usuario
	 * @return				vista a mostrar
	 * @throws IOException
	 * @throws DocumentException
	 */
	@PostMapping("/{contestId}/results")
	@Transactional
	public String getResults(
			HttpServletResponse response,
			@RequestParam("results") List<String> answerList,
			@PathVariable("id") long id,
			@PathVariable("classId") long classId,
			@PathVariable("contestId") long contestId,
			Model model, HttpSession session) throws IOException, DocumentException {
		User target = entityManager.find(User.class, id);
		model.addAttribute("user", target);
		
		//Comprobación de permisos
		User requester = (User)session.getAttribute("u");
		if (requester.getId() != target.getId() &&	! requester.hasRole(Role.ADMIN)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "No eres profesor, y éste no es tu perfil");
			return playContest(id, classId, contestId, model, session);
		}
		
		if (answerList == null || answerList.isEmpty()) {
			log.info("No se han creado equipos o ningún alumno ha sido asignado");
		} else {		
			Contest contest = entityManager.find(Contest.class, contestId);
			StTeam team = entityManager.find(StTeam.class, target.getTeam().getId());
			
			List<Achievement> achievementsU = entityManager.createNamedQuery("Achievement.byStudent", Achievement.class)
					.setParameter("userId", target.getId()).getResultList();
			List<Achievement> achievementsT = entityManager.createNamedQuery("Achievement.byTeam", Achievement.class)
					.setParameter("teamId", team.getId()).getResultList();
			
			//Corrección de las respuestas y actualización de los logros en función de los resultados
			Result result = AutoCorrector.correction(target, team, contest, answerList);
			achievementsU = AutoCorrector.updateAchievementsUser(achievementsU, target);
			achievementsT = AutoCorrector.updateAchievementsTeam(achievementsT, team);
			target.setAchievementUser(achievementsU);
			team.setAchievementTeam(achievementsT);
			
			for(Achievement aU : achievementsU)
				entityManager.persist(aU);
			for(Achievement aT : achievementsT)
				entityManager.persist(aT);

			entityManager.persist(result);
			entityManager.persist(target);
			entityManager.persist(team);
			
			model.addAttribute("result", result);
		}	
		
		return playContest(id, classId, contestId, model, session);
	}
}