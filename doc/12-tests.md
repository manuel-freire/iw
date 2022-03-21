% Probando tu aplicación web
% (manuel.freire@fdi.ucm.es)
% 2022.03.21

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
- De **compatibilidad**: viendo a ver con qué plataformas son compatibles o no
    + servidor: versiones de java, sistemas operativos y versiones, ...
    + cliente: navegadores, dispositivos, ...
- De **seguridad**: buscando posibles vulnerabilidades
- De **carga**: probando a partir de qué volumen de datos/peticiones empieza a saturarse 

# Pruebas y automatización

> Un buen programador es un vago ingenioso

> Si vas a tener que hacerlo más de una vez, automatízalo \vskip
(DRY\footnote{don't repeat yourself} aplicado a pruebas)

- Pruebas de más a menos automatizables:
	+ de unidad (muy fácil de automatizar)
	+ integración (sí, navegador inclusive -- contra historias predefinidas)
	+ carga (contra patrones de carga predefinidos)
	+ compatibilidad (contra plataformas específicas)
	+ seguridad (contra amenazas conocidas)
	+ usabilidad (sólo probable con humanos, hoy por hoy)

. . . 

En esta asignatura nos concentraremos en pruebas de *unidad* y de *integración*

# Pruebas y código

Estructura tu código para que sea fácil de probar, siguiendo filosofía 	TDD (Test-Driven **Design**, que no **Development**)

- Principios aplicables
	* KISS - keep it simple, st*pid
	* SOLID - los de toda la vida:
		- Single Responsibility Principle: que cada cosa haga sólo 1 cosa, y la haga bien
		- Open-closed: abierto a extensión, cerrado a modificación
		- Liskov-substitution: una *subclase* debe poder substituir a sus *sus superclases* \
		sin romper el programa -- y si no, ese uso de herencia es sospechoso
		- Interface-segregation: expón interfaces mínimas a cada cliente\footnote{\emph{cliente} es el que usa algo - por ejemplo, el código que llama a una API es \emph{cliente} de esa API}, \
		en lugar de interfaces más exhaustivas
		- Dependency-inversion: depende de abstracciones, y no de elementos abstractos
	* YAGNI - _You aren't going to need it_, interpretado como \
	"no hagas pruebas innecesarias sólo por hacerlas"
- Ventajas de **código fácil de probar**
	- más fácil encontrar errores (porque puedes probarlo)
	- y reemplazarlo por otro si los tiene (por SRP)
	- mejor diseño (por KISS, SRP, YAGNI)

. . .

Es decir, aunque no acabes automatizando pruebas, el mero hecho de **estructurar tu código para que pueda ser probado** hace que sea mucho más mantenible, y por tanto mejor.

# Tabla de contenidos

- Pruebas unitarias con JUnit & Spring MVC
	- idea general
	- de clases particulares
	- con BD
	- con BD y controlador, simulando peticiones
- Pruebas de integración
	- alternativas: selenium
	- uso de Karate 

# Pruebas con JUnit 5 en Maven

- Para probar la clase X, que estará en `src/`**main**`/ruta/de/paquete/X.java`
    + creas una clase llamada XTest, en `src/`**test**`/ruta/de/paquete/XTest.java`
    + escribes métodos anotados con `@Test`
    + donde usas `assertAlgo` para hacer pruebas, o `fail` si decides que ha fallado
    + y lo pruebas vía `mvn test` (o vía IDE)
- Además, puedes usar anotaciones para indicar código pre- y post-test:
    + `@BeforeEach` y `@AfterEach`, que se ejecutan antes y después de pasar *cada* test del fichero
    + `@BeforeAll` y `@AfterAll`, que deben ser estáticos, y se ejecutan, respectivamente, *una* vez, antes de hacer cualquier cosa con la clase, o cuando ya se ha acabado todo lo que se iba a hacer con esa clase.

# excepciones y tiempos

~~~~ {.java}

@Test
public void shouldRaiseAnException() throws Exception {
    Assertions.assertThrows(Exception.class, () -> {
        // este código hará que el test falle, 
		// porque debería lanzar una excepción y no lo hace
    });
}

@Test
public void shouldFailBecauseTimeout() throws InterruptedException {
    Assertions.assertTimeout(Duration.ofMillis(1), () -> {
		// este código hará que el test falle: debería tardar < 1 ms
		Thread.sleep(10); // y tarda 10 ms
	});
}

~~~~

# probando clases aisladas con JUnit 5
    
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
	- IE: Chakra (hasta Edge)
	- Chrome, Edge, Brave: V8
	- Firefox: Spidermonkey
	- Java: Nashorn (JDK) / Rhino (Mozilla)
* Y los navegadores en sí también hacen cosas distintas

# Pruebas de integración: opciones

* Pruebas manuales
	- requieren mucha disciplina para hacer lo mismo una y otra vez
	- *¿¡qué somos, robots!?*
* Pruebas automáticas con selenium
	- puede lanzar varios navegadores
	- muy establecido
	- [sintaxis y conceptos complejos, fruto de 16 años de historia](https://hackernoon.com/the-world-needs-an-alternative-to-selenium-so-we-built-one-zrk3j3nyr)
	- difícil de integrar con nuestras aplicaciones (porque está pensado para integrarse con 5 tipos de entornos, y por tanto no es fácil en ninguno)
* Pruebas automáticas con [karate](https://github.com/intuit/karate)
	- escrito en Java, pensado para usarse con Java
	- integración muy limpia [con Spring MVC](https://github.com/Sdaas/hello-karate)
	- *mucho más* sencillo que selenium

# Añadiendo Karate

1. incorporad las nuevas clases de tests de la plantilla (bajo `src/test`):

\small

~~~{.text}
test/java/
		karate-config.js			// json para configurar karate
		logback-test.xml			// configuracion de logging para pruebas
		internal/                   // para agrupar pruebas "internas"
			InternalTests.java		// integración con JUnit
			api/					// agrupa pruebas sobre 1 funcionalidad
				users.feature		// llama a una api de pruebas con usuarios
				UsersRunner.java	// integración con JUnit
		external/                   // para agrupar pruebas "externas"
			ExternalRunner.java		// integración con JUnit
			login.feature			// prueba logins
			ws.feature				// otra historia ejecutable
~~~

\normal

3. para lanzar las pruebas, hay que usar
	- `mvn spring-boot:run` -- porque karate usa un navegador para hablar con vuestro servidor, que tiene que estar funcionando
	- `mvn test -Dtest=InternalRunner` ó `mvn test -Dtest=ExternalRunner` -- para lanzar las pruebas de `karate`, ya sean internas (usando HttpClient como navegador) ó externas (usando, por ejemplo, chrome)

# Un archivo .feature

~~~{.txt}
Feature: Hello World

  Background:
    Given url baseUrl            # ver karate-config.js
    Given path '/api/hello'

  Scenario: Hello world

    * method GET
    * status 200
    * match $ == "Hello world!"

  Scenario: Hello with a param

    * param name = 'Patata'
    * method GET
    * status 200
    * match $ == "Hello Patata!"
~~~

# Vocabulario

* `#` - empieza un comentario
* `Feature:` - un conjunto de escenarios relacionados
* `Scenario:` - una prueba, tipo `@Test` del tipo "entra por aquí, haz esto, espera que suceda esto otro":
	- `*` - detalles de petición (`method`, `param`, `form field`, ...), \
	o de lo que tiene que pasar (`status`, `assert`, `match`), \
	o de lo que quieres apuntar cuando mires los resultados (`print`)
	- También puedes usar `Given`, `And`, `When` y `Then` en lugar de `*`, o incluso omitirlos por completo: son sólo *azúcar sintáctica*
* `Background:` - trasfondo común a todos los escenarios (piensa en `@BeforeEach`)

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
    * def util = Java.type('karate.KarateTests')

Scenario: get login page, capture csrf, send login
    * path 'login'
    * method get
    * status 200
    # ... name="_csrf" value="0a7c65e8-4e8e-452f-ad44-40b995bb91d6"
    * def csrf = karate.extract(response, '"_csrf" value="([^"]*)"', 1) 

    * path 'login'
    * form field username = 'a'
    * form field password = 'aa'
    * form field _csrf = csrf
    * method post    
    * status 200
    * def h4s = util.selectHtml(response, "h4");
    * match h4s contains 'Usuarios'
~~~

- - - 

¿Qué es eso de `    * def csrf = karate.extract(response, '"_csrf" value="([^"]*)"', 1) 
`?

~~~{.java}
String html = "... name=\"_csrf\" value=\"0a7c65e8-4e8e-452f\" ... ";
String csrf = Pattern.compile("\"_csrf\" value=\"([^\"]*)\"")
					.matcher(html)
					.group(1); // --> 0a7c65e8-4e8e-452f
~~~

(Hora de repasar [expresiones regulares](https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html))

- - - 

También puedes usar unas pruebas desde otras, con \
`call read(pruebaAEjecutar@labelDeEscenario)`

Y puedes acceder al HTML, o al contenido de texto:
`And match text('#eg01DivId') == 'el contenido en texto de ese selector'`

~~~{.txt}

Background:
	* url baseUrl
	* call read('login.feature@login_como_a')

Scenario: user page
    * path 'user/1'
    * method get
    * print response
    * status 200
    * assert text('h4>span') == 'a'

~~~

# Karate-UI

* Pruebas externas - un navegador de verdad, y no `HttpClient`
	- HttpClient no interpreta ni ejecuta js, ni solicita recursos tipo CSS o favicon
	- HttpClient no entiende de websockets

* ¿Qué navegador?
	- cualquiera que tenga un RemoteWebDriver de [Selenium](https://www.selenium.dev/documentation/en/remote_webdriver/remote_webdriver_client/) / que implemente [WebDriver](https://www.w3.org/TR/webdriver/) - o el protocolo propio de Chrome
	- Karate-UI soporta [12 drivers distintos](https://github.com/intuit/karate/tree/master/karate-core#driver-types) (en su v1.0.1)

* Usando Chrome bajo Windows / Linux:

~~~{.txt}
  * configure driver = { type: 'chrome', showDriverLog: true }
~~~

# Diferencias con Karate "interno"

* Requiere que el servidor ya esté lanzado (¡puede usarse para probar servidores de 3eros!)
* Da acceso al dom tras modificaciones de JS, permite probar websockets
* Documentado en [karate-core](https://github.com/intuit/karate/tree/master/karate-core)
* Nuevos verbos/acciones:
	+ `screenshot` para sacar pantallazos (que luego salen en el informe)
	+ `scroll`, `click`, `fullscreen`, ... no tienen sentido sin pantalla y ratón
	+ Una [sintaxis de localizadores](https://github.com/intuit/karate/tree/master/karate-core#locators) bastante más fácil de usar que la de las pruebas internas

# Un ejemplo

\smallsize

~~~{.txt}
Given driver 'http://localhost:8080/login'
* input('#username', 'a')
* input('#password', 'aa')
* submit().click("button[type=submit]")
* match html('title') contains 'Admin'
* driver.screenshot()

# voy al perfil si pulso en su foto
* click("img[class=userthumb]")
* match html('title') contains 'Perfil'

# voy a mensajes si pulso en el buzon
* click("a[id=received]")
* match html('title') contains 'Mensajes'

# me auto-envio un mensaje (via ajax) con un número aleatorio
* def msg = script("'el secreto es ' + ((Math.random() * 1000) | 0)")
* input('#message', msg)
# ojo: lo envío sin submit():es ajax y no hay recarga de página
* click("button[id=sendmsg]")
# retardo de 500 ms para dar tiempo a recibir respuesta vía WS
* delay(500)                  

# y puedo leer el resultado 
* match html('#datatable') contains mensaje
* driver.screenshot()
~~~

\normalsize

# Fin

## ¿?

![](img/cc-by-sa-4.png "Creative Commons Attribution-ShareAlike 4.0 International License"){ width=25% }

This work is licensed under a [Creative Commons Attribution-ShareAlike 4.0 International License](https://creativecommons.org/licenses/by-sa/4.0/)

