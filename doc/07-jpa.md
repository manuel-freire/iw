% JPA
% (manuel.freire@fdi.ucm.es)
% 2018.02.25

## Objetivo

> Incorporar una BD a vuestras webs

## application.properties

~~~~ {.properties}
spring.profiles.active: default

spring.datasource.username: sa
spring.datasource.password:

spring.jpa.properties.hibernate.dialect: \
      org.hibernate.dialect.HSQLDialect
spring.jpa.database: HSQL
spring.jpa.show-sql: true
spring.jpa.properties.hibernate.hbm2ddl.import_files_sql_extractor: \
      org.hibernate.tool.hbm2ddl.MultipleLinesSqlCommandExtractor

logging.level.root: INFO
logging.level.org.hibernate: ERROR
logging.level.org.springframework.web: DEBUG

es.ucm.fdi.base-path: /tmp/iw
~~~~ 

- *Modificando esto, podrías conectarte a otra BD no-embebida o no-hsqldb*

## import.sql

- Ejecutado tras conectar, después de actualizar esquema (si procede)

\small

~~~~ {.sql}
INSERT INTO user(id,enabled,login,password,roles) VALUES (
	1, 1, 'a', 
	'{bcrypt}$2a$04$2ao4NQnJbq3Z6UeGGv24a.wRRX0FGq2l5gcy2Pjd/83ps7YaBXk9C',
	'USER,ADMIN'
);
~~~~ 

## Clases de modelo con JPA

- Requieren 
    + @Entity en la clase
    + @Id en un getter (recomiendo que sea "long" y use @GeneratedValue)
    + constructor de clase público vacío (o no hay constructores, o hay uno vacío)
    + getters y setters para todo; *ponemos anotaciones JPA en los getters*

- - -

## Entity 

Marca una clase como persistible, y crea las tablas correspondientes

\small

~~~~ {.java}
@Entity
public class Book {
  private long id;
  private String title;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }
}
~~~~

## Más anotaciones 

"Un libro tiene un dueño, y un dueño puede tener muchos libros"

~~~~ {.java}
    // en Book
    private User owner;

    @ManyToOne(targetEntity=User.class)
    public User getOwner() {
      return owner;
    }

    public void setOwner(User owner) {
      this.owner = owner;
    }
~~~~

- - - 

~~~~ {.java}
    // en User
    private List<Book> ownedBooks;
  
    @OneToMany(targetEntity=Book.class)
    @JoinColumn(name="owner_id") // <-- evita crear User_Book
    public List<Book> getOwnedBooks() {
      return ownedBooks;
    }

    public void setOwnedBooks(List<Book> ownedBooks) {
      this.ownedBooks = ownedBooks;
    }
~~~~

- - - 

- Otras opciones:
    + ManyToMany: crea tabla intermedia (User_Book); *no uses JoinColumn*
    + OneToOne
    + ... (posible especificar nombres de tablas, columnas, anchos, ...)
