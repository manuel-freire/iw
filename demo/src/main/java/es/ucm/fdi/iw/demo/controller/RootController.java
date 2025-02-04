package es.ucm.fdi.iw.demo.controller;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
public class RootController {

    private static Logger log = LogManager.getLogger(
        RootController.class);

    private static final String OBJETIVO = "o";
    private static final String INTENTOS = "i";
    private final Random random = new Random();
    
    @GetMapping("/")
    public String index() {
	return "index";
    }

    @GetMapping("/game")            
    public String game(
    		HttpSession session,
            Model model,
            @RequestParam(required = false) Integer entero) {

    	Integer i = (Integer)session.getAttribute(INTENTOS);
    	Integer o = (Integer)session.getAttribute(OBJETIVO);
    	String respuesta = null;

    	if (o == null || i == -1) {
    		o = random.nextInt(11); // entre 0 y 10, ambos inclusive
    		i = 0; 
			/*
			 * si esto sale feo en una consola de Windows PowerShell, copia y pega esto en la consola:
			 * $OutputEncoding = [Console]::InputEncoding = [Console]::OutputEncoding =
                    New-Object System.Text.UTF8Encoding
			 * (ver https://stackoverflow.com/a/49481797/15472)
			 */
    		respuesta = "he pensado un número del 0 al 10 - ¿lo intentas adivinar?";
    	} else if (entero == null) {
			respuesta = "¿vas a intentar adivinarlo, o qué? - llevas " + i + " intentos.";
    	} else {
    		i ++;
    		if (entero < o) {
				respuesta = "el mío es más grande - y llevas " + i + " intentos.";
    		} else if (entero > o) {
				respuesta =  "el mío es más pequeño - y llevas " + i + " intentos.";
    		} else {
    			respuesta = "¡bingo! ¡era el " + o + "! - has necesitado " + i + " intentos... y ya he pensado en otro";
    			i = 0; // resetea intentos
        		o = random.nextInt(11); // entre 0 y 10, ambos inclusive
    		}
    	}
    	
		session.setAttribute(INTENTOS, i);
		session.setAttribute(OBJETIVO, o);
    	
        log.info("\n -- El usuario dice: '{}'!\n -- Yo respondo '{}'", entero, respuesta); 
		model.addAttribute("respuesta", respuesta);

		return "adivina";
    }
}
