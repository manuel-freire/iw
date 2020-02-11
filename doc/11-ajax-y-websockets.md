% Uso de AJAX y websockets
% (manuel.freire@fdi.ucm.es)
% 2019.03.18

## Objetivo

> Uso de AJAX y websockets

# AJAX

- **A**synchronous **J**avascript **A**nd **X**ml
    + intercambio de datos entre cliente y servidor *sin* recargar página
    + en un comienzo, XML; ahora se usa mucho más con JSON
    


# JSP vs PHP

- El uso de ficheros JSP es similar al que se hace de ficheros PHP ó ASP: permiten incluir código en páginas web.
- En Java, está mal visto incluir código general:

~~~~ {.jsp}
    <%-- Java: FEO, NO HACER --%>
    <% for (int i=0; i<10; i++) { %>
        <li> <%= ""+ i %> 
    <% } >
~~~~ 

~~~~ {.php}
    <?
    // PHP: Ok, es lo normal 
    for ($i=0; $i<10; $i++) { ?>
        <li> <?= $i ?>
    <? } ?>
~~~~ 

- En lugar de eso, usamos etiquetas especiales para control, y un lenguaje de expresión ("EL") para acceder al modelo u otras variables.

# JSP vs Thymeleaf

- Thymeleaf ("hoja de tomillo") no permite cosas tan feas: **sólo hace vistas**
- Y además, **es html válido**: puede verse sin desplegarlo

~~~~ {.jsp}
    <!-- muestra id, login de un iterable "users" en jsp -->
    <c:forEach items="${users}" var="u">
        <tr>
        <td>${u.id}
        <td>${u.login}
        </tr>	
    </c:forEach>
~~~~ 

~~~~ {.html}
    <!-- mismo ejemplo con thymeleaf -->
    <tr th:each="user: ${users}">
        <td th:text="${user.id}">1234</td>
        <td th:text="${user.login}">Pepe</td>
    </tr>
~~~~ 

# Primer contacto

- Instala STS 3.9+ sobre Java 8+
- Spring Starter project con 
    + Security
    + JPA
    + HSQLDB
    + Thymeleaf
    + Web
    + Websockets
- mvn spring-boot:run > user 6b92fb5c-cbb2-4114-bb1c-0f4225e70dec (ó equivalente)
- 404

# Integrando Thymeleaf

- https://www.thymeleaf.org/doc/tutorials/3.0/thymeleafspring.html
  innecesario, viene bien configurado de fábrica usando spring-boot-starter-thymeleaf

- https://www.thymeleaf.org/doc/articles/springsecurity.html describe que se usa una 
  alternativa al viejo spring security taglib (los taglibs son JSP-específicos):

~~~~ {.html}
    <!-- mismo ejemplo con thymeleaf -->
    <tr th:each="user: ${users}">
        <td th:text="${user.id}">1234</td>
        <td th:text="${user.login}">Pepe</td>
    </tr>
~~~~ 

# Spring Security

- Muy difícil hacer login bien
    + manejo de contraseñas
    + local vs SSO vs federado
    + cómo evitas abusos: CSRF, ...
- Spring security se encarga de evitar lo peor
- A cambio, pide
    + que dejes el login en sus manos
    + que le digas qué roles hay

ref: https://docs.spring.io/spring-security/site/docs/5.1.3.RELEASE/reference/htmlsingle/#hello-web-security-java-configuration
    
# Desconectando Spring Security

# Thymeleaf

Basado en https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html

~~~~ {.html}
    <!-- th:xyz es ahora parte del namespace así definido -->
    <html xmlns:th="http://www.thymeleaf.org">
~~~~

Esto sirve principalmente para evitar que el IDE se queje, y los navegadores tampoco protestan mucho si lo encuentran. No obstante, para html (thymeleaf puede gestionar también XML, texto, ...), también se puede usar 

~~~~ {.html}
    <!-- th:xyz equivale a data-th-xyz -->
    <p th:text="#{home.welcome}">Welcome to our grocery store!</p>
    <p data-th-text="#{home.welcome}">Welcome to our grocery store!</p>
~~~~

Internacionalización (i18n)

Buscando en ficheros de propiedades en /templates/xyz.html:

    /WEB-INF/templates/xyz_en.properties for English texts.
    /WEB-INF/templates/xyz_es.properties for Spanish language texts.
    /WEB-INF/templates/xyz_pt_BR.properties for Portuguese (Brazil) language texts.
    /WEB-INF/templates/xyz.properties for default texts (if the locale is not matched).

