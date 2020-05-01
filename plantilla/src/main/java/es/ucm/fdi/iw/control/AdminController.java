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
import java.util.List;

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
 * @author mfreire
 */
@Controller()
@RequestMapping("admin")
public class AdminController {
	
	private static final Logger log = LogManager.getLogger(AdminController.class);
	
	@Autowired 
	private EntityManager entityManager;
	
	@Autowired
	private LocalData localData;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private Environment env;
	
	@GetMapping("/")
	public String index(Model model) {
		model.addAttribute("activeProfiles", env.getActiveProfiles());
		model.addAttribute("basePath", env.getProperty("es.ucm.fdi.base-path"));

		model.addAttribute("users", entityManager.createQuery(
				"SELECT u FROM User u").getResultList());
		
		return "admin";
	}
	
	@GetMapping("/{id}")
	public String getUser(@PathVariable long id, Model model, HttpSession session) {
		User u = entityManager.find(User.class, id);
		model.addAttribute("user", u);
		return "admin";
	}
	
	@PostMapping("/toggleuser")
	@Transactional
	public String delUser(Model model,	@RequestParam long id) {
		User target = entityManager.find(User.class, id);
		if (target.getEnabled() == 1) {
			// disable
			File f = localData.getFile("user", ""+id);
			if (f.exists()) {
				f.delete();
			}
			// disable user
			target.setEnabled((byte)0); 
		} else {
			// enable user
			target.setEnabled((byte)1);
		}
		return index(model);
	}
	
	@GetMapping("/error")
	public String error(Model model) {
		return "error";
	}
	
	@GetMapping("/{id}/class")
	public String classes(@PathVariable long id, Model model, HttpSession session) {
		User u = entityManager.find(User.class, id);
		model.addAttribute("user", u);
		
		List<StClass> classList = entityManager.createNamedQuery("StClass.byTeacher", StClass.class)
				.setParameter("userId", u.getId()).getResultList();
		model.addAttribute("classList", classList);
		
		return "class";
	}

	@GetMapping("/{id}/contest")
	public String contest(@PathVariable long id, Model model, HttpSession session) {
		User u = entityManager.find(User.class, id);
		model.addAttribute("user", u);
		
		List<Contest> contestList = entityManager.createNamedQuery("Contest.byTeacher", Contest.class)
				.setParameter("userId", u.getId()).getResultList();
		model.addAttribute("contestList", contestList);
		
		return "contest";
	}
	
	@GetMapping("/{id}/play")
	public String play(@PathVariable long id, Model model, HttpSession session) {
		User u = entityManager.find(User.class, id);
		model.addAttribute("user", u);
		
		List<Contest> contestList = entityManager.createNamedQuery("Contest.byTeacher", Contest.class)
				.setParameter("userId", u.getId()).getResultList();
		model.addAttribute("contestList", contestList);
		
		return "play";
	}	
	
	@GetMapping("/{id}/class/{classId}")
	public String selectedClass(@PathVariable("id") long id, @PathVariable("classId") long classId,
			Model model, HttpSession session) {
		User target = entityManager.find(User.class, id);
		model.addAttribute("user", target);
		StClass stClass = entityManager.find(StClass.class, classId);
		model.addAttribute("stClass", stClass);
		
		List<StTeam> teams = entityManager.createNamedQuery("StTeam.byClass", StTeam.class)
                .setParameter("classId", classId).getResultList();
		
		List<User> students = entityManager.createNamedQuery("User.byClass", User.class)
                .setParameter("classId", classId).getResultList();
		
		List<StClass> classList = entityManager.createNamedQuery("StClass.byTeacher", StClass.class)
				.setParameter("userId", id).getResultList();
		
		List<Contest> contests = entityManager.createNamedQuery("Contest.byClass", Contest.class)
				.setParameter("classId", classId).getResultList();

		model.addAttribute("teams", teams);
		model.addAttribute("students", students);
		model.addAttribute("classList", classList);
		model.addAttribute("contests", contests);
		
		return classes(id, model, session);
	}
	
	@GetMapping("/{id}/class/{classId}/createQR")
	public StreamingResponseBody getQrFile(@PathVariable("id") long id, @PathVariable("classId") long classId,
			Model model, HttpSession session) throws IOException, DocumentException {	
		
		StClass stClass = entityManager.find(StClass.class, classId);
		
		if (stClass.getStudents() == null || stClass.getStudents().isEmpty()){
			log.info("Error al acceder a los datos de los alumnos");
		} else {
			log.info("Creando fichero QR de la clase");
			String qrFile = PdfGenerator.generateQrClassFile(stClass.getStudents(), stClass);				
			uploadToTemp(qrFile);
	    }
		
		File f = localData.getFile("qrcodes", ConstantsFromFile.QR_FILE + "." + ConstantsFromFile.PDF);
		InputStream in = new BufferedInputStream(new FileInputStream(f));
		return new StreamingResponseBody() {
			@Override
			public void writeTo(OutputStream os) throws IOException {
				FileCopyUtils.copy(in, os);
			}
		};
	}	
	
