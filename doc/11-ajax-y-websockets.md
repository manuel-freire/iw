% Uso de AJAX y websockets
% (manuel.freire@fdi.ucm.es)
% 2020.03.17

## Objetivo

> Uso de AJAX y websockets

# AJAX

## Palabro

- **A**synchronous **J**avascript **A**nd **X**ml
    + intercambio de datos entre cliente y servidor *sin* recargar página
    + en un comienzo, XML; ahora se usa mucho más con JSON
    + en un comienzo, asíncrono (= no bloqueante) \
     (aunque, si quieres, puedes bloquearte esperando a la respuesta)

## Orígenes

- objeto `XmlHttpRequest` expuesto por IE 5.0 ('99) para hacer peticiones "de fondo"
- usando JS para interpretar sus resultados (que se suponían serían XML)
- Evolución:

    - Copiado por Mozilla en el '00, Safari\footnote{Google Chrome aparece en el '08, basado en WebKit, el motor de Safari; luego se divorciaron, y ahora Blink $\neq$ WebKit, aunque ambos descienden del mismo tronco} en el '04, ...
    - Estandarizado por el W3C en el '06
    - Mejoras al estándar (progreso, ...) en el '08
    - Incluido en HTML5 por WhatWG en el '12

## Evolución

Los tiempos oscuros. Sacado de [stackoverflow.com/a/16800864](https://stackoverflow.com/a/16800864)

~~~{.javascript}
// detección para ver qué tipo de Ajax había disponible
if (typeof XMLHttpRequest === "undefined") {
  XMLHttpRequest = function () {
    try { return new ActiveXObject("Msxml2.XMLHTTP.6.0"); }
    catch (e) {}
    try { return new ActiveXObject("Msxml2.XMLHTTP.3.0"); }
    catch (e) {}
    try { return new ActiveXObject("Microsoft.XMLHTTP"); }
    catch (e) {}
    // Microsoft.XMLHTTP points to Msxml2.XMLHTTP and is redundant
    throw new Error(":-(");
  };
}
~~~

---

JQuery resolvía ésta y otras incompatibilidades entre navegadores, con múltiples [métodos de utilidad para Ajax](https://api.jquery.com/category/ajax/):

~~~{.javascript}
// envía un formulario de forma automática por post
$.post( "test.php", $( "#testform" ).serialize() );

// solicita por get algo que tiene un tiempo y hora
$.get( "test.php", { func: "getNameAndTime" },  function( data ) {
  console.log( data.name ); // John
  console.log( data.time ); // 2pm
}, "json");

// parecido, pero en breve
$.getJSON('test.php', (d) => console.log(d);
~~~

---

Los estándares mejoraron, y apareció [`fetch`](https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API/Using_Fetch), que tiene un formato bastante similar, pero basado en promesas asíncronas

~~~{.javascript}
// mediante fetch, sin librerías externas
fetch('http://example.com/movies.json')
  .then((response) => {
    return response.json();
  })
  .then((data) => {
    console.log(data);
  });
~~~

(no [disponible](https://caniuse.com/#feat=fetch) en IE, pero sí en Edge y todos los navegadores modernos)

## Usando fetch

Podeis elegir usar `JQuery`\footnote{Si lo haceis, usad una copia descargada en local y servida desde /\texttt{js}}. En los ejemplos siguientes, uso Fetch.

- Moderno, usa promesas para gestionar asincronía
- No requiere librerías externas

## Envoltorio sencillo para fetch+JSON

\small

~~~{.javascript}
// envía json, espera json de vuelta; lanza error si status != 200
function go(url, method, data = {}) {
  let params = {
    method: method, // POST, GET, POST, PUT, DELETE, etc.
    headers: {
      "Content-Type": "application/json; charset=utf-8",
    },
    body: JSON.stringify(data)
  };
  if (method === "GET") {
	  // GET requests cannot have body
	  delete params.body;
  }
  console.log("sending", url, params)
  return fetch(url, params).then(response => {
    if (response.ok) {
        return data = response.json();
    } else {
        response.text().then(t => {throw new Error(t + ", at " + url)});
    }
  })
}
~~~

## Uso del fetch anterior

~~~{.javascript}
function login(uid, pass) {
  return go(serverApiUrl + "login", 'POST', 
        {uid: uid, password: pass})
    .then(d => {serverToken = d.token; updateState(d); return d;})
    .catch(e => console.log(e))
}
~~~

Sacado de una [API Ajax](https://github.com/manuel-freire/iu1920/blob/master/server/src/main/resources/static/js/gbapi.js#L277) usada en IU 2019-20. Por abajo había Spring Boot, pero no usaba seguridad ni websockets.

## Introducción a promesas JS

* fetch devuelve una **promesa**
* las promesas son **asíncronas** - no hay garantía de que el código que viene después se vaya a ejecutar después de que la promesa se resuelva:

~~~{.javascript}
fetch('https://no-such-server.blabla') // seguramente falle
  .then(response => response.json())
  .then(json => console.log(json.nombre_y_apellidos))
  .catch(e => console.log(e))          // y por tanto se haga ésto

// es casi seguro que el fetch todavía habrá acabado al llegar aquí
console.log("Me imprimo antes de que el fetch acabe")
~~~

* si hay fallos, se salta directamente al **`catch`**
* mientras no hay fallos, se van ejecutando los **`then`** en orden, cada uno usando el `return` del anterior como argumento de entrada.
* los datos del primer `then` son de tipo [`response`](https://developer.mozilla.org/en-US/docs/Web/API/Response)
 
## Cosas que podemos sacar un objeto `Response`

* `Response.status` - estado HTTP
* `Response.ok` - true $\iff$ `Response.status == 200`
* `Response.body` - cuerpo de la respuesta. \
Se le pueden sacar las [siguientes promesas](https://developer.mozilla.org/en-US/docs/Web/API/Body):
    * `.blob()` - contenido en binario, leído de un único golpe
    * `.json()` - contenido interpretado como json
    * `.text()` - contenido como texto plano

## Complejidades

Con Ajax es posible emular cualquier petición. Y con HTTP es posible hacer muchos tipos de peticiones. Por ejemplo,

- El método puede ser `GET`, `POST`, `PUT`, `DELETE`, ...
- Las peticiones pueden incluir todo tipo de cabeceras
- Los datos a enviar y recibir pueden ser cualquier cosa. 
    - Para enviarlos, hay que informar de lo que envías, y de cómo lo envías. Por defecto, usa
    `application/x-www-form-urlencoded; charset=UTF-8`
    - Para recibirlos correctamente, hay que saber qué recibes y poder interpretarlo.

## Cosas típicas que puedes recibir

- Nada. Porque a menudo basta con ver el\
código de estado del resultado para saber si la petición ha funcionado o no
- `texto`. Y lo muestras por consola o lo usas para reemplazar un elemento
- `html`. Y luego lo muestras reemplazando el contenido de cualquier elemento
- `json`. Máxima flexibilidad

## Usando AJAX

En la asignatura, usaremos Ajax para:

- **Validación**: por ejemplo, para evitar que los usuarios intenten registrarse con logins ya existentes. 

- **Mensajería**: ¿recargar toda la página para enviar un mensaje?\
¡Estamos en 2020!.

- **Carga dinámica de resultados**: Paginar los resultados es una posibilidad. Pero usar algún tipo de carga dinámica es mucho más amigable.

- **Botones**: Borrar un elemento de un listado no debería requerir recargar todo el listado.

En general, siempre que quieras no recargar página (pero hacer una petición), un fetch estaría justificado. Y más si la página es cara de construir (ejemplo: listado)

## Ajax para validación

Recordamos el código de validación estándar:

~~~{.javascript}
// manejador que se ejecuta cuando la página se carga
document.addEventListener("DOMContentLoaded", () => {
    // selector para elegir sobre qué elementos validar
	let u = document.querySelectorAll('#username')[0]
    // cada vez que cambien, los revalidamos
    u.oninput = u.onchange = 
        () => u.setCustomValidity(  // si "", válido; si no, inválido
            validaUsername(u.value))
}
~~~~

---

Y ahora, el ajax (asumimos uso de función `go` anterior)

~~~{.java}
function validaUsername(username) {
    let params = {username: username};
    // Spring Security inyecta esto en formularios html, pero no en Ajax
    params[config.csrf.name] = config.csrf.value;
    // petición en sí
    return go(config.apiUrl + "/username", 'GET', params)
        .then(d => "")
        .catch(() => "nombre de usuario inválido o duplicado");
}
~~~~

---

Y en el servidor, un manejador normal y corriente (el servidor no distingue entre Ajax y no-Ajax; excepto en que no tiene sentido devolver vistas para peticiones que no las van a mostrar)

~~~{.java}
@GetMapping("/username")
public String getUser(@RequestParam (required=false) String uname) {
    User u = entityManager.find(User.class, id);
    model.addAttribute("user", u);
    return "user";
}
~~~~

## Ajax para carga dinámica de resultados

(falta por rellenar)

## Ajax para botones

En general, el lado cliente es sencillo...

~~~{.html}
<!-- Sin Ajax -->
<td><form method="post" 
  th:action="@{/admin/toggleuser(id=${u.id})}">
  <button type="submit"
     th:text="${u.enabled eq 1 ? 'inactivar' : 'activar'}" ></button>
</form>

<!-- Ajaxificado -->
<td><form method="post" 
  th:action="@{/admin/toggleuser(id=${u.id})}">
  <button type="submit"
     class="toggle" th:attr="data-el_id=${element.getId()}" 
     th:text="${u.enabled eq 1 ? 'inactivar' : 'activar'}" ></button>
</form>
~~~

~~~{.js}
document.addEventListener("DOMContentLoaded", () => {
	let bs = document.querySelectorAll('.toggle')
    // falta: enviar la petición y gestionar el resultado
}
~~~

---

... y el lado servidor, todavía más:

~~~{.java}
// sin Ajax: devuelve una vista
@PostMapping("/toggleuser")
@Transactional
public String delUser(Model model, @RequestParam long id) {
    User target = entityManager.find(User.class, id);
    // ...
    // y devuelve una vista
    return index(model);
}	    

// con Ajax: sólo devuelve status ok, y un texto mínimo
@PostMapping("/toggleuser")
@Transactional
@ResponseBody // <-- devuelve un texto literal
public String delUser(Model model,	@RequestParam long id) {
    // ...
    // y devuelve un texto literal, y no un nombre de vista
    return "ok";
}
~~~

# Websockets

## Idea

* Comunicación bidireccional sin bloqueos ("tipo socket")
* Muy útil para mensajería instantánea, juegos
* Basada en renegociar protocolos sobre una conexión HTTP(S) ya establecida

* Ejemplo de renegociación:

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


## STOMP

* La necesidad
    - Por defecto, no hay protocolo de mensajes sobre Websockets
    - Puedes enviar cualquier cosa; y recibir cualquier cosa. ¿Cómo poner orden?

* El palabro
    - **S**treaming **T**ext-Oriented **M**essaging **P**rotocol
    - Otra acepción: sonido que hace dar un pisotón

* El protocolo
    - imita marcos ("frames") HTTP en texto, delimitados por bytes a 0 ('\0')
    - al igual que HTTP, sus _frames_ tienen
        - varias posibles peticiones (COMMAND)
        - 0 o más cabeceras de tipo _clave_:_valor_
        - una línea en blanco
        - un contenido textual (posiblemente vacío)
        - un carácter nulo (aquí uso `^@`)

Ejemplo de frame:

~~~{.txt}
    COMMAND
    header1:value1
    header2:value2

    Body^@
~~~

## Comandos STOMP Cliente

* `SEND` - envía un mensaje a quien diga `destination`
* `SUBSCRIBE` - se suscribe a mensajes de `destination`, y recibe un `id`
* `UNSUBSCRIBE` - usa el `id` anterior para darse de baja
* Sólo para gestión de transacciones:
    - `BEGIN` - inicia una transacción, con un id dado por `transaction`
    - `COMMIT` - lanza la transacción
    - `ABORT` - la cancela
* `ACK` - confirma una recepción (identificada por su `id`)
* `NACK` - para pedir que el servidor haga como que algo no ha sido recibido
* `DISCONNECT` - para salir bien (y luego espera a recibir un `RECEIPT`)

## Comandos STOMP Servidor

* `MESSAGE` - información de suscripciones (o de punto-a-punto) al cliente
* `RECEIPT` - para confirmar que una _frame_ que lo requiere ha sido recibida
* `ERROR` - para notificar errores

## Una conversación típica

Con el cliente a la izquierda, el servidor a la derecha, y 
`^@` de carácter nulo

\tiny

~~~{.txt}
Cliente     Servidor
|           | 
V           V

CONNECT
accept-version:1.0,1.1,2.0
host:stomp.github.org

^@
            CONNECTED
            version:1.1

            ^@
SEND
destination:/queue/a
content-type:text/plain

hello queue a
^@
DISCONNECT
receipt:77

^@
            RECEIPT
            receipt-id:77

            ^@
~~~

## STOMP vs no-STOMP

* No es un protocolo muy pesado
* Cubre muchos escenarios de uso frecuente: 
    - unicast, y multicast vía suscripciones
    - negociación, confirmaciones
    - "heartbeat" para mantener conexiones abiertas
* Soporte en Spring MVC, y en navegador sólo requiere algo de JS
* Soporte para securizarlo vía Spring Security
* Posible extenderlo en funcionalidad con colas de mensajes

## Configurando Websockets

* Cada página que quiera usarlos tiene que solicitar la conexión vía JS
    - necesita la dirección `ws://aplicacion/ruta`
    - necesita cargar una librería STOMP para gestionar el protocolo
* El servidor tiene que ofrecer esas rutas, y saber gestionarlas
* Necesita también 

## Lo que usaremos de STOMP

* Subscripciones para
    - administradores haciendo seguimiento de la aplicación
    - grupos de usuarios interesados en cosas que surgen
* Mensajería punto-a-punto
* Permisos (no todo el mundo se puede suscribir a todo)\
  vía Spring (no es parte de STOMP)

## Tipos de rutas

* `/topic/algo` - suscripciones a canales

~~~{.javascript}
// en cliente
ws.stompClient.subscribe('/topic/welcome', 
        (m) => ws.receive(JSON.parse(m.body).content));
~~~

~~~{.javascript}
// en un controlador
@Autowired
private SimpMessagingTemplate messagingTemplate;

// en un controlador, dentro del @XyzMapping
messagingTemplate.convertAndSend("/topic/welcome",
        "{texto: \"hola mundo\"}");
~~~

## Suscripción desde un cliente

~~~{.javascript}
ws.stompClient.subscribe('/topic/welcome', 
        (m) => ws.receive(JSON.parse(m.body).content));
~~~

## Controlando la suscripción en el servidor

~~~{.java}
@Configuration
public class WebSocketSecurityConfig
      extends AbstractSecurityWebSocketMessageBrokerConfigurer { 

	
    protected void configureInbound(\
            MessageSecurityMetadataSourceRegistry messages) {
        messages
            .simpSubscribeDestMatchers("/topic/admin")	// solo admines
            	.hasRole(User.Role.ADMIN.toString())
            .anyMessage().authenticated(); 				// login requerido
    }
}
~~~

## Rutas de usuario



https://www.baeldung.com/spring-security-websockets

https://dzone.com/articles/rest-api-error-handling-with-spring-boot

## Referencias para websockets

* Websockets según [Spring Framework](https://docs.spring.io/spring/docs/current/spring-framework-reference/html/websocket.html)


* Más docs de [Spring MVC](https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#websocket)

* Según el estándar de la [IETF](https://tools.ietf.org/html/rfc6455)


* Api de Websockets, [según Mozilla](https://developer.mozilla.org/en-US/docs/Web/API/WebSockets_API)


# Fin

## ¿?

![](img/cc-by-sa-4.png "Creative Commons Attribution-ShareAlike 4.0 International License"){ width=25% }

This work is licensed under a [Creative Commons Attribution-ShareAlike 4.0 International License](https://creativecommons.org/licenses/by-sa/4.0/)

