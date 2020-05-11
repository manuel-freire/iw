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
	
	private static final int TOKEN_LENGTH = 7;

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
	
	/**
	 * Vista con las clases creadas por un profesor/a
	 * 
	 * @param id		id del usuario loggeado
	 * @param model		modelo que contendrá la información
	 * @param session	sesión asociada al usuario
	 * @return			vista a mostrar
	 */
	@GetMapping("/{id}/class")
	public String classes(@PathVariable long id, Model model, HttpSession session) {
		User u = entityManager.find(User.class, id);
		model.addAttribute("user", u);
		
		//Lista de clases creadas por un profesor/a
		List<StClass> classList = entityManager.createNamedQuery("StClass.byTeacher", StClass.class)
				.setParameter("userId", u.getId()).getResultList();
		model.addAttribute("classList", classList);
		
		return "class";
	}

	/**
	 * Vista con las pruebas creadas por un profesor/a
	 * 
	 * @param id		id del usuario loggeado
	 * @param model		modelo que contendrá la información
	 * @param session	sesión asociada al usuario
	 * @return			vista a mostrar
	 */
	@GetMapping("/{id}/contest")
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
	 * Vista con los rankings asociados a cada clase creada por un profesor/a
	 * 
	 * @param id		id del usuario loggeado
	 * @param model		modelo que contendrá la información
	 * @param session	sesión asociada al usuario
	 * @return			vista a mostrar
	 */
	@GetMapping("/{id}/rankings")
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
	 * Vista para testear las pruebas creadas 
	 * 
	 * @param id		id del usuario loggeado
	 * @param model		modelo que contendrá la información
	 * @param session	sesión asociada al usuario
	 * @return			vista a mostrar
	 */
	@GetMapping("/{id}/play")
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
	 * Vista con la información de una clase concreta
	 * 
	 * @param id		id del usuario loggeado
	 * @param classId	id de la clase
	 * @param model		modelo que contendrá la información
	 * @param session	sesión asociada al usuario
	 * @return			vista a mostrar
	 */
	@GetMapping("/{id}/class/{classId}")
	public String selectedClass(@PathVariable("id") long id, @PathVariable("classId") long classId,
			Model model, HttpSession session) {
		User target = entityManager.find(User.class, id);
		model.addAttribute("user", target);
		StClass stClass = entityManager.find(StClass.class, classId);
		model.addAttribute("stClass", stClass);
		
		//Lista de equipos asociados a una clase
		List<StTeam> teams = entityManager.createNamedQuery("StTeam.byClass", StTeam.class)
                .setParameter("classId", classId).getResultList();
		//Lista de estudiantes pertenecientes a una clase
		List<User> students = entityManager.createNamedQuery("User.byClass", User.class)
                .setParameter("classId", classId).getResultList();
		//Lista de clases creadas por un profesor/a
		List<StClass> classList = entityManager.createNamedQuery("StClass.byTeacher", StClass.class)
				.setParameter("userId", id).getResultList();
		//Lista de pruebas asociadas a una clase
		List<Contest> contests = entityManager.createNamedQuery("Contest.byClassTeacher", Contest.class)
				.setParameter("classId", classId).getResultList();

		model.addAttribute("teams", teams);
		model.addAttribute("students", students);
		model.addAttribute("classList", classList);
		model.addAttribute("contests", contests);
		
		return classes(id, model, session);
	}
	
	/**
	 * Crea un documento PDF con la información de los estudiantes asociados a una clase: nombre y apellidos, usuario y
	 * un código QR con la dirección de acceso a su perfil.
	 * 
	 * @param id					id del usuario loggeado
	 * @param classId				id de la clase de la que se va a obtener la información
	 * @param model					modelo que contendrá la información
	 * @param session				sesión asociada al usuario
	 * @return						documento con la información de la clase
	 * @throws IOException
	 * @throws DocumentException
	 */
	@GetMapping("/{id}/class/{classId}/createQR")
	@Transactional
	public StreamingResponseBody getQrFile(@PathVariable("id") long id, @PathVariable("classId") long classId,
			Model model, HttpSession session) throws IOException, DocumentException {	
		
		StClass stClass = entityManager.find(StClass.class, classId);
		
		if (stClass.getStudents() == null || stClass.getStudents().isEmpty()){
			log.info("Error al acceder a los datos de los alumnos");
		} else {
			log.info("Generando tokens para alumnos");
			for (User u : stClass.getStudents()) {
				u.createAndSetRandomToken(TOKEN_LENGTH);
				u.setPassword(passwordEncoder.encode(u.getToken()));
			}
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
	
	/**
	 * Vista con la información de una prueba concreta: preguntas, respuestas, puntuaciones asociadas y resultados de la clase
	 * 
	 * @param id		id del usuario loggeado
	 * @param contestId	id de la prueba
	 * @param model		modelo que contendrá la información
	 * @param session	sesión asociada al usuario
	 * @return			vista a mostrar
	 */
	@GetMapping("/{id}/contest/{contestId}")
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
	@GetMapping("/{id}/contest/{contestId}/ranking")
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
	 * Vista con el ranking global de las puntuaciones acumuladas por estudiantes y equipos
	 * 
	 * @param id		id del usuario loggeado
	 * @param classId	id de la clase
	 * @param model		modelo que contendrá la información
	 * @param session	sesión asociada al usuario
	 * @return			vista a mostrar
	 */
	@GetMapping("/{id}/rankings/{classId}")
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
	
	/**
	 * Vista para resolver una prueba
	 * 
	 * @param id		id del usuario loggeado
	 * @param contestId	id de la prueba
	 * @param model		modelo que contendrá la información
	 * @param session	sesión asociada al usuario
	 * @return			vista a mostrar
	 */
	@GetMapping("/{id}/play/{contestId}")
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
	 * Crea una nueva clase en base a la información cargada desde un fichero JSON.
	 * 
	 * @param response				para gestión de las peticiones HTTP
	 * @param classFile				fichero con la información de la clase
	 * @param id					id del usuario loggeado
	 * @param model					modelo que contendrá la información
	 * @param session				sesión asociada al usuario
	 * @return						vista a mostrar
	 * @throws IOException
	 * @throws DocumentException
	 */
	@PostMapping("/{id}/class")
	@Transactional
	public String createClassFromFile(
			HttpServletResponse response,
			@RequestParam("classfile") MultipartFile classFile,
			@PathVariable("id") long id,
			Model model, HttpSession session) throws IOException, DocumentException {
		User target = entityManager.find(User.class, id);
		model.addAttribute("user", target);
		
		//Comprobación de permisos
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
	
	/**
	 * Persiste los equipos creados por el profesor en la interfaz
	 * 
	 * @param response				para gestión de las peticiones HTTP
	 * @param teamComp				composición de los equipos
	 * @param numTeams				número de equipos
	 * @param id					id del usuario loggeado
	 * @param classId				id de la clase
	 * @param model					modelo que contendrá la información
	 * @param session				sesión asociada al usuario
	 * @return						vista a mostrar
	 * @throws IOException
	 * @throws DocumentException
	 */
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
		
		//Comprobación de permisos
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
			
			//Creación de los equipos con los valores por defecto
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

			//Asignación de los alumnos a los equipos
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
	
	/**
	 * Crea una nueva prueba en base a la información cargada desde un fichero JSON
	 * 
	 * @param response				para gestión de las peticiones HTTP
	 * @param contestFile			fichero con la información de la prueba
	 * @param id					id del usuario loggeado
	 * @param classId				id de la clase
	 * @param model					modelo que contendrá la información
	 * @param session				sesión asociada al usuario
	 * @return						vista a mostrar
	 * @throws IOException
	 * @throws DocumentException
	 */
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
	@PostMapping("/{id}/contest/{contestId}//toggleContest")
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
		}
		
		model.addAttribute("contest", contest);
		
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
	@PostMapping("/{id}/contest/{contestId}//completeContest")
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
		model.addAttribute("contest", contest);
		
		return contestRanking(id, contestId, model, session);
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
	@PostMapping("/{id}/play/{contestId}/retry")
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
	
	/**
	 * Persiste una clase en la base de datos
	 * 
	 * @param model		modelo que contendrá la información
	 * @param teacher	profesor propietario de la clase
	 * @param content	información de la clase
	 * @return			modelo actualizado
	 * @throws MalformedURLException
	 * @throws DocumentException
	 * @throws IOException
	 */
	private Model saveClassToDb(Model model, User teacher, String content) throws MalformedURLException, DocumentException, IOException {
		log.info("Inicio del procesado del fichero de clase");		
		//Procesado de la información de la clase
		StClass stClass = ClassFileReader.readClassFile(content);
		if (stClass != null) {		
			//Asignación de la clase al profesor
			stClass.setTeacher(teacher);
			teacher.getStClassList().add(stClass);
			entityManager.persist(stClass);
			entityManager.persist(teacher);
			
			//Generación de los logros y persistencia de cada alumno
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
	
	/**
	 * Persiste una prueba en la base de datos
	 * 
	 * @param model		modelo que contendrá la información
	 * @param teacher	profesor autor de la prueba
	 * @param stClass	clase asociada a la prueba
	 * @param content	información de la prueba
	 * @return			modelo actualizado
	 */
	private Model saveContestToDb(Model model, User teacher, StClass stClass, String content) {
		log.info("Inicio del procesado del fichero de clase");	
		//Procesado de la información de la prueba
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
	
	/**
	 * Añade el fichero PDF al directorio directorio de la aplicación para su posterior acceso
	 * 
	 * @param tempFile		fichero PDF con la información de una clase
	 * @throws IOException
	 */
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

	/**
	 * Inicializa los logros de un usuario
	 * 
	 * @param user	usuario al que se asignan los logros
	 * @return		lista de logros
	 */
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
	
	/**
	 * Inicializa los logros de un equipo
	 * 
	 * @param team	equipo al que se asignan los logros
	 * @return		lista de logros
	 */
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
				sb.append(a.getText() + "-->" + count + ",");
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
		double max = results.get(0).getScore();
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
		if (contest.getChecked() == 0) {
			updateAchievementsUser(results, positionUser);
		}		
		
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
        
		model.addAttribute("rankingTeam", Arrays.asList(sortedTeams.keySet().toArray()));
		model.addAttribute("scoreTeam", Arrays.asList(sortedTeams.values().toArray()));
		model.addAttribute("positionTeam", positionTeam);		

		if (contest.getChecked() == 0) {
			contest.setChecked((byte) 1);
			updateAchievementsTeam((List<StTeam>)(Object)Arrays.asList(sortedTeams.keySet().toArray()), positionTeam);
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