	@GetMapping("/{id}/contest/{contestId}")
	public String selectedContest(@PathVariable("id") long id, @PathVariable("contestId") long contestId,
			Model model, HttpSession session) {
		User target = entityManager.find(User.class, id);
		model.addAttribute("user", target);
		
		List<Contest> contestList = entityManager.createNamedQuery("Contest.byTeacher", Contest.class)
				.setParameter("userId", id).getResultList();
		model.addAttribute("contestList", contestList);
		
		Contest contest = entityManager.find(Contest.class, contestId);
		model.addAttribute("contest", contest);
		
		List<Result> resultList = entityManager.createNamedQuery("Result.byContest", Result.class)
				.setParameter("contestId", contestId).getResultList();
		model.addAttribute("resultList", resultList);
		
		StClass stClass = entityManager.createNamedQuery("StClass.contestOwner", StClass.class)
				.setParameter("contestId", contestId).getSingleResult();
		
		List<User> students = entityManager.createNamedQuery("User.byClass", User.class)
				.setParameter("classId", stClass.getId()).getResultList();
		model.addAttribute("students", students);
		
		model.addAttribute("stats", getContestStats(contest));
		
		return contest(id, model, session);
	}
	
	@GetMapping("/{id}/play/{contestId}")
	public String playContest(@PathVariable("id") long id, @PathVariable("contestId") long contestId,
			Model model, HttpSession session) {
		User u = entityManager.find(User.class, id);
		model.addAttribute("user", u);
		
		List<Contest> contestList = entityManager.createNamedQuery("Contest.byTeacher", Contest.class)
				.setParameter("userId", id).getResultList();
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
				
		return play(id, model, session);
	}

	@PostMapping("/{id}/class")
	@Transactional
	public String createClassFromFile(
			HttpServletResponse response,
			@RequestParam("classfile") MultipartFile classFile,
			@PathVariable("id") long id,
			Model model, HttpSession session) throws IOException, DocumentException {
		User target = entityManager.find(User.class, id);
		model.addAttribute("user", target);
		
		// check permissions
		User requester = (User)session.getAttribute("u");
		if (requester.getId() != target.getId() &&	! requester.hasRole(Role.ADMIN)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "No eres profesor, y éste no es tu perfil");
			return classes(id, model, session);
		}
		
		log.info("Profesor {} subiendo fichero de clase", id);
		if (classFile.isEmpty()) {
			log.info("El fichero está vacío");
		} else {
			String content = new String(classFile.getBytes(), "UTF-8");
			log.info("El fichero con los datos se ha cargado correctamente");
			saveClassToDb(model, target, content);			
		}
		
