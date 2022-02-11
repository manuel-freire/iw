package es.ucm.fdi.iw.qna.controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.util.List;

import javax.servlet.http.HttpSession;

import es.ucm.fdi.iw.qna.model.*;

@Controller
public class RootController {
    private static Logger log = LogManager.getLogger(
        RootController.class);
    
    @GetMapping("/")
    public String index() {
        return "dinamico";
    }

    private static final String PREGUNTA = "pregunta";
    private static final String TOTAL = "total";

    @GetMapping("/q")
    public String qna(HttpSession session, Model model, @RequestParam(required = false) Integer[] respuestas) {

        List<Question> qs = List.of(
            new Question("Cuánto es 1+1?", "1@-.2", "2@1", "3@-.2", "4@-.2"),
            new Question("Cuánto es 2+1?", "1@-.2", "2@-.2", "3@1", "4@-.2"),
            new Question("Cuánto es 2+2?", "1@-.2", "2@-.2", "3@-.2", "4@1")
        );


        Integer qi = (Integer)session.getAttribute(PREGUNTA);
        Float total = (Float)session.getAttribute(TOTAL);

        if (qi == null) { 
            // primera pregunta, no entran respuestas
            log.info("Primera pregunta para {}", session.getId());
            qi = 0;
            total = 0f;
            model.addAttribute(PREGUNTA, qs.get(qi));
    	} else if (qi < qs.size()-1) {
            log.info("Pregunta intermedia {} para {}, responde {}", qi, session.getId(), respuestas);
    		// entran respuestas de anterior, y hay pregunta siguiente
            Question q = qs.get(qi);
            if (respuestas != null) {
                total += q.grade(List.of(respuestas));
            }
            qi ++;
            model.addAttribute(PREGUNTA, qs.get(qi));
        } else {
            log.info("Ultima pregunta para {}, responde {}", session.getId(), respuestas);
    		// entran respuestas de anterior, y mostramos resultados
            Question q = qs.get(qi);
            if (respuestas != null) {
                total += q.grade(List.of(respuestas));
            }
            model.addAttribute(TOTAL, total);
            
            // para la siguiente iteración
            qi = null;
        }

        session.setAttribute(PREGUNTA, qi);
        session.setAttribute(TOTAL, total);

        return qi!=null ? "q" : "total";
    }
}