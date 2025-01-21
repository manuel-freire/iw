package es.ucm.fdi.iw.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpServletRequest;

/**
 *  Non-authenticated requests only.
 */
@Controller
public class RootController {

	private static final Logger log = LogManager.getLogger(RootController.class);

	@GetMapping("/login")
    public String login(Model model) {
        return "login";
    }

	@GetMapping("/")
    public String index(Model model) {
        return "index";
    }
    
    @ModelAttribute("requestURI")
    public String requestURI(final HttpServletRequest request) {
        return request.getRequestURI();
    }
    
    @ModelAttribute("requestURL")
    public String requestURL(final HttpServletRequest request) {
        return request.getRequestURL().toString();
    }
    
    @ModelAttribute("userAgentHeader")
    public String userAgentHeader(final HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }

    @ModelAttribute("requestQueryString")
    public String requestQueryString(final HttpServletRequest request) {
        return request.getQueryString();
    }
}