# text y utext

~~~~ {.properties}
home.welcome=Welcome to our <b>fantastic</b> grocery store!
~~~~

~~~~ {.html}
<p>Welcome to our &lt;b&gt;fantastic&lt;/b&gt; grocery store!</p>
<p th:utext="#{home.welcome}">Welcome to our grocery store!</p>

<!-- una vez se muestra -->
<p>Welcome to our &lt;b&gt;fantastic&lt;/b&gt; grocery store!</p>
<p>Welcome to our <b>fantastic</b> grocery store!</p>
~~~~

# Expresiones simples

- Variable Expressions: `${...}`
- Selection Variable Expressions: `*{...}`
- Message Expressions: `#{...}`
- Link URL Expressions: `@{...}`
- Fragment Expressions: `~{...}`

# Literales


- Text literals: 'one text', 'Another one!',…
- Number literals: 0, 34, 3.0, 12.3,…
- Boolean literals: true, false
- Null literal: null
- Literal tokens: one, sometext, main,…

# Operadores

- String concatenation: +
- Literal substitutions: |The name is ${name}|
- Binary operators: +, -, *, /, %
- Minus sign (unary operator): -
- Boolean binary operators: and, or
- Boolean negation (unary operator): !, not
- Comparators: >, <, >=, <= (gt, lt, ge, le)
- Equality operators: ==, != (eq, ne)

# Condicionales y NOP

- If-then: (if) ? (then)
- If-then-else: (if) ? (then) : (else)
- Default: (value) ?: (defaultvalue)
- No-Operation: _

# OGNL

Object-Graph Navigation Language

http://commons.apache.org/proper/commons-ognl/language-guide.html

- nombres de propiedad `usuario`
- llamadas a métodos `usuario.id`
- índices de iterables `usuario.amigos[0]`

# Servlets y JSPs

- Un servidor de aplicaciones (ej.: Tomcat, Pivotal TC) responde a peticiones HTTP devolviendo respuestas HTTP
    + generalmente, páginas HTML
    + pero también JSON, o cualquier otro elemento
- El servidor de aplicaciones acoge uno o más "Servlets", que son clases Java que procesan peticiones HTTP.
- Los servlets se pueden pasar la pelota (digo la petición) entre ellos de forma interna.
- **Cuando se accede a un JSP, éste se compila automáticamente a un servlet**.

- - - 

