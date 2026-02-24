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
    
    @Autowired
    private AuthorsRepository authorRepository; 
    
    @GetMapping("/authors")
    public String listAuthors(Model model) {
        List<Authors> authors = authorRepository.findAll();
        System.out.println("Found " + authors.size() + " authors"); // Add this
        model.addAttribute("authorList", authors);
        return "authors"; // returns authors.html template
    }
}