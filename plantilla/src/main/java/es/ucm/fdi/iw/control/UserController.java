package es.ucm.fdi.iw.control;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.itextpdf.text.DocumentException;

import es.ucm.fdi.iw.LocalData;
import es.ucm.fdi.iw.model.Achievement;
import es.ucm.fdi.iw.model.Answer;
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
 * @author mfreire
 */
@Controller()
@RequestMapping("user")
public class UserController {
	
	private static final Logger log = LogManager.getLogger(UserController.class);
	
	@Autowired 
	private EntityManager entityManager;
	
	@Autowired
	private LocalData localData;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@GetMapping("/{id}")
	public String getUser(@PathVariable long id, Model model, HttpSession session) {
		User u = entityManager.find(User.class, id);
		model.addAttribute("user", u);
		
		List<Achievement> achievements = entityManager.createNamedQuery("Achievement.byStudent", Achievement.class)
				.setParameter("userId", u.getId()).getResultList();
		model.addAttribute("achievements", achievements);
		
		return "profile";
	}

	@GetMapping("/{id}/team")
	public String team(@PathVariable long id, Model model, HttpSession session) {
		User u = entityManager.find(User.class, id);
		model.addAttribute("user", u);
		
		StTeam team = entityManager.find(StTeam.class, u.getTeam().getId());
		model.addAttribute("team", team);
		
		List<User> members = entityManager.createNamedQuery("User.byTeam", User.class)
				.setParameter("teamId", u.getTeam().getId()).getResultList();
		model.addAttribute("members", members);
		
		List<Achievement> achievements = entityManager.createNamedQuery("Achievement.byTeam", Achievement.class)
				.setParameter("teamId", u.getTeam().getId()).getResultList();
		model.addAttribute("achievements", achievements);
		
		return "team";
	}
	