		return classes(id, model, session);
	}
	
	@PostMapping("/{id}/class/{classId}/createTeams")
	@Transactional
	public String createTeams(
			HttpServletResponse response,
			@RequestParam("teamComp") List<String> teamComp,
			@RequestParam("numTeams") String numTeams,
			@PathVariable("id") long id,
			@PathVariable("classId") long classId,
			Model model, HttpSession session) throws IOException, DocumentException {
		User target = entityManager.find(User.class, id);
		model.addAttribute("user", target);
		
		// check permissions
		User requester = (User)session.getAttribute("u");
		if (requester.getId() != target.getId() &&	! requester.hasRole(Role.ADMIN)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "No eres profesor, y éste no es tu perfil");
			return selectedClass(id, classId, model, session);
		}
		
		if (teamComp == null || teamComp.isEmpty()) {
			log.info("No se han creado equipos o ningún alumno ha sido asignado");
		} else {
			log.info("Profesor {} creando equipos", id);	
			
			List<StTeam> teams = new ArrayList<>();
			StClass stClass= entityManager.find(StClass.class, classId);
			StTeam team;
			User student;
			
			String[] studentInfo;
			String username;
			int teamIndex;
			
			for (int i = 0; i < Integer.valueOf(numTeams); i++) {
				team = new StTeam();
				team.setBronze(0);
				team.setSilver(0);
				team.setGold(0);
				team.setElo(1000);
				team.setCorrect(0);
				team.setTeamName("Equipo " + (i+1));
				team.setStClass(stClass);
				team.setMembers(new ArrayList<>());
				team.setAchievementTeam(createAchievementsTeam(team));
				teams.add(team);
				entityManager.persist(team);
			}

			for (int j = 0; j < teamComp.size(); j++) {
				studentInfo = teamComp.get(j).split(ConstantsFromFile.SEPARATOR);
				username = studentInfo[0].split(" - ")[0];
				teamIndex = Integer.valueOf(studentInfo[1]);
				student = entityManager.createNamedQuery("User.userInClass", User.class)
	                    .setParameter("username", username)
	                    .setParameter("classId", stClass.getId())
	                    .getSingleResult();
				if (student != null) {
					team = teams.get(teamIndex);
					team.getMembers().add(student);
					student.setTeam(team);
					entityManager.persist(student);
				} else {
					log.info("No existe ningún alumno con ese nombre de usuario");
				}
			}
			
			log.info("{}", teams);
			stClass.setTeamList(teams);
			entityManager.persist(stClass);			

		}	
		
		return selectedClass(id, classId, model, session);
	}
	
	@PostMapping("/{id}/class/{classId}/addContest")
	@Transactional
	public String addContest(
			HttpServletResponse response,
			@RequestParam("contestfile") MultipartFile contestFile,
			@PathVariable("id") long id,
			@PathVariable("classId") long classId,
			Model model, HttpSession session) throws IOException, DocumentException {
		User target = entityManager.find(User.class, id);
		model.addAttribute("user", target);

		StClass stClass = entityManager.find(StClass.class, classId);
		model.addAttribute("stClass", target);
		
		// check permissions
		User requester = (User)session.getAttribute("u");
		if (requester.getId() != target.getId() &&	! requester.hasRole(Role.ADMIN)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "No eres profesor, y éste no es tu perfil");
			return selectedClass(id, classId, model, session);
		}
		
		log.info("Profesor {} subiendo fichero de clase", id);
		if (contestFile.isEmpty()) {
			log.info("El fichero está vacío");
		} else {
			String content = new String(contestFile.getBytes(), "UTF-8");
			log.info("El fichero con los datos se ha cargado correctamente");
			saveContestToDb(model, target, stClass, content);
		}	
		
		return selectedClass(id, classId, model, session);
	}		
	
	@PostMapping("/{id}/contest/{contestId}//toggleContest")
	@Transactional
	public String toggleContest(
			HttpServletResponse response,
			@PathVariable("id") long id,
			@PathVariable("contestId") long contestId,
			Model model, HttpSession session) throws IOException {
		User target = entityManager.find(User.class, id);
		model.addAttribute("user", target);
		
		List<Contest> contestList = entityManager.createNamedQuery("Contest.byTeacher", Contest.class)
				.setParameter("userId", id).getResultList();
		model.addAttribute("contestList", contestList);
		
		Contest contest = entityManager.find(Contest.class, contestId);

		// check permissions
		User requester = (User)session.getAttribute("u");
		if (requester.getId() != target.getId() &&	! requester.hasRole(Role.ADMIN)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "No eres profesor, y éste no es tu perfil");
			return selectedContest(id, contestId, model, session);
		}
		
		if (contest.getEnabled() == 1) {
			contest.setEnabled((byte)0); 
		} else {
			// enable user
			contest.setEnabled((byte)1);
		}
		
		model.addAttribute("contest", contest);
		
		return selectedContest(id, contestId, model, session);
	}
	
	@PostMapping("/{id}/play/{contestId}/results")
	@Transactional
	public String getResults(
			HttpServletResponse response,
			@RequestParam("results") List<String> answerList,
			@PathVariable("id") long id,
			@PathVariable("contestId") long contestId,
			Model model, HttpSession session) throws IOException, DocumentException {
		User target = entityManager.find(User.class, id);
		model.addAttribute("user", target);
		
		// check permissions
		User requester = (User)session.getAttribute("u");
		if (requester.getId() != target.getId() &&	! requester.hasRole(Role.ADMIN)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "No eres profesor, y éste no es tu perfil");
			return playContest(id, contestId, model, session);
		}
		
		
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
	
	private Model saveClassToDb(Model model, User teacher, String content) throws MalformedURLException, DocumentException, IOException {
		log.info("Inicio del procesado del fichero de clase");		
		StClass stClass = ClassFileReader.readClassFile(content);
		if (stClass != null) {		
			stClass.setTeacher(teacher);
			teacher.getStClassList().add(stClass);
			entityManager.persist(stClass);
			entityManager.persist(teacher);
			
			for(User student: stClass.getStudents()) {
				log.info("{} - {} \n \n ", student.getId(), student.hashCode());
				student.setPassword(passwordEncoder.encode(student.getPassword()));
				student.setAchievementUser(createAchievementsUser(student));
				entityManager.persist(student);
			}
			
			log.info("La información se ha cargado en la base de datos correctamente");
			
			model.addAttribute("users", entityManager.createNamedQuery("User.byClass", User.class)
                    .setParameter("classId", stClass.getId()).getResultList());
			model.addAttribute("stClass", entityManager.find(StClass.class, stClass.getId()));
	
		} else {
			log.warn("La información de la clase está incompleta");
		}
		
		return model;
	}
	
	private Model saveContestToDb(Model model, User teacher, StClass stClass, String content) {
		log.info("Inicio del procesado del fichero de clase");		
		Contest contest = ContestFileReader.readContestFile(content);
		List<Question> questionList;
		Question question;
		List<Answer> answerList;
		
		if (contest.getQuestions() != null) {
			questionList = contest.getQuestions();
			for (int i = 0; i < questionList.size(); i++) {
				question = questionList.get(i);
				if (question.getAnswers() != null) {
					answerList = question.getAnswers();
					for (int j = 0; j < answerList.size(); j++) {
						entityManager.persist(answerList.get(j));						
					}
				} else {
					log.info("La información de la pregunta {} es incompleta o errónea", i);						
				}
				entityManager.persist(question);				
			}
			
			contest.setTeacher(teacher);
			contest.setStClass(stClass);
			entityManager.persist(contest);	
			teacher.getContests().add(contest);
			stClass.getClassContest().add(contest);
			
			model.addAttribute("contest", entityManager.find(Contest.class, contest.getId()));	
			
		} else {
			log.warn("La información de las preguntas es incompleta o  errónea");
		}
		
		return model;
	}	
	
	private void uploadToTemp(String tempFile) throws IOException {
		FileInputStream instream = null;
		FileOutputStream outstream = null;
	 
	    File infile = new File(tempFile);
	    File outfile = localData.getFile("qrcodes", ConstantsFromFile.QR_FILE + "." + ConstantsFromFile.PDF);

	    instream = new FileInputStream(infile);
	    outstream = new FileOutputStream(outfile);
	    
	    byte[] buffer = new byte[1024];
	    int length;
	    while ((length = instream.read(buffer)) > 0){
	    	outstream.write(buffer, 0, length);
	    }

	    instream.close();
	    outstream.close();
	}

	private List<Achievement> createAchievementsUser(User user) {
		List<Achievement> achievementList = new ArrayList<Achievement>();
		List<Goal> goals = entityManager.createNamedQuery("Goal.forUser", Goal.class).getResultList();
		Achievement achievement;
		
		for (int i = 0; i < goals.size(); i++) {
			achievement = new Achievement();
			achievement.setGoal(goals.get(i));
			achievement.setStudent(user);
			achievement.setLevel(0);
			log.info(goals.get(i).getKey() + "\n\n\n\n\n");
			if (goals.get(i).getKey().equals("ELO")) {
				achievement.setProgress(user.getElo());
			} else {
				achievement.setProgress(0);
			}
				
			achievementList.add(achievement);
			entityManager.persist(achievement);
		}		
		
		return achievementList;
	}
	
	private List<Achievement> createAchievementsTeam(StTeam team) {
		List<Achievement> achievementList = new ArrayList<Achievement>();
		List<Goal> goals = entityManager.createNamedQuery("Goal.forTeam", Goal.class).getResultList();
		Achievement achievement;
		
		for (int i = 0; i < goals.size(); i++) {
			achievement = new Achievement();
			achievement.setGoal(goals.get(i));
			achievement.setTeam(team);
			achievement.setLevel(0);
			if (goals.get(i).getKey().equals("ELO")) {
				achievement.setProgress(team.getElo());
			} else {
				achievement.setProgress(0);
			}
				
			achievementList.add(achievement);
			entityManager.persist(achievement);
		}		
		
		return achievementList;
	}
	
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
				sb.append(a.getText() + "-->" + count + ",");
			}
			a = q.getAnswers().get(q.getAnswers().size()-1);
			count = (Long)entityManager.createNamedQuery("Result.numAnswers")
			.setParameter("answerId", a.getId())
			.setParameter("contestId", contest.getId()).getSingleResult();
			sb.append(a.getText() + ":" + Long.toString(count));
			
			contestStats.add(sb.toString());
		}
		
		return contestStats;		
	}
}
