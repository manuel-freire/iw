package es.ucm.fdi.iw.control;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.itextpdf.text.DocumentException;

import es.ucm.fdi.iw.LocalData;
import es.ucm.fdi.iw.constants.ClassFileDTO;
import es.ucm.fdi.iw.model.Answer;
import es.ucm.fdi.iw.model.Contest;
import es.ucm.fdi.iw.model.Question;
import es.ucm.fdi.iw.model.StClass;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.model.User.Role;
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
	public String classes(Model model) {
		
		return "class";
	}

	@GetMapping("/{id}/contest")
	public String contest(Model model) {
		return "contest";
	}
	
	@GetMapping("/{id}/play")
	public String play(Model model) {
		return "play";
	}	

	@PostMapping("/{id}/class")
	@Transactional
	public String createClassFromFile(
			HttpServletResponse response,
			@RequestParam("classfile") MultipartFile classFile,
			@PathVariable("id") String id,
			Model model, HttpSession session) throws IOException {
		User target = entityManager.find(User.class, Long.parseLong(id));
		model.addAttribute("user", target);
		
		// check permissions
		User requester = (User)session.getAttribute("u");
		if (requester.getId() != target.getId() &&	! requester.hasRole(Role.ADMIN)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "No eres profesor, y éste no es tu perfil");
			return classes(model);
		}
		
		log.info("Profesor {} subiendo fichero de clase", id);
		if (classFile.isEmpty()) {
			log.info("El fichero está vacío");
		} else {
			String content = new String(classFile.getBytes(), "UTF-8");
			log.info("El fichero con los datos se ha cargado correctamente");
			saveClassToDb(content);

			model.addAttribute("users", entityManager.createQuery(
					"SELECT u FROM User u").getResultList());
			model.addAttribute("stClass", entityManager.createQuery(
					"SELECT c FROM StClass c WHERE id=2").getResultList());
		}
		
		return classes(model);
	}	
	
	private void saveClassToDb(String content) {	
		User student;
		log.info("Inicio del procesado del fichero de clase");		
		ClassFileDTO classInfo = ClassFileReader.readClassFile(content);
		if (classInfo.getStClass() != null && classInfo.getStudents() != null) {
			entityManager.persist(classInfo.getStClass());
			for(int i = 0; i < classInfo.getStudents().size(); i++) {
				student = classInfo.getStudents().get(i);
				student.setPassword(passwordEncoder.encode(student.getPassword()));
				entityManager.persist(student);
			}
			log.info("La información se ha cargado en la base de datos correctamente");				
		} else {
			log.warn("La información de la clase está incompleta");
		}
	}
	
	@PostMapping("/{id}/contest")
	@Transactional
	public String createContestFromFile(
			HttpServletResponse response,
			@RequestParam("contestfile") MultipartFile contestFile,
			@PathVariable("id") String id,
			Model model, HttpSession session) throws IOException {
		User target = entityManager.find(User.class, Long.parseLong(id));
		model.addAttribute("user", target);
		
		// check permissions
		User requester = (User)session.getAttribute("u");
		if (requester.getId() != target.getId() &&	! requester.hasRole(Role.ADMIN)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "No eres profesor, y éste no es tu perfil");
			return contest(model);
		}
		
		log.info("Profesor {} subiendo fichero de clase", id);
		if (contestFile.isEmpty()) {
			log.info("El fichero está vacío");
		} else {
			String content = new String(contestFile.getBytes(), "UTF-8");
			log.info("El fichero con los datos se ha cargado correctamente");
			saveContestToDb(target, content);
		}
		
		return contest(model);
	}	
	
	private void saveContestToDb(User teacher, String content) {
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
			entityManager.persist(contest);				
		} else {
			log.warn("La información de las preguntas es incompleta o  errónea");
		}
	}
	
	@PostMapping("/{id}/class/saveFile")
	public String saveClassToFile(
			HttpServletResponse response,
			@PathVariable("id") String id,
			Model model, HttpSession session) throws IOException, DocumentException {
		User target = entityManager.find(User.class, Long.parseLong(id));
		model.addAttribute("user", target);
		
		// check permissions
		User requester = (User)session.getAttribute("u");
		if (requester.getId() != target.getId() &&	! requester.hasRole(Role.ADMIN)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "No eres profesor, y éste no es tu perfil");
			return classes(model);
		}
		
		List<User> userList = dummyUsers();
		StClass stClass = dummyClass();
		
		if (stClass == null) {
			log.info("Error al acceder a los datos de la clase");
		} else if (userList == null || userList.isEmpty()){
			log.info("Error al acceder a los datos de los alumnos");
		} else {
			log.info("Creando fichero QR de la clase");
			PdfGenerator.generateQrClassFile(userList, stClass);
		}
		
		return classes(model);
	}	
	
	public List<User> dummyUsers() {
		List<User> users = new ArrayList<>();
		
		User u1 = new User();
		u1.setFirstName("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
		u1.setLastName("aaa aaa");
		u1.setUsername("ST.001");
		u1.setId(4);
		
		User u2 = new User();
		u2.setFirstName("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
		u2.setLastName("bbb bbb");
		u2.setUsername("ST.002");
		u2.setId(5);
		
		User u3 = new User();
		u3.setFirstName("ccccccccccccccccccccccccccccccc");
		u3.setLastName("ccc ccc");
		u3.setUsername("ST.003");
		u3.setId(6);
		
		users.add(u1);
		users.add(u2);
		users.add(u3);
		users.add(u3);
		users.add(u2);
		users.add(u2);
		users.add(u1);
		users.add(u3);
		users.add(u3);
		users.add(u2);
		users.add(u1);
		users.add(u2);
		users.add(u3);
		users.add(u2);
		users.add(u1);
		users.add(u2);
		users.add(u2);
		users.add(u2);
		users.add(u1);
		users.add(u3);
		users.add(u3);
		users.add(u2);
		users.add(u1);
		users.add(u2);
		users.add(u3);
		users.add(u2);
		users.add(u1);
		users.add(u2);
		
		return users;
	}
	
	public StClass dummyClass() {
		StClass st = new StClass();
		st.setClassName("Clase de prueba");
		st.setId(2);
		
		return st;
	}
}
