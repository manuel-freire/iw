package es.ucm.fdi.iw.control;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import es.ucm.fdi.iw.model.Message;
import es.ucm.fdi.iw.model.Transferable;
import es.ucm.fdi.iw.model.User;

/**
 * User-administration controller
 * 
 * @author mfreire
 */
@Controller()
@RequestMapping("message")
public class MessageController {
	
	private static final Logger log = LogManager.getLogger(MessageController.class);
	
	@Autowired 
	private EntityManager entityManager;
		
	@GetMapping("/")
	public String getMessages(Model model, HttpSession session) {
		model.addAttribute("users", entityManager.createQuery(
			"SELECT u FROM User u").getResultList());
		return "messages";
	}

	@GetMapping(path = "/received", produces = "application/json")
	@Transactional // para no recibir resultados inconsistentes
	@ResponseBody  // para indicar que no devuelve vista, sino un objeto (jsonizado)
	public List<Message.Transfer> retrieveMessages(HttpSession session) {
		long userId = ((User)session.getAttribute("u")).getId();		
		User u = entityManager.find(User.class, userId);
		log.info("Generating message list for user {} ({} messages)", 
				u.getUsername(), u.getReceived().size());
		return  u.getReceived().stream().map(Transferable::toTransfer).collect(Collectors.toList());
	}	
	
	@GetMapping(path = "/unread", produces = "application/json")
	@ResponseBody
	public String checkUnread(HttpSession session) {
		long userId = ((User)session.getAttribute("u")).getId();		
		long unread = entityManager.createNamedQuery("Message.countUnread", Long.class)
			.setParameter("userId", userId)
			.getSingleResult();
		session.setAttribute("unread", unread);
		return "{\"unread\": " + unread + "}";
	}
}