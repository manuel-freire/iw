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
import es.ucm.fdi.iw.model.StClass;
import es.ucm.fdi.iw.model.StTeam;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.model.User.Role;
import es.ucm.fdi.iw.utils.ClassFileReader;
import es.ucm.fdi.iw.utils.ContestFileReader;
import es.ucm.fdi.iw.utils.PdfGenerator;

/**
 * Admin-only controller
 * @author aitorcay
 */

@Controller()
@RequestMapping("admin/{id}/class")
public class AdminClassController {
	
	private static final Logger log = LogManager.getLogger(AdminClassController.class);
	
	@Autowired 
	private EntityManager entityManager;
	
	@Autowired
	private LocalData localData;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	private static final int TOKEN_LENGTH = 7;
	
	/**
	 * Vista con las clases creadas por un profesor/a
	 * 
	 * @param id		id del usuario loggeado
	 * @param model		modelo que contendrá la información
	 * @param session	sesión asociada al usuario
	 * @return			vista a mostrar
	 */
	@GetMapping("")
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
	 * Vista con la información de una clase concreta
	 * 
	 * @param id		id del usuario loggeado
	 * @param classId	id de la clase
	 * @param model		modelo que contendrá la información
	 * @param session	sesión asociada al usuario
	 * @return			vista a mostrar
	 */
	@GetMapping("/{classId}")
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
	@GetMapping("/{classId}/createQR")
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
	@PostMapping("")
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
	@PostMapping("/{classId}/createTeams")
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
			String[] userInfo;
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
				userInfo = studentInfo[0].split(" ");
				teamIndex = Integer.valueOf(studentInfo[1]);
				student = entityManager.createNamedQuery("User.userInClass", User.class)
	                    .setParameter("firstName", userInfo[0])
	                    .setParameter("lastName", userInfo[1] + " " + userInfo[2])
	                    .setParameter("classId", classId)
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
	@PostMapping("/{classId}/addContest")
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
}
