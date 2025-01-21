package es.ucm.fdi.iw.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class DemoApplicationTests {	

	@Test
	void contextLoads() {
		/*
		 * Prueba muy sencilla que funciona si todo "carga correcto"
		 */
	}


	@Autowired
	private MockMvc mockMvc;

	@Test
	void challengeMessageReceived() throws Exception {
		/*
		 * Prueba más complicada que hace petición y verifica respuesta 
		 */
		mockMvc.perform(get("/"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("Intenta adivinarlo")));
	}
}