- Referencias
    + [openjpa](http://openjpa.apache.org/builds/1.0.2/apache-openjpa-1.0.2/docs/manual/jpa_overview_query.html#jpa_overview_query_named)
    + [JPA en Oracle](http://www.oracle.com/technetwork/middleware/ias/toplink-jpa-annotations-096251.html)

## Accediendo a la BD

- En tu controlador, usa:

~~~~ {.java}
    @AutoWired
    private EntityManager entityManager;
~~~~

- entityManager: acceso a la BD desde cualquier sitio de tu controlador
- lo puedes pasar de un lado a otro como parámetro (usa clases auxiliares si necesitas algo difícil)
- No hace falta inicializarlo: Spring hace **inyección de dependencias** (cuando encuentra un `@AutoWired`)

## Accediendo a la BD

~~~~ {.java}
// en User
@NamedQueries({
    @NamedQuery(name="userByLogin",
        query="select u from User u where u.login = :loginParam")
})
...

// en el controlador
...
    User u = null;
    try {
      u = (User)entityManager.createNamedQuery("userByLogin")
          .setParameter("loginParam", formLogin).getSingleResult();
...
~~~~ 

## Modificando la BD

~~~~ {.java}
@Transactional // en la cabecera de la función que va a hacer cambios
...
    User user = User.createUser(formLogin, formPass, "user");
    entityManager.persist(user); 
    entityManager.flush(); // <- implicito al final de la transaccion
~~~~ 

- Si no existe, lo crea; si existe, lo modifica 

## Consultas "impromptu" 
    
~~~~ {.java}
List<User> us = entityManager
    .createQuery("select u from User u").getResultList();
~~~~ 

- No hace falta `@Transactional` (a no ser que requieras el aislamiento adicional que ofrece)
- OJO: **prohibido** usarlas si tienen **argumentos manipulables** por usuarios de la aplicación. 
    * argumentos sin escapar en consultas resultan en **inyección SQL**
    * usa **namedQuerie**s parametrizadas en su lugar

## Tipos de relaciones

- OneToMany / ManyToOne: no generan tabla auxiliar\footnote{si usas bien @JoinColumn}
    * **un** jugador puede tener licencias de **varios** juegos (OneToMany)
    * **una** licencia de juego tiene **un** propietario (ManyToOne, \
    porque viceversa no es cierto)
- ManyToMany: generan 1 tabla auxiliar
    * **un** autor puede escribir **varios** libros
    * **un** libro puede estar escrito por **varios** autores

## OneToMany / ManyToOne

jugadores y juegos (en tienda online de juegos)

~~~~ {.java}
// Jugador.java -- 1 jugador tiene varias licencias de juegos
@OneToMany(targetEntity=Juego.class)
@JoinColum(name="propietario_id")   // <-- evita tabla auxiliar
List<Juego> getJuegos() { ... }

// Juego.java -- 1 licencia de juego tiene 1 propietario
@ManyToOne(targetEntity=Jugador.class)
Jugador getPropietario() { ... }
~~~~
        
## ManyToMany

autores y libros

~~~~ {.java}
// Autor.java -- 1 autor puede escribir varios libros; 
@ManyToOne(targetEntity=Libro.class)
List<Libro> getObras() { ... }

// Libro.java -- 1 libro puede estar escrito por varios autores)
@ManyToMany(targetEntity=Autor.class, 
    mappedBy="obras")           // <-- propietario: el autor
List<Autor> getAutores() { ... }
~~~~

- - -

Propietario de la relación: el autor\
tabla generada: AUTOR_LIBRO

~~~~ {.java}
@ManyToMany(targetEntity=Autor.class, mappedBy="obras")
@ManyToOne(targetEntity=Libro.class)
~~~~


. . .

Propietario de la relación: el libro\
tabla generada: LIBRO_AUTOR

~~~~ {.java}
@ManyToMany(targetEntity=Autor.class)
@ManyToOne(targetEntity=Libro.class, mappedBy="autores")
~~~~


- - - 

Resolución "a demanda" (por defecto en ManyToMany y OneToMany)

~~~~ {.java}
@ManyToOne(targetEntity=Libro.class, fetch=LAZY)
~~~~

Resolución immediata (por defecto en ManyToOne)

~~~~ {.java}
@ManyToOne(targetEntity=Libro.class, fetch=EAGER)
~~~~

Si usas resolución "a demanda", **sólo funciona desde dentro del controlador**. Haz todas tus consultas en el controlador, y que la vista sólo "muestre", pero sin consultar.

## Acceso a BD via JPA

- Obtener objetos por ID (ver [esta pregunta](http://stackoverflow.com/questions/1607532/when-to-use-entitymanager-find-vs-entitymanager-getreference)) \
con todos sus campos vs sólo para escribir en él:

~~~~ {.java}
// realiza un SELECT * FROM BOOK WHERE id coincide
Book b = entityManager.find(Book.class, id);

// hace el mismo select, pero evita cargar atributos
Book b = entityManager.getReference(Book.class, id);
~~~~

- Modificar un objeto de la BD:

~~~~ {.java}
b.setTitle("Momo"); 
entityManager.update(b)   // <-- innecesario si en transacción
~~~~

- Insertar un objeto en la BD:

~~~~ {.java}
Book b = new Book(); 
entityManager.persist(b)  // <-- siempre necesario; asigna ID
~~~~

- - -

- Saber el ID asignado a un nuevo objeto:

~~~~ {.java}
  Book b = new Book();
  // ... aquí setTitle, ...
  entityManager.persist(b); 
  entityManager.flush();        // fuerza escritura de cambios
  long idAsignado = b.getId(); 
~~~~

## Modificando relaciones

- borrar un objeto que referencia a otros, pero es _*propietario*_ de la relacion (no tiene el mappedBy): `entityManager.remove(b);`
- borrar un objeto que referencia a otros, y no es el propietario de la relacion: 

~~~~ {.java}
  Book b = entityManager.find(Book.class, id);
  // este for también podría hacerlo con un DELETE...
  for (Author a : b.getAuthors()) {
     a.getWritings().remove(b);
     entityManager.persist(a);
  }
  entityManager.remove(b);
~~~~

- también puedes usar modos de CASCADE para que eliminar un objeto elimine referencias

# Consultas JPA

## Documentación

- Sacadas de [la documentación](http://docs.oracle.com/javaee/6/tutorial/doc/bnbtl.html)
- También útiles para updates ó deletes (en lugar de selects).

## Selects sencillos

Asumiendo una clase `Player` con campos `name` y `position`

~~~~ {.sql}
SELECT p FROM Player p

SELECT DISTINCT p
FROM Player p
WHERE p.position = :position AND p.name = :name
~~~~

## Selects navegando relaciones

Asumiendo que los `Player` tienen listas de `teams`, cada uno con un `city`,

~~~~ {.sql}
SELECT DISTINCT p
FROM Player p, IN (p.teams) AS t
WHERE t.city = :city
~~~~

(`p.teams.city` no habría servido, ya que es una lista)

- - -

~~~~ {.sql}
SELECT DISTINCT p
FROM Player p, IN (p.teams) t
WHERE t.league.sport = :sport
~~~~

(`t.league.sport` sí vale, ya que cada equipo sólo está en una liga)

# Tipos de BDs

## Embebida, memoria

- no guarda nada en disco (desaparece al cerrar servidor, independiente de sistema operativo)
- vive en la misma JVM (imposible conectarse desde otro proceso)

## Embebida, disco

+ requiere un directorio para almacenarse
+ posible conectarse, pero peligroso que haya más de un proceso escribiendo a la vez

## En servidor

+ muchos subtipos en cuanto a puertos, seguridad, ...
+ requiere un directorio para almacenarse
+ requiere un proceso servidor (que acepta y procesa conexiones)
+ posible conectarse desde muchos sitios a la vez

## Cómo usar una BD "en memoria"

Ya la estás usando

~~~~ {.properties}
    # esto lo tienes en tu application-default.properties
    jdbc:hsqldb:mem:iw;create=true
~~~~ 

## Cómo usar una BD "en disco"

~~~~ {.properties}
    # esto lo tienes en tu application-externaldb.properties
    jdbc:hsqldb:file:/tmp/iw;create=true
~~~~ 

Y el fichero con la BD se guarda en `/tmp/iw`

- - -

puedes abrirlo (*una vez cerrado el servidor web*) 
usando el cliente Swing que viene dentro de HSQLDB

~~~~ {.sh}
    java -cp ~/.m2/repository/org/hsqldb/hsqldb/2.5.0/hsqldb-2.5.0.jar \
        org.hsqldb.util.DatabaseManagerSwing
~~~~ 

(nota: el `\` es por legibilidad - mejor si todo en la misma línea)

El mismo cliente GUI se puede lanzar desde STS, buscando en "dependencias Maven" 
el paquete `hsqldb-2.5.0.jar`, y con click derecho, ejecutando "Run As > Java Application"

## Cómo usar una BD con servidor independiente

en src/main/resources/application-externaldb.properties...

~~~~ {.properties}
    jdbc:hsqldb:hsql://localhost/iwdb 
~~~~ 

Pero antes, lanza el servidor, usando

~~~~ {.sh}
    java -cp ~/.m2/repository/org/hsqldb/hsqldb/2.5.0/hsqldb-2.5.0.jar \
        org.hsqldb.server.Server
~~~~ 

- - -

Para usar BD externa en lugar de BD embebida en memoria, tienes que cambiar 
el perfil activo:


src/main/resources/application.properties:

~~~~ {.properties}
    spring.profiles.active: default
~~~~ 


src/main/resources/application.properties:

~~~~ {.properties}
    spring.profiles.active: externaldb
~~~~ 

- - -

Puedes usar el mismo cliente Swing para inspeccionar tu BD. 
Ten en cuenta que, por defecto, no estás usando contraseña, 
y cualquiera puede tocarte la BD si sabe tu IP...

## Instrucciones útiles en HSQLDB

- ```SHUTDOWN``` - apaga un servidor de forma elegante
- ```SCRIPT /tmp/fichero``` - guarda en ```/tmp/fichero``` un dump completo de la BD, ideal para tu ```import.sql```

## Más información

- [El manual de usuario de HSQLDB](http://www.hsqldb.org/doc/guide/index.html)
- [El manual de las herramientas incluídas](http://www.hsqldb.org/doc/util-guide/index.html)

## En el mundo real

- Generalmente, querrás un servidor de BD separado
    + Bueno para escalar: si la carga aumenta, puedes tener más servidores de BD que cooperen entre sí
    + Bueno para redundancia: si tienes un cluster, puedes configurarlo para que si se caen uno o dos servidores no pase nada
- *Usa una BD con servidor separado*

## En tus entregas para IW

- Prefiero no tener que lanzar un servidor separado
- *Usa una BD embebida en memoria con datos de prueba cargados vía import.sql*
- *Incluye un fichero "vacio.sql"* que contenga los datos mínimos que entregarías a alquien que vaya a instalar tu aplicación desde cero.

## En todo caso

- A una buena aplicación web le debería dar igual dónde o quién guarda sus datos. Eso no es responsabilidad suya.
- La configuración de la BD debe estar lo más aislada e identificada posible
- No hay nada peor que tener 100 sitios en la aplicación donde están escritos "a pincho" los parámetros de conexión a la BD.
- En vuestro caso, "con 1 línea basta &copy;".

# Fin

## ¿?

¡No te quedes con preguntas!

------

![](img/cc-by-sa-4.png "Creative Commons Attribution-ShareAlike 4.0 International License"){ width=25% }

This work is licensed under a [Creative Commons Attribution-ShareAlike 4.0 International License](https://creativecommons.org/licenses/by-sa/4.0/)
