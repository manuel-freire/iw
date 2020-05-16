package es.ucm.fdi.iw.control;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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

import es.ucm.fdi.iw.LocalData;
import es.ucm.fdi.iw.model.Achievement;
import es.ucm.fdi.iw.model.StTeam;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.model.User.Role;

/**
 * User-administration controller
 * 
 * @author aitorcay
 */
@Controller()
@RequestMapping("user/{id}/team")
public class UserTeamController {
	
	private static final Logger log = LogManager.getLogger(UserTeamController.class);
	
	@Autowired 
	private EntityManager entityManager;
	
	@Autowired
	private LocalData localData;

	/**
	 * Vista del equipo al que pertenece el estudiante
	 * 
	 * @param id		id del usuario loggeado
	 * @param model		modelo que contendrá la información
	 * @param session	sesión asociada al usuario
	 * @return			vista a mostrar
	 */
	@GetMapping("")
	public String team(@PathVariable long id, Model model, HttpSession session) {
		User u = entityManager.find(User.class, id);
		model.addAttribute("user", u);
		
		//Equipo al que pertenece el estudiante
		StTeam team = entityManager.find(StTeam.class, u.getTeam().getId());
		model.addAttribute("team", team);
		//Miembros del equipo
		List<User> members = entityManager.createNamedQuery("User.byTeam", User.class)
				.setParameter("teamId", u.getTeam().getId()).getResultList();
		model.addAttribute("members", members);
		//Lista de logros asociados al equipo
		List<Achievement> achievements = entityManager.createNamedQuery("Achievement.byTeam", Achievement.class)
				.setParameter("teamId", u.getTeam().getId()).getResultList();
		model.addAttribute("achievements", achievements);
		
		return "team";
	}
	
	/**
	 * Obtiene la foto de perfil de un equipo
	 * 
	 * @param teamId	id	id del usuario loggeado
	 * @param model		modelo que contendrá la información
	 * @return			foto de perfil
	 * @throws IOException
	 */
	@GetMapping(value="/{teamId}/photo")
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
	
	/**
	 * Actualiza la foto de perfil de un equipo
	 * 
	 * @param response	para gestión de las peticiones HTTP
	 * @param photo		nueva foto
	 * @param id		id del usuario loggeado
	 * @param teamId	id del equipo
	 * @param model		modelo que contendrá la información
	 * @param session	sesión asociada al usuario
	 * @return			vista a mostrar
	 * @throws IOException
	 */
	@PostMapping("/{teamId}/photo")
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
}