% Logging
% (manuel.freire@fdi.ucm.es)
% 2022.02.28

## Objetivo

> Uso de logs para 
> depurar tu aplicación web

## Qué es el logging

* Log file = cuaderno de abordo, bitácora

    + logging = el acto de apuntar algo\footnote{Por ejemplo, trazas sobre el funcionamiento de programas que permiten reconstruir qué pasó cuándo, y así poder depurarlo.} en la bitácora. 
    + *ojo*: login ó log-in = cuando te apuntas /*a*/ algo. \
    Típicamente, como resultado, te meten en el libro de visitas:\
    "your login has been logged"
    
* Log también es "tronco"

    + logging = tala

## Logging sin logs

~~~~ {.java}
  System.out.println("he pasado por aquí " + 
    + "y soy el hilo " + Thread.currentThread().getName() + " "
    + "y el tiempo de ejecución es " + System.currentTimeMillis()); 
~~~~

o bien

~~~~ {.java}
  System.err.println("he pasado por aquí " + 
    + "y soy el hilo " + Thread.currentThread().getName() + " "
    + "y el tiempo de ejecución es " + System.currentTimeMillis()); 
~~~~

Diferencias entre `System.out` y `System.err`:

* stdout usa búferes (y no se escribe hasta que ve un `'\n'`); stderr **no**.
* stdout y stderr se pueden redirigir a lugares distintos

~~~{.bash}
echo "patata" >salida.txt 2>&1
~~~

* muchos entornos sacan stderr en rojo por su consola integrada

## Problemas de `System.x.println` para depurar ejecución

* `println`s no son fáciles de 
    + desactivar una vez te pones a depurar algo distinto
    + reactivar cuando vuelves a tener problemas allí
* no es fácil pasar la salida a un lugar distinto \
    (out, err, fichero, correos, Telegram)
