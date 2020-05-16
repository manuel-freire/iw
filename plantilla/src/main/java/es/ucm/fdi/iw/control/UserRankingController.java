package es.ucm.fdi.iw.control;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import es.ucm.fdi.iw.model.Contest;
import es.ucm.fdi.iw.model.Result;
import es.ucm.fdi.iw.model.StClass;
import es.ucm.fdi.iw.model.StTeam;
import es.ucm.fdi.iw.model.User;

/**
 * User-administration controller
 * 
 * @author aitorcay
 */
@Controller()
@RequestMapping("user/{id}/rankings/{classId}")
public class UserRankingController {
	
	private static final Logger log = LogManager.getLogger(UserRankingController.class);
	
	@Autowired 
	private EntityManager entityManager;
	
	/**
	 * Vista con el ranking de las puntuaciones acumuladas por los estudiantes y equipos de una clase
	 * 
	 * @param id		id del usuario loggeado
	 * @param classId	id de la clase
	 * @param model		modelo que contendrá la información
	 * @param session	sesión asociada al usuario
	 * @return			vista a mostrar
	 */
	@GetMapping("")
	public String rankings(@PathVariable long id, @PathVariable("classId") long classId,
			Model model, HttpSession session) {
		User u = entityManager.find(User.class, id);
		model.addAttribute("user", u);
		StClass stc = entityManager.find(StClass.class, classId);
		model.addAttribute("stClass", stc);
		//Lista de pruebas finalizadas asociadas a la clase
		List<Contest> contestList = entityManager.createNamedQuery("Contest.byClassComplete", Contest.class)
				.setParameter("classId", classId).getResultList();
		model.addAttribute("contestList", contestList);
		//Lista de estudiantes de la clase
		List<User> rankingUser = entityManager.createNamedQuery("User.ranking", User.class)
				.setParameter("classId", classId).getResultList();
		model.addAttribute("rankingUser", rankingUser);
		//En caso de que varios estudiantes hayan logrado la misma puntuación compartirán la posición de la clasificación
		List<Integer> positionUser = new ArrayList<>();
		int pos = 1;
		int max = rankingUser.get(0).getElo();
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
		List<StTeam> rankingTeam = entityManager.createNamedQuery("StTeam.ranking", StTeam.class)
				.setParameter("classId", classId).getResultList();
		model.addAttribute("rankingTeam", rankingTeam);	

		//En caso de que varios equipos hayan logrado la misma puntuación compartirán la posición de la clasificación
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
		
		return "rankings";
	}	
	
	/**
	 * Vista con la clasificación de los alumnos y equipos participantes en una prueba
	 * 
	 * @param id		id del usuario loggeado
	 * @param classId
	 * @param contestId	id de la prueba
	 * @param model		modelo que contendrá la información
	 * @param session	sesión asociada al usuario
	 * @return			vista a mostrar
	 */
	@GetMapping("/{contestId}")
	public String rankContest(@PathVariable long id, @PathVariable("classId") long classId, @PathVariable("contestId") long contestId,
			Model model, HttpSession session) {
		User u = entityManager.find(User.class, id);
		model.addAttribute("user", u);
		StClass stc = entityManager.find(StClass.class, classId);
		model.addAttribute("stClass", stc);
		Contest contest = entityManager.find(Contest.class, contestId);
		model.addAttribute("contest", contest);
		
		//Lita de concursos asociados a la clase
		List<Contest> contestList = entityManager.createNamedQuery("Contest.byClassComplete", Contest.class)
				.setParameter("classId", classId).getResultList();
		model.addAttribute("contestList", contestList);
		//Lista de resultados de los estudiantes
		List<Result> results = entityManager.createNamedQuery("Result.userRanking", Result.class)
				.setParameter("contestId", contestId).getResultList();
		model.addAttribute("results", results);
		//Lista de los equipos participantes
		List<StTeam> teams = entityManager.createNamedQuery("StClass.contestTeams", StTeam.class)
				.setParameter("contestId", contestId).getResultList();
		//Generación de la clasificación de la prueba
		getContestRanking(teams, results, model);	
		
		return "rankContest";
	}
	
	/**
	 * Genera la clasificación de estudiantes y equipos para una prueba finalizada
	 * 
	 * @param teams		lista de equipos
	 * @param results	lista de los resultados de los estudiantes
	 * @param model		modelo que contendrá la información
	 */
	private void getContestRanking(List<StTeam> teams, List<Result> results, Model model) {
		
		Map<StTeam, Double> sumScores = new HashMap<>();	
		for (StTeam s : teams) {
			sumScores.put(s, 0.0);
		}
		
		List<Integer> positionUser = new ArrayList<>();
		StTeam team;
		int pos = 1;
		double score;
		double max = results.get(0).getScore();
		positionUser.add(pos);
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
			sumScores.put(team, sumScores.get(team) + score);
		}

		model.addAttribute("positionUser", positionUser);		
		
		LinkedHashMap<StTeam, Double> sortedTeams = new LinkedHashMap<>();
		sumScores.entrySet()
	    .stream()
	    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())) 
	    .forEachOrdered(x -> sortedTeams.put(x.getKey(), x.getValue()));

		//En caso de que varios equipos hayan logrado la misma puntuación compartirán la posición de la clasificación
		List<Integer> positionTeam = new ArrayList<>();
		pos = 1;
		max = (double) sortedTeams.values().toArray()[0];
		positionTeam.add(pos);
		for (int k=0; k < sortedTeams.values().size(); k++) {
			score = (double) sortedTeams.values().toArray()[k];
			if ( score < max) {
				pos++; max = score;
				positionTeam.add(pos);
			} else {
				positionTeam.add(pos);
			}
		}
        
		model.addAttribute("rankingTeam", Arrays.asList(sortedTeams.keySet().toArray()));
		model.addAttribute("scoreTeam", Arrays.asList(sortedTeams.values().toArray()));
		model.addAttribute("positionTeam", positionTeam);	
	}
}