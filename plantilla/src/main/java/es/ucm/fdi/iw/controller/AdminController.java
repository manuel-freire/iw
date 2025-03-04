package es.ucm.fdi.iw.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.ucm.fdi.iw.model.Lorem;
import es.ucm.fdi.iw.model.User;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

/**
 *  Site administration.
 *
 *  Access to this end-point is authenticated - see SecurityConfig
 */
@Controller
@RequestMapping("admin")
public class AdminController {

	@Autowired
	private PasswordEncoder passwordEncoder;

    @Autowired
    private EntityManager entityManager;

    @ModelAttribute
    public void populateModel(HttpSession session, Model model) {        
        for (String name : new String[] {"u", "url", "ws"}) {
            model.addAttribute(name, session.getAttribute(name));
        }
    }

    private static final Logger log = LogManager.getLogger(AdminController.class);

	@GetMapping("/")
    public String index(Model model) {
        log.info("Admin acaba de entrar");
        model.addAttribute("users", 
            entityManager.createQuery("select u from User u").getResultList());
        return "admin";
    }

	@PostMapping("/toggle/{id}")
    @Transactional
    @ResponseBody
    public String toggleUser(@PathVariable long id, Model model) {
        log.info("Admin cambia estado de " + id);
        User target = entityManager.find(User.class, id);
        target.setEnabled(!target.isEnabled());
        return "{\"enabled\":" + target.isEnabled() + "}";
    }

    @PostMapping("/populate")
    @ResponseBody
    @Transactional
    public String populate(Model model) {
        for (int i=0; i<10; i++) {
            User u = new User();
            u.setUsername("user" + i);
            u.setPassword(passwordEncoder
                .encode(UserController
                    .generateRandomBase64Token(9)));
            u.setEnabled(true);
            u.setRoles(User.Role.USER.toString());
            u.setFirstName(Lorem.nombreAlAzar());
            u.setLastName(Lorem.apellidoAlAzar());
            entityManager.persist(u);
        }
        return "{\"admin\": \"populated\"}";
    }
}
