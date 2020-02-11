% Probando tu aplicación web
% (manuel.freire@fdi.ucm.es)
% 2019.03.25

## Objetivo

> Pruebas de integración en una aplicación web

# Pruebas en una aplicación web

- Unitarias: sobre elementos específicos, en aislamiento
    + sobre clases Java, las de toda la vida
    + sobre cambios en la BD, vía DbUnit
    + sobre el controlador, aislándolo de la BD
- De integración: sobre toda la web
    + enlaces, formularios, validación ...
    + flujo, autenticación, historias ...
- De usabilidad: con humanos, viendo a ver qué problemas o fricción encuentran para realizar tareas
- De compatibilidad: viendo a ver qué plataformas son compatibles o no
    + servidor: versiones de java, sistemas operativos y versiones, ...
    + cliente: navegadores, dispositivos, ...
- De seguridad: buscando posibles vulnerabilidades

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

# Ejemplo sencillo: `@Test` 
    
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
		assertFalse(r1.equals(r2));
	}
}
~~~~

# Ejemplo sencillo: `@Test(Exception)` 

~~~~ {.java}
@RunWith(SpringRunner.class)
@SpringBootTest(classes = IwApplication.class)
public class IwUserDetailsServiceTest {

	@Autowired private EntityManager entityManager;	
	@Autowired private IwUserDetailsService iwUserDetailsService;

	@Test(expected=UsernameNotFoundException.class)
	public void testInvalidUser() {
	    assertTrue(entityManager != null);
		iwUserDetailsService.loadUserByUsername("a user that does not exist"); 
	}

	@Test()
	public void testValidUser() {
		iwUserDetailsService.loadUserByUsername("a"); // exists by default 
	}
}
~~~~

# Pruebas de integración: simulando peticiones

~~~~ {.java}
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = IwApplication.class)
public class ApiControllerTest {
    @LocalServerPort private int port;
	@Autowired private WebApplicationContext wac;	
	private MockMvc mockMvc;	
	@Before
	public void setup() throws Exception {
	    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	public void givenGreetURI_whenMockMVC_thenVerifyResponse() throws Exception {
	    MvcResult mvcResult = this.mockMvc.perform(get("/api/status/test"))
	      .andDo(print()).andExpect(status().isOk())
	      .andExpect(jsonPath("$.code").value("test"))
	      .andReturn();	     
	    Assert.assertEquals("application/json;charset=UTF-8", 
	      mvcResult.getResponse().getContentType());
	}
}
~~~~ 

# Fin

## ¿?

![](img/cc-by-sa-4.png "Creative Commons Attribution-ShareAlike 4.0 International License"){ width=25% }

This work is licensed under a [Creative Commons Attribution-ShareAlike 4.0 International License](https://creativecommons.org/licenses/by-sa/4.0/)

