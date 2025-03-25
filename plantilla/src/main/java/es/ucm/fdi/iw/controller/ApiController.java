package es.ucm.fdi.iw.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.ucm.fdi.iw.model.Topic;
import es.ucm.fdi.iw.model.Message;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.model.User.Role;
import io.karatelabs.js.Context;
import io.karatelabs.js.Interpreter;
import io.karatelabs.js.Node;
import io.karatelabs.js.Parser;
import io.karatelabs.js.Source;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

/**
 * API, intended for logged-in users.
 *
 * Access to this end-point is NOT authenticated
 * - see SecurityConfig and add per-endpoint authentication as needed
 */

// RestController = all methods annotated with @ResponseBody by default, JSON
// in, JSON out
@RestController
@RequestMapping("api")
public class ApiController {

  @Autowired
  private EntityManager entityManager;

  private static final Logger log = LogManager.getLogger(ApiController.class);

  /**
   * Simple status test - returns whatever the message is
   * 
   * @param message
   * @return {"code" = "<message>"}
   */
  @GetMapping("/status/{message}")
  public Map<String, String> check(@PathVariable String message) {
    return Map.of("coder", message);
  }

  /**
   * Counts current users
   * 
   * @param message
   * @return {"code" = "<message>"}
   */
  @GetMapping("/users/count")
  public Map<String, Long> usersCount() {
    return Map.of("count",
        (Long) entityManager.createQuery("SELECT COUNT(u) FROM User u").getSingleResult());
  }


  /**
   * Loads a file from the classpath. 
   * This works even if the file is in a JAR.
   * @param path - path to the file - **relative to target/classes**
   * @return the file
   */
  private File loadFromClasspath(String path) {
      try {
          return ResourceUtils.getFile("classpath:"+path);
      } catch (FileNotFoundException e) {
          throw new RuntimeException("Could not load file from classpath: "+path, e);
      }
  }

  /**
   * Executes JS code using karate-js
   * @param text
   * @param vars
   * @return
   */
  private Object eval(String source, Map<String, Object> vars) {
    Parser parser = new Parser(new Source(source));
    Node node = parser.parse();
    Context context = Context.root();
    if (vars != null) {
        vars.forEach((k, v) -> context.declare(k, v));
    }
    return Interpreter.eval(node, context);
  }

  /** 
   * Executes JS code loaded from a file in the server
   */
  @GetMapping(value = "/js", produces = MediaType.APPLICATION_JSON_VALUE)
  public Map<String,String> testJs() throws Exception{
    String start = Files.readString(
      loadFromClasspath("static/js/js-eval.js").toPath());
    String source = start + "\n" + "f(v);";

    Object result = eval(source, Map.of(
      "v", 10, 
      "exampleExternalVar", "patata"));
    return Map.of("result", result.toString());
  }

  @Autowired
  private SimpMessagingTemplate messagingTemplate;

  /**
   * Posts a message to a topic.
   * 
   * @param topic of target user (source user is from ID)
   * @param o  JSON-ized message, similar to {"message": "text goes here"}
   * @throws JsonProcessingException
   */
  @PostMapping("/topic/{name}")
  @ResponseBody
  @Transactional
  public Map<String,String> postMsg(@PathVariable String name,
      @RequestBody JsonNode o, Model model, HttpSession session,
      HttpServletResponse response)
      throws JsonProcessingException {

    String text = o.get("message").asText();
    User sender = entityManager.find(
        User.class, ((User) session.getAttribute("u")).getId());
    Topic target = entityManager.createNamedQuery("Topic.byKey", Topic.class)
        .setParameter("key", name).getSingleResult();  

    // verify permissions
    if (! sender.hasRole(Role.ADMIN) && ! target.getMembers().contains(sender)) {
      response.setStatus(HttpServletResponse.SC_FORBIDDEN);
      return Map.of("error", "user not in group");
    }

    // build message, save to BD
    Message m = new Message();
    m.setRecipient(null);
    m.setSender(sender);
    m.setTopic(target);
    m.setDateSent(LocalDateTime.now());
    m.setText(text);
    entityManager.persist(m);
    entityManager.flush(); // to get Id before commit

    // send to topic & return
    String json = new ObjectMapper().writeValueAsString(m.toTransfer());
    log.info("Sending a message to  group {} with contents '{}'", target.getName(), json);
    messagingTemplate.convertAndSend("/topic/" + name, json);
    return Map.of("result", "message sent");
  }

    /**
   * Posts a message to a topic.
   * 
   * @param topic of target user (source user is from ID)
   * @param o  JSON-ized message, similar to {"message": "text goes here"}
   * @throws JsonProcessingException
   */
  @GetMapping("/topic/{name}")
  @ResponseBody
  @Transactional
  public Map<String,String> getMessages(@PathVariable String name, HttpSession session,
        HttpServletResponse response)
      throws JsonProcessingException {

      User requester = entityManager.find(
          User.class, ((User) session.getAttribute("u")).getId());
      Topic target = entityManager.createNamedQuery("Topic.byKey", Topic.class)
          .setParameter("key", name).getSingleResult();  
  
      // verify permissions
      if (! requester.hasRole(Role.ADMIN) && ! target.getMembers().contains(requester)) {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        return Map.of("error", "user not in group");
      } 
      // return result
      return Map.of("messages", new ObjectMapper().writeValueAsString(
        target.getMessages().stream()
          .map(Message::toTransfer).toArray()
      ));
  }
}