* sacar detalles sobre cada traza es muy repetitivo\footnote{DRY dice: copiar y pegar es siempre evitable. \textbf{D}on't \textbf{R}epeat \textbf{Y}ourself}:
    + **hilo** donde se produce
    + **momento** en el que se produce
    + **clase y método** en el que se produce
    + y luego, algún mensaje que te de información más específica\
    "usuario López no encontrado", "error insertando zuncho", ...

## Escribiendo una clase sencilla para gestionar trazas

\small 

~~~ {.java}
public enum Log {
  INSTANCE;  // creado al cargarse la clase; usado para Singleton desde JDK5
  private PrintStream stream = System.out;
  private int level = 0;
  private long t0 = System.currentTimeMillis();
  public void setWriter(PrintStream stream) {               // cambia destino
    INSTANCE.stream = stream;
  }
  public static void setLevel(int level) {                  // cambia detalle
    INSTANCE.level = level;
  }
  public static void log(int level, Object ... messages) {  // llamada de log
    if (level < INSTANCE.level) {
      return;
    }
    StackTraceElement[] frames = Thread.currentThread().getStackTrace();
    StringBuilder sb = new StringBuilder(
        Thread.currentThread().getName() + "@" +   // nombre del hilo
        frames[frames.length-1] + ",t=" +          // clase y línea
        (System.currentTimeMillis()-t0) + ":");    // tiempo desde t0
    for (Object o : messages) sb.append(" " + o);  // mensaje
    INSTANCE.stream.println(sb.toString());
  }
}    
~~~

## Usando nuestra clase sencilla


~~~ {.java}
    // main@Log.main(Log.java:27),t=1: y el número es... el  42
    Log.log(1, "calculando respuesta... ", calculaRespuesta());
~~~

Funcionamiento:

- Por defecto, escribe por `stdout`. 
- Podemos desactivarlo todo si ponemos un `level` mínimo más alto
- Podemos redirigirlo usando `setWriter`

~~~ {.java}
    Log.setWriter(new PrintWriter(new File("log.txt"));
~~~

Fallos:

* a veces queremos activar trazas en un sitio sí y en otros no
* algunas trazas son caras, y no queremos tener que construirlas a no ser que sea imprescindible; en el ejemplo anterior, \
 `calculaRespuesta()` se evaluaría aunque `Log.level` fuera > 1
* sería bueno tener un soporte especial para excepciones, para que se muestren con todos sus detalles... o no.
* ¿esto no estaría mejor como una librería bien escrita y documentada?

## Ventajas de Logging vs system.out.println

* casi igual de fácil de escribir
* soporte para excepciones
* mucho más ágil durante depuración: \
muestra más información sin tener que meterla en el `println`
* mucho más fácil de limpiar una vez todo (parece que) funciona
* mucho más fácil de reactivar cuando resulta que algo seguía sin funcionar 

## Ventajas de Logging vs. sesiones de depuración

* Puede usarse cuando no hay interfaz de depuración disponible
    - servidores a los que sólo tenemos acceso vía consola
    - cuando no hay un IDE a mano
* O cuando el programa tiene concurrencia y temporización
    - interfaces gráficas de usuario
    - sistemas con usuarios concurrentes
* Las trazas persisten
    - Una vez generada una traza, puedes mirarla y remirarla. 
    - Y compararla contra otras. 
    - Las sesiones de depuración no permiten volver al pasado, \
    y es difícil lanzarlas en paralelo
* Todas las librerías de logging permiten trazar a fichero y a consola a la vez.

## Librerías de logging

* `jul` - viene con la JDK. Algo lenta y obtusa de configurar (ver transparencia anterior).
* `log4j` - inspiró a jul, más sencilla de usar/configurar. Recomendada para proyectos vuestros.
* `commons logging` - variante Apache Commons. Similar a log4j.
* `slf4j` - fachada que permite escribir / leer de cualquiera de las anteriores, de forma que tu aplicación puede integrar componentes que usen distintas librerías de logging. Recomendada para proyectos que otros vayan a poder usar/integrar.

~~~ {.java}
  // log4j2
  logger.info("Logging {} and {} ", i, someString);
  
  // slf4j (es el mismo desarrollador que para log4j...)
  logger.info("Logging {} and {} ", i, someString);
  
  // jul
  logger.log(Level.INFO, "Logging {0} and {1}", 
    new Object[]{i, someString});
~~~

## Niveles en log4j

* FATAL - fin de programa
* ERROR - algo muy malo
* WARN - aviso importante
* INFO - algo más o menos importante ("arrancando programa")
* DEBUG - un detalle, tipo "entrando en función interesante"
* TRACE - un detalle realmente nimio, tipo "mostrando valor recóndito"

## Logging en vuestras aplicaciones

* Usamos log4j
* Ejemplos de uso:

~~~~ {.java}

// en algún lugar de la clase MiClase
private static final Logger log = LogManager.getLogger(MiClase.class);

// en versiones viejas de log4j:= Logger.getLogger(MiClase.class);

// mensaje de información
log.info("Usuario {} entra en el sistema", u.getName());

// mostrar una excepción
log.warn("Usuario {} la monta intentando {}", 
    u.getName(), e.getMessage(), e);
    
// ahorrando recursos
if (log.isDebugEnabled()) {
    log.debug("El dígito 1000000 de pi es {}", calcularDigito1MDePi());
}
~~~~ 

Más detalles en la [api de log4j 2](https://logging.apache.org/log4j/2.x/manual/api.html) 

## Logging en vuestras aplicaciones: librerías

\tiny

~~~{.txt}

[INFO] +- org.springframework.boot:spring-boot-starter-security:jar:2.6.3:compile
[INFO] |  +- org.springframework.boot:spring-boot-starter:jar:2.6.3:compile
[INFO] |  |  +- org.springframework.boot:spring-boot:jar:2.6.3:compile
[INFO] |  |  +- org.springframework.boot:spring-boot-autoconfigure:jar:2.6.3:compile
[INFO] |  |  +- org.springframework.boot:spring-boot-starter-logging:jar:2.6.3:compile
[INFO] |  |  |  +- org.apache.logging.log4j:log4j-to-slf4j:jar:2.17.1:compile
[INFO] |  |  |  |  \- org.apache.logging.log4j:log4j-api:jar:2.17.1:compile
[INFO] |  |  |  \- org.slf4j:jul-to-slf4j:jar:1.7.33:compile
...
[INFO] \- com.intuit.karate:karate-junit5:jar:1.1.0:test
[INFO]    +- com.intuit.karate:karate-core:jar:1.1.0:test
...
[INFO]    |  +- ch.qos.logback:logback-classic:jar:1.2.10:compile
[INFO]    |  |  \- ch.qos.logback:logback-core:jar:1.2.10:compile
[INFO]    |  +- org.slf4j:jcl-over-slf4j:jar:1.7.33:test
...
~~~

\large 

Es decir, tenemos disponible y cargadas:

- logback-classic
- log4j-to-slf4j
- log4j-api
- jul-to-slf4j
- jcl-over-slf4j

## Configurando logging

~~~~ {.properties}
# en application.properties
logging.level.root=INFO
logging.level.es.ucm.fdi.iw.model=DEBUG
logging.file=fichero-de-log.log
~~~~ 

* `level.root`: nivel raíz 
* `level.un.paquete.cualquiera`: nivel para ese paquete y subpaquetes
* `file`: fichero de salida a usar (opcional; por defecto, consola)

Más detalles en la [documentación de logging de spring](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-logging.html)

# Fin

## ¿?

¡No te quedes con preguntas!

------

![](../img/cc-by-sa-4.png "Creative Commons Attribution-ShareAlike 4.0 International License"){ width=25% }

This work is licensed under a [Creative Commons Attribution-ShareAlike 4.0 International License](https://creativecommons.org/licenses/by-sa/4.0/)
