package es.ucm.fdi.iw.control;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import es.ucm.fdi.iw.model.Achievement;
import es.ucm.fdi.iw.model.Answer;
import es.ucm.fdi.iw.model.Contest;
import es.ucm.fdi.iw.model.Question;
import es.ucm.fdi.iw.model.Result;
import es.ucm.fdi.iw.model.StClass;
import es.ucm.fdi.iw.model.StTeam;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.model.User.Role;

/**
 * Admin-only controller
 * @author aitorcay
 */

@Controller()
@RequestMapping("admin/{id}/contest")
public class AdminContestController {
	
	private static final Logger log = LogManager.getLogger(AdminContestController.class);
	
	@Autowired 
	private EntityManager entityManager;
	
	@Autowired
	private Environment env;
	
	/**
	 * Vista con las pruebas creadas por un profesor/a
	 * 
	 * @param id		id del usuario loggeado
	 * @param model		modelo que contendrá la información
	 * @param session	sesión asociada al usuario
	 * @return			vista a mostrar
	 */
	@GetMapping("")
	public String contest(@PathVariable long id, Model model, HttpSession session) {
		User u = entityManager.find(User.class, id);
		model.addAttribute("user", u);
		
		//Lista de pruebas creadas por un profesor/a
		List<Contest> contestList = entityManager.createNamedQuery("Contest.byTeacher", Contest.class)
				.setParameter("userId", u.getId()).getResultList();
		model.addAttribute("contestList", contestList);
		
		return "contest";
	}
	
	/**
	 * Vista con la información de una prueba concreta: preguntas, respuestas, puntuaciones asociadas y resultados de la clase
	 * 
	 * @param id		id del usuario loggeado
	 * @param contestId	id de la prueba
	 * @param model		modelo que contendrá la información
	 * @param session	sesión asociada al usuario
	 * @return			vista a mostrar
	 */
	@GetMapping("/{contestId}")
	public String selectedContest(@PathVariable("id") long id, @PathVariable("contestId") long contestId,
			Model model, HttpSession session) {
		User target = entityManager.find(User.class, id);
		model.addAttribute("user", target);
		
		//Lista de pruebas creadas por un profesor/a
		List<Contest> contestList = entityManager.createNamedQuery("Contest.byTeacher", Contest.class)
				.setParameter("userId", id).getResultList();
		model.addAttribute("contestList", contestList);
		//Prueba a consultar
		Contest contest = entityManager.find(Contest.class, contestId);
		model.addAttribute("contest", contest);
		//Lista de los resultados de los participantes
		List<Result> resultList = entityManager.createNamedQuery("Result.byContest", Result.class)
				.setParameter("contestId", contestId).getResultList();
		model.addAttribute("resultList", resultList);
		//Clase a la que está asociada la prueba
		StClass stClass = entityManager.createNamedQuery("StClass.contestOwner", StClass.class)
				.setParameter("contestId", contestId).getSingleResult();
		//Lista de estudiantes pertenecientes a la clase
		List<User> students = entityManager.createNamedQuery("User.byClass", User.class)
				.setParameter("classId", stClass.getId()).getResultList();
		model.addAttribute("students", students);
		//Generación de las estadísticas asociadas a cada pregunta
		model.addAttribute("stats", getContestStats(contest));
		
		return contest(id, model, session);
	}
	
	/**
	 * Vista con la clasificación de los alumnos y equipos participantes en una prueba
	 * 
	 * @param id		id del usuario loggeado
	 * @param contestId	id de la prueba
	 * @param model		modelo que contendrá la información
	 * @param session	sesión asociada al usuario
	 * @return			vista a mostrar
	 */
	@GetMapping("/{contestId}/ranking")
	@Transactional
	public String contestRanking(@PathVariable("id") long id, @PathVariable("contestId") long contestId,
			Model model, HttpSession session) {
		User u = entityManager.find(User.class, id);
		model.addAttribute("user", u);
		//Lista de pruebas creadas por un profesor/a
		List<Contest> contestList = entityManager.createNamedQuery("Contest.byTeacher", Contest.class)
				.setParameter("userId", id).getResultList();
		model.addAttribute("contestList", contestList);
		//Prueba a consultar
		Contest contest = entityManager.find(Contest.class, contestId);
		model.addAttribute("contest", contest);
		//Lista de resultados obtenidos en la prueba
		List<Result> results = entityManager.createNamedQuery("Result.userRanking", Result.class)
				.setParameter("contestId", contestId).getResultList();
		model.addAttribute("results", results);
		//Lista de equipos participantes en la prueba
		List<StTeam> teams = entityManager.createNamedQuery("StClass.contestTeams", StTeam.class)
				.setParameter("contestId", contestId).getResultList();
		//Generación de las clasificaciones de estudiantes y equipos
		getContestRanking(teams, results, contest, model);
		
		return "rankContest";
	}
	
