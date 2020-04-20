% Probando tu aplicación web
% (manuel.freire@fdi.ucm.es)
% 2020.04.20

## Objetivo

> Pruebas en una aplicación web

# Pruebas en una aplicación web

- De **unidad**: sobre elementos específicos, en aislamiento
    + sobre clases Java, las de toda la vida
    + sobre cambios en la BD, vía DbUnit
    + sobre el controlador, aislándolo de la BD
- De **integración**: sobre toda la web
    + enlaces, formularios, validación ...
    + flujo, autenticación, historias ...
- De **usabilidad**: con humanos, viendo a ver qué problemas o fricción encuentran para realizar tareas
- De **compatibilidad**: viendo a ver qué plataformas son compatibles o no
    + servidor: versiones de java, sistemas operativos y versiones, ...
    + cliente: navegadores, dispositivos, ...
- De **seguridad**: buscando posibles vulnerabilidades
- De **carga**: probando a partir de qué volumen de datos/peticiones empieza a saturarse 

# Pruebas y automatización

> Un buen programador es un vago ingenioso

> Si vas a tener que hacerlo más de una vez, automatízalo \vskip
(DRY aplicado a pruebas)

- Pruebas de más a menos automatizables:
	+ de unidad
	+ integración (sí, navegador inclusive -- contra historias predefinidas)
	+ carga (contra patrones de carga predefinidos)
	+ compatibilidad (contra plataformas específicas)
	+ seguridad (contra amenazas conocidas)
	+ usabilidad (sólo humanos, hoy por hoy)

# Pruebas y código

Estructura tu código para que sea fácil de probar, siguiendo filosofía 	TDD (Test-Driven **Design**, que no **Development**)

- Principios aplicables
	* KISS - keep it simple, st*pid
	* SRP - Single Responsibility Principle (parte de SOLID):
		- SRP: que cada cosa haga sólo 1 cosa, y la haga bien
		- Open-closed: abierto a extensión, cerrado a modificación
		- Liskov-substitution: una subclase debe poder substituir a sus superclases sin romperlas
		- Interface-segregation: expón interfaces mínimas a cada cliente, en lugar de interfaces más exhaustivas
		- Dependency-inversion: depende de abstracciones, y no de elementos abstractos
	* YAGNI - _You aren't going to need it_, interpretado como "no hagas pruebas innecesarias sólo por hacerlas"
- Ventajas de **código fácil de probar**
	- más fácil encontrar errores (porque puedes probarlo)
	- y reemplazarlo por otro si los tiene (por SRP)
	- mejor diseño (por KISS, SRP, YAGNI)

Es decir, aunque no acabes automatizando pruebas, el mero hecho de estructurar tu código para que pueda ser probado hace que sea mucho más mantenible, y por tanto mejor

# Tabla de contenidos

- Pruebas unitarias con JUnit & Spring MVC
	- idea general
	- de clases particulares
	- con BD
	- con BD y controlador, simulando peticiones
- Pruebas de integración
	- alternativas: selenium
	- uso de Karate 

# Pruebas con JUnit en Maven

- Para probar la clase X, que estará en `src/`**main**`/ruta/de/paquete/X.java`
    + creas una clase llamada XTest, en `src/`**test**`/ruta/de/paquete/XTest.java`
    + escribes métodos anotados con `@Test`
    + donde usas `assertAlgo` para hacer pruebas, o `fail` si decides que ha fallado
    + y lo pruebas vía `mvn test` (o vía IDE)
- Además, puedes usar
    + `@Test(expected = WeirdException.class)`, que falla si no lanza `WeirdException`
    + `@Test(timeout=100)`, que falla si pasan más de 100 segundos sin salir de la prueba
    + `@Before` y `@After`, que se ejecutan antes y después de pasar *cada* test del fichero
    + `@BeforeClass` y `@AfterClass`, que deben ser estáticos, y se ejecutan, respectivamente, *una* vez, antes de hacer cualquier cosa con la clase, o cuando ya se ha acabado todo lo que se iba a hacer con esa clase.

# probando clases aisladas con JUnit
    
~~~~ {.java}
// en src/main/java/es/ucm/fdi/iw/model/CGroup.java
package es.ucm.fdi.iw.model;
public class CGroup {
    // ...
	public static String createRandomId() {
		return String.format("%06d", 
				ThreadLocalRandom.current().nextLong(1_000_000));
	}
}

