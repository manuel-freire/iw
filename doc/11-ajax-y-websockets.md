% Uso de AJAX y websockets
% (manuel.freire@fdi.ucm.es)
% 2022.03.15

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

    - Copiado por Mozilla en el '00, Safari\footnote{Google Chrome aparece en el '08, basado en WebKit, el motor de Safari; luego se divorciaron, y ahora Blink (Google)$\neq$ WebKit (Safari), aunque ambos descienden del mismo tronco} en el '04, ...
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

// parecido, pero en breve - mostrando el resultado como objeto
$.getJSON('test.php', (d) => console.log(d));
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

Podeis elegir usar `JQuery`\footnote{Si lo haceis, usad una copia descargada en local y servida desde /\texttt{js}; en general, recomiendo hacer eso para todas las librerías y recursos}. En los ejemplos siguientes, uso Fetch.

- Moderno, usa promesas para gestionar asincronía
- No requiere librerías externas

## Envoltorio sencillo para fetch+JSON

\tiny

~~~{.javascript}
// versión completa disponible en resources/static/js/iw.js
function go(url, method, data = {}, headers = false) {
    let params = {
        method: method, // POST, GET, POST, PUT, DELETE, etc.
        headers: headers === false ? {
            "Content-Type": "application/json; charset=utf-8",
        } : headers,
        body: data instanceof FormData ? data : JSON.stringify(data)
    };
    if (method === "GET") {
        delete params.body;
    } else {
        params.headers["X-CSRF-TOKEN"] = config.csrf.value;
    }
    console.log("sending", url, params)
    return fetch(url, params)
        .then(response => {
            const r = response;
            if (r.ok) {
                return r.json().then(json => Promise.resolve(json));
            } else {
                return r.text().then(text => Promise.reject(/*snip*/));
            }
        });
}
}
~~~

## Uso del fetch anterior

~~~{.javascript}
function login(uid, pass) {
  return go(config.rootUrl + "/login", 'POST', {username, password})
    .then(d => console.log("ok!"))
    .catch(e => console.log(e))
}
~~~

## Introducción a promesas JS

* fetch devuelve una **promesa**
* las promesas son **asíncronas** - no hay garantía de que el código que viene después se vaya a ejecutar después de que la promesa se resuelva:

~~~{.javascript}
fetch('https://no-such-server.blabla') // seguramente falle
  .then(response => response.json())
  .then(json => console.log(json.nombre_y_apellidos))
  .catch(e => console.log(e))          // y por tanto se haga ésto

// es casi seguro que el fetch todavía NO habrá acabado al llegar aquí
console.log("Me imprimo antes de que el fetch acabe")
~~~

