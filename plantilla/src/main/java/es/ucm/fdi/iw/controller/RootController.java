package es.ucm.fdi.iw.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Data;

import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestBody;



/**
 *  Non-authenticated requests only.
 */
@Controller
public class RootController {

    private static final Logger log = LogManager.getLogger(RootController.class);

    @ModelAttribute
    public void populateModel(HttpSession session, Model model) {        
        for (String name : new String[] { "u", "url", "ws", "topics"}) {
          model.addAttribute(name, session.getAttribute(name));
        }
    }

	@GetMapping("/login")
    public String login(Model model, HttpServletRequest request) {
        boolean error = request.getQueryString() != null && request.getQueryString().indexOf("error") != -1;
        model.addAttribute("loginError", error);
        return "login";
    }

	@GetMapping("/")
    public String index(Model model) {
        return "index";
    }
  
  @GetMapping("/patata")
  public String getPatata() {
      return "patata";
  }


  /**
   * Post estándar, devuelve vista
   */
  @PostMapping(path="/patata", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public String postPatataStd(@RequestParam String type, @RequestParam int quantity) {      
      compraPatatas(type, quantity);
      return "patata";
  }  

  /**
   * Get con parámetros desde AJAX, devuelve JSON. 
   */
  @GetMapping(path="/patata2")
  @ResponseBody
  public String getPatata2(@RequestParam String type, @RequestParam int quantity) {      
      compraPatatas(type, quantity);
      return "{ \"patata\": \"" + type + "\", \"quantity\": \"" + quantity + "\" }";
  }

  /**
   * Post AJAX, devuelve JSON, espera multipart/form-data; podría funcionar con un 
   * <form enctype="multipart/form-data">, pero devuelve JSON pensando en JavaScript y FormData
   * 
   * OJO: por motivos históricos, Spring no soporta @RequestPart int quantity - usa String 
   * (por el lado bueno, esto sí aceptaría ficheros enteros!)
   */
  @PostMapping(path="/patata", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @ResponseBody
  public String postPatataAjax(@RequestPart String type, @RequestPart String quantity) {      
      compraPatatas(type, Integer.parseInt(quantity));
      return "{ \"patata\": \"" + type + "\", \"quantity\": \"" + quantity + "\" }";
  }  

  /**
   * Post AJAX que recibe y devuelve JSON. Para parsear JSON cómodamente, lo mejor es
   * definir una clase con los campos esperados
   */
  @Data
  static class PatataForm {
      private String type;
      private int quantity;  
  }
  @PostMapping(path="/patata", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public String postPatataJson(@RequestBody PatataForm p) {
      compraPatatas(p.getType(), p.getQuantity());
      return "{ \"patata\": \"" + p.getType() + "\", \"quantity\": \"" + p.getQuantity() + "\" }";

  }  

  private void compraPatatas(String type, int quantity) {
      log.info("Patatas compradas: " + type + " x " + quantity);
  }
}