// en src/test/java/es/ucm/fdi/iw/model/CGroup.java
package es.ucm.fdi.iw.model;
public class CGroupTest {
	@Test
	public void test() {
		String r1 = CGroup.createRandomId();
		assertTrue(r1.matches("[0-9]+"));
		
		String r2 = CGroup.createRandomId();
		assertFalse(r1.equals(r2)); // ¡falla ~1 de cada 1000 veces!
	}
}
~~~~

# probando modelo y BBDD con JUnit & Spring

~~~~ {.java}
@RunWith(SpringRunner.class)
@SpringBootTest(classes = IwApplication.class)
public class IwUserDetailsServiceTest {

	@Autowired private EntityManager entityManager;	
	@Autowired private IwUserDetailsService iwUserDetailsService;

	@Test(expected=UsernameNotFoundException.class)
	public void testInvalidUser() {
	    assertTrue(entityManager != null);
		iwUserDetailsService.loadUserByUsername(
			"a user that does not exist"); 
	}

	@Test
	public void testValidUser() {
		iwUserDetailsService.loadUserByUsername("a"); 
	}
}
~~~~

# probando el controlador con JUnit, Spring, y servidor


\small
~~~~ {.java}
@RunWith(SpringRunner.class)
@SpringBootTest(
		webEnvironment = WebEnvironment.RANDOM_PORT,
		classes = IwApplication.class)
public class ApiControllerTest {
    @LocalServerPort private int port;
	@Autowired private WebApplicationContext wac;	
	private MockMvc mockMvc;	
	@Before
	public void setup() throws Exception {
	    this.mockMvc = MockMvcBuilders
			.webAppContextSetup(this.wac).build();
	}

	@Test
	public void aSimpleTest() throws Exception {
	    MvcResult mvcResult = this.mockMvc.perform(get("/api/status/test"))
	      .andDo(print()).andExpect(status().isOk())
	      .andExpect(jsonPath("$.code").value("test"))
	      .andReturn();	     
	    Assert.assertEquals("application/json;charset=UTF-8", 
	      mvcResult.getResponse().getContentType());
	}
}
~~~~ 

# Limitaciones de las pruebas anteriores

* Usan thymeleaf para vistas, pero no saben nada de JS ni navegadores
* Para probar JS, necesitaríamos elegir qué motor emular:
	- IE: Chakra
	- Chrome: V8
	- Firefox: Spidermonkey
	- Java: Nashorn (JDK) / Rhino (Mozilla)
* Y los navegadores en sí también hacen cosas distintas

# Pruebas de integración: opciones

* Pruebas manuales
	- requieren mucha disciplina para hacer lo mismo una y otra vez