~~~~ {.java}
    // un servlet sencillo 
    public final class Hello extends HttpServlet {
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
      throws IOException, ServletException {
        response.setContentType("text/html");
        PrintWriter writer = response.getWriter();        
    
        writer.println("<html><head><title>Hola</title></head>")
        writer.println("<body>Hola mundo!</body></html>");
    }
~~~~ 

- - - 

~~~~ {.jsp}
    <%-- El JSP equivalente --%>
    <html><head><title>Hola</title></head>
    <body>Hola mundo!</body></html>
~~~~ 

# Servlets, JSPs y Spring MVC

- Tus métodos del controlador de SpringMVC actúan como servlets

- Si devuelves un String _xyz_ desde un manejador del controlador, Spring MVC entenderá, que, al finalizar, debe mostrar webapp/WEB-INF/views/xyz.jsp

- Parámetros importantes:
    + HttpServletRequest
    + HttpServletResponse
    + HttpSession
    + Model
    
- - - 

- HttpServletRequest: información de la petición (parámetros que te pasan, IP del navegador, ...)

- HttpServletResponse, tendrás acceso a la respuesta que estás preparando (y podrás añadir códigos de error, cambiar el tipo MIME del mensaje, ...)

- - - 

- HttpSession, **sesión del usuario**, un almacén importante para mantener el estado de la aplicación entre peticiones del mismo usuario.

- Model, almacén temporal que te permite compartir información con las vistas que procesan una petición concreta (el almacén se inicializa a vacío en cada nueva petición).

# Tags básicos en JSP

- Es útil asegurarte de que la codificación y el tipo MIME son correctos. Ojo: ésto hace falta repetirlo en cada JSP ó fragmento:
~~~~ {.jsp}
<%@ page contentType="text/html; charset=UTF-8"%>
~~~~

~~~~ {.jsp}
<%@ include file="../fragments/header.jspf" %>
~~~~

# JSTL

- Proporcionan estructuras de control básicas
    - iteración: forEach
    - condicionales: if, choose

- Muchas más facilidades, que no vamos a usar 
    - c:out para escapar HTML, ...

- Debes asegurarte de incluir la librería de tags para poder usarlos:

~~~~ {.jsp}
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
~~~~

- - - 

- Usa forEach para iterar por todos los elementos de una colección (array, lista, mapa, ...)


~~~~ {.jsp}
    <c:forEach var="i" items="${elementos}">
	<li>${i}</li>
    </c:forEach>
~~~~

- - - 

- Usa if para condiciones simples


~~~~ {.jsp}
    <c:if test="${i > 0}">
        ${i} es mayor que zero!
    </c:if>
~~~~

- - -

- Usa choose para condiciones complejas:

~~~~ {.jsp}
    <c:choose>
        <c:when test="${i > 0}">
            ${i} es mayor que zero!
        </c:when>
        <c:otherwise>
            ${i} es zero o negativo!
        </c:otherwise>
    </c:choose>
~~~~

# Ejemplos JSP EL

- ${titulo} -> sacado de Model ó Session
- ${persona.apellidos}
- ${miArray[5]}
- ${miHashMap["pedro"]}

- - -

- ${1 > (4/2)} -> false
- ${4.0 >= 3} -> true
- ${100.0 == 100} -> true
- ${(10*10) ne 100} -> false
- ${'a' < 'b'} -> true
- ${'hip' gt 'hit'} -> false
- ${4 > 3} -> true
- ${1.2E4 + 1.4} -> 12001.4
- ${3 div 4} -> 0.75
- ${10 mod 4} -> 2

- - -

- ${!empty param.Add} -> falso si parámetro Add es null ó ""
- ${param['mycom.productId']} -> valor del parámetro "mycom.productId"
- ${header["host"]} -> la URL de tu servidor (sólo hasta el /)
- ${departments[deptName]} -> el valor de _deptName_ dentro del mapa _departments_

# Recursos Flex

- Intro sencilla: https://css-tricks.com/snippets/css/a-guide-to-flexbox/
- Similar a los BoxLayout de Java Swing. Es decir, ok, pero requiere más anidamiento para cosas complicadas.

# Recursos Grid

- Intro sencilla: https://css-tricks.com/snippets/css/complete-guide-grid/
- Similar a los GridBagLayout de Java Swing (es decir, algo arcano)

# Alinemiento en Flex

- `margin-x: auto`: funciona tanto en filas como en columnas, para forzar mover al final del todo algo. Usado para login y para el footer. Hay muchas más formas de conseguirlo (pero son, a mi gusto, menos bonitas).

# Planes

- Escribir un modelo básico
- Permitir crear clases vía formulario
- Y borrarlas y tal
- Conectarlo con un websocket para poder votar y ver
- Añadir administración de profesores
- Añadir poder subir fotos de profes
- Añadir poder descargar csvs de acciones
- Añadir BD real.

# Sobre websockets

https://docs.spring.io/spring/docs/5.0.0.BUILD-SNAPSHOT/spring-framework-reference/html/websocket.html


https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#websocket


~~~
GET /spring-websocket-portfolio/portfolio HTTP/1.1
Host: localhost:8080
Upgrade: websocket 
Connection: Upgrade 
Sec-WebSocket-Key: Uc9l9TMkWGbHFD2qnFHltg==
Sec-WebSocket-Protocol: v10.stomp, v11.stomp
Sec-WebSocket-Version: 13
Origin: http://localhost:8080
~~~

~~~
HTTP/1.1 101 Switching Protocols 
Upgrade: websocket
Connection: Upgrade
Sec-WebSocket-Accept: 1qVdfYHU9hPOl4JYYNXF623Gzn0=
Sec-WebSocket-Protocol: v10.stomp
~~~

Descripción normativa:
https://tools.ietf.org/html/rfc6455

https://developer.mozilla.org/en-US/docs/Web/API/WebSockets_API




# Fin

## ¿?

![](img/cc-by-sa-4.png "Creative Commons Attribution-ShareAlike 4.0 International License"){ width=25% }

This work is licensed under a [Creative Commons Attribution-ShareAlike 4.0 International License](https://creativecommons.org/licenses/by-sa/4.0/)

