% Seguridad en aplicaciones Web Java
% (manuel.freire@fdi.ucm.es)
% 2022.03.07

# Introducción

## Objetivo

> Seguridad en aplicaciones Web Java

## Amenazas

* Denegación de servicio
* Destrucción de datos de usuarios
* Acceso a datos de usuarios
* Suplantación de identidad
* Acceso ó control externo de la aplicación
* Acceso ó control externo del servidor

*(de menos a más grave)*

## Vectores de ataque

* Externos a la aplicación: _puedes **mitigar** su impacto_
    + acceso físico al servidor
    + vulnerabilidad remota
    + vulnerabilidad local
* Internos a la aplicación: _puedes **evitarlos** completamente_
    + inyección de SQL (contra la BD)
    + inyección de HTML/JS (contra clientes)
    + inyección de ficheros (contra datos/programa)
    + datos incorrectos (contra integridad/validez)

# Vectores externos

## ¿Tu servidor está comprometido?

* Defensa en profundidad
    + primero, que no entren
    + si entran, que no se lo lleven _todo_

* Evita exponer datos sensibles
    + [contraseñas: ~11752 M cuentas comprometidas](https://haveibeenpwned.com/)\footnote{marzo 2022; cambia con frecuencia}
    + tarjetas / información de pago
    + otra información personal delicada\footnote{datos demográficos, amistades, ...}

## Contraseñas: qué NO hacer

* **Nunca** guardes contraseñas en _texto claro_
    + no en tu BD
    + no en tus logs (incluso si son fallidas)
* **Nunca** envies contraseñas en _texto claro_
    + para 'resetear' acceso, usa tokens aleatorios con expiración (_nonce_)

## Contraseñas: porqué NO hacerlo

* Usuarios a menudo
    + **reutilizan** contraseñas en muchos sitios
    + usan un "esquema de contraseña" más o menos atacable \
    (`cebra18`, `leon19`, ... ) para inventar nuevas contraseñas. 

* Con contraseñas en claro, atacante tiene vía libre
    + contra tu sitio, como cualquier usuario (por ejemplo, *admin*)
    + contra sitios externos, porque muchos usuarios 
        - reutilizan contraseñas
        - usan esquemas atacables

## Contraseñas: qué SI hacer

* Convierte tus contraseñas a un **formato modificado** que
    + permite **_verificar_** que el original corresponde a la modificación
    + **no** permite _obtener_ el original a partir de la modificación
    + **no** permite _entrar_ con la modificación

* Receta para un buen **formato modificado**
    + **clave** (elegida por el usuario)
    + **sal** (generada aleatoriamente, fastidia *ataques de diccionario*)
    + **pimienta** (igual para todos, pero no contenida en la BBDD; opcional)
    + y todo ello pasado por una buena batidora de bits (**hash**)

## Contraseñas: Hashes criptográficos

* Dada una cadena de bits _b_, h(b) (un hash) debe cumplir
    + **consistente**: $b_1 = b_2 \implies h(b_1) = h(b_2)$
    + longitud constante: $\forall b, |h(b)| = {n_h}$, \
    con $h$ típicamente entre 128 y 512 bits (16 a 64 bytes)
* Como puede haber más claves ($2^{n_k}$) que hashes ($2^{n_h}$), habrá\footnote{Teorema del palomar: si hay más palomas (posibles cadenas de bytes) que nidos (hashes), alguna paloma va a tener que compartir nido} _colisiones_
    + ¿cómo, tu contraseña no es de al menos 16 caracteres? \
    (la mía tampoco; pero también es posible hashear \
    cosas que no son contraseñas)
    + colisión: cuando $a \neq b \wedge h(a) = h(b)$ \
    "mismo resultado, a partir de cosas distintas"

- - - 


* Notación: $h(a\:||\:b)$ significa $h$($a$ **concatenado_con** $b$)

* Para que $h$ sirva como hash **criptográfico**, debe ser difícil\footnote{por ejemplo, con probabilidades de acertar del orden de $2^{-128};$ astronómicamente difícil porque es del orden del número de átomos en el universo visible}
    + encontrar $b_2 \:|\: h(b_1) = h(b_2)$ dado $h(b_1)$ (pre-imagen)
    + encontrar $b_2 \:|\: h(b_1) = h(b_2)$ dado $b_1$ (segunda pre-imagen)
    + encontrar cualquier colisión, en general (resistencia fuerte)

- - - 

## Contraseñas: Condimento

* Ataques contra un hash "sin sal", que almacena $h(pass)$
    + hash de gran diccionario: ¿alguna coincide?
        - puedo encontrar coincidencias entre diccionario y BD en tiempo logarítmico
    + miro lista de hashes: ¿alguna coincide con alguna otra?
        - contraseñas con el mismo hash seguramente son iguales
        - y si tienen pistas, esto se convierte en crucigrama
    + extensión del diccionario: *rainbow tables*
        - genero $n$ cadenas de hashes almacenando $p_{i}$ y $h_{2^{10}}(p_{i})$ para $i \in 1..n$
        - luego, itero ${2^{10}}$ veces sobre cada uno  de los $m$ hashes de la BD, \
        comparando a cada iteración con los $n$ finales de cadena
        - si un final coincide, vuelvo a recorrer esas cadenas desde el principio; \
            con alta probabilidad, hay un eslabón que cumple $b = h(a)$ \
            **aceleración con un factor $n$ durante $2^{10}$ iteraciones**


(referencias crucigrama: [XKCD](http://xkcd.com/1286/), [crucigrama al respecto](http://zed0.co.uk/crossword/) ) 

- - -

* Contra un hash "salado", almacenando $h(sal\:||\:pass)$ y $sal$
    + $h(s_1\:||\:pass_1) = h(s_2\:||\:pass_2) \not \Rightarrow pass_1 = pass_2$\
    (excepto por colisión)
    + $h(s_1\:||\:pass_1) \neq h(s_2\:||\:pass_2) \not \Rightarrow pass_1 \neq pass_2$\
    (excepto por colisión)
    + $\leadsto$ sólo puedes atacar contraseñas **una por una**, con su respectiva sal\
    -- en lugar poder atacarlas todas a la vez (¡crucigramas!) y/o reaprovechar trabajo previo (¡diccionarios, tablas arcoiris!)

## Mejores prácticas 

* **No escribas código de gestión de contraseñas**
    + La criptografía es difícil
    + Mejor dejársela a los tipos que la saben \
    *y que van a actualizarla cuando se rompa*
    + Coste de fuerza bruta siempre disminuye, nunca va a aumentar
* Usa un estándar para almacenar tus contraseñas transformadas. Buenas opciones (entre [muchas soportadas por Spring Security](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/crypto/factory/PasswordEncoderFactories.html))
    + Bcrypt
    + PBKDF2
    + Argon2
    + Scrypt
    
## Bcrypt 

 * [Bcrypt](https://en.wikipedia.org/wiki/Bcrypt)

* Multironda: aumenta coste de ataque\
(y de uso normal, pero es asimétrico: le "duele" mucho más al atacante, porque hay que atacar *muchas* veces si quieres romper un hash por fuerza bruta).
+ Distintos algorimos: empezó con `md5`, ahora se usa sobre todo con `blowfish`
+ Autodescriptivo: su salida incluye información sobre algoritmo, rondas, y sal
+ Por defecto, Spring Security usa algo similar a `$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy`
    * 2a: algoritmo **blowfish** sobre cadenas **utf8 terminadas con `\0`**
    * 10: **$2^{10}$ rondas**
    * siguientes 22 caracteres: la **sal** (128 bits, en algo similar a Base-64\footnote{bcrypt:  \texttt{./ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789}\\
    Base-64 usa: \texttt{ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/}}) \
    \texttt{N9qo8uLOickgx2ZMRZoMye = 0x3ffb2afb035091e9a2cf86ce4dba8ed20}
    * siguientes 31 caracteres: el **hash** resultante (184 bits, misma codificación) \
    \texttt{IjZAgcfl7p92ldGxad68LJZdL17lhWy = 0xa95b0a27a19fdaffe277c8cdc7fcf8d2db7cddfd9e3634}

## Tarjetas de crédito

* Peores que las contraseñas: 16 dígitos
    * 6 del banco (conocidos)
    * 1 redundante (se calcula a partir de los otros)
    * 4 últimos usados para resolver incidencias (semipúblicos, tendrías que guardarlos 'en claro')
    * adivinar 5 restantes por fuerza bruta es fácil: 100k posibilidades
* **Evita gestionarlas**
    * Usa las APIs de tu proveedor
    * Que carguen _otros_ con las responsabilidades asociadas ([PCI](https://www.pcisecuritystandards.org/))

# Vectores internos

## Inyección SQL

> Texto del usuario que se <br>
>   incluye en una consulta SQL, <br>
> pero se interpreta como SQL <br>
>   en lugar de texto

. . .

Introducing: little [Bobby tables](http://bobby-tables.com/java.html)

- - - 

(99%+ de los usuarios no son malvados)

~~~{.java}
login = "Robert"; // del formulario
sql = "SELECT * FROM Student WHERE login='" + login + "';";
~~~

. . .

~~~{.sql}   
SELECT * FROM Student WHERE login='Robert'
~~~

. . . 

\hspace{1cm}
\hrule
\hspace{1cm}

(1% de usuarios, incluyendo tu profesor)

~~~{.java}
login = "Robert'; DROP TABLE Students;--"; // del formulario
sql = "SELECT * FROM Student WHERE login='" + login + "';";
~~~

. . .

~~~{.sql}
SELECT * FROM Student WHERE login='Robert'; DROP TABLE Students;--';
~~~

---

Ejemplo de inyección SQL para saltarse comprobaciones

~~~{.java}
sql = "SELECT * FROM Student " +
    "WHERE login='" + login + "' AND pass='" + login + "';";
~~~

. . .

~~~{.java}
login = "Robert"; // del formulario
pass = "1' OR '1'='1"
~~~

. . .

~~~{.sql}
SELECT * FROM Student 
    WHERE login='Robert' AND pass='1' OR '1'='1';
~~~

. . .

(en SQL, AND tiene prioridad sobre OR -- devolviendo *todos* los usuarios)

## Más tipos de inyección SQL

* Ciegas: sin poder ver la respuesta \
(pero sí sus efectos secundarios)
    - retardos en proporcionar respuesta
    - tipo de error generados
    - retardos externos (DNS, ...)
* Exploratorias: para descubrir 
    - la estructura de la BD
    - y de sus consultas (para poder inyectar más y mejor)
* ...

(mucho más en, pongamos, [https://www.websec.ca/kb/sql_injection](https://www.websec.ca/kb/sql_injection))

## Evitando inyección SQL

* Usa **consultas preparadas**
* **No construyas** consultas por **concatenación**
    + porque para evitar inyecciones tendrías que limpiar los argumentos...
* Y **no intentes 'limpiar' a mano**
    + _no sabes suficiente SQL_
    + _ni conoces las variantes que usa cada BD_


. . . 

Codificaciones en MySQL según [websec.ca](https://www.websec.ca/kb/sql_injection)

* URL\
`SELECT %74able_%6eame FROM information_schema.tables;`    
* Double URL\
`SELECT %2574able_%256eame FROM information_schema.tables;`
* Unicode\
`SELECT %u0074able_%u6eame FROM information_schema.tables;`


## Inyección de HTML/JS

> Texto del usuario que se <br>
>   incluye en una página web, <br>
> pero se interpreta como HTML/JS<br>
>   en lugar de texto

. . .

Introducing: [alert(1) to win](http://escape.alf.nu/)

- - -

~~~{.java}
// en el controlador
login = "Berto"; // del formulario
model.addAttribute("login", login);
~~~
~~~{.html}
<!-- en la vista;
    (nunca useis 'utext' con datos controlados por usuarios) -->
<h1>Bienvenido, <span th:utext="${login}">Alguien</span></h1>
~~~

. . .

~~~{.html}
<h1>Bienvenido, <span>Berto</span></h1>
~~~

. . . 

---

~~~{.java}
// en el controlador
login = "<script>alert(1)</script>"; // del formulario
model.addAttribute("login", login);
~~~
~~~{.html}
<!-- en la vista;
    (nunca useis 'utext' con datos controlados por usuarios) -->
<h1>Bienvenido, <span th:utext="${login}">Alguien</span></h1>
~~~

. . .

~~~{.html}
<h1>Bienvenido, <span><script>alert(1)</script></span></h1>
~~~

---

~~~{.html}
<h1>Bienvenido, <span>
<script src="http://malo.com/maldades.js"></script>
</span></h1>
~~~

## Evitando inyección HTML/JS

* Valida, valida y valida. Y luego, escapa.

* Escapado: depende del contexto
    + dentro de un elemento HTML: **thymeleaf os ayuda**; usa `text`
    + dentro de un atributo de HTML: **thymeleaf os ayuda**: lo escapa automáticamente. 
    + dentro de JS: configura bien tu serialización (la veremos el próximo día).

## Incluyendo JS dinámico en tu HTML

~~~{.java}
model.addAttribute("message", "1 + Math.sqrt(2)");
~~~

~~~{.javascript}
// dentro de un <script> en un fichero html
var message =/*[[${message}]]*/ 'defaultanyvalue';
//            |              |
//            \--------------+-- sin ' ' entre comentario y corchetes
~~~

## Evitando inyección de HTML/JS desde terceras páginas

* vale, tu página puede no contener inyecciones. Pero, ¿y las de otros?

* ¿Qué pasa si visitas un foro, y lees el comentario siguiente, y tu navegador ejectua ese JS?

~~~{.html}
¡Qué post tan bueno!<script src="http://servidormaligno.com/script.js"></script>
~~~

* Soluciones:

- Navegadores modernos: 
    + cabecera content-security-policy
    + CORS
    * X-Frame-Options

## Protegiendo posts: CSRF

* Cross-Site Request Forgery

~~~~ {.html}
<div>
    // si eso lo lee el admin, está logeado, y el "endpoint" existe...
    <script>$.post("cambiarPasswd", {u: "admin", p: "muahaha"});</script>
</div>
~~~~

## Evitando CSRF

* Uso de tókenes de sesión
    + se genera en cada sesión, distinto para cada usuario, no-adivinable
    + se incluye en cada petición CSRF (que __debe__ ser tipo POST)
    + si token ausente o no válido, alerta: puede ser petición CSRF
    
- - -

Generación y verificación del token:

~~~~ {.java}
// al logear a un usuario, antes de devolver la vista
static String getTokenForSession (HttpSession session) {
    String token=UUID.randomUUID().toString();
    session.setAttribute("csrf_token", token);
    return token;
}

// para comprobar si token valido, antes de hacer nada con los datos
static boolean isTokenValid(HttpSession session, String token) {
    String t=session.getAttribute("csrf_token");
    return (t != null) && t.equals(token);
}
~~~~

- - - 

Uso del token en una consulta POST:

~~~~ {.java}
        /**
         * Delete a user; return status indicating success or failure
         */
        @RequestMapping(value = "/delUser", method = RequestMethod.POST)
        @ResponseBody
        @Transactional // needed to allow DB change
        public ResponseEntity<String> delUser(
                @RequestParam("id") long id,
                        @RequestParam("csrf") String token, 
                        HttpSession session) {
                if ( ! isAdmin(session) || ! isTokenValid(session, token)) {
                        return new ResponseEntity<String>("Error: bad auth", 
                                        HttpStatus.FORBIDDEN);
                } else if (entityManager.createNamedQuery("delUser")
                                .setParameter("idParam", id).executeUpdate() == 1) {
                        return new ResponseEntity<String>("Ok: user " + id + " removed", 
                                        HttpStatus.OK);
                } else {
                        return new ResponseEntity<String>("Error: no such user", 
                                        HttpStatus.BAD_REQUEST);
                }
        }       
~~~~

- - - 

En el formulario (si no usas Spring Security):

~~~~ {.html}
    <form action="http://bank.com/transferencia" method="POST">
        ...
        <input type="hidden" name="csrf" 
            value="${e:forJavaScript(csrf_token)}"/>
        ...
    </form> 
~~~~

O en la petición AJAX:

~~~~ {.html}
    <script>
        $.post("cambiarPasswd", 
            {u: "admin", p: "algomuycomplicado", csrf: "${csrf_token}"});
    </script>
~~~~

## Evitando CSRF con Spring Security & Thymeleaf

* Spring Security se encarga de generar y validar el token csrf
* Lo guarda en la sesión, y está disponible como `${_csrf}` desde Thymeleaf
* Thymeleaf lo mete automáticamente en formularios con `method="post"`, siempre que haya un `th:action`

* **OJO** si tu formulario es POST pero no usa Thymeleaf (por ejemplo, usas AJAX), *tienes que meter a mano* el token CSRF - o usar la función que viene en `iw.js`, que lo incorpora

- - - 

~~~~{.js}
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
    return fetch(url, params) // ...
~~~~

## Vectores internos: ficheros

* Desconfía de los ficheros que te suban
    + de su nombre y/o ruta: descártalo
    + de su contenido: no te fíes de que contiene
        - lo que has pedido
        - lo que dicen que te están dando
* Usa un esquema de nombrado que garantice que no hay colisiones
    + recomendado: _base_/_tabla_/_id-en-tabla_

(En J2EE es mucho más complicado conseguir que el servidor ejecute archivos externos que en PHP)
    
## Bajando ficheros en la aplicación de ejemplo

~~~{.java}
@GetMapping(value="/{id}/photo", produces = MediaType.IMAGE_JPEG_VALUE)
public StreamingResponseBody getPhoto(@PathVariable long id, 
        Model model) throws IOException {
    File f = localData.getFile("user", ""+id);
    InputStream in;
    if (f.exists()) {
        in = new BufferedInputStream(new FileInputStream(f));
    } else {
        in = new BufferedInputStream(getClass().getClassLoader()
            .getResourceAsStream("static/img/unknown-user.jpg"));
    }
    return new StreamingResponseBody() {
        @Override
        public void writeTo(OutputStream os) throws IOException {
            FileCopyUtils.copy(in, os);
        }
    };				
}
~~~

## Subiendo ficheros en la aplicación de ejemplo

~~~{.java}
@PostMapping("/{id}/photo")
@ResponseBody
public String postPhoto(@RequestParam("photo") MultipartFile photo,
        @PathVariable("id") String id){
    File f = localData.getFile("user", id);
    if (!photo.isEmpty()) {
        try (BufferedOutputStream stream = 
                new BufferedOutputStream(new FileOutputStream(f))) {
            byte[] bytes = photo.getBytes();
            stream.write(bytes);                
        } catch (Exception e) {
            return "Error uploading " + id + " => " + e.getMessage();
        }
    } else {
        return "You failed to upload a photo for " + id + ": empty?";
    }
    return "You successfully uploaded " + id + " into " 
        + f.getAbsolutePath() + "!";
}
~~~

## Validando que son lo que dicen ser

~~~{.java}
boolean isImage(File f) {
    try {
        BufferedImage img = ImageIO.read(f));
    } catch (IOException e) {
        // fallo interpretando como imagen
        return false;
    }
    return true;
}
~~~

* Nota: esto sólo garantiza que son imágenes. Pueden ser, además, más cosas:
[en este caso, las obras de Shakespeare y su retrato](https://twitter.com/David3141593/status/1057042085029822464)

## Vectores internos: datos incorrectos
    
* Nunca asumas que lo que llega es lo que esperas recibir
    + Es posible llegar a URLs sin hacer click en enlaces
        - por ejemplo, escribiéndolas directamente
    + Es posible modificar parámetros GET editando la URL
    + Es posible modificar parámetros POST editando JS\
    ó usando utilidades del navegador
        - recomiendo PostMan; muy útil para probar APIs

- - - 

~~~~
    /admin?op=borraUsuario&login=paco
~~~~

. . .

más te vale comprobar si el que pide esto tiene permiso para borrar a 'paco'

~~~~
    /carrito?producto=123&cantidad=2&precio=36
~~~~

. . .

el precio nunca debería formar parte de esta consulta: ¡pueden cambiarlo!

## Datos incorrectos

* VALIDA tus formularios en el CLIENTE
    + esto evita enfadar a tus clientes por tener que re-introducirlos
    + esto evita algunas peticiones malas en el servidor
* VALIDA tus formularios en el SERVIDOR (y los permisos de quien los envía) 
    + esto evita corromper tus datos
    + esto evita que se salten tus permisos

# Fin

## ¿?

¡No te quedes con preguntas!

------

![](./img/cc-by-sa-4.png "Creative Commons Attribution-ShareAlike 4.0 International License"){ width=25% }

This work is licensed under a [Creative Commons Attribution-ShareAlike 4.0 International License](https://creativecommons.org/licenses/by-sa/4.0/)