* Pruebas automáticas con selenium
	- puede lanzar varios navegadores
	- muy establecido
	- [sintaxis y conceptos complejos, fruto de 16 años de historia](https://hackernoon.com/the-world-needs-an-alternative-to-selenium-so-we-built-one-zrk3j3nyr)
	- difícil de integrar con nuestras aplicaciones (porque está pensado para integrarse con 5 tipos de entornos, y por tanto no es fácil en ninguno)
* Pruebas automáticas con [karate](https://github.com/intuit/karate#quickstart)
	- escrito en Java, pensado para usarse con Java
	- integración muy limpia [con Spring MVC](https://github.com/Sdaas/hello-karate)
	- mucho más sencillo que selenium

# Añadiendo Karate

1. modificad el pom para incorporar cambios a la plantilla
	- nuevas librerías: junit, mockito, karate-apache + karate-junit4
	- sección build: cambios en configuración de pruebas
2. añadid pruebas en carpeta `test/java/`, con estructura

\small

~~~{.text}
test/java/
	karate-config.js			// json para configurar karate
	logback.xml					// configuracion de logging para pruebas
	karate/	                    // para agrupar pruebas de karate
		KarateTests.java		// lanza todos los tests de karate
		login/					// agrupa pruebas sobre 1 funcionalidad
			login1.feature		// cuenta una historia ejecutable
			login2.feature		// otra historia ejecutable
			LoginRunner.java	// para explicar a karate cómo probarlas
		...
~~~

\normal

3. para lanzar las pruebas, hay que usar
	- `mvn spring-boot:run` -- porque karate usa un navegador para hablar con vuestro servidor, que tiene que estar funcionando
	- `mvn test -Dtest=KarateTests` -- para lanzar las pruebas en sí

# Un archivo .feature

~~~{.txt}
Feature: Hello World

  Background:
    Given url baseUrl            # ver karate-config.js
    Given path '/api/hello'

  Scenario: Hello world

    When method GET
    Then status 200
    And match $ == "Hello world!"

  Scenario: Hello with a param

    Given param name = 'Daas'
    When method GET
    Then status 200
    And match $ == "Hello Daas!"
~~~

# Vocabulario

* `#` - empieza un comentario
* `Feature:` - un conjunto de escenarios relacionados
* `Scenario:` - una prueba, del tipo "entra por aquí, haz esto, espera que suceda esto otro":
	- `Given` - detalles de petición
	- `When` - petición en sí
	- `Then` - lo que tiene que pasar
* `Background:` - trasfondo común a todos los escenarios

Esta sintaxis viene de [Gherkin/Cucumber](https://cucumber.io/docs/gherkin/reference/), y constituye un DSL (_Domain-Specific Language_) mucho más legible que el equivalente en java. 

# Una prueba más compleja: una API Json

~~~{.txt}
Feature: Create and Read persons ...

  Background:
    * url baseUrl
    * def personBase = '/api/person/'

  Scenario: Create a person

    Given path personBase
    And request { firstName: 'John', lastName: 'Doe', age: 30 }
    And header Accept = 'application/json'
    When method post
    Then status 200
    And match response == 0			# id asignado, que esperan sea 0

  Scenario: Get person we just created

    Given path personBase + '0'		# id usado aquí
    When method GET
    Then status 200
    And match response == { firstName: 'John', lastName: 'Doe', age: 30 }
~~~

# Usando karate en nuestra aplicación: el login

~~~{.txt}
Feature: csrf and sign-in end point

Background:
* url baseUrl

Given path 'login'
When method get
Then status 200
* def csrf = //input[@name="_csrf"]/@value

#<html lang="en">...<body><div><form>
#   <input name="_csrf" type="hidden" value="..." />

Scenario: html url encoded form submit - post
    Given path 'login'
    And form field username = 'a'
    And form field password = 'aa'
    And form field _csrf = csrf
    When method post
    Then status 200
    * string response = response
    And match response contains 'Plantilla de IW'
~~~

- - - 

~~~{.txt}
Background:
* url baseUrl
* call read('login1.feature')

Scenario: user page

    Given path 'user/1'
    When method get
    Then status 200
    * string response = response
    And match response contains 'Información del usuario <span>a'
~~~

# XPath

* Otro DSL\footnote{Domain Specific Language}, similar a los selectores de CSS.
* Usa `//` para inicio del selector. Algunos ejemplos:

~~~{.js}
    xpath_locator = '//div[@class="button-section col-xs-12 row"]'
    css_locator = 'div.button-section.col-xs-12.row'

	css_locator = '#table1 tbody tr td:nth-of-type(4)'
	xpath_locator = "//table[@id='table1']//tr/td[4]"
~~~

* Ejemplos de [esta respuesta](https://stackoverflow.com/a/50292127/15472) en StackOverfow
* Un buen [tutorial de XPath]()
* Una herramienta para probar [XPath de forma interactiva](https://extendsclass.com/xpath-tester.html)

# XPath mínimo

~~~{.js}
/*
<html>
<head>
  <title>This is a title</title>
  <meta content="text/html; charset=utf-8" http-equiv="content-type" />
</head>
<body>
  <div>
    <div>
      <p>This is a paragraph.</p>
      <p>Is this <a href="page2.html">a link</a>?</p>
*/

//a/@href 						=> ["page2.html", "page3.html"]
//a[@href="page2.html"]/text() 	=> "a link"
~~~

# XML vs HTML

* Sólo podeis usar XPath si la página es XML válido
* Esto parece ser una limitación de Karate, que os hará reescribir parte de vuestras páginas
* Por ahora, he cambiado el login de la plantilla para que sea XML válido... 

# Fin

## ¿?

![](img/cc-by-sa-4.png "Creative Commons Attribution-ShareAlike 4.0 International License"){ width=25% }

This work is licensed under a [Creative Commons Attribution-ShareAlike 4.0 International License](https://creativecommons.org/licenses/by-sa/4.0/)