	/**
	 * Gestiona la visibilidad/disponibilidad de una prueba
	 * 
	 * @param response		para gestión de las peticiones HTTP
	 * @param id			id del usuario loggeado
	 * @param contestId		id de la prueba
	 * @param model			modelo que contendrá la información
	 * @param session		sesión asociada al usuario
	 * @return				vista a mostrar
	 * @throws IOException
	 */
	@PostMapping("/{contestId}//toggleContest")
	@Transactional
	public String toggleContest(
			HttpServletResponse response,
			@PathVariable("id") long id,
			@PathVariable("contestId") long contestId,
			Model model, HttpSession session) throws IOException {
		User target = entityManager.find(User.class, id);
		model.addAttribute("user", target);
		
		//Lista de pruebas creadas por un profesor/a
		List<Contest> contestList = entityManager.createNamedQuery("Contest.byTeacher", Contest.class)
				.setParameter("userId", id).getResultList();
		model.addAttribute("contestList", contestList);
		//Prueba a modificar
		Contest contest = entityManager.find(Contest.class, contestId);

		//Comprobación de permisos
		User requester = (User)session.getAttribute("u");
		if (requester.getId() != target.getId() &&	! requester.hasRole(Role.ADMIN)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "No eres profesor, y éste no es tu perfil");
			return selectedContest(id, contestId, model, session);
		}
		
		//Alterna la disponibilidad de la prueba
		if (contest.getEnabled() == 1) {
			contest.setEnabled((byte)0); 
		} else {
			contest.setEnabled((byte)1);
			if (contest.getStartDate() == null) {
				contest.setStartDate(new Date());
			}
		}
		
		model.addAttribute("contest", contest);
		model.addAttribute("startDate", contest.getStartDate());
		
