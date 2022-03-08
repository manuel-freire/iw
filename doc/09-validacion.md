% Validación
% (manuel.freire@fdi.ucm.es)
% 2020.03.07

## Objetivo

> Validación en cliente y servidor para tu aplicación web

## Validación

* En el cliente (= el navegador), porque los usuarios
    + pueden no saber qué espera tu formulario
    + pueden no entender cómo lo espera, y equivocarse de formato
    + pueden despistarse, y olvidarse de introducir ciertos campos, o introducir información inconsistente.
    
* En el servidor (= tu aplicación web), porque
    + los usuarios pueden intentar romper tu aplicación
    + tu aplicación puede actuar como una API para otras aplicaciones, y éstas pueden estar mal programadas

. . . 

* Qué hacer con los errores
    + no proceses información en la que no confías, y
    + si estás en el cliente, **informa** y **ayuda** al usuario a corregir lo que esté mal
    + si estás en el servidor, usa códigos de estatus HTTP. Además, si piensas que
        - no es posible que esto suceda sin *malas intenciones*, **no** des información extra
        - crees que puede deberse a una confusión usando la API, **debes** informar algo más

## Mala validación

* Cosas a **no hacer** en validación **cliente**
    + usar `alert` para avisar de errores
        - los `alert` son molestos, y no muestran dónde tienes que hacer los cambios.
        - *mejor:* mensajes **junto a los controles** erróneos, donde haya que solucionarlo.
    + dar mensajes que se quejan de un problema, pero no dicen cómo solucionarlo
        - a no ser que tu aplicación consista en un juego de acertijos, \
        o que necesites que tu usuario se sienta culpable y no quiera usar tu aplicación
        - *mejor:* mensajes **que expliquen cómo solucionar el problema**

~~~{.js}
const mal   = 'identificador de objeto erróneo';
const mejor = 'este identificador debe corresponder a un objeto válido;'
    + 'haz click en el enlace para ver todos los objetos disponibles';
// y todavía mejor: usar un 'select' ó similar.
~~~

* Cosas a **no hacer** en validación **servidor**
    + confiar en que la validación cliente funciona.
        - la web es un sitio grande y salvaje. \
        - No puedes, **nunca**, fiarte de los clientes. Cualquiera puede apretar al F12.
    + olvidarte de elegir códigos de estado
        - usa bien `200`, `400`, `401`, ... - sobre todo si esperas consultas vía API.
    + dar demasiada información, que ya sabrían si fuesen gente honesta \
    (a no ser que estés ofreciendo una API)

## Validación en cliente

* Usa atributos html5 de validación. El navegador
    + no permite enviar formularios inválidos *(si sabe que lo son)*
    + muestra los errores *(según lo que sabe del error, y el idioma en el que esté configurado)*
    + marca controles válidos con `:valid`, y controles inválidos con `:invalid`, por si quieres estilarlos
    + Algunos atributos
        - `input="email"` - tiene que ser un correo válido
        - `pattern="[0-9]{3}"` - tiene que tener 3 dígitos

* Si esto no es suficiente, usa JS

~~~{.js}
const e = document.getElementById("fecha");
// marcando el campo como inválido, con mensaje para mostrar al usuario
e.setCustomValidity("Esta fecha no puede estar en el futuro");
// marcando el campo como válido
e.setCustomValidity("");
~~~

