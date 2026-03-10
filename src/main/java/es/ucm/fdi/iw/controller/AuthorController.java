package es.ucm.fdi.iw.controller;

import es.ucm.fdi.iw.model.Authors;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.repository.AuthorsRepository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthorController {
    
    private static final Logger log = LogManager.getLogger(AuthorController.class);

    @Autowired
    private AuthorsRepository authorRepository; 
    
    @GetMapping("/authors")
    public String listAuthors(Model model) {
        List<Authors> authors = authorRepository.findAll();

        log.debug("Listado de autores solicitado. {} autores encontrados", authors.size());
        
        model.addAttribute("authorList", authors);
        return "authors"; // returns authors.html template
    }
}