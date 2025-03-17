package es.ucm.fdi.iw.controller;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.EntityManager;


/**
 *  API, intended for logged-in users.
 *
 *  Access to this end-point is NOT authenticated
 *  - see SecurityConfig and add per-endpoint authentication as needed
 */

// RestController = all methods annotated with @ResponseBody by default, JSON in, JSON out
@RestController
@RequestMapping("api")
public class ApiController {

    @Autowired
    private EntityManager entityManager;

    private static final Logger log = LogManager.getLogger(ApiController.class);

    /**
     * Simple status test - returns whatever the message is
     * @param message
     * @return {"code" = "<message>"}
     */
	@GetMapping("/status/{message}")
    public Map<String,String> check(@PathVariable String message) {
        return Map.of("coder", message);
    }

    /**
     * Counts current users
     * @param message
     * @return {"code" = "<message>"}
     */
	@GetMapping("/users/count")
    public Map<String,Long> usersCount() {
        return Map.of("count", 
            (Long)entityManager.createQuery("SELECT COUNT(u) FROM User u").getSingleResult());
    }
}