(Lee la documentación en la [MDN sobre validación](https://developer.mozilla.org/en-US/docs/Learn/HTML/Forms/Form_validation))

## Validación en cliente con JS

* Lo normal es validar el campo cada vez que se modifica
    - escribimos una función de validación
    - y hacemos que se llame cada vez que haya cambios

~~~{.js}
// f. de validación: control 'e' debe ser palíndromo
function validatePalindrome(e) {
    return () => {
       const reversed = Array.from(e.value)
          .reverse().join("");
       e.setCustomValidity(e.value === reversed ?
          "" : "must be a palindrome")
    }
}
~~~

---

~~~{.js}
// registramos que esto se llame cada vez que cambie
window.onload = () => {
    for (let e of document.querySelectorAll('.palindrome')) {
        e.oninput = e.onchange = validatePalindrome(e);
    }
}
~~~ 

~~~{.html}
<!-- en el html -->
<input class="palindrome" name="myPalindrome" required >
~~~

## Validación en servidor

* Los métodos de Spring MVC pueden hacer validación básica de parámetros:
    - asegurando que existen en la petición, y que no están vacíos
    - y que se puedan convertir a ciertos tipos Java

~~~ {.java}
@GetMapping("/car")
public String getCar(@RequestParam long id, Model model ) {
    log.info("Requesting info about car {}",  id);
    model.addAttribute("car", entityManager.find(Car.class, id));
    return "car";
}
~~~

* la anotación `@RequestParam` hace que el valor de `id` proceda de algún `<input name="id" ...`
* si el campo se deja vacío, el servidor generará un error 400:

- - - 

~~~{.html}
<!-- ejemplo de error generado por Spring MVC -->
<html><body><h1>Whitelabel Error Page</h1>
<p>
    This application has no explicit mapping for /error, 
    so you are seeing this as a fallback.
</p>
<div id='created'>
    Tue Mar 05 13:01:35 CET 2019
</div>
<div>
    There was an unexpected error (type=Bad Request, status=400).
</div>
<div>
    Required long parameter &#39;id&#39; is not present
</div>
</body></html>
~~~

- - - 

* También se pueden marcar como opcionales, o darles valores por defecto

~~~ {.java}
// id opcional
@GetMapping("/car")
public String getCar(@RequestParam(required=false) long id 
                /*...*/ ) { /* ... */ ; return "car"; }

// id opcional, y con valor por defecto -1
@GetMapping("/car")
public String getCar(@RequestParam(defaultValue="-1") long id
                /*...*/ ) { /* ... */ ; return "car"; }
~~~

## Validando objetos de modelo enteros

* Con la anotación `@ModelAttribute` podemos especificar un objeto entero a leer del formulario

~~~ {.java}
    // sin @ModelAttribute
	@PostMapping("/addCar1")
	@Transactional 
	public String addCar1(
			@RequestParam String company,
			@RequestParam String model, Model m) {
		Car car = new Car();
		car.setCompany(company);
		car.setModel(model);
		entityManager.persist(car);
	     
	    //Do Something
	    return dump(m);
	}
~~~

- - - 

~~~ {.java}
    // con @Valid, @ModelAttribute y @BindingResult
	public String addCar2(@Valid @ModelAttribute Car car, 
			BindingResult result,  ModelMap model, Model m) {
		if (result.hasErrors()) {
			log.warn("Validation errors: {}", 
					result.getAllErrors());
	        return "error";
	    }
		entityManager.persist(car);
	     
	    //Do Something
	    return dump(m);
	}
~~~

## Anotaciones para validación en servidor

~~~ {.java}
public class Car {
	private long id;
	private String company;
	
	// no considerará válidos coches con modelo a null o muy largo
	@NotNull
	@Size(max=10)
	private String model;
~~~

* Referencia sobre [anotaciones de validez en campos](https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html#validation-beanvalidation)

* Una guía de Spring sobre [validación en servidor](https://spring.io/guides/gs/validating-form-input/)

# Controladores

## Idea básica

[referencia normativa](https://docs.spring.io/spring-framework/docs/5.3.x/reference/html/web.html#mvc-controller)

~~~ {.java}

@Controller
@RequestMapping("/appointments")
public class AppointmentsController {
    // ...
}
~~~ 

## Tipos de peticiones

~~~ {.java}
@RequestMapping(value="/x", method=RequestMethod.GET)

@GetMapping("/x")

@PostMapping("/x)

@RequestMapping("/x")

@RequestMapping(path="/x", method={RequestMethod.GET, RequestMethod.POST})

@PostMapping(path="/pets", consumes="application/json")

@GetMapping(path = "/pets/{petId}", produces = "application/json") 
~~~

## Qué puede recibir una petición

[referencia normativa](https://docs.spring.io/spring-framework/docs/5.3.x/reference/html/web.html#mvc-ann-arguments)

- sesión
- modelo
- parámetros
- principal (= identidad del usuario)
- petición y respuesta
- ...

## Qué puede devolver una petición

- el nombre de una vista: `String`
- algo directamente: `@ResponseBody ... Algo`
- algo directamente, incluyendo cabeceras: `ResponseEntity<Algo>`
- una vista implícita: `View`
- ...

## Recibiendo parámetros: por argumentos

~~~ {.java}
    @GetMapping
    public String setupForm(@RequestParam("petId") int petId, Model model) { 
        Pet pet = this.clinic.loadPet(petId);
        model.addAttribute("pet", pet);
        return "petForm";
    }
~~~

## Recibiendo parámetros: por rutas

~~~ {.java}
    @GetMapping("/{petId}")
    public String setupForm(@PathVariable Long petId, Model model) { 
        Pet pet = this.clinic.loadPet(petId);
        model.addAttribute("pet", pet);
        return "petForm";
    }
~~~

# Fin

## ¿?

¡No te quedes con preguntas!

------

![](./img/cc-by-sa-4.png "Creative Commons Attribution-ShareAlike 4.0 International License"){ width=25% }

This work is licensed under a [Creative Commons Attribution-ShareAlike 4.0 International License](https://creativecommons.org/licenses/by-sa/4.0/)
