package es.ucm.fdi.iw.controller;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RootController {

    private static Logger log = LogManager.getLogger(
        RootController.class);

    private static final String OBJETIVO = "o";
    private static final String INTENTOS = "i";
    private static final String RESULTADO = "resultado";
    private final Random random = new Random();
    
    @GetMapping("/")            
    public String index(
    		HttpSession session,
            Model model,
            @RequestParam(required = false) Integer entero) {

    	Integer i = (Integer)session.getAttribute(INTENTOS);
    	Integer o = (Integer)session.getAttribute(OBJETIVO);
    	
    	if (o == null || i == -1) {
    		o = random.nextInt(11); // entre 0 y 10, ambos inclusive
    		i = 0; 
    		model.addAttribute(RESULTADO, "he pensado un número del 0 al 10 - ¿lo intentas adivinar?");
    	} else if (entero == null) {
    		model.addAttribute(RESULTADO, "¿vas a intentar adivinarlo, o qué? - llevas " + i + " intentos.");
    	} else {
    		i ++;
    		if (entero < o) {
        		model.addAttribute(RESULTADO, "el mío es más grande - y llevas " + i + " intentos.");
    		} else if (entero > o) {
    			model.addAttribute(RESULTADO, "el mío es más pequeño - y llevas " + i + " intentos.");
    		} else {
    			model.addAttribute(RESULTADO, "¡bingo! ¡era el " + o + "! - has necesitado " + i + " intentos... Ya he pensado otro");
    			i = 0; // resetea intentos
        		o = random.nextInt(11); // entre 0 y 10, ambos inclusive
    		}
    	}
    	
		session.setAttribute(INTENTOS, i);
		session.setAttribute(OBJETIVO, o);
    	
        log.info("El usuario dice que se llama {}", entero); 
        return "adivina";
    }
}
