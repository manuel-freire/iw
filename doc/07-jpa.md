% JPA
% (manuel.freire@fdi.ucm.es)
% 2023.02.21

## Objetivo

> Incorporar una BD a vuestras webs

## application.properties

\small

~~~{.properties}

spring.profiles.active: default

spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.datasource.platform=h2

# url for DB; use file:/path/to/file to avoid empty initial DB
spring.datasource.url=jdbc:h2:mem:testdb

# enable web h2 console
spring.h2.console.enabled=true
spring.h2.console.path=/h2

spring.jpa.show-sql: true

# allow multi-line import.sql statements, from https://stackoverflow.com/a/15090964/15472
spring.jpa.properties.hibernate.hbm2ddl.import_files_sql_extractor: org.hibernate.tool.hbm2ddl.MultipleLinesSqlCommandExtractor

spring.thymeleaf.cache: false
es.ucm.fdi.base-path: /tmp/iw
~~~ 

\normal

- *Modificando `spring.profiles.active`, puedes cambiar entre distintos __perfiles__ de BD*

- *Las propiedades `spring.datasource.*` y  `spring.jpa.properties.*` definen el - tipo de BD a usar*

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
    + @Id en un atributo a usar como clave (recomiendo que sea "long" y use @GeneratedValue)
    + constructor de clase público vacío (o no hay constructores, o hay uno vacío)
    + getters y setters para todo
	+ anotaciones JPA en atributos (podríais ponerlas en getters y setters, pero es más feo)
	+ getters y setters para todo (generados por Lombok `@Data`)

- - -

## Entity anotando getter

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

## Entity anotando atributo

Marca una clase como persistible, y crea las tablas correspondientes

\small

~~~~ {.java}
@Entity
public class Book {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  private String title;

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

## Entity usando Lombok para generar getters y setters

Marca una clase como persistible, y crea las tablas correspondientes

\small

~~~~ {.java}
@Entity
@Data      // <-- requiere import lombok.Data;
public class Book {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  private String title;
}
~~~~

## Más anotaciones 

"Un libro tiene un dueño, y un dueño puede tener muchos libros"

~~~~ {.java}
    // en Book
    @ManyToOne
    private User owner;
~~~~

- - - 

~~~~ {.java}
    // en User
    @OneToMany
    @JoinColumn(name="owner_id") // <-- evita crear User_Book
    private List<Book> ownedBooks;
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

# Herencia y Entidades

## Hay muchas formas de gestionar herencia (OO) usando tablas (Relacional)

* *Todos* los atributos de *todas* las entidades de un único árbol de herencia van a una misma tabla; sabes el tipo concreto de cada entidad usando una nueva *columna discriminadora*: `InheritanceType.SINGLE_TABLE`
  - poco coste desde el punto de vista de procesamiento / tiempo de consulta
  - algo caro desde el punto de vista de espacio de almacenamiento: columnas que no usas
  - problemas para verificar integridad en la BD

* Cada entidad del árbol de herencia resulta en una tabla, que mediante un *join" con la tabla raíz de ese árbol, permite conseguir los datos de la entidad completa: `InheritanceType.JOINED`
  - caro para procesamiento / consulta de 1 entidad de una subclase concreta
  - barato en almacenaje, y bueno para validación en BD

* Cada entidad del árbol de herencia resulta en una tabla. No hay joins - a no ser que necesites datos de múltiples subclases de una misma clase a la vez: `InheritanceType.TABLE_PER_CLASS`
  - caro para procesamiento / consulta de 1 entidad y sus subclases
  - barato en almacenaje, y bueno para validación en BD

## Herencia con todo-en-una-tabla

* Por defecto en Hibernate (que es lo que usa Spring como proveedor de JPA): no hace falta especificar `@InheritanceType`
* Requiere establecer una columna discriminadora: 

~~~~ {.java}
@Entity
@Table(name = "IWUser")
@DiscriminatorColumn(name="DISC", discriminatorType=STRING, length=16)                  
~~~~

## Ejemplo con animales

~~~~ {.java}
// adaptado de https://stackoverflow.com/a/48415922/15472
 
@Entity 
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING, name = "TYPE")
public class Animal {
  @Id
  private long id;

  @Column(name = "NAME")
  private String name;
}

@Entity @DiscriminatorValue("REPTILE")
public class Reptile extends Animal {
  /* ... */
}

@Entity @DiscriminatorValue("BIRD")
public class Bird extends Animal {
  /* ... */
}
~~~~

- - - 

ID  NAME        TYPE     
--- ----------  -------
1   Crocodile   REPTILE  
2   Dinosaur    REPTILE   
3   Lizard      REPTILE   
4   Owl         BIRD     
5   parrot      BIRD     

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

Ya la estás usando: en application.properties, tienes

~~~~ {.properties}
    spring.datasource.url=jdbc:h2:mem:iwdb
~~~~ 

## Cómo usar una BD "en disco"

~~~~ {.properties}
    # variante para windows
    spring.datasource.url=jdbc:h2:file:C:/hlocal/iwdb
~~~~

~~~~ {.properties}
    # variante para linux
    spring.datasource.url=jdbc:h2:file:/tmp/iwdb
~~~~ 

Y el fichero con la BD se guarda en `/tmp/iw`

## Cómo usar una BD con servidor independiente

en src/main/resources/application-externaldb.properties...

~~~~ {.properties}
    spring.datasource.url=jdbc:h2:tcp://localhost:9092
~~~~ 

Pero **antes** de probarlo, lanza el servidor, usando

~~~~ {.sh}
    java -cp ~/.m2/repository/com/h2database/h2/2.1.214/h2-2.1.214.jar \
        org.h2.tools.Server -web -tcp
~~~~ 

ver http://www.h2database.com/html/tutorial.html#using_server

## Modos de acceso a la BD

~~~~ {.properties}
  # no lee import.sql, no comprueba que las tablas correspondan al modelo
  spring.jpa.hibernate.ddl-auto=none

  # no lee import.sql, SI comprueba que tablas y modelo coinciden
  spring.jpa.hibernate.ddl-auto=validate

  # no lee import.sql, si tabla y modelo no coinciden, INTENTA ARREGLAR
  spring.jpa.hibernate.ddl-auto=update

  # SÍ lee import.sql, CREA tablas para que coincidan con modelo
  spring.jpa.hibernate.ddl-auto=create

  # Como el anterior, pero además luego borra tablas
  spring.jpa.hibernate.ddl-auto=create-drop
~~~~

## Pasando de create-drop a validate

1. configura para usar fichero o servidor externo con `ddl-auto=create`
2. lanza 1 vez la aplicación, y ciérrala luego
3. modifica connfiguración para usar `ddl-auto=validate`
4. ya no cambies la configuración. ¡Y tampoco cambies tu modelo!

## Instrucciones útiles en H2

- ```SCRIPT TO '/tmp/fichero'``` - guarda en ```/tmp/fichero``` un dump completo de la BD, ideal para tu ```import.sql```

(ver https://stackoverflow.com/a/3259166/15472)

## Más información

- [El manual de usuario de H2](http://www.h2database.com/html/tutorial.html#using_server)

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
