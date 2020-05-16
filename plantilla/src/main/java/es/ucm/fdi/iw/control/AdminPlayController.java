package es.ucm.fdi.iw.control;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.itextpdf.text.DocumentException;

import es.ucm.fdi.iw.LocalData;
import es.ucm.fdi.iw.constants.ConstantsFromFile;
import es.ucm.fdi.iw.model.Achievement;
import es.ucm.fdi.iw.model.Answer;
import es.ucm.fdi.iw.model.Contest;
import es.ucm.fdi.iw.model.Goal;
import es.ucm.fdi.iw.model.Question;
import es.ucm.fdi.iw.model.Result;
import es.ucm.fdi.iw.model.StClass;
import es.ucm.fdi.iw.model.StTeam;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.model.User.Role;
import es.ucm.fdi.iw.utils.AutoCorrector;
import es.ucm.fdi.iw.utils.ClassFileReader;
import es.ucm.fdi.iw.utils.ContestFileReader;
import es.ucm.fdi.iw.utils.PdfGenerator;

/**
 * Admin-only controller
 * @author aitorcay
 */

@Controller()
@RequestMapping("admin/{id}/play")
public class AdminPlayController {
	
	private static final Logger log = LogManager.getLogger(AdminPlayController.class);
	
	@Autowired 
	private EntityManager entityManager;
	
	@Autowired
	private Environment env;
	
	/**
	 * Vista para testear las pruebas creadas 
	 * 
	 * @param id		id del usuario loggeado
	 * @param model		modelo que contendrá la información
	 * @param session	sesión asociada al usuario
	 * @return			vista a mostrar
	 */
	@GetMapping("")
	public String play(@PathVariable long id, Model model, HttpSession session) {
		User u = entityManager.find(User.class, id);
		model.addAttribute("user", u);
		
		//Lista de pruebas creadas por un profesor/a
		List<Contest> contestList = entityManager.createNamedQuery("Contest.byTeacher", Contest.class)
				.setParameter("userId", u.getId()).getResultList();
		model.addAttribute("contestList", contestList);
		
		return "play";
	}	
	
	/**
	 * Vista para resolver una prueba
	 * 
	 * @param id		id del usuario loggeado
	 * @param contestId	id de la prueba
	 * @param model		modelo que contendrá la información
	 * @param session	sesión asociada al usuario
	 * @return			vista a mostrar
	 */
	@GetMapping("/{contestId}")
	public String playContest(@PathVariable("id") long id, @PathVariable("contestId") long contestId,
			Model model, HttpSession session) {
		User u = entityManager.find(User.class, id);
		model.addAttribute("user", u);
		
		//Lista de pruebas creadas por un profesor/a
		List<Contest> contestList = entityManager.createNamedQuery("Contest.byTeacher", Contest.class)
				.setParameter("userId", id).getResultList();
		model.addAttribute("contestList", contestList);
		//Prueba a resolver
		Contest contest = entityManager.find(Contest.class, contestId);
		model.addAttribute("contest", contest);
		//En caso de que la prueba ya haya sido resuelta se mostrarán los resultados
		Long solved = (Long)entityManager.createNamedQuery("Result.hasAnswer")
				.setParameter("userId", id)
				.setParameter("contestId", contestId).getSingleResult();
		if (solved > 0) {
			Result result = entityManager.createNamedQuery("Result.getResult", Result.class)
					.setParameter("userId", id)
					.setParameter("contestId", contestId).getSingleResult();
			model.addAttribute("result", result);
		}
				
		return "play";
	}
	
	/**
	 * Obtiene los resultados correspondientes a una prueba
	 * 
	 * @param response		para gestión de las peticiones HTTP
	 * @param answerList	lista con las respuestas de la prueba
	 * @param id			id del usuario loggeado
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
			@PathVariable("contestId") long contestId,
			Model model, HttpSession session) throws IOException, DocumentException {
		User target = entityManager.find(User.class, id);
		model.addAttribute("user", target);
		
		//Comprobación de permisos
		User requester = (User)session.getAttribute("u");
		if (requester.getId() != target.getId() &&	! requester.hasRole(Role.ADMIN)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "No eres profesor, y éste no es tu perfil");
			return playContest(id, contestId, model, session);
		}
		
		//Corrección de la prueba
		if (answerList == null || answerList.isEmpty()) {
			log.info("No se han creado equipos o ningún alumno ha sido asignado");
		} else {		
			Contest contest = entityManager.find(Contest.class, contestId);
			Result result = AutoCorrector.correction(target, target.getTeam(), contest, answerList);
			entityManager.persist(result);
			model.addAttribute("result", result);
		}	

		return playContest(id, contestId, model, session);
	}
	
	/**
	 * Elimina los resultados ya existentes para resolver de nuevo una prueba
	 * 
	 * @param response		para gestión de las peticiones HTTP
	 * @param id			id del usuario loggeado
	 * @param contestId		id de la prueba
	 * @param model			modelo que contendrá la información
	 * @param session		sesión asociada al usuario
	 * @return				vista a mostrar
	 * @throws IOException
	 * @throws DocumentException
	 */
	@PostMapping("/{contestId}/retry")
	@Transactional
	public String retryContest(
			HttpServletResponse response,
			@PathVariable("id") long id,
			@PathVariable("contestId") long contestId,
			Model model, HttpSession session) throws IOException, DocumentException {
		User target = entityManager.find(User.class, id);
		model.addAttribute("user", target);
		
		//Comprobación de permisos
		User requester = (User)session.getAttribute("u");
		if (requester.getId() != target.getId() &&	! requester.hasRole(Role.ADMIN)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "No eres profesor, y éste no es tu perfil");
			return playContest(id, contestId, model, session);
		}
				
		Result result = entityManager.createNamedQuery("Result.getResult", Result.class)
		.setParameter("userId", id)
		.setParameter("contestId", contestId).getSingleResult();	
		entityManager.remove(result);
		
		return playContest(id, contestId, model, session);
	}
}
