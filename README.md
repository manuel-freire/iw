# Eat Away

¿No te apetece cocinar?¿Tienes la nevera vacía?¿Te apetece probar sabores nuevos? ¿Quieres impresionar a tu cita pero la cocina no es lo tuyo? EatAway resolverá todos estos problemas y más. Selecciona tus platos preferidos y en un abrir y cerrar de ojos tendrás tu comida en casa o dónde prefieras.

> "Come donde, cuando y como quieras"

## Funcionalidad
Con EatAway podrás elegir entre diferentes restaurantes. Estos estarán clasificados, por ejemplo, por el tipo de comida (hamburguesas, pizzas, etc) o por el rango de precios para hacer más sencilla la búsqueda.

Seleccionando un restaurante podrás ver información sobre éste, como las opiniones, valoración, dirección, horario... 

A su vez, podrás elegir un plato del menú del restaurante para ver su descripción, el precio, elegir algún elemento extra (como una guarnición, el punto de la carne, salsas...) y añadirlo al carrito, seleccionando el número de unidades que desees.


## Usuarios
Disponemos de 4 tipos de usuarios. Dependiendo de cuál se trate, se podrá acceder a diferentes vistas de la aplicación:

* **Usuario no registrado**: 
    - Podrá visitar los restaurantes y hacer pedidos. 
    - Al no disponer de una cuenta de usuario, deberá rellenar un formulario con sus datos cada vez que haga un pedido.
    - No dispondrá de ofertas ni rebajas.
    - No podrá valorar al repartidor de su pedido ni al restaurante.

 - **Usuario registrario**:
    - Podrá realizar pedidos y hablar por el chat con el repartidor una vez mandado el pedido. 
    - Podrá acceder a una vista del pedido, donde podrá ver su estado, su información, el precio de éste, si queremos añadir propina. También aparecerá un chat con el repartidor por si queremos ponernos en contacto con él. Además, habrá un mapa donde podremos localizar al repartidor.
    - Dispondrá de un perfil de usuario donde estarán almacenados los datos necesarios para poder realizar un pedido sin la necesidad de rellenar un formulario cada vez (nombre, apellidos, dirección, información de contacto). En su perfil, también tendrá un historial de pedidos y una zona ajustes para poder realizar cambios en su cuenta.

* **Restaurante**: 
    - Podrá gestionar todos sus locales, ver la carta que tiene y a su vez modificarla cuando existan cambios en ella. Aceptará los pedidos que un usuario (registrado o no registrao) haga en dicho restaurante.. También tendrá opción de poder ver las opiniones y valoraciones que hay sobre él. 

- **Repartidor**: 
    - El repartidor podrá ver desde su vista los pedidos que que los restaurantes aceptan. Podrá ver el estado del pedido (para saber cuándo están listos para recoger) así como asignarse un pedido para ir a recogerlo al restaurante y posteriormente entregarlo al cliente. 
    -Podrá revisar las valoraciones hechas por los clientes a los que ha repartido algún pedido.
    - Tendrá acceso a un chat para hablar con los clientes junto con un mapa para saber dónde hay que realizar la entrega. 


* **Administrador**: 
    - Tiene total autorización para modificar la base de datos de la aplicación. Tendrá la opción de eliminar o suspender cuentas de usuario por un mal uso, así como los comentarios que no cumplas con las normas. 

## Vistas

* **Página de inicio:** (localhost:8080/)
    - En esta página se verán las diferentes comidas y restaurantes inscritos en la aplicación web, y se podrá seleccionar la comida y restaurante que uno desee ordenar. Para los usuarios "avanzados" (Administrador, Dueño del restaurante y Repartidor) la página de inicio corresponderá con una vista especial para éstos, con las opciones que cada uno tenga habilitadas.
* **Información del Restaurante:** (localhost:8080/restaurante)
    - Aquí se va a ver la información detallada del restaurante desde la perspectiva de un cliente que quiere ordenarle comida a ellos. Podrá consultar la carta de este restaurante, su valoración media, entre otras cosas.
* **Información del Plato:** (localhost:8080/platos)
    - Aquí se podrá ver información detallada de cada plato que haya en la carta de un determinado restaurante, pudiendo ver sus ingredientes, cantidad a comprar, extras posibles, etc.
* **Lista de Pedidos Disponibles (Para el Repartidor):** (localhost:8080/listaPedidos)
    - Aquí el repartidor podrá ver la lista de pedidos que han sido pedidos por los clientes en la aplicación, para que puedan "designarse" un pedido para recogerlo en el restaurante que lo está preparando, y entregarselo al cliente una vez esté preparado. Tiene acceso a un pequeño mapa donde puede ver la dirección del restaurante y de entrega, y un chat para hablar con el cliente.
* **Perfil del Dueño del Restaurante:** (localhost:8080/perfilRestaurante)
    - Aquí el dueño de un restaurante podrá visualizar las últimas valoraciones y pedidos que se le han hecho a su restaurante, así como actualizar su carta, información de contacto, y hacer otras gestiones administrativas
* **Carrito:** (localhost:8080/carrito)
    - En esta vista el cliente podrá ver los productos que ha ido añadiendo a su pedido, y cuando esté preparado para pagar, efectuar la compra y supervisar el estado de su pedido, así como hablar con el repartidor que vaya a entregarle su comida