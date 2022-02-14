# Proyecto de demo: un clon de kahoot

## instalando el entorno

Usaremos Visual Studio Code, con las siguientes extensiones:
- Extension Pack for Java: 0.12.1
- Spring Initizlizr Java Support: 0.8
- Spring Boot Dashboard: 0.2.0
- Live Share: 1.0.5273
- Lombok Annotations Support for VS Code: 1.0.1

Es importante instalar *todas* las extensiones, y tener un entorno de Java funcionando, antes de emprender los siguientes pasos.

Para el entorno Java+Maven, necesitaremos:

### Windows 10/11

En los laboratorios de la FDI tenemos limitada la instalación de recursos a nivel de sistema. Esto dificulta el uso de los instaladores java que vienen en la última versión oficial; en cualquier caso, el profesor recomienda usar la versión abierta ([OpenJDK](https://download.java.net/java/GA/jdk17.0.2/dfd4a8d0985749f896bed50d7138ee7f/8/GPL/openjdk-17.0.2_windows-x64_bin.zip)) en lugar de la [versión Oracle](https://www.oracle.com/java/technologies/downloads/#jdk17-windows).

En ordenadores de clase, uso

* [OpenJDK 11](https://download.java.net/openjdk/jdk11/ri/openjdk-11+28_windows-x64_bin.zip)
* [Maven 3.8](https://dlcdn.apache.org/maven/maven-3/3.8.4/binaries/apache-maven-3.8.4-bin.zip)

Para que funcionen desde línea de comandos, es necesario incluir sus ejecutables en la variable de sistema `PATH`:

- busca "variables de entorno" (o "environment variables" si te va el inglés)
- `PATH` debe incorporar las rutas de los ejecutables de **java**, **javac**, y **mvn**. Así, si estaban instaladas en `C:\code\openjdk-11+28_windows-x64_bin\jdk-11\bin` (java, javac) y `C:\code\apache-maven-3.8.4\bin` (maven), `PATH` debería incorporar ambas rutas
- `JAVA_HOME` debe apuntar a la ruta de la **JDK**, que es justo lo que hay antes de la carpeta `bin` con los ejecutables. Es decir, en el ejemplo anterior, `C:\code\openjdk-11+28_windows-x64_bin\jdk-11`

Si **no** tienes acceso a la configuración de variables de entorno, puedes usar las siguientes instrucciones en una consola tipo `powershell` (que es la que usa el terminal de VS Code). Tendrás que volver a introducirlas cada vez que abras un terminal:

~~~{.ps1}
$env:Path = 'C:\code\openjdk-11+28_windows-x64_bin\jdk-11\bin;C:\code\apache-maven-3.8.4\bin;' + $env:Path
$env:JAVA_HOME = 'C:\code\openjdk-11+28_windows-x64_bin\jdk-11'
~~~

### Linux

Puedes descargar OpenJDK y Maven como paquetes para muchas distribuciones. Yo uso la OpenJDK tal y como la empaqueta Ubuntu (paquetes `openjdk-11-jdk` y `openjdk-11-doc`); pero prefiero descargar maven por mi cuenta (https://dlcdn.apache.org/maven/maven-3/3.8.4/binaries/apache-maven-3.8.4-bin.zip), colocarlo en `/opt/maven`, y añadir enlaces simbólicos para sus ejecutables vía `ln -s /opt/maven/bin/* /usr/local/bin`.

## creando un nuevo proyecto



Emmet... => Initialzr

Spring Initializr
- spring 2.6.x (último que haya; 3.0 esperado para dentro de poco, pero JDK17 mínimo)
- es.ucm.fdi.iw
- qna
- java 11
- with
    - security
    - web
    - thymeleaf
    - websockets
    - jpa
    - h2
    - lombok

### añadiendo una vista, un html estático, y un controlador mínimo

Creamos un html tonto, con contenido 

~~~{.html}
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8"/>
    </head>
    <body>
        <!-- 
          nota: el atributo th:text no tiene efecto si thymeleaf no procesa el archivo;
          si thymeleaf *si* lo procesa, reemplazará el texto del elemento por el resultado de evaluarlo
        -->
        Probando: 1+1 = <span th:text="${1+1}">algo</span>

        <ul>
            <a href="estatico.html">estatico</a>
            <a href="dinamico.html">dinamico</a>
        </ul>
    </body>
</html>

~~~

- lo guardamos en resources/static/estatico.html
- lo guardamos en resources/templates/dinamico.html

- añadimos un controlador bajo java/es/ucm/fdi/iw/qna/controller/RootController.java, con contenido

~~~{.java}
    package es.ucm.fdi.iw.qna.controller;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.stereotype.Controller;

    @Controller
    public class RootController {
        @GetMapping("/")
        public String index() {
            return "dinamico";  // <-- nombres de templates. Con "estatico" habría dado un error 500
        }
    }
~~~

### primera prueba

Probamos que funciona: en consola/terminal, escribimos, desde el mismo directorio del pom.xml,

~~~{.sh}
    mvn spring-boot:run
~~~
(alternativamente: damos al play en spring-boot dashboard)

Y nos fijamos, en los logs, en la línea que dice algo así como 

~~~{.txt}
Using generated security password: f20af948-2804-485c-b285-f0d71cc7c848
~~~

Este valor será distinto cada vez que lances la aplicación. Si navegas a "localhost:8080", podrás ver la aplicación, y te pedirá un nombre de usuario y contraseña. Usa
usuario: "user"
contraseña: "f20af948-2804-485c-b285-f0d71cc7c848" (<-- el que te haya salido por los logs)

Deberás poder ver el resultado de interpretar dinamico.html como template.

### desactivando seguridad

Por defecto, si has incluido `spring-security`, tendrás que haber hecho login para acceder a cualquier página; y la contraseña será distinta cada vez. Seguiremos instrucciones de https://spring.io/guides/gs/securing-web/ para desactivar seguridad y cambiar la contraseña por defecto:

~~~{.java}
package es.ucm.fdi.iw.qna;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
				.antMatchers("/", "/*").permitAll()
				.anyRequest().authenticated()
				.and()
			.formLogin()
				.loginPage("/login")
				.permitAll()
				.and()
			.logout()
				.permitAll();
	}

	@Bean
	@Override
	public UserDetailsService userDetailsService() {
		UserDetails user =
			 User.withDefaultPasswordEncoder()
				.username("user")
				.password("password")
				.roles("USER")
				.build();

		return new InMemoryUserDetailsManager(user);
	}
}
~~~

Volvemos a lanzar la aplicación: ahora no genera usuarios/contraseñas nuevos, y podemos ver las páginas sin problemas.

### añadiendo preguntas y respuestas

Primero, pensaremos clases de modelo para contener preguntas y respuestas. Propongo las siguientes:

~~~{.java}

package es.ucm.fdi.iw.qna.model;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class Answer {
    private String text;
    private float value; // 0 = none, 1 = max, can be negative
}

~~~

~~~{.java}

package es.ucm.fdi.iw.qna.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Question {
    private String text;
    private List<Answer> answers = new ArrayList<>();

    public Question(String text, String ... answersAndValues) {
        this.text = text;
        for (String a : answersAndValues) {
            String[] parts = a.split("@");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Respuestas deben tener exactamente 1 @ separando texto y valor");
            }
            answers.add(Answer.builder().text(parts[0]).value(Float.parseFloat(parts[1])).build());
        }
    }

    public float grade(List<Integer> chosen) {
        float total = 0;
        for (int i=0; i<answers.size(); i++) {
            if (chosen.contains(i)) {
                total += answers.get(i).getValue();
            }
        }
        return total;
    }
}

~~~

Esto tiene la ventaja de que podemos crear un cuestionario sencillo en muy poco código:

~~~{.java}
    List<Question> qs = List.of(
        new Question("Cuánto es 1+1?", "1@-.2", "2@1", "3@-.2", "4@-.2"),
        new Question("Cuánto es 2+1?", "1@-.2", "2@-.2", "3@1", "4@-.2"),
        new Question("Cuánto es 2+2?", "1@-.2", "2@-.2", "3@-.2", "4@1")
    );
~~~

Y, si nos entra un array de enteros, es fácil de evaluar:

~~~{.java}
    total += q.grade(List.of(respuestas));
~~~

### vistas

Una para mostrar preguntas (y poder contestarlas). Podríamos haberlo hecho más breve y genérico usando th:foreach ...

~~~{.html}
    <!DOCTYPE html>
    <html>
        <head>
            <meta charset="UTF-8"/>
        </head>
        <body>
            <form>
                <span th:text="${pregunta.text}">Texto de la pregunta</span>
                <ul>
                    <li>
                        <input type="checkbox" name="respuestas" value="0"/>
                        <span th:text="${pregunta.answers[0].text}">Texto de una respuesta</span>     
                    </li>
                    <li>
                        <input type="checkbox" name="respuestas" value="1"/>
                        <span th:text="${pregunta.answers[1].text}">Texto de una respuesta</span>     
                    </li>
                    <li>
                        <input type="checkbox" name="respuestas" value="2"/>
                        <span th:text="${pregunta.answers[2].text}">Texto de una respuesta</span>     
                    </li>
                    <li>
                        <input type="checkbox" name="respuestas" value="3"/>
                        <span th:text="${pregunta.answers[3].text}">Texto de una respuesta</span>     
                    </li>
                </ul>
                <button type="submit">Enviar estas respuestas</button>
            </form>
        </body>
    </html>
~~~

Y otra para ver el resultado final. Uso algo de magia negra para formatear mejor los números; pero también habría funcionado (algo más feo) con `${total}`.

~~~{.html}
    <!DOCTYPE html>
    <html>
        <head>
            <meta charset="UTF-8"/>
        </head>
        <body>
            Has sacado un <span th:text="${#numbers.formatDecimal(total, 1, 'DEFAULT', 2, 'DEFAULT')}">cero patatero</span>
            ¿Quieres <a href="q">volver a intentarlo</a>?
        </body>
    </html>
~~~

### Y el controlador

La parte más complicada es el controlador -- que se parece bastante al de "adivina el número":

~~~{.java}
    private static final String PREGUNTA = "pregunta";
    private static final String TOTAL = "total";

    @GetMapping("/q")
    public String qna(HttpSession session, Model model, @RequestParam(required = false) Integer[] respuestas) {

        List<Question> qs = List.of(
            new Question("Cuánto es 1+1?", "1@-.2", "2@1", "3@-.2", "4@-.2"),
            new Question("Cuánto es 2+1?", "1@-.2", "2@-.2", "3@1", "4@-.2"),
            new Question("Cuánto es 2+2?", "1@-.2", "2@-.2", "3@-.2", "4@1")
        );


        Integer qi = (Integer)session.getAttribute(PREGUNTA);
        Float total = (Float)session.getAttribute(TOTAL);

        if (qi == null) { 
            // primera pregunta, no entran respuestas
            log.info("Primera pregunta para {}", session.getId());
            qi = 0;
            total = 0f;
            model.addAttribute(PREGUNTA, qs.get(qi));
    	} else if (qi < qs.size()-1) {
            log.info("Pregunta intermedia {} para {}, responde {}", qi, session.getId(), respuestas);
    		// entran respuestas de anterior, y hay pregunta siguiente
            Question q = qs.get(qi);
            if (respuestas != null) {
                total += q.grade(List.of(respuestas));
            }
            qi ++;
            model.addAttribute(PREGUNTA, qs.get(qi));
        } else {
            log.info("Ultima pregunta para {}, responde {}", session.getId(), respuestas);
    		// entran respuestas de anterior, y mostramos resultados
            Question q = qs.get(qi);
            if (respuestas != null) {
                total += q.grade(List.of(respuestas));
            }
            model.addAttribute(TOTAL, total);
            
            // para la siguiente iteración
            qi = null;
        }

        session.setAttribute(PREGUNTA, qi);
        session.setAttribute(TOTAL, total);

        return qi!=null ? "q" : "total";
    }
}
~~~