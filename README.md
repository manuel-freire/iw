# Aplicación de Plantilla de IW

Debes buscar todos los lugares donde aparece la palabra "plantilla" (incluido este párrafo) y reemplazar las ocurrencias, y el contexto circundante, por valores que tengan sentido en tu aplicación. Por ejemplo, este párrafo deberías eliminarlo de tu proyecto.

¿No te apetece cocinar?¿Tienes la nevera vacía?¿Te apetece probar sabores nuevos? ¿Quieres impresionar a tu cita pero la cocina no es lo tuyo? EatAway resolverá todos estos problemas y más. Selecciona tus platos preferidos y en un abrir y cerrar de ojos tendrás tu comida en casa o dónde prefieras.

“”Come donde, cuando y como quieras”

La funcionalidad principal de nuestra aplicación será poder ver diferentes restaurantes, clasificados por ejemplo por el tipo de comida (hamburguesas, pizzas, etc), seleccionar uno y poder ver información de éste como será las opiniones que hay sobre él, puntuación, donde podemos encontrarlo, la hora que abre y su cierre. A su vez podemos meternos en el plato que queremos pedir, ver sus características y añadirlo a nuestra cesta, modificarlo en caso de que queramos algún tipo de guarnición, el precio, la cantidad de dicho plato, el punto de la carne.

Vamos a poder encontrar diferentes tipos de usuarios y dependiendo en cuál estemos, nos saldrá un tipo de información u otra:

-Usuario no registrado: el cual podrá hacer pedidos y hablar por chat con el repartidor. No dispondrá de perfil de usuario con lo cuál deberá rellenar un formulario cada vez que haga un pedido. No dispondrá de ofertas ni rebajas. Podrá valorar al repartidor después de la entrega, pero no podrá escribir reseñas del restaurante.

-Usuario registrario: será el usuario con más privilegios y el que realmente podrá usar todas las funcionalidades principales de la aplicación (descartando aquellas que tienen que ver con la implementación). Podrá realizar pedidos y hablar por el chat con el repartidor una vez mandado el pedido. 
Dispondrá de un perfil de usuario donde estarán almacenados los datos necesarios para poder realizar un pedido (nombre, apellidos, dirección, historial, información de contacto y ajustes para poder realizar cambiios en nuestra cuenta).
Tendrá también una vista de pedidos, donde podremos ver el estado de nuestro pedido y su información, el precio de éste, si queremos añadir propina, y donde también aparecerá el chat con el repartidor por si queremos ponernos en contacto con él y a su vez, un mapa donde podremos localizar al repartidor.

-Restaurante: la finalidad de dicho restaurante es que pueda manejar todos sus locales, ver la carta que tiene y a su vez modificarla cuando existan cambios en ella. Los pedidos que se hacen en dicho restaurante de los diferentes platos que podemos encontrar en él. También tendrá opción de poder ver las opiniones y valoraciones que hay sobre él. 

-Repartidor: El repartidor podrá recibir los pedidos que realicen los clientes, así como asignarse un pedido para ir a recogerlo al restaurante, y posteriormente entregárselo al cliente. Contará con acceso a la aplicación para revisar las valoraciones hechas por los clientes, y también con acceso a un chat para hablar con los clientes junto con un mapa para saber donde hay que realizar la entrega. Podrá ver desde su vista los pedidos disponibles que tiene y el estado en el que están estos (para saber cuando están listos para recoger y llevarlos a su cliente).

-Administrador: será el encargado de la implementación de la aplicación, es decir, su aspecto y de realizar cambios en ella, así como solucionar los problemas y errores que hay en ella. Tendrá la opción de eliminar o suspender cuentas de usuario por un mal uso, así como los comentarios que no cumplas con las normas. 




### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.6.3/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.6.3/maven-plugin/reference/html/#build-image)
* [Spring Web](https://docs.spring.io/spring-boot/docs/2.6.3/reference/htmlsingle/#boot-features-developing-web-applications)
* [WebSocket](https://docs.spring.io/spring-boot/docs/2.6.3/reference/htmlsingle/#boot-features-websockets)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/2.6.3/reference/htmlsingle/#boot-features-jpa-and-spring-data)
* [Spring Security](https://docs.spring.io/spring-boot/docs/2.6.3/reference/htmlsingle/#boot-features-security)
* [Thymeleaf](https://docs.spring.io/spring-boot/docs/2.6.3/reference/htmlsingle/#boot-features-spring-mvc-template-engines)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/bookmarks/)
* [Using WebSocket to build an interactive web application](https://spring.io/guides/gs/messaging-stomp-websocket/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Securing a Web Application](https://spring.io/guides/gs/securing-web/)
* [Spring Boot and OAuth2](https://spring.io/guides/tutorials/spring-boot-oauth2/)
* [Authenticating a User with LDAP](https://spring.io/guides/gs/authenticating-ldap/)
* [Handling Form Submission](https://spring.io/guides/gs/handling-form-submission/)

