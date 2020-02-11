% Logging
% (manuel.freire@fdi.ucm.es)
% 2019.02.12

## Objetivo

> Uso de logs para 
> depurar tu aplicación web

## Qué es el logging

* Log file = cuaderno de abordo, bitácora

    + logging = el acto de apuntar algo* en la bitácora. 
    + *ojo*: login / log-in = cuando te apuntas /a/ algo. Típicamente te meten en el libro de visitas.
    
* Log también es "tronco"

    + logging = tala

. . . 

(*) Por ejemplo, trazas sobre el funcionamiento de programas que permiten reconstruir qué pasó cuándo, y así poder depurarlo.

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

* stdout usa búferes (y no se escribe hasta que ve un `'\n'`); stderr no.
* stdout y stderr se pueden redirigir a lugares distintos
* muchos entornos sacan stderr en rojo por su consola integrada

## Problemas de System.x.println para depurar ejecución

* no son fáciles de desactivar una vez te pones a depurar algo distinto
* no es fácil pasar la salida a un lugar distinto \
    (out, err, fichero, correos, Telegram)
* sacar dónde se produce cada traza de depuración exige copiar y pegar mucho código:
    + **hilo** donde se produce
    + **momento** en el que se produce
    + **clase y método** en el que se produce
    + y luego, algún mensaje que te de información más específica ("usuario no encontrado")

## Escribiendo una clase sencilla para gestionar trazas

~~~ {.java}
public enum Log {
  INSTANCE;
  private PrintStream stream = System.out;
  private int level;
  private long t0 = System.currentTimeMillis();
  public void setWriter(PrintStream stream) { 
    INSTANCE.stream = stream;
  }
  public static void setLevel(int level) {
    INSTANCE.level = level;
  }    
  public static void log(int level, Object ... messages) {
    if (level < INSTANCE.level) return;
      StackTraceElement[] stes = Thread.currentThread().getStackTrace();
      StringBuilder sb = new StringBuilder(
          Thread.currentThread().getName() + "@" + 
          stes[stes.length-1] + ",t=" + 
          (System.currentTimeMillis()-t0) + ":");
      for (Object o : messages) sb.append(" " + o);
      INSTANCE.stream.println(sb.toString());
  }
}    
~~~

## Usando nuestra clase sencilla

~~~ {.java}
    Log.log(1, "y el número es... el ", 42);
~~~

... resulta en ...

~~~ {.java}
    main@Log.main(Log.java:27),t=1: y el número es... el  42
~~~

por `stdout`; podemos desactivarlo todo si ponemos un `level` mínimo lo bastante alto, y es posible también modificar fácilmente a dónde se escriben las cosas. No obstante,

* a veces queremos activar trazas en un sitio sí y en otros no
* algunas trazas son caras, y no queremos tener que construirlas a no ser que sea imprescindible
* sería bueno tener un soporte especial para excepciones, para que se muestren con todos sus detalles... o no.
* ¿esto no estaría mejor como una librería bien escrita y documentada?

## Logging vs system.out.println

* casi igual de fácil de escribir
* soporte para excepciones
* mucho más útil durante depuración (muestra más información sin tener que meterla en el println)
* mucho más fácil de limpiar una vez todo parece funcionar
* mucho más fácil de reactivar si resulta que algo no estaba tan bien como parecía

## Logging vs sesiones de depuración

* Puede usarse cuando no hay interfaz de depuración disponible (ej.: servidores a los que sólo tenemos acceso vía consola)
* O cuando el programa tiene concurrencia y temporización que hace difíl el uso de un depurador (ej.: interfaces gráficas de usuario)
* Una vez generada una traza, ahí está. En cambio, las sesiones de depuración se pierden muy fácilmente. 
* Puedes comparar una traza contra otra - eso es más difícil con sesiones de depuración.
* Todas las librerías de logging permiten trazar a fichero y a consola a la vez.

## Librerías de logging

* jul - viene con la JDK. Algo lenta y obtusa de configurar (ver transparencia anterior).
* log4j - inspiró a jul, más sencilla de usar/configurar. Recomendada para proyectos vuestros.
* commons logging - variante Apache Commons, usada en muchos proyectos de Commons. Similar a log4j.
* slf4j - fachada que permite escribir / leer de cualquiera de las anteriores, de forma que tu aplicación puede integrar componentes que usen distintas librerías de logging. Recomendada para proyectos que otros vayan a poder usar/integrar.

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

## Configurando logging

~~~~ {.properties}

# en application.properties
logging.level.root=INFO
logging.level.es.ucm.fdi.iw.model=DEBUG
logging.file=fichero-de-log.log
~~~~ 

Más detalles en la [documentación de logging de spring](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-logging.html)