		return selectedContest(id, contestId, model, session);
	}	
	
	/**
	 * Finaliza una prueba
	 * 
	 * @param response		para gestión de las peticiones HTTP
	 * @param id			id del usuario loggeado
	 * @param contestId		id de la prueba
	 * @param model			modelo que contendrá la información
	 * @param session		sesión asociada al usuario
	 * @return
	 * @throws IOException
	 */
	@PostMapping("/{contestId}//completeContest")
	@Transactional
	public String completeContest(
			HttpServletResponse response,
			@PathVariable("id") long id,
			@PathVariable("contestId") long contestId,
			Model model, HttpSession session) throws IOException {
		User target = entityManager.find(User.class, id);
		model.addAttribute("user", target);

		//Comprobación de permisos
		User requester = (User)session.getAttribute("u");
		if (requester.getId() != target.getId() &&	! requester.hasRole(Role.ADMIN)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "No eres profesor, y éste no es tu perfil");
			return selectedContest(id, contestId, model, session);
		}
		
		Contest contest = entityManager.find(Contest.class, contestId);
		contest.setComplete((byte)1);		
		if (contest.getEndDate() == null) {
			contest.setEndDate(new Date());
		}
		model.addAttribute("contest", contest);
		
		return contestRanking(id, contestId, model, session);
	}	
	
	/**
	 * Genera las estadísticas para las preguntas de una prueba. Para ello se realiza el recuento de respuestas
	 * para cada opción en cada pregunta
	 * 
	 * @param contest	prueba a procesar
	 * @return			estadísticas de las preguntas
	 */
	private List<String> getContestStats(Contest contest) {
		List<String> contestStats = new ArrayList<>();
		StringBuilder sb;
		Answer a;
		Long count;
		
		for(Question q : contest.getQuestions()) {
			sb = new StringBuilder();
			sb.append(q.getText() + "|");
			for(int i = 0; i < q.getAnswers().size()-1; i++) {
				a = q.getAnswers().get(i);
				count = (Long)entityManager.createNamedQuery("Result.numAnswers")
				.setParameter("answerId", a.getId())
				.setParameter("contestId", contest.getId()).getSingleResult();
				sb.append(a.getText() + "-->" + count + ";");
			}
			a = q.getAnswers().get(q.getAnswers().size()-1);
			count = (Long)entityManager.createNamedQuery("Result.numAnswers")
			.setParameter("answerId", a.getId())
			.setParameter("contestId", contest.getId()).getSingleResult();
			sb.append(a.getText() + "-->" + Long.toString(count));
			
			contestStats.add(sb.toString());
		}
		
		return contestStats;		
	}
	
	/**
	 * Genera la clasificación de estudiantes y equipos para una prueba finalizada
	 * 
	 * @param teams		lista de equipos
	 * @param results	lista de los resultados de los estudiantes
	 * @param model		modelo que contendrá la información
	 */
	@SuppressWarnings("unchecked")
	private void getContestRanking(List<StTeam> teams, List<Result> results, Contest contest, Model model) {
		
		Map<StTeam, Double> sumScores = new HashMap<>();	
		for (StTeam s : teams) {
			sumScores.put(s, 0.0);
		}
		
		List<Integer> positionUser = new ArrayList<>();
		StTeam team;
		int pos = 1;
		double score;
		double max;
		if (results != null && !results.isEmpty()) {
			max = results.get(0).getScore();
			//En caso de que varios estudiantes hayan logrado la misma puntuación compartirán la posición de la clasificación
			for (int i=0; i < results.size(); i++) {
				score = results.get(i).getScore();
				
				if ( score < max) {
					pos++; max = score;
					positionUser.add(pos);
				} else {
					positionUser.add(pos);
				}
				
				team = results.get(i).getUser().getTeam();
				if (sumScores.keySet().contains(team))
					sumScores.put(team, sumScores.get(team) + score);
				else 
					sumScores.put(team, score);
			}
	
			model.addAttribute("positionUser", positionUser);
			if (contest.getChecked() == 0) {
				updateAchievementsUser(results, positionUser);
			}	
		}
			
		if (sumScores != null && !sumScores.isEmpty()) {
			LinkedHashMap<StTeam, Double> sortedTeams = new LinkedHashMap<>();
			sumScores.entrySet()
		    .stream()
		    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())) 
		    .forEachOrdered(x -> sortedTeams.put(x.getKey(), x.getValue()));
			
			//En caso de que varios equipos hayan logrado la misma puntuación compartirán la posición de la clasificación
			List<Integer> positionTeam = new ArrayList<>();
			pos = 1;
			max = (double) sortedTeams.values().toArray()[0];
			for (int k=0; k < sortedTeams.values().size(); k++) {
				score = (double) sortedTeams.values().toArray()[k];
				if ( score < max) {
					pos++; max = score;
					positionTeam.add(pos);
				} else {
					positionTeam.add(pos);
				}
			}
			
	        if (contest.getChecked() == 0) {
				contest.setChecked((byte) 1);
				updateAchievementsTeam((List<StTeam>)(Object)Arrays.asList(sortedTeams.keySet().toArray()), positionTeam);
			}
	        
			model.addAttribute("rankingTeam", Arrays.asList(sortedTeams.keySet().toArray()));
			model.addAttribute("scoreTeam", Arrays.asList(sortedTeams.values().toArray()));
			model.addAttribute("positionTeam", positionTeam);		
		}			
	}
	
	/**
	 * Actualiza los logros de los estudiantes en función de su posición en la clasificación
	 * 
	 * @param results		lista con los resultados de los estudiantes
	 * @param positionUser	lista con las posiciones en la clasificación
	 */
	private void updateAchievementsUser(List<Result> results, List<Integer> positionUser) {
		User u;
		Achievement a;
		String[] levels;
		
		for (int i = 0; i < results.size() && positionUser.get(i) < 4; i++) {
			u = entityManager.find(User.class, results.get(i).getUser().getId());
			u.setTop(u.getTop()+1);
			
			a = entityManager.createNamedQuery("Achievement.byStudentRanking", Achievement.class)
					.setParameter("userId", u.getId()).getSingleResult();
			a.setProgress(u.getTop());

			levels = a.getGoal().getLevels().split(",");
			if (u.getTop() >= Integer.parseInt(levels[a.getLevel()])) {
				a.setLevel(a.getLevel() + 1);
			}	
		}
	}
	
	/**
	 * Actualiza los logros de los equipos en función de su posición en la clasificación
	 * 
	 * @param teams			lista con los equipos participantes
	 * @param positionTeam	lista con las posiciones en la clasificación
	 */
	private void updateAchievementsTeam(List<StTeam> teams, List<Integer> positionTeam) {
		StTeam t;
		Achievement a;
		String[] levels;
		int trophies;
		
		for (int i = 0; i < teams.size()  && positionTeam.get(i) < 4; i++) {
			t = entityManager.find(StTeam.class, teams.get(i).getId());
			switch(positionTeam.get(i)) {
				case(1):
					t.setGold(t.getGold()+1);
					break;
				case(2):
					t.setSilver(t.getSilver()+1);
					break;
				case(3):
					t.setBronze(t.getBronze()+1);
					break;
				default:
					break;
			}

			a = entityManager.createNamedQuery("Achievement.byTeamRanking", Achievement.class)
					.setParameter("teamId", t.getId()).getSingleResult();
			trophies = t.getBronze()+t.getSilver()+t.getGold();
			a.setProgress(trophies);

			levels = a.getGoal().getLevels().split(",");
			if (trophies >= Integer.parseInt(levels[a.getLevel()])) {
				a.setLevel(a.getLevel() + 1);
			}

			entityManager.persist(t);
			entityManager.persist(a);
		}
	}
}