* si hay fallos, se salta directamente al **`catch`**
* mientras no hay fallos, se van ejecutando los **`then`** en orden, cada uno usando el `return` del anterior como argumento de entrada.
* los datos del primer `then` son de tipo [`response`](https://developer.mozilla.org/en-US/docs/Web/API/Response)
 
## Cosas que podemos sacar un objeto `Response`

* `Response.status` - estado HTTP
* `Response.ok == true` $\iff$ `Response.status == 200`
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

- Nada. Porque a menudo basta con ver el **código de estado** \
del resultado para saber si la petición ha funcionado o no
- **`texto`**. Y lo muestras por consola o lo usas para reemplazar un elemento
- **`html`**. Y luego lo muestras reemplazando el contenido de cualquier elemento
- **`json`**. Máxima flexibilidad, porque puede expresar cualquier estructura de datos sin ciclos.

## Usando AJAX

En la asignatura, usaremos Ajax para:

- **Validación**: por ejemplo, para evitar que los usuarios intenten registrarse con logins ya existentes. 

- **Mensajería**: ¿recargar toda la página para enviar un mensaje?\
¡Estamos en 2022!.

- **Carga dinámica de resultados**: Paginar los resultados es una posibilidad. Pero usar algún tipo de carga dinámica es mucho más amigable.

- **Botones**: Borrar un elemento de un listado no debería requerir recargar todo el listado.

En general, siempre que quieras **pedir o informar de algo sin recargar** la página, un fetch estaría justificado. Y más si la página es cara de construir (ejemplo: listado)

## JQuery y JS moderno

* [JQuery]() facilita mucho interaccionar con el modelo (DOM) de las páginas. **Su uso en la asignatura es opcional**.
* En los ejemplos, y en la plantilla, **no lo usaré** - porque de un tiempo a esta parte, [hay equivalentes no-JQuery](https://plainjs.com/javascript/):
  - `$(selector)` $\longrightarrow$ `document.querySelectorAll(selector)`
  - `$(html)` $\longrightarrow$ `const el = document.createElement('div'); el.innerHTML(html)`
  - ... (más en [web de plainjs](https://plainjs.com/javascript/)

## Ajax para validación

Recordamos el código de validación estándar:

~~~{.javascript}
// manejador que se ejecuta cuando la página se carga, sin machacar otros
document.addEventListener("DOMContentLoaded", () => {
    // selector para elegir sobre qué elementos validar
	let u = document.querySelectorAll('#username')[0]
    // cada vez que cambien, los revalidamos
    u.oninput = u.onchange = 
        () => u.setCustomValidity(  // si "", válido; si no, inválido
            validaUsername(u.value))
})

// NO LO HAGAS ASÍ: machaca manejador existente (si lo hay)
document.onload = () => {
    // (mismo contenido)
}
~~~

---

Y ahora, el ajax (asumimos uso de función `go` anterior)

~~~{.java}
function validaUsername(username) {
    let params = {username: username};
    // Spring Security lo añade en formularios html, pero no en Ajax
    params[config.csrf.name] = config.csrf.value;
    // petición en sí
    return go(config.rootUrl + "/username", 'GET', params)
        .then(d => "")
        .catch(() => "nombre de usuario inválido o duplicado");
}
~~~

. . .

\small

~~~{.js}
// ejemplo de 'config', definido en templates/head.html de la plantilla
const config = {
  socketUrl: "ws://localhost:8080/ws",	// vacío indica falso
  rootUrl: "//localhost:8080/",
  csrf: {
    name: "_csrf",
    value: "e8079d8b-84dc-405b-8eca-9d95a48a0fc7"
  },
  admin: true,
  userId: 1
};
~~~

\normalsize

---

Y en el servidor, un manejador normal y corriente (el servidor no distingue entre Ajax y no-Ajax; excepto en que no tiene sentido devolver vistas para peticiones que no las van a mostrar)

~~~{.java}
@GetMapping("/username")
public String getUser(@RequestParam (required=false) String uname) {
    User u = buscaUsuarioOLanzaExcepcion(uname);
    return "user"; // devuelve una vista completa
}
~~~

~~~{.java}
@GetMapping("/username")
@ResponseBody // <-- "lo que devuelvo es la respuesta, tal cual"
public String getUser(@RequestParam (required=false) String uname) {
    User u = buscaUsuarioOLanzaExcepcion(uname);
    return "ok"; // devuelve la cadena 'OK': gasta menos recursos
}
~~~

## Ajax para carga dinámica de resultados

* Usaremos [Simple Datatables](https://github.com/fiduswriter/Simple-DataTables) como _datatable_ (hay muchas alternativas; podeis elegir la que más os guste)
  - Código libre
  - No requiere ninguna librería externa, ni siquiera JQuery
  - Ajax muy sencillo
  - Incluida en última versión de la plantilla, siguiendo instrucciones de su ["manual"](https://github.com/fiduswriter/Simple-DataTables/wiki/ajax)

---

En el cliente...

~~~{.html}
<script th:src="@{/js/simple-datatables-2.1.10.min.js}"></script>
<link th:href="@{/css/simple-datatables-2.1.10.css}" rel="stylesheet">
<!-- ... -->
<table class="datatable" id="datatable"></table>
~~~

~~~{.js}
new simpleDatatables.DataTable(
  '#datatable', { 
      ajax: {
          url: config.rootUrl + "message/received", 
          load: (xhr) => { /* ... opcional: fechas más bonitas */ } 
      }});
~~~

---

Y en el [MessageController](https://github.com/manuel-freire/iw/blob/a97e6112d102704edc10b4a35fbe467a3f1edcc8/plantilla/src/main/java/es/ucm/fdi/iw/control/MessageController.java#L47) ...

~~~{.java}
@GetMapping(path = "/received", produces = "application/json")
@Transactional // para no recibir resultados inconsistentes
@ResponseBody  // no devuelve nombre de vista, sino objeto JSON
public List<Message.Transfer> retrieveMessages(HttpSession session) {
  long userId = ((User)session.getAttribute("u")).getId();		
  User u = entityManager.find(User.class, userId);
  log.info("Generating message list for user {} ({} messages)", 
     u.getUsername(), u.getReceived().size());
  return  u.getReceived()          // List<Message>
    .stream()                      // para operar como flujo
    .map(Transferable::toTransfer) // Message => Message.Transfer
    .collect(Collectors.toList()); // List<Message.Transfer>
}	
~~~

--- 

Los métodos de controlador anotados con `@ResponseBody` convierten su resultado a JSON usando **Jackson**

Jackson (y otras librerías que van de objeto Java a JSON y viceversa)

  - fallan con estructuras que tengan circularidad (*stack overflow*)
  - estarán encantadas de enviar la BD entera siguiendo *toodas* las referencias (no saben cuándo parar)
  - no sabe cómo quieres manejar tus fechas 

Solución estándar: usar [anotaciones](https://github.com/FasterXML/jackson-annotations/wiki/Jackson-Annotations) para indicar qué se JSONiza cómo

  - `@JsonProperty("nombre-distinto")` - para que el nombre en JSON sea otro
  - `@JsonIgnore` - para ignorar un campo al JSONizar
  - `@JsonView` - para agrupar muchas instruccines sobre cómo JSONizar por perfiles (ejemplo: `@JsonView("admin")` vs `@JsonView("public")`)
  -  anotaciones + configuración vía `mapper.addMixInAnnotations(UnaClase.class, ClaseQueLaSerializa.class);` para dar el cambiazo de una clase por otra

Solución propuesta: **objetos delegados (`Transferrable`)** que no requieren anotaciones, porque la serialización "por defecto" funciona perfectamente.

---

\small

~~~{.java}
public class Message implements Transferable<Message.Transfer> {

  // ... campos de Message

  @Getter @AllArgsConstructor
  public static class Transfer {
    private String from;
    private String to;
    private String sent;
    private String received;
    private String text;
    long id;
  }

  @Override
  public Transfer toTransfer() {
    return new Transfer(sender.getUsername(), recipient.getUsername(), 
      DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(dateSent),
      dateRead == null ? null : 
        DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(dateRead),
      text, id);
  }
}
~~~

\normalsize

## Fechas en Java y JS

- Usad [LocalDateTime](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/LocalDateTime.html#now()) para instantes temporales en **Java**. Mucho más fácil de trabajar que con el viejo `Date`.

- Usad `new Date()` para instantes temporales en **JS**.

- Usad formato ISO para pasar de una a otra:

~~~{.java}
  // carga `t` con una fecha como cadena en formato ISO
  LocalDateTime t = LocalDateTime.parse("2007-12-03T10:15:30");
  // genera la representación ISO de `t` ("2007-12-03T10:15:30")
  String iso = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(t);
~~~

~~~{.js}
  // carga `t` con una fecha como cadena en formato ISO
  let t = new Date("2007-12-03T10:15:30");
  // genera la representación ISO de `t` ("2007-12-03T09:15:30.000Z")
  console.log(t.toISOString());
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
    - **S**treaming **T**ext-**O**riented **M**essaging **P**rotocol
    - Otra acepción: sonido que hace dar un pisotón

* El protocolo
    - imita marcos ("frames") HTTP en texto, delimitados por bytes a 0 ('\0')
    - al igual que HTTP, sus _frames_ tienen
        - varias posibles peticiones (COMMAND)
        - 0 o más cabeceras de tipo _clave_:_valor_
        - una línea en blanco
        - un contenido textual (posiblemente vacío)
        - un *carácter nulo* (aquí uso `^@`)

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

\small

~~~{.html}
  <!-- declarado en fragments/head.html -->
  <script type="text/javascript">
    const config = {
      // dirección (metida en la sesión en el "LoginSuccessHandler")
      socketUrl: "[[${session.ws}?:'']]",	// vacío indica falso
      // ... otras propiedades
    };
  </script>
  <!-- librería para gestionar protocolo -->
  <script th:src="@{/js/stomp.js}" src="js/stomp.js" 
    type="text/javascript"></script>
  <!-- y aquí es donde te suscribes y manejas lo que viene por el WS -->
  <script th:src="@{/js/iwclient.js}" src="js/iwclient.js"
      type="text/javascript"></script>
~~~

\normalsize

* El servidor tiene que ofrecer esas rutas, y saber gestionarlas

## Lo que usaremos de STOMP

* Subscripciones para
    - administradores haciendo seguimiento de la aplicación
    - grupos de usuarios interesados en cosas que surgen
* Mensajería punto-a-punto
* Permisos (no todo el mundo se puede suscribir a todo)\
  vía Spring (no es parte de STOMP)

## Suscripción desde un cliente

* Ya incorporado a `iw.js`, que es parte de la plantilla

~~~{.javascript}
document.addEventListener("DOMContentLoaded", () => {
  if (config.socketUrl) {
    let subs = config.admin ? 
        ["/topic/admin", "/user/queue/updates"] : 
        ["/user/queue/updates"]
    ws.initialize(config.socketUrl, subs);
  }
);
~~~

## Controlando la suscripción en el servidor

~~~{.java}
@Configuration
public class WebSocketSecurityConfig
    extends AbstractSecurityWebSocketMessageBrokerConfigurer { 


  protected void configureInbound(
      MessageSecurityMetadataSourceRegistry messages) {
    messages
      .simpSubscribeDestMatchers("/topic/admin") // solo admin
        .hasRole(User.Role.ADMIN.toString())
      .anyMessage().authenticated();             // solo logueado
  }
}
~~~

... de forma que sin un no-admin abre la consola y se intenta suscribir a `/topic/admin`, la operación falla

## Enviando mensajes a un usuario

* Destino: `queue/updates/nombreusuario`
* Requiere suscripción a `user/queue/updates` (te llames como te llames)

~~~{.java}
// en el controlador...
@Autowired
private SimpMessagingTemplate messagingTemplate;


// dentro de un @AlgoMapping ...
messagingTemplate.convertAndSend("/user/"+u.getUsername()
  +"/queue/updates", json);
~~~

## Enviando mensajes a un canal

* Destino: `topic/nombrecanal`
* Requiere suscripción previa (y puede ser prohibida por `WebSocketSecurityConfig`)

~~~{.java}
// en el controlador...
@Autowired
private SimpMessagingTemplate messagingTemplate;


// dentro de un @AlgoMapping ...
messagingTemplate.convertAndSend("/topic/admin", json);
~~~

## Creando JSON para enviar cosas

* Con Jackson, usando la API
  - Escapa cadenas de forma correcta
  - No tienes que definir una clase para tu mensaje

~~~{.java}
User requester = (User)session.getAttribute("u");
ObjectMapper mapper = new ObjectMapper();
ObjectNode rootNode = mapper.createObjectNode();
rootNode.put("text", requester.getUsername() 
  + " is looking up " + u.getUsername());
String json = mapper.writeValueAsString(rootNode);
// { "text": "pepito is looking up juanito" }
~~~

- - - 

* Con Jackson, serializando objetos
  - Escapa cadenas de forma correcta
  - No tienes que aprender a usar la API
  - Pero tienes que definir una clase para cada mensaje

~~~{.java}

// en algún sitio
@Getter
public static class Chivatazo {
  private String text;
  public Chivatazo(User uno, User otro) {
    this.text = uno.getUserName() + 
      + " is looking up " + otro.getUsername());    
  }
}


// en el método del controlador:
User requester = (User)session.getAttribute("u");
ObjectMapper mapper = new ObjectMapper();
String json = mapper.writeValueAsString(new Chivatazo(requester, u));
~~~

- - - 

* Sin Jackson: *NO RECOMENDADO*
  - Son cadenas de texto, fáciles de montar por concatenación
  - **Pero también es fácil no escaparlas, o escaparlas mal**

~~~{.java}
User requester = (User)session.getAttribute("u");

String json = "{ \"text\": " 
  + requester.getUsername() + " is looking up " + u 
  + "}";
~~~

**En ese fragmento hay 2 nombres de usuario sin escapar, y ambos pueden contener `"` que rompan cosas**

## Referencias para websockets

* Websockets según [Spring Framework](https://docs.spring.io/spring/docs/current/spring-framework-reference/html/websocket.html)

* Más docs de [Spring MVC](https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#websocket)

* Según el estándar de la [IETF](https://tools.ietf.org/html/rfc6455)

* Api de Websockets, [según Mozilla](https://developer.mozilla.org/en-US/docs/Web/API/WebSockets_API)

# Resumen final

## Ajax

* Primero que funcione, y luego Ajax donde tenga sentido
* Usad formularios normales con `th:action` - así tendran las URLs correctas, y si son post, los CSRFs necesarios.
* Convertidlos a Ajax vía JS que inhabilite el envío tradicional, y en su lugar use un `fetch`
* Ejemplo en [messages.html](https://github.com/manuel-freire/iw/blob/master/plantilla/src/main/resources/templates/messages.html)

~~~{.html}

<!-- formulario normal (sólo resulta raro el "idOfTarget") -->
<form th:action="@{/user/idOfTarget/msg}" method="POST">
<select id="idOfTarget">
  <option th:each="user: ${users}" th:value="${user.id}" 
    th:text="${user.username}">mfreire</option>
</select>
<textarea id="message" rows="4" cols="20" 
  placeholder="escribe al usuario seleccionado"></textarea>
<button id="sendmsg" type="submit">Enviar</button>
</form>

~~~

- - - 

~~~{.javascript}
// envío de mensajes vía AJAX, sin recargar la página
document.addEventListener("DOMContentLoaded", () => {
  let b = document.getElementById("sendmsg");
  b.onclick = (e) => {
    let idOfTarget = document.getElementById("idOfTarget").value;
    let url = b.parentNode.action.replace("idOfTarget", idOfTarget);
    e.preventDefault(); // <-- evita que se envíe de la forma normal
    console.log(b, b.parentNode)
    go(url, 'POST',     // <-- hace el `fetch`, y recibe resultados
       { message: document.getElementById("message").value })
      .then(d => console.log("happy", d))
      .catch(e => console.log("sad", e))
  }
});
~~~

## Websockets

Ficheros implicados, lado servidor:

- WebSocketConfig.java: ¿qué URLs habrá? 
- WebSocketSecurityConfig.java: ¿quién puede registrarse dónde?
- LoginSuccessHandler.java: ¿cómo saben los clientes a qué URL escuchar?

Y en el lado cliente:

- header.html: objeto 'config', y carga stomp.js + iw.js
- stomp.js: librería para protocolo STOMP del lado de cliente
- iw.js: 
  + establece comunicación STOMP con servidor
  + configura objeto 'ws', donde se configura lo que pasa cuando llegan mensajes
  + define una función 'go' para hacer peticiones 'fetch' sencillas enviando y recibiendo JSON

## Recibiendo datos de WS

* Por defecto, en [iwclient.js](https://github.com/manuel-freire/iw/blob/a97e6112d102704edc10b4a35fbe467a3f1edcc8/plantilla/src/main/resources/static/js/iwclient.js#L14):

~~~{.javascript}
const ws = { 
  // lo que recibes por WS se muestra por consola y punto
  receive: (text) => console.log(text)

  // ... otras cosas
}
~~~

* Si quieres hacer algo más sofisticado, como aquí en [messages.html](https://github.com/manuel-freire/iw/blob/a97e6112d102704edc10b4a35fbe467a3f1edcc8/plantilla/src/main/resources/templates/messages.html#L80), debes sobreescribir el comportamiento de `ws.receive`:
~~~{.javascript}
ws.receive = (m) => {
  // cuando recibo un mensaje, lo añado como fila al final de la tabla
  dt.rows().add([m.from, m.to, 
    formatDate(new Date().toISOString()), "", m.text, m.id]);		
}
~~~

## Enviando cosas por WS

* Sólo servidor $\rightarrow$ cliente\
(para enviar cliente $\rightarrow$ servidor usamos peticiones normales)

* Basta con especificar destinatario y mensaje, y usar un `messagingTemplate`: 

~~~{.java}
// en el controlador...
@Autowired
private SimpMessagingTemplate messagingTemplate;

// dentro de un @AlgoMapping ...
ObjectMapper mapper = new ObjectMapper();
String json = mapper.writeValueAsString(
  new CosaQueJacksonPuedeConvertirAJSON(argumentos));
messagingTemplate.convertAndSend(
  // podría ser un topic: "/topic/algo"; sólo reciben los suscritos
  // en este caso, se lo enviamos por su canal personal
  "/user/"+u.getUsername()+"/queue/updates", json);
~~~

# Fin

## ¿?

![](img/cc-by-sa-4.png "Creative Commons Attribution-ShareAlike 4.0 International License"){ width=25% }

This work is licensed under a [Creative Commons Attribution-ShareAlike 4.0 International License](https://creativecommons.org/licenses/by-sa/4.0/)

