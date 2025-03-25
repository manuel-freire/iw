package es.ucm.fdi.iw.controller;

import java.util.List;
import java.util.stream.Collectors;

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

import es.ucm.fdi.iw.model.Topic;
import es.ucm.fdi.iw.model.Lorem;
import es.ucm.fdi.iw.model.Message;
import es.ucm.fdi.iw.model.Transferable;
import es.ucm.fdi.iw.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

/**
 * Site administration.
 *
 * Access to this end-point is authenticated - see SecurityConfig
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
    for (String name : new String[] { "u", "url", "ws", "topics"}) {
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

  /**
   * Returns JSON with all received messages
   */
  @GetMapping(path = "all-messages", produces = "application/json")
  @Transactional // para no recibir resultados inconsistentes
  @ResponseBody // para indicar que no devuelve vista, sino un objeto (jsonizado)
  public List<Message.Transfer> retrieveMessages(HttpSession session) {
    TypedQuery<Message> query = entityManager.createQuery("select m from Message m", Message.class);
    query.setMaxResults(5);
    query.setFirstResult(0); // para paginar: cambias el 1er resultado
    // devuelve resultado
    return query.getResultList().stream().map(Transferable::toTransfer)
        .collect(Collectors.toList());
  }

  @RequestMapping("/populate")
  @ResponseBody
  @Transactional
  public String populate(Model model) {

    // create some groups
    Topic g1 = new Topic();
    g1.setName("g1");
    g1.setKey(UserController.generateRandomBase64Token(6));
    entityManager.persist(g1);
    Topic g2 = new Topic();
    g2.setName("g2");
    g2.setKey(UserController.generateRandomBase64Token(6));
    entityManager.persist(g2);

    // create some users & assign to groups
    for (int i = 0; i < 15; i++) {
      User u = new User();
      u.setUsername("user" + i);
      u.setPassword(passwordEncoder
          .encode("aa"));
            //UserController.generateRandomBase64Token(9)));
      u.setEnabled(true);
      u.setRoles(User.Role.USER.toString());
      u.setFirstName(Lorem.nombreAlAzar());
      u.setLastName(Lorem.apellidoAlAzar());
      entityManager.persist(u);
      if (i%2 == 0) {
        g1.getMembers().add(u);
        // u.getTopics().add(g1); NO FUNCIONA: propietario es g, no u
      }
      if (i%3 == 0) {
        g2.getMembers().add(u);
      }
    }
    return "{\"admin\": \"populated\"}";
  }
}