	@GetMapping("/{id}/rankings/{classId}")
	public String rankings(@PathVariable long id, @PathVariable("classId") long classId,
			Model model, HttpSession session) {
		User u = entityManager.find(User.class, id);
		model.addAttribute("user", u);
		
		StClass stc = entityManager.find(StClass.class, classId);
		model.addAttribute("stClass", stc);
		
		List<Contest> contestList = entityManager.createNamedQuery("Contest.byClassComplete", Contest.class)
				.setParameter("classId", classId).getResultList();
		model.addAttribute("contestList", contestList);
		
		List<User> rankingUser = entityManager.createNamedQuery("User.ranking", User.class)
				.setParameter("classId", classId).getResultList();
		model.addAttribute("rankingUser", rankingUser);
		
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
	
	@GetMapping("/{id}/rankings/{classId}/{contestId}")
	public String rankContest(@PathVariable long id, @PathVariable("classId") long classId, @PathVariable("contestId") long contestId,
			Model model, HttpSession session) {
		User u = entityManager.find(User.class, id);
		model.addAttribute("user", u);
		
		StClass stc = entityManager.find(StClass.class, classId);
		model.addAttribute("stClass", stc);
		
		List<Contest> contestList = entityManager.createNamedQuery("Contest.byClassComplete", Contest.class)
				.setParameter("classId", classId).getResultList();
		model.addAttribute("contestList", contestList);
		
		Contest contest = entityManager.find(Contest.class, contestId);
		model.addAttribute("contest", contest);
		
		List<Result> results = entityManager.createNamedQuery("Result.userRanking", Result.class)
				.setParameter("contestId", contestId).getResultList();
		model.addAttribute("results", results);

		List<StTeam> teams = entityManager.createNamedQuery("StClass.contestTeams", StTeam.class)
				.setParameter("contestId", contestId).getResultList();
		
		getContestRanking(teams, results, model);	
		
		return "rankContest";
	}	
	
	@GetMapping("/{id}/play/{classId}")
	public String play(@PathVariable("id") long id, @PathVariable("classId") long classId,
			Model model, HttpSession session) {
		User u = entityManager.find(User.class, id);
		model.addAttribute("user", u);
		
		StClass stc = entityManager.find(StClass.class, classId);
		model.addAttribute("stClass", stc);
		
		List<Contest> contestList = entityManager.createNamedQuery("Contest.byClassUser", Contest.class)
				.setParameter("classId", classId).getResultList();
		model.addAttribute("contestList", contestList);		
		
		return "play";
	}	
	
	@GetMapping("/{id}/play/{classId}/{contestId}")
	public String playContest(@PathVariable("id") long id, @PathVariable("classId") long classId, @PathVariable("contestId") long contestId,
			Model model, HttpSession session) {
		User u = entityManager.find(User.class, id);
		model.addAttribute("user", u);
		
		StClass stc = entityManager.find(StClass.class, classId);
		model.addAttribute("stClass", stc);
		
		List<Contest> contestList = entityManager.createNamedQuery("Contest.byClassUser", Contest.class)
				.setParameter("classId", classId).getResultList();
		model.addAttribute("contestList", contestList);
		
		Contest contest = entityManager.find(Contest.class, contestId);
		model.addAttribute("contest", contest);
		
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

	@PostMapping("/{id}")
	@Transactional
	public String postUser(
			HttpServletResponse response,
			@PathVariable long id, 
			@ModelAttribute User edited, 
			@RequestParam(required=false) String pass2,
			Model model, HttpSession session) throws IOException {
		User target = entityManager.find(User.class, id);
		model.addAttribute("user", target);
		
		User requester = (User)session.getAttribute("u");
		if (requester.getId() != target.getId() &&
				! requester.hasRole(Role.ADMIN)) {			
			response.sendError(HttpServletResponse.SC_FORBIDDEN, 
					"No eres administrador, y éste no es tu perfil");
		}
		
		if (edited.getPassword() != null && edited.getPassword().equals(pass2)) {
			// save encoded version of password
			target.setPassword(passwordEncoder.encode(edited.getPassword()));
		}		
		target.setUsername(edited.getUsername());
		return "profile";
	}	
	
	@PostMapping("/{id}/play/{classId}/{contestId}/results")
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
		
		// check permissions
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
	
	@GetMapping(value="/{id}/photo")
	public StreamingResponseBody getPhoto(@PathVariable long id, Model model) throws IOException {		
		File f = localData.getFile("user", ""+id);
		InputStream in;
		if (f.exists()) {
			in = new BufferedInputStream(new FileInputStream(f));
		} else {
			in = new BufferedInputStream(getClass().getClassLoader()
					.getResourceAsStream("static/img/unknown-user.jpg"));
		}
		return new StreamingResponseBody() {
			@Override
			public void writeTo(OutputStream os) throws IOException {
				FileCopyUtils.copy(in, os);
			}
		};
	}
	
	@PostMapping("/{id}/photo")
	public String postPhoto(
			HttpServletResponse response,
			@RequestParam("photo") MultipartFile photo,
			@PathVariable("id") String id, Model model, HttpSession session) throws IOException {
		User target = entityManager.find(User.class, Long.parseLong(id));
		model.addAttribute("user", target);
		
		// check permissions
		User requester = (User)session.getAttribute("u");
		if (requester.getId() != target.getId() &&
				! requester.hasRole(Role.ADMIN)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, 
					"No eres administrador, y éste no es tu perfil");
			return "profile";
		}
		
		log.info("Updating photo for user {}", id);
		File f = localData.getFile("user", id);
		if (photo.isEmpty()) {
			log.info("failed to upload photo: emtpy file?");
		} else {
			try (BufferedOutputStream stream =
					new BufferedOutputStream(new FileOutputStream(f))) {
				byte[] bytes = photo.getBytes();
				stream.write(bytes);
			} catch (Exception e) {
				log.warn("Error uploading " + id + " ", e);
			}
			log.info("Successfully uploaded photo for {} into {}!", id, f.getAbsolutePath());
		}
		return "profile";
	}	
	
	@GetMapping(value="/team/{teamId}/photo")
	public StreamingResponseBody getTeamPhoto(@PathVariable("teamId") String teamId,
			Model model) throws IOException {		
		File f = localData.getFile("team", ""+teamId);
		InputStream in;
		if (f.exists()) {
			in = new BufferedInputStream(new FileInputStream(f));
		} else {
			in = new BufferedInputStream(getClass().getClassLoader()
					.getResourceAsStream("static/img/unknown-user.jpg"));
		}
		return new StreamingResponseBody() {
			@Override
			public void writeTo(OutputStream os) throws IOException {
				FileCopyUtils.copy(in, os);
			}
		};
	}
	
	@PostMapping("/{id}/team/{teamId}/photo")
	public String postTeamPhoto(
			HttpServletResponse response,
			@RequestParam("photo") MultipartFile photo,
			@PathVariable("id") String id, @PathVariable("teamId") String teamId, 
			Model model, HttpSession session) throws IOException {
		User target = entityManager.find(User.class, Long.parseLong(id));
		model.addAttribute("user", target);
		
		StTeam team = entityManager.find(StTeam.class, Long.parseLong(teamId));
		model.addAttribute("team", team);
		
		// check permissions
		User requester = (User)session.getAttribute("u");
		if (requester.getId() != target.getId() &&
				! requester.hasRole(Role.ADMIN)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, 
					"No eres administrador, y éste no es tu perfil");
			return "team";
		}
		
		log.info("Updating photo for team {}", teamId);
		File f = localData.getFile("team", teamId);
		if (photo.isEmpty()) {
			log.info("failed to upload photo: emtpy file?");
		} else {
			try (BufferedOutputStream stream =
					new BufferedOutputStream(new FileOutputStream(f))) {
				byte[] bytes = photo.getBytes();
				stream.write(bytes);
			} catch (Exception e) {
				log.warn("Error uploading " + teamId + " ", e);
			}
			log.info("Successfully uploaded photo for {} into {}!", id, f.getAbsolutePath());
		}
		return "team";
	}
	
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