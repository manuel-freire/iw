package es.ucm.fdi.iw.controller;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.ucm.fdi.iw.model.Label;
import es.ucm.fdi.iw.model.Restaurante;
import es.ucm.fdi.iw.model.User;

/**
 *  Non-authenticated requests only.
 */
@Controller
public class RootController {

    @Autowired
	private EntityManager entityManager;

	private static final Logger log = LogManager.getLogger(RootController.class);

    @GetMapping("/restaurante")
    public String restaurante(Model model, @RequestParam long id) {
        String query = "Select x From Restaurante x Where id="+id;
        Restaurante restaur = (Restaurante) entityManager.createQuery(query).getSingleResult();
        model.addAttribute("restaurante", restaur);
        return "restaurante";
    }

    @GetMapping("/platos")
    public String platos(Model model) {
        return "platos";
    }

    @GetMapping("/perfilRestaurante")
    public String perfilRestaurante(Model model){
        return "perfilRestaurante";
    }


	@GetMapping("/login")
    public String login(Model model) {
        return "login";
    }

    @GetMapping("/repartidor")
    public String repartidor(Model model) {
        return "repartidor";
    }

    @GetMapping("/listaPedidos")
    public String listaPedidos(Model model) {
        return "listaPedidos";
    }

    @GetMapping("/carrito")
    public String carrito(Model model) {
        return "carrito";
    }

    @GetMapping("/pedidoCliente")
    public String pedidoCliente(Model model) {
        return "pedidoCliente";
    }

	@GetMapping("/")
    public String index(Model model) {
        
        List<Restaurante> availableRestaurants = entityManager.createQuery("SELECT x FROM Restaurante x").getResultList();
        model.addAttribute("availableRestaurants", availableRestaurants);

        List<String> filterOptions = new ArrayList<>();
        filterOptions.add("Sin Filtro");
        filterOptions.add("Favoritos");
        filterOptions.add("Precio Ascendente");
        filterOptions.add("Precio Descendente");
        filterOptions.add("Populares");
        
        List<Label> labelOptions = entityManager.createQuery("Select x from Label x").getResultList();

        model.addAttribute("filterOptions",filterOptions);
        model.addAttribute("labelOptions",labelOptions);
        return "index";
    }
}
